package uur.com.pinbook.Activities;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.facebook.AccessToken;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.TwitterAuthCredential;
import com.google.firebase.auth.TwitterAuthProvider;
import com.twitter.sdk.android.core.AuthToken;
import com.twitter.sdk.android.core.AuthTokenAdapter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterAuthToken;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterAuthClient;

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


            if(isFacebookLoggedIn()){

                Log.i("Info","isFacebookLoggedIn - ProfilePageActivity starts");
                delay();
                finish();
                startActivity(new Intent(WelcomePageActivity.this, ProfilePageActivity.class));
            }

            if(isTwitterLoggedIn()) {

                Log.i("Info","isTwitterLoggedIn - ProfilePageActivity starts");
                delay();
                finish();
                startActivity(new Intent(WelcomePageActivity.this, ProfilePageActivity.class));
            }

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

    private boolean isFacebookLoggedIn() {

        AccessToken accessToken = AccessToken.getCurrentAccessToken();

        if(accessToken != null)
            return true;
        else
            return false;
    }

    private boolean isTwitterLoggedIn(){

        TwitterSession session = TwitterCore.getInstance().getSessionManager().getActiveSession();
        TwitterAuthToken authToken = session.getAuthToken();

        if(authToken != null)
            return true;
        else
            return false;
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
