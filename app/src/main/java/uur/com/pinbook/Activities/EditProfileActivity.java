package uur.com.pinbook.Activities;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import uur.com.pinbook.Adapters.CustomDialogAdapter;
import uur.com.pinbook.Adapters.UriAdapter;
import uur.com.pinbook.Controller.BitmapConversion;
import uur.com.pinbook.Controller.ClearSingletonClasses;
import uur.com.pinbook.FirebaseGetData.FirebaseGetAccountHolder;
import uur.com.pinbook.FirebaseGetData.FirebaseGetFriends;
import uur.com.pinbook.JavaFiles.Friend;
import uur.com.pinbook.JavaFiles.User;
import uur.com.pinbook.LazyList.ImageLoader;
import uur.com.pinbook.R;

import static uur.com.pinbook.ConstantsModel.FirebaseConstant.Friends;
import static uur.com.pinbook.ConstantsModel.FirebaseConstant.GroupPictureUrl;
import static uur.com.pinbook.ConstantsModel.FirebaseConstant.Groups;
import static uur.com.pinbook.ConstantsModel.FirebaseConstant.Users;
import static uur.com.pinbook.ConstantsModel.FirebaseConstant.birthday;
import static uur.com.pinbook.ConstantsModel.FirebaseConstant.gender;
import static uur.com.pinbook.ConstantsModel.FirebaseConstant.mobilePhone;
import static uur.com.pinbook.ConstantsModel.FirebaseConstant.name;
import static uur.com.pinbook.ConstantsModel.FirebaseConstant.nameSurname;
import static uur.com.pinbook.ConstantsModel.FirebaseConstant.profilePictureUrl;
import static uur.com.pinbook.ConstantsModel.FirebaseConstant.surname;
import static uur.com.pinbook.ConstantsModel.FirebaseConstant.userName;
import static uur.com.pinbook.ConstantsModel.StringConstant.displayRounded;
import static uur.com.pinbook.ConstantsModel.StringConstant.friendsCacheDirectory;
import static uur.com.pinbook.ConstantsModel.StringConstant.genderFemale;
import static uur.com.pinbook.ConstantsModel.StringConstant.genderMale;
import static uur.com.pinbook.ConstantsModel.StringConstant.genderUnknown;

