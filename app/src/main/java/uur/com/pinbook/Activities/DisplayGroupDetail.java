package uur.com.pinbook.Activities;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

import uur.com.pinbook.Adapters.SpecialSelectTabAdapter;
import uur.com.pinbook.DefaultModels.SelectedFriendList;
import uur.com.pinbook.JavaFiles.Friend;
import uur.com.pinbook.JavaFiles.Group;
import uur.com.pinbook.LazyList.ImageLoader;
import uur.com.pinbook.R;
import uur.com.pinbook.SpecialFragments.GroupDetailFragment;

import static uur.com.pinbook.ConstantsModel.StringConstant.*;

public class DisplayGroupDetail extends AppCompatActivity implements View.OnClickListener{

    public static Group group;
    ImageView groupPictureImgV;
    public ImageLoader imageLoader;
    TextView personCntTv;
    String FBUserID = null;

    SpecialSelectTabAdapter adapter;
    private Context appContext = null;

    ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_group_detail);
        appContext = getApplicationContext();

        imageLoader = new ImageLoader(this, groupsCacheDirectory);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.inflateMenu(R.menu.menu_profile_page);

        Intent i = getIntent();
        group = (Group) i.getSerializableExtra(groupConstant);

        groupPictureImgV = (ImageView) findViewById(R.id.groupPictureImgv);

        personCntTv = (TextView) findViewById(R.id.personCntTv);
        personCntTv.setText(Integer.toString(group.getFriendList().size()));

        CardView addFriendCardView = (CardView) findViewById(R.id.addFriendCardView);
        addFriendCardView.setOnClickListener(this);

        if(getFbUserID().equals(group.getAdminID()))
            addFriendCardView.setVisibility(View.VISIBLE);

        CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbarLayout.setTitle(group.getGroupName());

        imageLoader.DisplayImage(group.getPictureUrl(), groupPictureImgV, displayRectangle);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager();
    }


    private void setupViewPager() {
        adapter = new SpecialSelectTabAdapter(this.getSupportFragmentManager());
        adapter.addFragment(new GroupDetailFragment(group, verticalShown)," ");
        viewPager.setAdapter(adapter);
    }

    public Group getGroup(){
        return this.group;
    }

    public void setGroup(Group group){
        this.group = group;
    }

    public void setGroupFriendList(ArrayList<Friend> friendList){
        this.group.setFriendList(friendList);
    }

    public static void addFriendToGroup(Friend friend){
        group.getFriendList().add(friend);
    }

    public String getFbUserID() {

        if(FBUserID == null){
            FirebaseAuth firebaseAuth;
            firebaseAuth = FirebaseAuth.getInstance();
            FirebaseUser currentUser = firebaseAuth.getCurrentUser();
            FBUserID = currentUser.getUid();
        }
        return FBUserID;
    }

    @Override
    public void onStart(){
        super.onStart();
        reloadAdapter();
    }

    public void reloadAdapter(){
        Log.i("Info", "group dt:" + group);
        //adapter.notifyDataSetChanged();
        adapter.addFragment(new GroupDetailFragment(group, verticalShown)," ");
        viewPager.setAdapter(adapter);
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();

        Intent intent;

        switch (i) {
            case R.id.addFriendCardView:
                intent = new Intent(getApplicationContext(), AddNewFriendActivity.class);
                intent.putExtra(comeFromPage, this.getClass().getSimpleName());
                intent.putExtra(groupConstant, group);
                startActivity(intent);
                break;

            default:
                Toast.makeText(this, "Error occured!!", Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
