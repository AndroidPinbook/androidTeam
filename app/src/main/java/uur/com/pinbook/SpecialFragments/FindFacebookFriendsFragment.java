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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

import butterknife.ButterKnife;
import uur.com.pinbook.Activities.DisplayGroupDetail;
import uur.com.pinbook.FirebaseGetData.FirebaseGetFriends;
import uur.com.pinbook.JavaFiles.Friend;
import uur.com.pinbook.JavaFiles.Group;
import uur.com.pinbook.ListAdapters.FriendGridListAdapter;
import uur.com.pinbook.ListAdapters.FriendVerticalListAdapter;
import uur.com.pinbook.R;

import static uur.com.pinbook.ConstantsModel.StringConstant.*;

@SuppressLint("ValidFragment")
public class FindFacebookFriendsFragment extends Fragment{

    RecyclerView personRecyclerView;

    private View mView;
    Context context;

    LinearLayoutManager linearLayoutManager;
    GridLayoutManager gridLayoutManager;

    @SuppressLint("ValidFragment")
    public FindFacebookFriendsFragment(Context context) {
        this.context = context;
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
        getFacebookFriends();
    }

    public void getFacebookFriends() {

        GraphRequest graphRequest = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(),
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {

                        Log.i("Info", "Facebook response:" + response.toString());


                    }

                    /*@Override
                    public void onCompleted(JRSONObject object, GraphResponse response) {

                        Log.i("Info", "Facebook response:" + response.toString());

                        try {

                            user.setEmail(object.getString("email"));
                            user.setBirthdate(object.getString("birthday"));
                            user.setGender(object.getString("gender"));
                            user.setUsername(" ");
                            user.setPhoneNum(" ");

                            String[] elements = object.getString("name").split(" ");
                            user.setName(elements[0]);

                            String[] lastname = Arrays.copyOfRange(elements, 1, elements.length);

                            StringBuilder builder = new StringBuilder();
                            for (String s : lastname) {
                                builder.append(s);
                                builder.append(" ");
                            }
                            String surname = builder.toString().trim();
                            user.setSurname(surname);

                            String fbUserId = object.getString("id");
                            setFacebookProfilePicture(fbUserId);

                            Log.i("FBLogin", "  >>email     :" + user.getEmail());
                            Log.i("FBLogin", "  >>birthday  :" + user.getBirthdate());
                            Log.i("FBLogin", "  >>gender    :" + user.getGender());
                            Log.i("FBLogin", "  >>name      :" + user.getName());
                            Log.i("FBLogin", "  >>surname   :" + user.getSurname());

                        } catch (JSONException e) {
                            Log.i("Info", "  >>JSONException error:" + e.toString());
                        } catch (Exception e) {
                            Log.i("Info", "  >>Profile error:" + e.toString());
                        }
                    }*/
                });

        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,email,gender,birthday, friends");
        graphRequest.setParameters(parameters);
        graphRequest.executeAsync();
    }


    public void setFacebookProfilePicture(String userID) {

        try {
            String url = "https://graph.facebook.com/" + userID + "/picture?type=large";
            //user.setProfilePicSrc(url);

        } catch (Exception e) {

            Log.i("Info", "  >>setFacebookProfilePicture error:" + e.toString());
        }
    }

    /*public void getData(){

        FirebaseGetFriends instance = FirebaseGetFriends.getInstance(FBuserID);

        switch (viewType){
            case verticalShown:
                FriendVerticalListAdapter friendVerticalListAdapter = null;

                Log.i("Info", "xxx:" + DisplayGroupDetail.class.getSimpleName());

                if(comingPage != null) {
                    if(comingPage.equals(DisplayGroupDetail.class.getSimpleName()))
                        friendVerticalListAdapter = new FriendVerticalListAdapter(context, getFriendsForGroup(instance), null);
                    else
                        friendVerticalListAdapter = new FriendVerticalListAdapter(context, instance.getFriendList(), null);
                }
                else{
                    friendVerticalListAdapter = new FriendVerticalListAdapter(context, instance.getFriendList(), searchText);
                }

                personRecyclerView.setAdapter(friendVerticalListAdapter);
                linearLayoutManager = new LinearLayoutManager(context);
                linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                personRecyclerView.setLayoutManager(linearLayoutManager);
                break;

            case horizontalShown:
                linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
                personRecyclerView.setLayoutManager(linearLayoutManager);
                break;

            case gridShown:
                instance = FirebaseGetFriends.getInstance(FBuserID);
                FriendGridListAdapter friendGridListAdapter = new FriendGridListAdapter(context, instance.getFriendList());
                personRecyclerView.setAdapter(friendGridListAdapter);
                gridLayoutManager =new GridLayoutManager(context, 4);
                personRecyclerView.setLayoutManager(gridLayoutManager);
                break;

            default:
                Toast.makeText(context, "Person Fragment getData teknik hata!!", Toast.LENGTH_SHORT).show();
        }
    }

    //Gruba eklenecek arkadaslar listelenecek
    public ArrayList<Friend> getFriendsForGroup(FirebaseGetFriends instance){

        ArrayList<Friend> friendArrayList = new ArrayList<Friend>();

        for(Friend friend : instance.getFriendList()){

            boolean friendFind = false;

            for(int i=0; i< group.getFriendList().size(); i++){
                Friend tempFriend = group.getFriendList().get(i);

                if(friend.getUserID().equals(tempFriend.getUserID())){
                    friendFind = true;
                    break;
                }
            }

            if(!friendFind)
                friendArrayList.add(friend);
        }
        return  friendArrayList;
    }*/
}
