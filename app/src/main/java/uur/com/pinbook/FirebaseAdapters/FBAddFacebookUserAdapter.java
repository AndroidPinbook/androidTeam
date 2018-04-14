package uur.com.pinbook.FirebaseAdapters;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableResult;

import org.json.JSONObject;
import uur.com.pinbook.FirebaseGetData.FirebaseGetAccountHolder;
import static uur.com.pinbook.ConstantsModel.FirebaseConstant.*;
import static uur.com.pinbook.ConstantsModel.FirebaseFunctionsConstant.*;

public class FBAddFacebookUserAdapter {

    public static void saveFacebookUser(String providerID){

        JSONObject faceJsonMain = null;

        try {
            JSONObject faceUserDtl = new JSONObject();

            faceUserDtl.put(fbUserId, FirebaseGetAccountHolder.getUserID());

            faceJsonMain = new JSONObject();
            faceJsonMain.put(providerID, faceUserDtl);

        }catch (Exception e){
            e.printStackTrace();
        }

        FirebaseFunctions.getInstance()
                .getHttpsCallable(addFacebookUser)
                .call(faceJsonMain)
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.i("Info", "Function call failure:" + e.toString());
                    }
                }).addOnSuccessListener(new OnSuccessListener<HttpsCallableResult>() {
            @Override
            public void onSuccess(HttpsCallableResult httpsCallableResult) {
                Log.i("Info", "Function call is ok");
            }
        });
    }
}
