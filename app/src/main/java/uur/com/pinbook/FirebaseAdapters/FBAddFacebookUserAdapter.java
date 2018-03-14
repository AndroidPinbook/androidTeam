package uur.com.pinbook.FirebaseAdapters;

import android.util.Log;

import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import uur.com.pinbook.FirebaseGetData.FirebaseGetAccountHolder;
import static uur.com.pinbook.ConstantsModel.FirebaseConstant.*;

public class FBAddFacebookUserAdapter {

    public static void saveFacebookUser(String providerID){

        Map<String, String> values = new HashMap<>();

        Log.i("Info","FirebaseGetAccountHolder.getUserID():" + FirebaseGetAccountHolder.getUserID());

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child(FacebookUsers).
                child(providerID);

        values.put(fbUserId, FirebaseGetAccountHolder.getUserID());

        databaseReference.setValue(values, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                Log.i("Info","databaseError:" + databaseError);
            }
        });
    }
}
