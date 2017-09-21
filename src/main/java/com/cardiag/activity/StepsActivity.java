package com.cardiag.activity;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.cardiag.R;
import com.cardiag.fragments.MyFragmentPagerAdapter;
import com.cardiag.fragments.StepFragment;
import com.cardiag.models.solutions.Step;
import com.cardiag.persistence.DataBaseService;

import java.util.ArrayList;


public class StepsActivity extends AppCompatActivity {
    MyFragmentPagerAdapter pagerAdapter;
    ViewPager pager = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_steps);

        // Instantiate a ViewPager
        this.pager = (ViewPager) this.findViewById(R.id.pager);

        // Create an adapter with the fragments we show on the ViewPager
        addFragments();
      //  addFrag2();
        //this.removeFragment();
    }
   private void addFragments() {
        MyFragmentPagerAdapter adapter = new MyFragmentPagerAdapter(
               getSupportFragmentManager());
       int id = 1;

       ArrayList<Step> steps = new DataBaseService(this).getSteps(id);
       int size = steps.size();
       for(int index = 0; index< size; index++){
           adapter.addFragment(StepFragment.newInstance(steps.get(index), index+1,size,this));
       }
        this.pager.setAdapter(adapter);

      //  this.pager.setPageTransformer(true, new ZoomOutPageTransformer());
    }

/*    private void addFragments() {
        MyFragmentPagerAdapter adapter = new MyFragmentPagerAdapter(
                getSupportFragmentManager());
        Step step = new Step();
        adapter.addFragment(StepFragment.newInstance(step, 0,this));
        adapter.addFragment(StepFragment.newInstance(step, 1,this));
        this.pager.setAdapter(adapter);

        this.pager.setPageTransformer(true, new ZoomOutPageTransformer());
    }*/
    private void removeFragment() {

    }

}
