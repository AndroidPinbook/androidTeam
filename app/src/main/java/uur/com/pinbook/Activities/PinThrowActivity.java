package uur.com.pinbook.Activities;

import android.*;
import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Interpolator;
import android.graphics.Point;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.location.Criteria;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;
import android.widget.ViewFlipper;

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
import com.arsy.maps_library.MapRipple;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import retrofit2.http.Url;
import uur.com.pinbook.Controller.BitmapConversion;
import uur.com.pinbook.Controller.FirebaseLocationAdapter;
import uur.com.pinbook.Controller.LocationTrackerAdapter;
import uur.com.pinbook.Controller.OnInfoWindowElemTouchListener;
import uur.com.pinbook.R;
import wseemann.media.FFmpegMediaMetadataRetriever;


public class PinThrowActivity extends FragmentActivity implements
        OnMapReadyCallback,
        GoogleMap.OnMapLongClickListener,
        GoogleMap.OnMarkerClickListener,
        GeoFire.CompletionListener,
        GoogleMap.OnCameraChangeListener,
        GoogleMap.OnMapClickListener,
        View.OnClickListener,
        LocationListener,
        MediaPlayer.OnPreparedListener {

    public GoogleMap mMap;

    LocationManager locationManager;

    private GeoLocation geoLocation;

    private boolean itemsAddedToFB = true;
    private boolean isCancelPinCheck = false;

    private FrameLayout mapRelativeLayout;
    private View popupMainView = null;

    private int mWidth;
    private int mHeight;
    private String videopath;

    private View firstDemoLayout;
    private View secondDemoLayout;
    private View noteTextLayout;
    private static View markerInfoLayout;

    private ImageView videoImageView;
    private ImageView noteTextImageView;

    private static EditText noteTextEditText;
    private static Bitmap editTextBitmap = null;


    private Marker marker;

    private boolean pinMarkedApproved = false;

    private String markerAddress;


    private boolean isMarkerClicked = false;
    private boolean isPinThrowClicked = false;

    private LocationTrackerAdapter locationTrackObj;


    private Circle searchCircle;
    private Circle mapCircle;

    private Context context;

    private Handler handler;

    private static final GeoLocation INITIAL_CENTER = new GeoLocation(37.7789, -122.4017);
    private static final int INITIAL_ZOOM_LEVEL = 14;
    private static final String GEO_FIRE_DB = "https://androidteam-f4c25.firebaseio.com";
    private static final String GEO_FIRE_LOCATIONS = GEO_FIRE_DB + "/locations";
    private static final String GEO_FIRE_USER_LOCATIONS = GEO_FIRE_DB + "/user_locations";
    private static final String GEO_FIRE_USER_ITEMS = GEO_FIRE_DB + "/items";

    private String appId = "geofire";
    private double radius = 1.0f;

    public Criteria criteria;
    public String bestProvider;

    private FirebaseAuth mAuth;
    private DatabaseReference mDbref;

    private FloatingActionButton nextButton;
    private FloatingActionButton pinThrowButton;
    private FloatingActionButton pinApproveFab;
    private FloatingActionButton pinCancelFab;

    GoogleApiClient mGoogleApiClient;
    LocationRequest mLocationRequest;

    LatLng latLng;
    //private CircleOptions circleOptions;

    private LocationListener locationListener;
    private Runnable runnable = null;

    private FirebaseUser currentUser;
    private String FBuserId;

    private boolean mLocationPermissionGranted = false;

    private PopupWindow popupWindow = null;
    private PopupWindow popupSelectionWindow = null;

    private static final int DIALOG_PICTURE_SELECTED = 1;
    private static final int DIALOG_VIDEO_SELECTED = 2;
    private static final int DIALOG_DESCRIPTION_SELECTED = 3;
    private static final int MY_PERMISSION_ACTION_GET_CONTENT = 4;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 5;
    private static final int REQUEST_VIDEO_CAPTURE = 6;
    private static final int REQUEST_READ_EXTERNAL_STORAGE = 7;
    private static final int REQUEST_WRITE_EXTERNAL_STORAGE = 7;

    private static final int VIDEO_CAMERA_SELECTED = 0;
    private static final int VIDEO_GALLERY_SELECTED = 1;

    private MapRipple mapRipple;

    private static Uri videoUri = null;
    private VideoView pinVideoView = null;
    private TextureView pinTextureView = null;

    private static Location markerLocation = null;
    private static LatLng markerLatlng = null;

    /*========================================================================================*/
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        try {

            switch (requestCode) {

                case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION:
                    if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        mLocationPermissionGranted = true;
                        initializeMap(mMap);
                    }
                    break;

                case REQUEST_VIDEO_CAPTURE:
                    if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        dispatchTakeVideoIntent();
                    }
                    break;

                case REQUEST_READ_EXTERNAL_STORAGE:
                    if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        setBitmapFromUriForVideo();
                    }
                    break;

                default:
                    break;
            }
        } catch (Exception e) {
            Log.i("Info", "     >>onRequestPermissionsResult Error:" + e.toString());
        }
    }

    /*========================================================================================*/
    @SuppressLint("MissingPermission")
    public void centerMapOnLocation() {


        if (!pinMarkedApproved) return;

        Log.i("Info", "centerMapOnLocation============");

        if (!mLocationPermissionGranted) {

            //bestProvider = String.valueOf(locationManager.getBestProvider(criteria, true)).toString();
            //locationManager.requestLocationUpdates(bestProvider, 0, 0, locationListener);

            locationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER,
                    0,
                    0, (android.location.LocationListener) context);
        }

        Log.i("Info", "     >>centerMapOnLocation will go on");

        Location currentLocation = getLastKnownLocation();

        //LatLng userLocation = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());

        if (mMap != null) {
            mMap.clear();
            //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 18));

            mapRipple = new MapRipple(mMap, latLng, context);

            float zoom = mMap.getCameraPosition().zoom;

            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new
                    LatLng(currentLocation.getLatitude(),
                    currentLocation.getLongitude()), zoom));

            //mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new
            //                                LatLng(userLocation.latitude,
            //                                userLocation.longitude), 14));
        }
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

            context = this;

            locationTrackObj = new LocationTrackerAdapter(PinThrowActivity.this);

            if (!locationTrackObj.canGetLocation()) {
                locationTrackObj.showSettingsAlert();
            } else {
                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    checkLocationPermission();
                }
            }

            mapRelativeLayout = (FrameLayout) findViewById(R.id.mapRelativeLayout);
            mapRelativeLayout.setOnClickListener(this);

            nextButton = (FloatingActionButton) findViewById(R.id.nextButton);
            pinThrowButton = (FloatingActionButton) findViewById(R.id.pinThrowButton);

            nextButton.setOnClickListener(this);
            pinThrowButton.setOnClickListener(this);

            mAuth = FirebaseAuth.getInstance();
            currentUser = mAuth.getCurrentUser();
            FBuserId = currentUser.getUid();

            FirebaseOptions locationsOptions = new FirebaseOptions.Builder().
                    setApplicationId(appId).setDatabaseUrl(GEO_FIRE_LOCATIONS).build();

            FirebaseOptions userLocationsOptions = new FirebaseOptions.Builder().
                    setApplicationId(appId).setDatabaseUrl(GEO_FIRE_USER_LOCATIONS).build();

            FirebaseOptions userItemsOptions = new FirebaseOptions.Builder().
                    setApplicationId(appId).setDatabaseUrl(GEO_FIRE_USER_ITEMS).build();


            Log.i("Info", "     >>locationsOptions.AppId    :" + locationsOptions.getApplicationId());
            Log.i("Info", "     >>userLocationsOptions.AppId:" + userLocationsOptions.getApplicationId());
            Log.i("Info", "     >>userItemsOptions.AppId    :" + userItemsOptions.getApplicationId());

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
            mMap.setOnMapClickListener(this);
            mMap.setOnCameraChangeListener(this);

            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (ActivityCompat.checkSelfPermission(this,
                        android.Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {
                    initializeMap(mMap);
                }
            } else {
                initializeMap(mMap);
            }

            showDemoPageSecond();

        } catch (Exception e) {
            Log.i("Info", "     >>onMapReadyException Error:" + e.toString());
        }
    }

    /*========================================================================================*/
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
        }
    }

    /*========================================================================================*/
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
                        PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
            }

            return false;
        } else {
            mLocationPermissionGranted = true;
            return true;
        }
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
    /*@SuppressLint("MissingPermission")
    public void showCircleCurrentLocation() {

        Log.i("Info", "showCircleCurrenctLocation starts");

        float[] meters = new float[300];

        if (!mLocationPermissionGranted) {

            bestProvider = String.valueOf(locationManager.getBestProvider(criteria, true)).toString();
            locationManager.requestLocationUpdates(bestProvider, 0, 0, locationListener);
            Log.i("Info", "     >>permissons are not ok");
        }

        Log.i("Info", "     >>--1--");

        Location currentLocation = getLastKnownLocation();

        LatLng latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());

        Log.i("Info", "     >>--2--");

        if (mapCircle != null) {
            mapCircle.remove();
        }

        mapRipple = new MapRipple(mMap, latLng, context);

        CircleOptions circleOptions = new CircleOptions()
                .center(latLng)
                .radius(8)
                .strokeColor(Color.TRANSPARENT)
                .fillColor(0x220000FF)
                .strokeWidth(5);



        mapCircle = mMap.addCircle(circleOptions);

        Location.distanceBetween(currentLocation.getLatitude(), currentLocation.getLongitude(),
                circleOptions.getCenter().latitude, circleOptions.getCenter().longitude, meters);

        if (meters[0] < circleOptions.getRadius()) {
            Toast.makeText(getBaseContext(), "Inside given radius", Toast.LENGTH_SHORT).show();

            marker = mMap.addMarker(new MarkerOptions()
                    .position(latLng)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))
                    .title("title"));

        }
    }*/

    /*========================================================================================*/
    @SuppressLint("MissingPermission")
    private Location getLastKnownLocation() {

        Log.i("Info", "getLastKnownLocation starts");

        locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
        List<String> providers = locationManager.getProviders(true);
        Location bestLocation = null;

        for (String provider : providers) {

            Location location = null;

            if (mLocationPermissionGranted) {

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

    /*========================================================================================*/
    @Override
    public void onMapLongClick(LatLng latLng) {


        //displaySelectionWindow();
    }

    /*========================================================================================*/
    public void addMarkerToMap() {

        if(marker != null){
            Toast.makeText(context, "Please approve or delete your pin!", Toast.LENGTH_SHORT).show();
            return;
        }

        isPinThrowClicked = true;
        centerMapOnLocation();

        markerLocation = getLastKnownLocation();
        markerLatlng = new LatLng(markerLocation.getLatitude(), markerLocation.getLongitude());

        MarkerOptions markerOptions = new MarkerOptions()
                .position(markerLatlng)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));

        marker = mMap.addMarker(markerOptions);

        markerAddress = FirebaseLocationAdapter.getAddress();

        Toast.makeText(this, "Location Saved", Toast.LENGTH_SHORT).show();

        createPopupWindow();
    }




    /*========================================================================================*/
    /*public void showItemSelectionDialog() {

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
        adapter.add("  Picture");
        adapter.add("  Video");
        adapter.add("  Description");
        AlertDialog.Builder builder = new AlertDialog.Builder(PinThrowActivity.this, android.R.style.Theme_Material_Dialog);
        builder.setTitle("Choose a profile photo");

        builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {

                Log.i("Info", "  >>Item selected:" + item);

                switch (item) {

                    case DIALOG_PICTURE_SELECTED:

                        startGalleryProcess();
                        break;

                    case DIALOG_VIDEO_SELECTED:

                        break;

                    case DIALOG_DESCRIPTION_SELECTED:

                        break;

                    default:

                        break;

                }
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }*/

    /*========================================================================================*/
    private void startGalleryProcess() {

        Log.i("Info", "startGalleryProcess");

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), MY_PERMISSION_ACTION_GET_CONTENT);
    }

    /*========================================================================================*/
    @Override
    public void onComplete(String key, DatabaseError error) {

    }

    /*========================================================================================*/
    @Override
    public boolean onMarkerClick(Marker marker) {

        Log.i("Info", "onMarkerClick starts");

        isMarkerClicked = true;

        /*if (popupWindow.isShowing()) {
            popupWindow.dismiss();
        } else {
            showPopupWindow();
        }*/

        return false;
    }

    /*========================================================================================*/
    @Override
    public void onCameraChange(CameraPosition cameraPosition) {
        try {

            Log.i("Info", "onCameraChange starts");

            if (popupWindow.isShowing()) {

                if (!isPinThrowClicked && !isMarkerClicked)
                    popupWindow.dismiss();
            } else {
                if(isMarkerClicked)
                    showPopupWindow();
            }

            isMarkerClicked = false;
            isPinThrowClicked = false;

        } catch (Exception e) {
            Log.i("Info", "     >>onCameraChanged Error:" + e.toString());
        }
    }

    /*========================================================================================*/
    @Override
    protected void onStop() {
        //handler.removeCallbacks(runnable);

        try {
            Log.i("Info", "onStop============");
            super.onStop();

        } catch (Exception e) {
            Log.i("Info", "     >>onStop Error:" + e.toString());
        }
    }

    /*========================================================================================*/
    @Override
    protected void onStart() {
        super.onStart();
    }

    /*========================================================================================*/
    @Override
    public void onClick(View v) {
        int i = v.getId();

        switch (i) {
            case R.id.nextButton:  //Next button on map
                startProfilePage();
                break;

            case R.id.pinThrowButton: //Pin throw button on map
                addMarkerToMap();
                break;

            case R.id.mapRelativeLayout:  //Map framelayout layout
                checkPopupShown();
                break;

            case R.id.gotItButton:  //Second demo page got it button
                Toast.makeText(context, "got it button clicked", Toast.LENGTH_SHORT).show();
                enableMapItems();
                pinMarkedApproved = true;
                break;

            case R.id.appOkButton:  //First demo page yes button
                showDemoPageFirst();
                break;

            case R.id.appCancelButton: //First demo page no button
                startProfilePage();
                break;

            case R.id.videoImageView: //Video Imageview on popup window
                addVideo();
                break;

            case R.id.noteImageView:  //Note Imageview on popup window
                handleNoteText();
                break;

            default:
                break;
        }
    }

    /*========================================================================================*/
    private void handleNoteText() {

        popupWindow.dismiss();

        LayoutInflater inflater = getLayoutInflater();
        noteTextLayout = inflater.inflate(R.layout.default_notetext_window, mapRelativeLayout, false);

        FloatingActionButton textAppOkButton = (FloatingActionButton) noteTextLayout.findViewById(R.id.textApproveFab);
        FloatingActionButton textAppCancelButton = (FloatingActionButton) noteTextLayout.findViewById(R.id.textCancelFab);
        LinearLayout noteTextMainLayout = (LinearLayout) noteTextLayout.findViewById(R.id.noteTextMainLayout);

        mapRelativeLayout.addView(noteTextLayout);

        noteTextMainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyBoard();
            }
        });

        textAppCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("Log", "  >>textAppCancelButton clicked");

                showPopupWindow();
                noteTextImageView.setImageResource(R.drawable.text_icon80);
                mapRelativeLayout.removeView(noteTextLayout);
                editTextBitmap = null;
            }
        });

        textAppOkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("Log", "  >>textAppOkButton clicked");

                showPopupWindow();
                ScrollView noteScrollView = (ScrollView) noteTextLayout.findViewById(R.id.noteScrollView);
                editTextBitmap = getScreenShot(noteScrollView);
                editTextBitmap = BitmapConversion.getRoundedShape(editTextBitmap, 700, 700);
                noteTextImageView.setImageBitmap(editTextBitmap);
                mapRelativeLayout.removeView(noteTextLayout);
            }
        });
    }

    public static Bitmap getScreenShot(View view) {
        /*View screenView = view.getRootView();
        screenView.setDrawingCacheEnabled(true);
        Bitmap bitmap = Bitmap.createBitmap(screenView.getDrawingCache());
        screenView.setDrawingCacheEnabled(false);
        return bitmap;*/

        view.setDrawingCacheEnabled(true);
        Bitmap bitmap = Bitmap.createBitmap(view.getDrawingCache());
        view.setDrawingCacheEnabled(false);
        return bitmap;
    }

    /*========================================================================================*/
    private void createPopupWindow() {

        popupMainView = getLayoutInflater().inflate(R.layout.default_marker_info_window, null);

        ViewFlipper markerInfoContainer = (ViewFlipper) popupMainView.findViewById(R.id.markerInfoContainer);

        markerInfoLayout = getLayoutInflater().inflate(R.layout.default_marker_info_layout, null);

        markerInfoContainer.addView(markerInfoLayout);

        videoImageView = (ImageView) markerInfoLayout.findViewById(R.id.videoImageView);
        noteTextImageView = (ImageView) markerInfoLayout.findViewById(R.id.noteImageView);
        pinApproveFab = (FloatingActionButton) markerInfoLayout.findViewById(R.id.approveFab);
        pinCancelFab = (FloatingActionButton) markerInfoLayout.findViewById(R.id.cancelFab);


        videoImageView.setOnClickListener(this);
        noteTextImageView.setOnClickListener(this);

        pinApproveFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                pinApproved();
            }
        });

        pinCancelFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                initializeValues();
                isCancelPinCheck = true;
                showYesNoDialog(null, "Pin'i silmek istediginize emin misiniz?");
            }
        });

        popupWindow = new PopupWindow(popupMainView,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

        showPopupWindow();
    }

    /* Pin approved and items saved to Firebase database*/
    public void pinApproved(){

        addItemsToFirebaseDatabase();

        if(!itemsAddedToFB)
            return;

        checkPopupShown();

        AlertDialog.Builder builder = new AlertDialog.Builder(context, android.R.style.Theme_Material_Dialog_Alert);
        builder.setTitle("CONGRATULATIONS :)");
        builder.setIcon(R.drawable.approve_icon);
        builder.setMessage("You added your first pin. Now we will direct you to next page");
        final AlertDialog alert = builder.create();
        alert.show();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //do your work here after 60 second
                alert.dismiss();
                startProfilePage();
            }
        }, 2000);
    }



    public void showPopupWindow() {

        Point p = mMap.getProjection().toScreenLocation(marker.getPosition());

        Point size = new Point();
        popupMainView.measure(size.x, size.y);

        mWidth = popupMainView.getMeasuredWidth();
        mHeight = popupMainView.getMeasuredHeight();

        popupWindow.showAtLocation(findViewById(R.id.map),
                Gravity.NO_GRAVITY, p.x - mWidth / 2, p.y - mHeight - 65);
    }

    public void showDemoPageFirst() {

        mapRelativeLayout.removeView(secondDemoLayout);

        // inflate (create) another copy of our custom layout
        LayoutInflater inflater = getLayoutInflater();
        firstDemoLayout = inflater.inflate(R.layout.default_pinthrow_demofirst, mapRelativeLayout, false);

        Button gotItButton = (Button) firstDemoLayout.findViewById(R.id.gotItButton);
        gotItButton.setOnClickListener(PinThrowActivity.this);

        // add our custom layout to the main layout
        mapRelativeLayout.addView(firstDemoLayout);
    }

    private void showDemoPageSecond() {

        disableMapItems();

        LayoutInflater inflater = getLayoutInflater();
        secondDemoLayout = inflater.inflate(R.layout.default_pinthrow_demosec, mapRelativeLayout, false);

        Button appOkButton = (Button) secondDemoLayout.findViewById(R.id.appOkButton);
        Button appCancelButton = (Button) secondDemoLayout.findViewById(R.id.appCancelButton);

        appOkButton.setOnClickListener(PinThrowActivity.this);
        appCancelButton.setOnClickListener(PinThrowActivity.this);

        mapRelativeLayout.addView(secondDemoLayout);
    }

    private void disableMapItems() {

        nextButton.setEnabled(false);
        pinThrowButton.setEnabled(false);
        mMap.getUiSettings().setScrollGesturesEnabled(false);
    }

    private void enableMapItems() {

        nextButton.setEnabled(true);
        pinThrowButton.setEnabled(true);
        mMap.getUiSettings().setScrollGesturesEnabled(true);
        mapRelativeLayout.removeView(firstDemoLayout);
    }

    @Override
    public void onMapClick(LatLng latLng) {

        checkPopupShown();
    }

    public void checkPopupShown() {

        if (popupWindow != null) {
            if (popupWindow.isShowing()) {
                popupWindow.dismiss();
                //popupWindow = null;
            }
        }
    }

    private void updatePopup() {

        Log.i("Info", "   >>popupWindow not null");

        if (marker != null && popupWindow != null) {
            // marker is visible
            if (mMap.getProjection().getVisibleRegion().latLngBounds.contains(marker.getPosition())) {

                Point p = mMap.getProjection().toScreenLocation(marker.getPosition());

                if (!popupWindow.isShowing()) {
                    popupWindow.showAtLocation(popupMainView, Gravity.NO_GRAVITY, p.x - mWidth / 2,
                            p.y - mHeight - 65);
                }

                popupWindow.update(p.x - mWidth / 2, p.y - mHeight - 65, -1, -1);

            } else { // marker outside screen
                popupWindow.dismiss();
            }
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
    public void onLocationChanged(Location location) {

        if (mapRipple.isAnimationRunning())
            mapRipple.withLatLng(new LatLng(location.getLatitude(), location.getLongitude()));

    }

    public void startProfilePage() {

        Intent intent = new Intent(getApplicationContext(), ProfilePageActivity.class);
        finish();
        startActivity(intent);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    private void cancelPinSave() {

        mMap.clear();

        if (popupWindow != null) {

            if (popupWindow.isShowing()) {
                popupWindow.dismiss();
            }
            popupWindow = null;
        }

        pinThrowButton.setEnabled(true);
        marker = null;
    }

    private void initializeValues() {

        isCancelPinCheck = false;
    }

    public void showYesNoDialog(String title, String message){

        AlertDialog.Builder builder = new AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert);

        builder.setIcon(R.drawable.warning_icon40);
        builder.setMessage(message);

        if(title != null)
            builder.setTitle(title);

        builder.setPositiveButton("EVET", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if(isCancelPinCheck){
                    cancelPinSave();
                }
            }
        });

        builder.setNegativeButton("KAPAT", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    public void hideKeyBoard(){

        Log.i("Info", "hideKeyBoard");

        InputMethodManager inputMethodManager =(InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
    }

    private void addVideo() {

        if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE_EXTERNAL_STORAGE);
            }
        }


        if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{android.Manifest.permission.CAMERA}, REQUEST_VIDEO_CAPTURE);
            } else {
                dispatchTakeVideoIntent();
            }
        } else {
            dispatchTakeVideoIntent();

        }
    }

    private void dispatchTakeVideoIntent() {

        try {
            if (!hasCamera()) {
                Toast.makeText(context, "Device has no camera!", Toast.LENGTH_SHORT).show();
                return;
            }

            chooseVideoProperty();

        } catch (Exception e) {
            Log.i("Info", "  >>dispatchTakeVideoIntent error:" + e.toString());
        }
    }

    private void chooseVideoProperty() {

        Log.i("Info", "chooseVideoProperty");

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
        adapter.add("  Open Camera");
        adapter.add("  Open Galery");
        AlertDialog.Builder builder = new AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert);
        builder.setTitle("How to upload your video?");

        builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {

                Log.i("Info", "  >>Item selected:" + item);

                if (item == VIDEO_CAMERA_SELECTED) {

                    choseVideoFromCamera();

                } else if (item == VIDEO_GALLERY_SELECTED) {


                } else {
                    Toast.makeText(PinThrowActivity.this, "Item Selected Error!!", Toast.LENGTH_SHORT).show();
                }

            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    public void choseVideoFromCamera() {

        Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);

        if (takeVideoIntent.resolveActivity(getPackageManager()) != null) {

            startActivityForResult(takeVideoIntent, REQUEST_VIDEO_CAPTURE);
        }
    }

    public String getRealPathFromURI(Uri contentUri) {
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = managedQuery(contentUri, proj, null, null, null);
        int column_index = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == REQUEST_VIDEO_CAPTURE && resultCode == RESULT_OK) {

            videoUri = intent.getData();

            videopath = getRealPathFromURI(videoUri);


            if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_READ_EXTERNAL_STORAGE);
                } else {
                    setBitmapFromUriForVideo();
                }
            } else {
                setBitmapFromUriForVideo();
            }












            //try {
            //    bitmapxx = MediaStore.Images.Media.getBitmap(getContentResolver(), videoUri);
            //} catch (IOException e) {
            //    e.printStackTrace();
            //}

            //Bitmap photo = (Bitmap) intent.getExtras().get("data");

            //try {
            //    Bitmap bitmap = getThumbnail(videoUri);
            //} catch (IOException e) {
            //    e.printStackTrace();
            //}


            //Bundle extras = intent.getExtras();
            //Bitmap x = extras.getParcelable("data");









            pinVideoView = (VideoView) findViewById(R.id.pinVideoView);
            pinTextureView = (TextureView) findViewById(R.id.pinTextureView);

            MediaController mediacontroller = new MediaController(context);

            mediacontroller.setAnchorView(pinVideoView);


            pinVideoView.setMediaController(mediacontroller);

            ViewGroup.LayoutParams params = pinVideoView.getLayoutParams();
            params.height = 150;
            pinVideoView.setLayoutParams(params);

            pinVideoView.setVideoURI(videoUri);
            pinVideoView.requestFocus();





            /*MediaPlayer mPlayer = new MediaPlayer();
            try {
                mPlayer.setDataSource( videoUri.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
            int width = mPlayer.getVideoWidth();
            int height = mPlayer.getVideoHeight();
            Bitmap bMap = Bitmap.createBitmap( width, height, Bitmap.Config.ARGB_4444 );

            videoImageView.setImageBitmap(bMap);*/








            pinVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                public void onPrepared(MediaPlayer mp) {

                    /*MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
                    mediaMetadataRetriever.setDataSource(videoUri.getPath());
                    try {
                        Bitmap bitmap = mediaMetadataRetriever.getFrameAtTime(100);
                        videoImageView.setImageBitmap(bitmap);

                    } catch (OutOfMemoryError outOfMemoryError) {
                        //Not entirely sure if this will ever be thrown but better safe than sorry.
                        pinVideoView.seekTo(100);
                    }*/

                    mp.start();

                    //int width = mp.getVideoWidth();
                    //int height = mp.getVideoHeight();
                    //Bitmap bMap = Bitmap.createBitmap( width, height, Bitmap.Config.ARGB_4444 );

//                    videoImageView.setImageBitmap(bMap);

                    /*mp.seekTo(100);







                    pinVideoView.start();

                    pinVideoView.seekTo(100);



                    Bitmap a = getScreenShot(pinVideoView);

                    try {
                        Log.i("Info", "mp.getVideoWidth(); : " + mp.getVideoWidth());

                        videoImageView.setImageBitmap(a);
                        pinVideoView.stopPlayback();
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.i("Info", "exception : " + e.toString());
                    }*/
                }
            });
        }
    }

    void savefile(Uri sourceuri)
    {
        String sourceFilename= sourceuri.getPath();
        String destinationFilename = android.os.Environment.getExternalStorageDirectory().getPath()+File.separatorChar+"abc.mp4";

        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;

        try {
            bis = new BufferedInputStream(new FileInputStream(sourceFilename));
            bos = new BufferedOutputStream(new FileOutputStream(destinationFilename, false));
            byte[] buf = new byte[1024];
            bis.read(buf);
            do {
                bos.write(buf);
            } while(bis.read(buf) != -1);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (bis != null) bis.close();
                if (bos != null) bos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void createDirectoryFolder(){

        String folder = Environment.getExternalStorageDirectory().toString();
        File saveFolder = new File(folder + "/Movies/new /");
        if(!saveFolder.exists()){
            saveFolder.mkdirs();
        }
    }

    public void saveFrames(ArrayList<Bitmap> saveBitmapList) throws IOException{

        String folder = Environment.getExternalStorageDirectory().toString();
        File saveFolder = new File(folder + "/Movies/new /");
        if(!saveFolder.exists()){
            saveFolder.mkdirs();
        }


        int i=1;
        for (Bitmap b : saveBitmapList){
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            b.compress(Bitmap.CompressFormat.JPEG, 40, bytes);

            File f = new File(saveFolder,"frame"+i+".jpg");

            f.createNewFile();

            FileOutputStream fo = new FileOutputStream(f);
            fo.write(bytes.toByteArray());

            fo.flush();
            fo.close();

            i++;
        }

    }


    private void setBitmapFromUriForVideo() {

        //FFmpegMediaMetadataRetriever retriever = new FFmpegMediaMetadataRetriever();
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();

        try {

            retriever.setDataSource(videopath);
            Bitmap bitmap = retriever.getFrameAtTime(100);
            //Bitmap x = ThumbnailUtils.createVideoThumbnail(uri.getPath(), MediaStore.Images.Thumbnails.MINI_KIND);

            bitmap = BitmapConversion.getRoundedShape(bitmap, 60, 60);

            /*int sourceWidth = bitmap.getWidth();
            int sourceHeight = bitmap.getHeight();

            float scale = Math.max(1.0f, 1.0f);

            float scaledWidth = scale * sourceWidth;
            float scaledHeight = scale * sourceHeight;

            float left = (60 - scaledWidth) / 2;
            float top = (60 - scaledHeight) / 2;

            RectF targetRect = new RectF(left, top, left + scaledWidth, top + scaledHeight);

            Bitmap dest = Bitmap.createBitmap(60, 60, bitmap.getConfig());
            Canvas canvas = new Canvas(dest);
            canvas.drawBitmap(bitmap, null, targetRect, null);*/

            videoImageView.setImageBitmap(bitmap);


        } catch (Exception e) {
            e.printStackTrace();
            Log.i("Info", "  >>getVideoFrame IllegalArgumentException:" + e.toString());
        }

    }

    private boolean hasCamera() {
        if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)) {
            return true;
        } else {
            return false;
        }
    }

    public Bitmap getVideoFrame(Uri uri) {


        /*try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver() , Uri.parse(uri.toString()));
            return bitmap;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;*/


        //MediaMetadataRetriever retriever = new MediaMetadataRetriever();

        FFmpegMediaMetadataRetriever retriever = new FFmpegMediaMetadataRetriever();

        try {

            retriever.setDataSource(uri.toString());
            Bitmap c = retriever.getFrameAtTime(5000000);
            //Bitmap x = ThumbnailUtils.createVideoThumbnail(uri.getPath(), MediaStore.Images.Thumbnails.MINI_KIND);

            return c;

        } catch (Exception e) {
            e.printStackTrace();
            Log.i("Info", "  >>getVideoFrame IllegalArgumentException:" + e.toString());
        }

        return null;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
        mediaMetadataRetriever.setDataSource(videoUri.getPath());
        try {
            Bitmap bitmap = mediaMetadataRetriever.getFrameAtTime(0);


            pinVideoView.setBackgroundDrawable(new BitmapDrawable(bitmap));

        } catch (OutOfMemoryError e) {
            //Not entirely sure if this will ever be thrown but better safe than sorry.
            pinVideoView.seekTo(0);
            Log.i("Info", "  >>onPrepared OutOfMemoryError:" + e.toString());
        }
    }

    public Bitmap getThumbnail(Uri uri) throws FileNotFoundException, IOException {
        InputStream input = this.getContentResolver().openInputStream(uri);

        BitmapFactory.Options onlyBoundsOptions = new BitmapFactory.Options();
        onlyBoundsOptions.inJustDecodeBounds = true;
        onlyBoundsOptions.inDither = true;//optional
        onlyBoundsOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;//optional
        BitmapFactory.decodeStream(input, null, onlyBoundsOptions);
        input.close();

        if ((onlyBoundsOptions.outWidth == -1) || (onlyBoundsOptions.outHeight == -1)) {
            return null;
        }

        int originalSize = (onlyBoundsOptions.outHeight > onlyBoundsOptions.outWidth) ? onlyBoundsOptions.outHeight : onlyBoundsOptions.outWidth;

        double ratio = (originalSize > 3) ? (originalSize / 3) : 1.0;

        BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
        bitmapOptions.inSampleSize = getPowerOfTwoForSampleRatio(ratio);
        bitmapOptions.inDither = true; //optional
        bitmapOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;//
        input = this.getContentResolver().openInputStream(uri);
        Bitmap bitmap = BitmapFactory.decodeStream(input, null, bitmapOptions);
        input.close();
        return bitmap;
    }

    private static int getPowerOfTwoForSampleRatio(double ratio) {
        int k = Integer.highestOneBit((int) Math.floor(ratio));
        if (k == 0) return 1;
        else return k;
    }

    private void addItemsToFirebaseDatabase() {

        String itemid;
        itemid = saveCurrLocation();
        saveLocationIDToUserLocations(itemid);

    }

    /* Marker's detail information saved to firebase database(locations root) **********************/
    public String saveCurrLocation() {

        if(!itemsAddedToFB)
            return null;

        Log.i("Info", "saveCurrLocation============");
        Log.i("Info", "     >>userId:" + FBuserId);

        Log.i("Info", "     >>Latlng latitude :" + markerLatlng.latitude);
        Log.i("Info", "     >>Latlng longitude:" + markerLatlng.longitude);

        try {
            geoLocation = new GeoLocation(markerLatlng.latitude, markerLatlng.longitude);

            mDbref = FirebaseDatabase.getInstance().getReferenceFromUrl(GEO_FIRE_LOCATIONS);

            String itemId = mDbref.child("locations").push().getKey();

            Log.i("Info", "     >>itemId:" + itemId);

            Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
            FirebaseLocationAdapter firebaseLocationAdapter = new FirebaseLocationAdapter();
            firebaseLocationAdapter.saveLocationInfo(geocoder, markerLatlng, FBuserId, itemId, geoLocation, mDbref);

            return itemId;

        } catch (Exception e) {
            itemsAddedToFB = false;
            Log.i("Info", "     >>SaveCurrLocation Error:" + e.toString());
            return null;
        }
    }

    /* Marker's location id is saved to firebase database(user_locations) **********************/
    private void saveLocationIDToUserLocations(String locationId) {

        if(!itemsAddedToFB)
            return;

        try{
            mDbref = FirebaseDatabase.getInstance().getReferenceFromUrl(GEO_FIRE_USER_LOCATIONS);

            String itemId = mDbref.child("user_locations").push().getKey();

            FirebaseLocationAdapter firebaseLocationAdapter = new FirebaseLocationAdapter();
            firebaseLocationAdapter.saveUserLocation(mDbref,locationId, FBuserId);


        }catch (Exception e){
            itemsAddedToFB = false;
            Log.i("Info", "     >>saveUserLocation Error:" + e.toString());
        }
    }
}
