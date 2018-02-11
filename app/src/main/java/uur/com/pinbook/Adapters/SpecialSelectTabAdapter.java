package uur.com.pinbook.Adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;

import java.util.ArrayList;

import uur.com.pinbook.SpecialFragments.PersonFragment;

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

    /*@Override
    public int getItemPosition(Object object) {

        Log.i("Info", "object:" + object);
        int position = 0;

        if (position >= 0) {
            return position;
        } else {
            return POSITION_NONE;
        }
    }*/

    public void updateFragment(int position, Fragment fragment){
        mFragmentList.set(position, fragment);
    }

    public void updateFragmentTitle(int position, String title){
        mFragmentListTitles.set(position, title);
    }
}
