package uur.com.pinbook.fragments;

import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;


import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import uur.com.pinbook.Activities.ProfilePageActivity;
import uur.com.pinbook.JavaFiles.LocationDb;
import uur.com.pinbook.JavaFiles.UserLocation;
import uur.com.pinbook.R;

import butterknife.ButterKnife;

import static uur.com.pinbook.JavaFiles.ConstValues.GEO_FIRE_DB_USER_LOCATIONS;
import static uur.com.pinbook.JavaFiles.ConstValues.Locations;
import static uur.com.pinbook.JavaFiles.ConstValues.UserLocations;

public class ProfileFragment extends BaseFragment
        implements OnMapReadyCallback {

    GoogleMap mMap;
    MapView mapView;
    View mView;

    DatabaseReference mDbref;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private String FBuserId;
    List<UserLocation> locationList;
    List<LocationDb> locationDbsList;
    Map <String,String> mp;
    LocationDb locationDb;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        mView = inflater.inflate(R.layout.fragment_profile, container, false);

        ButterKnife.bind(this, mView);

        ((ProfilePageActivity) getActivity()).updateToolbarTitle("Profile");


        return mView;
    }

    @Override
    public void onViewCreated(View view,  Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        FBuserId = currentUser.getUid();


        mDbref = FirebaseDatabase.getInstance().getReference(UserLocations).child(FBuserId);

        mapView = (MapView) mView.findViewById(R.id.map);

        if(mapView != null){
            mapView.onCreate(null);
            mapView.onResume();
            mapView.getMapAsync(this);

        }

        locationList = new ArrayList<>();
        locationDbsList = new ArrayList<>();
        mp =  new HashMap<String,String>();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        MapsInitializer.initialize(getContext());

        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        mMap.addMarker(new MarkerOptions()
                .position(new LatLng(40.689247, -74.044502))
                .title("State of Liberty")
                .snippet("I have been here"));

        CameraPosition liberty = CameraPosition.builder()
                .target(new LatLng(40.689247, -74.044502)).bearing(0)
                .build();

        googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(liberty));
    }

    String f;
    @Override
    public void onStart() {
        super.onStart();

        mDbref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                locationList.clear();
                locationDbsList.clear();

                for (DataSnapshot locationSnapshot : dataSnapshot.getChildren()){
                    UserLocation userLocation = new UserLocation();
                    userLocation.setUserId(FBuserId);
                    final String locationId = locationSnapshot.getKey();
                    userLocation.setLocationId(locationSnapshot.getValue(String.class));

                    locationList.add(userLocation);

                    DatabaseReference tempRef = FirebaseDatabase.getInstance().getReference(Locations).child(locationId);

                    tempRef.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {

                                    locationDb = new LocationDb();

                                    Log.i("dataSnapshot ", dataSnapshot.toString());

                                    mp = ((Map) dataSnapshot.getValue());
                                    Log.i("map", mp.toString());
                                    Log.i("--> ", mp.get("countryCode"));

                                    setLocationProperties(FBuserId, locationId);

                                    locationDbsList.add(locationDb);
                                    for (int i=0; i<locationDbsList.size(); i++) {

                                        Log.i("", "=========================================");
                                        Log.i("Location  "  ,locationDbsList.get(i).getLocationId() );
                                        Log.i("latitude  ", locationDbsList.get(i).getLatitude());
                                        Log.i("timestamp ", locationDbsList.get(i).getLocTimestamp());

                                    }

                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });






                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void setLocationProperties(String userId, String locationId) {

        locationDb.setLocationId(locationId);
        locationDb.setCountryCode(mp.get("countryCode"));
        locationDb.setCountryName(mp.get("countryName"));
        locationDb.setLatitude(mp.get("latitude"));
        locationDb.setLongitude(mp.get("longitude"));
        locationDb.setLocTimestamp(mp.get("timestamp"));
        locationDb.setUserId(userId);

    }


    @Override
    public void onResume() {
        super.onResume();




    }
}
