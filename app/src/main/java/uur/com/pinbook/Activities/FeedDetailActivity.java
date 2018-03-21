package uur.com.pinbook.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.RelativeLayout;
import android.widget.Toast;

import uur.com.pinbook.FeedsChildFragments.ImageFragment;
import uur.com.pinbook.R;
import uur.com.pinbook.RecyclerView.Adapter.FeedAllItemAdapter;
import uur.com.pinbook.RecyclerView.Model.FeedPinItem;

import static uur.com.pinbook.ConstantsModel.FirebaseConstant.picture;
import static uur.com.pinbook.ConstantsModel.FirebaseConstant.text;
import static uur.com.pinbook.ConstantsModel.FirebaseConstant.video;

public class FeedDetailActivity extends AppCompatActivity {

    Toolbar mToolBar;
    RelativeLayout container;
    FeedPinItem singleItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_detail);

        overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);

        mToolBar = findViewById(R.id.toolbarLayout);
        mToolBar.setTitle("Şifreyi Değiştir");
        mToolBar.setNavigationIcon(R.drawable.back_arrow);
        mToolBar.setBackgroundColor(getResources().getColor(R.color.background, null));
        mToolBar.setTitleTextColor(getResources().getColor(R.color.background_white, null));
        setSupportActionBar(mToolBar);

        container = findViewById(R.id.container);
        setItemsClickListeners();

        Intent intent = getIntent();
        singleItem = (FeedPinItem) intent.getSerializableExtra("singleItem");
        Log.i("itemTag", singleItem.getName());
        if (singleItem.getName().equals(picture)) {
            showFragment(new ImageFragment());
        } else if (singleItem.getName() == video) {

        } else if (singleItem.getName() == text) {

        } else {

        }

        //Toast.makeText(getApplicationContext(), singleItem.getName(), Toast.LENGTH_SHORT).show();

    }

    private void setItemsClickListeners() {

        container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getCurrentFocus() != null) {
                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                }
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return super.onOptionsItemSelected(item);
    }

    public void showFragment(Fragment fragment) {

        Bundle bundle = new Bundle();
        bundle.putSerializable("singleItem", singleItem);
        fragment.setArguments(bundle);



        String TAG = fragment.getClass().getSimpleName();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

        fragmentTransaction.replace(R.id.mainContainer, fragment, TAG);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commitAllowingStateLoss();
    }

    public void backstackFragment() {
        Log.d("Stack count", getSupportFragmentManager().getBackStackEntryCount() + "");

        if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
            finish();
        }
        getSupportFragmentManager().popBackStack();
        removeCurrentFragment();
    }

    public void removeCurrentFragment() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        Fragment currentFrag = getSupportFragmentManager()
                .findFragmentById(R.id.mainContainer);

        if (currentFrag != null) {
            transaction.remove(currentFrag);
        }
        transaction.commitAllowingStateLoss();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
            finish();
        }
    }

    public void enableNavigationIcon() {

        mToolBar.setNavigationIcon(R.mipmap.back_arrow);
        mToolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backstackFragment();
            }
        });
    }

    public void disableNavigationIcon() {
        mToolBar.setNavigationIcon(null);
    }

    public void setToolbarTitle(int resID) {
        mToolBar.setTitle("deneme..");
    }

}
