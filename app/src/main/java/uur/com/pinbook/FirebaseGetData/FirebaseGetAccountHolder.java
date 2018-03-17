package uur.com.pinbook.FirebaseGetData;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

import uur.com.pinbook.JavaFiles.User;

import static uur.com.pinbook.ConstantsModel.FirebaseConstant.*;

public class FirebaseGetAccountHolder {

    private String userID = "";
    private User user;

    private static FirebaseGetAccountHolder instance;

    public static FirebaseGetAccountHolder getInstance(String userID) {

        if(instance == null) {
            instance = new FirebaseGetAccountHolder(userID);
        }
        return instance;
    }

    public FirebaseGetAccountHolder(String userID) {
        user = new User();
        this.userID = userID;
        getUserFromFirebase();
    }

    public static FirebaseGetAccountHolder getInstance() {
        return instance;
    }

    public static void setInstance(FirebaseGetAccountHolder instance) {
        FirebaseGetAccountHolder.instance = instance;
    }

    public static String getUserID() {

        if(instance == null){
            FirebaseAuth firebaseAuth;
            firebaseAuth = FirebaseAuth.getInstance();
            FirebaseUser currentUser = firebaseAuth.getCurrentUser();
            instance = new FirebaseGetAccountHolder(currentUser.getUid());
            return currentUser.getUid();
        }

        if(!instance.userID.isEmpty())
            return instance.userID;
        else{
            FirebaseAuth firebaseAuth;
            firebaseAuth = FirebaseAuth.getInstance();
            FirebaseUser currentUser = firebaseAuth.getCurrentUser();
            instance.userID = currentUser.getUid();
            return instance.userID;
        }
    }

    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    private void getUserFromFirebase() {

        final FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = db.getReference(Users).child(userID);

        ValueEventListener valueEventListener = databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Map<String , Object> map = new HashMap<String, Object>();
                map = (Map) dataSnapshot.getValue();

                user.setEmail((String) map.get(email));
                user.setBirthdate((String) map.get(birthday));
                user.setGender((String) map.get(gender));
                user.setPhoneNum((String) map.get(mobilePhone));
                user.setName((String) map.get(name));
                user.setSurname((String) map.get(surname));
                user.setProfilePicSrc((String) map.get(profilePictureUrl));
                user.setUsername((String) map.get(userName));
                user.setProviderId((String) map.get(providerId));
                user.setUserId(userID);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
