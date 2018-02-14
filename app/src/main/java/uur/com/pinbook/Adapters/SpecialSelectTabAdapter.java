package uur.com.pinbook.Adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;

import uur.com.pinbook.SpecialFragments.PersonFragment;
import uur.com.pinbook.utils.Utils;

public class SpecialSelectTabAdapter extends FragmentStatePagerAdapter{

    private ArrayList<Fragment> mFragmentList = new ArrayList<>();
    private ArrayList<String> mFragmentListTitles = new ArrayList<>();

    public SpecialSelectTabAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return mFragmentList.get(position);
    }

    @Override
    public int getCount() {
        return mFragmentList.size();
    }

    public void addFragment(Fragment fragment, String title){
        mFragmentList.add(fragment);
        mFragmentListTitles.add(title);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mFragmentListTitles.get(position);
    }

    public void updateFragment(int position, Fragment fragment){
        mFragmentList.set(position, fragment);
    }

    @Override
    public int getItemPosition(Object object) {
        // POSITION_NONE makes it possible to reload the PagerAdapter
        return POSITION_NONE;
    }

    public void updateFragmentTitle(int position, String title){
        mFragmentListTitles.set(position, title);
    }
}
