package com.cardiag.activity;

import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.cardiag.R;
import com.cardiag.fragments.MyFragmentPagerAdapter;
import com.cardiag.fragments.StepFragment;
import com.cardiag.models.commands.entities.Category;
import com.cardiag.models.solutions.Step;
import com.cardiag.persistence.DataBaseService;
import com.ogaclejapan.smarttablayout.SmartTabLayout;

import java.util.ArrayList;


public class StepsActivity extends AppCompatActivity {
    MyFragmentPagerAdapter pagerAdapter;
    ViewPager pager = null;
    private SmartTabLayout indicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_steps);

        // Instantiate a ViewPager
        this.pager = (ViewPager) this.findViewById(R.id.pager);

        // Create an adapter with the fragments we show on the ViewPager
        addFragments();

        //Floating Action Button
        FloatingActionButton btnFab = (FloatingActionButton) findViewById(R.id.backButton);
        btnFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Snackbar.make(view, "Esto es una prueba", Snackbar.LENGTH_LONG).show();
                onBackPressed();
            }
        });
        btnFab.setVisibility(View.GONE);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        indicator = (SmartTabLayout)findViewById(R.id.indicator);
        indicator.setViewPager(pager);
     //   this.addButtons();
    }

    private void addButtons() {
        CoordinatorLayout ll = (CoordinatorLayout)findViewById(R.id.activity_steps);
        Button b = new Button(this);
        b.setText("Omitir");

        RelativeLayout.LayoutParams lp2 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
      //  lp2.addRule(RelativeLayout.CENTER_VERTICAL);
      //  lp2.addRule(RelativeLayout.CENTER_HORIZONTAL);
//        b.setId(900000);
        lp2.addRule(RelativeLayout.ALIGN_PARENT_END);
        lp2.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        b.setLayoutParams(lp2);
        ll.addView(b);

    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
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
