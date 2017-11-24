package uur.com.pinbook.Activities;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import uur.com.pinbook.R;

public class WelcomePageActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_page);

        // Hide the status bar.
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);

        // User authorization and verification controls.
        firebaseAuth = FirebaseAuth.getInstance();
        Intent intent = new Intent();

        if (firebaseAuth.getCurrentUser() != null) {

            FirebaseUser user = firebaseAuth.getCurrentUser();

            if (user.isEmailVerified()) {
                intent.setClass(getApplicationContext(), ProfilePageActivity.class);
            } else {
                intent.setClass(getApplicationContext(), LoginPageActivity.class);
            }
        } else {
            intent.setClass(getApplicationContext(), EnterPageActivity.class);
        }

        // Pass to the next page.
        passToNextActivity(intent);

    }

    private void passToNextActivity(final Intent intent) {

        Handler handler = new Handler();
        handler.postDelayed(new Runnable(){
            private Intent i = intent;
            @Override
            public void run(){
                // do something
                finish();
                startActivity(i);
            }
        }, 1000);


    }



}
