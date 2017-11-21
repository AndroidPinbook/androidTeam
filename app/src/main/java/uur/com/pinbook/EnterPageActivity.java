package uur.com.pinbook;

import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

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
