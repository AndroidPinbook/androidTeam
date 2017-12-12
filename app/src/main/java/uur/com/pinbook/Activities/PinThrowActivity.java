package uur.com.pinbook.Activities;

import android.*;
import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Interpolator;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.location.Criteria;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.hardware.SensorListener;
import android.hardware.SensorManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Toast;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import uur.com.pinbook.Controller.FirebaseLocationAdapter;
import uur.com.pinbook.R;


public class PinThrowActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener, GoogleMap.OnMarkerClickListener
        , GeoFire.CompletionListener, GeoQueryEventListener, GoogleMap.OnCameraChangeListener,
        View.OnClickListener {

    private GoogleMap mMap;

    LocationManager locationManager;
    //LocationListener locationListener;

    private GeoFire geoFire;
    private GeoQuery geoQuery;
    private GeoLocation geoLocation;

    private Marker marker;
    private MapView mapView;

    private Map<String, Marker> markers;


    private Circle searchCircle;

    private Handler handler;

    private static final GeoLocation INITIAL_CENTER = new GeoLocation(37.7789, -122.4017);
    private static final int INITIAL_ZOOM_LEVEL = 14;
    private static final String GEO_FIRE_DB = "https://androidteam-f4c25.firebaseio.com";
    private static final String GEO_FIRE_REF = GEO_FIRE_DB + "/_locations";

    private String appId = "geofire";
    private double radius = 1.0f;

    public Criteria criteria;
    public String bestProvider;

    private FirebaseAuth mAuth;
    private DatabaseReference mDbref;

    private FloatingActionButton nextButton;
    private FloatingActionButton pinThrowButton;

    GoogleApiClient mGoogleApiClient;
    LocationRequest mLocationRequest;

    LatLng latLng;
    private CircleOptions circleOptions;

    private LocationListener locationListener;
    private Runnable runnable = null;

    private FirebaseUser currentUser;
    private String FBuserId;


    /*========================================================================================*/
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        try {

            Log.i("Info", "onRequestPermissionsResult============");

            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ) {

                    Log.i("Info", "     >>permission is ok");
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

                    centerMapOnLocation();
                }
            }
        } catch (Exception e) {
            Log.i("Info", "     >>onRequestPermissionsResult Error:" + e.toString());
        }
    }

    /*========================================================================================*/
    public void centerMapOnLocation() {

        Log.i("Info", "centerMapOnLocation============");

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            bestProvider = String.valueOf(locationManager.getBestProvider(criteria, true)).toString();
            locationManager.requestLocationUpdates(bestProvider, 0, 0, locationListener);
        }

        Log.i("Info", "     >>centerMapOnLocation will go on");

        Location currentLocation = getLastKnownLocation();

        LatLng userLocation = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
        mMap.clear();
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 14));
    }

    /*========================================================================================*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pin_throw);

        try {
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);

            Log.i("Info", "onCreate============");

            mapFragment.getMapAsync(this);

            nextButton = (FloatingActionButton) findViewById(R.id.nextButton);
            pinThrowButton = (FloatingActionButton) findViewById(R.id.pinThrowButton);

            nextButton.setOnClickListener(this);
            pinThrowButton.setOnClickListener(this);

            mAuth = FirebaseAuth.getInstance();
            currentUser = mAuth.getCurrentUser();
            FBuserId = currentUser.getUid();


            FirebaseOptions options = new FirebaseOptions.Builder().
                    setApplicationId(appId).setDatabaseUrl(GEO_FIRE_REF).build();

            Log.i("Info", "     >>FirebaseOptions.AppId:" + options.getApplicationId());


            //FirebaseApp app = FirebaseApp.initializeApp(this, options, appId);


            // setup GeoFire
            this.geoFire = new GeoFire(FirebaseDatabase.getInstance().getReferenceFromUrl(GEO_FIRE_REF));
            this.geoQuery = this.geoFire.queryAtLocation(INITIAL_CENTER, 1);

        } catch (Exception e) {
            Log.i("Info", "     >>onCreate try error:" + e.toString());
        }
    }

    /*========================================================================================*/
    @Override
    public void onMapReady(GoogleMap googleMap) {
        try {
            mMap = googleMap;

            Log.i("Info", "onMapReady============");

            mMap.setOnMapLongClickListener(this);
            mMap.setOnMarkerClickListener(this);

            Intent intent = getIntent();

            locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

            runLocationHandler();

            locationListener = new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {

                    Log.i("Info", "     >>onLocationChanged method......");
                    Log.i("Info", "     >>centerMapOnLocation3");
                    centerMapOnLocation();
                    showCircleCurrentLocation();
                }

                @Override
                public void onStatusChanged(String s, int i, Bundle bundle) {

                    Log.i("Info", "     >>onStatusChanged method......");
                }

                @Override
                public void onProviderEnabled(String s) {

                    Log.i("Info", "     >>onProviderEnabled method......");
                }

                @Override
                public void onProviderDisabled(String s) {

                    Log.i("Info", "     >>onProviderDisabled method......");
                }
            };

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);

            }

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 2);

            }

        } catch (Exception e) {
            Log.i("Info", "     >>onMapReadyException Error:" + e.toString());
        }

    }

    /*========================================================================================*/
    public void runLocationHandler() {

        Log.i("Info", "runLocationHandler starts");

        handler = new Handler();

        runnable = new Runnable() {
            public void run() {

                if (ActivityCompat.checkSelfPermission(PinThrowActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(PinThrowActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                    Log.i("Info", "     >>permissons are ok on runLocationHandler");

                    //Location lastKnownLocation = getLastKnownLocation();
                    //centerMapOnLocation();
                    showCircleCurrentLocation();
                }

                handler.postDelayed(this, 1500);
            }
        };
        handler.postDelayed(runnable, 1500);
    }

    /*========================================================================================*/
    private void isLocationEnabled() {

        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
            alertDialog.setTitle("Enable Location");
            alertDialog.setMessage("Your locations setting is not enabled. Please enabled it in settings menu.");

            alertDialog.setPositiveButton("Location Settings", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(intent);
                }
            });

            alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            AlertDialog alert = alertDialog.create();
            alert.show();
        } else {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
            alertDialog.setTitle("Confirm Location");
            alertDialog.setMessage("Your Location is enabled, please enjoy");

            alertDialog.setNegativeButton("Back to interface", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            AlertDialog alert = alertDialog.create();
            alert.show();
        }
    }

    /*========================================================================================*/
    public void showCircleCurrentLocation() {

        Log.i("Info", "showCircleCurrenctLocation starts");

        float[] meters = new float[300];

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            bestProvider = String.valueOf(locationManager.getBestProvider(criteria, true)).toString();
            locationManager.requestLocationUpdates(bestProvider, 0, 0, locationListener);
            Log.i("Info", "     >>permissons are not ok");
        }

        Log.i("Info", "     >>--1--");

        Location currentLocation = getLastKnownLocation();

        LatLng latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());

        Log.i("Info", "     >>--2--");

        circleOptions = new CircleOptions()
                .center(latLng)
                .radius(300)
                .strokeColor(Color.TRANSPARENT)
                .fillColor(Color.argb(100, 150, 150, 150))
                .strokeWidth(2);

        mMap.addCircle(circleOptions);

        Location.distanceBetween(currentLocation.getLatitude(), currentLocation.getLongitude(),
                circleOptions.getCenter().latitude, circleOptions.getCenter().longitude, meters);

        if (meters[0] < circleOptions.getRadius()) {
            //Toast.makeText(getBaseContext(), "Inside given radius", Toast.LENGTH_SHORT).show();

           /* marker = mMap.addMarker(new MarkerOptions()
                    .position(latLng)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))
                    .title("title"));

            markers.put("1", marker);*/
        }
    }

    /*========================================================================================*/
    private Location getLastKnownLocation() {

        Log.i("Info", "getLastKnownLocation starts");

        locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
        List<String> providers = locationManager.getProviders(true);
        Location bestLocation = null;

        for (String provider : providers) {

            Location location = null;

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                location = locationManager.getLastKnownLocation(provider);

            } else {

                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 2);
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

    /*========================================================================================*/
    @Override
    public void onMapLongClick(LatLng latLng) {


    }

    /*========================================================================================*/
    public void saveCurrLocation() {

        Log.i("Info", "saveCurrLocation============");
        Log.i("Info", "     >>userId:" + FBuserId);

        centerMapOnLocation();
        Location location = getLastKnownLocation();
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

        Log.i("Info", "     >>Latlng latitude :" + latLng.latitude);
        Log.i("Info", "     >>Latlng longitude:" + latLng.longitude);

        geoLocation = new GeoLocation(latLng.latitude, latLng.longitude);

        mDbref = FirebaseDatabase.getInstance().getReferenceFromUrl(GEO_FIRE_REF);

        String itemId = mDbref.child("locations").push().getKey();

        Log.i("Info", "     >>itemId:" + itemId);

        try {
            Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
            FirebaseLocationAdapter firebaseLocationAdapter = new FirebaseLocationAdapter();
            firebaseLocationAdapter.saveLocationInfo(geocoder, latLng, FBuserId, itemId, geoLocation, mDbref);

            marker = mMap.addMarker(new MarkerOptions().position(latLng).title(FirebaseLocationAdapter.getAddress()));

            Toast.makeText(this, "Location Saved", Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            Log.i("Info", "     >>SaveCurrLocation Error:" + e.toString());
        }
    }


    /*========================================================================================*/
    @Override
    public boolean onMarkerClick(Marker marker) {

        Log.i("Info", "onMarkerClick starts");

        Toast.makeText(this, "Marker clicked", Toast.LENGTH_SHORT).show();
        return false;
    }

    /*========================================================================================*/
    @Override
    public void onComplete(String key, DatabaseError error) {

    }

    /*========================================================================================*/
    @Override
    public void onKeyEntered(String key, GeoLocation location) {

        try {
            Log.i("Info", "onKeyEntered============");

            Marker marker = this.mMap.addMarker(new MarkerOptions().position(new LatLng(location.latitude, location.longitude)));
            this.markers.put(key, marker);
        } catch (Exception e) {
            Log.i("Info", "     >>onKeyEntered Error:" + e.toString());
        }
    }

    /*========================================================================================*/
    @Override
    public void onKeyExited(String key) {
        try {
            // Remove any old marker
            Log.i("Info", "onKeyExited============");
            Marker marker = this.markers.get(key);
            if (marker != null) {
                marker.remove();
                this.markers.remove(key);
            }
        } catch (Exception e) {
            Log.i("Info", "     >>onKeyExcited Error:" + e.toString());
        }
    }

    /*========================================================================================*/
    @Override
    public void onKeyMoved(String key, GeoLocation location) {
        Log.i("Info", "onKeyMoved============");

        Marker marker = this.markers.get(key);

        if (marker != null) {

            this.animateMarkerTo(marker, location.latitude, location.longitude);
        }
    }

    /*========================================================================================*/
    @Override
    public void onGeoQueryReady() {

    }

    /*========================================================================================*/
    @Override
    public void onGeoQueryError(DatabaseError error) {
        try {
            Log.i("Info", "onGeoQueryError starts");
            new AlertDialog.Builder(this)
                    .setTitle("Error")
                    .setMessage("There was an unexpected error querying GeoFire: " + error.getMessage())
                    .setPositiveButton(android.R.string.ok, null)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        } catch (Exception e) {
            Log.i("Info", "     >>onQueryError Error:" + e.toString());
        }
    }

    /*========================================================================================*/
    private void animateMarkerTo(final Marker marker, final double lat, final double lng) {
        try {
            Log.i("Info", "animateMarkerTo============");

            final Handler handler = new Handler();
            final long start = SystemClock.uptimeMillis();
            final long DURATION_MS = 3000;
            final AccelerateDecelerateInterpolator interpolator = new AccelerateDecelerateInterpolator();
            final LatLng startPosition = marker.getPosition();

            handler.post(new Runnable() {
                @Override
                public void run() {
                    float elapsed = SystemClock.uptimeMillis() - start;
                    float t = elapsed / DURATION_MS;
                    float v = interpolator.getInterpolation(t);

                    double currentLat = (lat - startPosition.latitude) * v + startPosition.latitude;
                    double currentLng = (lng - startPosition.longitude) * v + startPosition.longitude;
                    marker.setPosition(new LatLng(currentLat, currentLng));

                    // if animation is not finished yet, repeat
                    if (t < 1) {
                        handler.postDelayed(this, 16);
                    }
                }
            });
        } catch (Exception e) {
            Log.i("Info", "     >>animateMarkerTo Error:" + e.toString());
        }
    }

    /*========================================================================================*/
    @Override
    public void onCameraChange(CameraPosition cameraPosition) {
        try {

            Log.i("Info", "onCameraChange starts");

            LatLng center = cameraPosition.target;
            double radius = zoomLevelToRadius(cameraPosition.zoom);
            this.searchCircle.setCenter(center);
            this.searchCircle.setRadius(radius);
            this.geoQuery.setCenter(new GeoLocation(center.latitude, center.longitude));
            // radius in km
            this.geoQuery.setRadius(radius / 1000);
        } catch (Exception e) {
            Log.i("Info", "     >>onCameraChanged Error:" + e.toString());
        }
    }

    /*========================================================================================*/
    private double zoomLevelToRadius(double zoomLevel) {
        // Approximation to fit circle into view
        return 16384000 / Math.pow(2, zoomLevel);
    }

    /*========================================================================================*/
    @Override
    protected void onStop() {
        handler.removeCallbacks(runnable);

        try {
            Log.i("Info", "onStop============");
            super.onStop();

            /*this.geoQuery.removeAllListeners();
            for (Marker marker : this.markers.values()) {
                marker.remove();
            }
            this.markers.clear();*/

        } catch (Exception e) {
            Log.i("Info", "     >>onStop Error:" + e.toString());
        }
    }

    /*========================================================================================*/
    @Override
    protected void onStart() {
        try {

            super.onStart();

            Log.i("Info", "onStart starts");

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                centerMapOnLocation();
                showCircleCurrentLocation();
            }

        } catch (Exception e) {
            Log.i("Info", "      >>onStart Error:" + e.toString());
        }
    }

    /*========================================================================================*/
    @Override
    public void onClick(View v) {
        int i = v.getId();

        Intent intent;

        switch (i) {
            case R.id.nextButton:

                intent = new Intent(getApplicationContext(), ProfilePageActivity.class);
                finish();
                startActivity(intent);
                break;

            case R.id.pinThrowButton:
                centerMapOnLocation();
                saveCurrLocation();
                break;

            default:
                break;
        }
    }

    /*========================================================================================*/
    @Override
    public void onPause() {
        super.onPause();
        handler.removeCallbacks(runnable);
    }

    /*========================================================================================*/
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        handler.removeCallbacks(runnable);

    }

}
