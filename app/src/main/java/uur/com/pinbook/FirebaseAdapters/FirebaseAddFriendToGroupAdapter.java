package uur.com.pinbook.FirebaseAdapters;

import android.util.Log;

import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import uur.com.pinbook.JavaFiles.Friend;

import static uur.com.pinbook.ConstantsModel.FirebaseConstant.Groups;
import static uur.com.pinbook.ConstantsModel.FirebaseConstant.UserList;
import static uur.com.pinbook.ConstantsModel.FirebaseConstant.nameSurname;
import static uur.com.pinbook.ConstantsModel.FirebaseConstant.profilePictureUrl;

public class FirebaseAddFriendToGroupAdapter {

    ArrayList<Friend> friendList = new ArrayList<>();
    String groupID;

    public FirebaseAddFriendToGroupAdapter(ArrayList<Friend> friendList, String groupID){
        this.friendList = friendList;
        this.groupID = groupID;
    }

    public void addFriendsToGroup() {

        DatabaseReference databaseReference;

        for(Friend friend:friendList){

            Map<String, String> friendSpecMap = new HashMap<String, String>();

            databaseReference = FirebaseDatabase.getInstance().getReference().child(Groups).
                    child(groupID).child(UserList).child(friend.getUserID());

            friendSpecMap.put(nameSurname, friend.getNameSurname());
            friendSpecMap.put(profilePictureUrl, friend.getProfilePicSrc());

            databaseReference.setValue(friendSpecMap, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                    Log.i("Info", "     >>databaseError:" + databaseError);
                }
            });

        }
    }
}
