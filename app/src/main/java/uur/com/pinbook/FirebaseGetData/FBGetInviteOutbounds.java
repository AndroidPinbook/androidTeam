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

public class FBGetInviteOutbounds {

    //String userId;
    static ValueEventListener valueEventListenerForDetails;
    static ValueEventListener valueEventListenerForOutboundList;

    private static FBGetInviteOutbounds FBGetInviteOutboundsInstance = null;

    ArrayList<String> outboundList;

    public static FBGetInviteOutbounds getInstance(String userId){

        if(FBGetInviteOutboundsInstance == null)
            FBGetInviteOutboundsInstance = new FBGetInviteOutbounds(userId);

        return FBGetInviteOutboundsInstance;
    }

    public static FBGetInviteOutbounds getFBGetInviteOutboundsInstance() {
        return FBGetInviteOutboundsInstance;
    }

    public static void setFBGetOutboundsInstance(FBGetInviteOutbounds FBGetInviteOutboundsInstance) {
        FBGetInviteOutbounds.FBGetInviteOutboundsInstance = FBGetInviteOutboundsInstance;
    }

    public static void setInstance(FBGetInviteOutbounds instance) {
        FBGetInviteOutboundsInstance = instance;
    }

    public ArrayList<String> getOutboundList() {
        return outboundList;
    }

    public void setOutboundList(ArrayList<String> outboundList) {
        this.outboundList = outboundList;
    }

    public FBGetInviteOutbounds(String userID){

        //this.userId = userID;
        fillOutboundList();
    }

    public int getListSize(){
        return  outboundList.size();
    }

    public void addFriendIdToList(String id){

        boolean idCheck = false;

        for(String friendId:outboundList){

            if(friendId.equals(id)){
                idCheck = true;
                break;
            }
        }

        if(!idCheck)
            outboundList.add(id);

    }

    private void fillOutboundList() {

        outboundList = new ArrayList<String>();

        DatabaseReference mDbrefFriendList = FirebaseDatabase.getInstance().getReference(InviteOutbound).child(FirebaseGetAccountHolder.getUserID());

        valueEventListenerForOutboundList = mDbrefFriendList.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(outboundList != null) outboundList.clear();

                for(DataSnapshot outboundSnapshot: dataSnapshot.getChildren()){

                    if(outboundSnapshot.getValue() != null) {

                        String friendId = outboundSnapshot.getKey();
                        addFriendIdToList(friendId);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.i("Info", "onCancelled2 error:" + databaseError.toString());
            }
        });
    }
}
