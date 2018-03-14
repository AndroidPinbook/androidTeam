package uur.com.pinbook.OtherFragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toolbar;

import java.util.ArrayList;

import butterknife.ButterKnife;
import uur.com.pinbook.Activities.SpecialSelectActivity;
import uur.com.pinbook.Adapters.SpecialSelectTabAdapter;
import uur.com.pinbook.DefaultModels.GetContactList;
import uur.com.pinbook.FirebaseGetData.FirebaseGetAccountHolder;
import uur.com.pinbook.FirebaseGetData.FirebaseGetGroups;
import uur.com.pinbook.JavaFiles.Friend;
import uur.com.pinbook.JavaFiles.User;
import uur.com.pinbook.ListAdapters.GroupVerticalListAdapter;
import uur.com.pinbook.R;
import uur.com.pinbook.SpecialFragments.FindContactFriendFragment;
import uur.com.pinbook.SpecialFragments.FindFacebookFriendsFragment;
import uur.com.pinbook.SpecialFragments.GroupFragment;
import uur.com.pinbook.SpecialFragments.PersonFragment;

import static uur.com.pinbook.ConstantsModel.StringConstant.verticalShown;

@SuppressLint("ValidFragment")
public class InviteContactFriendFragment extends Fragment {

    private View mView;
    String FBuserID;
    ViewGroup mContainer;
    LayoutInflater mLayoutInflater;

    private ViewPager viewPager;
    private TabLayout tabLayout;
    SpecialSelectTabAdapter adapter;

    LinearLayoutManager linearLayoutManager;

    private Context context;
    private String searchText;

    @SuppressLint("ValidFragment")
    public InviteContactFriendFragment(Context context) {
        this.context = context;
        this.FBuserID = FirebaseGetAccountHolder.getUserID();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        this.mContainer = container;
        this.mLayoutInflater = inflater;
        mView = inflater.inflate(R.layout.activity_special_select, container, false);
        ButterKnife.bind(this, mView);

        android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) mView.findViewById(R.id.toolbarLayout);
        toolbar.setVisibility(View.GONE);

        FloatingActionButton addSpecialFab = (FloatingActionButton) mView.findViewById(R.id.addSpecialFab);
        addSpecialFab.setVisibility(View.GONE);

        AppBarLayout appBarLay = (AppBarLayout) mView.findViewById(R.id.appBarLay);
        appBarLay.setVisibility(View.GONE);

        return mView;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        viewPager = (ViewPager) mView.findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        //tabLayout = (TabLayout) mView.findViewById(R.id.tabs);
        //tabLayout.setupWithViewPager(viewPager);
    }

    private void setupViewPager(final ViewPager viewPager) {

        adapter = new SpecialSelectTabAdapter(getFragmentManager());
        //adapter.addFragment(new PersonFragment(FBuserID, verticalShown, null,
        //        null, null, getActivity()),"Facebook");
        adapter.addFragment(new FindContactFriendFragment( getActivity(), verticalShown), "Kisiler");
        //adapter.addFragment(new GroupFragment(FBuserID, getActivity(), null), "Contact");
        viewPager.setAdapter(adapter);
    }
}
