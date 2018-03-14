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
import android.support.v4.content.ContextCompat;
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
import com.firebase.client.Firebase;
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
import uur.com.pinbook.DefaultModels.ContactFriendList;
import uur.com.pinbook.DefaultModels.FacebookFriendList;
import uur.com.pinbook.DefaultModels.GetContactList;
import uur.com.pinbook.DefaultModels.SelectedFriendList;
import uur.com.pinbook.FirebaseGetData.FBGetInviteInbounds;
import uur.com.pinbook.FirebaseGetData.FBGetInviteOutbounds;
import uur.com.pinbook.FirebaseGetData.FirebaseGetAccountHolder;
import uur.com.pinbook.FirebaseGetData.FirebaseGetFriends;
import uur.com.pinbook.JavaFiles.Friend;
import uur.com.pinbook.JavaFiles.User;
import uur.com.pinbook.Manifest;
import uur.com.pinbook.R;

import static uur.com.pinbook.ConstantsModel.FirebaseConstant.*;
import static uur.com.pinbook.ConstantsModel.NumericConstant.PERMISSION_REQUEST_READ_CONTACTS;
import static uur.com.pinbook.ConstantsModel.StringConstant.*;

@SuppressLint("ValidFragment")
public class ProfileDetailFragment extends Fragment {

    RecyclerView groupRecyclerView;

    private View mView;
    String FBuserID;
    ViewGroup mContainer;
    LayoutInflater mLayoutInflater;

    LinearLayoutManager linearLayoutManager;
    DatabaseReference mDbrefFriendList;

    FirebaseGetFriends firebaseGetFriendsInstance = null;
    ArrayList<Friend> invitableFriends = new ArrayList<>();

    private Context context;
    private String searchText;

    static ValueEventListener valueEventListenerForFriendList;

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

        LinearLayout addFromFacebookLayout = (LinearLayout) mView.findViewById(R.id.addFromFacebookLayout);
        LinearLayout inviteForInstallLayout = (LinearLayout) mView.findViewById(R.id.inviteForInstallLayout);
        LinearLayout addFromContactLayout = (LinearLayout) mView.findViewById(R.id.addFromContactLayout);

        FacebookFriendList.setInstance(null);

        firebaseGetFriendsInstance = FirebaseGetFriends.getFBGetFriendsInstance();

        getFacebookFriends();

        GetContactList.getInstance(context);
        fillContactFriendList();

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


