package uur.com.pinbook.SpecialFragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import butterknife.ButterKnife;
import uur.com.pinbook.FirebaseGetData.FirebaseGetGroups;
import uur.com.pinbook.ListAdapters.GroupVerticalListAdapter;
import uur.com.pinbook.R;

@SuppressLint("ValidFragment")
public class GroupFragment extends Fragment {

    RecyclerView groupRecyclerView;

    private View mView;
    String FBuserID;
    ViewGroup mContainer;
    LayoutInflater mLayoutInflater;

    LinearLayoutManager linearLayoutManager;
    RelativeLayout specialSelectRelLayout;

    private Context context;
    private String searchText;

    @SuppressLint("ValidFragment")
    public GroupFragment(String FBuserID, Context context, String searchText) {
        this.FBuserID = FBuserID;
        this.context = context;
        this.searchText = searchText;
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
        mView = inflater.inflate(R.layout.fragment_special_select, container, false);

        ButterKnife.bind(this, mView);
        specialSelectRelLayout = (RelativeLayout) mView.findViewById(R.id.specialSelectRelLayout);
        return mView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        groupRecyclerView = (RecyclerView) mView.findViewById(R.id.specialRecyclerView);
        getData(FBuserID);
    }

    public void getData(String userID){

        FirebaseGetGroups instance = FirebaseGetGroups.getInstance(userID);
        final GroupVerticalListAdapter groupVerticalListAdapter = new GroupVerticalListAdapter(context, instance.getGroupArrayList(), searchText);

        groupRecyclerView.setAdapter(groupVerticalListAdapter);
        linearLayoutManager  = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        groupRecyclerView.setLayoutManager(linearLayoutManager);
    }
}
