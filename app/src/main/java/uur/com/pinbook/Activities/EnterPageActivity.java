package uur.com.pinbook.Activities;

import android.os.Build;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import uur.com.pinbook.JavaFiles.CustomPagerAdapter;
import uur.com.pinbook.JavaFiles.EnterPageDataModel;
import uur.com.pinbook.R;

public class EnterPageActivity extends AppCompatActivity {

    private LinearLayout dotsLayout;
    private TextView[] dots;

    CustomPagerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Making notification bar transparent
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }

        setContentView(R.layout.activity_enter_page);

        ViewPager enterViewPager = (ViewPager)findViewById(R.id.enterViewPager);
        dotsLayout = (LinearLayout) findViewById(R.id.layoutDots);

        adapter = new CustomPagerAdapter(this, EnterPageDataModel.getDataList());

        enterViewPager.setAdapter(adapter);

        addBottomDots(0);

        enterViewPager.addOnPageChangeListener(viewPagerPageChangeListener);
    }


    private void addBottomDots(int currentPage) {
        dots = new TextView[adapter.getCount()];

        int cActive = getResources().getColor(R.color.dot_dark_screen3, null);
        int cInactive = getResources().getColor(R.color.dot_light_screen3, null);

        dotsLayout.removeAllViews();

        for (int i = 0; i < dots.length; i++) {
            dots[i] = new TextView(this);
            dots[i].setText(Html.fromHtml("&#8226;"));
            dots[i].setTextSize(35);
            dots[i].setTextColor(cInactive);
            dotsLayout.addView(dots[i]);
        }

        if (dots.length > 0)
            dots[currentPage].setTextColor(cActive);

    }

    //  viewpager change listener
    ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageSelected(int position) {
            addBottomDots(position);

            // changing the next button text 'NEXT' / 'GOT IT'
            if (position == adapter.getCount() - 1) {
                // last page. make button text to GOT IT

            } else {
                // still pages are left

            }
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        @Override
        public void onPageScrollStateChanged(int arg0) {

        }
    };
}
