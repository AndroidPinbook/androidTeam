package uur.com.pinbook.SpecialFragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;

import butterknife.ButterKnife;
import uur.com.pinbook.Activities.ProfilePageActivity;
import uur.com.pinbook.DefaultModels.ContactFriendList;
import uur.com.pinbook.DefaultModels.GetContactList;
import uur.com.pinbook.FirebaseGetData.FBGetInviteContactInbounds;
import uur.com.pinbook.FirebaseGetData.FBGetInviteContactOutbounds;
import uur.com.pinbook.FirebaseGetData.FirebaseGetFriends;
import uur.com.pinbook.JavaFiles.Friend;
import uur.com.pinbook.JavaFiles.User;
import uur.com.pinbook.ListAdapters.InviteContactFriendListAdapter;
import uur.com.pinbook.ListAdapters.InviteFaceFriendListAdapter;
import uur.com.pinbook.R;

import static uur.com.pinbook.ConstantsModel.FirebaseConstant.InbndWaiting;
import static uur.com.pinbook.ConstantsModel.FirebaseConstant.No;
import static uur.com.pinbook.ConstantsModel.FirebaseConstant.OutbndWaiting;
import static uur.com.pinbook.ConstantsModel.FirebaseConstant.PhoneNums;
import static uur.com.pinbook.ConstantsModel.FirebaseConstant.Users;
import static uur.com.pinbook.ConstantsModel.FirebaseConstant.Yes;
import static uur.com.pinbook.ConstantsModel.FirebaseConstant.name;
import static uur.com.pinbook.ConstantsModel.FirebaseConstant.profilePictureUrl;
import static uur.com.pinbook.ConstantsModel.FirebaseConstant.providerId;
import static uur.com.pinbook.ConstantsModel.FirebaseConstant.surname;
import static uur.com.pinbook.ConstantsModel.StringConstant.*;

@SuppressLint("ValidFragment")
public class FindContactFriendFragment extends Fragment{

    RecyclerView personRecyclerView;

    private View mView;
    Context context;
    LinearLayoutManager linearLayoutManager;
    FirebaseGetFriends firebaseGetFriendsInstance;
    InviteContactFriendListAdapter inviteContactFriendListAdapter;

    @SuppressLint("ValidFragment")
    public FindContactFriendFragment(Context context) {
        this.context = context;
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
    public void onResume(){
        super.onResume();
        //((ProfilePageActivity) getActivity()).setActionBarTitle("Your Title");
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        personRecyclerView =  mView.findViewById(R.id.specialRecyclerView);
        // ContactFriendList.setInstance(null);

        GetContactList.getInstance(context);

        linearLayoutManager = new LinearLayoutManager(context);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        personRecyclerView.setLayoutManager(linearLayoutManager);

        inviteContactFriendListAdapter = new InviteContactFriendListAdapter(context,
                ContactFriendList.getInstance().getContactFriendList());

        personRecyclerView.setAdapter(inviteContactFriendListAdapter);

        firebaseGetFriendsInstance = FirebaseGetFriends.getFBGetFriendsInstance();

        fillContactFriendList();
    }

    private void fillContactFriendList() {

        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {

            if(ContactFriendList.getInstance().getContactFriendList().size() > 0) {
                //inviteContactFriendListAdapter.notifyDataSetChanged();
                return;
            }

            for (User user : GetContactList.getInstance(context).getContactList()) {

                FirebaseDatabase.getInstance().getReference(PhoneNums).child(user.getPhoneNum()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        for (DataSnapshot contactSnapshot : dataSnapshot.getChildren()) {

                            if (contactSnapshot.getValue() != null) {

                                final String FirebaseUID = contactSnapshot.getValue().toString();

                                Friend friend = checkFriendByFireUId(FirebaseUID);

                                if (friend != null)
                                    setContactFriendToInviteList(friend, Yes);
                                else {
                                    FirebaseDatabase.getInstance().getReference(Users).child(FirebaseUID).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {

                                            Map<String, Object> map =  (Map) dataSnapshot.getValue();

                                            Friend friend1 = new Friend();

                                            friend1.setUserID(FirebaseUID);
                                            friend1.setNameSurname(map.get(name).toString() + " " + map.get(surname).toString());
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

            Log.i("Info", "On Failure");
        }
        Log.i("Info", "On Failure");
    }

    public void setContactFriendToInviteList(Friend friend, String friendStatus) {

        if (friendStatus != null) {
            friend.setFriendStatus(friendStatus);
        } else {
            if (OutboundContactStatusChk(friend.getUserID()))
                friend.setFriendStatus(OutbndWaiting);
            else if (InboundContactStatusChk(friend.getUserID()))
                friend.setFriendStatus(InbndWaiting);
            else
                friend.setFriendStatus(No);
        }

        boolean checkFriend = false;

        for (Friend friend1 : ContactFriendList.getInstance().getContactFriendList()) {
            if (friend1.getUserID().equals(friend.getUserID())) {
                checkFriend = true;
                break;
            }
        }

        if (!checkFriend){
            ContactFriendList.getInstance().addFriend(friend);
            personRecyclerView.scrollToPosition(ContactFriendList.getInstance().getContactFriendList().size() - 1);
            //inviteContactFriendListAdapter.notifyDataSetChanged();
            inviteContactFriendListAdapter.notifyItemInserted(ContactFriendList.getInstance().getContactFriendList().size() - 1);
        }
    }

    public boolean OutboundContactStatusChk(String fbUserID) {

        boolean status = false;

        for (String uID : FBGetInviteContactOutbounds.getInstance().getOutboundList()) {
            if (uID.equals(fbUserID)) {
                status = true;
                break;
            }
        }
        return status;
    }

    public boolean InboundContactStatusChk(String userFBId) {

        boolean status = false;

        if (FBGetInviteContactInbounds.getInstance() == null)
            return status;

        for (String uID : FBGetInviteContactInbounds.getInstance().getInboundList()) {
            if (uID.equals(userFBId)) {
                status = true;
                break;
            }
        }
        return status;
    }

    public Friend checkFriendByFireUId(String FirebaseUID) {

        Friend tempFriend = null;

        for (Friend friend : firebaseGetFriendsInstance.getFriendList()) {
            if (friend.getUserID().equals(FirebaseUID)) {
                tempFriend = friend;
                break;
            }
        }

        return tempFriend;
    }

}
