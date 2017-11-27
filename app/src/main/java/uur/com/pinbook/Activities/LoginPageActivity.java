package uur.com.pinbook.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import uur.com.pinbook.R;

public class LoginPageActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText editTextEmail;
    private EditText editTextPassword;
    private Button buttonSignIn;
    private TextView textViewFogetPassword;
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);

        editTextEmail = (EditText) findViewById(R.id.editTextEmail);
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);
        setTextViewDrawableColor(editTextEmail, R.color.background);
        setTextViewDrawableColor(editTextPassword, R.color.background);


        buttonSignIn = (Button) findViewById(R.id.buttonSignIn);
        textViewFogetPassword = (TextView) findViewById(R.id.textViewForgetPassword);
        progressDialog = new ProgressDialog(this);

        buttonSignIn.setOnClickListener(this);
        textViewFogetPassword.setOnClickListener(this);

        firebaseAuth = FirebaseAuth.getInstance();
        if(firebaseAuth.getCurrentUser() != null){
            firebaseAuth.signOut();
        }

    }

    public void onClick(View view) {
        if(view == buttonSignIn){
            userLogin();
        }

        if(view == textViewFogetPassword){
            // Go to reset password..
        }
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

                        }

                    }
                });

    }

    public void displayResult(){
        Toast.makeText(this, "Please verify your email..", Toast.LENGTH_SHORT).show();
    }





}
