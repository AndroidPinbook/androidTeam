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

    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_page);

        // Hide the status bar.
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);

        //Perform next page..
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //do your work here after 60 second
                performNextPage();
            }
        },1500);

    }

    protected void performNextPage(){

        // User authorization and verification controls.
        firebaseAuth = FirebaseAuth.getInstance();

        if (firebaseAuth.getCurrentUser() != null) {

            FirebaseUser user = firebaseAuth.getCurrentUser();
            user.reload();

            Log.i("Info","User check");
            Log.i("Info","User mail:" + firebaseAuth.getCurrentUser().getEmail());
            //if(a) Log.i("User verified", "yes");
            //else Log.i("User verified", "no");

            if (user.isEmailVerified()) {
                Log.i("Info","ProfilePageActivity starts");
                finish();
                startActivity(new Intent(WelcomePageActivity.this, ProfilePageActivity.class));
            } else {
                Log.i("Info","LoginPageActivity starts");
                finish();
                startActivity(new Intent(WelcomePageActivity.this, LoginPageActivity.class));
            }
        } else {
            Log.i("Info","EnterPageActivity starts");
            finish();
            startActivity(new Intent(WelcomePageActivity.this, EnterPageActivity.class));
        }

    }


}
