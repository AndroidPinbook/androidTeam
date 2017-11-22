package uur.com.pinbook.Activities;

import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import uur.com.pinbook.JavaFiles.CustomPagerAdapter;
import uur.com.pinbook.JavaFiles.EnterPageDataModel;
import uur.com.pinbook.R;

public class EnterPageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_page);

        ViewPager enterViewPager = (ViewPager)findViewById(R.id.enterViewPager);

        CustomPagerAdapter adapter = new CustomPagerAdapter(this, EnterPageDataModel.getDataList());

        enterViewPager.setAdapter(adapter);

    }
}
