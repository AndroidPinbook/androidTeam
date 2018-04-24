package uur.com.pinbook.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
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
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.arsy.maps_library.MapRipple;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

import uur.com.pinbook.Activities.PlayVideoActivity;
import uur.com.pinbook.Activities.ProfilePageActivity;
import uur.com.pinbook.Activities.SpecialSelectActivity;
import uur.com.pinbook.Activities.TabActivity;
import uur.com.pinbook.Controller.BitmapConversion;
import uur.com.pinbook.Adapters.CustomDialogAdapter;
import uur.com.pinbook.FirebaseGetData.FirebaseGetAccountHolder;
import uur.com.pinbook.FirebaseGetData.FirebaseGetGroups;
import uur.com.pinbook.Interfaces.IOnBackPressed;
import uur.com.pinbook.DefaultModels.SelectedFriendList;
import uur.com.pinbook.DefaultModels.SelectedGroupList;
import uur.com.pinbook.FirebaseAdapters.FirebaseLocationAdapter;
import uur.com.pinbook.FirebaseAdapters.FirebasePinItemsAdapter;
import uur.com.pinbook.FirebaseAdapters.FirebasePinModelAdapter;
import uur.com.pinbook.Adapters.LocationTrackerAdapter;
import uur.com.pinbook.Adapters.UriAdapter;
import uur.com.pinbook.JavaFiles.Friend;
import uur.com.pinbook.JavaFiles.PinData;
import uur.com.pinbook.JavaFiles.PinModels;
import uur.com.pinbook.JavaFiles.RegionBasedLocation;
import uur.com.pinbook.JavaFiles.UserLocation;
import uur.com.pinbook.R;
import butterknife.ButterKnife;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.LOCATION_SERVICE;
import static android.content.Context.SENSOR_SERVICE;
import static com.facebook.FacebookSdk.getApplicationContext;

import static uur.com.pinbook.ConstantsModel.FirebaseConstant.*;
import static uur.com.pinbook.ConstantsModel.NumericConstant.*;
import static uur.com.pinbook.ConstantsModel.StringConstant.*;

