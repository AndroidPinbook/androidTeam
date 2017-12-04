package uur.com.pinbook.Activities;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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

        if (firebaseAuth.getCurrentUser() != null) {

            FirebaseUser user = firebaseAuth.getCurrentUser();
            user.reload();

            boolean a = user.isEmailVerified();
            boolean b = user.isEmailVerified();
            user.reload();
            boolean c = user.isEmailVerified();
            user.reload();
            boolean d = user.isEmailVerified();

            Log.i("Info","User check");
            Log.i("Info","User mail:" + user.getEmail());
            //if(a) Log.i("User verified", "yes");
            //else Log.i("User verified", "no");

            if (user.isEmailVerified()) {
                Log.i("Info","ProfilePageActivity starts");
                delay();
                finish();
                startActivity(new Intent(WelcomePageActivity.this, ProfilePageActivity.class));
            } else {
                Log.i("Info","LoginPageActivity starts");
                delay();
                finish();
                startActivity(new Intent(WelcomePageActivity.this, LoginPageActivity.class));
            }
        } else {
            Log.i("Info","EnterPageActivity starts");
            delay();
            finish();
            startActivity(new Intent(WelcomePageActivity.this, EnterPageActivity.class));
        }

    }

    private void delay() {

        Handler handler = new Handler();
        handler.postDelayed(new Runnable(){
            @Override
            public void run(){
                // do something
            }
        }, 1000);


    }



}
