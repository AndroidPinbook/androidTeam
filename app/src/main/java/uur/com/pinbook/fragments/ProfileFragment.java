package uur.com.pinbook.fragments;

import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.ViewFlipper;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import uur.com.pinbook.Activities.ProfilePageActivity;
import uur.com.pinbook.JavaFiles.LocationDb;
import uur.com.pinbook.JavaFiles.UserLocation;
import uur.com.pinbook.R;

import butterknife.ButterKnife;
import uur.com.pinbook.fragments.InnerFragments.SingleLocationFragment;


import static uur.com.pinbook.JavaFiles.ConstValues.Locations;
import static uur.com.pinbook.JavaFiles.ConstValues.UserLocations;

public class ProfileFragment extends BaseFragment
        implements OnMapReadyCallback,
        GoogleMap.OnMarkerClickListener,
        GoogleMap.OnCameraMoveStartedListener,
        GoogleMap.OnCameraIdleListener,
        GoogleMap.OnCameraMoveListener,
        View.OnClickListener,
        GoogleMap.OnMapClickListener {

    GoogleMap mMap;
    MapView mapView;
    View mView;

    DatabaseReference mDbref;
    DatabaseReference tempRef;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private String FBuserId;
    List<UserLocation> locationList;
    List<LocationDb> locationDbsList;
    Map<String, String> mp;
    LocationDb locationDb;

    private ValueEventListener mDbRefListener;
    private ValueEventListener tempRefListener;

    private boolean clickedMarkerChanged = true;
    private String lastClickedMarkerId = null;

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
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        FBuserId = currentUser.getUid();

        mDbref = FirebaseDatabase.getInstance().getReference(UserLocations).child(FBuserId);

        mapView = (MapView) mView.findViewById(R.id.map);

        if (mapView != null) {
            mapView.onCreate(null);
            mapView.onResume();
            mapView.getMapAsync(this);
        }

        locationList = new ArrayList<>();
        locationDbsList = new ArrayList<>();
        mp = new HashMap<String, String>();

        Log.i("info ", "==========================================");
        Log.i("", "xx'e gidiyoruz. ");
        xx();
        Log.i("", "xx'ten döndük. ");
        String p;
        printLocations();

    }

    private void printLocations() {

        Log.i("", "=========================================");
        Log.i("", "printteyiz..");
        Log.i("locationDbsList.size() ", ((Integer) locationDbsList.size()).toString());
        for (int i = 0; i < locationDbsList.size(); i++) {

            Log.i("", "=========================================");
            Log.i("Location  ", locationDbsList.get(i).getLocationId());
            Log.i("latitude  ", locationDbsList.get(i).getLatitude());
            Log.i("timestamp ", locationDbsList.get(i).getLocTimestamp());
        }

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

        mMap.setOnMarkerClickListener(this);
        mMap.setOnMapClickListener(this);

        mMap.getUiSettings().setMapToolbarEnabled(false);
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

    public void xx() {

        Log.i("", "=======================================>> xx start ");

        mDbRefListener = mDbref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                locationList.clear();
                locationDbsList.clear();

                for (DataSnapshot locationSnapshot : dataSnapshot.getChildren()) {
                    UserLocation userLocation = new UserLocation();
                    userLocation.setUserId(FBuserId);
                    final String locationId = locationSnapshot.getKey();
                    userLocation.setLocationId(locationSnapshot.getValue(String.class));

                    locationList.add(userLocation);

                    tempRef = FirebaseDatabase.getInstance().getReference(Locations).child(locationId);
                    Log.i("tempRef", tempRef.toString());
                    if (tempRef != null) {

                    }
                    tempRefListener = tempRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            if (dataSnapshot.getValue() != null) {
                                locationDb = new LocationDb();

                                Log.i("dataSnapshot ", dataSnapshot.toString());

                                mp = ((Map) dataSnapshot.getValue());
                                Log.i("map", mp.toString());
                                Log.i("--> ", mp.get("countryCode"));

                                setLocationProperties(FBuserId, locationId);

                                locationDbsList.add(locationDb);

                                addMarkerToLocation(locationDb);
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


        Log.i("", "=======================================>> xx finish ");
    }

    private void addMarkerToLocation(LocationDb lb) {


        Log.i("-----> locationId ", lb.getLocationId().toString());

        double lat = Double.parseDouble(lb.getLatitude());
        double lng = Double.parseDouble(lb.getLongitude());

        mMap.addMarker(new MarkerOptions()
                .position(new LatLng(lat, lng))
                .title(lb.getLocationId()));


    }


    @Override
    public void onDestroy() {
        super.onDestroy();

        mDbref.removeEventListener(mDbRefListener);
        tempRef.removeEventListener(tempRefListener);
    }

    private PopupWindow popupWindow = null;
    private Marker clickedMarker = null;
    private View popupMainView = null;
    private int mWidth;
    private int mHeight;
    private static View markerInfoLayout;
    private ImageView videoImageView;
    private ImageView noteTextImageView;
    private ImageView pictureImageView;

    @Override
    public boolean onMarkerClick(Marker marker) {
        Log.i("info ", "===========> onMarkerClick triggered..");


        if (mFragmentNavigation != null) {
            mFragmentNavigation.pushFragment(SingleLocationFragment.newInstance(1));
        }

        /*
        BaseFragment bf = new BaseFragment();
        android.support.v4.app.FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.content_frame, bf)
                .commit();


        try {
            // Marker title should be closed everytime, it keeps locationId

            Log.i("--> Clicked marker ", marker.getTitle());
            marker.hideInfoWindow();

            Log.i("Info", "onMarkerClick starts");

            if (lastClickedMarkerId == null){
                lastClickedMarkerId = marker.getTitle();
            }else{

                if (lastClickedMarkerId.equals(marker.getTitle()) ){
                    clickedMarkerChanged = false;
                    Log.i("clickedMarkerChanged ", "false");
                }else{
                    clickedMarkerChanged = true;
                    lastClickedMarkerId = marker.getTitle();
                    Log.i("clickedMarkerChanged ", "true");
                }
            }

            if(lastClickedMarkerId!=null)
                Log.i("lastClickedMarkerId", lastClickedMarkerId.toString());


            if (popupWindow != null) {
                if (popupWindow.isShowing()) {

                    if(clickedMarkerChanged){
                        popupWindow.dismiss();
                        showPopupWindow(marker);
                    }else{
                        popupWindow.dismiss();
                    }

                } else {
                    //createPopupWindow();
                    showPopupWindow(marker);
                }

            } else {
                createPopupWindow();
                showPopupWindow(marker);
            }




        } catch (Exception e) {
            Log.i("Info", "     >>onCameraChanged Error:" + e.toString());
        }

        */


        return false;
    }

    @Override
    public void onCameraMove() {
        Log.i("info ", "===========> OnCameraMove triggered..");
    }

    @Override
    public void onCameraIdle() {
        Log.i("info ", "===========> onCameraIdle triggered..");
    }

    @Override
    public void onCameraMoveStarted(int i) {
        Log.i("info ", "===========> onCameraMoveStarted triggered..");
    }

    public void showPopupWindow(Marker marker) {

        if (popupWindow == null)
            return;

        if (popupWindow.isShowing())
            return;

        Point p = mMap.getProjection().toScreenLocation(marker.getPosition());

        Point size = new Point();
        popupMainView.measure(size.x, size.y);

        mWidth = popupMainView.getMeasuredWidth();
        mHeight = popupMainView.getMeasuredHeight();

        popupWindow.showAtLocation(mapView, Gravity.CENTER, 0, -150);
        //popupWindow.showAtLocation(mapView,
        //        Gravity.NO_GRAVITY, p.x - mWidth / 2, p.y - mHeight - 65);

        setLocationItems(marker);
    }

    private void setLocationItems(Marker marker) {
        //Log.i("--> Clicked marker ", marker.getTitle());
    }

    private void createPopupWindow() {

        popupMainView = getLayoutInflater().inflate(R.layout.default_marker_info_window, null);

        ViewFlipper markerInfoContainer = (ViewFlipper) popupMainView.findViewById(R.id.markerInfoContainer);

        markerInfoLayout = getLayoutInflater().inflate(R.layout.marker_info_layout, null);

        markerInfoContainer.addView(markerInfoLayout);

        videoImageView = (ImageView) markerInfoLayout.findViewById(R.id.videoImageView);
        noteTextImageView = (ImageView) markerInfoLayout.findViewById(R.id.noteImageView);
        pictureImageView = (ImageView) markerInfoLayout.findViewById(R.id.pictureImageView);

        videoImageView.setOnClickListener(this);
        noteTextImageView.setOnClickListener(this);
        pictureImageView.setOnClickListener(this);

        popupWindow = new PopupWindow(popupMainView,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onMapClick(LatLng latLng) {
        checkPopupShown();
    }

    public void checkPopupShown() {

        if (popupWindow != null) {
            if (popupWindow.isShowing()) {
                popupWindow.dismiss();
            }
        }
    }
}
