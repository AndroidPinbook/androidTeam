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

import uur.com.pinbook.FirebaseGetData.FirebaseGetAccountHolder;
import uur.com.pinbook.JavaFiles.Feed;
import uur.com.pinbook.JavaFiles.LocationDb;
import uur.com.pinbook.R;
import uur.com.pinbook.RecyclerView.Adapter.FeedAllItemAdapter;
import uur.com.pinbook.RecyclerView.HelperClasses.NetworkCheckingClass;
import uur.com.pinbook.RecyclerView.Model.FeedAllItem;
import uur.com.pinbook.RecyclerView.Model.FeedPinItem;


import static com.facebook.FacebookSdk.getApplicationContext;
import static uur.com.pinbook.ConstantsModel.FirebaseConstant.Feeds;
import static uur.com.pinbook.ConstantsModel.FirebaseConstant.Locations;
import static uur.com.pinbook.ConstantsModel.FirebaseConstant.owner;
import static uur.com.pinbook.ConstantsModel.FirebaseConstant.picture;
import static uur.com.pinbook.ConstantsModel.FirebaseConstant.pictureURL;
import static uur.com.pinbook.ConstantsModel.FirebaseConstant.text;
import static uur.com.pinbook.ConstantsModel.FirebaseConstant.textURL;
import static uur.com.pinbook.ConstantsModel.FirebaseConstant.video;
import static uur.com.pinbook.ConstantsModel.FirebaseConstant.videoImageURL;
import static uur.com.pinbook.ConstantsModel.FirebaseConstant.videoURL;

public class ProfileFragment extends BaseFragment {

    int fragCount;
    View view;

    RecyclerView recyclerViewVertical;

    ProgressBar progressBar;
    RelativeLayout relativeLayout;

    DatabaseReference mDbref;
    DatabaseReference tempRef;

    Map<String, String> mapFeed;
    private ValueEventListener mDbRefListener;
    private ValueEventListener tempRefListener;
    LocationDb locationDb;

    int counter = 0;
    Uri uriPicture, uriVideo, uriText ;
    final static int round = 10;

    String locationId;
    Map<String, String> tempMap;

    Feed feed;
    FeedAllItem feedAllItem;
    FeedPinItem feedPinItem;
    List<FeedAllItem> feedAllItemList = new ArrayList<FeedAllItem>();

    FeedAllItemAdapter feedAllItemAdapter;

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

        if (NetworkCheckingClass.isNetworkAvailable(getActivity())) {

            init();
            progressBar.setVisibility(View.VISIBLE);
            Log.i("----> userId ", getUserID());

            setLocations();
        } else {
            progressBar.setVisibility(View.GONE);
            Toast.makeText(getActivity(), "No internet Connection", Toast.LENGTH_LONG).show();
        }

        return view;
    }

    public String getUserID(){
        return FirebaseGetAccountHolder.getUserID();
    }

    private void init() {

        relativeLayout = (RelativeLayout) view.findViewById(R.id.activity_main);
        recyclerViewVertical = (RecyclerView) view.findViewById(R.id.vertical_recycler_view);
        recyclerViewVertical.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);

        mDbref = FirebaseDatabase.getInstance().getReference(Feeds).child(getUserID());
        mapFeed = new HashMap<String, String>();
        tempMap = new HashMap<String, String>();

    }

    private void setLocations() {

        Log.i("", "=======================================>> xx start ");

        mDbRefListener = mDbref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot locationSnapshot : dataSnapshot.getChildren()) {

                    Log.i("====== locationSnapshot", locationSnapshot.toString());

                    mapFeed.clear();
                    mapFeed = ((Map) locationSnapshot.getValue());
                    locationId = locationSnapshot.getKey();

                    Log.i("info", "========================================");

                    feedAllItem = setFeedAllItem();
                    insertIntoRecyclerView(feedAllItem);

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        Log.i("", "=======================================>> xx finish ");

    }

    private FeedAllItem setFeedAllItem() {

        FeedAllItem tempFeedAllItem = new FeedAllItem();
        FeedPinItem tempFeedPinItem = new FeedPinItem();
        ArrayList<FeedPinItem> tempFeedPinItemList = new ArrayList<FeedPinItem>();

        tempFeedAllItem.setLocationId(locationId);

        Log.i("map :", mapFeed.toString());

        for (Map.Entry<String, String> entry : mapFeed.entrySet())
        {

            switch (entry.getKey()){

                case owner:
                    tempFeedAllItem.setOwnerId(entry.getValue());
                    break;

                case pictureURL:
                    tempFeedPinItem = setFeedPinItem(picture, entry.getValue());
                    tempFeedPinItemList.add(tempFeedPinItem);
                    break;

                case videoImageURL:
                    tempFeedPinItem = setFeedPinItem(video, entry.getValue());
                    tempFeedPinItemList.add(tempFeedPinItem);
                    break;

                case textURL:
                    tempFeedPinItem = setFeedPinItem(text, entry.getValue());
                    tempFeedPinItemList.add(tempFeedPinItem);
                    break;

                default:
                    break;
            }

        }

        tempFeedAllItem.setFeedPinItems(tempFeedPinItemList);
        tempFeedAllItem.setOwnerPictureUrl("https://firebasestorage.googleapis.com/v0/b/androidteam-f4c25.appspot.com" +
                "/o/Users%2FprofilePics%2F8tnZ6beMDdcY3zD0rWGzOhytacA3.jpg?alt=media&token" +
                "=1fbe7d20-82e5-42e1-af42-a15026f03120");
        tempFeedAllItem.setOwnerName("Ugur Göğebakan/" + locationId);
        tempFeedAllItem.setTime(2);

        return tempFeedAllItem;

    }

    private FeedPinItem setFeedPinItem(String name, String url) {

        FeedPinItem feedPinItem = new FeedPinItem();
        feedPinItem.setName(name);
        feedPinItem.setUrl(url);

        return feedPinItem;
    }


    private void insertIntoRecyclerView(FeedAllItem feedAllItem) {

        relativeLayout.setBackgroundColor(Color.parseColor("#ffffb3"));
        progressBar.setVisibility(View.GONE);


        Log.i("", "========================  "+locationId+" ======================");
        //Log.i("imgUrl   ", tempPinData.getPinImageUri().toString());
        //Log.i("videoUrl ", tempPinData.getPinVideoUri().toString());
        //Log.i("textUrl  ", tempPinData.getPinTextUri().toString());

        feedAllItemList.add(feedAllItem);

        recyclerViewVertical.setHasFixedSize(true);

        if (feedAllItemList.size() == 30) {
            feedAllItemAdapter = new FeedAllItemAdapter(getContext(), feedAllItemList);
            recyclerViewVertical.setAdapter(feedAllItemAdapter);
        }


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

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        return "city";

    }


}
