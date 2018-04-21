package uur.com.pinbook.fragments;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;


import com.baoyz.widget.PullRefreshLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import uur.com.pinbook.Activities.FeedDetailActivity;
import uur.com.pinbook.FirebaseGetData.FirebaseGetAccountHolder;
import uur.com.pinbook.JavaFiles.Feed;
import uur.com.pinbook.JavaFiles.LocationDb;
import uur.com.pinbook.JavaFiles.User;
import uur.com.pinbook.R;

import butterknife.ButterKnife;
import uur.com.pinbook.RecyclerView.Adapter.FeedAllItemAdapter;
import uur.com.pinbook.RecyclerView.HelperClasses.NetworkCheckingClass;
import uur.com.pinbook.RecyclerView.Model.FeedAllItem;
import uur.com.pinbook.RecyclerView.Model.FeedPinItem;

import static com.facebook.FacebookSdk.getApplicationContext;
import static uur.com.pinbook.ConstantsModel.FirebaseConstant.Feeds;
import static uur.com.pinbook.ConstantsModel.FirebaseConstant.Locations;
import static uur.com.pinbook.ConstantsModel.FirebaseConstant.Users;
import static uur.com.pinbook.ConstantsModel.FirebaseConstant.feedLimit;
import static uur.com.pinbook.ConstantsModel.FirebaseConstant.nextItemCount;
import static uur.com.pinbook.ConstantsModel.FirebaseConstant.picture;
import static uur.com.pinbook.ConstantsModel.FirebaseConstant.pictureURL;
import static uur.com.pinbook.ConstantsModel.FirebaseConstant.profilePictureUrl;
import static uur.com.pinbook.ConstantsModel.FirebaseConstant.surname;
import static uur.com.pinbook.ConstantsModel.FirebaseConstant.text;
import static uur.com.pinbook.ConstantsModel.FirebaseConstant.textURL;
import static uur.com.pinbook.ConstantsModel.FirebaseConstant.timestamp;
import static uur.com.pinbook.ConstantsModel.FirebaseConstant.userID;
import static uur.com.pinbook.ConstantsModel.FirebaseConstant.userName;
import static uur.com.pinbook.ConstantsModel.FirebaseConstant.video;
import static uur.com.pinbook.ConstantsModel.FirebaseConstant.videoImageURL;
import static uur.com.pinbook.ConstantsModel.FirebaseConstant.videoURL;

public class HomeFragment extends BaseFragment {

    int fragCount;
    View view;

    RecyclerView recyclerViewVertical;

    ProgressBar progressBar;
    RelativeLayout relativeLayout;

    DatabaseReference mDbref;
    DatabaseReference tempRef;
    DatabaseReference userRef;

    Map<String, String> mapFeed;
    Map<String, String> mapUser;
    private ValueEventListener mDbRefListener;
    private ValueEventListener tempRefListener;
    private ValueEventListener userRefListener;
    LocationDb locationDb;

    int counter = 0;
    Uri uriPicture, uriVideo, uriText;
    final static int round = 10;

    String locationId;

    Feed feed;
    FeedAllItem feedAllItem;
    FeedPinItem feedPinItem;
    List<FeedAllItem> feedAllItemList = new ArrayList<FeedAllItem>();
    User user;

    FeedAllItemAdapter feedAllItemAdapter;
    private String startKey;
    private int lastVisibleItem, totalItemCount;
    private LinearLayoutManager lm;

    private int mPostsPerPage = 15;
    Map<String, Long> mapTemp;
    Long ts;
    private boolean loading = true;
    List<String> locList = new ArrayList<String>();
    PullRefreshLayout layout;

    public static HomeFragment newInstance(int instance) {
        Bundle args = new Bundle();
        args.putInt(ARGS_INSTANCE, instance);
        HomeFragment fragment = new HomeFragment();
        fragment.setArguments(args);
        return fragment;
    }


