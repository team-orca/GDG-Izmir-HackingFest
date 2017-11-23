package com.example.orca.howbeautyiam;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

/**
 * Created by Admin on 5.05.2016.
 */

public class PagerAdapter extends FragmentPagerAdapter {
    List<Fragment> fragmentList;
    public PagerAdapter(android.support.v4.app.FragmentManager fm,List<Fragment> fragmentList)
    {
        super(fm);
        this.fragmentList=fragmentList;
    }
    @Override
    public Fragment getItem(int position) {
        return fragmentList.get(position);
    }

    @Override
    public int getCount() {
        return fragmentList.size();
    }
}
