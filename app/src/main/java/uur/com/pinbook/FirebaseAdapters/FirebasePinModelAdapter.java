package uur.com.pinbook.FirebaseAdapters;

import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import uur.com.pinbook.Adapters.CustomDialogAdapter;
import uur.com.pinbook.JavaFiles.Friend;
import uur.com.pinbook.JavaFiles.Group;
import uur.com.pinbook.JavaFiles.PinModels;
import static uur.com.pinbook.ConstantsModel.FirebaseConstant.*;


/**
 * Created by mac on 11.01.2018.
 */

public class FirebasePinModelAdapter extends AppCompatActivity{

    PinModels pinModels;
    Map<String, String> values;

    private DatabaseReference mDbref;

    public PinModels getPinModels() {
        return pinModels;
    }

    public void setPinModels(PinModels pinModels) {
        this.pinModels = pinModels;
    }

    public void savePinModel(){

        values = new HashMap<>();
        mDbref = FirebaseDatabase.getInstance().getReference().child(PinModels).child(pinModels.getLocationID());

        values.put(notified, pinModels.getNotifiedFlag());
        values.put(owner, pinModels.getOwner());
        values.put(property, pinModels.getProperty());

        String pinProperty = pinModels.getProperty();

        switch (pinProperty){

            case propFriends:
                values.put(toWhom, toWhomAll);
                break;

            case propOnlyMe:
                values.put(toWhom, pinModels.getOwner());
                break;

            case propPersons:
                fillFriendArray();
                break;

            case propGroups:
                fillGroupArray();
                break;

            default:
                CustomDialogAdapter.showErrorDialog(this, "Pinin kime birakilacagi bulunamadi !");
                break;
        }

        setValuesToCloud(values);
    }

    public void fillFriendArray(){
        String[] friendIDs = new String[pinModels.getFriendList().size()];

        int i = 0;
        for(Friend friend: pinModels.getFriendList()){
            if(friend.getUserID() != null && !friend.getUserID().equals(" ")) {
                friendIDs[i] = friend.getUserID();
                i++;
            }
        }
        Log.i("Info", "friendIDs.toString():" + Arrays.toString(friendIDs));

        values.put(toWhom, Arrays.toString(friendIDs));
    }

    public void fillGroupArray(){
        String[] groupIDs = new String[pinModels.getGroupList().size()];
        int i = 0;

        for(Group group: pinModels.getGroupList()){
            if(group.getGroupID() != null && !group.getGroupID().equals(" ")) {
                groupIDs[i] = group.getGroupID();
                i++;
            }
        }

        values.put(toWhom, Arrays.toString(groupIDs));
    }

    public  void setValuesToCloud(Map<String, String> values){

        try {
            mDbref.setValue(values, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                    Log.i("Info","databaseError:" + databaseError);
                }
            });
        } catch (Exception e) {
            Log.i("Info","  >>setValuesToCloud error:" + e.toString());
        }
    }
}
