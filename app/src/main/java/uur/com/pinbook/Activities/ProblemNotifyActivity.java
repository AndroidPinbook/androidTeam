package uur.com.pinbook.Activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableResult;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.InputStream;
import uur.com.pinbook.FirebaseGetData.FirebaseGetAccountHolder;
import uur.com.pinbook.R;

import static uur.com.pinbook.ConstantsModel.FirebaseConstant.*;
import static uur.com.pinbook.ConstantsModel.FirebaseFunctionsConstant.*;

public class ProblemNotifyActivity extends AppCompatActivity {

    Toolbar mToolBar;
    ImageView addPictureImgv1;
    ImageView addPictureImgv2;
    ImageView addPictureImgv3;

    ImageView imgDelete1;
    ImageView imgDelete2;
    ImageView imgDelete3;

    EditText noteTextEditText;

    Button sendNotifyBtn;

    boolean img_1_selected = false;
    boolean img_2_selected = false;
    boolean img_3_selected = false;

    boolean userTextInserted = false;

    Uri uriForImage1 = null;
    Uri uriForImage2 = null;
    Uri uriForImage3 = null;

    int imageViewSelected = 0;
    ProgressDialog mProgressDialog;

    String key;

    private static final int MY_PERMISSION_ACTION_GET_CONTENT = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_problem_notify);

        mToolBar = findViewById(R.id.toolbarLayout);
        mToolBar.setTitle("Sorun/Görüş Bildir");
        mToolBar.setNavigationIcon(R.drawable.back_arrow);
        mToolBar.setBackgroundColor(getResources().getColor(R.color.background, null));
        mToolBar.setTitleTextColor(getResources().getColor(R.color.background_white, null));
        setSupportActionBar(mToolBar);

        noteTextEditText = findViewById(R.id.noteTextEditText);
        addPictureImgv1 = findViewById(R.id.addPictureImgv1);
        addPictureImgv2 = findViewById(R.id.addPictureImgv2);
        addPictureImgv3 = findViewById(R.id.addPictureImgv3);

        imgDelete1 = findViewById(R.id.imgDelete1);
        imgDelete2 = findViewById(R.id.imgDelete2);
        imgDelete3 = findViewById(R.id.imgDelete3);

        sendNotifyBtn = findViewById(R.id.sendNotifyBtn);

        key = FirebaseDatabase.getInstance().getReference().child(ErrNotifies).push().getKey();
        DatabaseReference mdbRef = FirebaseDatabase.getInstance().getReference(ErrNotifies).child(key);

        addPictureImgv1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageViewSelected = 1;
                startGalleryProcess();
            }
        });

        addPictureImgv2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageViewSelected = 2;
                startGalleryProcess();
            }
        });

        addPictureImgv3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageViewSelected = 3;
                startGalleryProcess();
            }
        });

        imgDelete1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addPictureImgv1.setImageResource(R.drawable.add_new_problem);
                imgDelete1.setVisibility(View.GONE);
                img_1_selected = false;
                uriForImage1 = null;
            }
        });

        imgDelete2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addPictureImgv2.setImageResource(R.drawable.add_new_problem);
                imgDelete2.setVisibility(View.GONE);
                img_2_selected = false;
                uriForImage2 = null;
            }
        });

        imgDelete3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addPictureImgv3.setImageResource(R.drawable.add_new_problem);
                imgDelete3.setVisibility(View.GONE);
                img_3_selected = false;
                uriForImage3 = null;
            }
        });

        sendNotifyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!checkInformData()) return;

                mProgressDialog = new ProgressDialog(ProblemNotifyActivity.this);
                mProgressDialog.setMessage("Gönderiliyor.....");
                mProgressDialog.show();
                new SaveErrDetails().execute(" ");
                problemInformTh.start();
            }
        });
    }

    public boolean checkInformData() {

        if (uriForImage1 == null & uriForImage2 == null && uriForImage3 == null && noteTextEditText.getText().toString().isEmpty()) {
            Snackbar.make(findViewById(R.id.container), "Lütfen açıklama veya resim ekleyin.", Snackbar.LENGTH_LONG)
                    .show();
            return false;
        }

        return true;
    }

    Thread problemInformTh = new Thread() {
        @Override
        public void run() {
            try {
                Thread.sleep(1000);
                if (mProgressDialog.isShowing()) mProgressDialog.dismiss();
                finish();
                problemInformTh.interrupt();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    };

    public void savePicToFirebase(final int imageId, Uri uriImage) {

        final String errImageId = "image" + Integer.toString(imageId);

        StorageReference riversRef = FirebaseStorage.getInstance().getReference().
                child(ErrNotifies).child(key).
                child(errImageId + ".jpg");

        riversRef.putFile(uriImage)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        Uri downloadUrl = taskSnapshot.getDownloadUrl();

                        JSONObject imageJsonMain = null;
                        try {
                            JSONObject imageJsonDtl = new JSONObject();
                            imageJsonDtl.put(errImageId, downloadUrl.toString());

                            imageJsonMain = new JSONObject();
                            imageJsonMain.put(key, imageJsonDtl);
                        } catch (JSONException e) {
                            Snackbar.make(findViewById(R.id.container), "Teknik Hata:" + e.toString(), Snackbar.LENGTH_LONG)
                                    .show();
                            e.printStackTrace();
                            return;
                        }

                        FirebaseFunctions.getInstance()
                                .getHttpsCallable(addErrNotifies)
                                .call(imageJsonMain)
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.i("Info", "Function call failure:" + e.toString());
                                    }
                                }).addOnSuccessListener(new OnSuccessListener<HttpsCallableResult>() {
                            @Override
                            public void onSuccess(HttpsCallableResult httpsCallableResult) {
                                Log.i("Info", "Function call is ok");
                            }
                        });

                        saveErrInfoDetails();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {

                    }
                });
    }

    public class SaveErrDetails extends AsyncTask<String, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(String... params) {

            if (uriForImage1 == null && uriForImage2 == null && uriForImage3 == null) {
                saveErrInfoDetails();
                return null;
            }

            if (uriForImage1 != null) savePicToFirebase(1, uriForImage1);
            if (uriForImage2 != null) savePicToFirebase(2, uriForImage2);
            if (uriForImage3 != null) savePicToFirebase(3, uriForImage3);

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onCancelled(Void result) {
            super.onCancelled(result);
        }
    }

    public void saveErrInfoDetails() {

        if (!userTextInserted) {

            JSONObject textJsonMain = null;
            try {
                JSONObject textJsonDtl = new JSONObject();
                textJsonDtl.put(fbUserId, FirebaseGetAccountHolder.getUserID());
                textJsonDtl.put(text, noteTextEditText.getText().toString());

                textJsonMain = new JSONObject();
                textJsonMain.put(key, textJsonDtl);
            } catch (JSONException e) {
                Snackbar.make(findViewById(R.id.container), "Teknik Hata:" + e.toString(), Snackbar.LENGTH_LONG)
                        .show();
                e.printStackTrace();
                return;
            }

            FirebaseFunctions.getInstance()
                    .getHttpsCallable(addErrNotifies)
                    .call(textJsonMain)
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.i("Info", "Function call failure:" + e.toString());
                        }
                    }).addOnSuccessListener(new OnSuccessListener<HttpsCallableResult>() {
                @Override
                public void onSuccess(HttpsCallableResult httpsCallableResult) {
                    Log.i("Info", "Function call is ok");
                }
            });


            userTextInserted = true;

            JSONObject userErrMainJson = null;
            try {
                userErrMainJson = new JSONObject();
                userErrMainJson.put(errorId, key);
                userErrMainJson.put(fbUserId,FirebaseGetAccountHolder.getUserID() );
            } catch (JSONException e) {
                Snackbar.make(findViewById(R.id.container), "Teknik Hata:" + e.toString(), Snackbar.LENGTH_LONG)
                        .show();
                e.printStackTrace();
                return;
            }

            FirebaseFunctions.getInstance()
                    .getHttpsCallable(addUserErrorNotif)
                    .call(userErrMainJson)
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.i("Info", "Function call failure:" + e.toString());
                        }
                    }).addOnSuccessListener(new OnSuccessListener<HttpsCallableResult>() {
                @Override
                public void onSuccess(HttpsCallableResult httpsCallableResult) {
                    Log.i("Info", "Function call is ok");
                }
            });
        }
    }

    private void startGalleryProcess() {

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Fotoğraf Seçin"), MY_PERMISSION_ACTION_GET_CONTENT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case MY_PERMISSION_ACTION_GET_CONTENT:
                    manageProfilePicChoosen(data);
                    break;

                default:
                    Toast.makeText(this, "Teknik Hata:" + requestCode, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void manageProfilePicChoosen(Intent data) {

        Uri tempUri = data.getData();
        InputStream profileImageStream = null;
        try {
            profileImageStream = getContentResolver().openInputStream(tempUri);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        Bitmap photo = BitmapFactory.decodeStream(profileImageStream);

        if (imageViewSelected == 1) {
            addPictureImgv1.setImageBitmap(photo);
            //addPictureImgv2.setVisibility(View.VISIBLE);
            imgDelete1.setVisibility(View.VISIBLE);
            img_1_selected = true;
            uriForImage1 = tempUri;
        }

        if (imageViewSelected == 2) {
            addPictureImgv2.setImageBitmap(photo);
            //addPictureImgv3.setVisibility(View.VISIBLE);
            imgDelete2.setVisibility(View.VISIBLE);
            img_2_selected = true;
            uriForImage2 = tempUri;
        }

        if (imageViewSelected == 3) {
            addPictureImgv3.setImageBitmap(photo);
            imgDelete3.setVisibility(View.VISIBLE);
            img_3_selected = true;
            uriForImage3 = tempUri;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return super.onOptionsItemSelected(item);
    }

}
