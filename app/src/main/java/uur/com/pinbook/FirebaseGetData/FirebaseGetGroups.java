package uur.com.pinbook.FirebaseGetData;

import android.util.Log;
import android.widget.ImageView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.WeakHashMap;

import uur.com.pinbook.JavaFiles.Friend;
import uur.com.pinbook.JavaFiles.Group;

import static uur.com.pinbook.ConstantsModel.FirebaseConstant.*;

public class FirebaseGetGroups {

    String userId;
    ArrayList<Group> groupArrayList;

    public static FirebaseGetGroups FBGetGroupsInstance = null;

    public static FirebaseGetGroups getInstance(String userId){

        if(FBGetGroupsInstance == null)
            FBGetGroupsInstance = new FirebaseGetGroups(userId);

        return FBGetGroupsInstance;
    }

    public static void setInstance(FirebaseGetGroups instance) {
        FBGetGroupsInstance = instance;
    }

    public ArrayList<Group> getGroupArrayList() {
        return groupArrayList;
    }

    public void setGroupArrayList(ArrayList<Group> groupArrayList) {
        this.groupArrayList = groupArrayList;
    }

    public FirebaseGetGroups(String userID){
        this.userId = userID;
        fillGroupList();
    }

    public void addGroupToList(Group group){

        boolean groupFounded = false;

        for(Group group1 : groupArrayList){
            if(group1.getGroupID().equals(group.getGroupID())) {
                groupFounded = true;
                break;
            }
        }

        if(!groupFounded)
            groupArrayList.add(group);
    }

    public void removeGroupFromList(String groupID){
        int index = 0;
        for(Group group : groupArrayList){
            if(group.getGroupID().equals(groupID)) {
                groupArrayList.remove(index);
                break;
            }
            index++;
        }
    }

    public int getListSize(){
        return groupArrayList.size();
    }

    private void fillGroupList() {

        groupArrayList = new ArrayList<Group>();

        DatabaseReference mDbrefFriendList = FirebaseDatabase.getInstance().getReference(UserGroups).child(userId);

        //UserGroups altindan groupID bilgileri okunur
        ValueEventListener valueEventListenerForFriendList = mDbrefFriendList.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for(DataSnapshot groupSnapShot: dataSnapshot.getChildren()){

                    final Group group = new Group();
                    String groupID = groupSnapShot.getKey();

                    group.setGroupID(groupID);

                    final DatabaseReference mDbrefDetails = FirebaseDatabase.getInstance().getReference(Groups).child(groupID);

                    ValueEventListener valueEventListenerForDetails = mDbrefDetails.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            if(dataSnapshot.getValue() != null){

                                Map<String , Object> map = new HashMap<String, Object>();
                                map = (Map) dataSnapshot.getValue();

                                group.setAdminID((String) map.get(Admin));
                                group.setGroupName((String) map.get(GroupName));
                                group.setPictureUrl((String) map.get(GroupPictureUrl));

                                Map<String, Object> userList = new HashMap<String, Object>();
                                userList = (Map<String, Object>) map.get(UserList);

                                Log.i("Info", "xx");

                                ArrayList<Friend> friendArrayList = new ArrayList<Friend>();
                                Iterator it = userList.entrySet().iterator();

                                while (it.hasNext()) {
                                    Map.Entry pair = (Map.Entry) it.next();
                                    String key = (String) pair.getKey();

                                    Friend friend = new Friend();

                                    Map<String, String > userDetail = new HashMap<String, String>();
                                    userDetail = (Map<String, String>) pair.getValue();

                                    friend.setUserID((String) pair.getKey());
                                    friend.setProfilePicSrc(userDetail.get(profilePictureUrl));
                                    friend.setNameSurname(userDetail.get(nameSurname));

                                    friendArrayList.add(friend);
                                    it.remove();
                                }

                                group.setFriendList(friendArrayList);
                                addGroupToList(group);
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