    public HomeFragment() {
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
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_home, container, false);
            if (NetworkCheckingClass.isNetworkAvailable(getActivity())) {

                init();
                progressBar.setVisibility(View.VISIBLE);
                Log.i("----> userId ", getUserID());

                //setLocations();
                getUsers(null);
            } else {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(getActivity(), "No internet Connection", Toast.LENGTH_LONG).show();
            }
        }


        ButterKnife.bind(this, view);

        Bundle args = getArguments();
        if (args != null) {
            fragCount = args.getInt(ARGS_INSTANCE);
        }

        return view;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


    }


    public String getUserID() {
        return FirebaseGetAccountHolder.getUserID();
    }

    private void init() {


        layout = (PullRefreshLayout) view.findViewById(R.id.pull_to_refresh);
        relativeLayout = (RelativeLayout) view.findViewById(R.id.activity_main);
        recyclerViewVertical = (RecyclerView) view.findViewById(R.id.vertical_recycler_view);


        lm = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        recyclerViewVertical.setLayoutManager(lm);
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);


        mDbref = FirebaseDatabase.getInstance().getReference(Feeds).child(getUserID());
        mapFeed = new HashMap<String, String>();
        mapUser = new HashMap<String, String>();
        startKey = null;


        FeedAllItemAdapter.RecyclerViewClickListener listener = new FeedAllItemAdapter.RecyclerViewClickListener() {
            @Override
            public void onClick(View view, FeedAllItem feedAllItem) {
                Toast.makeText(getContext(), "PositionCard " + feedAllItem.getLocationId(), Toast.LENGTH_SHORT).show();
            }
        };


        FeedAllItemAdapter.InnerRecyclerViewClickListener innerRecyclerViewClickListener = new FeedAllItemAdapter.InnerRecyclerViewClickListener() {
            @Override
            public void onClick(View view, FeedPinItem singleItem, FeedAllItem feedAllItem) {
                //Toast.makeText(getContext(), singleItem.getName(), Toast.LENGTH_SHORT).show();
                Toast.makeText(getContext(), "Şunu tıkladın : " +feedAllItem.getLocationId(), Toast.LENGTH_SHORT).show();


                FeedPinItem fp;
                int clickedItem = -1;
                for(int k=0 ; k< feedAllItem.getFeedPinItems().size(); k++){
                    fp = feedAllItem.getFeedPinItems().get(k);
                    if(fp.getItemTag().equals(singleItem.getItemTag())){
                        clickedItem = k;
                    }
                }
                if(clickedItem != -1){
                    Intent intent = new Intent(getContext(), FeedDetailActivity.class);
                    intent.putExtra("feedAllItem", feedAllItem);
                    intent.putExtra("clickedItem", clickedItem);
                    startActivity(intent);
                }



            }
        };

        feedAllItemAdapter = new FeedAllItemAdapter(getContext(), listener, innerRecyclerViewClickListener);
        recyclerViewVertical.setAdapter(feedAllItemAdapter);
        DividerItemDecoration itemDecor = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        recyclerViewVertical.addItemDecoration(itemDecor);

        // listen refresh event
        layout.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // start refresh
                feedAllItemAdapter.clear();
                getUsers(null);
            }
        });

        layout.setRefreshing(false);


        recyclerViewVertical.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                int pastVisiblesItems, visibleItemCount, totalItemCount;

                if (dy > 0) //check for scroll down
                {
                    visibleItemCount = lm.getChildCount();
                    totalItemCount = lm.getItemCount();
                    pastVisiblesItems = lm.findFirstVisibleItemPosition();

                    if (loading) {

                        if ((visibleItemCount + pastVisiblesItems) >= totalItemCount) {
                            loading = false;
                            Log.v("...", "Last Item Wow !");
                            //Do pagination.. i.e. fetch new data
                            Long nodeId = ts;
                            getUsers(nodeId);

                        }

                    }


                }


            }
        });

    }

    private void getUsers(Long nodeId) {

        Query query;

        if (nodeId == null)
            query = FirebaseDatabase.getInstance().getReference(Feeds)
                    .child(getUserID())
                    .orderByChild(timestamp)
                    .limitToLast(mPostsPerPage);
        else
            query = FirebaseDatabase.getInstance().getReference(Feeds)
                    .child(getUserID())
                    .orderByChild(timestamp)
                    .endAt(nodeId)
                    .limitToLast(mPostsPerPage);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                long cnt = dataSnapshot.getChildrenCount();
                int i = 0;

                for (DataSnapshot locationSnapshot : dataSnapshot.getChildren()) {

                    Log.i("====== locationSnapshot", locationSnapshot.toString());

                    String locId = locationSnapshot.getKey();


                    locList.add(locId);


                    mapTemp = ((Map) locationSnapshot.getValue());

                    if (i == 0) {
                        ts = mapTemp.get("timestamp");
                    }


                    i++;
                    if (i == cnt) {

                        int f = locList.size();

                        while (f > 0) {

                            getLocationDetails(locList.get(f - 1));


                            f--;
                        }

                        locList.clear();
                        // refresh complete
                        layout.setRefreshing(false);

                    }


                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                mIsLoading = false;
            }
        });
    }


    private void setLocations() {

        Log.i("", "=======================================>> xx start ");

        Query query = mDbref.orderByChild("timestamp");

        if (startKey == null) {
            mDbRefListener = query.limitToFirst(feedLimit).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    for (DataSnapshot locationSnapshot : dataSnapshot.getChildren()) {

                        Log.i("====== locationSnapshot", locationSnapshot.toString());

                        String locId = locationSnapshot.getKey();
                        getLocationDetails(locId);
                        startKey = locationSnapshot.getKey();
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        } else {

            DatabaseReference ref2 = FirebaseDatabase.getInstance().getReference(Feeds).child(getUserID());
            Query query2 = ref2.orderByChild("timestamp");
            ValueEventListener mDbRefListener2;
            mDbRefListener2 = query2.startAt(startKey).limitToFirst(nextItemCount).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot locationSnapshot : dataSnapshot.getChildren()) {

                        Log.i("====== locationSnapshot", locationSnapshot.toString());

                        String locId = locationSnapshot.getKey();
                        getLocationDetails(locId);
                        startKey = locationSnapshot.getKey();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }

        Log.i("", "=======================================>> xx finish ");

    }

    private void getLocationDetails(final String loc) {

        tempRef = FirebaseDatabase.getInstance().getReference(Locations).child(loc);
        tempRefListener = tempRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                mapFeed.clear();
                mapFeed = ((Map) dataSnapshot.getValue());

                FeedAllItem feedAllItem1 = setFeedAllItem(loc);

                getUSerDetails(feedAllItem1, loc);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    private void getUSerDetails(final FeedAllItem feedAllItem1, final String loc) {

        userRef = FirebaseDatabase.getInstance().getReference(Users).child(feedAllItem1.getOwnerId());

        userRefListener = userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {

                    mapUser.clear();
                    mapUser = ((Map) dataSnapshot.getValue());

                    User user1 = setUser();

                    feedAllItem1.setOwnerName(user1.getName() + " " + user1.getSurname() + "/" + loc);
                    feedAllItem1.setOwnerPictureUrl(user1.getProfilePicSrc());

                    insertIntoRecyclerView(feedAllItem1);

                } else {
                    progressBar.setVisibility(View.GONE);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private FeedAllItem setFeedAllItem(String loc) {

        FeedAllItem tempFeedAllItem = new FeedAllItem();
        FeedPinItem tempFeedPinItem = new FeedPinItem();
        ArrayList<FeedPinItem> tempFeedPinItemList = new ArrayList<FeedPinItem>();

        tempFeedAllItem.setLocationId(loc);

        Log.i("map :", mapFeed.toString());

        for (Map.Entry<String, String> entry : mapFeed.entrySet()) {

            switch (entry.getKey()) {

                case userID:
                    tempFeedAllItem.setOwnerId(entry.getValue());
                    break;

                case pictureURL:
                    tempFeedPinItem = setFeedPinItem(picture, entry.getValue(), null, null);
                    tempFeedPinItemList.add(tempFeedPinItem);
                    break;

                case videoImageURL:
                    tempFeedPinItem = setFeedPinItem(video, entry.getValue(), mapFeed.get(videoURL), null);
                    tempFeedPinItemList.add(tempFeedPinItem);
                    break;

                case textURL:
                    String desc=null;
                    if(mapFeed.get(text) != null ) {
                        desc = mapFeed.get(text);
                    }
                    tempFeedPinItem = setFeedPinItem(text, entry.getValue(), null, desc);
                    tempFeedPinItemList.add(tempFeedPinItem);
                    break;

                default:
                    break;
            }

        }

        tempFeedAllItem.setFeedPinItems(tempFeedPinItemList);
        tempFeedAllItem.setTime(2);

        return tempFeedAllItem;

    }

    private User setUser() {

        User tempUser = new User();

        for (Map.Entry<String, String> entry : mapUser.entrySet()) {

            switch (entry.getKey()) {

                case userName:
                    tempUser.setName(entry.getValue());
                    break;

                case surname:
                    tempUser.setSurname(entry.getValue());
                    break;

                case profilePictureUrl:
                    tempUser.setProfilePicSrc(entry.getValue());
                    break;

                default:
                    break;
            }
        }

        return tempUser;
    }

    private FeedPinItem setFeedPinItem(String itemTag, String url, String detailUrl, String description) {

        FeedPinItem feedPinItem = new FeedPinItem();
        feedPinItem.setItemTag(itemTag);
        feedPinItem.setItemImageUrl(url);
        if (detailUrl != null) {
            feedPinItem.setItemDetailUrl(detailUrl);
        }
        if(description!=null){
            feedPinItem.setDescription(description);
        }

        return feedPinItem;
    }

    private boolean mIsLoading = false;

    private void insertIntoRecyclerView(FeedAllItem feedAllItem1) {

        relativeLayout.setBackgroundColor(Color.parseColor("#ffffb3"));
        progressBar.setVisibility(View.GONE);
        //dismiss kullanabilirsin...

        Log.i("", "========================  " + locationId + " ======================");
        //Log.i("imgUrl   ", tempPinData.getPinImageUri().toString());
        //Log.i("videoUrl ", tempPinData.getPinVideoUri().toString());
        //Log.i("textUrl  ", tempPinData.getPinTextUri().toString());

        feedAllItemList.add(feedAllItem1);

        recyclerViewVertical.setHasFixedSize(true);

        String x = feedAllItem1.getLocationId();
        String y = feedAllItemAdapter.getLastItemId();

        if (!x.equals(y))
            feedAllItemAdapter.addAll(feedAllItem1);

        //feedAllItemAdapter.addAll(feedAllItemList);
        loading = true;

        //recyclerViewVertical.setAdapter(feedAllItemAdapter);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }


}
