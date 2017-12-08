package uur.com.pinbook.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.twitter.sdk.android.core.TwitterCore;

import uur.com.pinbook.R;

public class ProfilePageActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth firebaseAuth;
    private Button buttonLogout;

    private CallbackManager mCallbackManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FacebookSdk.sdkInitialize(getApplicationContext());

        setContentView(R.layout.activity_profile_page);

        firebaseAuth = FirebaseAuth.getInstance();

        if(firebaseAuth.getCurrentUser() == null){
            finish();
            startActivity(new Intent(this, EnterPageActivity.class));
        }

        firebaseAuth = FirebaseAuth.getInstance();

        buttonLogout = (Button) findViewById(R.id.buttonLogout);
        buttonLogout.setOnClickListener(this);

    }

    public void onClick(View v) {

        if(v == buttonLogout){
            firebaseAuth.signOut();

            LoginManager.getInstance().logOut();
            TwitterCore.getInstance().getSessionManager().clearActiveSession();
            finish();
            startActivity(new Intent(this, EnterPageActivity.class));
        }

    }

}
