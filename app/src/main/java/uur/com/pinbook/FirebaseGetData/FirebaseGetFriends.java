package uur.com.pinbook.FirebaseGetData;

import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import uur.com.pinbook.JavaFiles.Friend;

import static uur.com.pinbook.ConstantsModel.FirebaseConstant.*;

/**
 * Created by mac on 17.01.2018.
 */

public class FirebaseGetFriends {

    String userId;
    static ValueEventListener valueEventListenerForFriendList;

    private static FirebaseGetFriends FBGetFriendsInstance = null;

    ArrayList<Friend> friendList;

    public static FirebaseGetFriends getInstance(String userId){

        if(FBGetFriendsInstance == null)
            FBGetFriendsInstance = new FirebaseGetFriends(userId);

        return FBGetFriendsInstance;
    }

    public static FirebaseGetFriends getFBGetFriendsInstance() {
        return FBGetFriendsInstance;
    }

    public static void setInstance(FirebaseGetFriends instance) {
        FBGetFriendsInstance = instance;
    }

    public ArrayList<Friend> getFriendList() {
        return friendList;
    }

    public void setFriendList(ArrayList<Friend> friendList) {
        this.friendList = friendList;
    }

    public FirebaseGetFriends(String userID){

        this.userId = userID;
        fillFriendList();
    }

    public int getListSize(){
        return  friendList.size();
    }

    private void fillFriendList() {

        friendList = new ArrayList<>();

        DatabaseReference mDbrefFriendList = FirebaseDatabase.getInstance().getReference(Friends).child(userId);

        valueEventListenerForFriendList = mDbrefFriendList.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(friendList != null)
                    friendList.clear();

                for(DataSnapshot friendsSnapshot: dataSnapshot.getChildren()){

                    if(friendsSnapshot.getValue() != null) {

                        final Friend friend = new Friend();
                        String friendUserID = friendsSnapshot.getKey();
                        friend.setUserID(friendUserID);

                        Map<String, Object> userList = (Map) dataSnapshot.getValue();
                        Map<String, Object> users = (Map<String, Object>) userList.get(friendUserID);

                        friend.setNameSurname((String) users.get(nameSurname));
                        friend.setProfilePicSrc((String) users.get(profilePictureUrl));
                        friend.setProviderId((String)users.get(providerId));

                        friendList.add(friend);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

                Log.i("Info", "onCancelled2 error:" + databaseError.toString());
            }
        });
    }
}
