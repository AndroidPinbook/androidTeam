package uur.com.pinbook.Controller;

import android.util.Log;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

import uur.com.pinbook.JavaFiles.User;

import static uur.com.pinbook.JavaFiles.ConstValues.*;

public class FirebaseUserAdapter {

    public static DatabaseReference mDbref;

    public static void saveUserInfo(User user){

        Map<String, String> values = new HashMap<>();

        String userId = user.getUserId();

        mDbref = FirebaseDatabase.getInstance().getReference().child(Users);

        values.put(email, user.getEmail());
        setValuesToCloud(userId, values);

        values.put(gender, user.getGender());
        setValuesToCloud(userId, values);

        values.put(userName, user.getUsername());
        setValuesToCloud(userId, values);

        values.put(name, user.getName());
        setValuesToCloud(userId, values);

        values.put(surname, user.getSurname());
        setValuesToCloud(userId, values);

        values.put(mobilePhone, user.getPhoneNum());
        setValuesToCloud(userId, values);

        values.put(birthday, user.getBirthdate());
        setValuesToCloud(userId, values);

        values.put(profilePictureUrl, user.getProfilePicSrc());
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
}
