package uur.com.pinbook.fragments;

import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

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

import butterknife.ButterKnife;

import uur.com.pinbook.Activities.ProfilePageActivity;
import uur.com.pinbook.JavaFiles.LocationDb;
import uur.com.pinbook.JavaFiles.PinData;
import uur.com.pinbook.JavaFiles.UserLocation;
import uur.com.pinbook.R;
import uur.com.pinbook.RecyclerView.Adapter.FeedAdapter;
import uur.com.pinbook.RecyclerView.HelperClasses.NetworkCheckingClass;
import uur.com.pinbook.RecyclerView.Model.SingleFeed;
import uur.com.pinbook.RecyclerView.Model.FeedInnerItem;


import static com.facebook.FacebookSdk.getApplicationContext;
import static uur.com.pinbook.Activities.ProfilePageActivity.FBuserId;
import static uur.com.pinbook.JavaFiles.ConstValues.Locations;
import static uur.com.pinbook.JavaFiles.ConstValues.PinItems;
import static uur.com.pinbook.JavaFiles.ConstValues.UserLocations;
import static uur.com.pinbook.JavaFiles.ConstValues.Users;

public class ProfileFragment extends BaseFragment {

    int fragCount;
    View view;

    RecyclerView recyclerViewVertical;
    FeedAdapter feedAdapter;
    ProgressBar progressBar;
    RelativeLayout relativeLayout;

    DatabaseReference mDbref;
    DatabaseReference tempRef;
    List<UserLocation> locationList;
    List<LocationDb> locationDbsList;
    List<PinData> pinDataList;
    Map<String, String> mp;
    private ValueEventListener mDbRefListener;
    private ValueEventListener tempRefListener;
    LocationDb locationDb;
    PinData pinData;
    int counter = 0;
    Uri uriPicture;
    Uri uriVideo;
    Uri uriText;
    final static int round = 10;

    List<SingleFeed> singleFeedDataList = new ArrayList<SingleFeed>();
    String locationId;
    Map<String, String> tempMap;

    public static ProfileFragment newInstance(int instance) {
        Bundle args = new Bundle();
        args.putInt(ARGS_INSTANCE, instance);
        ProfileFragment fragment = new ProfileFragment();
        fragment.setArguments(args);
        return fragment;
    }


    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        view = inflater.inflate(R.layout.fragment_profile, container, false);

        ButterKnife.bind(this, view);

        Bundle args = getArguments();
        if (args != null) {
            fragCount = args.getInt(ARGS_INSTANCE);
        }

