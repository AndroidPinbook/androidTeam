package uur.com.pinbook.Activities;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.support.v13.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import uur.com.pinbook.R;

public class ProfilePhotoActivity extends AppCompatActivity implements View.OnClickListener{

    ImageView photoImgView;
    ImageView galleryImgView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_photo);

        photoImgView = (ImageView) findViewById(R.id.photoImageView);
        galleryImgView = (ImageView) findViewById(R.id.galleryImageView);

        findViewById(R.id.photoImageView).setOnClickListener(this);
        findViewById(R.id.galleryImageView).setOnClickListener(this);
    }

    public void startCameraProcess(){

        if(!checkCameraHardware(this)){
            Toast.makeText(this, "Device has no camera!", Toast.LENGTH_SHORT).show();
            return;
        }

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA},1);
        }else{

            try {
                Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                startActivityForResult(intent, 1);
            }catch (Exception e){
                Log.i("Info","Camera open err:" + e.toString());
            }
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        Log.i("Info", "onActivityResult starts");

        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {

            Log.i("Info", "  >>onActivityResult");

            ImageView photoImageView = (ImageView) findViewById(R.id.photoImageView);
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            photoImgView.setImageBitmap(photo);
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
                startProfileActivity();
                break;

            case R.id.nextButton:
                startProfileActivity();
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
}
