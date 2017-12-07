package uur.com.pinbook.Activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
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
import android.os.StrictMode;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.TwitterAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.Session;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterConfig;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import retrofit2.http.GET;
import retrofit2.http.Query;
import twitter4j.AccountSettings;
import twitter4j.Category;
import twitter4j.DirectMessage;
import twitter4j.Friendship;
import twitter4j.GeoLocation;
import twitter4j.GeoQuery;
import twitter4j.IDs;
import twitter4j.Location;
import twitter4j.OEmbed;
import twitter4j.OEmbedRequest;
import twitter4j.PagableResponseList;
import twitter4j.Paging;
import twitter4j.Place;
import twitter4j.QueryResult;
import twitter4j.RateLimitStatus;
import twitter4j.RateLimitStatusEvent;
import twitter4j.RateLimitStatusListener;
import twitter4j.Relationship;
import twitter4j.ResponseList;
import twitter4j.SavedSearch;
import twitter4j.Status;
import twitter4j.StatusUpdate;
import twitter4j.Trends;
import twitter4j.TwitterAPIConfiguration;
import twitter4j.UploadedMedia;
import twitter4j.UserList;
import twitter4j.api.DirectMessagesResources;
import twitter4j.api.FavoritesResources;
import twitter4j.api.FriendsFollowersResources;
import twitter4j.api.HelpResources;
import twitter4j.api.ListsResources;
import twitter4j.api.PlacesGeoResources;
import twitter4j.api.SavedSearchesResources;
import twitter4j.api.SearchResource;
import twitter4j.api.SpamReportingResource;
import twitter4j.api.SuggestedUsersResources;
import twitter4j.api.TimelinesResources;
import twitter4j.api.TrendsResources;
import twitter4j.api.TweetsResources;
import twitter4j.api.UsersResources;
import twitter4j.auth.Authorization;
import twitter4j.auth.OAuth2Token;
import twitter4j.auth.RequestToken;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;
import twitter4j.util.function.Consumer;
import uur.com.pinbook.Controller.BitmapConversion;
import uur.com.pinbook.Controller.CustomPagerAdapter;
import uur.com.pinbook.Controller.EnterPageDataModel;
import uur.com.pinbook.Controller.FirebaseUserAdapter;
import uur.com.pinbook.JavaFiles.User;
import uur.com.pinbook.R;

//import oauth.signpost.OAuthProvider;
//import oauth.signpost.basic.DefaultOAuthProvider;
//import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
//import oauth.signpost.exception.OAuthCommunicationException;
//import oauth.signpost.exception.OAuthExpectationFailedException;
//import oauth.signpost.exception.OAuthMessageSignerException;
//import oauth.signpost.exception.OAuthNotAuthorizedException;
import twitter4j.TwitterFactory;
//import twitter4j.http.AccessToken;

public class EnterPageActivity extends AppCompatActivity implements View.OnClickListener {

    private LinearLayout dotsLayout;
    private TextView[] dots;

    CustomPagerAdapter adapter;
    private CallbackManager mCallbackManager;

    private FirebaseAuth mAuth;

    private TwitterLoginButton mLoginButton;
    private boolean fbLogin = false;
    private boolean twLogin = false;

    private Button registerButton;
    private Button loginBtn;

    private InputStream profileImageStream;
    private StorageReference riversRef;
    private StorageReference mStorageRef;


    private boolean userIsDetected = false;
    private String FBuserId;

    public User user;

    private Bitmap photo = null;
    private Uri downloadUrl = null;

    private static Twitter twitter;
    private static RequestToken requestToken;
    private static AccessToken accessToken;

    private AccessToken mAccessToken;

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
        TwitterAuthConfig authConfig = new TwitterAuthConfig(
                getString(R.string.twitter_consumer_key),
                getString(R.string.twitter_consumer_secret));

        TwitterConfig twitterConfig = new TwitterConfig.Builder(this)
                .twitterAuthConfig(authConfig)
                .build();

        Twitter.initialize(twitterConfig);

        setContentView(R.layout.activity_enter_page);

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        user = new User();
        mStorageRef = FirebaseStorage.getInstance().getReference();