public class AddPinFragment extends BaseFragment implements
        OnMapReadyCallback,
        GoogleMap.OnMarkerClickListener,
        GoogleMap.OnCameraChangeListener,
        GoogleMap.OnMapClickListener,
        View.OnClickListener,
        LocationListener,
        SensorEventListener,
        IOnBackPressed{

    private static Animation rightSlideAnim;
    private static Animation leftSlideAnim;
    private static Animation upSlideAnim;

    private static Animation rightSlideDownAnim;
    private static Animation leftSlideDownAnim;
    private static Animation upSlideDownAnim;

    private static SelectedGroupList selectedGroupListInstance;
    private static SelectedFriendList selectedFriendListInstance;

    private ProgressDialog progressDialog;

    public GoogleMap mMap;

    LocationManager locationManager;

    private UriAdapter uriAdapter;

    private SensorManager mSensorMgr;
    GeoFire geoFire;
    GeoFire geoFireF;

    // Test iconlari
    private int[] tabIcons = {android.R.drawable.ic_menu_camera,
            android.R.drawable.ic_menu_agenda};

    public static LinearLayout profilePageMainLinearLayout;

    public static View defaultSpecialChoosenPage;

    //Firebase adapters
    private static FirebaseLocationAdapter firebaseLocationAdapter;
    private static FirebasePinItemsAdapter firebasePinItemsAdapter;
    private static FirebasePinModelAdapter firebasePinModelAdapter;

    private static UserLocation userLocation;
    private static RegionBasedLocation regionBasedLocation;
    private static PinModels pinModels;

    private static ImageView pinApproveImgv;
    private static ImageView pinDeleteImgv;
    private static ImageView noteTextApproveImgv;
    private static ImageView noteTextDeleteImgv;
    private static ImageView pinPictureApproveImgv;
    private static ImageView pinPictureDeleteImgv;
    private static ImageView pinThrowImgv;

    private static ImageView pinFriendsImgv;
    private static ImageView pinOnlymeImgv;
    private static ImageView pinSpecialImgv;
    private boolean isSpecialSelected = false;

    private MapView mapView;
    private View mView;

    private long mLastShakeTime;

    private GeoLocation geoLocation;

    private boolean itemsAddedToFB;
    private boolean isCancelPinCheck = false;
    private boolean isFriendsNotifCheck = false;
    private boolean isSpecialNotifCheck = false;
    private boolean pinThrowImageClicked = false;

    //pin notify values for properties
    private boolean pinFriendsNotifyValue = false;
    private boolean pinSpecialsNotifyValue = false;
    private boolean pinGroupsNotifyValue = false;

    private RelativeLayout mapRelativeLayout;
    private View popupMainView = null;

    private Boolean imageChangedInd = false;

    private int mWidth;
    private int mHeight;
    private String videoRealPath;
    private String imageRealPath;

    private View noteTextLayout;
    private View imageViewLayout;

    private static View markerInfoLayout;

    private ImageView videoImageView;
    private ImageView noteTextImageView;
    private ImageView pictureImageView;

    private static EditText noteTextEditText;
    private static Bitmap editTextBitmap = null;

    private static ImageView pinPhotoImageView;


    private int specialSelectedPage = 0;
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
    DatabaseReference ref;

    LatLng latLng;
    //private CircleOptions circleOptions;

    private LocationListener locationListener;
    private Runnable runnable = null;

    private boolean mLocationPermissionGranted = false;

    private PopupWindow popupWindow = null;

    private MapRipple mapRipple;

    private static Uri videoUri = null;
    private static Uri imageUri = null;

    private static Location markerLocation = null;
    private static LatLng markerLatlng = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i("Info", "AddPinFragment onCreate");
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

        mView = inflater.inflate(R.layout.fragment_add_pin, container, false);
        ButterKnife.bind(this, mView);

        progressDialog = new ProgressDialog(getActivity());

        ((ProfilePageActivity)getActivity()).updateToolbarTitle(PinThrowTitle);

        profilePageMainLinearLayout = (LinearLayout) ((ProfilePageActivity)getActivity()).findViewById(R.id.profilePageMainLayout);

        return mView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        try {
            //SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
            //        .findFragmentById(R.id.map);

            mapView = (MapView) mView.findViewById(R.id.map);

            if(mapView != null){
                mapView.onCreate(null);
                mapView.onResume();
                mapView.getMapAsync(this);
            }

            Log.i("Info", "onCreate============");

            context = getActivity();
            imageUri = null;

            firebaseLocationAdapter = new FirebaseLocationAdapter();
            firebasePinItemsAdapter = new FirebasePinItemsAdapter();
            firebasePinModelAdapter = new FirebasePinModelAdapter();

            userLocation = new UserLocation();
            regionBasedLocation = new RegionBasedLocation();
            uriAdapter = new UriAdapter();

            locationTrackObj = new LocationTrackerAdapter((ProfilePageActivity)getActivity());

            if (!locationTrackObj.canGetLocation()) {
                locationTrackObj.showSettingsAlert();
            } else {
                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    checkLocationPermission();
                }
            }

            mSensorMgr = (SensorManager) context.getSystemService(SENSOR_SERVICE);
            Sensor accelerometer = mSensorMgr.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

            if (accelerometer != null)
                mSensorMgr.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);

            mapRelativeLayout = (RelativeLayout) mView.findViewById(R.id.mapRelativeLayout);
            mapRelativeLayout.setOnClickListener(this);

            pinThrowImgv = (ImageView) mView.findViewById(R.id.pinThrowImgv);
            pinFriendsImgv = (ImageView) mView.findViewById(R.id.pinFriendsImgv);
            pinOnlymeImgv = (ImageView) mView.findViewById(R.id.pinOnlymeImgv);
            pinSpecialImgv = (ImageView) mView.findViewById(R.id.pinSpecialImgv);

            defineAnimations();

            pinThrowImgv.setOnClickListener(this);

            pinFriendsImgv.setOnClickListener(this);
            pinOnlymeImgv.setOnClickListener(this);
            pinSpecialImgv.setOnClickListener(this);

            ref = FirebaseDatabase.getInstance().getReference("GeoFireModel");
            geoFire = new GeoFire(ref);

        } catch (Exception e) {
            Log.i("Info", "     >>onCreate try error:" + e.toString());
        }

    }

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

        if (mMap != null) {
            mMap.clear();
            //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 18));

            mapRipple = new MapRipple(mMap, latLng, context);

            float zoom = mMap.getCameraPosition().zoom;

            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new
                    LatLng(currentLocation.getLatitude(),
                    currentLocation.getLongitude()), zoom), new GoogleMap.CancelableCallback() {
                @Override
                public void onFinish() {
                    checkMarkerAndCreatePopup();
                }

                @Override
                public void onCancel() {

                }
            });
        }
    }

    private void defineAnimations() {

        rightSlideAnim = AnimationUtils.loadAnimation(context, R.anim.slide_right_up_anim);
        leftSlideAnim = AnimationUtils.loadAnimation(context, R.anim.slide_left_up_anim);
        upSlideAnim = AnimationUtils.loadAnimation(context, R.anim.slide_up_anim);

        rightSlideDownAnim = AnimationUtils.loadAnimation(context, R.anim.slide_right_down_anim);
        leftSlideDownAnim = AnimationUtils.loadAnimation(context, R.anim.slide_left_down_anim);
        upSlideDownAnim = AnimationUtils.loadAnimation(context, R.anim.slide_down_anim);

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

            mMap.setOnMarkerClickListener(this);
            mMap.setOnMapClickListener(this);
            mMap.setOnCameraChangeListener(this);

            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (ActivityCompat.checkSelfPermission(context,
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

    /*========================================================================================*/
    private void initializeMap(GoogleMap mMap) {
        if (mMap != null) {
            mMap.getUiSettings().setScrollGesturesEnabled(true);
            mMap.getUiSettings().setAllGesturesEnabled(true);

            if (ContextCompat.checkSelfPermission(context,
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
        if (ActivityCompat.checkSelfPermission(context,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    android.Manifest.permission.ACCESS_FINE_LOCATION)) {

                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
            } else {
                ActivityCompat.requestPermissions(getActivity(),
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
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
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
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
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
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
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
    public void addMarkerToMap() {

        pinData = new PinData();

        isPinThrowClicked = true;
        centerMapOnLocation();
    }

    public void checkMarkerAndCreatePopup(){

        markerLocation = getLastKnownLocation();
        markerLatlng = new LatLng(markerLocation.getLatitude(), markerLocation.getLongitude());

        checkCurrLocRadius();

        MarkerOptions markerOptions = new MarkerOptions()
                .position(markerLatlng)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));

        marker = mMap.addMarker(markerOptions);

        createPopupWindow();
    }

    private void checkCurrLocRadius() {

        CircleOptions circleOptions = new CircleOptions()
                .center(markerLatlng)
                .radius(8);


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
    public boolean onMarkerClick(Marker marker) {

        Log.i("Info", "onMarkerClick starts");

        isMarkerClicked = true;
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
    public void onStop() {
        //handler.removeCallbacks(runnable);

        try {
            Log.i("Info", "onStop============");
            checkPopupShown();

            if(defaultSpecialChoosenPage != null)
                profilePageMainLinearLayout.removeView(defaultSpecialChoosenPage);

            super.onStop();

        } catch (Exception e) {
            Log.i("Info", "     >>onStop Error:" + e.toString());
        }
    }

    /*========================================================================================*/
    @Override
    public void onStart() {

        Log.i("Info", "AddPinFragment onStart");
        checkComeFromSpecialSelectActivity();
        super.onStart();
    }

    private void checkComeFromSpecialSelectActivity() {

        if(SpecialSelectActivity.specialSelectedInd) {

            selectedFriendListInstance = SelectedFriendList.getInstance();
            selectedGroupListInstance = SelectedGroupList.getInstance();

            if (selectedFriendListInstance.getSelectedFriendList().size() > 0) {
                pinModels.setProperty(propPersons);
                pinModels.setFriendList(selectedFriendListInstance.getSelectedFriendList());
            }

            if (selectedGroupListInstance.getGroupList().size() > 0) {
                pinModels.setProperty(propGroups);
                pinModels.setGroupList(selectedGroupListInstance.getGroupList());
            }

            mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
                @Override
                public void onMapLoaded() {
                    addMarkerToMap();
                }
            });

            SpecialSelectActivity.specialSelectedInd = false;
        }
    }

    /*========================================================================================*/
    @Override
    public void onClick(View v) {
        int i = v.getId();

        switch (i) {
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
                break;

            case R.id.mapRelativeLayout:  //Map framelayout layout
                checkPopupShown();
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
                if(checkExistingMarker()) {
                    pinFriendsImgv.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.img_anim));
                    handlePinThrowForFriends();
                }
                break;

            case R.id.pinOnlymeImgv:
                if (checkExistingMarker()) {
                    pinOnlymeImgv.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.img_anim));
                    handlePinThrowForOnlyMe();
                }
                break;
            case R.id.pinSpecialImgv:
                if (checkExistingMarker()) {
                    pinSpecialImgv.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.img_anim));
                    handlePinThrowForSpecial();
                }
                break;

            default:
                break;
        }
    }

    public boolean checkExistingMarker(){

        if (marker != null) {
            CustomDialogAdapter.showDialogWarning(context,
                    "Yeni PIN birakmak icin ekrandaki pini onaylayiniz veya siliniz");
            return false;
        }else
            return true;
    }

    public void handlePinThrowForFriends(){

        pinModels = new PinModels();
        initializeValues();
        isFriendsNotifCheck = true;
        pinModels.setProperty(propFriends);
        pinModels.setOwner(getFbUserID());
        pinModels.setToWhom(toWhomAll);
        pinModels.setGroupList(null);
        pinModels.setFriendList(null);
        showYesNoDialog(null, "Pininiz bildirimli birakilsin mi?");
        addMarkerToMap();
    }

    private void handlePinThrowForOnlyMe() {

        pinModels = new PinModels();
        initializeValues();
        pinModels.setProperty(propOnlyMe);
        pinModels.setOwner(getFbUserID());
        pinModels.setToWhom(getFbUserID());
        pinModels.setGroupList(null);
        pinModels.setFriendList(null);
        pinModels.setNotifiedFlag(notifyNo);
        addMarkerToMap();
    }

    private void handlePinThrowForSpecial() {

        pinModels = new PinModels();
        initializeValues();
        isSpecialNotifCheck = true;
        pinModels.setOwner(getFbUserID());
        pinModels.setToWhom(toWhomSpecial);
        pinModels.setNotifiedFlag(notifyYes);
        //openSpecialChoosenPage();
        openSpecialChoosenPage2();
    }

    public void openSpecialChoosenPage2(){
        selectedFriendListInstance = null;
        selectedGroupListInstance = null;

        startActivity(new Intent(getActivity(), SpecialSelectActivity.class));
        //startActivity(new Intent(getActivity(), TabActivity.class));
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

        imageViewLayout = getLayoutInflater().inflate(R.layout.default_image_window, mapRelativeLayout, false);

        pinPictureApproveImgv = (ImageView) imageViewLayout.findViewById(R.id.pinPictureApproveImgv);
        pinPictureDeleteImgv = (ImageView) imageViewLayout.findViewById(R.id.pinPictureDeleteImgv);

        LinearLayout imageMainLayout = (LinearLayout) imageViewLayout.findViewById(R.id.imageMainLayout);

        pinPhotoImageView = (ImageView) imageViewLayout.findViewById(R.id.pinPhotoImageView);

        InputStream profileImageStream = null;
        try {
            profileImageStream = context.getContentResolver().openInputStream(pinData.getPinImageUri());
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

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1);
        adapter.add("  Open Camera");
        adapter.add("  Open Galery");

        AlertDialog.Builder builder = new AlertDialog.Builder(context, android.R.style.Theme_Material_Dialog_Alert);
        builder.setTitle("How to upload your Picture?");

        builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {

                Log.i("Info", "  >>Item selected:" + item);

                if (item == IMAGE_CAMERA_SELECTED) {

                    choseImageFromCamera();

                } else if (item == IMAGE_GALLERY_SELECTED) {

                    startGalleryProcess("image");

                } else {
                    Toast.makeText(context, "Item Selected Error!!", Toast.LENGTH_SHORT).show();
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

        noteTextLayout = getLayoutInflater().inflate(R.layout.default_notetext_window, mapRelativeLayout, false);
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

            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
            String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
            return Uri.parse(path);
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
                progressDialog.setMessage("Yukleniyor...");
                progressDialog.show();
                pinApproveImgv.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.img_anim));
                pinApproved();
                progressDialog.dismiss();
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

        itemsAddedToFB = true;
        addItemsToFirebaseDatabase();

        if (!itemsAddedToFB)
            return;

        checkPopupShown();
        CustomDialogAdapter.showDialogSuccess(context, "Pin Birakildi");
        marker = null;
        imageUri = null;
        mMap.clear();
    }

    public void showPopupWindow() {

        if (popupWindow == null)
            return;

        if (popupWindow.isShowing())
            return;

        Log.i("Info", "marker.getPosition():" + marker.getPosition());

        if (!mMap.getProjection().getVisibleRegion().latLngBounds.contains(marker.getPosition())) {
            float zoom = mMap.getCameraPosition().zoom;
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(markerLatlng, 18));
        }

        Point p = mMap.getProjection().toScreenLocation(marker.getPosition());

        Point size = new Point();
        popupMainView.measure(size.x, size.y);

        mWidth = popupMainView.getMeasuredWidth();
        mHeight = popupMainView.getMeasuredHeight();

        showPinPopupWindow(mView.findViewById(R.id.map), p);
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

                popupWindow.update(p.x - mWidth / 2, p.y - mHeight + multiPinThrowVal, -1, -1);

            } else { // marker outside screen
                popupWindow.dismiss();
            }
        }
    }

    public void showPinPopupWindow(View view, Point p){

        popupWindow.showAtLocation(view, Gravity.NO_GRAVITY, p.x - mWidth / 2,
                p.y - mHeight + multiPinThrowVal);
    }

    @Override
    public void onDestroy() {
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
        marker = null;
        pinThrowImgv.setEnabled(true);

        pinData = new PinData();

        if (noteTextEditText != null)
            noteTextEditText.setText(null);

    }

    private void initializeValues() {

        isCancelPinCheck = false;
        isFriendsNotifCheck = false;
        pinFriendsNotifyValue = false;
        isSpecialNotifCheck = false;
    }

    public void showYesNoDialog(String title, String message) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context, android.R.style.Theme_Material_Dialog_Alert);

        builder.setIcon(R.drawable.warning_icon40);
        builder.setMessage(message);

        if (title != null)
            builder.setTitle(title);

        builder.setPositiveButton("EVET", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if (isCancelPinCheck)
                    deletePin();
                else if(isFriendsNotifCheck)
                    pinModels.setNotifiedFlag(notifyYes);
            }
        });

        builder.setNegativeButton("HAYIR", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if(isFriendsNotifCheck)
                    pinModels.setNotifiedFlag(notifyNo);

                dialog.dismiss();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public boolean onBackPressed() {

        if(defaultSpecialChoosenPage != null)
            profilePageMainLinearLayout.removeView(defaultSpecialChoosenPage);

        return false;
    }

    public boolean hideKeyBoard() {

        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);

        if (getActivity().getCurrentFocus() != null) {
            inputMethodManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
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

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1);
        adapter.add("  Open Camera");
        adapter.add("  Open Galery");

        if (pinData.getPinVideoUri() != null) //Pin e video eklenmisse itemlara eklenir
        {
            adapter.add("  Play Video");
            adapter.add("  Delete Video");
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(context, android.R.style.Theme_Material_Dialog_Alert);
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
                    Toast.makeText(context, "Item Selected Error!!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    public void playPinVideo() {

        Log.i("Info", "    -->pinData.getPinVideoUri     :" + pinData.getPinVideoUri());
        Log.i("Info", "    -->pinData.getPinVideoRealPath:" + pinData.getVideoRealPath());

        Intent intent = new Intent(context, PlayVideoActivity.class);
        intent.putExtra("videoUri", pinData.getPinVideoUri().toString());
        startActivity(intent);
    }

    public void choseVideoFromCamera() {

        Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        takeVideoIntent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, videoDuration);

        if (takeVideoIntent.resolveActivity(context.getPackageManager()) != null) {

            startActivityForResult(takeVideoIntent, MY_PERMISSION_GET_VIDEO_CAMERA);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {

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
        }

        showPopupWindow();

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
            pinImageStream = context.getContentResolver().openInputStream(imageUri);
            Bitmap photo = BitmapFactory.decodeStream(pinImageStream);
            photo = BitmapConversion.getRoundedShape(photo, 600, 600, imageRealPath);
            pictureImageView.setImageBitmap(photo);

            if (imageChangedInd) {
                mapRelativeLayout.removeView(imageViewLayout);
                imageChangedInd = false;
            }

            showPopupWindow();

            Log.i("Info", "    -->pindata.getImageUri     :" + pinData.getPinImageUri());
            Log.i("Info", "    -->pindata.getImageRealPath:" + pinData.getImageRealPath());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public String getRealPathFromCameraURI(Uri contentUri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = getActivity().managedQuery(contentUri, proj, null, null, null);
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

            showPopupWindow();

            Log.i("Info", "    -->pinData.getPinVideoUri     :" + pinData.getPinVideoUri());
            Log.i("Info", "    -->pinData.getPinVideoRealPath:" + pinData.getVideoRealPath());

        } catch (Exception e) {
            e.printStackTrace();
            Log.i("Info", "  >>manageVideoFromGallery error:" + e.toString());
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
                    CustomDialogAdapter.showDialogWarning(context,
                            "Video suresi 20 saniyeden buyuk olamaz!");
                    videoUri = null;
                    videoRealPath = null;
                    videoImageView.setImageResource(R.drawable.video_icon80);
                    showPopupWindow();
                    return false;
                }
            }

            pinData.setPinVideoImageUri(getImageUri(getApplicationContext(), bitmap));
            bitmap = BitmapConversion.getRoundedShape(bitmap, 600, 600, null);
            videoImageView.setImageBitmap(bitmap);
            showPopupWindow();
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
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)) {
            return true;
        } else {
            return false;
        }
    }

    private void addItemsToFirebaseDatabase() {

        saveCurrLocation();

        regionBasedLocation.setLocationId(firebaseLocationAdapter.getLocationId());
        pinModels.setLocationID(firebaseLocationAdapter.getLocationId());
        userLocation.setLocationId(firebaseLocationAdapter.getLocationId());

        saveLocationIDToUserLocations();
        saveRegionBasedLocation();
        savePinItems();
        savePinModel();
        saveGeoFireModel();
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
            CustomDialogAdapter.showErrorDialog(context, e.toString());
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
            CustomDialogAdapter.showErrorDialog(context, e.toString());
        }
    }

    private void saveRegionBasedLocation(){

        if (!itemsAddedToFB)
            return;

        try {

            firebaseLocationAdapter.saveRegionBasedLocation(getFbUserID(), regionBasedLocation);

        } catch (Exception e) {
            itemsAddedToFB = false;
            Log.i("Info", "     >>saveUserLocation Error:" + e.toString());
            CustomDialogAdapter.showErrorDialog(context, e.toString());
        }
    }

    private void savePinItems() {

        if (!itemsAddedToFB)
            return;

        try {

            firebasePinItemsAdapter.savePinItems(userLocation, pinData);

        } catch (Exception e) {
            itemsAddedToFB = false;
            Log.i("Info", "     >>saveUserLocation Error:" + e.toString());
            CustomDialogAdapter.showErrorDialog(context, e.toString());
        }
    }

    private void savePinModel() {

        if (!itemsAddedToFB)
            return;

        try {
            firebasePinModelAdapter.setPinModels(pinModels);
            firebasePinModelAdapter.savePinModel();

        } catch (Exception e) {
            itemsAddedToFB = false;
            Log.i("Info", "     >>savePinModel Error:" + e.toString());
            CustomDialogAdapter.showErrorDialog(context, e.toString());
        }
    }

    public String getFbUserID() {
        return FirebaseGetAccountHolder.getUserID();
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

                    if (acceleration > SHAKE_THRESHOLD) {
                        mLastShakeTime = curTime;

                        Log.i("Info", "Shake, Rattle, and Roll");

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

    private void saveGeoFireModel() {

        /*geoFire.setLocation(FirebaseGetAccountHolder.getUserID(), new GeoLocation(markerLatlng.latitude, markerLatlng.longitude),
                new GeoFire.CompletionListener() {
                    @Override
                    public void onComplete(String key, DatabaseError error) {

                    }
                });*/


        for(Friend friend: pinModels.getFriendList())
        {
            ref = FirebaseDatabase.getInstance().getReference("GeoFireModel").child(friend.getUserID());

            geoFire = new GeoFire(ref);

            geoFire.setLocation(firebaseLocationAdapter.getLocationId(), new GeoLocation(markerLatlng.latitude, markerLatlng.longitude),
                    new GeoFire.CompletionListener() {
                        @Override
                        public void onComplete(String key, DatabaseError error) {

                        }
                    });

            Map<String, Object> values = new HashMap<>();
            values.put("notifSend", "N");
            values.put("pinOwner", FirebaseGetAccountHolder.getUserID());
            ref.child(firebaseLocationAdapter.getLocationId()).updateChildren(values);



        }

        //geoFireF = new GeoFire(ref.child(FirebaseGetAccountHolder.getUserID()));

        //0.5f = 0.5 km = 500 m
        /*GeoQuery geoQuery = geoFire
                .queryAtLocation(new GeoLocation(markerLatlng.latitude, markerLatlng.longitude), 0.5f);
        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                Log.i("key", "key:" + key);
                sendNotification("ugur", String.format("%s entered the dangerous area", key));
            }

            @Override
            public void onKeyExited(String key) {
                Log.i("key", "key:" + key);
                sendNotification("ugur", String.format("%s is no longer in the dangerous area", key));
            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {
                Log.i("key", "key:" + key);
                Log.i("MOVE", String.format("%s moved within the dangerous area[%f / %f ]", key, location.latitude, location.longitude));
            }

            @Override
            public void onGeoQueryReady() {

            }

            @Override
            public void onGeoQueryError(DatabaseError error) {
                Log.i("Error", " " + error.toString());
            }
        });*/

    }

    private void sendNotification(String title, String content) {

        Notification.Builder builder = new Notification.Builder(context)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setContentTitle(title)
                .setContentText(content);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Intent intent = new Intent(context, ProfilePageActivity.class);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE);
        builder.setContentIntent(pendingIntent);
        Notification notification = builder.build();
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        notification.defaults |= Notification.DEFAULT_SOUND;

        notificationManager.notify(new Random().nextInt(), notification);
    }


}
