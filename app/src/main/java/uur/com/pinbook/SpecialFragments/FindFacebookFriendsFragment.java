package uur.com.pinbook.SpecialFragments;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.ButterKnife;
import uur.com.pinbook.DefaultModels.FacebookFriendList;
import uur.com.pinbook.FirebaseGetData.FBGetInviteFacebookInbounds;
import uur.com.pinbook.FirebaseGetData.FBGetInviteFacebookOutbounds;
import uur.com.pinbook.FirebaseGetData.FirebaseGetFriends;
import uur.com.pinbook.JavaFiles.Friend;
import uur.com.pinbook.ListAdapters.InviteFaceFriendListAdapter;
import uur.com.pinbook.R;

import static uur.com.pinbook.ConstantsModel.FirebaseConstant.FacebookUsers;
import static uur.com.pinbook.ConstantsModel.FirebaseConstant.InbndWaiting;
import static uur.com.pinbook.ConstantsModel.FirebaseConstant.No;
import static uur.com.pinbook.ConstantsModel.FirebaseConstant.OutbndWaiting;
import static uur.com.pinbook.ConstantsModel.FirebaseConstant.Yes;
import static uur.com.pinbook.ConstantsModel.FirebaseConstant.fbUserId;
import static uur.com.pinbook.ConstantsModel.FirebaseConstant.name;
import static uur.com.pinbook.ConstantsModel.StringConstant.*;

@SuppressLint("ValidFragment")
public class FindFacebookFriendsFragment extends Fragment{

    RecyclerView personRecyclerView;

    private View mView;
    Context context;
    String viewType;
    ProgressDialog mProgressDialog;

    LinearLayoutManager linearLayoutManager;
    FirebaseGetFriends firebaseGetFriendsInstance = null;

    long friendListSize = 0;

    @SuppressLint("ValidFragment")
    public FindFacebookFriendsFragment(Context context, String viewType) {
        this.context = context;
        this.viewType = viewType;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        mView = inflater.inflate(R.layout.fragment_special_select, container, false);
        ButterKnife.bind(this, mView);
        return mView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        personRecyclerView = (RecyclerView) mView.findViewById(R.id.specialRecyclerView);
        firebaseGetFriendsInstance = FirebaseGetFriends.getFBGetFriendsInstance();
        mProgressDialog = new ProgressDialog(context);
        getFacebookFriends();
    }

    public void getFacebookFriends() {

        GraphRequest graphRequest = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(),
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {

                        if (object != null) {
                            try {
                                JSONArray friendList = object.getJSONObject("friends").getJSONArray("data");
                                FacebookFriendList.setInstance(null);

                                friendListSize = friendList.length();

                                mProgressDialog.setMessage("Yükleniyor...");
                                mProgressDialog.show();

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

        DatabaseReference mDbrefFriendList = FirebaseDatabase.getInstance().getReference(FacebookUsers).child(providerId);

        mDbrefFriendList.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot friendsSnapshot : dataSnapshot.getChildren()) {

                    if (friendsSnapshot.getValue() != null) {

                        Friend friend = new Friend();

                        Map<String, Object> userList = (Map) dataSnapshot.getValue();

                        String firebaseUid = (String) userList.get(fbUserId);

                        friend.setUserID(firebaseUid);
                        friend.setProviderId(providerId);
                        friend.setNameSurname(friendName);
                        friend.setProfilePicSrc(getFacebookProfilePicture(providerId));

                        if (friendStatus == null) {
                            if (OutboundFacebookStatusChk(firebaseUid))
                                friend.setFriendStatus(OutbndWaiting);
                            else if (InboundFacebookStatusChk(firebaseUid))
                                friend.setFriendStatus(InbndWaiting);
                            else
                                friend.setFriendStatus(No);
                        } else
                            friend.setFriendStatus(friendStatus);


                        FacebookFriendList.getInstance().addFriend(friend);

                        if(friendListSize == FacebookFriendList.getInstance().getSize()) {
                            mProgressDialog.dismiss();
                            sendFriendsToRecView();
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
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

    public String getFacebookProfilePicture(String userID) {
        String url = "https://graph.facebook.com/" + userID + "/picture?type=normal";
        return url;
    }

    public boolean OutboundFacebookStatusChk(String fbUserID) {

        boolean status = false;

        for (String uID : FBGetInviteFacebookOutbounds.getInstance().getOutboundList()) {
            if (uID.equals(fbUserID)) {
                status = true;
                break;
            }
        }
        return status;
    }

    public boolean InboundFacebookStatusChk(String userFBId) {

        boolean status = false;

        if (FBGetInviteFacebookInbounds.getFBGetInviteInboundsInstance() == null)
            return status;

        for (String uID : FBGetInviteFacebookInbounds.getFBGetInviteInboundsInstance().getInboundList()) {
            if (uID.equals(userFBId)) {
                status = true;
                break;
            }
        }
        return status;
    }

    public void sendFriendsToRecView(){

        switch (viewType) {
            case verticalShown:

                InviteFaceFriendListAdapter inviteOutboundVerListAdapter = new InviteFaceFriendListAdapter(context,
                        FacebookFriendList.getInstance().getFacebookFriendList());

                personRecyclerView.setAdapter(inviteOutboundVerListAdapter);
                linearLayoutManager = new LinearLayoutManager(context);
                linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                personRecyclerView.setLayoutManager(linearLayoutManager);
                break;
            default:
                Toast.makeText(context, "Person Fragment getData teknik hata!!", Toast.LENGTH_SHORT).show();
        }
    }
}
