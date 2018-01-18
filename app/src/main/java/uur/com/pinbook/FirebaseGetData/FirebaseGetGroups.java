package uur.com.pinbook.FirebaseGetData;

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
import uur.com.pinbook.JavaFiles.Group;

import static uur.com.pinbook.ConstantsModel.FirebaseConstant.Friends;
import static uur.com.pinbook.ConstantsModel.FirebaseConstant.Users;
import static uur.com.pinbook.ConstantsModel.FirebaseConstant.name;
import static uur.com.pinbook.ConstantsModel.FirebaseConstant.profilePictureUrl;
import static uur.com.pinbook.ConstantsModel.FirebaseConstant.surname;

/**
 * Created by mac on 18.01.2018.
 */

public class FirebaseGetGroups {

    String userId;
    static ValueEventListener valueEventListenerForDetails;
    static ValueEventListener valueEventListenerForFriendList;

    private static FirebaseGetGroups instance = null;

    ArrayList<Group> groupList;

    public static FirebaseGetGroups getInstance(String userId){

        if(instance == null)
            instance = new FirebaseGetGroups(userId);

        return instance;
    }

    public ArrayList<Group> getGroupList() {
        return groupList;
    }

    public void setGroupList(ArrayList<Group> groupList) {
        this.groupList = groupList;
    }

    public FirebaseGetGroups(String userID){

        this.userId = userID;
        fillFriendList();
    }

    public int getListSize(){
        return  groupList.size();
    }

    private void fillGroupList() {

        friendList = new ArrayList<Friend>();

        DatabaseReference mDbrefFriendList = FirebaseDatabase.getInstance().getReference(Friends).child(userId);

        valueEventListenerForFriendList = mDbrefFriendList.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(friendList != null)
                    friendList.clear();

                for(DataSnapshot friendsSnapshot: dataSnapshot.getChildren()){

                    final Friend friend = new Friend();
                    String friendUserID = friendsSnapshot.getKey();
                    friend.setUserID(friendUserID);

                    final DatabaseReference mDbrefDetails = FirebaseDatabase.getInstance().getReference(Users).child(friendUserID);

                    valueEventListenerForDetails = mDbrefDetails.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            if(dataSnapshot.getValue() != null){

                                Map<String , String> map = new HashMap<String, String>();

                                map = (Map) dataSnapshot.getValue();

                                String nameSurname = map.get(name) + " " + map.get(surname);
                                friend.setNameSurname(nameSurname);

                                friend.setProfilePicSrc(map.get(profilePictureUrl));

                                Log.i("Info", "  >>nameSurname:" + nameSurname);
                                Log.i("Info", "  >>profilePictureUrl:" + map.get(profilePictureUrl));

                                friendList.add(friend);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                            Log.i("Info", "onCancelled1 error:" + databaseError.toString());
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

                Log.i("Info", "onCancelled2 error:" + databaseError.toString());
            }
        });
    }
}
