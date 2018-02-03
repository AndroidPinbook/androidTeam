package uur.com.pinbook.FirebaseAdapters;

import android.util.Log;

import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

import uur.com.pinbook.JavaFiles.Group;

import static uur.com.pinbook.ConstantsModel.FirebaseConstant.*;


public class FirebaseAddGroupAdapter {

    Group group;
    public static DatabaseReference mDbref;
    String groupID;

    public String getGroupID() {
        return groupID;
    }

    public FirebaseAddGroupAdapter(Group group){

        this.group = group;
        saveGroupToFB();
    }

    private void saveGroupToFB() {

        mDbref = null;
        mDbref = FirebaseDatabase.getInstance().getReference();

        this.groupID = mDbref.child(Groups).push().getKey();

        Map<String, String> values = new HashMap<>();

        values.put(Admin, group.getAdminID());
        values.put(GroupName, group.getGroupName());

        addGroupItems(values);
        addGroupUserIDs();

        for(int i=0; i < group.getUserIDList().size(); i++)
            addGroupToUserGroup(group.getUserIDList().get(i));

        addGroupToUserGroup(group.getAdminID());
    }

    public void addGroupItems(Map values) {

        Log.i("Info", "addGroupItems starts");

        mDbref.child(Groups).child(getGroupID()).setValue(values, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                Log.i("Info", "     >>databaseError:" + databaseError);
            }
        });
    }

    public void savePictureUrl(String picUrl){

        mDbref = FirebaseDatabase.getInstance().getReference().child(Groups).child(getGroupID());

        Map<String, Object> values = new HashMap<>();

        values.put(GroupPictureUrl, picUrl);

        mDbref.updateChildren(values);
    }

    public void addGroupUserIDs(){

        for(int i=0; i< group.getUserIDList().size(); i++){

            String userIDItem = group.getUserIDList().get(i);
            setUserIDToUserList(userIDItem);
        }

        setUserIDToUserList(group.getAdminID());
    }

    public void setUserIDToUserList(String addedUserID){

        mDbref.child(Groups)
                .child(getGroupID())
                .child(UserList)
                .child(addedUserID).setValue(" ", new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                Log.i("Info", "     >>databaseError:" + databaseError);
            }
        });
    }

    public void addGroupToUserGroup(String userID){

        mDbref = FirebaseDatabase.getInstance().getReference().child(UserGroups).child(userID);

        mDbref.child(getGroupID()).setValue(" ", new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                Log.i("Info", "     >>databaseError:" + databaseError);
            }
        });
    }
}
