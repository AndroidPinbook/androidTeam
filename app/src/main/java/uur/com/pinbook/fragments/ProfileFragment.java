package uur.com.pinbook.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import butterknife.ButterKnife;
import uur.com.pinbook.Activities.EditProfileActivity;
import uur.com.pinbook.Activities.ProfilePageActivity;
import uur.com.pinbook.Activities.SpecialSelectActivity;
import uur.com.pinbook.Adapters.SpecialSelectTabAdapter;
import uur.com.pinbook.FirebaseGetData.FBGetInviteContactInbounds;
import uur.com.pinbook.FirebaseGetData.FBGetInviteContactOutbounds;
import uur.com.pinbook.FirebaseGetData.FBGetInviteFacebookInbounds;
import uur.com.pinbook.FirebaseGetData.FBGetInviteFacebookOutbounds;
import uur.com.pinbook.FirebaseGetData.FirebaseGetAccountHolder;
import uur.com.pinbook.FirebaseGetData.FirebaseGetFriends;
import uur.com.pinbook.FirebaseGetData.FirebaseGetGroups;
import uur.com.pinbook.JavaFiles.User;
import uur.com.pinbook.LazyList.ImageLoader;
import uur.com.pinbook.OtherFragments.ProfileDetailFragment;
import uur.com.pinbook.R;
import uur.com.pinbook.SpecialFragments.GroupFragment;
import uur.com.pinbook.SpecialFragments.PersonFragment;

import static uur.com.pinbook.ConstantsModel.FirebaseConstant.Friends;
import static uur.com.pinbook.ConstantsModel.FirebaseConstant.InviteFacebookOutbound;
import static uur.com.pinbook.ConstantsModel.FirebaseConstant.UserLocations;
import static uur.com.pinbook.ConstantsModel.FirebaseConstant.propFriends;
import static uur.com.pinbook.ConstantsModel.FirebaseConstant.propGroups;
import static uur.com.pinbook.ConstantsModel.FirebaseConstant.propPersons;
import static uur.com.pinbook.ConstantsModel.FirebaseConstant.propShares;
import static uur.com.pinbook.ConstantsModel.NumericConstant.groups;
import static uur.com.pinbook.ConstantsModel.NumericConstant.persons;
import static uur.com.pinbook.ConstantsModel.NumericConstant.showFriendsItem;
import static uur.com.pinbook.ConstantsModel.NumericConstant.showGroupItem;
import static uur.com.pinbook.ConstantsModel.NumericConstant.showShareItem;
import static uur.com.pinbook.ConstantsModel.StringConstant.ProfileTitle;
import static uur.com.pinbook.ConstantsModel.StringConstant.displayRounded;
import static uur.com.pinbook.ConstantsModel.StringConstant.friendsCacheDirectory;
import static uur.com.pinbook.ConstantsModel.StringConstant.noFriendText;
import static uur.com.pinbook.ConstantsModel.StringConstant.noGroupText;
import static uur.com.pinbook.ConstantsModel.StringConstant.profileDetailFragment;
import static uur.com.pinbook.ConstantsModel.StringConstant.verticalShown;

public class ProfileFragment extends BaseFragment{

    private Button editProfileButton;
    private ImageView openSettingsImgv;

    private View mView;
    public static String selectedProperty;

    private FrameLayout profileMainLayout;
    private ImageView profilePicImgview;
    private TextView shareCountTextView;
    private TextView friendCountTextView;

    private User user;
    ImageLoader imageLoader;
    private Context context;