    private void fillContactFriendList() {

        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {

            if(ContactFriendList.getInstance().getContactFriendList().size() > 0)
                return;

            String clearPhoneNum = "";

            for(User user: GetContactList.getInstance(context).getContactList()){

                clearPhoneNum = "";

                for(int i=0; i < user.getPhoneNum().length(); i++){

                    char ch = user.getPhoneNum().charAt(i);
                    if(Character.isDigit(ch)){
                        clearPhoneNum += ch;
                    }
                }

                DatabaseReference dbRefContact = FirebaseDatabase.getInstance().getReference(PhoneNums).child(clearPhoneNum);

                valueEventListenerForFriendList = dbRefContact.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        for(DataSnapshot contactSnapshot: dataSnapshot.getChildren()){

                            if(contactSnapshot.getValue() != null) {

                                final String FirebaseUID = contactSnapshot.getValue().toString();

                                Friend friend = checkFriendByFireUId(FirebaseUID);

                                if(friend != null)
                                    setContactFriendToInviteList(friend, Yes);
                                else{
                                    FirebaseDatabase.getInstance().getReference(Users).child(FirebaseUID)
                                            .addValueEventListener(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot dataSnapshot) {

                                                    Map<String , Object> map = new HashMap<String, Object>();
                                                    map = (Map) dataSnapshot.getValue();

                                                    Friend friend1 = new Friend();

                                                    friend1.setUserID(FirebaseUID);
                                                    friend1.setNameSurname((String) map.get(name) + " " + (String) map.get(surname));
                                                    friend1.setProfilePicSrc((String) map.get(profilePictureUrl));
                                                    friend1.setProviderId((String) map.get(providerId));

                                                    setContactFriendToInviteList(friend1, null);
                                                }

                                                @Override
                                                public void onCancelled(DatabaseError databaseError) {

                                                }
                                            });
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        }
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

    public void getFacebookFriends() {

        GraphRequest graphRequest = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(),
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {

                        Map<String, Map<String, Map<String, String>>> fbFriends = new HashMap<>();

                        if (object != null) {
                            try {
                                JSONArray friendList = new JSONArray();
                                friendList = object.getJSONObject("friends").getJSONArray("data");

                                for (int i = 0; i < friendList.length(); i++) {
                                    JSONObject jsonObject = friendList.getJSONObject(i);
                                    String friendName = (String) jsonObject.get(name);
                                    String friendProviderID = (String) jsonObject.get("id");

                                    if (checkFriendByProviderId(friendProviderID))
                                        setFaceFriendToInviteList(friendProviderID, friendName, Yes);
                                    else
                                        setFaceFriendToInviteList(friendProviderID, friendName, null);

                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });

        Bundle parameters = new Bundle();
        parameters.putString("fields", "friends");
        graphRequest.setParameters(parameters);
        graphRequest.executeAsync();
    }

    public void setFaceFriendToInviteList(final String providerId, final String friendName, final String friendStatus) {

        Log.i("Info", "setFaceFriendToInviteList");
        Log.i("Info", "  -->providerId:" + providerId);
        Log.i("Info", "  -->friendName:" + friendName);
        Log.i("Info", "  -->friendStatus:" + friendStatus);

        mDbrefFriendList = FirebaseDatabase.getInstance().getReference(FacebookUsers).child(providerId);

        mDbrefFriendList.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot friendsSnapshot : dataSnapshot.getChildren()) {

                    if (friendsSnapshot.getValue() != null) {

                        Friend friend = new Friend();

                        Map<String, Object> userList = new HashMap<String, Object>();
                        userList = (Map) dataSnapshot.getValue();

                        String firebaseUid = (String) userList.get(fbUserId);

                        friend.setUserID(firebaseUid);
                        friend.setProviderId(providerId);
                        friend.setNameSurname(friendName);
                        friend.setProfilePicSrc(getFacebookProfilePicture(providerId));

                        if (friendStatus == null) {
                            if (OutboundStatusChk(firebaseUid))
                                friend.setFriendStatus(OutbndWaiting);
                            else if (InboundStatusChk(firebaseUid))
                                friend.setFriendStatus(InbndWaiting);
                            else
                                friend.setFriendStatus(No);
                        } else
                            friend.setFriendStatus(friendStatus);

                        //invitableFriends.add(friend);
                        FacebookFriendList.getInstance().addFriend(friend);

                        Log.i("Info", "  -->invitableFriends:" + invitableFriends.toString());
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void setContactFriendToInviteList(Friend friend, String friendStatus){

        if(friendStatus != null){
            friend.setFriendStatus(friendStatus);
        }else {
            if (OutboundStatusChk(friend.getUserID()))
                friend.setFriendStatus(OutbndWaiting);
            else if (InboundStatusChk(friend.getUserID()))
                friend.setFriendStatus(InbndWaiting);
            else
                friend.setFriendStatus(No);
        }

        ContactFriendList.getInstance().addFriend(friend);
    }

    public boolean OutboundStatusChk(String fbUserID) {

        boolean status = false;

        for (String uID : FBGetInviteOutbounds.getInstance(FBuserID).getOutboundList()) {
            if (uID.equals(fbUserID)) {
                status = true;
                break;
            }
        }
        return status;
    }

    public boolean InboundStatusChk(String userFBId) {

        boolean status = false;

        if (FBGetInviteInbounds.getFBGetInviteInboundsInstance() == null)
            return status;

        for (String uID : FBGetInviteInbounds.getFBGetInviteInboundsInstance().getInboundList()) {
            if (uID.equals(userFBId)) {
                status = true;
                break;
            }
        }
        return status;
    }

    public boolean checkFriendByProviderId(String providerId) {

        boolean checkFriend = false;

        for (Friend friend : firebaseGetFriendsInstance.getFriendList()) {
            if (friend.getProviderId().equals(providerId)) {
                checkFriend = true;
                break;
            }
        }
        return checkFriend;
    }

    public Friend checkFriendByFireUId(String FirebaseUID){

        Friend tempFriend = new Friend();

        for (Friend friend : firebaseGetFriendsInstance.getFriendList()) {
            if (friend.getUserID().equals(FirebaseUID)) {
                tempFriend = friend;
                break;
            }
        }

        return tempFriend;
    }

    public String getFacebookProfilePicture(String userID) {
        String url = "https://graph.facebook.com/" + userID + "/picture?type=normal";
        return url;
    }

    @Override
    public void onDestroy() throws NullPointerException {
        super.onDestroy();
        //mDbrefFriendList.removeEventListener(valueEventListenerForFriendList);
    }
}
