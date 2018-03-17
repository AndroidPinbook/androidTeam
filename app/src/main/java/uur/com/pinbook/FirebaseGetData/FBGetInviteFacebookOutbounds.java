package uur.com.pinbook.FirebaseGetData;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static uur.com.pinbook.ConstantsModel.FirebaseConstant.*;

/**
 * Created by mac on 17.01.2018.
 */

public class FBGetInviteFacebookOutbounds {

    static ValueEventListener valueEventListenerForOutboundList;

    private static FBGetInviteFacebookOutbounds FBGetInviteFacebookOutboundsIns = null;

    ArrayList<String> outboundList;

    public static FBGetInviteFacebookOutbounds getInstance(){

        if(FBGetInviteFacebookOutboundsIns == null)
            FBGetInviteFacebookOutboundsIns = new FBGetInviteFacebookOutbounds();

        return FBGetInviteFacebookOutboundsIns;
    }

    public static void setInstance(FBGetInviteFacebookOutbounds instance) {
        FBGetInviteFacebookOutboundsIns = instance;
    }

    public ArrayList<String> getOutboundList() {
        return outboundList;
    }

    public FBGetInviteFacebookOutbounds(){
        fillOutboundList();
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

        outboundList = new ArrayList<>();

        DatabaseReference mDbrefFriendList = FirebaseDatabase.getInstance().getReference(InviteFacebookOutbound).child(FirebaseGetAccountHolder.getUserID());

        valueEventListenerForOutboundList = mDbrefFriendList.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

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
