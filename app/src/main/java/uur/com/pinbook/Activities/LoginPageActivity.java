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
import android.text.Layout;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
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
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;

import org.w3c.dom.Text;

import cn.refactor.lib.colordialog.ColorDialog;
import cn.refactor.lib.colordialog.PromptDialog;
import okhttp3.Response;
import uur.com.pinbook.Controller.CustomDialogAdapter;
import uur.com.pinbook.Controller.ErrorMessageAdapter;
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

    private Button btn_showPromptDlg;
    private Button btn_showTextDialog;
    private Button btn_showPicDialog;
    private Button btn_showAllModeDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);

        CustomDialogAdapter.showErrorDialog(LoginPageActivity.this, ErrorMessageAdapter.PASSWORD_EMPTY.getText() );

        backGroundLayout = (RelativeLayout) findViewById(R.id.layoutLogIn);
        inputLayout = (LinearLayout) findViewById(R.id.inputLayout);
        rememberMeCheckBox = findViewById(R.id.rememberMeCb);


        editTextEmail = (EditText) findViewById(R.id.editTextEmail);
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);
        setTextViewDrawableColor(editTextEmail, R.color.button_color);
        setTextViewDrawableColor(editTextPassword, R.color.button_color);


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
            inputLayout.clearFocus();
            hideKeyBoard();
        }


        if(view == btn_showPromptDlg){
            new PromptDialog(this)
                    .setDialogType(PromptDialog.DIALOG_TYPE_SUCCESS)
                    .setAnimationEnable(true)
                    .setTitleText("Başarılı")
                    .setContentText("text_data")
                    .setPositiveListener("ok", new PromptDialog.OnPositiveListener() {
                        @Override
                        public void onClick(PromptDialog dialog) {
                            dialog.dismiss();
                        }
                    }).show();
        }

        if(view == btn_showPicDialog){
            ColorDialog dialog = new ColorDialog(this);
            dialog.setTitle("operation");
            dialog.setAnimationEnable(true);
            dialog.setAnimationIn(getInAnimationTest(this));
            dialog.setAnimationOut(getOutAnimationTest(this));
            dialog.setContentImage(getResources().getDrawable(R.mipmap.ic_help));
            dialog.setPositiveListener("delete", new ColorDialog.OnPositiveListener() {
                @Override
                public void onClick(ColorDialog dialog) {
                    Toast.makeText(LoginPageActivity.this, dialog.getPositiveText().toString(), Toast.LENGTH_SHORT).show();
                }
            })
                    .setNegativeListener("cancel", new ColorDialog.OnNegativeListener() {
                        @Override
                        public void onClick(ColorDialog dialog) {
                            Toast.makeText(LoginPageActivity.this, dialog.getNegativeText().toString(), Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }
                    }).show();
        }

        if(view == btn_showTextDialog){
            ColorDialog dialog = new ColorDialog(this);
            dialog.setColor("#8ECB54");
            dialog.setAnimationEnable(true);
            dialog.setTitle("operation");
            dialog.setContentText("content_text");
            dialog.setPositiveListener("text_know", new ColorDialog.OnPositiveListener() {
                @Override
                public void onClick(ColorDialog dialog) {
                    Toast.makeText(LoginPageActivity.this, dialog.getPositiveText().toString(), Toast.LENGTH_SHORT).show();
                }
            }).show();
        }

        if(view == btn_showAllModeDialog){
            ColorDialog dialog = new ColorDialog(this);
            dialog.setTitle("operation");
            dialog.setAnimationEnable(true);
                dialog.setContentText("content_text");
            dialog.setContentImage(getResources().getDrawable(R.mipmap.ic_help));
            dialog.setPositiveListener("delete", new ColorDialog.OnPositiveListener() {
                @Override
                public void onClick(ColorDialog dialog) {
                    Toast.makeText(LoginPageActivity.this, dialog.getPositiveText().toString(), Toast.LENGTH_SHORT).show();
                }
            })
                    .setNegativeListener("cancel", new ColorDialog.OnNegativeListener() {
                        @Override
                        public void onClick(ColorDialog dialog) {
                            Toast.makeText(LoginPageActivity.this, dialog.getNegativeText().toString(), Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }
                    }).show();
        }

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

                                    }
                                }
                            });


                    dialog.dismiss();
                }else{
                    /*Toast.makeText(LoginPageActivity.this,
                            "Please enter your email address",
                            Toast.LENGTH_SHORT).show();*/
                    String s = "Please enter your email address";
                    CustomDialogAdapter.showErrorDialog(LoginPageActivity.this, s);
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

        Log.i("info:", "userLogin'deyiz..");
        String email = editTextEmail.getText().toString().trim();
        Log.i("info:", "email_ok");
        String password = editTextPassword.getText().toString().trim();
        Log.i("info:", "password_ok");

        if(TextUtils.isEmpty(email)){
            Toast.makeText(this,"Please enter your email", Toast.LENGTH_SHORT).show();
            return;
        }
        Log.i("info:", "email_ok");
        if(TextUtils.isEmpty(password)){
            Toast.makeText(this,"Please enter your password", Toast.LENGTH_SHORT).show();
            return;
        }
        Log.i("info:", "password_ok");

        //if validations ok
        progressDialog.setMessage("Registering User..");
        progressDialog.show();

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

                                startActivity(new Intent(getApplicationContext(), ProfilePageActivity.class));


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
                            //
                            Log.i("info:", "sign in fail..");

                            try {
                                throw task.getException();
                            } catch(FirebaseAuthWeakPasswordException e) {
                                editTextPassword.setError("weak_password");
                                editTextPassword.requestFocus();
                                Log.i("error ", e.toString());
                            } catch(FirebaseAuthInvalidCredentialsException e) {
                                editTextEmail.setError("error_invalid_email");
                                editTextEmail.requestFocus();
                                Log.i("error ", e.toString());
                            } catch(FirebaseAuthUserCollisionException e) {
                                editTextEmail.setError("error_user_exists");
                                editTextEmail.requestFocus();
                                Log.i("error ", e.toString());
                            } catch(Exception e) {
                                Log.i("error :", e.getMessage());
                            }

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
    public void onBackPressed() {

        Intent intent = new Intent(getApplicationContext(), EnterPageActivity.class);
        startActivity(intent);
    }
}
