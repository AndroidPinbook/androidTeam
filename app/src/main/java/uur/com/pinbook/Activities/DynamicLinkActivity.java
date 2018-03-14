package uur.com.pinbook.Activities;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.appinvite.FirebaseAppInvite;
import com.google.firebase.dynamiclinks.DynamicLink;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.PendingDynamicLinkData;
import com.google.firebase.dynamiclinks.ShortDynamicLink;

import java.util.List;

import uur.com.pinbook.R;

import static uur.com.pinbook.ConstantsModel.FirebaseConstant.*;

public class DynamicLinkActivity extends AppCompatActivity {

    TextView onSuccessTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dynamic_link);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        onSuccessTv = (TextView) findViewById(R.id.textView);

        FirebaseDynamicLinks.getInstance()
                .getDynamicLink(getIntent())
                .addOnSuccessListener(this, new OnSuccessListener<PendingDynamicLinkData>() {
                    @Override
                    public void onSuccess(PendingDynamicLinkData pendingDynamicLinkData) {

                        if(pendingDynamicLinkData != null){

                            Uri deepLink = pendingDynamicLinkData.getLink();
                            onSuccessTv.append("\n onSuccess called:" + deepLink.toString());

                            FirebaseAppInvite invite = FirebaseAppInvite.getInvitation(pendingDynamicLinkData);
                            if(invite != null){

                                String invitationId = invite.getInvitationId();
                                if(!TextUtils.isEmpty(invitationId)){
                                    onSuccessTv.append("\ninvitation id:" + invitationId);
                                }
                            }
                        }
                    }
                }).addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                onSuccessTv.append("\nOn failure");
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                //        .setAction("Action", null).show();

                /*Intent intent = new Intent();
                String msg = "visit my awesome site:" + buildDynamicLink();
                intent.setAction(Intent.ACTION_SEND);
                intent.putExtra(intent.EXTRA_TEXT, msg);
                intent.setType("text/plain");
                if (intent.resolveActivity(getPackageManager()) != null)
                    startActivity(Intent.createChooser(intent, "Share"));*/

                shareShortDynamicLink();

            }
        });
    }

    public void openAppList(){
        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> pkgAppsList = this.getPackageManager().queryIntentActivities( mainIntent, 0);
        startActivity(mainIntent);
    }

    public void openSendList(){
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.putExtra(intent.EXTRA_TEXT, "hiyartolar");
        intent.setType("text/plain");

        if (intent.resolveActivity(getPackageManager()) != null)
            startActivity(Intent.createChooser(intent, "Share"));
    }

    public void shareShortDynamicLink(){

        Task<ShortDynamicLink> createLinkTask = FirebaseDynamicLinks.getInstance().createDynamicLink()
                .setLongLink(Uri.parse(buildDynamicLink()))
                .buildShortDynamicLink()
                .addOnCompleteListener(this, new OnCompleteListener<ShortDynamicLink>() {
                    @Override
                    public void onComplete(@NonNull Task<ShortDynamicLink> task) {

                        if(task.isSuccessful()){
                            Uri shortLink = task.getResult().getShortLink();
                            Uri flowChartLink = task.getResult().getPreviewLink();

                            Intent intent = new Intent();
                            String msg = "visit my awesome site:" + shortLink;
                            intent.setAction(Intent.ACTION_SEND);
                            intent.putExtra(Intent.EXTRA_TEXT, msg);
                            intent.setType("text/plain");
                            if (intent.resolveActivity(getPackageManager()) != null)
                                startActivity(Intent.createChooser(intent, "Share"));

                        }else {
                            onSuccessTv.append("\nError building shor link");
                        }
                    }
                });
    }

    public String buildDynamicLink(){
        String dynamicLink = null;

        dynamicLink = FirebaseDynamicLinks.getInstance().createDynamicLink()
                .setDynamicLinkDomain(dynamicLinkDomain)
                .setLink(Uri.parse(appShareLink))
                .setAndroidParameters(new DynamicLink.AndroidParameters.Builder().build())
                //.setSocialMetaTagParameters(new DynamicLink.SocialMetaTagParameters.Builder().setTitle("Share This app"))
                .setSocialMetaTagParameters(new DynamicLink.SocialMetaTagParameters.Builder().build())
                .buildDynamicLink().getUri().toString();


        return dynamicLink;
    }

    public void onClickWhatsApp() {

        PackageManager pm=getPackageManager();
        try {

            Intent waIntent = new Intent(Intent.ACTION_SEND);
            waIntent.setType("text/plain");
            String text = "YOUR TEXT HERE";

            PackageInfo info=pm.getPackageInfo("com.whatsapp", PackageManager.GET_META_DATA);
            //Check if package exists or not. If not then code
            //in catch block will be called
            waIntent.setPackage("com.whatsapp");

            waIntent.putExtra(Intent.EXTRA_TEXT, buildDynamicLink());
            startActivity(Intent.createChooser(waIntent, "Share with"));

        } catch (PackageManager.NameNotFoundException e) {
            Toast.makeText(this, "WhatsApp not Installed", Toast.LENGTH_SHORT)
                    .show();
        }

    }

}
