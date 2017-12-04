package uur.com.pinbook.Activities;

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
import com.google.firebase.auth.ActionCodeSettings;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import uur.com.pinbook.R;

public class EmailVerifyPageActivity extends AppCompatActivity implements View.OnClickListener{

    private TextView textVerifyAgain;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_verify_page);

        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();

        //initially verify  message should be sent
        sendVerificationMail();

        String userEmail = getIntent().getStringExtra("user_email");
        TextView textEmail = (TextView) findViewById(R.id.textEmail);
        String s = user.getEmail().toString();
        textEmail.setText(s);

        textVerifyAgain = (TextView) findViewById(R.id.textVerifyAgain);
        textVerifyAgain.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        if(v == textVerifyAgain){
            sendVerificationMail();
        }
    }


    private void sendVerificationMail(){

/*
        ActionCodeSettings actionCodeSettings = ActionCodeSettings.newBuilder()
                .setHandleCodeInApp(true)
                .setAndroidPackageName("com.example.asus.firebasedemo", true, null)
                .setUrl("fir-demov2-2b0c6.firebaseapp.com")
                .build();
*/

        final FirebaseUser final_user = firebaseAuth.getCurrentUser();

        ActionCodeSettings actionCodeSettings = ActionCodeSettings.newBuilder()
                .setAndroidPackageName("uur.com.pinbook", true, null)
                .setHandleCodeInApp(false)
                .setIOSBundleId(null)
                .setUrl("https://androidteam-f4c25.firebaseapp.com")
                .build();


        final_user.sendEmailVerification(actionCodeSettings).addOnCompleteListener(this, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(getApplicationContext(),
                            "Verification email sent to " + user.getEmail(),
                            Toast.LENGTH_SHORT).show();
                } else {
                    Log.i("hata : ", task.getException().toString());
                    Toast.makeText(getApplicationContext(),
                            "Failed to send verification email.",
                            Toast.LENGTH_SHORT).show();
                }
            }

        });


    /*
        user.sendEmailVerification().addOnCompleteListener(this, new OnCompleteListener() {

            @Override
            public void onComplete(@NonNull Task task) {

                if (task.isSuccessful()) {
                    Toast.makeText(getApplicationContext(),
                            "Verification email sent to " + user.getEmail(),
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(),
                            "Failed to send verification email.",
                            Toast.LENGTH_SHORT).show();
                }

            }
        });


    */

    }


}
