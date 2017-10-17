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
import com.cardiag.models.solutions.Solution;
import com.cardiag.models.solutions.Step;
import com.cardiag.persistence.DataBaseService;
import com.ogaclejapan.smarttablayout.SmartTabLayout;

import java.util.ArrayList;

import static com.cardiag.R.id.saltar;
import static com.cardiag.R.id.siguiente;


public class StepsActivity extends AppCompatActivity {
    MyFragmentPagerAdapter pagerAdapter;
    ViewPager pager = null;
    private SmartTabLayout indicator;
    private int size =-1;
    private int idSolution;
    private Button btnEnd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_steps);


        Bundle bundle = getIntent().getExtras();
        Solution dato= (Solution) bundle.get("solution");
        idSolution = (int) dato.getId();

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

        initaliceNavigation();

        if(idSolution==10) addSkipButton();
        addEndButton();
    //    this.addButtons();
    }

    private void initaliceNavigation() {
        final FloatingActionButton btnFabLeft = (FloatingActionButton) findViewById(R.id.swipe_left);
        final FloatingActionButton btnFabRight = (FloatingActionButton) findViewById(R.id.swipe_right);

        btnFabLeft.setVisibility(View.INVISIBLE);
        btnFabRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pager.setCurrentItem(
                        pager.getCurrentItem() + 1,
                        true
                );
            }
        });
        btnFabLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pager.setCurrentItem(
                        pager.getCurrentItem() - 1,
                        true
                );
            }
        });

        indicator.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                if(position == 0){
                    btnFabLeft.setVisibility(View.INVISIBLE);
                    return;
                }
                if(position == size-1){
                    btnFabRight.setVisibility(View.INVISIBLE);
                    btnEnd.setVisibility(View.VISIBLE);
                }
                else {
                    btnFabLeft.setVisibility(View.VISIBLE);
                    btnFabRight.setVisibility(View.VISIBLE);
                    btnEnd.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    private void addSkipButton() {
        CoordinatorLayout ll = (CoordinatorLayout)findViewById(R.id.activity_steps);
        Button b = new Button(this);
        b.setText("Omitir");

        CoordinatorLayout.LayoutParams lp2 = new CoordinatorLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        lp2.gravity = Gravity.END;
        b.setLayoutParams(lp2);
        ll.addView(b);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

    }

    private void addEndButton() {
        CoordinatorLayout ll = (CoordinatorLayout)findViewById(R.id.activity_steps);
        btnEnd = new Button(this);
        btnEnd.setText(R.string.end);

        CoordinatorLayout.LayoutParams lp2 = new CoordinatorLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        lp2.gravity = Gravity.BOTTOM;
        btnEnd.setLayoutParams(lp2);
        ll.addView(btnEnd);
        btnEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        btnEnd.setVisibility(View.INVISIBLE);
    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }

   private void addFragments() {
        MyFragmentPagerAdapter adapter = new MyFragmentPagerAdapter(
               getSupportFragmentManager());

       ArrayList<Step> steps = new DataBaseService(this).getSteps(idSolution);
       size = steps.size();
       for(int index = 0; index< size; index++){
           adapter.addFragment(StepFragment.newInstance(steps.get(index), index+1,size,this));
       }
        this.pager.setAdapter(adapter);

      //  this.pager.setPageTransformer(true, new ZoomOutPageTransformer());
    }

    private void removeFragment() {

    }

}
