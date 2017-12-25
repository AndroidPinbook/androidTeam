package uur.com.pinbook.Activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.location.Criteria;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
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
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.arsy.maps_library.MapRipple;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import uur.com.pinbook.Controller.BitmapConversion;
import uur.com.pinbook.Controller.CustomDialogAdapter;
import uur.com.pinbook.Controller.FirebaseLocationAdapter;
import uur.com.pinbook.Controller.LocationTrackerAdapter;
import uur.com.pinbook.R;


public class PinThrowActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener, GoogleMap.OnMarkerClickListener
        , GeoFire.CompletionListener, GeoQueryEventListener, GoogleMap.OnCameraChangeListener, GoogleMap.OnMapClickListener,
        View.OnClickListener, LocationListener{

    public GoogleMap mMap;

    LocationManager locationManager;
    //LocationListener locationListener;

    private GeoFire geoFire;
    private GeoQuery geoQuery;
    private GeoLocation geoLocation;

    private FrameLayout mapRelativeLayout;
    private View popupMainView = null;
    private View popupSelectionView = null;
    private int mWidth;
    private int mHeight;


    private Marker marker;
    private MapView mapView;

    private String markerAddress;

    private Map<String, Marker> markers;

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

    private MapRipple mapRipple;

    private FloatingActionButton btnSave;
    private FloatingActionButton btnCancel;
    private ImageView imgPicture;
    private ImageView imgVideo;
    private ImageView imgText;


    public String photoChoosenType = "";

    private Button continueWithEmailVerifButton;
    private ImageView photoImageView;
    private InputStream profileImageStream;
    private StorageReference riversRef;

    private Bitmap photo = null;
    private Uri tempUri = null;

    private static final int  MY_PERMISSION_CAMERA = 1;
    private static final int  MY_PERMISSION_WRITE_EXTERNAL_STORAGE = 2;
    private static final int  MY_PERMISSION_READ_EXTERNAL_STORAGE = 3;

    private static final int PROFILE_PIC_CAMERA_SELECTED = 0;
    private static final int PROFILE_PIC_GALLERY_SELECTED = 1;
    private static final int VIDEO_CAMERA_SELECTED = 0;
    private static final int VIDEO_GALLERY_SELECTED = 1;

    private TextView chooseProfilePicTextView;


    /*========================================================================================*/
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        try {

            switch (requestCode) {

                case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION:
                    if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        mLocationPermissionGranted = true;

                        /*if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                            Log.i("Info", "     >>permission is ok");
                            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                            centerMapOnLocation();
                        }*/

                        initializeMap(mMap);
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

        Log.i("Info", "centerMapOnLocation============");

        if (!mLocationPermissionGranted) {

            locationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER,
                    0,
                    0, (android.location.LocationListener) context);
        }

        Log.i("Info", "     >>centerMapOnLocation will go on");

        Location currentLocation = getLastKnownLocation();

        LatLng userLocation = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());

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

            nextButton = (FloatingActionButton) findViewById(R.id.nextButton);
            pinThrowButton = (FloatingActionButton) findViewById(R.id.pinThrowButton);
            mapRelativeLayout = (FrameLayout) findViewById(R.id.mapRelativeLayout);
            nextButton.setOnClickListener(this);
            pinThrowButton.setOnClickListener(this);
            mapRelativeLayout.setOnClickListener(this);

            currentUser = mAuth.getCurrentUser();
            mAuth = FirebaseAuth.getInstance();
            FBuserId = currentUser.getUid();




            FirebaseOptions options = new FirebaseOptions.Builder().
                    setApplicationId(appId).setDatabaseUrl(GEO_FIRE_REF).build();

            Log.i("Info", "     >>FirebaseOptions.AppId:" + options.getApplicationId());


            //this.geoFire = new GeoFire(FirebaseDatabase.getInstance().getReferenceFromUrl(GEO_FIRE_REF));
            //this.geoQuery = this.geoFire.queryAtLocation(INITIAL_CENTER, 1);

        } catch (Exception e) {
            Log.i("Info", "     >>onCreate try error:" + e.toString());
        }
    }

    /*========================================================================================*/
    @Override
    public void onMapReady(GoogleMap googleMap) {
        try {
            mMap = googleMap;
            //Disable Map Toolbar:
            mMap.getUiSettings().setMapToolbarEnabled(false);

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

        } catch (Exception e) {
            Log.i("Info", "     >>onMapReadyException Error:" + e.toString());
        }

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
        }
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



        } catch (Exception e) {
            Log.i("Info", "     >>SaveCurrLocation Error:" + e.toString());
        }
    }

    /*========================================================================================*/
    public void addMarkerToMap(){

        isPinThrowClicked = true;
        centerMapOnLocation();
        Location location = getLastKnownLocation();
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

        MarkerOptions markerOptions = new MarkerOptions()
                .position(latLng)
                //.title(FirebaseLocationAdapter.getAddress())
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
        //.snippet("bu ne la");

        marker = mMap.addMarker(markerOptions);

        markerAddress = FirebaseLocationAdapter.getAddress();

        Toast.makeText(this, "Location Saved", Toast.LENGTH_SHORT).show();

        //displayPopupWindow();
    }


    /*========================================================================================*/
    @Override
    public boolean onMarkerClick(Marker marker) {

        Log.i("Info", "onMarkerClick starts");

        isMarkerClicked = true;

        if (popupWindow == null) {


            Log.i("Info", "  >>popupWindow null");
        } else {
            if (popupWindow.isShowing()) {
                popupWindow.dismiss();
                popupWindow = null;
                marker.hideInfoWindow();
            }
        }


        return false;
    }


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
    public void onKeyEntered(String key, GeoLocation location) {

        /*try {
            Log.i("Info", "onKeyEntered============");

            Marker marker = this.mMap.addMarker(new MarkerOptions().position(new LatLng(location.latitude, location.longitude)));
            this.markers.put(key, marker);
        } catch (Exception e) {
            Log.i("Info", "     >>onKeyEntered Error:" + e.toString());
        }*/
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


            //updatePopup();

            if (popupWindow == null) {
                Log.i("Info", "  >>onCameraChange popupWindow null");
                if (isMarkerClicked || isPinThrowClicked)
                    displayPopupWindow();
            } else if (popupWindow.isShowing()) {

                if (!isMarkerClicked || !isPinThrowClicked) {
                    popupWindow.dismiss();
                    popupWindow = null;
                }
            }

            isMarkerClicked = false;
            isPinThrowClicked = false;


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
        //handler.removeCallbacks(runnable);

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
                //centerMapOnLocation();
                //saveCurrLocation();
                addMarkerToMap();
                break;

            case R.id.mapRelativeLayout:
                checkPopupShown();
                break;

            case R.id.approveFab:
                Log.i("info ", "save btn clicked..");
                break;

            case R.id.cancelFab:
                Log.i("info ", "cancel btn clicked..");
                break;

            case R.id.pictureImageView:
                Log.i("info ", "picture img clicked..");
                imgPicture.startAnimation(AnimationUtils.loadAnimation(PinThrowActivity.this, R.anim.img_anim));
                startImageProcess();
                break;

            case R.id.videoImageView:
                Log.i("info ", "video img clicked..");
                imgVideo.startAnimation(AnimationUtils.loadAnimation(PinThrowActivity.this, R.anim.img_anim));
                startVideoProcess();
                break;

            case R.id.noteImageView:
                Log.i("info ", "note img clicked..");
                imgText.startAnimation(AnimationUtils.loadAnimation(PinThrowActivity.this, R.anim.img_anim));
                break;

            default:
                break;
        }
    }

    private void startVideoProcess() {

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
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("How to upload your video?");

        builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {

                Log.i("Info", "  >>Item selected:" + item);

                if (item == VIDEO_CAMERA_SELECTED) {

                    choseVideoFromCamera();

                } else if (item == VIDEO_GALLERY_SELECTED) {
                    choseVideoFromGallery();

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

    private boolean hasCamera() {
        if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)) {
            return true;
        } else {
            return false;
        }
    }

    public void choseVideoFromGallery() {

    }

    private void startImageProcess() {
        Log.i("Info", "   >> start Image Process");

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
        adapter.add("  Open Camera");
        adapter.add("  Open Galery");
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose a profile photo");

        builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {

                Log.i("Info", "  >>Item selected:" + item);

                if(item == PROFILE_PIC_CAMERA_SELECTED){

                    photoChoosenType = getResources().getString(R.string.camera);
                    startCameraProcess();

                }else if(item == PROFILE_PIC_GALLERY_SELECTED){

                    photoChoosenType = getResources().getString(R.string.gallery);
                    startGalleryProcess();

                }else {
                    CustomDialogAdapter.showDialogError(PinThrowActivity.this, "Choose error!");
                }

            }
        });

        AlertDialog alert = builder.create();
        alert.show();

    }

    public void startCameraProcess(){

        Log.i("Info", "startCameraProcess");

        if(!hasCamera()){
            Toast.makeText(this, "Device has no camera!", Toast.LENGTH_SHORT).show();
            return;
        }

        checkMediaStoragePermission();
    }

    private void checkMediaStoragePermission() {

        Log.i("Info","checkMediaStoragePermission");

        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED) {

            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSION_WRITE_EXTERNAL_STORAGE);
        }else{

            checkCameraPermission();
        }
    }

    private void checkCameraPermission() {

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){

            requestPermissions(new String[]{Manifest.permission.CAMERA},MY_PERMISSION_CAMERA);
        }else{

            Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
            startActivityForResult(intent, MY_PERMISSION_CAMERA);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        Log.i("Info", "onActivityResult starts");

        if(resultCode == Activity.RESULT_OK){

            switch (requestCode){

                case MY_PERMISSION_CAMERA:
                    manageProfilePicChoosen(data);
                    break;

                case MY_PERMISSION_ACTION_GET_CONTENT:
                    manageProfilePicChoosen(data);
                    break;

                default:
                    Toast.makeText(this, "requestCode error:" + requestCode, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void manageProfilePicChoosen(Intent data) {

        try {

            if(photoChoosenType == getResources().getString(R.string.camera)){

                photo = (Bitmap) data.getExtras().get("data");
                photo = BitmapConversion.getRoundedShape(photo, 600, 600);
                tempUri = getImageUri(getApplicationContext(), photo);

            }else if(photoChoosenType == getResources().getString(R.string.gallery)){

                tempUri = data.getData();
                profileImageStream = getContentResolver().openInputStream(tempUri);
                photo = BitmapFactory.decodeStream(profileImageStream);
                photo = BitmapConversion.getRoundedShape(photo, 600, 600);

            }

            imgPicture.setImageBitmap(photo);

            //saveProfilePicToFirebase();

        }catch (Exception e){
            Log.i("Info", "  >>manageProfilePicChoosen exception:" + e.toString());
        }
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        try {
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
            String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);

            return Uri.parse(path);
        }catch (Exception e){

            Log.i("Info", "getImageUri exception:" + e.toString());
            return null;
        }
    }

    /*========================================================================================*/
    @Override
    public void onPause() {
        super.onPause();
//        handler.removeCallbacks(runnable);
    }

    /*========================================================================================*/
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //handler.removeCallbacks(runnable);
    }

    /*========================================================================================*/
    private void displayPopupWindow() {

        ViewFlipper markerInfoContainer = null;
        View viewContainer = null;

        try {
            popupMainView = getLayoutInflater().inflate(R.layout.default_marker_info_window, null);

            markerInfoContainer = (ViewFlipper) popupMainView.findViewById(R.id.markerInfoContainer);

            viewContainer = getLayoutInflater().inflate(R.layout.default_marker_info_layout, null);

            btnSave = (FloatingActionButton) viewContainer.findViewById(R.id.approveFab);
            btnCancel = (FloatingActionButton) viewContainer.findViewById(R.id.cancelFab);
            imgPicture = (ImageView) viewContainer.findViewById(R.id.pictureImageView);
            imgVideo = (ImageView) viewContainer.findViewById(R.id.videoImageView);
            imgText = (ImageView) viewContainer.findViewById(R.id.noteImageView);

            btnSave.setOnClickListener(this);
            btnCancel.setOnClickListener(this);
            imgPicture.setOnClickListener(this);
            imgVideo.setOnClickListener(this);
            imgText.setOnClickListener(this);
        } catch (Exception e) {
            e.printStackTrace();
            Log.i("ErrorDisplay ", e.toString());
        }

        markerInfoContainer.addView(viewContainer);

        popupWindow = new PopupWindow(popupMainView,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

        Point p = mMap.getProjection().toScreenLocation(marker.getPosition());

        Point size = new Point();
        popupMainView.measure(size.x, size.y);

        mWidth = popupMainView.getMeasuredWidth();
        mHeight = popupMainView.getMeasuredHeight();

        popupWindow.showAtLocation(findViewById(R.id.map),
                Gravity.NO_GRAVITY, p.x - mWidth / 2, p.y - mHeight - 65); //map is the fragment on the activity layout where I put the map

    }

    @Override
    public void onMapClick(LatLng latLng) {

        checkPopupShown();
    }

    public void checkPopupShown() {
        if (popupWindow != null) {
            if (popupWindow.isShowing()) {
                popupWindow.dismiss();
                popupWindow = null;
            }

            Log.i("Info", "   >>onMapClick not null");
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

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }


}
