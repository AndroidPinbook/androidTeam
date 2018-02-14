package uur.com.pinbook.FirebaseAdapters;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import uur.com.pinbook.FirebaseGetData.FirebaseGetAccountHolder;
import uur.com.pinbook.JavaFiles.Friend;
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
            deleteGroupImageFromStorage();
        }else if(type == exitGroup){

            deleteUserFromGroup();
        }
    }

    public String getFbUserID() {
        return FirebaseGetAccountHolder.getUserID();
    }

    private void deleteGroupImageFromStorage() {

        if(group.getPictureUrl() == null)
            return;

        if(group.getPictureUrl().equals(""))
            return;

        StorageReference photoRef = FirebaseStorage.getInstance().getReferenceFromUrl(group.getPictureUrl());

        photoRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                // File deleted successfully
                Log.i("Info", "onSuccess: deleted file");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Uh-oh, an error occurred!
                Log.i("Info", "onFailure: did not delete file");
            }
        });
    }

    public void deleteFromGroupModel(){
        mDbref = FirebaseDatabase.getInstance().getReference().child(Groups).child(groupID);
        mDbref.removeValue();
    }

    public void deleteUserFromGroup(){
        mDbref = FirebaseDatabase.getInstance().getReference().child(Groups).child(groupID).child(UserList).child(getFbUserID());
        mDbref.removeValue();

        mDbref = FirebaseDatabase.getInstance().getReference().child(UserGroups).child(getFbUserID()).child(groupID);
        mDbref.removeValue();
    }

    public void deleteUsersFromUserGroupsModel(){

        for(Friend friend : group.getFriendList()){
            String userid = friend.getUserID();
            mDbref = FirebaseDatabase.getInstance().getReference().child(UserGroups).child(userid).child(groupID);
            mDbref.removeValue();
        }
    }
}