    private ViewPager viewPager;
    private TabLayout tabLayout;
    SpecialSelectTabAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_profile, container, false);
        ButterKnife.bind(this, mView);

        context = getActivity();

        editProfileButton = mView.findViewById(R.id.editProfileButton);
        openSettingsImgv = mView.findViewById(R.id.openSettingsImgv);
        profileMainLayout = mView.findViewById(R.id.profileMainLayout);
        profilePicImgview = mView.findViewById(R.id.profilePicImgview);

        shareCountTextView = mView.findViewById(R.id.shareCountText);
        friendCountTextView = mView.findViewById(R.id.friendCountText);

        imageLoader = new ImageLoader(context, friendsCacheDirectory);

        user = FirebaseGetAccountHolder.getInstance().getUser();

        getShareItemsCount();
        getFriendsCount();

        imageLoader.DisplayImage(user.getProfilePicSrc(), profilePicImgview, displayRounded);

        viewPager = mView.findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = mView.findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        FBGetInviteFacebookInbounds.getInstance();
        FBGetInviteFacebookOutbounds.getInstance();
        FBGetInviteContactInbounds.getInstance();
        FBGetInviteContactOutbounds.getInstance();

        editProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), EditProfileActivity.class));
            }
        });

        openSettingsImgv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProfileDetailFragment nextFrag = new ProfileDetailFragment(getActivity());
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(((ViewGroup)getView().getParent()).getId(), nextFrag, profileDetailFragment)
                        .addToBackStack(profileDetailFragment)
                        .commit();
            }
        });

        viewPager.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        selectedProperty = propPersons;

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                switch (tab.getPosition()){
                    case showShareItem:

                        selectedProperty = propShares;

                        /*if( FirebaseGetFriends.getInstance(getFbUserID()).getListSize() == 0) {
                            noItemFoundTv.setVisibility(View.VISIBLE);
                            noItemFoundTv.setText(noFriendText);
                        }else {
                            noItemFoundTv.setVisibility(View.GONE);
                        }*/

                        Log.i("Info", "Tablayout Shares");
                        break;
                    case showFriendsItem:

                        selectedProperty = propFriends;

                        /*if(FirebaseGetGroups.getInstance(getFbUserID()).getListSize() == 0) {
                            noItemFoundTv.setVisibility(View.VISIBLE);
                            noItemFoundTv.setText(noGroupText);
                        }else {
                            noItemFoundTv.setVisibility(View.GONE);
                        }*/

                        Log.i("Info", "Tablayout friends");
                        break;
                    case showGroupItem:
                        selectedProperty = propGroups;

                        /*if(FirebaseGetGroups.getInstance(getFbUserID()).getListSize() == 0) {
                            noItemFoundTv.setVisibility(View.VISIBLE);
                            noItemFoundTv.setText(noGroupText);
                        }else {
                            noItemFoundTv.setVisibility(View.GONE);
                        }*/

                        Log.i("Info", "Tablayout groups");
                    default:
                        Log.i("Info", "Tablayout unknown");
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });

        return mView;
    }

    private void setupViewPager(final ViewPager viewPager) {

        if(FirebaseGetAccountHolder.getUserID().isEmpty()) return;

        adapter = new SpecialSelectTabAdapter(getFragmentManager());
        adapter.addFragment(new GroupFragment(FirebaseGetAccountHolder.getUserID(), context, null), "Gonderiler");
        adapter.addFragment(new PersonFragment(FirebaseGetAccountHolder.getUserID(), verticalShown, null, null, null, context),"Kisiler");
        adapter.addFragment(new GroupFragment(FirebaseGetAccountHolder.getUserID(), context, null), "Gruplar");
        viewPager.setAdapter(adapter);
    }

    public void reloadAdapter(){
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onStart(){
        super.onStart();
        reloadAdapter();
    }

    @Override
    public void onPause(){
        super.onPause();
       // viewPager.removeAllViews();

        Log.i("Info", "Infoooooo");
    }

    private void getShareItemsCount() {

        if(FirebaseGetAccountHolder.getUserID().isEmpty()) return;

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(UserLocations).child(FirebaseGetAccountHolder.getUserID());

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                long shareCount = dataSnapshot.getChildrenCount();
                shareCountTextView.setText(Long.toString(shareCount));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void getFriendsCount(){

        if(FirebaseGetAccountHolder.getUserID().isEmpty()) return;

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(Friends).child(FirebaseGetAccountHolder.getUserID());

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                long friendCount = dataSnapshot.getChildrenCount();
                friendCountTextView.setText(Long.toString(friendCount));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ((ProfilePageActivity)getActivity()).updateToolbarTitle(ProfileTitle);
    }


}
