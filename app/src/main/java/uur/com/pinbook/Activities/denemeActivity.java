package uur.com.pinbook.Activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;

import com.arsy.maps_library.MapRadar;
import com.arsy.maps_library.MapRipple;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import uur.com.pinbook.Controller.CustomInfoWindowAdapter;
import uur.com.pinbook.R;

public class denemeActivity extends AppCompatActivity
        implements OnMapReadyCallback, View.OnClickListener, GoogleMap.OnMarkerClickListener {

    private GoogleMap mMap;
    private LatLng latLng = new LatLng(28.7938709, 77.1427639);
    private Context context;
    private final int MY_PERMISSIONS_REQUEST_FINE_LOCATION = 100;
    private LocationTracker locationTrackObj;
    private MapRipple mapRipple;
    private MapRadar mapRadar;
    private Button startstoprippleBtn;
    private final int ANIMATION_TYPE_RIPPLE = 0;
    private final int ANIMATION_TYPE_RADAR = 1;
    private int whichAnimationWasRunning = ANIMATION_TYPE_RIPPLE;

    private ImageView imgViewPinThrow;
    private ImageView imgViewNext;
    private Marker marker;
    private List<Marker> markers = new ArrayList<Marker>();

    String arrayFeatures[] = {"Text", "Image", "Video"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deneme);
        context = this;
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        locationTrackObj = new LocationTracker(context);
        if (!locationTrackObj.canGetLocation()) {
            locationTrackObj.showSettingsAlert();
        } else {
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                checkLocationPermission();
            }
        }

        imgViewPinThrow = (ImageView) findViewById(R.id.imgPinThrow);
        imgViewNext = (ImageView) findViewById(R.id.imgNext);
        imgViewPinThrow.setOnClickListener(this);
        imgViewNext.setOnClickListener(this);

        /*
        CircleMenu circleMenu = (CircleMenu) findViewById(R.id.circleMenu);
        circleMenu.setMainMenu(Color.parseColor("CDCDCD"), R.drawable.batman_icon, R.drawable.wonder_woman_icon)
                .addSubMenu(Color.parseColor("258CFF"), R.drawable.img_school_material)
                .addSubMenu(Color.parseColor("6d4c41"), R.drawable.img_photo_camera)
                .addSubMenu(Color.parseColor("ff0000"), R.drawable.img_play_button)
                .setOnMenuSelectedListener(new OnMenuSelectedListener() {
                    @Override
                    public void onMenuSelected(int index) {
                        Toast.makeText(denemeActivity.this, "You selected "+ arrayFeatures[index], Toast.LENGTH_SHORT);
                    }
                });
        */




    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    initializeMap(mMap);
                } else {
                }
                return;
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                initializeMap(mMap);
            }
        } else {
            initializeMap(mMap);
        }
        mMap.setOnMarkerClickListener(this);

    }

    private void initializeMap(GoogleMap mMap) {
        if (mMap != null) {
            mMap.getUiSettings().setScrollGesturesEnabled(true);
            mMap.getUiSettings().setAllGesturesEnabled(true);
            if (ContextCompat.checkSelfPermission(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                mMap.setMyLocationEnabled(true);
            }
            Location location = getLastKnownLocation();
            if (location == null)
                location = locationTrackObj.getLocation();

            try {
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new
                        LatLng(location.getLatitude(),
                        location.getLongitude()), 14));
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (location != null)
                latLng = new LatLng(location.getLatitude(), location.getLongitude());
            else {
                latLng = new LatLng(0.0, 0.0);
            }
            mapRipple = new MapRipple(mMap, latLng, context);

//            mapRipple.withNumberOfRipples(3);
//            mapRipple.withFillColor(Color.parseColor("#FFA3D2E4"));
//            mapRipple.withStrokeColor(Color.BLACK);
//            mapRipple.withStrokewidth(0);      // 10dp
//            mapRipple.withDistance(2000);      // 2000 metres radius
//            mapRipple.withRippleDuration(12000);    //12000ms
//            mapRipple.withTransparency(0.5f);
            //mapRipple.startRippleMapAnimation();


        }
    }

    @SuppressLint("MissingPermission")
    private Location getLastKnownLocation() {

        Log.i("Info", "getLastKnownLocation starts");

        LocationManager locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
        List<String> providers = locationManager.getProviders(true);
        Location bestLocation = null;

        for (String provider : providers) {

            Location location = null;
            if (ContextCompat.checkSelfPermission(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                location = locationManager.getLastKnownLocation(provider);
            } else {

                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }

            if (location == null) {
                continue;
            }
            if (bestLocation == null || location.getAccuracy() < bestLocation.getAccuracy()) {
                bestLocation = location;
            }
        }
        return bestLocation;
    }

    public boolean checkLocationPermission() {
        if (ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Asking user if explanation is needed
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                //Prompt the user once explanation has been shown
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_FINE_LOCATION);


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_FINE_LOCATION);
            }

            return false;
        } else {
            return true;
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            if (mapRipple.isAnimationRunning()) {
                mapRipple.stopRippleMapAnimation();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        if (v == imgViewPinThrow) {
            //Throw a pin
            v.startAnimation(AnimationUtils.loadAnimation(denemeActivity.this, R.anim.img_anim));
            saveCurrLocation();
        }

        if (v == imgViewNext) {
            //Go to next page
            v.startAnimation(AnimationUtils.loadAnimation(denemeActivity.this, R.anim.img_anim));
        }
    }

    public void saveCurrLocation() {

        Log.i("Info", "saveCurrLocation============");


        Location location = getLastKnownLocation();
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

        Log.i("Info", "     >>Latlng latitude :" + latLng.latitude);
        Log.i("Info", "     >>Latlng longitude:" + latLng.longitude);


        if(markers.size() != 0){
            markers.remove(marker);
            marker.remove();
        }

        mMap.setInfoWindowAdapter(new CustomInfoWindowAdapter(denemeActivity.this));

        marker = mMap.addMarker(new MarkerOptions()
                .position(latLng)
                .title("Title")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))
                .snippet("bu ne la"));
        markers.add(marker);

        marker.showInfoWindow();

    }

    @Override
    public boolean onMarkerClick(final Marker mark) {

        Log.i("info ", "marker clk..");
        if (mark.equals(marker)) {
            //handle click here
            Log.i("info ", "marker clicked..");
        }
        return true;
    }

    private class LocationTracker implements LocationListener {

        private final Context mContext;

        // flag for GPS status
        private boolean isGPSEnabled = false;

        // flag for network status
        private boolean isNetworkEnabled = false;

        // flag for GPS status
        private boolean canGetLocation = false;

        private Location location; // location
        private double latitude; // latitude
        private double longitude; // longitude

        // The minimum distance to change Updates in meters
        private static final float MIN_DISTANCE_CHANGE_FOR_UPDATES = 1; // 1 meters

        // The minimum time between updates in milliseconds
        private static final long MIN_TIME_BW_UPDATES = 1000; // 1 sec

        private final String TAG = "LocationTracker";
        // Declaring a Location Manager
        protected LocationManager locationManager;

        public LocationTracker(Context context) {
            this.mContext = context;
            getLocation();
        }

        public Location getLocation() {
            try {
                locationManager = (LocationManager) mContext
                        .getSystemService(Context.LOCATION_SERVICE);

                // getting GPS status
                isGPSEnabled = locationManager
                        .isProviderEnabled(LocationManager.GPS_PROVIDER);

                // getting network status
                isNetworkEnabled = locationManager
                        .isProviderEnabled(LocationManager.NETWORK_PROVIDER);

                if (!isGPSEnabled && !isNetworkEnabled) {
                    // no network provider is enabled
                    this.canGetLocation = false;
                } else {
                    this.canGetLocation = true;
                    // First get location from Network Provider
                    if (isNetworkEnabled) {
                        long x = 1;
                        float y = 5.7f;
                        if (ContextCompat.checkSelfPermission(denemeActivity.this,
                                android.Manifest.permission.ACCESS_FINE_LOCATION)
                                == PackageManager.PERMISSION_GRANTED) {
                            locationManager.requestLocationUpdates(
                                    LocationManager.NETWORK_PROVIDER,
                                    MIN_TIME_BW_UPDATES,
                                    MIN_DISTANCE_CHANGE_FOR_UPDATES, (android.location.LocationListener) denemeActivity.this);
                        }


                        Log.d("Network", "Network");
                        if (ContextCompat.checkSelfPermission(denemeActivity.this,
                                android.Manifest.permission.ACCESS_FINE_LOCATION)
                                == PackageManager.PERMISSION_GRANTED) {
                            if (locationManager != null) {
                                location = locationManager
                                        .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                                if (location != null) {
                                    latitude = location.getLatitude();
                                    longitude = location.getLongitude();
                                }
                            }
                        }


                    }
                    // if GPS Enabled get lat/long using GPS Services
                    if (isGPSEnabled) {
                        if (location == null) {
                            if (ContextCompat.checkSelfPermission(denemeActivity.this,
                                    android.Manifest.permission.ACCESS_FINE_LOCATION)
                                    == PackageManager.PERMISSION_GRANTED) {
                                locationManager.requestLocationUpdates(
                                        LocationManager.NETWORK_PROVIDER,
                                        MIN_TIME_BW_UPDATES,
                                        MIN_DISTANCE_CHANGE_FOR_UPDATES, (android.location.LocationListener) denemeActivity.this);
                            }
                            Log.d("GPS Enabled", "GPS Enabled");
                            if (locationManager != null) {
                                location = locationManager
                                        .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                                if (location != null) {
                                    latitude = location.getLatitude();
                                    longitude = location.getLongitude();
                                }
                            }
                        }
                    }
                }

            } catch (Exception e) {
                Log.e(TAG, Log.getStackTraceString(e));
            }

            return location;
        }

        /**
         * Stop using GPS listener
         * Calling this function will stop using GPS in your app
         */


        /**
         * Function to get latitude
         */
        public double getLatitude() {
            if (location != null) {
                latitude = location.getLatitude();
            }

            // return latitude
            return latitude;
        }

        /**
         * Function to get longitude
         */
        public double getLongitude() {
            if (location != null) {
                longitude = location.getLongitude();
            }

            // return longitude
            return longitude;
        }

        /**
         * Function to check GPS/wifi enabled
         *
         * @return boolean
         */
        public boolean canGetLocation() {
            return this.canGetLocation;
        }

        /**
         * Function to show settings alert dialog
         * On pressing Settings button will lauch Settings Options
         */
        public void showSettingsAlert() {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);

            // Setting Dialog Title
            alertDialog.setTitle("GPS Settings");

            // Setting Dialog Message
            alertDialog.setMessage("GPS is not enabled. Click on setting to enable and get location, please start app again after turning on GPS.");
            alertDialog.setCancelable(false);

            // On pressing Settings button
            alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    mContext.startActivity(intent);
                }
            });


            // Showing Alert Message
            alertDialog.show();
        }

        Random rand = new Random();

        @Override
        public void onLocationChanged(Location location) {
            //            mapRipple.withNumberOfRipples(3);
            this.location = location;
//            Toast.makeText(context, "  " + location.getLatitude() + ",  " + location.getLongitude(), Toast.LENGTH_SHORT).show();
            if (mapRipple.isAnimationRunning())
                mapRipple.withLatLng(new LatLng(location.getLatitude(), location.getLongitude()));
            if (mapRadar.isAnimationRunning())
                mapRadar.withLatLng(new LatLng(location.getLatitude(), location.getLongitude()));
        }


        /*************************************************************************************************************************/

    }
}
