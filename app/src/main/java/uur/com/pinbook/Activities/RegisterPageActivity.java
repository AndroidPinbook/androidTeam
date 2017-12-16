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
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
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

import uur.com.pinbook.Controller.CustomDialogAdapter;
import uur.com.pinbook.Controller.ErrorMessageAdapter;
import uur.com.pinbook.Controller.ValidationAdapter;
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

    public User user = null;

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
                            Log.i("Info","CreateUserEmail:Failed:" + task.getException());

                            try {
                                throw task.getException();
                            } catch(FirebaseAuthUserCollisionException e) {
                                CustomDialogAdapter.showDialogError(RegisterPageActivity.this, ErrorMessageAdapter.COLLISION_EXCEPTION.getText());
                            } catch(Exception e) {
                                Log.i("error :", e.getMessage());
                            }
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
        String password = passwordEditText.getText().toString();
        String username = usernameEditText.getText().toString();
        String name = nameEditText.getText().toString();
        String surname = surnameEditText.getText().toString();
        String birthdate = birthdateEditText.getText().toString();

        //Email - password check
        if(TextUtils.isEmpty(email) || TextUtils.isEmpty((password))){
            Log.i("i", "1");
            if (TextUtils.isEmpty(email)) {
                emailEditText.setError("Required");
                valid = false;
            } else {
                if(!ValidationAdapter.isValidEmail(email)){
                    emailEditText.setError("Email is not valid");
                    valid = false;

                }else{
                    emailEditText.setError(null);
                }
            }

            if (TextUtils.isEmpty(password)) {
                Log.i("i", "2");
                passwordEditText.setError("Required");
                valid = false;
            } else {
                Log.i("i", "3");
                if(!ValidationAdapter.isValidPassword(password)){
                    Log.i("i", "4");
                    passwordEditText.setError("Password min length is 6");
                    valid = false;

                }else{
                    Log.i("i", "5");
                    passwordEditText.setError(null);
                }
            }

        }else{
            if(!ValidationAdapter.isValidEmail(email)){
                emailEditText.setError("Email is not valid");
                valid = false;

            }else{
                emailEditText.setError(null);
            }

            if(!ValidationAdapter.isValidPassword(password)){
                passwordEditText.setError("Password min length is 6");
                valid = false;
            }else{
                passwordEditText.setError(null);
            }

        }

        //Username check
        if (TextUtils.isEmpty(username)) {
            usernameEditText.setError("Required");
            valid = false;
        } else {
            usernameEditText.setError(null);
        }
        //Name check
        if (TextUtils.isEmpty(name)) {
            nameEditText.setError("Required");
            valid = false;
        } else {
            nameEditText.setError(null);
        }
        //Surname check
        if (TextUtils.isEmpty(surname)) {
            surnameEditText.setError("Required");
            valid = false;
        } else {
            surnameEditText.setError(null);
        }
        //Birthdate check
        if (TextUtils.isEmpty(birthdate)) {
            birthdateEditText.setError("Required");
            valid = false;
        } else {
            birthdateEditText.setError(null);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            phoneEditText.setText(PhoneNumberUtils.formatNumber(phoneEditText.getText().toString(), Locale.getDefault().getCountry()));
            Log.i("Info","standart phone num:" + phoneEditText.getText().toString());
        } else {
            phoneEditText.setText(PhoneNumberUtils.formatNumber(phoneEditText.getText().toString())); //Deprecated method
        }

        return valid;
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
    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

    }

    @Override
    public void onBackPressed() {

        Intent intent = new Intent(getApplicationContext(), EnterPageActivity.class);
        startActivity(intent);
    }
}
