package com.tti.unilagmba;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * Created by Scarecrow on 3/9/2018.
 */

public class TabsPager extends FragmentStatePagerAdapter {

    String[] titles = new String[]{"", "", "", "", ""};

    public TabsPager(FragmentManager fm) {
        super(fm);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titles[position];
    }

    @Override
    public Fragment getItem(int position) {

        switch (position){
            case 0:
                News news = new News();
                return news;
            case 1:
                Materials materials = new Materials();
                return materials;
            case 2:
                Specializations specializations = new Specializations();
                return specializations;
            case 3:
                TimeTable timeTable = new TimeTable();
                return timeTable;
            case 4:
                Chat chat = new Chat();
                return chat;
        }

        return null;
    }

    @Override
    public int getCount() {
        return 5;
    }
}
