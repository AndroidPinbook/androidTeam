package uur.com.pinbook.Activities;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.twitter.sdk.android.core.TwitterCore;

import uur.com.pinbook.Controller.ClearSingletonClasses;
import uur.com.pinbook.FirebaseGetData.FirebaseGetAccountHolder;
import uur.com.pinbook.R;

public class ChangePasswordActivity extends AppCompatActivity {

    Toolbar mToolBar;
    Button changePasswordButton;
    EditText currPasswordEdittext;
    EditText newPasswordEdittext;
    EditText validatePassEdittext;
    RelativeLayout container;
    FirebaseUser user;
    String newPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        mToolBar = findViewById(R.id.toolbarLayout);
        mToolBar.setTitle("Şifreyi Değiştir");
        mToolBar.setNavigationIcon(R.drawable.back_arrow);
        mToolBar.setBackgroundColor(getResources().getColor(R.color.background, null));
        mToolBar.setTitleTextColor(getResources().getColor(R.color.background_white, null));
        setSupportActionBar(mToolBar);

        changePasswordButton = findViewById(R.id.changePasswordButton);
        currPasswordEdittext = findViewById(R.id.currPasswordEdittext);
        newPasswordEdittext = findViewById(R.id.newPasswordEdittext);
        validatePassEdittext = findViewById(R.id.validatePassEdittext);
        container = findViewById(R.id.container);

        setItemsClickListeners();
    }

    private void setItemsClickListeners() {

        changePasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validatePasswords();
            }
        });

        container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (getCurrentFocus() != null) {
                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                }
            }
        });
    }

    public void validatePasswords() {

        if (currPasswordEdittext.getText().toString().isEmpty()) {
            Snackbar.make(findViewById(R.id.container), "Mevcut Şifre alani boş olamaz", Snackbar.LENGTH_LONG)
                    .show();
            return;
        }

        if (newPasswordEdittext.getText().toString().isEmpty()) {
            Snackbar.make(findViewById(R.id.container), "Yeni Şifre alani boş olamaz", Snackbar.LENGTH_LONG)
                    .show();
            return;
        }

        if (newPasswordEdittext.getText().toString().trim().length() < 6) {
            Snackbar.make(findViewById(R.id.container), "Yeni Şifre Minimum 6 karakter olmalı!", Snackbar.LENGTH_LONG)
                    .show();
            return;
        }

        if (validatePassEdittext.getText().toString().isEmpty()) {
            Snackbar.make(findViewById(R.id.container), "Şifre doğrula alani boş olamaz", Snackbar.LENGTH_LONG)
                    .show();
            return;
        }

        if (!newPasswordEdittext.getText().toString().equals(validatePassEdittext.getText().toString())) {
            Snackbar.make(findViewById(R.id.container), "Doğrulama alanı hatali. Tekrar kontrol ediniz.", Snackbar.LENGTH_LONG)
                    .show();
            return;
        }

        changePassword();
    }

    private void changePassword() {

        user = FirebaseAuth.getInstance().getCurrentUser();

        newPassword = newPasswordEdittext.getText().toString();

        AuthCredential credential = EmailAuthProvider.
                getCredential(FirebaseGetAccountHolder.getInstance().getUser().getEmail(),
                        currPasswordEdittext.getText().toString());

        user.reauthenticate(credential)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            user.updatePassword(newPassword).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(ChangePasswordActivity.this,
                                                "Şifre Yenilendi" , Toast.LENGTH_SHORT).show();
                                        thread.start();
                                    } else {
                                        Toast.makeText(ChangePasswordActivity.this,
                                                "Teknik Hata:Şifre Güncellenemedi!" , Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        } else {
                            Snackbar.make(findViewById(R.id.container), "Teknik Hata:Mevcut Şifre Hatalı!", Snackbar.LENGTH_LONG)
                                    .show();
                        }
                    }
                });
    }

    Thread thread = new Thread(){
        @Override
        public void run() {
            try {
                Thread.sleep(3500);
                FirebaseAuth.getInstance().signOut();
                LoginManager.getInstance().logOut();
                TwitterCore.getInstance().getSessionManager().clearActiveSession();
                ClearSingletonClasses.clearAllClasses();
                finish();
                startActivity(new Intent(ChangePasswordActivity.this, EnterPageActivity.class));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return super.onOptionsItemSelected(item);
    }
}
