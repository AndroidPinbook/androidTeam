package uur.com.pinbook.FirebaseGetData;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import static uur.com.pinbook.ConstantsModel.FirebaseConstant.*;

public class FBGetInviteInbounds {

    //String userId;
    static ValueEventListener valueEventListenerForInboundList;

    private static FBGetInviteInbounds FBGetInviteInboundsInstance = null;

    ArrayList<String> inboundList;

    public static FBGetInviteInbounds getInstance(String userId){

        if(FBGetInviteInboundsInstance == null)
            FBGetInviteInboundsInstance = new FBGetInviteInbounds(userId);

        return FBGetInviteInboundsInstance;
    }

    public static FBGetInviteInbounds getFBGetInviteInboundsInstance() {
        return FBGetInviteInboundsInstance;
    }

    public static void setFBGetinboundsInstance(FBGetInviteInbounds FBGetInviteInboundsInstance) {
        FBGetInviteInbounds.FBGetInviteInboundsInstance = FBGetInviteInboundsInstance;
    }

    public static void setInstance(FBGetInviteInbounds instance) {
        FBGetInviteInboundsInstance = instance;
    }

    public ArrayList<String> getInboundList() {
        return inboundList;
    }

    public void setInboundList(ArrayList<String> inboundList) {
        this.inboundList = inboundList;
    }

    public FBGetInviteInbounds(String userID){

        //this.userId = userID;
        fillInboundList();
    }

    public int getListSize(){
        return  inboundList.size();
    }

    public void addFriendIdToList(String id){

        boolean idCheck = false;

        for(String friendId:inboundList){

            if(friendId.equals(id)){
                idCheck = true;
                break;
            }
        }

        if(!idCheck)
            inboundList.add(id);
    }

    private void fillInboundList() {

        inboundList = new ArrayList<String>();

        DatabaseReference mDbrefFriendList = FirebaseDatabase.getInstance().getReference(InviteInbound).
                child(FirebaseGetAccountHolder.getUserID());

        valueEventListenerForInboundList = mDbrefFriendList.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(inboundList != null) inboundList.clear();

                for(DataSnapshot inboundSnapshot: dataSnapshot.getChildren()){

                    if(inboundSnapshot.getValue() != null) {

                        String friendId = inboundSnapshot.getKey();
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
