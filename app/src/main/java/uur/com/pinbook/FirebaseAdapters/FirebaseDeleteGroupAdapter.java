package uur.com.pinbook.FirebaseAdapters;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import uur.com.pinbook.JavaFiles.Group;

import static uur.com.pinbook.ConstantsModel.FirebaseConstant.*;
import static uur.com.pinbook.ConstantsModel.StringConstant.*;


public class FirebaseDeleteGroupAdapter {

    String groupID;
    String adminID;
    Group group;

    public static DatabaseReference mDbref;

    public FirebaseDeleteGroupAdapter(Group group, String type){

        this.groupID = group.getGroupID();
        this.adminID = group.getAdminID();
        this.group = group;

        if(type == deleteGroup) {
            deleteUsersFromUserGroupsModel();
            deleteFromGroupModel();
        }else if(type == exitGroup){

            deleteAdminFromUserGroupsModel();
        }
    }

    public void deleteFromGroupModel(){
        mDbref = FirebaseDatabase.getInstance().getReference().child(Groups).child(groupID);
        mDbref.removeValue();
    }

    public void deleteAdminFromUserGroupsModel(){
        mDbref = FirebaseDatabase.getInstance().getReference().child(UserGroups).child(adminID).child(groupID);
        mDbref.removeValue();
    }

    public void deleteUsersFromUserGroupsModel(){

        for(String userid : group.getUserIDList()){
            mDbref = FirebaseDatabase.getInstance().getReference().child(UserGroups).child(userid).child(groupID);
            mDbref.removeValue();
        }
    }
}
