package uur.com.pinbook.Controller;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import uur.com.pinbook.Activities.ProfilePageActivity;
import uur.com.pinbook.DefaultModels.GetContactList;
import uur.com.pinbook.FirebaseGetData.FirebaseGetAccountHolder;
import uur.com.pinbook.FirebaseGetData.FirebaseGetFriends;
import uur.com.pinbook.JavaFiles.Friend;
import uur.com.pinbook.R;

import static uur.com.pinbook.ConstantsModel.NumericConstant.PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION;
import static uur.com.pinbook.ConstantsModel.NumericConstant.PERMISSION_REQUEST_READ_CONTACTS;

public class NotifyService extends Service implements
        LocationListener,
        GeoFire.CompletionListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    final static String ACTION = "NotifyServiceAction";
    final static int RQS_STOP_SERVICE = 1;

    NotifyServiceReceiver notifyServiceReceiver;

    private Context context;

    private Location mLastLocation;
    LocationManager locationManager;

    private LocationRequest mLocationRequest;
    private GoogleApiClient mGoogleApiClient;

    private static int UPDATE_INTERVAL = 5000;
    private static int FATEST_INTERVAL = 3000;
    private static int DISPLACEMENT = 10;

    DatabaseReference ref;
    GeoFire geoFire;

    @Override
    public void onCreate() {
        context = this;

        ref = FirebaseDatabase.getInstance().getReference("GeoFireModel").child(FirebaseGetAccountHolder.getUserID());
        geoFire = new GeoFire(ref);

        locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);

        setUpLocation();
        notifyServiceReceiver = new NotifyServiceReceiver();

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION);
        registerReceiver(notifyServiceReceiver, intentFilter);

        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION);
        registerReceiver(notifyServiceReceiver, intentFilter);

        mLastLocation = getLastKnownLocation();
        checkFriendLocations();

        //sendNotification("xxxxx", "hiyartolar sizi");

        return super.onStartCommand(intent, flags, startId);
    }

    private void sendNotification(String title, String content, String locId) {

        Notification.Builder builder = new Notification.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setContentTitle(title)
                .setContentText(content);

        NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        Intent intent = new Intent(this, ProfilePageActivity.class);
        //Intent intent = new Intent("do_something");
        //intent.putExtra("locationId", locId);


        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);
        builder.setContentIntent(pendingIntent);
        Notification notification = builder.build();
        notification.contentIntent = pendingIntent;
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        notification.defaults |= Notification.DEFAULT_SOUND;

        notificationManager.notify(new Random().nextInt(), notification);
    }

    @Override
    public void onDestroy() {
        this.unregisterReceiver(notifyServiceReceiver);
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;
        //displayLocation();
        checkFriendLocations();
        Log.i("info", "location Changed++++++++++++++++++++++");
        //Toast.makeText(this, "locationChanged", Toast.LENGTH_SHORT).show();
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

    @Override
    public void onComplete(String key, DatabaseError error) {

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        //displayLocation();
        startLocationUpdates();
    }

    private void startLocationUpdates() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }

        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    public class NotifyServiceReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context arg0, Intent arg1) {

            int rqs = arg1.getIntExtra("RQS", 0);
            if (rqs == RQS_STOP_SERVICE) {
                stopSelf();
            }
        }
    }

    private void displayLocation() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }

        mLastLocation = getLastKnownLocation();

        if (mLastLocation != null) {
            final double latitude = mLastLocation.getLatitude();
            final double longitude = mLastLocation.getLongitude();

            Toast.makeText(this, "", Toast.LENGTH_SHORT).show();

            Log.i("Info", String.format("Your location was changed: %f / %f", latitude, longitude));
        } else
            Log.i("Info", "Can not get your location!!!");
    }

    private void checkFriendLocations() {

        if (mLastLocation == null)
            return;

        //0.5f = 0.5 km = 500 m
        GeoQuery geoQuery = geoFire.queryAtLocation(new GeoLocation(mLastLocation.getLatitude(), mLastLocation.getLongitude()), 0.1f);
        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(final String key, GeoLocation location) {
                Log.i("key", "key:" + key);

                FirebaseDatabase.getInstance().getReference("GeoFireModel").child(FirebaseGetAccountHolder.getUserID())
                        .child(key).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {

                            if (dataSnapshot1.getValue() != null) {

                                if (dataSnapshot1.getKey().equals("notifSend") && dataSnapshot1.getValue().equals("N")) {

                                    Map<String, Object> map = (Map) dataSnapshot.getValue();

                                    //String notifSendParam = (String) map.get("notifSend");
                                    final String pinOwner = (String) map.get("pinOwner");


                                    for (Friend friend : FirebaseGetFriends.getInstance(FirebaseGetAccountHolder.getUserID()).getFriendList()) {
                                        if (pinOwner.equals(friend.getUserID())) {
                                            sendNotification("notif", String.format("%s burada pin birakmisti :)", friend.getNameSurname()), key);

                                            //NotificationUtils notificationUtils = new NotificationUtils();
                                            //notificationUtils.displayNotification(context);

                                            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("GeoFireModel").child(FirebaseGetAccountHolder.getUserID())
                                                    .child(key);
                                            Map<String, Object> values = new HashMap<>();
                                            values.put("notifSend", "Y");
                                            ref.updateChildren(values);

                                            break;
                                        }
                                    }


                                }

                            }
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


            }

            @Override
            public void onKeyExited(String key) {
                Log.i("key", "key:" + key);

                for (Friend friend : FirebaseGetFriends.getInstance(FirebaseGetAccountHolder.getUserID()).getFriendList()) {

                    if (key.equals(friend.getUserID())) {
                        // sendNotification("notif", String.format("%s is no longer in the dangerous area", friend.getNameSurname()));
                    }
                }

                //sendNotification("ugur", String.format("%s is no longer in the dangerous area", key));
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
        });
    }

    @SuppressLint("MissingPermission")
    private Location getLastKnownLocation() {

        Log.i("Info", "getLastKnownLocation starts");

        //locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
        List<String> providers = locationManager.getProviders(true);
        Location bestLocation = null;

        for (String provider : providers) {

            Location location = null;

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
            {
                /*Criteria criteria = new Criteria();
                criteria.setPowerRequirement(Criteria.POWER_LOW);
                criteria.setAccuracy(Criteria.ACCURACY_COARSE);
                String bestprovider = locationManager.getBestProvider(criteria,false);
                location = locationManager.getLastKnownLocation(bestprovider);*/

                location = locationManager.getLastKnownLocation(provider);

            }//else
            //ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);

            if (location == null) {
                continue;
            }
            if (bestLocation == null || location.getAccuracy() < bestLocation.getAccuracy()) {
                bestLocation = location;
            }
        }
        return bestLocation;
    }

    /*private boolean checkPlayServices() {

        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);

        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode))
                GooglePlayServicesUtil.getErrorDialog(resultCode, this, PLAY_SERVICES_RESOLUTION_REQUEST).show();
            else {
                Toast.makeText(this, "This device is not supported!", Toast.LENGTH_SHORT).show();
            }
            return false;
        }

        return true;
    }*/

    private void buildGoogleApiClient() {

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    @SuppressLint("RestrictedApi")
    private void createLocationRequest() {

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FATEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setSmallestDisplacement(DISPLACEMENT);
    }

    private void setUpLocation() {

        /*if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
            }, MY_PERMISSION_REQUEST_CODE);
        } else {
            if (checkPlayServices()) {
                buildGoogleApiClient();
                createLocationRequest();
                displayLocation();
            }
        }*/

        buildGoogleApiClient();
        createLocationRequest();
        //displayLocation();
    }

}