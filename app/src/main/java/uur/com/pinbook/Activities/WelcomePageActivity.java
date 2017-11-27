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
                finish();
                startActivity(new Intent(getApplicationContext(), ProfilePageActivity.class));
            } else {
                Log.i("Info","LoginPageActivity starts");
                finish();
                startActivity(new Intent(getApplicationContext(), LoginPageActivity.class));

            }
        } else {
            Log.i("Info","EnterPageActivity starts");
            finish();
            startActivity(new Intent(getApplicationContext(), EnterPageActivity.class));
        }

        // Pass to the next page.
        //passToNextActivity(intent);

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
