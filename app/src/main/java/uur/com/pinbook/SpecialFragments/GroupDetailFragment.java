package uur.com.pinbook.SpecialFragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import butterknife.ButterKnife;
import uur.com.pinbook.FirebaseGetData.FirebaseGetFriends;
import uur.com.pinbook.JavaFiles.Group;
import uur.com.pinbook.ListAdapters.FriendGridListAdapter;
import uur.com.pinbook.ListAdapters.FriendVerticalListAdapter;
import uur.com.pinbook.ListAdapters.GroupDTLVerticalListAdapter;
import uur.com.pinbook.R;

import static uur.com.pinbook.ConstantsModel.StringConstant.gridShown;
import static uur.com.pinbook.ConstantsModel.StringConstant.horizontalShown;
import static uur.com.pinbook.ConstantsModel.StringConstant.verticalShown;

@SuppressLint("ValidFragment")
public class GroupDetailFragment extends Fragment {

    Group group;
    String viewType;
    View mView;
    RecyclerView personRecyclerView;

    LinearLayoutManager linearLayoutManager;

    @SuppressLint("ValidFragment")
    public GroupDetailFragment(Group group, String viewType) {
        this.group = group;
        this.viewType = viewType;
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
        getData();
    }

    public void getData(){

        switch (viewType){

            case verticalShown:
                GroupDTLVerticalListAdapter groupDTLVerticalListAdapter =
                        new GroupDTLVerticalListAdapter(getActivity(), group.getFriendList(), group.getGroupID());
                personRecyclerView.setAdapter(groupDTLVerticalListAdapter);
                linearLayoutManager = new LinearLayoutManager(getActivity());
                linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                personRecyclerView.setLayoutManager(linearLayoutManager);
                break;

            default:
                Toast.makeText(getActivity(), "Person Fragment getData teknik hata!!", Toast.LENGTH_SHORT).show();
        }
    }
}
