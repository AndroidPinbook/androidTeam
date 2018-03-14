package uur.com.pinbook.Activities;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.PhoneNumberUtils;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

import twitter4j.HttpClient;
import twitter4j.HttpResponse;
import uur.com.pinbook.Adapters.CustomDialogAdapter;
import uur.com.pinbook.Adapters.ErrorMessageAdapter;
import uur.com.pinbook.Adapters.ValidationAdapter;
import uur.com.pinbook.ConstantsModel.StringConstant;
import uur.com.pinbook.Controller.HttpHandler;
import uur.com.pinbook.JavaFiles.Friend;
import uur.com.pinbook.JavaFiles.User;
import uur.com.pinbook.ListAdapters.InviteOutboundVerListAdapter;
import uur.com.pinbook.R;

import static uur.com.pinbook.ConstantsModel.FirebaseConstant.PhoneNums;
import static uur.com.pinbook.ConstantsModel.FirebaseConstant.Users;
import static uur.com.pinbook.ConstantsModel.FirebaseConstant.birthday;
import static uur.com.pinbook.ConstantsModel.FirebaseConstant.email;
import static uur.com.pinbook.ConstantsModel.FirebaseConstant.gender;
import static uur.com.pinbook.ConstantsModel.FirebaseConstant.mobilePhone;
import static uur.com.pinbook.ConstantsModel.FirebaseConstant.name;
import static uur.com.pinbook.ConstantsModel.FirebaseConstant.profilePictureUrl;
import static uur.com.pinbook.ConstantsModel.FirebaseConstant.providerId;
import static uur.com.pinbook.ConstantsModel.FirebaseConstant.surname;
import static uur.com.pinbook.ConstantsModel.FirebaseConstant.userName;
import static uur.com.pinbook.ConstantsModel.StringConstant.*;

