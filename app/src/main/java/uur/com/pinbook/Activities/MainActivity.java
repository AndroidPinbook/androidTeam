package uur.com.pinbook.Activities;


import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import uur.com.pinbook.FirebaseGetData.FirebaseGetFriends;
import uur.com.pinbook.JavaFiles.Friend;
import uur.com.pinbook.LazyList.LazyAdapter;
import uur.com.pinbook.R;

public class MainActivity extends Activity {

    ListView list;
    LazyAdapter adapter;

    private FirebaseAuth firebaseAuth;
    private String FBuserId;

    private String[] mStrings;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_xx);

        Button b=(Button)findViewById(R.id.button1);
        b.setOnClickListener(listener);

        Button c=(Button)findViewById(R.id.button2);
        c.setOnClickListener(listener2);

        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        FBuserId = currentUser.getUid();

        FirebaseGetFriends instance = FirebaseGetFriends.getInstance(FBuserId);

        mStrings = new String[instance.getListSize()];

        int i = 0;
        for(Friend friend : instance.getFriendList()){
            if(!friend.getUserID().equals(" ") && !friend.getProfilePicSrc().equals(" ")){
                mStrings[i] = friend.getProfilePicSrc();
                i ++;
            }
        }

        list=(ListView)findViewById(R.id.list);
        adapter=new LazyAdapter(this, mStrings);
        list.setAdapter(adapter);
    }

    @Override
    public void onDestroy()
    {
        list.setAdapter(null);
        super.onDestroy();
    }

    public OnClickListener listener=new OnClickListener(){
        @Override
        public void onClick(View arg0) {
            adapter.imageLoader.clearCache();
        }
    };

    public OnClickListener listener2=new OnClickListener(){
        @Override
        public void onClick(View arg0) {
            adapter.notifyDataSetChanged();
        }
    };

}