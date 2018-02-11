package uur.com.pinbook.Activities;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import uur.com.pinbook.Adapters.SpecialSelectTabAdapter;
import uur.com.pinbook.DefaultModels.SelectedFriendList;
import uur.com.pinbook.FirebaseAdapters.FirebaseAddFriendToGroupAdapter;
import uur.com.pinbook.FirebaseGetData.FirebaseGetGroups;
import uur.com.pinbook.JavaFiles.Friend;
import uur.com.pinbook.JavaFiles.Group;
import uur.com.pinbook.R;
import uur.com.pinbook.SpecialFragments.PersonFragment;

import static uur.com.pinbook.ConstantsModel.FirebaseConstant.*;
import static uur.com.pinbook.ConstantsModel.StringConstant.*;

public class AddNewFriendActivity extends AppCompatActivity {

    Toolbar mToolBar;
    private String FBuserId;

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    ViewPager viewPager;
    String comingPageName = null;

    Group group;

    private static SelectedFriendList selectedFriendListInstance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_group);

        mToolBar = (Toolbar) findViewById(R.id.toolbarLayout);
        //mToolBar.setTitle(getResources().getString(R.string.addNewGroup));
        mToolBar.setSubtitle(getResources().getString(R.string.addPersonToGroup));
        mToolBar.setNavigationIcon(R.drawable.back_arrow);
        mToolBar.setBackgroundColor(getResources().getColor(R.color.background, null));
        mToolBar.setTitleTextColor(getResources().getColor(R.color.background_white, null));
        mToolBar.setSubtitleTextColor(getResources().getColor(R.color.background_white, null));
        setSupportActionBar(mToolBar);

        getIntentValues(savedInstanceState);

        Log.i("info", "ddd:" + FirebaseGetGroups.getInstance(FBuserId));

        SelectedFriendList.setInstance(null);

        FloatingActionButton nextFab = (FloatingActionButton) findViewById(R.id.nextFab);
        nextFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkSelectedPerson();
            }
        });

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        FBuserId = currentUser.getUid();

        openPersonSelectionPage();
    }

    private void getIntentValues(Bundle savedInstanceState) {

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras != null) {
                comingPageName = extras.getString(comeFromPage);
            }
        } else {
            comingPageName = (String) savedInstanceState.getSerializable(comeFromPage);
        }

        Intent i = getIntent();
        group = (Group) i.getSerializableExtra(groupConstant);
    }

    private void openPersonSelectionPage() {

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        SpecialSelectTabAdapter adapter = new SpecialSelectTabAdapter(this.getSupportFragmentManager());
        adapter.addFragment(new PersonFragment(FBuserId, verticalShown, comingPageName,
                group, null, AddNewFriendActivity.this)," ");
        viewPager.setAdapter(adapter);
    }

    public void checkSelectedPerson(){

        selectedFriendListInstance = SelectedFriendList.getInstance();

        if (selectedFriendListInstance.getSelectedFriendList().size() == 0) {
            Toast.makeText(this, "En az 1 kisi secmelisiniz!", Toast.LENGTH_SHORT).show();
            return;
        }

        if(comingPageName != null){
            if(comingPageName.equals(DisplayGroupDetail.class.getSimpleName())) {
                addFriendToGroup();
                finish();
            }
        }else
            startActivity(new Intent(this, AddGroupDetailActivity.class));
    }

    private void addFriendToGroup() {

        for(Friend friend : selectedFriendListInstance.getSelectedFriendList()) {
            //((DisplayGroupDetail) getApplicationContext()).addFriendToGroup(friend);
             //Context context = ((DisplayGroupDetail) this).getAppContext();
            DisplayGroupDetail.addFriendToGroup(friend);

     //       ((DisplayGroupDetail)this).addFriendToGroup(null);
        }

        FirebaseAddFriendToGroupAdapter addFriendToGroupAdapter =
                new FirebaseAddFriendToGroupAdapter(selectedFriendListInstance.getSelectedFriendList(), group.getGroupID());
        addFriendToGroupAdapter.addFriendsToGroup();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
