package uur.com.pinbook.Activities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

import java.util.Calendar;

import uur.com.pinbook.R;

public class RegisterPageActivity extends AppCompatActivity implements View.OnClickListener, FirebaseAuth.AuthStateListener{

    //Set the radius of the Blur. Supported range 0 < radius <= 25
    private static final float BLUR_RADIUS = 10f;

    private String emailText;
    private String passwordText;

    private Button registerButton;
    private Button sendVerifButton;

    private EditText usernameEditText;
    private EditText nameEditText;
    private EditText surnameEditText;
    private EditText phoneEditText;
    private EditText birthdateEditText;
    private TextView genderTextView;

    private FirebaseAuth mAuth;
    private DatabaseReference mDbref;

    public String tag_users = "users";

    public String gender;

    Handler handler;
    private boolean onCreateOk = false;
    private boolean runnableOk = false;

    public ImageView maleImageView;
    public ImageView femaleImageView;

    public Calendar myCalendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_page);

        RelativeLayout backGrounRelLayout = (RelativeLayout) findViewById(R.id.registerLayout);

        mAuth = FirebaseAuth.getInstance();

        registerButton = findViewById(R.id.registerButton);
        sendVerifButton = findViewById(R.id.sendVerifyMailBtn);

        usernameEditText = findViewById(R.id.usernameEditText);
        nameEditText = findViewById(R.id.nameEditText);
        surnameEditText = findViewById(R.id.surnameEditText);
        phoneEditText = findViewById(R.id.phoneEditText);
        birthdateEditText = findViewById(R.id.birthdateEditText);
        maleImageView = findViewById(R.id.maleImageView);
        femaleImageView = findViewById(R.id.femaleImageView);

        getCalender();

        getMailAndPassword();

        findViewById(R.id.registerButton).setOnClickListener(this);
        findViewById(R.id.sendVerifyMailBtn).setOnClickListener(this);
        findViewById(R.id.birthdateEditText).setOnClickListener(this);
        findViewById(R.id.maleImageView).setOnClickListener(this);
        findViewById(R.id.femaleImageView).setOnClickListener(this);

        backGrounRelLayout.setOnClickListener(this);


        if(runnableOk == false)
            checkMailVerified();
    }


    public void getCalender(){

        Log.i("Info", "getCalender");

        myCalendar = Calendar.getInstance();
        final DatePickerDialog.OnDateSetListener date;

        date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }

        };

        birthdateEditText.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(DevlerLigiRegister.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

    }
}
