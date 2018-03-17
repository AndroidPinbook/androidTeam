package uur.com.pinbook.FirebaseGetData;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import static uur.com.pinbook.ConstantsModel.FirebaseConstant.*;

public class FBGetInviteContactInbounds {

    static ValueEventListener valueEventListenerForInboundList;

    private static FBGetInviteContactInbounds FBGetInviteContactInboundsIns = null;

    ArrayList<String> inboundList;

    public static FBGetInviteContactInbounds getInstance(){

        if(FBGetInviteContactInboundsIns == null)
            FBGetInviteContactInboundsIns = new FBGetInviteContactInbounds();

        return FBGetInviteContactInboundsIns;
    }

    public static void setInstance(FBGetInviteContactInbounds instance) {
        FBGetInviteContactInboundsIns = instance;
    }

    public ArrayList<String> getInboundList() {
        return inboundList;
    }

    public FBGetInviteContactInbounds(){
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

        DatabaseReference mDbrefFriendList = FirebaseDatabase.getInstance().getReference(InviteContactInbound).
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