public class RegisterPageActivity extends AppCompatActivity implements View.OnClickListener,
        FirebaseAuth.AuthStateListener, AdapterView.OnItemSelectedListener{

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
    private Spinner countrySpinner;

    private FirebaseAuth mAuth;
    private DatabaseReference mDbref;

    public String tag_users = "users";

    public HashMap<String, String> countryNameList = new HashMap<>();
    public HashMap<String, String> countryPhoneList = new HashMap<>();

    public String gender;
    String phoneNumber = " ";
    String beforePhoneNum = " ";

    public boolean genderSelected = false;
    DatabaseReference databaseReference;
    ValueEventListener valueEventListener;

    Handler handler;
    private boolean onCreateOk = false;
    private boolean runnableOk = false;

    private String clearPhoneNum = "";

    public ImageView maleImageView;
    public ImageView femaleImageView;

    public Calendar myCalendar;

    RelativeLayout backGrounRelLayout;

    ArrayList<String> countryNameArray = new ArrayList<String>();

    public User user = null;

    private DatePickerDialog.OnDateSetListener mDateSetListener;

    String selectedCountryName;
    String selectedPhoneNum;
    int selectedCountyPosition;
    String selectedCountryCode;

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
        countrySpinner = findViewById(R.id.countrySpinner);

        registerButton.setOnClickListener(this);
        birthdateEditText.setOnClickListener(this);
        maleImageView.setOnClickListener(this);
        femaleImageView.setOnClickListener(this);
        //countrySpinner.setOnItemClickListener((AdapterView.OnItemClickListener) this);
        countrySpinner.setOnItemSelectedListener((AdapterView.OnItemSelectedListener) this);
        backGrounRelLayout.setOnClickListener(this);

        new GetCountryNameList().execute();
        new GetPhoneCodeList().execute();

        selectedCountryName = countryNameTurkey;
        selectedPhoneNum = phoneCodeTurkey;

        countrySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedCountryName = countrySpinner.getSelectedItem().toString();

                findCountryCode(selectedCountryName);
                Log.i("Info","selectedCountryName:" + selectedCountryName);
                Log.i("Info","position:" + position);
                Log.i("Info","selectedCountryCode:" + selectedCountryCode);
                selectedPhoneNum = countryPhoneList.get(selectedCountryCode);
                Log.i("Info","selectedPhoneNum:" + selectedPhoneNum);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                month = month + 1;
                String date =  dayOfMonth + "/"  + month + "/" + year;
                birthdateEditText.setText(date);
            }
        };
    }

    public void findCountryCode(String countryName){

        for ( String key : countryNameList.keySet() ) {
            if(countryNameList.get(key).equals(countryName)){
                selectedCountryCode = key;
                break;
            }
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
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

                            //databaseReference.removeEventListener(valueEventListener);

                            startProfilePhotoPage();

                            onCreateOk = true;

                        } else {
                            Log.i("Info","CreateUserEmail:Failed:" + task.getException());

                            try {
                                throw task.getException();
                            } catch(FirebaseAuthUserCollisionException e) {
                                CustomDialogAdapter.showDialogError(RegisterPageActivity.this, ErrorMessageAdapter.COLLISION_EXCEPTION.getText());
                            } catch(Exception e) {
                                CustomDialogAdapter.showDialogError(RegisterPageActivity.this, ErrorMessageAdapter.UNKNOW_ERROR.getText());
                                Log.i("error ", e.toString());
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
        user.setPhoneNum(clearPhoneNum);
    }

    @Override
    public void onClick(View v) {

        int i = v.getId();

        Log.i("Info","onClick works!");

        switch (i){
            case R.id.registerButton:
                clearPhoneNum = "";
                if(!validateForm()){
                    return;
                }

                String userEmail = emailEditText.getText().toString();
                String userPassword = passwordEditText.getText().toString();

                checkPhoneNumExistance(userEmail, userPassword);
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

            case R.id.countrySpinner:
                break;

            default:
                break;
        }
    }

    private void checkPhoneNumExistance(final String uMail, final String uPassword) {

        final FirebaseDatabase db = FirebaseDatabase.getInstance();
        databaseReference = db.getReference(PhoneNums).child(clearPhoneNum);

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(dataSnapshot.getValue() != null)
                    CustomDialogAdapter.showDialogWarning(RegisterPageActivity.this,
                            "Telefon Bilgisi Sistemde Kayitli");
                else
                    createAccount(uMail, uPassword);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void hideKeyBoard(){

        Log.i("Info", "hideKeyBoard");

        InputMethodManager inputMethodManager =(InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
        //inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);

        if (getCurrentFocus() != null) {
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
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
        Log.i("Info", "complexPhone:" + selectedPhoneNum + phoneEditText.getText().toString());
        getClearPhoneNum(selectedPhoneNum + phoneEditText.getText().toString());

        return valid;
    }

    public void getClearPhoneNum(String complexPhoneNum){

        for(int i=0; i < complexPhoneNum.length(); i++){
            char ch = complexPhoneNum.charAt(i);
            if(Character.isDigit(ch)){
                clearPhoneNum += ch;
            }
        }
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

    private class GetCountryNameList extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... arg0) {

            HttpHandler sh = new HttpHandler();
            String jsonStr = sh.makeServiceCall(countryNameJSONUrl);

            if (jsonStr != null) {
                try {

                    JSONObject jsonObj = null;
                    jsonObj = new JSONObject(jsonStr);

                    Iterator key = jsonObj.keys();
                    while (key.hasNext()) {
                        String k = key.next().toString();
                        countryNameList.put(k, jsonObj.getString(k));
                        countryNameArray.add(jsonObj.getString(k));
                        Log.i("Info", "countryNameArray:");
                    }

                    Collections.sort(countryNameArray, new Comparator<String>() {
                        @Override
                        public int compare(String name1, String name2)
                        {
                            return  name1.compareTo(name2);
                        }
                    });

                } catch (final JSONException e) {

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "Json parsing error: " + e.toString(),
                                    Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>
                    (RegisterPageActivity.this, android.R.layout.simple_spinner_item, countryNameArray);
            spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            countrySpinner.setAdapter(spinnerArrayAdapter);
        }
    }

    private class GetPhoneCodeList extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... arg0) {

            HttpHandler sh = new HttpHandler();
            String jsonStr = sh.makeServiceCall(countryPhoneJSONUrl);

            if (jsonStr != null) {
                try {

                    JSONObject jsonObj = null;
                    jsonObj = new JSONObject(jsonStr);

                    Iterator key = jsonObj.keys();
                    while (key.hasNext()) {
                        String k = key.next().toString();
                        countryPhoneList.put(k, jsonObj.getString(k));
                    }

                } catch (final JSONException e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "Json parsing error: " + e.toString(),
                                    Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }

            return null;
        }
    }

}