        Log.i("Info", "EnterPageActivity========================================================");

        try {

            mAuth = FirebaseAuth.getInstance();

            FirebaseUser fbUser = mAuth.getCurrentUser();

            if(fbUser == null)
                Log.i("Info", "  >>fbUser is NULL");
            else
                Log.i("Info", "  >>fbUser is NOT NULL");

            ViewPager enterViewPager = (ViewPager) findViewById(R.id.enterViewPager);
            dotsLayout = (LinearLayout) findViewById(R.id.layoutDots);

            // [START initialize_twitter_login]
            mLoginButton = findViewById(R.id.twitterLoginButton);

            mLoginButton.setCallback(new Callback<TwitterSession>() {
                @Override
                public void success(Result<TwitterSession> result) {

                    Log.i("Info", "twitterLogin:success" + result);

                    handleTwitterSession(result.data);
                }

                @Override
                public void failure(TwitterException exception) {

                    Log.i("Info", "twitterLogin:failure:" + exception);
                }
            });

            // Initialize Facebook Login button
            mCallbackManager = CallbackManager.Factory.create();

            LoginButton loginButton = findViewById(R.id.facebookLoginButton);

            loginButton.setReadPermissions(Arrays.asList(
                    "public_profile",
                    "email",
                    "user_birthday",
                    "user_friends"));

            loginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
                @Override
                public void onSuccess(LoginResult loginResult) {

                    Log.i("Info", "facebook:onSucces:" + loginResult);

                    getFacebookuserInfo(loginResult);

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
        } catch (Exception e) {
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

        Log.i("Info", "handleFacebookAccessToken starts:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());

        try {
            mAuth.signInWithCredential(credential)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information

                                Log.i("Info", "  >>signInWithCredential:success");

                                fbLogin = true;

                                FirebaseUser currentUser = mAuth.getCurrentUser();
                                user.setUserId(currentUser.getUid());

                                //userIsDetected = FirebaseUserAdapter.emailIsRegistered(currentUser.getEmail());

                                Log.i("Info", "  >>userIsDetected:" + userIsDetected);

                                if (user.getProfilePicSrc() != null) {

                                    try {
                                        DownloadTask taskManager = new DownloadTask();
                                        taskManager.execute(user.getProfilePicSrc()).get();
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    } catch (ExecutionException e) {
                                        e.printStackTrace();
                                    }
                                }

                                saveProfPicViaSocialApp();
                                FirebaseUserAdapter.saveUserInfo(user);

                                startNextPage();

                            } else {
                                // If sign in fails, display a message to the user.

                                Log.i("Info", "  >>signInWithCredential:failure:" + task.getException());

                                Toast.makeText(EnterPageActivity.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        } catch (Exception e) {
            Log.i("Info", "  >>handleFacebookAccessToken error:" + e.toString());
        }
    }

    public void getFacebookuserInfo(LoginResult loginResult) {

        GraphRequest graphRequest = GraphRequest.newMeRequest(loginResult.getAccessToken(),
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {

                        Log.i("Info", "Facebook response:" + response.toString());

                        try {

                            user.setEmail(object.getString("email"));
                            user.setBirthdate(object.getString("birthday"));
                            user.setGender(object.getString("gender"));
                            user.setUsername(" ");
                            user.setPhoneNum(" ");

                            String[] elements = object.getString("name").split(" ");
                            user.setName(elements[0]);

                            String[] lastname = Arrays.copyOfRange(elements, 1, elements.length);

                            StringBuilder builder = new StringBuilder();
                            for (String s : lastname) {
                                builder.append(s);
                                builder.append(" ");
                            }
                            String surname = builder.toString().trim();
                            user.setSurname(surname);

                            String fbUserId = object.getString("id");
                            setFacebookProfilePicture(fbUserId);

                            Log.i("FBLogin", "  >>email     :" + user.getEmail());
                            Log.i("FBLogin", "  >>birthday  :" + user.getBirthdate());
                            Log.i("FBLogin", "  >>gender    :" + user.getGender());
                            Log.i("FBLogin", "  >>name      :" + user.getName());
                            Log.i("FBLogin", "  >>surname   :" + user.getSurname());

                        } catch (JSONException e) {
                            Log.i("Info", "  >>JSONException error:" + e.toString());
                        } catch (Exception e) {
                            Log.i("Info", "  >>Profile error:" + e.toString());
                        }
                    }
                });

        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,email,gender,birthday");
        graphRequest.setParameters(parameters);
        graphRequest.executeAsync();
    }


    public void setFacebookProfilePicture(String userID) {

        try {
            String url = "https://graph.facebook.com/" + userID + "/picture?type=large";
            user.setProfilePicSrc(url);

        } catch (Exception e) {

            Log.i("Info", "  >>setFacebookProfilePicture error:" + e.toString());
        }
    }

    private void handleTwitterSession(final TwitterSession session) {

        Log.i("Info", "handleTwitterSession:" + session);


        AuthCredential credential = TwitterAuthProvider.getCredential(
                session.getAuthToken().token,
                session.getAuthToken().secret);

        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information

                            Log.i("Info", "signInWithCredential:success");

                            twLogin = true;

                            String username = session.getUserName();
                            //new RetrieveFeedTask().execute(username);

                            saveTwitterInfo(username);

                            FirebaseUser currentUser = mAuth.getCurrentUser();
                            user.setUserId(currentUser.getUid());

                            Log.i("Info", "  >>userIsDetected:" + userIsDetected);

                            if (user.getProfilePicSrc() != null) {

                                try {
                                    DownloadTask taskManager = new DownloadTask();
                                    taskManager.execute(user.getProfilePicSrc()).get();
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                } catch (ExecutionException e) {
                                    e.printStackTrace();
                                }
                            }

                            saveProfPicViaSocialApp();
                            FirebaseUserAdapter.saveUserInfo(user);

                            startNextPage();

                        } else {
                            Log.i("Info", "  >>signInWithCredential:failure:" + task.getException());

                            Toast.makeText(EnterPageActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


    public void saveTwitterInfo(String username){

        try {

            Log.i("Info", "RetrieveFeedTask starts_xxxxx");

            try {
                Log.i("Info", "RetrieveFeedTask starts2");
                ConfigurationBuilder cb = new ConfigurationBuilder();
                cb.setOAuthConsumerKey(getResources().getString(R.string.twitter_consumer_key));
                cb.setOAuthConsumerSecret(getResources().getString(R.string.twitter_consumer_secret));
                cb.setOAuthAccessToken(getResources().getString(R.string.twitter_token));
                cb.setOAuthAccessTokenSecret(getResources().getString(R.string.twitter_token_secret));
                twitter4j.Twitter twitter = new TwitterFactory(cb.build()).getInstance();
                Log.i("Info", "RetrieveFeedTask starts3");

                twitter4j.User twUser = null;

                twUser = twitter.showUser(username);

                String profImage = twUser.getBiggerProfileImageURL();
                Log.i("Info", "RetrieveFeedTask starts4");

                String[] elements = twUser.getName().split(" ");
                user.setName(elements[0]);

                String[] lastname = Arrays.copyOfRange(elements, 1, elements.length);

                Log.i("Info", "RetrieveFeedTask starts5");

                StringBuilder builder = new StringBuilder();
                for (String s : lastname) {
                    builder.append(s);
                    builder.append(" ");
                }
                String surname = builder.toString().trim();
                user.setSurname(surname);

                Log.i("Info", "RetrieveFeedTask starts6");

                user.setUsername(twUser.getScreenName());
                user.setProfilePicSrc(profImage);

                Log.i("Info", "  >>twitter profImage :" + profImage);
                Log.i("Info", "  >>twitter name      :" + elements[0]);
                Log.i("Info", "  >>twitter surname   :" + surname);
                Log.i("Info", "  >>twitter username  :" + twUser.getScreenName());

            } catch (twitter4j.TwitterException e) {
                Log.i("Info", "  >>twitter try exception1:" + e.toString());
            }
            catch (Exception e) {
                Log.i("Info", "  >>twitter try exception2:" + e.toString());
            }

        } catch (Exception e) {
            Log.i("Info", "  >>twitter try exception3:" + e.toString());

        } finally {

        }

    }







    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        try {

            // Pass the activity result to the Twitter login button.
            mLoginButton.onActivityResult(requestCode, resultCode, data);

            // Pass the activity result back to the Facebook SDK
            mCallbackManager.onActivityResult(requestCode, resultCode, data);
        } catch (Exception e) {
            Log.i("Info", "onActivityResult error:" + e.toString());
        }
    }

    @Override
    public void onClick(View v) {

        int i = v.getId();

        Intent intent;

        switch (i) {
            case R.id.registerButton:

                intent = new Intent(getApplicationContext(), RegisterPageActivity.class);
                finish();
                startActivity(intent);
                break;

            case R.id.logInButton:
                intent = new Intent(getApplicationContext(), LoginPageActivity.class);
                finish();
                startActivity(intent);
                break;

            default:
                Toast.makeText(this, "Error occured!!", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    public void startNextPage() {

        try {

            Log.i("Info", "startNextPage starts");

            Intent intent = null;

            Log.i("UserInfo", "  >>mail     :" + user.getEmail());
            Log.i("UserInfo", "  >>name     :" + user.getName());
            Log.i("UserInfo", "  >>surname  :" + user.getSurname());
            Log.i("UserInfo", "  >>userid   :" + user.getUserId());
            Log.i("UserInfo", "  >>username :" + user.getUsername());
            Log.i("UserInfo", "  >>gender   :" + user.getGender());
            Log.i("UserInfo", "  >>src      :" + user.getProfilePicSrc());
            Log.i("UserInfo", "  >>birthdate:" + user.getBirthdate());
            Log.i("UserInfo", "  >>phone    :" + user.getPhoneNum());
            Log.i("UserInfo", "  >>src      :" + user.getProfilePicSrc());

            if (fbLogin || twLogin) {

                intent = new Intent(getApplicationContext(), ProfilePageActivity.class);

            } else {

                intent = new Intent(getApplicationContext(), ProfilePhotoActivity.class);
                intent.putExtra("User", user);
            }

            Log.i("UserInfo", "  >>Info buradayim");
            finish();
            startActivity(intent);

        } catch (Exception e) {
            Log.i("Info", "  >>startNextPage exception:" + e.toString());
        }
    }

    public void saveProfPicViaSocialApp() {

        Log.i("Info", "saveProfPicViaSocialApp");

        try {
            //photoImageView.setDrawingCacheEnabled(true);
            //photo = photoImageView.getDrawingCache();

            FirebaseUser currentUser = mAuth.getCurrentUser();
            FBuserId = currentUser.getUid();

            riversRef = mStorageRef.child("Users/profilePics").child(FBuserId + ".jpg");

            //photo = ((BitmapDrawable) photoImageView.getDrawable()).getBitmap();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            photo.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] data = baos.toByteArray();

            UploadTask uploadTask = riversRef.putBytes(data);

            uploadTask = riversRef.putStream(profileImageStream);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {

                    Log.i("Info", "put file err:" + exception.toString());
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    downloadUrl = taskSnapshot.getDownloadUrl();
                    user.setProfilePicSrc(downloadUrl.toString());

                    Log.i("Info", "downloadUrl:" + downloadUrl);
                }
            });
        } catch (Exception e) {

            Log.i("Info", "  >>saveProfPicViaSocialApp exception:" + e.toString());
        }
    }

    public class DownloadTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {

            String result = "";
            URL url;
            HttpURLConnection urlConnection = null;

            try {
                url = new URL(urls[0]);
                urlConnection = (HttpURLConnection) url.openConnection();

                profileImageStream = urlConnection.getInputStream();
                photo = BitmapFactory.decodeStream(profileImageStream);
                photo = BitmapConversion.getRoundedShape(photo, 250, 250);

                return result;

            } catch (Exception e) {

                Log.i("Info", "  >>DownloadTask error:" + e.toString());
                return result;
            }
        }
    }
}
