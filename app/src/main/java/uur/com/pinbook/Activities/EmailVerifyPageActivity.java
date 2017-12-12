package uur.com.pinbook.Activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.ActionCodeSettings;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.dynamiclinks.DynamicLink;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.PendingDynamicLinkData;

import java.net.URL;

import retrofit2.http.Url;
import uur.com.pinbook.Controller.CustomDialogAdapter;
import uur.com.pinbook.Controller.ErrorMessageAdapter;
import uur.com.pinbook.R;

public class EmailVerifyPageActivity extends AppCompatActivity implements View.OnClickListener{

    Button buttonActivated;
    TextView textVerifyAgain;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;

    private ActionCodeSettings actionCodeSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            setContentView(R.layout.activity_email_verify_page);
        } catch (Exception e) {
            Log.i("Info", "  >>setContentView error:" + e.toString());
        }

        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();

        sendVerificationMail();

        TextView textEmail = (TextView) findViewById(R.id.textEmail);
        textEmail.setText(user.getEmail().toString());

        textVerifyAgain = (TextView) findViewById(R.id.textVerifyAgain);
        buttonActivated = (Button) findViewById(R.id.buttonActivated);

        textVerifyAgain.setOnClickListener(this);
        buttonActivated.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v == textVerifyAgain){
            sendVerificationMail();
        }
        if(v == buttonActivated){
            buttonActivatedFunc();
        }
    }

    public void buttonActivatedFunc() {

        Log.i("Info", "buttonActivatedFunc starts======");

        firebaseAuth.getCurrentUser().reload().addOnCompleteListener(this, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if(user.isEmailVerified()){
                    Log.i("verified: ", "yes");
                    finish();
                    startActivity(new Intent(EmailVerifyPageActivity.this, PinThrowActivity.class));
                }else{
                    Log.i("!verified: ", "no");
                    CustomDialogAdapter.showDialogWarning(EmailVerifyPageActivity.this,
                            "Lütfen önce hesabınızı aktifleştirin.");
                }
            }
        });
    }

    private void sendVerificationMail(){

        final FirebaseUser final_user = firebaseAuth.getCurrentUser();

        actionCodeSettings = ActionCodeSettings.newBuilder()
                .setAndroidPackageName("uur.com.pinbook", true, null)
                .setHandleCodeInApp(false)
                .setIOSBundleId(null)
                .setUrl("https://androidteam-f4c25.firebaseapp.com")
                .build();

        final_user.sendEmailVerification(actionCodeSettings).addOnCompleteListener(this, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    CustomDialogAdapter.showDialogInfo(EmailVerifyPageActivity.this,
                                                       "Aktivasyon linki mailinize gönderildi, lütfen aktive ediniz.");
                } else {
                    Log.i("hata ", task.getException().toString());
                    CustomDialogAdapter.showDialogInfo(EmailVerifyPageActivity.this,
                            ErrorMessageAdapter.FAIL_TO_SEND_VERIFICATION_MAIL.getText());
                }
            }

        });
    }
}
