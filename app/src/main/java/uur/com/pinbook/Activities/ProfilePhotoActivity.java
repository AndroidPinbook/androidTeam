package uur.com.pinbook.Activities;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import uur.com.pinbook.Controller.BitmapConversion;
import uur.com.pinbook.Controller.FirebaseUserAdapter;
import uur.com.pinbook.JavaFiles.User;
import uur.com.pinbook.R;

public class ProfilePhotoActivity extends AppCompatActivity implements View.OnClickListener{

    private FirebaseAuth mAuth;
    private DatabaseReference mDbref;
    private Uri downloadUrl = null;

    private StorageReference mStorageRef;

    ProgressDialog mProgressDialog;

    public String tag_users = "users";
    String FBuserId;

    public User user;
    public String photoChoosenType = "";

    String loginType = "";

    private Button continueButton;
    private Button continueWithEmailVerifButton;
    private ImageView photoImageView;

    private boolean socialAppLoginInd = false;

    private static final int  MY_PERMISSION_CAMERA = 1;
    private static final int  MY_PERMISSION_WRITE_EXTERNAL_STORAGE = 2;
    private static final int  MY_PERMISSION_READ_EXTERNAL_STORAGE = 3;
    private static final int  MY_PERMISSION_ACTION_GET_CONTENT = 4;

    private static final int PROFILE_PIC_CAMERA_SELECTED = 0;
    private static final int PROFILE_PIC_GALLERY_SELECTED = 1;

