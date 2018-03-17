package uur.com.pinbook.Activities;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import java.util.Calendar;

import uur.com.pinbook.Adapters.CustomDialogAdapter;
import uur.com.pinbook.Controller.ClearSingletonClasses;
import uur.com.pinbook.FirebaseGetData.FirebaseGetAccountHolder;
import uur.com.pinbook.FirebaseGetData.FirebaseGetFriends;
import uur.com.pinbook.JavaFiles.User;
import uur.com.pinbook.LazyList.ImageLoader;
import uur.com.pinbook.R;

import static uur.com.pinbook.ConstantsModel.StringConstant.displayRounded;
import static uur.com.pinbook.ConstantsModel.StringConstant.friendsCacheDirectory;

public class EditProfileActivity extends AppCompatActivity {

    Toolbar mToolBar;
    EditText nameEditText;
    EditText surnameEditText;
    EditText usernameEditText;
    EditText birthdateEditText;
    EditText emailEditText;
    EditText phonenumEditText;
    Spinner genderSpinner;
    Button saveEditButton;
    ImageView userImageView;

    ImageLoader imageLoader;

    User userBef;

    DatePickerDialog.OnDateSetListener mDateSetListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        mToolBar = findViewById(R.id.toolbarLayout);
        mToolBar.setTitle("Profili Düzenle");
        mToolBar.setNavigationIcon(R.drawable.back_arrow);
        mToolBar.setBackgroundColor(getResources().getColor(R.color.background, null));
        mToolBar.setTitleTextColor(getResources().getColor(R.color.background_white, null));
        setSupportActionBar(mToolBar);

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        nameEditText = findViewById(R.id.nameEditText);
        surnameEditText = findViewById(R.id.surnameEditText);
        usernameEditText = findViewById(R.id.usernameEditText);
        birthdateEditText = findViewById(R.id.birthdateEditText);
        emailEditText = findViewById(R.id.emailEditText);
        phonenumEditText = findViewById(R.id.phonenumEditText);
        genderSpinner = findViewById(R.id.genderSpinner);
        saveEditButton = findViewById(R.id.saveEditButton);
        userImageView = findViewById(R.id.userImageView);

        imageLoader = new ImageLoader(this, friendsCacheDirectory);

        emailEditText.setFocusableInTouchMode(false);
        emailEditText.setFocusable(false);

        birthdateEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getCalender();
            }
        });

        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month = month + 1;
                String date =  dayOfMonth + "/"  + month + "/" + year;
                birthdateEditText.setText(date);
            }
        };

        userBef = FirebaseGetAccountHolder.getInstance().getUser();
        fillUserInfo();
    }

    public void getCalender(){

        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog = new DatePickerDialog(EditProfileActivity.this,
                android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                mDateSetListener,
                year, month, day);

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return super.onOptionsItemSelected(item);
    }

    private void fillUserInfo() {

        imageLoader.DisplayImage(userBef.getProfilePicSrc(), userImageView, displayRounded);

        if(userBef.getName() != null) nameEditText.setText(userBef.getName().toString());
        if(userBef.getSurname() != null) surnameEditText.setText(userBef.getSurname().toString());
        if(userBef.getUsername() != null) usernameEditText.setText(userBef.getUsername().toString());
        if(userBef.getBirthdate() != null) birthdateEditText.setText(userBef.getBirthdate().toString());
        if(userBef.getEmail() != null) emailEditText.setText(userBef.getEmail().toString());
        if(userBef.getPhoneNum() != null) phonenumEditText.setText(userBef.getPhoneNum().toString());
    }

}
