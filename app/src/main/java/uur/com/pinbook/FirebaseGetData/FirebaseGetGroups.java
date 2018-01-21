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

import uur.com.pinbook.JavaFiles.Group;

import static uur.com.pinbook.ConstantsModel.FirebaseConstant.*;

/**
 * Created by mac on 18.01.2018.
 */

public class FirebaseGetGroups {

    String userId;
    String groupID;
    static ValueEventListener valueEventListenerForDetails;
    static ValueEventListener valueEventListenerForFriendList;
    static ValueEventListener valueEventListenerForUsers;

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
        fillGroupList();
    }

    public int getListSize(){
        return  groupList.size();
    }

    private void fillGroupList() {

        groupList = new ArrayList<Group>();

        DatabaseReference mDbrefFriendList = FirebaseDatabase.getInstance().getReference(UserGroups).child(userId);

        //UserGroups altindan groupID bilgileri okunur
        valueEventListenerForFriendList = mDbrefFriendList.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(groupList != null) groupList.clear();

                for(DataSnapshot groupSnapShot: dataSnapshot.getChildren()){

                    final Group group = new Group();
                    groupID = groupSnapShot.getKey();
                    group.setGroupID(groupID);

                    final DatabaseReference mDbrefDetails = FirebaseDatabase.getInstance().getReference(Groups).child(groupID);

                    valueEventListenerForDetails = mDbrefDetails.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            if(dataSnapshot.getValue() != null){

                                Map<String , String> map = new HashMap<String, String>();
                                map = (Map) dataSnapshot.getValue();

                                group.setAdminID(map.get(Admin));
                                group.setGroupName(map.get(GroupName));
                                group.setPictureUrl(map.get(GroupPictureUrl));
                            }

                            final DatabaseReference mDbrefDetailsExt = FirebaseDatabase.getInstance().getReference(Groups)
                                    .child(groupID).child(Users);

                            valueEventListenerForUsers = mDbrefDetailsExt.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {

                                    ArrayList<String> userList = new ArrayList<String>();

                                    for(DataSnapshot userSnapshot : dataSnapshot.getChildren()){
                                        String userID = userSnapshot.getKey();
                                        userList.add(userID);

                                    }
                                    group.setUserList(userList);
                                    groupList.add(group);
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {}
                            });
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
