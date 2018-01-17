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
    static ValueEventListener valueEventListenerForDetails;
    static ValueEventListener valueEventListenerForFriendList;

    private static FirebaseGetFriends instance = null;

    ArrayList<Friend> friendList;

    public static FirebaseGetFriends getInstance(String userId){

        if(instance == null)
            instance = new FirebaseGetFriends(userId);

        return instance;
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
