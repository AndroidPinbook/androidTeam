package uur.com.pinbook.Activities;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.PhoneNumberUtils;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import android.Manifest;

import uur.com.pinbook.JavaFiles.User;
import uur.com.pinbook.R;

public class RegisterPageActivity extends AppCompatActivity implements View.OnClickListener,
        FirebaseAuth.AuthStateListener{

    //Set the radius of the Blur. Supported range 0 < radius <= 25
    private static final float BLUR_RADIUS = 10f;

    private Button registerButton;

    private EditText usernameEditText;
    private EditText nameEditText;
    private EditText surnameEditText;
    private EditText phoneEditText;
    private EditText birthdateEditText;
    private EditText passwordEditText;
    private EditText emailEditText;

    private FirebaseAuth mAuth;
    private DatabaseReference mDbref;

    public String tag_users = "users";

    public String gender;
    String phoneNumber = " ";
    String beforePhoneNum = " ";

    public boolean genderSelected = false;

    Handler handler;
    private boolean onCreateOk = false;
    private boolean runnableOk = false;

    public ImageView maleImageView;
    public ImageView femaleImageView;

    public Calendar myCalendar;

    RelativeLayout backGrounRelLayout;

    public User user;

    private DatePickerDialog.OnDateSetListener mDateSetListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_page);

        backGrounRelLayout = (RelativeLayout) findViewById(R.id.registerLayout);

        user = new User();

        mAuth = FirebaseAuth.getInstance();

        registerButton = findViewById(R.id.registerButton);

        usernameEditText = findViewById(R.id.usernameEditText);
        nameEditText = findViewById(R.id.nameEditText);
        surnameEditText = findViewById(R.id.surnameEditText);
        phoneEditText = findViewById(R.id.phoneEditText);
        birthdateEditText = findViewById(R.id.birthdateEditText);
        maleImageView = findViewById(R.id.maleImageView);
        femaleImageView = findViewById(R.id.femaleImageView);
        passwordEditText = findViewById(R.id.passwordEditText);
        emailEditText = findViewById(R.id.emailEditText);

        findViewById(R.id.registerButton).setOnClickListener(this);
        findViewById(R.id.birthdateEditText).setOnClickListener(this);
        findViewById(R.id.maleImageView).setOnClickListener(this);
        findViewById(R.id.femaleImageView).setOnClickListener(this);

        backGrounRelLayout.setOnClickListener(this);

        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                month = month + 1;
                String date =  dayOfMonth + "/"  + month + "/" + year;
                birthdateEditText.setText(date);
            }
        };

        phoneEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

              /*  Log.i("Info","afterTextChanged");

                phoneNumber = phoneEditText.getText().toString();

                Log.i("Info","beforePhoneNum:" + beforePhoneNum);
                Log.i("Info","phoneNumber   :" + phoneNumber);

                int a = phoneNumber.trim().length();

                if(!beforePhoneNum.equals(phoneNumber) &&
                        a >= 10) {

                    Log.i("Info","         >>ICERDEYIM");

                    beforePhoneNum = phoneNumber;

                    phoneNumber = formatE164Number(Locale.getDefault().getCountry(), phoneEditText.getText().toString());
                    phoneEditText.setText(phoneNumber);


                    Log.i("Info","beforePhoneNum_2:" + beforePhoneNum);
                }*/
            }
        });
    }

    public void getCalender(){

        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog = new DatePickerDialog(RegisterPageActivity.this,
                android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                mDateSetListener,
                year, month, day);

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    private void createAccount(String email, String password){

        Log.i("Info","createAccount method=====");

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        Log.i("TaskExp","TaskExp:" + task.getException());

                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.i("Info","CreateUserEmail:Success");

                            FirebaseUser user = mAuth.getCurrentUser();

                            saveUserInfo(user);

                            startProfilePhotoPage();

                            onCreateOk = true;

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.i("Info","CreateUserEmail:Failed:" + task.getException());

                            //Toast.makeText(RegisterPageActivity.this, "Authentication failed." + task.getException(),
                            //        Toast.LENGTH_SHORT).show();

                            /*Toast toast = Toast.makeText(RegisterPageActivity.this, "Authentication failed:" + task.getException(),
                                    Toast.LENGTH_SHORT);
                            TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
                            if( v != null) v.setGravity(Gravity.CENTER);

                            toast.show();*/


                            LayoutInflater inflater = getLayoutInflater();
                            View view = inflater.inflate(R.layout.cust_toast_layout,
                                    (ViewGroup) findViewById(R.id.relativeLayout1));


                            Toast toast = new Toast(RegisterPageActivity.this);
                            toast.setGravity(Gravity.CENTER_VERTICAL,0,0);
                            toast.setView(view);
                            toast.show();
                        }
                    }
                });
    }

    public void startProfilePhotoPage(){

        Intent intent = new Intent(getApplicationContext(), ProfilePhotoActivity.class);
        intent.putExtra("User", user);
        startActivity(intent);
    }

    public void saveUserInfo(FirebaseUser currentUser){

        user.setUserId(currentUser.getUid());
        user.setEmail(currentUser.getEmail());
        user.setUsername(usernameEditText.getText().toString());
        user.setName(nameEditText.getText().toString());
        user.setSurname(surnameEditText.getText().toString());
        user.setGender(gender);
        user.setBirthdate(birthdateEditText.getText().toString());
        user.setPhoneNum(phoneEditText.getText().toString());
    }

    @Override
    public void onClick(View v) {

        int i = v.getId();

        Log.i("Info","onClick works!");

        switch (i){
            case R.id.registerButton:
                if(!validateForm()){
                    return;
                }

                String email = emailEditText.getText().toString();
                String password = passwordEditText.getText().toString();

                createAccount(email, password);
                break;

            case R.id.birthdateEditText:
                getCalender();
                break;

            case R.id.registerLayout:
                hideKeyBoard();
                break;

            case R.id.maleImageView:
                scaleAnimation(i);
                break;

            case R.id.femaleImageView:
                scaleAnimation(i);
                break;

            default:
                Toast.makeText(this, "Error occured!!", Toast.LENGTH_SHORT).show();
                break;
        }
    }



    public void hideKeyBoard(){

        Log.i("Info", "hideKeyBoard");

        InputMethodManager inputMethodManager =(InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
    }

    private boolean validateForm() {
        boolean valid = true;

        Log.i("Info", "validateForm");

        String email = emailEditText.getText().toString();

        if (TextUtils.isEmpty(email)) {
            emailEditText.setError("Required.");
            valid = false;
        } else {
            emailEditText.setError(null);
        }

        String password = passwordEditText.getText().toString();

        if (TextUtils.isEmpty(password)) {
            passwordEditText.setError("Required.");
            valid = false;
        } else {
            passwordEditText.setError(null);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            phoneEditText.setText(PhoneNumberUtils.formatNumber(phoneEditText.getText().toString(), Locale.getDefault().getCountry()));
            Log.i("Info","standart phone num:" + phoneEditText.getText().toString());
        } else {
            phoneEditText.setText(PhoneNumberUtils.formatNumber(phoneEditText.getText().toString())); //Deprecated method
        }

        return valid;
    }

    public String formatE164Number(String countryCode, String phNum) {

        //Log.i("Info","formatE164Number");
        //Log.i("Info","  >>countryCode:" + countryCode);
        //Log.i("Info","  >>phNum      :" + phNum);

        String e164Number;
        if (TextUtils.isEmpty(countryCode)) {
            e164Number = phNum;
        } else {
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP) {
                e164Number = PhoneNumberUtils.formatNumberToE164(phNum, countryCode);
                //Log.i("Info","  >>e164Number1      :" + e164Number);
            } else {
                try {
                    PhoneNumberUtil instance = PhoneNumberUtil.getInstance();
                    Phonenumber.PhoneNumber phoneNumber = instance.parse(phNum, countryCode);
                    e164Number = instance.format(phoneNumber, PhoneNumberUtil.PhoneNumberFormat.E164);
                    //Log.i("Info","  >>e164Number2      :" + e164Number);

                } catch (NumberParseException e) {
                    Log.i("Info" ," Phone error"+ e.getMessage());
                    e164Number = phNum;
                    Log.i("Info","  >>e164Number3      :" + e164Number);
                }
            }
        }

        //Log.i("Info","  >>e164Numberlast      :" + e164Number);

        return e164Number;
    }

    public void scaleAnimation(int genderType){

        Log.i("Info","scaleMaleAnimation");

        ObjectAnimator objectAnimator;

        if(genderType == R.id.maleImageView){
            gender = (String) getString(R.string.male_text);

            objectAnimator = ObjectAnimator.ofFloat(femaleImageView, "alpha", 1.0f, 0.3f);
            objectAnimator.setDuration(300);
            objectAnimator.start();

            if(genderSelected) {
                objectAnimator = ObjectAnimator.ofFloat(maleImageView, "alpha", 0.3f, 1.0f);
                objectAnimator.setDuration(300);
                objectAnimator.start();
            }

        }else{
            gender = (String) getString(R.string.female_text);

            objectAnimator = ObjectAnimator.ofFloat(maleImageView, "alpha", 1.0f, 0.3f);
            objectAnimator.setDuration(300);
            objectAnimator.start();

            if(genderSelected) {
                objectAnimator = ObjectAnimator.ofFloat(femaleImageView, "alpha", 0.3f, 1.0f);
                objectAnimator.setDuration(300);
                objectAnimator.start();
            }
        }

        genderSelected = true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == 1){

            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){

                if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED){

                    try {
                        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                        startActivity(intent);
                    }catch (Exception e){
                        Log.i("Info","Camera open err:" + e.toString());
                    }
                }
            }
        }
    }

    @Override
    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

    }
}
