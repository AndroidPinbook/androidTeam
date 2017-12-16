package uur.com.pinbook.Controller;

import android.location.Address;
import android.location.Geocoder;
import android.util.Log;

import com.firebase.geofire.GeoLocation;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by mac on 11.12.2017.
 */

public class FirebaseLocationAdapter {

    public static String locationAddress;

    public static String getAddress() {
        return locationAddress;
    }

    public static void setAddress(String address) {
        FirebaseLocationAdapter.locationAddress = address;
    }

    public static void saveLocationInfo(Geocoder geocoder, LatLng latLng, String FBuserId,
                                        String itemId, GeoLocation geoLocation,
                                        DatabaseReference mDbref){

        try {

            List<Address> listAddresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);

            if (listAddresses != null && listAddresses.size() > 0) {

                Log.i("Info", "        >>Thorougfare   :" + listAddresses.get(0).getThoroughfare());
                Log.i("Info", "        >>CountryName   :" + listAddresses.get(0).getCountryName());
                Log.i("Info", "        >>PostalCode    :" + listAddresses.get(0).getPostalCode());
                Log.i("Info", "        >>Locality      :" + listAddresses.get(0).getLocality());
                Log.i("Info", "        >>CountryCode   :" + listAddresses.get(0).getCountryCode());
                Log.i("Info", "        >>SubThorougfare:" + listAddresses.get(0).getSubThoroughfare());

                if (listAddresses.get(0).getThoroughfare() != null) {

                    if (listAddresses.get(0).getSubThoroughfare() != null) {

                        locationAddress += listAddresses.get(0).getSubThoroughfare() + " ";
                    }
                    locationAddress += listAddresses.get(0).getThoroughfare();
                }
            }

            Map<String, String> values = new HashMap<>();
            values.put("userId", FBuserId);
            addItemLocation(FBuserId, values, itemId, mDbref);


            values.put("countryCode", listAddresses.get(0).getCountryCode());
            addItemLocation(FBuserId, values, itemId, mDbref);


            values.put("countryName", listAddresses.get(0).getCountryName());
            addItemLocation(FBuserId, values, itemId, mDbref);


            Long tsLong = System.currentTimeMillis()/1000;
            String currTimestamp = tsLong.toString();
            values.put("pinThrowTst", currTimestamp);
            addItemLocation(FBuserId, values, itemId, mDbref);


            values.put("postalCode", listAddresses.get(0).getPostalCode());
            addItemLocation(FBuserId, values, itemId, mDbref);


            values.put("thoroughFare", listAddresses.get(0).getThoroughfare());
            addItemLocation(FBuserId, values, itemId, mDbref);


            values.put("subThoroughfare", listAddresses.get(0).getSubThoroughfare());
            addItemLocation(FBuserId, values, itemId, mDbref);

            saveLocationGeoLocation(FBuserId, itemId, geoLocation, mDbref);

        } catch (IOException e) {
            e.printStackTrace();
        }

        if (locationAddress == "") {

            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm yyyy-MM-dd");
            locationAddress = sdf.format(new Date());
        }

        Log.i("Info", "     >>adress:" + locationAddress);
    }

    /*========================================================================================*/
    public static void addItemLocation(String userId, Map values, String itemId, DatabaseReference mDbref) {

        Log.i("Info", "FBLocationAdapter addItemLocation starts");

        mDbref.child(itemId).setValue(values, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                Log.i("Info", "     >>databaseError:" + databaseError);
            }
        });
    }

    /*========================================================================================*/
    public static void saveLocationGeoLocation(String FBuserId, String itemId,
                                               GeoLocation geoLocation, DatabaseReference mDbref){

        Log.i("Info", "FBLocationAdapter saveLocationGeoLocation starts");

        mDbref.child(itemId).child("geolocation").setValue(geoLocation, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                Log.i("Info", "     >>databaseError:" + databaseError);
            }
        });
    }
}
