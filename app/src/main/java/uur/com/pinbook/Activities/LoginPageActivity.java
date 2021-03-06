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
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
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
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;



import cn.refactor.lib.colordialog.ColorDialog;
import uur.com.pinbook.Adapters.CustomDialogAdapter;
import uur.com.pinbook.Adapters.ErrorMessageAdapter;
import uur.com.pinbook.Adapters.ValidationAdapter;
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
        if (view == buttonSignIn) {
            userLogin();
        }

        if (view == rememberMeCheckBox) {
            saveLoginInformation();
        }

        if (view == textViewFogetPassword) {
            startForgetPassFunc();
        }

        if (view == backGroundLayout) {
            saveLoginInformation();
            hideKeyBoard();
        }

    }
    public static void showCustomDialog2(final Context context, String errMessage){
        ColorDialog dialog = new ColorDialog(context);
        dialog.setTitle("operation");
        dialog.setAnimationEnable(true);
        dialog.setAnimationIn(getInAnimationTest(context));
        dialog.setAnimationOut(getOutAnimationTest(context));
        dialog.setContentImage((R.mipmap.ic_help));
        dialog.setPositiveListener("delete", new ColorDialog.OnPositiveListener() {
            @Override
            public void onClick(ColorDialog dialog) {
                Toast.makeText(context, dialog.getPositiveText().toString(), Toast.LENGTH_SHORT).show();
            }
        })
                .setNegativeListener("cancel", new ColorDialog.OnNegativeListener() {
                    @Override
                    public void onClick(ColorDialog dialog) {
                        Toast.makeText(context, dialog.getNegativeText().toString(), Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                }).show();
    }

    public static AnimationSet getInAnimationTest(Context context) {
        AnimationSet out = new AnimationSet(context, null);
        AlphaAnimation alpha = new AlphaAnimation(0.0f, 1.0f);
        alpha.setDuration(150);
        ScaleAnimation scale = new ScaleAnimation(0.6f, 1.0f, 0.6f, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        scale.setDuration(150);
        out.addAnimation(alpha);
        out.addAnimation(scale);
        return out;
    }

    public static AnimationSet getOutAnimationTest(Context context) {
        AnimationSet out = new AnimationSet(context, null);
        AlphaAnimation alpha = new AlphaAnimation(1.0f, 0.0f);
        alpha.setDuration(150);
        ScaleAnimation scale = new ScaleAnimation(1.0f, 0.6f, 1.0f, 0.6f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        scale.setDuration(150);
        out.addAnimation(alpha);
        out.addAnimation(scale);
        return out;
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
                                        CustomDialogAdapter.showDialogInfo(LoginPageActivity.this,
                                                "Şifre sıfırlama linki e-mail adresinize gönderildi.");

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

        if(!validateForm()){
            return;
        }

        progressDialog.setMessage("Registering User..");
        progressDialog.show();
        Log.i("email ",email);
        Log.i("password :", password);
        Log.i("firebaseAuth :", firebaseAuth.toString());
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
                                startActivity(new Intent(getApplicationContext(), PinThrowActivity.class));

                            }else{
                                Log.i("verified :", "no!");
                                finish();
                                startActivity(new Intent(getApplicationContext(), EmailVerifyPageActivity.class));
                            }
                            Log.i("sonuç :", "cikis..");
                        }else{

                            try {
                                throw task.getException();
                            } catch(FirebaseAuthInvalidCredentialsException e) {
                                CustomDialogAdapter.showDialogError(LoginPageActivity.this, ErrorMessageAdapter.INVALID_CREDENTIALS.getText());

                                //editTextEmail.setError("error_invalid_email");
                                //editTextEmail.requestFocus();
                                Log.i("error", e.toString());
                            } catch(FirebaseAuthInvalidUserException e){
                                CustomDialogAdapter.showDialogError(LoginPageActivity.this, ErrorMessageAdapter.INVALID_USER.getText());
                                Log.i("error ", e.toString());
                            }catch(Exception e) {
                                CustomDialogAdapter.showDialogError(LoginPageActivity.this, ErrorMessageAdapter.UNKNOW_ERROR.getText());
                                Log.i("error ", e.toString());
                            }

                        }
                    }
                });
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

    private boolean validateForm() {
        boolean valid = true;

        Log.i("Info", "validateForm");

        String email = editTextEmail.getText().toString();
        String password = editTextPassword.getText().toString();

        //Email - password check
        if(TextUtils.isEmpty(email) || TextUtils.isEmpty((password))){
            if (TextUtils.isEmpty(email)) {
                editTextEmail.setError("Required");
                valid = false;
            } else {
                if(!ValidationAdapter.isValidEmail(email)){
                    editTextEmail.setError("Email is not valid");
                    valid = false;

                }else{
                    editTextEmail.setError(null);
                }
            }

            if (TextUtils.isEmpty(password)) {
                editTextPassword.setError("Required");
                valid = false;
            } else {
                editTextPassword.setError(null);
            }

        }else{

            if(!ValidationAdapter.isValidEmail(email)){
                editTextEmail.setError("Email is not valid");
                valid = false;

            }else{
                editTextEmail.setError(null);
            }

            /*
            if(!ValidationAdapter.isValidPassword(email)){
                editTextPassword.setError("Password min length is 6");
                valid = false;
            }else{
                editTextPassword.setError(null);
            }
            */

        }

        return valid;

    }


}
