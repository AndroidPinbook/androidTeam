package uur.com.pinbook.FirebaseGetData;

import android.util.Log;

import com.google.firebase.database.ChildEventListener;
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
    String prevGroupID = " ";
    static ValueEventListener valueEventListenerForDetails;
    static ValueEventListener valueEventListenerForFriendList;
    static ValueEventListener valueEventListenerForUsers;

    public static FirebaseGetGroups instance = null;

    //ArrayList<Group> groupList;
    HashMap<String, Group> groupListMap = new HashMap<String, Group>();

    public static FirebaseGetGroups getInstance(String userId){

        if(instance == null)
            instance = new FirebaseGetGroups(userId);

        return instance;
    }

    public static void setInstance(FirebaseGetGroups instance) {
        FirebaseGetGroups.instance = instance;
    }

    public HashMap<String, Group> getGroupListMap() {
        return groupListMap;
    }

    public void setGroupListMap(HashMap<String, Group> groupListMap) {
        this.groupListMap = groupListMap;
    }

    //public ArrayList<Group> getGroupList() {
    //    return instance.groupList;
    //}

    //public void setGroupList(ArrayList<Group> groupList) {
    //    instance.groupList = groupList;
    //}

    public FirebaseGetGroups(String userID){

        this.userId = userID;
        fillGroupList();
    }

    public void addGroupToList(Group group){
        //instance.groupList.add(group);
        instance.groupListMap.put(group.getGroupID(), group);
    }

  /*  public void addGroupWithCheck(Group group){
        boolean recFound = false;
        if(instance.getListSize() != 0) {
            for (Group grp : instance.getGroupList()) {
                if (grp.getGroupID().equals(group.getGroupID()))
                    recFound = true;
            }
        }

        if(!recFound) instance.groupList.add(group);
    }*/

    public void removeGroupFromList(String groupID){

        instance.groupListMap.remove(groupID);

      /*  int index = 0;
        for(Group group : instance.groupList){
            if(groupID.equals(group.getGroupID())){
                instance.groupList.remove(index);
                break;
            }
            index ++;
        }*/
    }

    public int getListSize(){

        return instance.groupListMap.size();
        //return  instance.groupList.size();
    }

    private void fillGroupList() {

        //groupList = new ArrayList<Group>();

        DatabaseReference mDbrefFriendList = FirebaseDatabase.getInstance().getReference(UserGroups).child(userId);

        //UserGroups altindan groupID bilgileri okunur
        valueEventListenerForFriendList = mDbrefFriendList.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                //if(groupList != null) groupList.clear();

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
                                    .child(groupID).child(UserList);



                            valueEventListenerForUsers = mDbrefDetailsExt.addChildEventListener(new ChildEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {

                                    ArrayList<String> userIDList = new ArrayList<String>();

                                    for(DataSnapshot userSnapshot : dataSnapshot.getChildren()){
                                        String userID = userSnapshot.getKey();
                                        userIDList.add(userID);

                                    }
                                    group.setUserIDList(userIDList);

                                    //addGroupWithCheck(group);

                                    //if(!prevGroupID.equals(groupID))
                                        //instance.groupList.add(group);

                                    instance.groupListMap.put(group.getGroupID(), group);


                                    //prevGroupID = groupID;
                                }

                                @Override
                                public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                                }

                                @Override
                                public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                                }

                                @Override
                                public void onChildRemoved(DataSnapshot dataSnapshot) {

                                }

                                @Override
                                public void onChildMoved(DataSnapshot dataSnapshot, String s) {

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

        //Log.i("Info", "groupList:" + groupList);
    }
}
