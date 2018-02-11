package uur.com.pinbook.FirebaseAdapters;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import static uur.com.pinbook.ConstantsModel.FirebaseConstant.*;

public class FirebaseDeleteFriendAdapter {

    String groupID;
    String userID;

    public static DatabaseReference mDbref;

    public FirebaseDeleteFriendAdapter(String groupID, String userID){

        this.groupID = groupID;
        this.userID = userID;
    }

    public void deleteUserFromGroup(){

        deleteUserFromUserGroup();
        deleteUserFromGroupList();
    }

    private void deleteUserFromUserGroup() {
        mDbref = FirebaseDatabase.getInstance().getReference().child(Groups).child(groupID).child(UserList).child(userID);
        mDbref.removeValue();
    }

    private void deleteUserFromGroupList(){
        mDbref = FirebaseDatabase.getInstance().getReference().child(UserGroups).child(userID).child(groupID);
        mDbref.removeValue();

    }
}