public class EditProfileActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    Toolbar mToolBar;
    EditText nameEditText;
    EditText surnameEditText;
    EditText usernameEditText;
    EditText birthdateEditText;
    EditText emailEditText;
    EditText phonenumEditText;
    Spinner genderSpinner;
    Button saveEditButton;
    ImageView userImageView;
    TextView changePhotoTv;

    ImageLoader imageLoader;
    boolean userInfoChanged = false;
    boolean nameOrSurnameChanged = false;
    boolean profPicChanged = false;
    ProgressDialog progressDialog;
    Uri downloadUrl;

    User userBef;
    User userCurr;

    DatePickerDialog.OnDateSetListener mDateSetListener;
    Bitmap photo;
    Uri tempUri = null;

    private static final int PROFILE_PIC_CAMERA_SELECTED = 0;
    private static final int PROFILE_PIC_GALLERY_SELECTED = 1;

    private static final int MY_PERMISSION_CAMERA = 1;
    private static final int MY_PERMISSION_WRITE_EXTERNAL_STORAGE = 2;
    private static final int MY_PERMISSION_ACTION_GET_CONTENT = 4;

    String photoChoosenType = "";
    String selectedGender;
    ArrayList<String> genderArray = new ArrayList<String>();

    ArrayAdapter<String> spinnerArrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        mToolBar = findViewById(R.id.toolbarLayout);
        mToolBar.setTitle("Profili Düzenle");
        mToolBar.setNavigationIcon(R.drawable.back_arrow);
        mToolBar.setBackgroundColor(getResources().getColor(R.color.background, null));
        mToolBar.setTitleTextColor(getResources().getColor(R.color.background_white, null));
        setSupportActionBar(mToolBar);

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        nameEditText = findViewById(R.id.nameEditText);
        surnameEditText = findViewById(R.id.surnameEditText);
        usernameEditText = findViewById(R.id.usernameEditText);
        birthdateEditText = findViewById(R.id.birthdateEditText);
        emailEditText = findViewById(R.id.emailEditText);
        phonenumEditText = findViewById(R.id.phonenumEditText);
        genderSpinner = findViewById(R.id.genderSpinner);
        saveEditButton = findViewById(R.id.saveEditButton);
        userImageView = findViewById(R.id.userImageView);
        changePhotoTv = findViewById(R.id.changePhotoTv);

        progressDialog = new ProgressDialog(this);

        userBef = new User();
        userCurr = new User();

        genderSpinner.setOnItemSelectedListener(this);

        imageLoader = new ImageLoader(this, friendsCacheDirectory);

        emailEditText.setFocusableInTouchMode(false);
        emailEditText.setFocusable(false);

        userBef = FirebaseGetAccountHolder.getInstance().getUser();
        fillUserInfo();

        setItemClickListeners();
        setGenderList();

        if (userBef.getGender() != null) {
            genderSpinner.setSelection(spinnerArrayAdapter.getPosition(userBef.getGender().toString()));
            selectedGender = userBef.getGender().toString();
        }
    }

    private void setGenderList() {
        genderArray.clear();
        genderArray.add(genderUnknown);
        genderArray.add(genderMale);
        genderArray.add(genderFemale);

        spinnerArrayAdapter = new ArrayAdapter<String>(EditProfileActivity.this, android.R.layout.simple_spinner_item, genderArray);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genderSpinner.setAdapter(spinnerArrayAdapter);
    }

    private void setItemClickListeners() {

        changePhotoTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startChooseImageProc();
            }
        });

        userImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startChooseImageProc();
            }
        });

        birthdateEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getCalender();
            }
        });

        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month = month + 1;
                String date = dayOfMonth + "/" + month + "/" + year;
                birthdateEditText.setText(date);
            }
        };

        saveEditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.setMessage("Güncelleniyor...");
                progressDialog.show();
                setUserCurrentInfo();
                updateUserInfo();
            }
        });

        genderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedGender = genderSpinner.getSelectedItem().toString();
                Log.i("Info", "selectedGender:" + selectedGender);
                ((TextView) view).setTextColor(getResources().getColor(R.color.grey_trans, null));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void startChooseImageProc() {

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
        adapter.add("  Kamerayı aç");
        adapter.add("  Galeriden seç");
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Profil Fotoğrafı Seçin");

        builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {

                if (item == PROFILE_PIC_CAMERA_SELECTED) {
                    photoChoosenType = getResources().getString(R.string.camera);
                    startCameraProcess();
                } else if (item == PROFILE_PIC_GALLERY_SELECTED) {
                    photoChoosenType = getResources().getString(R.string.gallery);
                    startGalleryProcess();
                } else {
                    Toast.makeText(EditProfileActivity.this, "Teknik Hata!!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    private void startGalleryProcess() {

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Fotoğraf Seçin"), MY_PERMISSION_ACTION_GET_CONTENT);
    }

    public void startCameraProcess() {

        if (!checkCameraHardware(this)) {
            Toast.makeText(this, "Cihazın kamerası yok!", Toast.LENGTH_SHORT).show();
            return;
        }
        checkMediaStoragePermission();
    }

    private boolean checkCameraHardware(Context context) {
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA))
            return true;
        else
            return false;
    }

    private void checkMediaStoragePermission() {

        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSION_WRITE_EXTERNAL_STORAGE);
        else
            checkCameraPermission();
    }

    private void checkCameraPermission() {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
            requestPermissions(new String[]{Manifest.permission.CAMERA}, MY_PERMISSION_CAMERA);
        else {
            Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
            startActivityForResult(intent, MY_PERMISSION_CAMERA);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {

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
                Toast.makeText(this, "Teknik Hata!", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == Activity.RESULT_OK) {

            switch (requestCode) {
                case MY_PERMISSION_CAMERA:
                    manageProfilePicChoosen(data);
                    break;

                case MY_PERMISSION_ACTION_GET_CONTENT:
                    manageProfilePicChoosen(data);
                    break;

                default:
                    Toast.makeText(this, "Teknik Hata:" + requestCode, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void manageProfilePicChoosen(Intent data) {

        String imageRealPath = null;

        if (photoChoosenType == getResources().getString(R.string.camera)) {

            photo = (Bitmap) data.getExtras().get("data");
            tempUri = getImageUri(getApplicationContext(), photo);
            imageRealPath = getRealPathFromCameraURI(tempUri);
            photo = BitmapConversion.getRoundedShape(photo, 600, 600, imageRealPath);
            photo = BitmapConversion.getBitmapOriginRotate(photo, imageRealPath);

        } else if (photoChoosenType == getResources().getString(R.string.gallery)) {

            tempUri = data.getData();
            imageRealPath = UriAdapter.getPathFromGalleryUri(getApplicationContext(), tempUri);
            InputStream profileImageStream = null;
            try {
                profileImageStream = getContentResolver().openInputStream(tempUri);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            photo = BitmapFactory.decodeStream(profileImageStream);
            photo = BitmapConversion.getRoundedShape(photo, 600, 600, imageRealPath);
        }

        userImageView.setImageBitmap(photo);
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        try {
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
            String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
            return Uri.parse(path);
        } catch (Exception e) {
            Toast.makeText(this, "Teknik Hata:" + e.toString(), Toast.LENGTH_SHORT).show();
            return null;
        }
    }

    public String getRealPathFromCameraURI(Uri contentUri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = managedQuery(contentUri, proj, null, null, null);
        int column_index = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    public void getCalender() {

        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog = new DatePickerDialog(EditProfileActivity.this,
                android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                mDateSetListener,
                year, month, day);

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
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

    private void fillUserInfo() {

        imageLoader.DisplayImage(userBef.getProfilePicSrc(), userImageView, displayRounded);

        if (userBef.getName() != null)
            nameEditText.setText(userBef.getName().toString());
        if (userBef.getSurname() != null)
            surnameEditText.setText(userBef.getSurname().toString());
        if (userBef.getUsername() != null)
            usernameEditText.setText(userBef.getUsername().toString());
        if (userBef.getBirthdate() != null)
            birthdateEditText.setText(userBef.getBirthdate().toString());
        if (userBef.getEmail() != null)
            emailEditText.setText(userBef.getEmail().toString());
        if (userBef.getPhoneNum() != null)
            phonenumEditText.setText(userBef.getPhoneNum().toString());
    }

    public void setUserCurrentInfo() {

        userCurr.setName(nameEditText.getText().toString());
        userCurr.setSurname(surnameEditText.getText().toString());
        userCurr.setUsername(usernameEditText.getText().toString());
        userCurr.setBirthdate(birthdateEditText.getText().toString());
        userCurr.setPhoneNum(phonenumEditText.getText().toString());
        userCurr.setGender(selectedGender);
    }

    public void updateUserInfo() {

        DatabaseReference mDbref = FirebaseDatabase.getInstance().getReference().child(Users).child(userBef.getUserId());

        Map<String, Object> values = new HashMap<>();

        if (!userCurr.getName().toString().equals(userBef.getName().toString())) {
            FirebaseGetAccountHolder.getInstance().getUser().setName(userCurr.getName().toString());
            values.put(name, userCurr.getName().toString());
            userInfoChanged = true;
            nameOrSurnameChanged = true;
        }

        if (!userCurr.getSurname().toString().equals(userBef.getSurname().toString())) {
            FirebaseGetAccountHolder.getInstance().getUser().setSurname(userCurr.getSurname().toString());
            values.put(surname, userCurr.getSurname().toString());
            userInfoChanged = true;
            nameOrSurnameChanged = true;
        }

        if (!userCurr.getUsername().toString().equals(userBef.getUsername().toString())) {
            FirebaseGetAccountHolder.getInstance().getUser().setUsername(userCurr.getUsername().toString());
            values.put(userName, userCurr.getUsername().toString());
            userInfoChanged = true;
        }

        if (!userCurr.getBirthdate().toString().equals(userBef.getBirthdate().toString())) {
            FirebaseGetAccountHolder.getInstance().getUser().setBirthdate(userCurr.getBirthdate().toString());
            values.put(birthday, userCurr.getBirthdate().toString());
            userInfoChanged = true;
        }

        if (!userCurr.getPhoneNum().toString().equals(userBef.getPhoneNum().toString())) {
            FirebaseGetAccountHolder.getInstance().getUser().setPhoneNum(userCurr.getPhoneNum().toString());
            values.put(mobilePhone, userCurr.getPhoneNum().toString());
            userInfoChanged = true;
        }

        if (!userCurr.getGender().toString().equals(userBef.getGender().toString())) {
            FirebaseGetAccountHolder.getInstance().getUser().setGender(userCurr.getGender().toString());
            values.put(gender, userCurr.getGender().toString());
            userInfoChanged = true;
        }

        if (userInfoChanged)
            mDbref.updateChildren(values);

        if(tempUri != null)
            saveProfPicViaEmailVerify();
        else {
            new UpdateFBChild().execute((Void) null);
            if(progressDialog.isShowing())
                progressDialog.dismiss();
            finish();
        }
    }

    public void saveProfPicViaEmailVerify() {

        StorageReference mStorageRef = FirebaseStorage.getInstance().getReference();

        StorageReference riversRef  = mStorageRef.child("Users/profilePics").child(userBef.getUserId().toString() + ".jpg");

        riversRef.putFile(tempUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        downloadUrl = taskSnapshot.getDownloadUrl();
                        userCurr.setProfilePicSrc(downloadUrl.toString());

                        profPicChanged = true;

                        FirebaseGetAccountHolder.getInstance().getUser().setProfilePicSrc(downloadUrl.toString());

                        Map<String, Object> values = new HashMap<>();

                        DatabaseReference mDbref = FirebaseDatabase.getInstance().getReference().child(Users).child(userBef.getUserId());
                        values.put(profilePictureUrl, downloadUrl.toString());
                        mDbref.updateChildren(values);

                        new UpdateFBChild().execute((Void) null);

                        progressDialog.dismiss();
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {

                        Toast.makeText(EditProfileActivity.this, "Teknik Hata:" + exception.toString(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public class UpdateFBChild extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {

            if(!profPicChanged && !nameOrSurnameChanged)
                return null;

            for(Friend friend: FirebaseGetFriends.getFBGetFriendsInstance().getFriendList()){
                DatabaseReference mDbref = FirebaseDatabase.getInstance().getReference().child(Friends).
                        child(friend.getUserID()).child(userBef.getUserId());

                Map<String, Object> values = new HashMap<>();

                if(nameOrSurnameChanged)
                    values.put(nameSurname, userCurr.getName().toString() + " " + userCurr.getSurname().toString());

                if(profPicChanged)
                    values.put(profilePictureUrl, downloadUrl.toString());

                mDbref.updateChildren(values);
            }

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

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
