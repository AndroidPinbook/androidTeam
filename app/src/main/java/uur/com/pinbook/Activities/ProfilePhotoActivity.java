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

        findViewById(R.id.photoImageView).setOnClickListener(this);
        findViewById(R.id.continueWithEmailVerifButton).setOnClickListener(this);
        findViewById(R.id.chooseProfilePicTextView).setOnClickListener(this);

        mProgressDialog = new ProgressDialog(this);

        getUserInfo();
    }

    private void getUserInfo() {

        Intent i = getIntent();
        user = (User) i.getSerializableExtra("User");
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

        try {

            Bitmap photo = null;
            Uri tempUri = null;

            ImageView photoImageView = (ImageView) findViewById(R.id.photoImageView);

            if(photoChoosenType == getResources().getString(R.string.camera)){

                photo = (Bitmap) data.getExtras().get("data");
                photo = BitmapConversion.getRoundedShape(photo, 250, 250);
                tempUri = getImageUri(getApplicationContext(), photo);

            }else if(photoChoosenType == getResources().getString(R.string.gallery)){

                tempUri = data.getData();
                InputStream imageStream = getContentResolver().openInputStream(tempUri);
                photo = BitmapFactory.decodeStream(imageStream);
                photo = BitmapConversion.getRoundedShape(photo, 250, 250);
            }

            photoImageView.setImageBitmap(photo);

            FirebaseUser user = mAuth.getCurrentUser();
            FBuserId = user.getUid();

            StorageReference riversRef = mStorageRef.child("Users/profilePics").child(FBuserId + ".jpg");

            mProgressDialog.setMessage("Uploading.....");
            mProgressDialog.show();

            riversRef.putFile(tempUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // Get a URL to the uploaded content
                            downloadUrl = taskSnapshot.getDownloadUrl();

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
            Log.i("Info", "put file exception:" + e.toString());
        }
    }

    public void saveUserInfo(){

        Map<String, String> values = new HashMap<>();

        String userId = user.getUserId();

        mDbref = FirebaseDatabase.getInstance().getReference().child(tag_users);

        values.put("email", user.getEmail());
        setValuesToCloud(userId, values);

        values.put("gender", user.getGender());
        setValuesToCloud(userId, values);

        values.put("username", user.getUsername());
        setValuesToCloud(userId, values);

        values.put("name", user.getName());
        setValuesToCloud(userId, values);

        values.put("surname", user.getSurname());
        setValuesToCloud(userId, values);

        values.put("phone", user.getPhoneNum());
        setValuesToCloud(userId, values);

        values.put("birthdate", user.getBirthdate());
        setValuesToCloud(userId, values);

        if(downloadUrl != null) {
            values.put("profImageSrc", downloadUrl.toString());
            setValuesToCloud(userId, values);
        }
    }

    public void setValuesToCloud(String userId, Map<String, String> values){

        mDbref.child(userId).setValue(values, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                Log.i("Info","databaseError:" + databaseError);
            }
        });
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

    /*public String getRealPathFromURI(Uri uri) {
        try {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            return cursor.getString(idx);
        }catch (Exception e){

            Log.i("Info", "getRealPathFromURI exception:" + e.toString());
            return null;
        }
    }*/

    @Override
    public void onClick(View v) {

        int i = v.getId();

        Log.i("Info","onClick works!");

        switch (i){
            case R.id.continueWithEmailVerifButton:
                startProfileActivity();
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

    public void startProfileActivity(){

        saveUserInfo();

        Intent intent = new Intent(getApplicationContext(), ProfilePageActivity.class);
        startActivity(intent);
    }


}
