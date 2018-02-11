package uur.com.pinbook.FirebaseGetData;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import uur.com.pinbook.JavaFiles.Friend;
import uur.com.pinbook.JavaFiles.Group;

import static uur.com.pinbook.ConstantsModel.FirebaseConstant.*;

/**
 * Created by mac on 18.01.2018.
 */

public class FirebaseGetOneGroup {

    Group group;
    String groupID;

    public FirebaseGetOneGroup(String groupID){

        this.groupID = groupID;
    }

    private void fillGroup() {

        final DatabaseReference mDbrefDetails = FirebaseDatabase.getInstance().getReference(Groups).child(groupID);

        ValueEventListener valueEventListenerForDetails = mDbrefDetails.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.getValue() != null) {

                    Map<String, Object> map = new HashMap<String, Object>();
                    map = (Map) dataSnapshot.getValue();

                    group.setAdminID((String) map.get(Admin));
                    group.setGroupName((String) map.get(GroupName));
                    group.setPictureUrl((String) map.get(GroupPictureUrl));

                    Map<String, Object> userList = new HashMap<String, Object>();
                    userList = (Map<String, Object>) map.get(UserList);

                    ArrayList<Friend> friendArrayList = new ArrayList<Friend>();
                    Iterator it = userList.entrySet().iterator();

                    while (it.hasNext()) {
                        Map.Entry pair = (Map.Entry) it.next();
                        String key = (String) pair.getKey();

                        Friend friend = new Friend();

                        Map<String, String> userDetail = new HashMap<String, String>();
                        userDetail = (Map<String, String>) pair.getValue();

                        friend.setUserID((String) pair.getKey());
                        friend.setProfilePicSrc(userDetail.get(profilePictureUrl));
                        friend.setNameSurname(userDetail.get(nameSurname));

                        friendArrayList.add(friend);
                        it.remove();
                    }

                    group.setFriendList(friendArrayList);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.i("Info", "onCancelled1 error:" + databaseError.toString());
            }
        });
    }
}
