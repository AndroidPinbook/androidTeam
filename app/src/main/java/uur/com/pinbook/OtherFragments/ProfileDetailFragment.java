package uur.com.pinbook.OtherFragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.appinvite.FirebaseAppInvite;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.dynamiclinks.DynamicLink;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.PendingDynamicLinkData;
import com.google.firebase.dynamiclinks.ShortDynamicLink;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.ButterKnife;
import uur.com.pinbook.Activities.EditProfileActivity;
import uur.com.pinbook.FirebaseGetData.FirebaseGetAccountHolder;
import uur.com.pinbook.FirebaseGetData.FirebaseGetFriends;
import uur.com.pinbook.JavaFiles.Friend;
import uur.com.pinbook.R;

import static uur.com.pinbook.ConstantsModel.FirebaseConstant.*;
import static uur.com.pinbook.ConstantsModel.StringConstant.*;

@SuppressLint("ValidFragment")
public class ProfileDetailFragment extends Fragment {

    private View mView;
    String FBuserID;
    ViewGroup mContainer;
    LayoutInflater mLayoutInflater;

    DatabaseReference mDbrefFriendList;

    FirebaseGetFriends firebaseGetFriendsInstance = null;
    ArrayList<Friend> invitableFriends = new ArrayList<>();

    private Context context;

    @SuppressLint("ValidFragment")
    public ProfileDetailFragment(Context context) {
        this.context = context;
        this.FBuserID = FirebaseGetAccountHolder.getUserID();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        this.mContainer = container;
        this.mLayoutInflater = inflater;
        mView = inflater.inflate(R.layout.fragment_profile_detail, container, false);
        ButterKnife.bind(this, mView);

        return mView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        LinearLayout addFromFacebookLayout = mView.findViewById(R.id.addFromFacebookLayout);
        LinearLayout inviteForInstallLayout = mView.findViewById(R.id.inviteForInstallLayout);
        LinearLayout addFromContactLayout = mView.findViewById(R.id.addFromContactLayout);
        LinearLayout editProfileLayout = mView.findViewById(R.id.editProfileLayout);

        addFromFacebookLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InviteFacebookFriendFragment nextFrag = new InviteFacebookFriendFragment(getActivity());
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(((ViewGroup) getView().getParent()).getId(), nextFrag, inviteFacebookFriendFragment)
                        .addToBackStack(inviteFacebookFriendFragment)
                        .commit();
            }
        });

        addFromContactLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InviteContactFriendFragment nextFrag = new InviteContactFriendFragment(getActivity());
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(((ViewGroup) getView().getParent()).getId(), nextFrag, inviteContactFriendFragment)
                        .addToBackStack(inviteContactFriendFragment)
                        .commit();
            }
        });

        inviteForInstallLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendInvitation();
            }
        });

        editProfileLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), EditProfileActivity.class));
            }
        });
    }

    public void sendInvitation() {

        FirebaseDynamicLinks.getInstance()
                .getDynamicLink(getActivity().getIntent())
                .addOnSuccessListener(getActivity(), new OnSuccessListener<PendingDynamicLinkData>() {
                    @Override
                    public void onSuccess(PendingDynamicLinkData pendingDynamicLinkData) {

                        if (pendingDynamicLinkData != null) {

                            Uri deepLink = pendingDynamicLinkData.getLink();

                            FirebaseAppInvite invite = FirebaseAppInvite.getInvitation(pendingDynamicLinkData);
                            if (invite != null) {

                                String invitationId = invite.getInvitationId();
                                if (!TextUtils.isEmpty(invitationId)) {
                                    Log.i("Info", "invitation id:" + invitationId);
                                }
                            }
                        }
                    }
                }).addOnFailureListener(getActivity(), new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.i("Info", "On Failure");
            }
        });

        shareShortDynamicLink();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    public void shareShortDynamicLink() {

        Task<ShortDynamicLink> createLinkTask = FirebaseDynamicLinks.getInstance().createDynamicLink()
                .setLongLink(Uri.parse(buildDynamicLink()))
                .buildShortDynamicLink()
                .addOnCompleteListener(getActivity(), new OnCompleteListener<ShortDynamicLink>() {
                    @Override
                    public void onComplete(@NonNull Task<ShortDynamicLink> task) {

                        if (task.isSuccessful()) {
                            Uri shortLink = task.getResult().getShortLink();
                            Uri flowChartLink = task.getResult().getPreviewLink();

                            Intent intent = new Intent();
                            String msg = "visit my awesome site:" + shortLink;
                            intent.setAction(Intent.ACTION_SEND);
                            intent.putExtra(Intent.EXTRA_TEXT, msg);
                            intent.setType("text/plain");
                            if (intent.resolveActivity(getActivity().getPackageManager()) != null)
                                startActivity(Intent.createChooser(intent, "Share"));

                        } else {
                            Log.i("Info", "On Failure short link");
                        }
                    }
                });
    }

    public String buildDynamicLink() {
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

    @Override
    public void onDestroy() throws NullPointerException {
        super.onDestroy();
        //mDbrefFriendList.removeEventListener(valueEventListenerForFriendList);
    }
}
