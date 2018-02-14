package uur.com.pinbook.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import uur.com.pinbook.Activities.ProfilePageActivity;
import uur.com.pinbook.JavaFiles.UserLocation;
import uur.com.pinbook.R;

import butterknife.ButterKnife;

import static uur.com.pinbook.JavaFiles.ConstValues.UserLocations;

public class ProfileFragment extends BaseFragment
        implements OnMapReadyCallback {

    GoogleMap mMap;
    MapView mapView;
    View mView;

    DatabaseReference dataBaseLocations;
    List<UserLocation> locationList;

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



        dataBaseLocations = FirebaseDatabase.getInstance().getReference(UserLocations);

        mapView = (MapView) mView.findViewById(R.id.map);

        if(mapView != null){
            mapView.onCreate(null);
            mapView.onResume();
            mapView.getMapAsync(this);

        }

        locationList = new ArrayList<>();
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


    @Override
    public void onStart() {
        super.onStart();

        dataBaseLocations.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                locationList.clear();
                for (DataSnapshot locationSnapshot : dataSnapshot.getChildren()){
                    UserLocation userLocation = locationSnapshot.getValue(UserLocation.class);
                    String a = userLocation.getLocationId();
                    locationList.add(userLocation);
                }

                String x = Integer.toString(locationList.size());
                Log.i("list_size ", x);
                Log.i("info ", "================LocationList=============");

                for (int i=0; i<locationList.size(); i++) {

                    String y = locationList.get(i).getLocationId();
                    Log.i("location id_", "");
                    
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }
}
