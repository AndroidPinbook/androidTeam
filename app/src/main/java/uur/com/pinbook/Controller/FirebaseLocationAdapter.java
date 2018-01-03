package uur.com.pinbook.Controller;

import android.util.Log;

import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

import uur.com.pinbook.JavaFiles.UserLocation;
import static uur.com.pinbook.JavaFiles.ConstValues.*;

public class FirebaseLocationAdapter {

    public static DatabaseReference mDbref;

    public static String locationId;

    public FirebaseOptions locationsOptions;
    public FirebaseOptions userLocationsOptions;

    public FirebaseOptions getLocationsOptions() {

        locationsOptions = new FirebaseOptions.Builder().
                setApplicationId(FB_APPLICATION_ID).setDatabaseUrl(GEO_FIRE_DB_LOCATIONS).build();
        return locationsOptions;
    }

    public FirebaseOptions getUserLocationsOptions() {

        userLocationsOptions = new FirebaseOptions.Builder().
                setApplicationId(FB_APPLICATION_ID).setDatabaseUrl(GEO_FIRE_DB_USER_LOCATIONS).build();
        return userLocationsOptions;
    }

    public static String getLocationId() {
        return locationId;
    }

    public static void setLocationId(String locationId) {
        FirebaseLocationAdapter.locationId = locationId;
    }

    public static void saveUserLocationInfo(String FBUserId){

        mDbref = null;
        mDbref = FirebaseDatabase.getInstance().getReferenceFromUrl(GEO_FIRE_DB_USER_LOCATIONS);

        Map<String, String> values = new HashMap<>();
        addUserItemLocation(FBUserId, values, getLocationId());
    }

    public static void saveLocationInfo(UserLocation userLocation) {

        mDbref = null;
        Map<String, String> values = new HashMap<>();
        mDbref = FirebaseDatabase.getInstance().getReferenceFromUrl(GEO_FIRE_DB_LOCATIONS);
        String locItemId = mDbref.child(Locations).push().getKey();

        setLocationId(locItemId);

        values.put(userID, userLocation.getUserId());
        addItemLocation(values, locItemId);

        values.put(countryCode, userLocation.getCountryCode());
        addItemLocation(values, locItemId);

        values.put(countryName, userLocation.getCountryName());
        addItemLocation(values, locItemId);

        values.put(timestamp, userLocation.getLocTimestamp());
        addItemLocation(values, locItemId);

        values.put(postalCode, userLocation.getPostalCode());
        addItemLocation(values, locItemId);

        values.put(thorough, userLocation.getThoroughFare());
        addItemLocation(values, locItemId);

        values.put(subThorough, userLocation.getSubThoroughfare());
        addItemLocation(values, locItemId);

        values.put(latitude, userLocation.getLatitude());
        addItemLocation(values, locItemId);

        values.put(longitude, userLocation.getLongitude());
        addItemLocation(values, locItemId);
    }

    /*========================================================================================*/
    public static void addItemLocation(Map values, String itemId) {

        Log.i("Info", "FBLocationAdapter addItemLocation starts");

        mDbref.child(itemId).setValue(values, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                Log.i("Info", "     >>databaseError:" + databaseError);
            }
        });
    }

    public static void addUserItemLocation(String FBUserId, Map values, String locId){

        Log.i("Info", "FBLocationAdapter addItemLocation starts");

        mDbref.child(FBUserId).child(locId).setValue(" ", new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                Log.i("Info", "     >>databaseError:" + databaseError);
            }
        });
    }
}
