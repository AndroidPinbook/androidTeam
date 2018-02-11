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
    Map<String, Group> groupListMap = Collections.synchronizedMap(new WeakHashMap<String, Group>());

    public static FirebaseGetGroups FBGetGroupsInstance = null;

    public static FirebaseGetGroups getInstance(String userId){

        if(FBGetGroupsInstance == null)
            FBGetGroupsInstance = new FirebaseGetGroups(userId);

        return FBGetGroupsInstance;
    }

    public static void setInstance(FirebaseGetGroups instance) {
        FBGetGroupsInstance = instance;
    }

    public Map<String, Group> getGroupListMap() {
        return FBGetGroupsInstance.groupListMap;
    }

    public void setGroupListMap(HashMap<String, Group> groupListMap) {
        this.groupListMap = groupListMap;
    }

    public FirebaseGetGroups(String userID){
        this.userId = userID;
        fillGroupList();
    }

    public void addGroupToList(Group group){
        groupListMap.put(group.getGroupID(), group);
    }

    public void removeGroupFromList(String groupID){
        this.groupListMap.remove(groupID);
    }

    public int getListSize(){
        return this.groupListMap.size();
    }

    private void fillGroupList() {

        //groupListMap = new HashMap<String, Group>();

        DatabaseReference mDbrefFriendList = FirebaseDatabase.getInstance().getReference(UserGroups).child(userId);

        //UserGroups altindan groupID bilgileri okunur
        ValueEventListener valueEventListenerForFriendList = mDbrefFriendList.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                //if(groupList != null) groupList.clear();

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
