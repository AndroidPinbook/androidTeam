package uur.com.pinbook.Controller;

import android.util.Log;

import com.firebase.client.Firebase;
import com.firebase.client.Query;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

import uur.com.pinbook.JavaFiles.User;

/**
 * Created by mac on 5.12.2017.
 */

public class FirebaseUserAdapter {

    public static DatabaseReference mDbref;
    public static String tag_users = "users";

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

    /*public boolean findFBUserWithUserId(final String userId){

        Log.i("Info", "findFBUserWithUserId starts");

        mDbref = FirebaseDatabase.getInstance().getReference();
        final DatabaseReference users = mDbref.child(tag_users).child(userId);

        users.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                Log.i("Info","  >>addValueEventListener onDataChange:" + snapshot.getValue());

                if(snapshot != null)
                    userIsDetected = true;
                else
                    userIsDetected = false;

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return userIsDetected;
    }*/

}
