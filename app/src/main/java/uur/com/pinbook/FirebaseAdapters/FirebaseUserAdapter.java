package uur.com.pinbook.FirebaseAdapters;

import android.util.Log;

import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

import uur.com.pinbook.JavaFiles.User;

import static uur.com.pinbook.ConstantsModel.FirebaseConstant.*;

public class FirebaseUserAdapter {

    public static DatabaseReference mDbref;
    public static String userId;

    public static void saveUserInfo(User user) {

        Map<String, String> values = new HashMap<>();

        userId = user.getUserId();
        mDbref = FirebaseDatabase.getInstance().getReference().child(Users);

        values.put(email, user.getEmail());
        values.put(gender, user.getGender());
        values.put(userName, user.getUsername());
        values.put(name, user.getName());
        values.put(surname, user.getSurname());
        values.put(mobilePhone, user.getPhoneNum().toString());
        values.put(birthday, user.getBirthdate());
        values.put(profilePictureUrl, user.getProfilePicSrc());
        values.put(profilePicMiniUrl, user.getMiniProfPicUrl());
        values.put(providerId, user.getProviderId());
        setValuesToCloud(values);

        savePhoneNumInfo(user.getPhoneNum());
    }

    private static void savePhoneNumInfo(String phoneNum) {

        if(phoneNum.trim().isEmpty()) return;

        mDbref = FirebaseDatabase.getInstance().getReference().child(PhoneNums);
        Map<String, Object> values = new HashMap<>();
        values.put(fbUserId, userId);
        mDbref.child(phoneNum).setValue(values);
    }

    public static void setValuesToCloud(Map<String, String> values) {

        mDbref.child(userId).setValue(values, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

            }
        });
    }
}
