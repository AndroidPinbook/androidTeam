package uur.com.pinbook.Activities;

import android.app.Fragment;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.twitter.sdk.android.core.TwitterCore;

import uur.com.pinbook.Adapters.SpecialSelectTabAdapter;
import uur.com.pinbook.DefaultModels.SelectedFriendList;
import uur.com.pinbook.DefaultModels.SelectedGroupList;
import uur.com.pinbook.R;
import uur.com.pinbook.SpecialFragments.GroupFragment;
import uur.com.pinbook.SpecialFragments.PersonFragment;

import static uur.com.pinbook.ConstantsModel.FirebaseConstant.propGroups;
import static uur.com.pinbook.ConstantsModel.FirebaseConstant.propPersons;
import static uur.com.pinbook.ConstantsModel.FirebaseConstant.thorough;
import static uur.com.pinbook.ConstantsModel.NumericConstant.groups;
import static uur.com.pinbook.ConstantsModel.NumericConstant.persons;
import static uur.com.pinbook.ConstantsModel.StringConstant.SelectPersonOrGroupTitle;

public class SpecialSelectActivity extends AppCompatActivity implements View.OnClickListener{

    private ViewPager viewPager;
    private TabLayout tabLayout;

    private String FBuserId;

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    public static String selectedProperty;
    public static boolean specialSelectedInd = false;

    private static SelectedGroupList selectedGroupListInstance;
    private static SelectedFriendList selectedFriendListInstance;

    Toolbar mToolBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_special_select);

        mToolBar = (Toolbar) findViewById(R.id.toolbarLayout);
        mToolBar.setTitle("Kisi veya Gruplari Seciniz...");
        mToolBar.setSubtitle("sun title");
        mToolBar.setNavigationIcon(R.drawable.back_arrow);
        mToolBar.setBackgroundColor(getResources().getColor(R.color.background, null));
        mToolBar.setTitleTextColor(getResources().getColor(R.color.background_white, null));
        mToolBar.setSubtitleTextColor(getResources().getColor(R.color.background_white, null));
        setSupportActionBar(mToolBar);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        FBuserId = currentUser.getUid();

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        selectedProperty = propPersons;

        FloatingActionButton addSpecialFab = (FloatingActionButton) findViewById(R.id.addSpecialFab);

        addSpecialFab.setOnClickListener(this);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                switch (tab.getPosition()){
                    case persons:
                        selectedProperty = propPersons;
                        Log.i("Info", "Tablayout kisiler");
                        break;
                    case groups:
                        selectedProperty = propGroups;
                        Log.i("Info", "Tablayout groups");
                        break;
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
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_special_select, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public boolean onPrepareOptionsMenu(Menu menu)
    {
        MenuItem register = menu.findItem(R.id.addNewGroup);
        if(selectedProperty == propPersons)
            register.setVisible(false);
        else
            register.setVisible(true);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
            case R.id.logOut:
                finish();
                startActivity(new Intent(this, EnterPageActivity.class));
                break;
            case R.id.addNewGroup:
                startActivity(new Intent(this, AddNewGroupActivity.class));
                break;
            default:
                return super.onOptionsItemSelected(item);

        }
        return super.onOptionsItemSelected(item);
    }

    private void setupViewPager(final ViewPager viewPager) {

        SpecialSelectTabAdapter adapter = new SpecialSelectTabAdapter(this.getSupportFragmentManager());
        adapter.addFragment(new PersonFragment(FBuserId),"Kisiler");
        adapter.addFragment(new GroupFragment(FBuserId), "Gruplar");
        viewPager.setAdapter(adapter);
    }

    @Override
    public void onClick(View v) {

        int i = v.getId();
        Intent intent;

        switch (i) {
            case R.id.addSpecialFab:
                selectedFriendListInstance = SelectedFriendList.getInstance();
                selectedGroupListInstance = SelectedGroupList.getInstance();

                specialSelectedInd = true;

                if(selectedProperty == propPersons){
                    if(selectedFriendListInstance.getSelectedFriendList().size() == 0){
                        Toast.makeText(this, "En az 1 kisi secmelisiniz!", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    selectedGroupListInstance.clearGrouplist();
                }else {
                    if(selectedGroupListInstance.getGroupList().size() == 0){
                        Toast.makeText(this, "En az 1 grup secmelisiniz!", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    selectedFriendListInstance.clearFriendList();
                }
                
                finish();
                break;

            default:
                Toast.makeText(this, "Error occured!!", Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
