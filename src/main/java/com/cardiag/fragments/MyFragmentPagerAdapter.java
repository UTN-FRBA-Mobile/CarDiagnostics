package com.cardiag.fragments;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.cardiag.R;
import com.cardiag.activity.GenericActivity;
import com.cardiag.activity.StepsActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lrocca on 13/06/2017.
 */
public class MyFragmentPagerAdapter extends FragmentPagerAdapter {
    List<Fragment> fragments;
    private View pagina1;

    public MyFragmentPagerAdapter(FragmentManager fm) {
        super(fm);
        this.fragments = new ArrayList<Fragment>();
    }

    /**
     * Add a new fragment in the list.
     *
     * @param fragment a new fragment
     */
    public void addFragment(Fragment fragment) {
        this.fragments.add(fragment);
    }

    @Override
    public Fragment getItem(int arg0) {
        return this.fragments.get(arg0);
    }

    @Override
    public int getCount() {
        return this.fragments.size();
    }

}
