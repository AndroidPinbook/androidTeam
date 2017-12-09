package uur.com.pinbook.Activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.ActionCodeSettings;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import uur.com.pinbook.Controller.CustomDialogAdapter;
import uur.com.pinbook.Controller.ValidationAdapter;
import uur.com.pinbook.R;

public class LoginPageActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText editTextEmail;
    private EditText editTextPassword;
    private Button buttonSignIn;
    private TextView textViewFogetPassword;
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;

    RelativeLayout backGroundLayout;
    LinearLayout inputLayout;

    private CheckBox rememberMeCheckBox;
    private SharedPreferences loginPreferences;
    private SharedPreferences.Editor loginPrefsEditor;
    private Boolean saveLogin;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);



        backGroundLayout = (RelativeLayout) findViewById(R.id.layoutLogIn);
        inputLayout = (LinearLayout) findViewById(R.id.inputLayout);
        rememberMeCheckBox = findViewById(R.id.rememberMeCb);

        editTextEmail = (EditText) findViewById(R.id.editTextEmail);
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);

        buttonSignIn = (Button) findViewById(R.id.buttonSignIn);
        textViewFogetPassword = (TextView) findViewById(R.id.textViewForgetPassword);
        textViewFogetPassword.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
        progressDialog = new ProgressDialog(this);

        buttonSignIn.setOnClickListener(this);
        textViewFogetPassword.setOnClickListener(this);
        backGroundLayout.setOnClickListener(this);
        rememberMeCheckBox.setOnClickListener(this);

        loginPreferences = getSharedPreferences("loginPrefs", MODE_PRIVATE);
        loginPrefsEditor = loginPreferences.edit();

        saveLogin = loginPreferences.getBoolean("saveLogin", false);
        if (saveLogin == true) {
            editTextEmail.setText(loginPreferences.getString("email", editTextEmail.getText().toString()));
            editTextPassword.setText(loginPreferences.getString("password", editTextPassword.getText().toString()));
            rememberMeCheckBox.setChecked(true);
        }

        firebaseAuth = FirebaseAuth.getInstance();
        if(firebaseAuth.getCurrentUser() != null){
            firebaseAuth.signOut();
        }
    }

    public void onClick(View view) {
        if(view == buttonSignIn){
            userLogin();
        }

        if (view == rememberMeCheckBox){
            saveLoginInformation();
        }

        if(view == textViewFogetPassword){
            startForgetPassFunc();
        }

        if(view == backGroundLayout){
            saveLoginInformation();
            hideKeyBoard();
        }
    }

    private void startForgetPassFunc() {

        // Go to reset password..
        Log.i("İnfo : ", "forget password clicked");
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(LoginPageActivity.this);
        View mView = getLayoutInflater().inflate(R.layout.layout_forget_password, null);

        final EditText mEmail = (EditText) mView.findViewById(R.id.etEmail);

        Button mReset = (Button) mView.findViewById(R.id.buttonReset);

        mEmail.setText("");
        mEmail.append(editTextEmail.getText().toString());

        mBuilder.setView(mView);
        final AlertDialog dialog = mBuilder.create();
        dialog.show();

        mReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!mEmail.getText().toString().isEmpty()){

                    FirebaseAuth auth = FirebaseAuth.getInstance();
                    String emailAddress = mEmail.getText().toString();
/*
                        auth.sendPasswordResetEmail(emailAddress)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Log.i("i :", "Email sent.");
                                        }
                                    }
                                });
*/
                    ActionCodeSettings actionCodeSettings = ActionCodeSettings.newBuilder()
                            .setAndroidPackageName("uur.com.pinbook", true, null)
                            .setHandleCodeInApp(false)
                            .setIOSBundleId(null)
                            .setUrl("https://androidteam-f4c25.firebaseapp.com")
                            .build();

                    auth.sendPasswordResetEmail(emailAddress, actionCodeSettings)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Log.i("reset Email status :", "Email sent.");

                                    }
                                }
                            });


                    dialog.dismiss();
                }else{
                    Toast.makeText(LoginPageActivity.this,
                            "Please enter your email address",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void setTextViewDrawableColor(EditText textView, int color) {
        for (Drawable drawable : textView.getCompoundDrawables()) {
            if (drawable != null) {
                drawable.setColorFilter(new PorterDuffColorFilter(getColor(color), PorterDuff.Mode.SRC_IN));
            }
        }
    }

    private void userLogin(){

        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();


        if(TextUtils.isEmpty(email)){
            editTextEmail.setError("Required.");
            return;
        }

        if(TextUtils.isEmpty(password)){
            editTextPassword.setError("Required.");
            return;
        }

        if(!ValidationAdapter.isValidEmail(email)){
            CustomDialogAdapter.showErrorDialog(LoginPageActivity.this, "Email is not valid!");
            return;
        }

        progressDialog.setMessage("Registering User..");
        progressDialog.show();
        Intent intent = new Intent();

        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {

                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressDialog.dismiss();
                        Log.i("info:", "Sign in step..");
                        if(task.isSuccessful()){
                            Log.i("info:", "signIn successfull..");

                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            user.reload();

                            Log.i("verified :", user.getEmail().toString());

                            if(user.isEmailVerified()){
                                Log.i("verified :", "yes");
                                finish();
//                                Intent intent = new Intent(getBaseContext(), ProfilePageActivity.class);
//                                intent.putExtra("user_email", user.getEmail().toString());
//                                startActivity(intent);

                                startActivity(new Intent(getApplicationContext(), PinThrowActivity.class));


                            }else{
                                Log.i("verified :", "no!");
                                displayResult();
                                finish();
//                                Intent intent = new Intent(getBaseContext(), EmailVerifyPageActivity.class);
//                                intent.putExtra("user_email", user.getEmail().toString());
//                                startActivity(intent);

                                startActivity(new Intent(getApplicationContext(), EmailVerifyPageActivity.class));
                            }
                            Log.i("sonuç :", "cikis..");
                        }else{
                            CustomDialogAdapter.showErrorDialog(LoginPageActivity.this, "Email or password is incorrect!");

                        }
                    }
                });
    }

    public void displayResult(){
        Toast.makeText(this, "Please verify your email..", Toast.LENGTH_SHORT).show();
    }

    public void hideKeyBoard(){

        Log.i("Info", "hideKeyBoard");

        InputMethodManager inputMethodManager =(InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
    }


    private void saveLoginInformation() {

        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editTextEmail.getWindowToken(), 0);

        String username = editTextEmail.getText().toString();
        String password = editTextPassword.getText().toString();

        if (rememberMeCheckBox.isChecked()) {
            loginPrefsEditor.putBoolean("saveLogin", true);
            loginPrefsEditor.putString("email", username);
            loginPrefsEditor.putString("password", password);
            loginPrefsEditor.commit();
        } else {
            loginPrefsEditor.clear();
            loginPrefsEditor.commit();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK){

            saveLoginInformation();
            Intent intent = new Intent(getApplicationContext(), EnterPageActivity.class);
            startActivity(intent);

        }else if(keyCode == KeyEvent.KEYCODE_HOME){

            saveLoginInformation();
        }

        return super.onKeyDown(keyCode, event);
    }
}
