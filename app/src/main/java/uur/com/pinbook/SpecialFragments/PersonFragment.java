package uur.com.pinbook.SpecialFragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;
import uur.com.pinbook.FirebaseGetData.FirebaseGetFriends;
import uur.com.pinbook.LazyList.LazyAdapterFriends;
import uur.com.pinbook.R;

@SuppressLint("ValidFragment")
public class PersonFragment extends Fragment{

    RecyclerView personRecyclerView;

    private View mView;
    String FBuserID;

    LinearLayoutManager linearLayoutManager;

    @SuppressLint("ValidFragment")
    public PersonFragment(String FBuserID) {
        this.FBuserID = FBuserID;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        mView = inflater.inflate(R.layout.fragment_special_select, container, false);
        ButterKnife.bind(this, mView);
        return mView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        personRecyclerView = (RecyclerView) mView.findViewById(R.id.specialRecyclerView);
        getData(FBuserID);
    }

    public void getData(String userID){

        FirebaseGetFriends instance = FirebaseGetFriends.getInstance(userID);
        LazyAdapterFriends lazyAdapterFriends = new LazyAdapterFriends(getActivity(), instance.getFriendList());

        personRecyclerView.setAdapter(lazyAdapterFriends);
        linearLayoutManager  = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        personRecyclerView.setLayoutManager(linearLayoutManager);

    }
}
