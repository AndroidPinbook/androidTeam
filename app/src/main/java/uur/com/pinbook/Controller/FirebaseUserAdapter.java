package uur.com.pinbook.Controller;

import android.util.Log;

import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

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

        mDbref.child(userId).setValue(values, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                Log.i("Info","databaseError:" + databaseError);
            }
        });
    }

}
