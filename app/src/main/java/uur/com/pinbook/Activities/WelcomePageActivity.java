package uur.com.pinbook.Activities;

import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.TwitterAuthCredential;
import com.google.firebase.auth.TwitterAuthProvider;
import com.twitter.sdk.android.core.AuthToken;
import com.twitter.sdk.android.core.AuthTokenAdapter;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterAuthToken;
import com.twitter.sdk.android.core.TwitterConfig;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterAuthClient;

import uur.com.pinbook.R;

public class WelcomePageActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Configure Twitter SDK
        TwitterAuthConfig authConfig = new TwitterAuthConfig(
                getString(R.string.twitter_consumer_key),
                getString(R.string.twitter_consumer_secret));

        TwitterConfig twitterConfig = new TwitterConfig.Builder(this)
                .twitterAuthConfig(authConfig)
                .build();

        Twitter.initialize(twitterConfig);

        setContentView(R.layout.activity_welcome_page);

        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();

        Log.i("Info", "WelcomePageActivity========================================================");

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
        }, 1500);

    }

    protected void performNextPage() {

        if (firebaseAuth.getCurrentUser() != null) {


            firebaseAuth.getCurrentUser().reload().addOnCompleteListener(this, new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {

                    waitForReloadCheck();
                }
            });

        } else {
            finish();
            startActivity(new Intent(WelcomePageActivity.this, EnterPageActivity.class));
        }
    }

    public void waitForReloadCheck(){

        if (isFacebookLoggedIn()) {

            Log.i("Info", "isFacebookLoggedIn - ProfilePageActivity starts");
            finish();
            startActivity(new Intent(WelcomePageActivity.this, ProfilePageActivity.class));

        } else if (isTwitterLoggedIn()) {

            Log.i("Info", "isTwitterLoggedIn - ProfilePageActivity starts");
            finish();
            startActivity(new Intent(WelcomePageActivity.this, ProfilePageActivity.class));

        } else if (user.isEmailVerified()) {

            Log.i("Info", "ProfilePageActivity starts");
            finish();
            startActivity(new Intent(WelcomePageActivity.this, ProfilePageActivity.class));

        } else {

            Log.i("Info", "EnterPageActivity starts");
            finish();
            startActivity(new Intent(WelcomePageActivity.this, EnterPageActivity.class));
        }
    }

    private boolean isFacebookLoggedIn() {

        AccessToken accessToken = AccessToken.getCurrentAccessToken();

        if (accessToken != null)
            return true;
        else
            return false;
    }

    private boolean isTwitterLoggedIn() {

        TwitterSession session = TwitterCore.getInstance().getSessionManager().getActiveSession();

        if (session != null)
            return true;
        else
            return false;
    }
}
