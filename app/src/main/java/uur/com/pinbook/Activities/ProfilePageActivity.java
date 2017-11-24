package uur.com.pinbook.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

import uur.com.pinbook.R;

public class ProfilePageActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth firebaseAuth;
    private Button buttonLogout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_page);



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
            finish();
            startActivity(new Intent(this, EnterPageActivity.class));
        }

    }

}
