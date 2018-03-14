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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import butterknife.ButterKnife;
import uur.com.pinbook.Activities.DisplayGroupDetail;
import uur.com.pinbook.DefaultModels.ContactFriendList;
import uur.com.pinbook.DefaultModels.FacebookFriendList;
import uur.com.pinbook.DefaultModels.SelectedFriendList;
import uur.com.pinbook.FirebaseGetData.FirebaseGetFriends;
import uur.com.pinbook.JavaFiles.Friend;
import uur.com.pinbook.JavaFiles.Group;
import uur.com.pinbook.ListAdapters.FriendGridListAdapter;
import uur.com.pinbook.ListAdapters.FriendVerticalListAdapter;
import uur.com.pinbook.ListAdapters.InviteOutboundVerListAdapter;
import uur.com.pinbook.R;

import static uur.com.pinbook.ConstantsModel.FirebaseConstant.*;
import static uur.com.pinbook.ConstantsModel.StringConstant.*;

@SuppressLint("ValidFragment")
public class FindContactFriendFragment extends Fragment{

    RecyclerView personRecyclerView;

    private View mView;
    Context context;
    String viewType;

    LinearLayoutManager linearLayoutManager;
    GridLayoutManager gridLayoutManager;

    @SuppressLint("ValidFragment")
    public FindContactFriendFragment(Context context, String viewType) {
        this.context = context;
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
        sendFriendsToRecView();
    }

    public void sendFriendsToRecView(){

        switch (viewType) {
            case verticalShown:

                InviteOutboundVerListAdapter inviteOutboundVerListAdapter = null;

                inviteOutboundVerListAdapter = new InviteOutboundVerListAdapter(context,
                        ContactFriendList.getInstance().getContactFriendList(), null);

                personRecyclerView.setAdapter(inviteOutboundVerListAdapter);
                linearLayoutManager = new LinearLayoutManager(context);
                linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                personRecyclerView.setLayoutManager(linearLayoutManager);
                break;
            default:
                Toast.makeText(context, "Fragment getData teknik hata!!", Toast.LENGTH_SHORT).show();
        }
    }
}
