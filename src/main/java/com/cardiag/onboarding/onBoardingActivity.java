package com.cardiag.onboarding;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.Button;

import com.cardiag.R;
import com.cardiag.activity.MainActivity;
import com.ogaclejapan.smarttablayout.SmartTabLayout;

/**
 * Created by Matias on 11/10/2017.
 */

public class onBoardingActivity  extends FragmentActivity {

    private ViewPager pager;
    private SmartTabLayout indicator;
    private Button saltar;
    private Button siguiente;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);

        pager = (ViewPager)findViewById(R.id.pager);
        indicator = (SmartTabLayout)findViewById(R.id.indicator);
        siguiente = (Button)findViewById(R.id.siguiente);
        saltar = (Button)findViewById(R.id.saltar);

        FragmentStatePagerAdapter adapter = new FragmentStatePagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                switch (position) {
                    case 0 : return new onBoardingFragment1();
                    case 1 : return new onBoardingFragment2();
                    case 2 : return new onBoardingFragment3();
                    case 3 : return new onBoardingFragment4();
                    default: return null;
                }
            }

            @Override
            public int getCount() {
                return 4;
            }
        };
        pager.setAdapter(adapter);
        indicator.setViewPager(pager);

        saltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishOnboarding();
            }
        });

        siguiente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(pager.getCurrentItem() == 3) { // The last screen
                    finishOnboarding();
                } else {
                    pager.setCurrentItem(
                            pager.getCurrentItem() + 1,
                            true
                    );
                }
            }
        });
        indicator.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                if(position == 3){
                    saltar.setVisibility(View.GONE);
                    siguiente.setText("Fin");
                } else {
                    saltar.setVisibility(View.VISIBLE);
                    siguiente.setText("Siguiente");
                }
            }
        });
    }
    private void finishOnboarding() {
        // Get the shared preferences
        SharedPreferences preferences =
                getSharedPreferences("my_preferences", MODE_PRIVATE);

        // Set onboarding_complete to true
        preferences.edit()
                .putBoolean("onboarding_complete",true).apply();

        // Launch the main Activity, called MainActivity
        Intent main = new Intent(this, MainActivity.class);
        startActivity(main);

        // Close the OnboardingActivity
        finish();
    }
}