    private TextView chooseProfilePicTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_photo);

        mAuth = FirebaseAuth.getInstance();

        mStorageRef = FirebaseStorage.getInstance().getReference();

        continueButton = findViewById(R.id.continueButton);
        continueWithEmailVerifButton = findViewById(R.id.continueWithEmailVerifButton);
        photoImageView = (ImageView) findViewById(R.id.photoImageView);

        findViewById(R.id.photoImageView).setOnClickListener(this);
        continueWithEmailVerifButton.setOnClickListener(this);
        continueButton.setOnClickListener(this);
        findViewById(R.id.chooseProfilePicTextView).setOnClickListener(this);

        mProgressDialog = new ProgressDialog(this);

        getUserAndLoginInfo();
    }

    private void getUserAndLoginInfo() {

        Log.i("Info", "getUserAndLoginInfo starts");

        Intent i = getIntent();
        user = (User) i.getSerializableExtra(getResources().getString(R.string.User));
        loginType = (String) i.getSerializableExtra(getResources().getString(R.string.EntryType));

        Log.i("Info", "  >>loginType   :" + loginType);
        Log.i("Info", "  >>user pic src:" + user.getProfilePicSrc());

        manageLoginType();
    }

    private void manageLoginType() {

        if(loginType.equals(getResources().getString(R.string.fbLoginType) ) ||
                loginType.equals(getResources().getString(R.string.twLoginType))){

            Log.i("Info", "  >>inside manageLoginType function");
            continueButton.setVisibility(View.VISIBLE);
            continueButton.setEnabled(true);
            continueWithEmailVerifButton.setVisibility(View.GONE);
            continueWithEmailVerifButton.setEnabled(false);
            socialAppLoginInd = true;
            saveProfilePicToFB(null);
        }
    }

    public void startCameraProcess(){

        Log.i("Info", "startCameraProcess");

        if(!checkCameraHardware(this)){
            Toast.makeText(this, "Device has no camera!", Toast.LENGTH_SHORT).show();
            return;
        }
        
        checkMediaStoragePermission();
    }

    private void checkMediaStoragePermission() {

        Log.i("Info","checkMediaStoragePermission");

        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED) {

            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSION_WRITE_EXTERNAL_STORAGE);
        }else{

            checkCameraPermission();
        }
    }

    private void checkCameraPermission() {

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){

            requestPermissions(new String[]{Manifest.permission.CAMERA},MY_PERMISSION_CAMERA);
        }else{

            Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
            startActivityForResult(intent, MY_PERMISSION_CAMERA);
        }
    }

    /** Check if this device has a camera */
    private boolean checkCameraHardware(Context context) {
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)){
            // this device has a camera
            return true;
        } else {
            // no camera on this device
            return false;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        Log.i("Info", "onRequestPermissionsResult starts");

        switch (requestCode){

            case MY_PERMISSION_WRITE_EXTERNAL_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    checkCameraPermission();
                }
                break;
            case MY_PERMISSION_CAMERA:
                Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                startActivityForResult(intent, MY_PERMISSION_CAMERA);
                break;
            default:
                Toast.makeText(this, "Undefined permission request!", Toast.LENGTH_SHORT).show();
                break;
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        Log.i("Info", "onActivityResult starts");

        if(resultCode == Activity.RESULT_OK){

            switch (requestCode){

                case MY_PERMISSION_CAMERA:
                    saveProfilePicToFB(data);
                    break;

                case MY_PERMISSION_ACTION_GET_CONTENT:
                    saveProfilePicToFB(data);
                    break;

                default:
                    Toast.makeText(this, "requestCode error:" + requestCode, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void saveProfilePicToFB(Intent data) {

        Log.i("Info", "saveProfilePicToFB starts");
        Log.i("Info", "  >>socialAppLoginInd    :" + socialAppLoginInd);
        Log.i("Info", "  >>user.getProfilePicSrc:" + user.getProfilePicSrc());

        try {

            Bitmap photo = null;
            Uri tempUri = null;

            if(photoChoosenType == getResources().getString(R.string.camera)){

                photo = (Bitmap) data.getExtras().get("data");
                photo = BitmapConversion.getRoundedShape(photo, 250, 250);
                tempUri = getImageUri(getApplicationContext(), photo);

            }else if(photoChoosenType == getResources().getString(R.string.gallery)){

                tempUri = data.getData();
                InputStream imageStream = getContentResolver().openInputStream(tempUri);
                photo = BitmapFactory.decodeStream(imageStream);
                photo = BitmapConversion.getRoundedShape(photo, 250, 250);

            }else if(socialAppLoginInd){

                tempUri = Uri.parse(user.getProfilePicSrc());
                Log.i("Info", "  --1--");
                InputStream imageStream = getContentResolver().openInputStream(tempUri);
                Log.i("Info", "  --2--");
                photo = BitmapFactory.decodeStream(imageStream);
                Log.i("Info", "  --3--");
                photo = BitmapConversion.getRoundedShape(photo, 250, 250);
                Log.i("Info", "  --4--");
                socialAppLoginInd = false;
            }

            photoImageView.setImageBitmap(photo);

            FirebaseUser currentUser = mAuth.getCurrentUser();
            FBuserId = currentUser.getUid();

            StorageReference riversRef = mStorageRef.child("Users/profilePics").child(FBuserId + ".jpg");

            mProgressDialog.setMessage("Uploading.....");
            mProgressDialog.show();

            riversRef.putFile(tempUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // Get a URL to the uploaded content
                            downloadUrl = taskSnapshot.getDownloadUrl();
                            user.setProfilePicSrc(downloadUrl.toString());

                            Log.i("Info", "downloadUrl:" + downloadUrl);
                            mProgressDialog.dismiss();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Handle unsuccessful uploads

                            Log.i("Info", "put file err:" + exception.toString());
                            mProgressDialog.dismiss();
                        }
                    });
        }catch (Exception e){
            Log.i("Info", "  >>put file exception:" + e.toString());
        }
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        try {
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
            String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);

            return Uri.parse(path);
        }catch (Exception e){

            Log.i("Info", "getImageUri exception:" + e.toString());
            return null;
        }
    }

    @Override
    public void onClick(View v) {

        int i = v.getId();

        Log.i("Info","onClick works!");

        switch (i){
            case R.id.continueWithEmailVerifButton:
                startEmailVerifyActivity();
                break;

            case R.id.continueButton:
                startProfilePageActivity();
                break;

            case R.id.chooseProfilePicTextView:
                startChooseImageProc();
                break;

            case R.id.photoImageView:
                startChooseImageProc();
                break;

            default:
                Toast.makeText(this, "Error occured!!", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private void startChooseImageProc() {

        Log.i("Info", "startChooseImageProc");

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
        adapter.add("  Open Camera");
        adapter.add("  Open Galery");
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose a profile photo");

        builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {

                Log.i("Info", "  >>Item selected:" + item);

                if(item == PROFILE_PIC_CAMERA_SELECTED){

                    photoChoosenType = getResources().getString(R.string.camera);
                    startCameraProcess();

                }else if(item == PROFILE_PIC_GALLERY_SELECTED){

                    photoChoosenType = getResources().getString(R.string.gallery);
                    startGalleryProcess();

                }else {
                    Toast.makeText(ProfilePhotoActivity.this, "Item Selected Error!!", Toast.LENGTH_SHORT).show();
                }

            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    private void startGalleryProcess() {

        Log.i("Info", "startGalleryProcess");

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"),MY_PERMISSION_ACTION_GET_CONTENT);
    }

    public void startEmailVerifyActivity(){

        FirebaseUserAdapter.saveUserInfo(user);

        Intent intent = new Intent(getApplicationContext(), EmailVerifyPageActivity.class);
        startActivity(intent);
    }

    private void startProfilePageActivity() {

        FirebaseUserAdapter.saveUserInfo(user);

        Intent intent = new Intent(getApplicationContext(), ProfilePageActivity.class);
        startActivity(intent);
    }


}
