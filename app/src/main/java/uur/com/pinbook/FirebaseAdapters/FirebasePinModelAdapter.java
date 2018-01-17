package uur.com.pinbook.FirebaseAdapters;

import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

import uur.com.pinbook.Adapters.CustomDialogAdapter;
import uur.com.pinbook.JavaFiles.PinModels;
import static uur.com.pinbook.ConstantsModel.FirebaseConstant.*;


/**
 * Created by mac on 11.01.2018.
 */

public class FirebasePinModelAdapter extends AppCompatActivity{

    PinModels pinModels;

    private DatabaseReference mDbref;

    public PinModels getPinModels() {
        return pinModels;
    }

    public void setPinModels(PinModels pinModels) {
        this.pinModels = pinModels;
    }

    public void savePinModel(){

        Map<String, String> values = new HashMap<>();

        mDbref = FirebaseDatabase.getInstance().getReference().child(PinModels).child(pinModels.getLocationID());

        values.put(notified, pinModels.getNotifiedFlag());
        setValuesToCloud(values);

        values.put(owner, pinModels.getOwner());
        setValuesToCloud(values);

        values.put(property, pinModels.getProperty());
        setValuesToCloud(values);

        String pinProperty = pinModels.getProperty();

        switch (pinProperty){

            case propFriends:
                values.put(toWhom, toWhomAll);
                setValuesToCloud(values);
                break;

            case propOnlyMe:
                values.put(toWhom, pinModels.getOwner());
                setValuesToCloud(values);
                break;

            case propPersons:
                break;

            case propGroups:
                break;

            default:
                CustomDialogAdapter.showErrorDialog(this, "Pinin kime birakilacagi bulunamadi !");
                break;
        }


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
