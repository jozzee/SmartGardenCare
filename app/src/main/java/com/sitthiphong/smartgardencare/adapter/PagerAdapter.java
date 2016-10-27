package com.sitthiphong.smartgardencare.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sitthiphong on 10/16/2016 AD.
 */

public class PagerAdapter extends FragmentPagerAdapter {

    private final List<Fragment> fragmentList = new ArrayList<>();
    private final List<String> fragmentTitleList = new ArrayList<>();

    public PagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {

        return fragmentList.get(position);
    }

    @Override
    public int getCount() {
        return fragmentList.size();
    }
    @Override
    public CharSequence getPageTitle(int position) {
        return "";

    }
    public CharSequence getPageTitleCustom(int position){
        return fragmentTitleList.get(position);
    }
    public void addFragment(Fragment fragment, String title) {
        fragmentList.add(fragment);
        fragmentTitleList.add(title);
    }
}