        relativeLayout = (RelativeLayout) view.findViewById(R.id.activity_main);
        recyclerViewVertical = (RecyclerView) view.findViewById(R.id.vertical_recycler_view);
        recyclerViewVertical.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));

        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);

        if (NetworkCheckingClass.isNetworkAvailable(getActivity())) {
            progressBar.setVisibility(View.VISIBLE);
            //setFeedData();
        } else {
            progressBar.setVisibility(View.GONE);
            Toast.makeText(getActivity(), "No internet Connection", Toast.LENGTH_LONG).show();
        }

        Log.i("----> userId ", FBuserId);

        setLocationList();
        return view;
    }

    private void setLocationList() {

        mDbref = FirebaseDatabase.getInstance().getReference(PinItems).child(FBuserId);
        locationList = new ArrayList<>();
        locationDbsList = new ArrayList<>();
        pinDataList = new ArrayList<>();
        mp = new HashMap<String, String>();
        tempMap = new HashMap<String, String>();

        getLocations();

    }

    private void getLocations() {

        Log.i("", "=======================================>> xx start ");

        mDbRefListener = mDbref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                locationList.clear();
                locationDbsList.clear();

                for (DataSnapshot locationSnapshot : dataSnapshot.getChildren()) {

                    Log.i("====== locationSnapshot", locationSnapshot.toString());

                    mp = ((Map) locationSnapshot.getValue());
                    locationId = locationSnapshot.getKey();

                    Log.i("info", "========================================");
                    pinData = new PinData();
                    if (mp.get("pictureURL") != null) {
                        //Log.i("pictureUrl ", mp.get("pictureURL"));
                        uriPicture = Uri.parse(mp.get("pictureURL"));
                        pinData.setPinImageUri(uriPicture);

                    }

                    if (mp.get("videoURL") != null) {
                        //.i("videoURL ", mp.get("videoURL"));
                        uriVideo = Uri.parse(mp.get("videoURL"));
                        pinData.setPinVideoUri(uriVideo);
                    }

                    if (mp.get("textURL") != null) {
                        //Log.i("textURL ", mp.get("textURL"));
                        uriText = Uri.parse(mp.get("textURL"));
                        pinData.setPinTextUri(uriText);
                    }

                    pinDataList.add(pinData);
                    setFeedData(pinData);





                    /*
                    UserLocation userLocation = new UserLocation();
                    userLocation.setUserId(FBuserId);
                    final String locationId = locationSnapshot.getKey();
                    userLocation.setLocationId(locationSnapshot.getValue(String.class));

                    locationList.add(userLocation);

                    tempRef = FirebaseDatabase.getInstance().getReference(Locations).child(locationId);

                    getLocationDetail(locationId);

                    */
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        Log.i("", "=======================================>> xx finish ");

    }

    private void addFeedList(LocationDb locationDb) {

        counter++;
        Log.i(" ========= location_" + counter, locationDb.getLocationId());

    }


    private void setFeedData(PinData pinData) {

        relativeLayout.setBackgroundColor(Color.parseColor("#ffffb3"));
        progressBar.setVisibility(View.GONE);


        Log.i("", "========================  "+locationId+" ======================");
        //Log.i("imgUrl   ", tempPinData.getPinImageUri().toString());
        //Log.i("videoUrl ", tempPinData.getPinVideoUri().toString());
        //Log.i("textUrl  ", tempPinData.getPinTextUri().toString());

        setMyLocations(pinData);

        recyclerViewVertical.setHasFixedSize(true);

        if (singleFeedDataList.size() == 30) {
            feedAdapter = new FeedAdapter(getContext(), singleFeedDataList);
            recyclerViewVertical.setAdapter(feedAdapter);
        }


/*
        if(dataList.size()/round >= 1){

            if(dataList.size()%round == 0){
                feedAdapter = new FeedAdapter(getContext(), dataList);
                recyclerViewVertical.setAdapter(feedAdapter);
            }
            else{
                //continue
            }

        }
        else{
            //continue
        }
*/

    }

    private void setMyLocations(PinData pinData) {

        getLocationInfo();

        SingleFeed singleFeed = new SingleFeed();

        singleFeed.setImage("http://lorempixel.com//400//200//");
        singleFeed.setName("Ugur Göğebakan/" + locationId);
        singleFeed.setNameImage("https://firebasestorage.googleapis.com/v0/b/androidteam-f4c25.appspot.com" +
                "/o/Users%2FprofilePics%2F8tnZ6beMDdcY3zD0rWGzOhytacA3.jpg?alt=media&token" +
                "=1fbe7d20-82e5-42e1-af42-a15026f03120");
        singleFeed.setCount(2);
        singleFeed.setType("audio");

        if (mp.get("text") != null) {
            singleFeed.setTitle(mp.get("text"));
        } else {
            singleFeed.setTitle(mp.get("Text boş.."));
        }


        ArrayList<FeedInnerItem> singleItem = new ArrayList<FeedInnerItem>();
        if (pinData.getPinImageUri() != null) {
            singleItem.add(new FeedInnerItem("Item image", pinData.getPinImageUri().toString()));
        }
        if (pinData.getPinVideoUri() != null) {
            //singleItem.add(new FeedInnerItem("Item video", tempPinData.getPinVideoUri().toString()));
        }
        if (pinData.getPinTextUri() != null) {
            singleItem.add(new FeedInnerItem("Item text", pinData.getPinTextUri().toString()));
        }

        singleFeed.setAllItemsInSingleFeed(singleItem);

        singleFeedDataList.add(singleFeed);

    }

    private String getLocationInfo() {

        DatabaseReference tempRef =  FirebaseDatabase.getInstance().getReference(Locations).child(locationId);

        tempRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.getValue() != null) {
                    locationDb = new LocationDb();

                    Log.i("dataSnapshot ", dataSnapshot.toString());
                    tempMap.clear();
                    tempMap = ((Map) dataSnapshot.getValue());

                    Log.i("--> ", mp.get("city"));
                    String city = mp.get("city");

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        return "city";

    }


}
