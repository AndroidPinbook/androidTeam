package uur.com.pinbook.Controller;

import android.support.annotation.NonNull;
import android.util.Log;

import com.firebase.client.Firebase;
import com.firebase.client.Query;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.ProviderQueryResult;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

import uur.com.pinbook.Activities.RegisterPageActivity;
import uur.com.pinbook.JavaFiles.User;

/**
 * Created by mac on 5.12.2017.
 */

public class FirebaseUserAdapter {

    public static DatabaseReference mDbref;
    public static String tag_users = "users";

    FirebaseAuth mAuth = null;

    public static boolean userIsDetected = false;

    public static void saveUserInfo(User user){

        Map<String, String> values = new HashMap<>();

        String userId = user.getUserId();

        mDbref = FirebaseDatabase.getInstance().getReference().child(tag_users);

        values.put("email", user.getEmail());
        setValuesToCloud(userId, values);

        values.put("gender", user.getGender());
        setValuesToCloud(userId, values);

        values.put("username", user.getUsername());
        setValuesToCloud(userId, values);

        values.put("name", user.getName());
        setValuesToCloud(userId, values);

        values.put("surname", user.getSurname());
        setValuesToCloud(userId, values);

        values.put("phone", user.getPhoneNum());
        setValuesToCloud(userId, values);

        values.put("birthdate", user.getBirthdate());
        setValuesToCloud(userId, values);

        values.put("profImageSrc", user.getProfilePicSrc());
        setValuesToCloud(userId, values);
    }

    public static void setValuesToCloud(String userId, Map<String, String> values){

        try {
            mDbref.child(userId).setValue(values, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                    Log.i("Info","databaseError:" + databaseError);
                }
            });
        } catch (Exception e) {
            Log.i("Info","  >>setValuesToCloud error:" + e.toString());
        }
    }

   /* public static boolean emailIsRegistered(String email) {

        userIsDetected = false;

        FirebaseAuth auth = FirebaseAuth.getInstance();

        auth.fetchProvidersForEmail(email).addOnCompleteListener(new OnCompleteListener<ProviderQueryResult>() {

            @Override
            public void onComplete(@NonNull Task<ProviderQueryResult> task) {

                Log.i("Info","  >>emailIsRegistered task:" + task.getResult());
                Log.i("Info","  >>emailIsRegistered task size:" + task.getResult().getProviders().size());

                if (task.getResult().getProviders().size() > 0) {

                    userIsDetected = true;
                }
            }
        });

        return userIsDetected;
    }*/
}
