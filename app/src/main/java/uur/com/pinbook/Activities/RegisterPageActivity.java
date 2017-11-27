package uur.com.pinbook.Activities;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
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
import android.view.View;
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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import android.Manifest;
import uur.com.pinbook.R;

public class RegisterPageActivity extends AppCompatActivity implements View.OnClickListener, FirebaseAuth.AuthStateListener{

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_page);

        backGrounRelLayout = (RelativeLayout) findViewById(R.id.registerLayout);

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

        Log.i("Info", "getCalender");

        myCalendar = Calendar.getInstance();
        final DatePickerDialog.OnDateSetListener date;

        date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }

        };

        birthdateEditText.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(RegisterPageActivity.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
    }

    private void updateLabel() {

        Log.i("Info", "  >>updateLabel");
        String myFormat = "MM/dd/yy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        birthdateEditText.setText(sdf.format(myCalendar.getTime()));
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

                            Toast.makeText(RegisterPageActivity.this, "Authentication failed." + task.getException(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void startProfilePhotoPage(){

        Intent intent = new Intent(getApplicationContext(), ProfilePhotoActivity.class);
        startActivity(intent);
    }

    public void saveUserInfo(FirebaseUser currentUser){

        String userId = currentUser.getUid();

        Map<String, String> values = new HashMap<>();

        Log.i("Info","userId:" + userId);

        mDbref = FirebaseDatabase.getInstance().getReference().child(tag_users);

        values.put("email", currentUser.getEmail());
        setValuesToCloud(userId, values);

        values.put("gender", gender);
        setValuesToCloud(userId, values);

        values.put("username", usernameEditText.getText().toString());
        setValuesToCloud(userId, values);

        values.put("name", nameEditText.getText().toString());
        setValuesToCloud(userId, values);

        values.put("surname", surnameEditText.getText().toString());
        setValuesToCloud(userId, values);

        values.put("phone", phoneEditText.getText().toString());
        setValuesToCloud(userId, values);

        values.put("birthdate", birthdateEditText.getText().toString());
        setValuesToCloud(userId, values);
    }

    public void setValuesToCloud(String userId, Map<String, String> values){

        mDbref.child(userId).setValue(values, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                Log.i("Info","databaseError:" + databaseError);
            }
        });
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
