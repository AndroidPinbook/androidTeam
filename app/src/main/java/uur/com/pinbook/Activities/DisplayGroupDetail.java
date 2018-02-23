package uur.com.pinbook.Activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import uur.com.pinbook.FirebaseAdapters.FirebaseDeleteGroupAdapter;
import uur.com.pinbook.FirebaseGetData.FirebaseGetAccountHolder;
import uur.com.pinbook.FirebaseGetData.FirebaseGetGroups;
import uur.com.pinbook.JavaFiles.Friend;
import uur.com.pinbook.JavaFiles.Group;
import uur.com.pinbook.LazyList.ImageLoader;
import uur.com.pinbook.R;
import uur.com.pinbook.SpecialFragments.GroupDetailFragment;

import static uur.com.pinbook.ConstantsModel.FirebaseConstant.notifyNo;
import static uur.com.pinbook.ConstantsModel.FirebaseConstant.notifyYes;
import static uur.com.pinbook.ConstantsModel.StringConstant.*;

public class DisplayGroupDetail extends AppCompatActivity implements View.OnClickListener{

    public static Group group;
    ImageView groupPictureImgV;
    public ImageLoader imageLoader;
    TextView personCntTv;

    SpecialSelectTabAdapter adapter;

    ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_group_detail);

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

        CardView deleteGroupCardView = (CardView)findViewById(R.id.deleteGroupCardView);
        deleteGroupCardView.setOnClickListener(this);

        TextView deleteGroupTextView = (TextView) findViewById(R.id.deleteGroupTextView);

        if(getFbUserID().equals(group.getAdminID())) {
            addFriendCardView.setVisibility(View.VISIBLE);
            deleteGroupTextView.setText("GRUBU SIL");
        }else {
            deleteGroupTextView.setText("GRUPTAN CIK");
        }


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
        return FirebaseGetAccountHolder.getUserID();
    }

    @Override
    public void onStart(){
        super.onStart();
        reloadAdapter();
    }

    public void reloadAdapter(){
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

            case R.id.deleteGroupCardView:
                if(getFbUserID().equals(group.getAdminID())) {
                    showYesNoDialog(null, "Grubu silmek istediginize emin misiniz?", deleteGroup);
                }else {
                    showYesNoDialog(null, "Gruptan çikmak istediginize emin misiniz?", exitGroup);
                }
                break;
            default:
                Toast.makeText(this, "Error occured!!", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    public void showYesNoDialog(String title, String message, final String type) {

        AlertDialog.Builder builder = new AlertDialog.Builder(DisplayGroupDetail.this, android.R.style.Theme_Material_Dialog_Alert);

        builder.setIcon(R.drawable.warning_icon40);
        builder.setMessage(message);

        if (title != null)
            builder.setTitle(title);

        builder.setPositiveButton("EVET", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if(type.equals(deleteGroup)){
                    imageLoader.removeImageViewFromMap(group.getPictureUrl());
                    FirebaseDeleteGroupAdapter firebaseDeleteGroupAdapter =
                            new FirebaseDeleteGroupAdapter(group, deleteGroup);
                    FirebaseGetGroups.getInstance(group.getAdminID()).removeGroupFromList(group.getGroupID());
                    finish();
                }else if(type.equals(exitGroup)){
                    FirebaseDeleteGroupAdapter firebaseDeleteGroupAdapter =
                            new FirebaseDeleteGroupAdapter(group, exitGroup);
                    FirebaseGetGroups.getInstance(getFbUserID()).removeGroupFromList(group.getGroupID());
                    finish();
                }else {
                    dialog.dismiss();
                }

            }
        });

        builder.setNegativeButton("HAYIR", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }
}
