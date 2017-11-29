package uur.com.pinbook.Activities;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v13.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

import uur.com.pinbook.R;

public class ProfilePhotoActivity extends AppCompatActivity implements View.OnClickListener{

    ImageView photoImgView;
    ImageView galleryImgView;
    ImageView profilePhotoImgView;

    RelativeLayout galleryOrPhotoLayout;
    RelativeLayout profilePhotoRelLayout;

    private FirebaseAuth mAuth;
    private DatabaseReference mDbref;
    private Uri downloadUrl;

    private StorageReference mStorageRef;

    ProgressDialog mProgressDialog;

    public String tag_users = "users";
    String FBuserId;

    private static final int  MY_PERMISSION_CAMERA = 1;
    private static final int  MY_PERMISSION_WRITE_EXTERNAL_STORAGE = 2;
    private static final int  MY_PERMISSION_READ_EXTERNAL_STORAGE = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_photo);

        mAuth = FirebaseAuth.getInstance();

        photoImgView = (ImageView) findViewById(R.id.photoImageView);
        galleryImgView = (ImageView) findViewById(R.id.galleryImageView);
        profilePhotoImgView = (ImageView) findViewById(R.id.profilePhotoImgView);

        galleryOrPhotoLayout = (RelativeLayout) findViewById(R.id.galleryOrPhotoLayout);
        profilePhotoRelLayout = (RelativeLayout) findViewById(R.id.profilePhotoRelLayout);

        mStorageRef = FirebaseStorage.getInstance().getReference();


        findViewById(R.id.photoImageView).setOnClickListener(this);
        findViewById(R.id.galleryImageView).setOnClickListener(this);
        findViewById(R.id.skipButton).setOnClickListener(this);
        findViewById(R.id.nextButton).setOnClickListener(this);

        mProgressDialog = new ProgressDialog(this);
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

        if (requestCode == MY_PERMISSION_CAMERA && resultCode == Activity.RESULT_OK) {

            Log.i("Info", "  >>onActivityResult");

            galleryOrPhotoLayout.setVisibility(View.GONE);
            profilePhotoRelLayout.setVisibility(View.VISIBLE);

            ImageView photoImageView = (ImageView) findViewById(R.id.photoImageView);
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            profilePhotoImgView.setImageBitmap(photo);

            try {

                Uri tempUri = getImageUri(getApplicationContext(), photo);

                Log.i("Info","  >>tempUri:" + tempUri);

                File finalFile = new File(getRealPathFromURI(tempUri));

                Log.i("Info","  >>finalFile:" + finalFile);

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

                                saveUserInfo(FBuserId);

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
    }

    public void saveUserInfo(String userId){

        Log.i("Info","userId:" + userId);

        mDbref = FirebaseDatabase.getInstance().getReference().child(tag_users);

        mDbref.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                 @Override
                 public void onDataChange(DataSnapshot dataSnapshot) {
                     Map<String, Object> postValues = new HashMap<String,Object>();

                     for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                         postValues.put(snapshot.getKey(),snapshot.getValue());
                     }

                     Map<String, String> values = new HashMap<>();
                     values.put("profImageSrc", downloadUrl.toString());
                     mDbref.child(tag_users).child(FBuserId).updateChildren(postValues);
                 }

                 @Override
                 public void onCancelled(DatabaseError databaseError) {

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

    public String getRealPathFromURI(Uri uri) {
        try {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            return cursor.getString(idx);
        }catch (Exception e){

            Log.i("Info", "getRealPathFromURI exception:" + e.toString());
            return null;
        }
    }

    @Override
    public void onClick(View v) {

        int i = v.getId();

        Log.i("Info","onClick works!");

        switch (i){
            case R.id.photoImageView:
                startCameraProcess();
                break;

            case R.id.skipButton:
                //startProfileActivity();
                startEmailVerifyPage();
                break;

            case R.id.nextButton:
                //startProfileActivity();
                startEmailVerifyPage();
                break;

            default:
                Toast.makeText(this, "Error occured!!", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    public void startProfileActivity(){

        Intent intent = new Intent(getApplicationContext(), ProfilePageActivity.class);
        startActivity(intent);
    }

    public void startEmailVerifyPage(){

        Intent intent = new Intent(getApplicationContext(), EmailVerifyPageActivity.class);
        startActivity(intent);
    }
}
