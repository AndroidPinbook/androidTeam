package uur.com.pinbook.FirebaseGetData;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import static uur.com.pinbook.ConstantsModel.FirebaseConstant.*;

public class FBGetInviteFacebookInbounds {

    static ValueEventListener valueEventListenerForInboundList;

    private static FBGetInviteFacebookInbounds FBGetInviteFacebookInboundsIns = null;

    ArrayList<String> inboundList;

    public static FBGetInviteFacebookInbounds getInstance(){

        if(FBGetInviteFacebookInboundsIns == null)
            FBGetInviteFacebookInboundsIns = new FBGetInviteFacebookInbounds();

        return FBGetInviteFacebookInboundsIns;
    }

    public static FBGetInviteFacebookInbounds getFBGetInviteInboundsInstance() {
        return FBGetInviteFacebookInboundsIns;
    }

    public static void setInstance(FBGetInviteFacebookInbounds instance) {
        FBGetInviteFacebookInboundsIns = instance;
    }

    public ArrayList<String> getInboundList() {
        return inboundList;
    }

    public FBGetInviteFacebookInbounds(){
        fillInboundList();
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

        inboundList = new ArrayList<>();

        DatabaseReference mDbrefFriendList = FirebaseDatabase.getInstance().getReference(InviteFacebookInbound).
                child(FirebaseGetAccountHolder.getUserID());

        valueEventListenerForInboundList = mDbrefFriendList.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for(DataSnapshot inboundSnapshot: dataSnapshot.getChildren()){

                    if(inboundSnapshot.getValue() != null) {

                        String friendId = inboundSnapshot.getKey();
                        addFriendIdToList(friendId);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
