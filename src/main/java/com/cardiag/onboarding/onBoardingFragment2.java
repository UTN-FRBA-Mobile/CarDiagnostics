package com.cardiag.onboarding;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cardiag.R;

/**
 * Created by Matias on 11/10/2017.
 */

public class onBoardingFragment2 extends Fragment {

    @Nullable
    @Override


    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle s) {


        return inflater.inflate(
                R.layout.onboarding_screen2,
                container,
                false
        );


    }
}
