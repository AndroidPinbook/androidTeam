package uur.com.pinbook.FirebaseGetData;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

import uur.com.pinbook.JavaFiles.User;

import static uur.com.pinbook.ConstantsModel.FirebaseConstant.*;

public class FirebaseGetUser {

    private static String userID;
    private static User user;

    private static FirebaseGetUser instance = null;

    public static FirebaseGetUser getInstance(String userID) {

        if(instance == null) {
            instance = new FirebaseGetUser(userID);
        }
        return instance;
    }

    public FirebaseGetUser(String userID) {
        user = new User();
        instance.userID = userID;
        getUserFromFirebase();
    }

    public User getUser() {
        return instance.user;
    }

    public void setUser(User user) {
        instance.user = user;
    }

    private void getUserFromFirebase() {

        final FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = db.getReference(Users).child(userID);

        ValueEventListener valueEventListener;
        valueEventListener = databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Map<String , Object> map = new HashMap<String, Object>();
                map = (Map) dataSnapshot.getValue();

                instance.user.setEmail((String) map.get(email));
                instance.user.setBirthdate((String) map.get(birthday));
                instance.user.setGender((String) map.get(gender));
                instance.user.setPhoneNum((String) map.get(mobilePhone));
                instance.user.setName((String) map.get(name));
                instance.user.setSurname((String) map.get(surname));
                instance.user.setProfilePicSrc((String) map.get(profilePictureUrl));
                instance.user.setUsername((String) map.get(userName));
                instance.user.setUserId(userID);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
