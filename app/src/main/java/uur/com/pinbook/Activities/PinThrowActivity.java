package uur.com.pinbook.Activities;

import android.Manifest;
import android.animation.ValueAnimator;
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
import android.graphics.Point;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Address;
import android.location.Criteria;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ScrollView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import uur.com.pinbook.Controller.BitmapConversion;
import uur.com.pinbook.Adapters.CustomDialogAdapter;
import uur.com.pinbook.FirebaseAdapters.FirebaseLocationAdapter;
import uur.com.pinbook.FirebaseAdapters.FirebasePinItemsAdapter;
import uur.com.pinbook.Adapters.LocationTrackerAdapter;
import uur.com.pinbook.Adapters.UriAdapter;
import uur.com.pinbook.FirebaseGetData.FirebaseGetAccountHolder;
import uur.com.pinbook.JavaFiles.PinData;
import uur.com.pinbook.JavaFiles.RegionBasedLocation;
import uur.com.pinbook.JavaFiles.UserLocation;
import uur.com.pinbook.R;
import static uur.com.pinbook.ConstantsModel.NumericConstant.*;


public class PinThrowActivity extends FragmentActivity implements
        OnMapReadyCallback,
        GoogleMap.OnMapLongClickListener,
        GoogleMap.OnMarkerClickListener,
        GeoFire.CompletionListener,
        GoogleMap.OnCameraChangeListener,
        GoogleMap.OnMapClickListener,
        View.OnClickListener,
        LocationListener,
        SensorEventListener,
        ValueAnimator.AnimatorUpdateListener{

    private static Animation rightSlideAnim;
    private static Animation leftSlideAnim;
    private static Animation upSlideAnim;

    private static Animation rightSlideDownAnim;
    private static Animation leftSlideDownAnim;
    private static Animation upSlideDownAnim;

    public GoogleMap mMap;

    LocationManager locationManager;

    private UriAdapter uriAdapter;

    private SensorManager mSensorMgr;

    private static FirebaseLocationAdapter firebaseLocationAdapter;
    private static FirebasePinItemsAdapter firebasePinItemsAdapter;
    private static UserLocation userLocation;
    private static RegionBasedLocation regionBasedLocation;

    LayoutInflater noteTextInflater = null;
    LayoutInflater videoInflater = null;
    LayoutInflater imageViewInflater = null;

    private static ImageView pinApproveImgv;
    private static ImageView pinDeleteImgv;
    private static ImageView noteTextApproveImgv;
    private static ImageView noteTextDeleteImgv;
    private static ImageView pinPictureApproveImgv;
    private static ImageView pinPictureDeleteImgv;
    private static ImageView pinThrowImgv;
    private static ImageView gotoNextPageImgv;

    private static ImageView pinFriendsImgv;
    private static ImageView pinOnlymeImgv;
    private static ImageView pinSpecialImgv;

    private long mLastShakeTime;

    private GeoLocation geoLocation;

    private boolean itemsAddedToFB = true;
    private boolean isCancelPinCheck = false;
    private boolean pinThrowImageClicked = false;

    private FrameLayout mapRelativeLayout;
    private View popupMainView = null;

    private Boolean imageChangedInd = false;
    private Boolean demoThirdPageShown = false;
    private Boolean demoFourPageShown = false;

    private int mWidth;
    private int mHeight;
    private String videoRealPath;
    private String imageRealPath;

    private View firstDemoLayout;
    private View secondDemoLayout;
    private View thirdDemoLayout;
    private View fourDemoLayout;
    private View noteTextLayout;
    private View imageViewLayout;

    private static View markerInfoLayout;

    private ImageView videoImageView;
    private ImageView noteTextImageView;
    private ImageView pictureImageView;

    private static EditText noteTextEditText;
    private static Bitmap editTextBitmap = null;

    private static ImageView pinPhotoImageView;


    private Marker marker;

    private boolean pinMarkedApproved = false;

    private String markerAddress;

    private Uri noteTextImageUri = null;

    private static PinData pinData;

    private boolean isMarkerClicked = false;
    private boolean isPinThrowClicked = false;

    private LocationTrackerAdapter locationTrackObj;

    private String noteText = null;

    private Circle searchCircle;
    private Circle mapCircle;

    private Context context;

    private ScrollView noteTextScrollView;

    private Handler handler;

    private static final int INITIAL_ZOOM_LEVEL = 14;

    private double radius = 1.0f;

    public Criteria criteria;
    public String bestProvider;

    private FirebaseAuth mAuth;
    private DatabaseReference mDbref;

    LatLng latLng;
    //private CircleOptions circleOptions;

    private LocationListener locationListener;
    private Runnable runnable = null;

    private FirebaseUser currentUser;
    private String FBUserID = null;

    private boolean mLocationPermissionGranted = false;

    private PopupWindow popupWindow = null;

    private MapRipple mapRipple;

    private static Uri videoUri = null;
    private static Uri imageUri = null;

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

                case PERMISSION_REQUEST_VIDEO_CAPTURE:
                    if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        dispatchTakeVideoIntent();
                    }
                    break;

                case PERMISSION_REQUEST_READ_EXTERNAL_STORAGE:
                    if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        setBitmapFromUriForVideo(null);
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
            imageUri = null;

            firebaseLocationAdapter = new FirebaseLocationAdapter();
            firebasePinItemsAdapter = new FirebasePinItemsAdapter();
            userLocation = new UserLocation();
            regionBasedLocation = new RegionBasedLocation();
            uriAdapter = new UriAdapter();

            locationTrackObj = new LocationTrackerAdapter(PinThrowActivity.this);

            if (!locationTrackObj.canGetLocation()) {
                locationTrackObj.showSettingsAlert();
            } else {
                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    checkLocationPermission();
                }
            }

            mSensorMgr = (SensorManager) getSystemService(SENSOR_SERVICE);
            Sensor accelerometer = mSensorMgr.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

            if (accelerometer != null)
                mSensorMgr.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);

            mapRelativeLayout = (FrameLayout) findViewById(R.id.mapRelativeLayout);
            mapRelativeLayout.setOnClickListener(this);

            gotoNextPageImgv = (ImageView) findViewById(R.id.gotoNextPageImgv);
            pinThrowImgv = (ImageView) findViewById(R.id.pinThrowImgv);

            pinFriendsImgv = (ImageView) findViewById(R.id.pinFriendsImgv);
            pinOnlymeImgv = (ImageView) findViewById(R.id.pinOnlymeImgv);
            pinSpecialImgv = (ImageView) findViewById(R.id.pinSpecialImgv);

            defineAnimations();

            gotoNextPageImgv.setOnClickListener(this);
            pinThrowImgv.setOnClickListener(this);

            pinFriendsImgv.setOnClickListener(this);
            pinOnlymeImgv.setOnClickListener(this);
            pinSpecialImgv.setOnClickListener(this);

        } catch (Exception e) {
            Log.i("Info", "     >>onCreate try error:" + e.toString());
        }
    }

    public String getFbUserID() {

        if(FBUserID != null)
            return FBUserID;

        if(!FirebaseGetAccountHolder.getInstance().getUserID().isEmpty()) {
            FBUserID = FirebaseGetAccountHolder.getInstance().getUserID();
            return FBUserID;
        }

        FirebaseAuth firebaseAuth;
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        FBUserID = currentUser.getUid();

        return FBUserID;
    }

    private void defineAnimations() {

        rightSlideAnim = AnimationUtils.loadAnimation(this, R.anim.slide_right_up_anim);
        leftSlideAnim = AnimationUtils.loadAnimation(this, R.anim.slide_left_up_anim);
        upSlideAnim = AnimationUtils.loadAnimation(this, R.anim.slide_up_anim);

        rightSlideDownAnim = AnimationUtils.loadAnimation(this, R.anim.slide_right_down_anim);
        leftSlideDownAnim = AnimationUtils.loadAnimation(this, R.anim.slide_left_down_anim);
        upSlideDownAnim = AnimationUtils.loadAnimation(this, R.anim.slide_down_anim);

        rightSlideAnim.setFillAfter(true);
        leftSlideAnim.setFillAfter(true);
        upSlideAnim.setFillAfter(true);

        rightSlideDownAnim.setFillAfter(true);
        leftSlideDownAnim.setFillAfter(true);
        upSlideDownAnim.setFillAfter(true);
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

            if (mLocationPermissionGranted)
                location = locationManager.getLastKnownLocation(provider);
            else
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);

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

    public void showDemoThirdPage() {

        disableMapItems();
        LayoutInflater inflater = getLayoutInflater();
        thirdDemoLayout = inflater.inflate(R.layout.default_pinthrow_demothird, mapRelativeLayout, false);

        Button gotItThirdButton = (Button) thirdDemoLayout.findViewById(R.id.gotItThirdButton);
        gotItThirdButton.setOnClickListener(PinThrowActivity.this);

        final ImageView demo3textImgView = (ImageView) thirdDemoLayout.findViewById(R.id.demo3textImgView);
        final ImageView demo3videoImgView = (ImageView) thirdDemoLayout.findViewById(R.id.demo3videoImgView);
        final ImageView demo3pictureImgView = (ImageView) thirdDemoLayout.findViewById(R.id.demo3pictureImgView);

        mapRelativeLayout.addView(thirdDemoLayout);

        demo3pictureImgView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                demo3pictureImgView.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.img_anim));
            }
        });
        demo3textImgView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                demo3textImgView.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.img_anim));
            }
        });
        demo3videoImgView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                demo3videoImgView.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.img_anim));
            }
        });
    }

    public void showDemoFourPage(){

        disableMapItems();
        LayoutInflater inflater = getLayoutInflater();
        fourDemoLayout = inflater.inflate(R.layout.default_pinthrow_demofour, mapRelativeLayout, false);

        Button gotItFourButton = (Button) fourDemoLayout.findViewById(R.id.gotItFourButton);
        mapRelativeLayout.addView(fourDemoLayout);

        gotItFourButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mapRelativeLayout.removeView(fourDemoLayout);




                //pinFriendsImgv.setVisibility(View.VISIBLE);
                //pinOnlymeImgv.setVisibility(View.VISIBLE);
                //pinSpecialImgv.setVisibility(View.VISIBLE);
                enableMapItems();
            }
        });
    }

    /*========================================================================================*/
    public void addMarkerToMap() {

        if (marker != null) {
            CustomDialogAdapter.showDialogWarning(this,
                    "Yeni PIN birakmak icin ekrandaki pini onaylayiniz veya siliniz");
            return;
        }

        pinData = new PinData();

        isPinThrowClicked = true;
        centerMapOnLocation();

        markerLocation = getLastKnownLocation();
        markerLatlng = new LatLng(markerLocation.getLatitude(), markerLocation.getLongitude());

        MarkerOptions markerOptions = new MarkerOptions()
                .position(markerLatlng)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));

        marker = mMap.addMarker(markerOptions);

        //Toast.makeText(this, "Location Saved", Toast.LENGTH_SHORT).show();

        createPopupWindow();
    }

    /*========================================================================================*/
    private void startGalleryProcess(String choosenType) {

        Log.i("Info", "startGalleryProcess");

        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);

        if (choosenType == "video") {
            intent.setType("video/*");
            startActivityForResult(Intent.createChooser(intent, "Select Video"), MY_PERMISSION_GET_VIDEO_GALLERY);
        } else if (choosenType == "image") {
            intent.setType("image/*");
            startActivityForResult(Intent.createChooser(intent, "Select Image"), MY_PERMISSION_GET_IMAGE_GALLERY);
        }

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

            if (popupWindow != null) {
                if (popupWindow.isShowing()) {
                    if (!isPinThrowClicked && !isMarkerClicked)
                        popupWindow.dismiss();
                }else if(isMarkerClicked)
                    showPopupWindow();
            } else {
                if (isMarkerClicked)
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
            case R.id.gotoNextPageImgv:  //Next button on map
                gotoNextPageImgv.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.img_anim));
                startProfilePage();
                break;

            case R.id.pinThrowImgv: //Pin throw button on map
                pinThrowImgv.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.img_anim));

                if(!pinThrowImageClicked) {
                    pinFriendsImgv.setVisibility(View.VISIBLE);
                    pinOnlymeImgv.setVisibility(View.VISIBLE);
                    pinSpecialImgv.setVisibility(View.VISIBLE);

                    pinFriendsImgv.startAnimation(rightSlideDownAnim);
                    pinOnlymeImgv.startAnimation(upSlideDownAnim);
                    pinSpecialImgv.startAnimation(leftSlideDownAnim);

                    pinThrowImageClicked = true;
                }else{
                    pinFriendsImgv.startAnimation(rightSlideAnim);
                    pinOnlymeImgv.startAnimation(upSlideAnim);
                    pinSpecialImgv.startAnimation(leftSlideAnim);

                    pinThrowImageClicked = false;
                }

                if(!demoFourPageShown){
                    showDemoFourPage();
                    demoFourPageShown = true;
                    //pinThrowImgv.setEnabled(false);
                    //pinThrowImgv.setVisibility(View.GONE);
                }
                break;

            case R.id.mapRelativeLayout:  //Map framelayout layout
                checkPopupShown();
                break;

            case R.id.gotItButton:  //Second demo page got it button
                //Toast.makeText(context, "got it button clicked", Toast.LENGTH_SHORT).show();
                enableMapItems();
                pinMarkedApproved = true;
                break;

            case R.id.gotItThirdButton:
                mapRelativeLayout.removeView(thirdDemoLayout);
                enableMapItems();
                addMarkerToMap();
                break;

            case R.id.appOkButton:  //First demo page yes button
                showDemoPageFirst();
                break;

            case R.id.appCancelButton: //First demo page no button
                startProfilePage();
                break;

            case R.id.videoImageView: //Video Imageview on popup window
                videoImageView.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.img_anim));
                addPinVideo();
                break;

            case R.id.pictureImageView:
                pictureImageView.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.img_anim));
                addPinImage();
                break;

            case R.id.noteImageView:  //Note Imageview on popup window
                noteTextImageView.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.img_anim));
                handleNoteText();
                break;

            case R.id.pinFriendsImgv:
                pinFriendsImgv.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.img_anim));
                if (!demoThirdPageShown) {
                    showDemoThirdPage();
                    demoThirdPageShown = true;
                } else
                    addMarkerToMap();
                break;

            case R.id.pinOnlymeImgv:
                pinOnlymeImgv.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.img_anim));
                if (!demoThirdPageShown) {
                    showDemoThirdPage();
                    demoThirdPageShown = true;
                } else
                    addMarkerToMap();
                break;

            case R.id.pinSpecialImgv:
                pinSpecialImgv.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.img_anim));
                if (!demoThirdPageShown) {
                    showDemoThirdPage();
                    demoThirdPageShown = true;
                } else
                    addMarkerToMap();
                break;

            default:
                break;
        }
    }

    private void addPinImage() {

        if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE);
            }
        }

        if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{android.Manifest.permission.CAMERA}, PERMISSION_REQUEST_VIDEO_CAPTURE);
            } else {
                if (imageUri == null)
                    dispatchTakeImageIntent();
                else
                    startImageItemShown();
            }
        } else {
            if (imageUri == null)
                dispatchTakeImageIntent();
            else
                startImageItemShown();
        }
    }

    private void startImageItemShown() {

        if (popupWindow != null && popupWindow.isShowing())
            popupWindow.dismiss();

        if (imageViewInflater == null) {
            imageViewInflater = getLayoutInflater();
            imageViewLayout = imageViewInflater.inflate(R.layout.default_image_window, mapRelativeLayout, false);
        }

        pinPictureApproveImgv = (ImageView) imageViewLayout.findViewById(R.id.pinPictureApproveImgv);
        pinPictureDeleteImgv = (ImageView) imageViewLayout.findViewById(R.id.pinPictureDeleteImgv);

        LinearLayout imageMainLayout = (LinearLayout) imageViewLayout.findViewById(R.id.imageMainLayout);

        pinPhotoImageView = (ImageView) imageViewLayout.findViewById(R.id.pinPhotoImageView);

        InputStream profileImageStream = null;
        try {
            profileImageStream = getContentResolver().openInputStream(pinData.getPinImageUri());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        Bitmap photo = BitmapFactory.decodeStream(profileImageStream);
        Bitmap bg1 = Bitmap.createScaledBitmap(photo, 500, 650, true);
        pinPhotoImageView.setImageBitmap(bg1);

        mapRelativeLayout.addView(imageViewLayout);

        imageMainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mapRelativeLayout.removeView(imageViewLayout);
                showPopupWindow();
            }
        });

        pinPhotoImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageChangedInd = true;
                chooseImageProperty();
            }
        });

        pinPictureDeleteImgv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pinPictureDeleteImgv.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.img_anim));
                imageUri = null;
                imageRealPath = null;
                pictureImageView.setImageResource(R.drawable.gallery_icon80);
                showPopupWindow();
                pinData.setPinImageUri(imageUri);
                pinData.setImageRealPath(imageRealPath);
                imageViewInflater = null;
                mapRelativeLayout.removeView(imageViewLayout);

                Log.i("Info", "    -->pindata.getImageUri     :" + pinData.getPinImageUri());
                Log.i("Info", "    -->pindata.getImageRealPath:" + pinData.getImageRealPath());
            }
        });

        pinPictureApproveImgv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pinPictureApproveImgv.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.img_anim));
                mapRelativeLayout.removeView(imageViewLayout);
                showPopupWindow();

                Log.i("Info", "    -->pindata.getImageUri     :" + pinData.getPinImageUri());
                Log.i("Info", "    -->pindata.getImageRealPath:" + pinData.getImageRealPath());
            }
        });
    }

    private void dispatchTakeImageIntent() {

        try {
            if (!hasCamera()) {
                Toast.makeText(context, "Device has no camera!", Toast.LENGTH_SHORT).show();
                return;
            }

            chooseImageProperty();

        } catch (Exception e) {
            Log.i("Info", "  >>dispatchTakeVideoIntent error:" + e.toString());
        }
    }

    private void chooseImageProperty() {

        Log.i("Info", "chooseImageProperty");

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
        adapter.add("  Open Camera");
        adapter.add("  Open Galery");

        AlertDialog.Builder builder = new AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert);
        builder.setTitle("How to upload your Picture?");

        builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {

                Log.i("Info", "  >>Item selected:" + item);

                if (item == IMAGE_CAMERA_SELECTED) {

                    choseImageFromCamera();

                } else if (item == IMAGE_GALLERY_SELECTED) {

                    startGalleryProcess("image");

                } else {
                    Toast.makeText(PinThrowActivity.this, "Item Selected Error!!", Toast.LENGTH_SHORT).show();
                }

            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    private void choseImageFromCamera() {

        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        startActivityForResult(intent, MY_PERMISSION_GET_IMAGE_CAMERA);
    }

    /*========================================================================================*/
    private void handleNoteText() {

        popupWindow.dismiss();

        if (noteTextInflater == null) {
            noteTextInflater = getLayoutInflater();
            noteTextLayout = noteTextInflater.inflate(R.layout.default_notetext_window, mapRelativeLayout, false);
        }

        noteTextApproveImgv = (ImageView) noteTextLayout.findViewById(R.id.noteTextApproveImgv);
        noteTextDeleteImgv = (ImageView) noteTextLayout.findViewById(R.id.noteTextDeleteImgv);

        LinearLayout noteTextMainLayout = (LinearLayout) noteTextLayout.findViewById(R.id.noteTextMainLayout);

        noteTextEditText = (EditText) noteTextLayout.findViewById(R.id.noteTextEditText);
        noteTextScrollView = (ScrollView) noteTextLayout.findViewById(R.id.noteScrollView);

        mapRelativeLayout.addView(noteTextLayout);

        noteTextMainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Boolean keybordHide = hideKeyBoard();

                if (!keybordHide) {
                    mapRelativeLayout.removeView(noteTextLayout);
                    showPopupWindow();
                }
            }
        });

        noteTextDeleteImgv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("Log", "  >>textAppCancelButton clicked");

                noteTextDeleteImgv.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.img_anim));
                noteTextEditText.setText(null);
                noteTextImageView.setImageResource(R.drawable.text_icon80);
                editTextBitmap = null;
                noteText = null;
                noteTextInflater = null;
                mapRelativeLayout.removeView(noteTextLayout);
                noteTextImageUri = null;
                showPopupWindow();
                pinData.setNoteText(noteText);
                pinData.setPinTextUri(noteTextImageUri);

                Log.i("Info", "    -->pindata.noteText:" + pinData.getNoteText());
                Log.i("Info", "    -->pindata.textUri :" + pinData.getPinTextUri());
            }
        });

        noteTextApproveImgv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("Log", "  >>textAppOkButton clicked");

                noteTextApproveImgv.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.img_anim));
                noteText = noteTextEditText.getText().toString();

                Log.i("Info", "  notetext null mu :" + noteText + ":");

                if (noteText == null)
                    Log.i("Info", "  notetext null mu :" + noteText + ":");

                if (noteText.equals("")) {
                    noteTextImageView.setImageResource(R.drawable.text_icon80);
                    editTextBitmap = null;
                    noteTextImageUri = null;
                    noteText = null;
                } else {
                    editTextBitmap = getScreenShot(noteTextScrollView);
                    noteTextImageUri = getImageUri(getApplicationContext(), editTextBitmap);
                    editTextBitmap = BitmapConversion.getRoundedShape(editTextBitmap, 700, 700, null);
                    noteTextImageView.setImageBitmap(editTextBitmap);
                }

                pinData.setNoteText(noteText);
                pinData.setPinTextUri(noteTextImageUri);
                mapRelativeLayout.removeView(noteTextLayout);
                showPopupWindow();

                Log.i("Info", "    -->pindata.noteText:" + pinData.getNoteText());
                Log.i("Info", "    -->pindata.textUri :" + pinData.getPinTextUri());
            }
        });
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        try {
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
            String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);

            return Uri.parse(path);
        } catch (Exception e) {

            Log.i("Info", "getImageUri exception:" + e.toString());
            return null;
        }
    }

    public static Bitmap getScreenShot(View view) {
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
        pictureImageView = (ImageView) markerInfoLayout.findViewById(R.id.pictureImageView);

        pinApproveImgv = (ImageView) markerInfoLayout.findViewById(R.id.pinApproveImgv);
        pinDeleteImgv = (ImageView) markerInfoLayout.findViewById(R.id.pinDeleteImgv);

        videoImageView.setOnClickListener(this);
        noteTextImageView.setOnClickListener(this);
        pictureImageView.setOnClickListener(this);

        pinApproveImgv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pinApproveImgv.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.img_anim));
                pinApproved();
            }
        });

        pinDeleteImgv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pinDeleteImgv.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.img_anim));
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
    public void pinApproved() {

        addItemsToFirebaseDatabase();

        if (!itemsAddedToFB)
            return;

        checkPopupShown();

        AlertDialog.Builder builder = new AlertDialog.Builder(context, android.R.style.Theme_Material_Dialog_Alert);
        builder.setTitle("Tebrikler :)");
        builder.setIcon(R.drawable.approve_icon);
        builder.setMessage("Ilk pininizi biraktiniz. Simdi bir sonraki sayfaya yonlendirileceksiniz");
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

        if (popupWindow == null)
            return;

        if (popupWindow.isShowing())
            return;

        Point p = mMap.getProjection().toScreenLocation(marker.getPosition());

        Point size = new Point();
        popupMainView.measure(size.x, size.y);

        mWidth = popupMainView.getMeasuredWidth();
        mHeight = popupMainView.getMeasuredHeight();

        showPinPopupWindow(findViewById(R.id.map), p);
    }

    public void showPinPopupWindow(View view, Point p){

        popupWindow.showAtLocation(popupMainView, Gravity.NO_GRAVITY, p.x - mWidth / 2,
                p.y - mHeight - singlePinThrowVal);
    }

    public void showDemoPageFirst() {

        mapRelativeLayout.removeView(secondDemoLayout);

        // inflate (create) another copy of our custom layout
        LayoutInflater inflater = getLayoutInflater();
        firstDemoLayout = inflater.inflate(R.layout.default_pinthrow_demofirst, mapRelativeLayout, false);

        Button gotItButton = (Button) firstDemoLayout.findViewById(R.id.gotItButton);
        gotItButton.setOnClickListener(PinThrowActivity.this);

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

        gotoNextPageImgv.setEnabled(false);
        pinThrowImgv.setEnabled(false);
        mMap.getUiSettings().setScrollGesturesEnabled(false);
        pinFriendsImgv.setEnabled(false);
        pinOnlymeImgv.setEnabled(false);
        pinSpecialImgv.setEnabled(false);
    }

    private void enableMapItems() {

        gotoNextPageImgv.setEnabled(true);
        pinThrowImgv.setEnabled(true);
        mMap.getUiSettings().setScrollGesturesEnabled(true);
        mapRelativeLayout.removeView(firstDemoLayout);
        pinFriendsImgv.setEnabled(true);
        pinOnlymeImgv.setEnabled(true);
        pinSpecialImgv.setEnabled(true);
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

    private void updatePopup() {

        Log.i("Info", "   >>popupWindow not null");

        if (marker != null && popupWindow != null) {
            // marker is visible
            if (mMap.getProjection().getVisibleRegion().latLngBounds.contains(marker.getPosition())) {

                Point p = mMap.getProjection().toScreenLocation(marker.getPosition());

                if (!popupWindow.isShowing()) {
                    showPinPopupWindow(popupMainView, p);
                }

                popupWindow.update(p.x - mWidth / 2, p.y - mHeight - singlePinThrowVal, -1, -1);

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

    private void deletePin() {

        mMap.clear();

        if (popupWindow != null) {

            if (popupWindow.isShowing()) {
                popupWindow.dismiss();
            }
            popupWindow = null;
        }

        videoUri = null;
        videoRealPath = null;
        imageUri = null;
        imageRealPath = null;
        noteTextImageUri = null;
        noteText = null;
        initializePinItems();
        marker = null;
        pinThrowImgv.setEnabled(true);

        if (noteTextEditText != null)
            noteTextEditText.setText(null);

    }

    private void initializePinItems() {

        pinData.setPinImageUri(null);
        pinData.setPinTextUri(null);
        pinData.setPinVideoUri(null);
        pinData.setNoteText(null);
        pinData.setVideoRealPath(null);
        pinData.setImageRealPath(null);
        pinData.setPinVideoImageUri(null);
    }

    private void initializeValues() {

        isCancelPinCheck = false;
    }

    public void showYesNoDialog(String title, String message) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert);

        builder.setIcon(R.drawable.warning_icon40);
        builder.setMessage(message);

        if (title != null)
            builder.setTitle(title);

        builder.setPositiveButton("EVET", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if (isCancelPinCheck) {
                    deletePin();
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

    public boolean hideKeyBoard() {

        Log.i("Info", "hideKeyBoard");

        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);

        if (getCurrentFocus() != null) {
            inputMethodManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            return true;
        }

        return false;
    }

    private void addPinVideo() {

        if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE);
            }
        }

        if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{android.Manifest.permission.CAMERA}, PERMISSION_REQUEST_VIDEO_CAPTURE);
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

        if (pinData.getPinVideoUri() != null) //Pin e video eklenmisse itemlara eklenir
        {
            adapter.add("  Play Video");
            adapter.add("  Delete Video");
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert);
        builder.setTitle("How to upload your video?");

        builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {

                Log.i("Info", "  >>Item selected:" + item);

                if (item == VIDEO_CAMERA_SELECTED) {

                    choseVideoFromCamera();

                } else if (item == VIDEO_GALLERY_SELECTED) {

                    startGalleryProcess("video");

                } else if (item == VIDEO_PLAY_SELECTED) {

                    playPinVideo();

                } else if (item == VIDEO_DELETE_SELECTED) {

                    //videoUri = null;
                    videoImageView.setImageResource(R.drawable.video_icon80);
                    pinData.setPinVideoUri(null);
                    pinData.setVideoRealPath(null);
                    pinData.setPinVideoImageUri(null);

                    Log.i("Info", "    -->pinData.getPinVideoUri     :" + pinData.getPinVideoUri());
                    Log.i("Info", "    -->pinData.getPinVideoRealPath:" + pinData.getVideoRealPath());

                } else {
                    Toast.makeText(PinThrowActivity.this, "Item Selected Error!!", Toast.LENGTH_SHORT).show();
                }

            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    public void playPinVideo() {

        Log.i("Info", "    -->pinData.getPinVideoUri     :" + pinData.getPinVideoUri());
        Log.i("Info", "    -->pinData.getPinVideoRealPath:" + pinData.getVideoRealPath());

        Intent intent = new Intent(PinThrowActivity.this, PlayVideoActivity.class);
        intent.putExtra("videoUri", pinData.getPinVideoUri().toString());
        startActivity(intent);
    }

    public void choseVideoFromCamera() {

        Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        takeVideoIntent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, videoDuration);

        if (takeVideoIntent.resolveActivity(getPackageManager()) != null) {

            startActivityForResult(takeVideoIntent, MY_PERMISSION_GET_VIDEO_CAMERA);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {

        if (requestCode == MY_PERMISSION_GET_IMAGE_GALLERY && resultCode == RESULT_OK) {

            manageImageFromGallery(intent);

        } else if (requestCode == MY_PERMISSION_GET_IMAGE_CAMERA && resultCode == RESULT_OK) {

            manageImageFromCamera(intent);

        } else if (requestCode == MY_PERMISSION_GET_VIDEO_GALLERY && resultCode == RESULT_OK) {

            manageVideoFromGallery(intent);

        } else if (requestCode == MY_PERMISSION_GET_VIDEO_CAMERA && resultCode == RESULT_OK) {

            manageVideoFromCamera(intent);

        }
    }

    private void manageImageFromCamera(Intent intent) {

        Bitmap imagePhoto = (Bitmap) intent.getExtras().get("data");
        imageUri = getImageUri(getApplicationContext(), imagePhoto);
        imageRealPath = getRealPathFromCameraURI(imageUri);
        pinData.setPinImageUri(imageUri);
        pinData.setImageRealPath(imageRealPath);
        imagePhoto = BitmapConversion.getRoundedShape(imagePhoto, 600, 600, imageRealPath);
        pictureImageView.setImageBitmap(imagePhoto);

        if (imageChangedInd) {
            mapRelativeLayout.removeView(imageViewLayout);
            imageChangedInd = false;
            showPopupWindow();
        }

        Log.i("Info", "    -->pindata.getImageUri     :" + pinData.getPinImageUri());
        Log.i("Info", "    -->pindata.getImageRealPath:" + pinData.getImageRealPath());
    }

    private void manageImageFromGallery(Intent intent) {

        try {
            imageUri = intent.getData();
            imageRealPath = UriAdapter.getPathFromGalleryUri(getApplicationContext(), imageUri);

            pinData.setPinImageUri(imageUri);
            pinData.setImageRealPath(imageRealPath);
            InputStream pinImageStream = null;
            pinImageStream = getContentResolver().openInputStream(imageUri);
            Bitmap photo = BitmapFactory.decodeStream(pinImageStream);
            photo = BitmapConversion.getRoundedShape(photo, 600, 600, imageRealPath);
            pictureImageView.setImageBitmap(photo);

            if (imageChangedInd) {
                mapRelativeLayout.removeView(imageViewLayout);
                imageChangedInd = false;
                showPopupWindow();
            }

            Log.i("Info", "    -->pindata.getImageUri     :" + pinData.getPinImageUri());
            Log.i("Info", "    -->pindata.getImageRealPath:" + pinData.getImageRealPath());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public String getRealPathFromCameraURI(Uri contentUri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = managedQuery(contentUri, proj, null, null, null);
        int column_index = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    private void manageVideoFromCamera(Intent intent) {

        videoUri = intent.getData();
        videoRealPath = getRealPathFromCameraURI(videoUri);
        pinData.setPinVideoUri(videoUri);
        pinData.setVideoRealPath(videoRealPath);

        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_READ_EXTERNAL_STORAGE);
            } else {
                setBitmapFromUriForVideo(null);
            }
        } else {
            setBitmapFromUriForVideo(null);
        }

        Log.i("Info", "    -->pinData.getPinVideoUri     :" + pinData.getPinVideoUri());
        Log.i("Info", "    -->pinData.getPinVideoRealPath:" + pinData.getVideoRealPath());
    }

    private void manageVideoFromGallery(Intent intent) {

        try {
            videoUri = intent.getData();
            videoRealPath = UriAdapter.getPathFromGalleryUri(getApplicationContext(), videoUri);
            setBitmapFromUriForVideo(getString(R.string.gallery));
            pinData.setPinVideoUri(videoUri);
            pinData.setVideoRealPath(videoRealPath);


            Log.i("Info", "    -->pinData.getPinVideoUri     :" + pinData.getPinVideoUri());
            Log.i("Info", "    -->pinData.getPinVideoRealPath:" + pinData.getVideoRealPath());

        } catch (Exception e) {
            e.printStackTrace();
            Log.i("Info", "  >>manageVideoFromGallery error:" + e.toString());
        }

    }


    // UPDATED!
    public String getPath(Uri uri) {
        String[] projection = {MediaStore.Video.Media.DATA};
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null) {
            // HERE YOU WILL GET A NULLPOINTER IF CURSOR IS NULL
            // THIS CAN BE, IF YOU USED OI FILE MANAGER FOR PICKING THE MEDIA
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } else
            return null;
    }


    void savefile(Uri sourceuri) {
        String sourceFilename = sourceuri.getPath();
        String destinationFilename = android.os.Environment.getExternalStorageDirectory().getPath() + File.separatorChar + "abc.mp4";

        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;

        try {
            bis = new BufferedInputStream(new FileInputStream(sourceFilename));
            bos = new BufferedOutputStream(new FileOutputStream(destinationFilename, false));
            byte[] buf = new byte[1024];
            bis.read(buf);
            do {
                bos.write(buf);
            } while (bis.read(buf) != -1);
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

    public void createDirectoryFolder() {

        String folder = Environment.getExternalStorageDirectory().toString();
        File saveFolder = new File(folder + "/Movies/new /");
        if (!saveFolder.exists()) {
            saveFolder.mkdirs();
        }
    }

    public void saveFrames(ArrayList<Bitmap> saveBitmapList) throws IOException {

        String folder = Environment.getExternalStorageDirectory().toString();
        File saveFolder = new File(folder + "/Movies/new /");
        if (!saveFolder.exists()) {
            saveFolder.mkdirs();
        }


        int i = 1;
        for (Bitmap b : saveBitmapList) {
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            b.compress(Bitmap.CompressFormat.JPEG, 40, bytes);

            File f = new File(saveFolder, "frame" + i + ".jpg");

            f.createNewFile();

            FileOutputStream fo = new FileOutputStream(f);
            fo.write(bytes.toByteArray());

            fo.flush();
            fo.close();

            i++;
        }

    }


    private boolean setBitmapFromUriForVideo(String chosenType) {

        MediaMetadataRetriever retriever = new MediaMetadataRetriever();

        try {
            retriever.setDataSource(videoRealPath);
            Bitmap bitmap = retriever.getFrameAtTime(100);

            if(chosenType == getString(R.string.gallery)){

                String time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
                long timeInMillisec = Long.parseLong(time );

                if(timeInMillisec > (videoDuration * 1000)) {
                    CustomDialogAdapter.showDialogWarning(this,
                            "Video suresi 20 saniyeden buyuk olamaz!");
                    videoUri = null;
                    videoRealPath = null;
                    videoImageView.setImageResource(R.drawable.video_icon80);
                    return false;
                }
            }

            pinData.setPinVideoImageUri(getImageUri(getApplicationContext(), bitmap));
            bitmap = BitmapConversion.getRoundedShape(bitmap, 600, 600, null);
            videoImageView.setImageBitmap(bitmap);
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            Log.i("Info", "  >>getVideoFrame IllegalArgumentException:" + e.toString());
            videoUri = null;
            videoRealPath = null;
            return false;
        }

    }

    private boolean hasCamera() {
        if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)) {
            return true;
        } else {
            return false;
        }
    }

    private void addItemsToFirebaseDatabase() {

        saveCurrLocation();
        saveLocationIDToUserLocations();
        saveRegionBasedLocation();
        savePinItems();
    }


    /* Marker's detail information saved to firebase database(locations root) **********************/
    public void saveCurrLocation() {

        if (!itemsAddedToFB)
            return;

        Log.i("Info", "     >>Latlng latitude :" + markerLatlng.latitude);
        Log.i("Info", "     >>Latlng longitude:" + markerLatlng.longitude);

        try {
            userLocation.setUserId(getFbUserID());
            userLocation.setLatitude(String.valueOf(markerLatlng.latitude));
            userLocation.setLongitude(String.valueOf(markerLatlng.longitude));
            userLocation.setLocation(markerLocation);

            Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
            List<Address> listAddresses = geocoder.getFromLocation(markerLatlng.latitude, markerLatlng.longitude, 1);

            if (listAddresses != null && listAddresses.size() > 0) {

                if (listAddresses.get(0).getThoroughfare() != null)
                    userLocation.setThoroughFare(listAddresses.get(0).getThoroughfare());

                if (listAddresses.get(0).getSubThoroughfare() != null)
                    userLocation.setSubThoroughfare(listAddresses.get(0).getSubThoroughfare());

                if (listAddresses.get(0).getPostalCode() != null)
                    userLocation.setPostalCode(listAddresses.get(0).getPostalCode());

                if (listAddresses.get(0).getCountryName() != null)
                    userLocation.setCountryName(listAddresses.get(0).getCountryName());

                if (listAddresses.get(0).getCountryCode() != null)
                    userLocation.setCountryCode(listAddresses.get(0).getCountryCode());

                if(listAddresses.get(0).getAdminArea() != null)
                    userLocation.setCity(listAddresses.get(0).getAdminArea());

                if (listAddresses.get(0).getCountryCode() != null)
                    regionBasedLocation.setCountryCode(listAddresses.get(0).getCountryCode());

                if (listAddresses.get(0).getAdminArea() != null)
                    regionBasedLocation.setCity(listAddresses.get(0).getAdminArea());
            }

            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm yyyy-MM-dd");
            userLocation.setLocTimestamp(sdf.format(new Date()));

            firebaseLocationAdapter.saveLocationInfo(userLocation);

        } catch (Exception e) {
            itemsAddedToFB = false;
            Log.i("Info", "     >>SaveCurrLocation Error:" + e.toString());
        }
    }

    /* Marker's location id is saved to firebase database(user_locations) **********************/
    private void saveLocationIDToUserLocations() {

        if (!itemsAddedToFB)
            return;

        try {
            firebaseLocationAdapter.saveUserLocationInfo(getFbUserID());

        } catch (Exception e) {
            itemsAddedToFB = false;
            Log.i("Info", "     >>saveUserLocation Error:" + e.toString());
        }
    }

    private void saveRegionBasedLocation(){

        if (!itemsAddedToFB)
            return;

        try {
            regionBasedLocation.setLocationId(firebaseLocationAdapter.getLocationId());
            firebaseLocationAdapter.saveRegionBasedLocation(getFbUserID(), regionBasedLocation);

        } catch (Exception e) {
            itemsAddedToFB = false;
            Log.i("Info", "     >>saveUserLocation Error:" + e.toString());
        }
    }

    private void savePinItems() {

        if (!itemsAddedToFB)
            return;

        try {
            userLocation.setLocationId(firebaseLocationAdapter.getLocationId());
            firebasePinItemsAdapter.savePinItems(userLocation, pinData);

        } catch (Exception e) {
            itemsAddedToFB = false;
            Log.i("Info", "     >>saveUserLocation Error:" + e.toString());
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        try {
            if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                long curTime = System.currentTimeMillis();

                if ((curTime - mLastShakeTime) > MIN_TIME_BETWEEN_SHAKES_MILLISECS) {
                    float x = event.values[0];
                    float y = event.values[1];
                    float z = event.values[2];

                    double acceleration = Math.sqrt(Math.pow(x, 2) +
                            Math.pow(y, 2) +
                            Math.pow(z, 2)) - SensorManager.GRAVITY_EARTH;

                    //Log.i("Info", "___________________________");
                    //Log.i("Info", "acceleration:" + acceleration);

                    if (acceleration > SHAKE_THRESHOLD) {
                        mLastShakeTime = curTime;

                        Log.i("Info", "Shake, Rattle, and Roll");

                        if (!demoThirdPageShown) {
                            showDemoThirdPage();
                            demoThirdPageShown = true;
                        } else
                            addMarkerToMap();
                    }
                }
            }
        } catch (Exception e) {
            Log.i("Info", "onSensorChanged Error:" + e.toString());
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onAnimationUpdate(ValueAnimator animation) {

    }
}
