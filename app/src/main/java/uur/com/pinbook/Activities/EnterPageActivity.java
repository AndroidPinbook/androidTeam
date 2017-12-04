package uur.com.pinbook.Activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.TwitterAuthProvider;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterConfig;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;

import org.json.JSONObject;

import java.util.Arrays;

import uur.com.pinbook.Controller.CustomPagerAdapter;
import uur.com.pinbook.Controller.EnterPageDataModel;
import uur.com.pinbook.R;

public class EnterPageActivity extends AppCompatActivity implements View.OnClickListener{

    private LinearLayout dotsLayout;
    private TextView[] dots;

    CustomPagerAdapter adapter;
    private CallbackManager mCallbackManager;

    private FirebaseAuth mAuth;

    private TwitterLoginButton mLoginButton;

    private Button registerButton;
    private Button loginBtn;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Making notification bar transparent
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }

        FacebookSdk.sdkInitialize(getApplicationContext());

        // Configure Twitter SDK
        TwitterAuthConfig authConfig =  new TwitterAuthConfig(
                getString(R.string.twitter_consumer_key),
                getString(R.string.twitter_consumer_secret));

        TwitterConfig twitterConfig = new TwitterConfig.Builder(this)
                .twitterAuthConfig(authConfig)
                .build();

        Twitter.initialize(twitterConfig);

        setContentView(R.layout.activity_enter_page);

        try {

            mAuth = FirebaseAuth.getInstance();

            ViewPager enterViewPager = (ViewPager) findViewById(R.id.enterViewPager);
            dotsLayout = (LinearLayout) findViewById(R.id.layoutDots);

            // [START initialize_twitter_login]
            mLoginButton = findViewById(R.id.twitterLoginButton);

            mLoginButton.setCallback(new Callback<TwitterSession>() {
                @Override
                public void success(Result<TwitterSession> result) {

                    Log.i("Info","twitterLogin:success" + result);

                    handleTwitterSession(result.data);
                }

                @Override
                public void failure(TwitterException exception) {

                    Log.i("Info","twitterLogin:failure:" + exception);
                }
            });

            // Initialize Facebook Login button
            mCallbackManager = CallbackManager.Factory.create();

            LoginButton loginButton = findViewById(R.id.facebookLoginButton);

            loginButton.setReadPermissions(Arrays.asList(
                    "public_profile", "email", "user_birthday", "user_friends"));

            //loginButton.setReadPermissions("email", "public_profile");

            loginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
                @Override
                public void onSuccess(LoginResult loginResult) {

                    Log.i("Info", "facebook:onSucces:" + loginResult);

                    GraphRequest graphRequest = GraphRequest.newMeRequest(loginResult.getAccessToken(),
                            new GraphRequest.GraphJSONObjectCallback() {
                                @Override
                                public void onCompleted(JSONObject object, GraphResponse response) {

                                }
                            });

                    handleFacebookAccessToken(loginResult.getAccessToken());
                }

                @Override
                public void onCancel() {
                    Log.i("Info", "facebook:onCancel");
                }

                @Override
                public void onError(FacebookException error) {

                    Log.i("Info", "facebook:onError:" + error);
                }
            });

            loginBtn = findViewById(R.id.logInButton);
            registerButton = findViewById(R.id.registerButton);

            findViewById(R.id.registerButton).setOnClickListener(this);
            findViewById(R.id.logInButton).setOnClickListener(this);


            adapter = new CustomPagerAdapter(this, EnterPageDataModel.getDataList());

            enterViewPager.setAdapter(adapter);

            addBottomDots(0);

            enterViewPager.addOnPageChangeListener(viewPagerPageChangeListener);
        }catch (Exception e){
            Log.i("Info", "On create error:" + e.toString());
        }
    }


    private void addBottomDots(int currentPage) {
        dots = new TextView[adapter.getCount()];

        int cActive = getResources().getColor(R.color.dot_dark_screen3, null);
        int cInactive = getResources().getColor(R.color.dot_light_screen3, null);

        dotsLayout.removeAllViews();

        for (int i = 0; i < dots.length; i++) {
            dots[i] = new TextView(this);
            dots[i].setText(Html.fromHtml("&#8226;"));
            dots[i].setTextSize(35);
            dots[i].setTextColor(cInactive);
            dotsLayout.addView(dots[i]);
        }

        if (dots.length > 0)
            dots[currentPage].setTextColor(cActive);

    }

    //  viewpager change listener
    ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageSelected(int position) {
            addBottomDots(position);

            // changing the next button text 'NEXT' / 'GOT IT'
            if (position == adapter.getCount() - 1) {
                // last page. make button text to GOT IT

            } else {
                // still pages are left

            }
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        @Override
        public void onPageScrollStateChanged(int arg0) {

        }
    };

    private void handleFacebookAccessToken(AccessToken token) {

        Log.i("Info","handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());

        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information

                            Log.i("Info","signInWithCredential:success" );

                            startProfilePage();

                        } else {
                            // If sign in fails, display a message to the user.

                            Log.i("Info","signInWithCredential:failure:" + task.getException());

                            Toast.makeText(EnterPageActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void handleTwitterSession(TwitterSession session) {

        Log.i("Info","handleTwitterSession:" + session);

        AuthCredential credential = TwitterAuthProvider.getCredential(
                session.getAuthToken().token,
                session.getAuthToken().secret);

        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information

                            Log.i("Info","signInWithCredential:success");

                            startProfilePage();

                        } else {
                            // If sign in fails, display a message to the user.

                            Log.i("Info","signInWithCredential:failure:" + task.getException());

                            Toast.makeText(EnterPageActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        try {

            // Pass the activity result to the Twitter login button.
            mLoginButton.onActivityResult(requestCode, resultCode, data);

            // Pass the activity result back to the Facebook SDK
            mCallbackManager.onActivityResult(requestCode, resultCode, data);
        }catch (Exception e){
            Log.i("Info", "onActivityResult error:" + e.toString());
        }
    }

    @Override
    public void onClick(View v) {

        int i = v.getId();

        Intent intent;

        switch (i){
            case R.id.registerButton:

                intent = new Intent(getApplicationContext(), RegisterPageActivity.class);
                startActivity(intent);
                break;

            case R.id.logInButton:
                intent = new Intent(getApplicationContext(), LoginPageActivity.class);
                startActivity(intent);
                break;

            default:
                Toast.makeText(this, "Error occured!!", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    public void startProfilePage(){

        Intent intent = new Intent(getApplicationContext(), ProfilePageActivity.class);
        startActivity(intent);
    }
}
