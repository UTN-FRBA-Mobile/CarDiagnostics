package com.cardiag.fragments;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.res.ResourcesCompat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.cardiag.R;
import com.cardiag.activity.StepsActivity;
import com.cardiag.models.solutions.Step;

/**
 * Created by lrocca on 13/06/2017.
 */
public class StepFragment extends Fragment {
    private static final String INDEX = "index";
    private static final String IMGID = "-1";
    private static final String DESC = "--";
    private static final String TOTAL = "0";

    private static Activity activ;
    //   private OnFragmentInteractionListener mListener;
    private int index;
    private String imgid;
    private String desc;
    private int total;


    public StepFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        //  super.onActivityCreated(savedInstanceState);
        super.onCreate(savedInstanceState);
        // Load parameters when the initial creation of the fragment is done
        this.index = (getArguments() != null) ? getArguments().getInt(INDEX)
                : -1;
        this.imgid = (getArguments() != null) ? getArguments().getString(IMGID)
                :"";
        this.desc = (getArguments() != null) ? getArguments().getString(DESC)
                :"";
        this.total = (getArguments() != null) ? getArguments().getInt(TOTAL)
                : -1;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.fragment_step, container, false);

        // Show the current page index in the view
        TextView tvIndex = (TextView) rootView.findViewById(R.id.tvIndex);
        ImageView img = (ImageView) rootView.findViewById(R.id.imageView);
        tvIndex.setText(desc);

        TextView tvStepPosition = (TextView) rootView.findViewById(R.id.tvStep);
        tvStepPosition.setText("Paso "+ String.valueOf(index) + " de "+ String.valueOf(total));
        tvStepPosition.setGravity(Gravity.CENTER);

        int intDrawable= getResourceId(imgid, "drawable", activ.getPackageName());
        // int intDrawable=getResId("back0", Drawable.class);
    //    int intDrawable= getResourceId("back"+String.valueOf(index), "drawable", activ.getPackageName()); //va
      //  if(intDrawable==-1)intDrawable= R.drawable.back0;                             ----va
 //       int intDrawable= getResourceId("ic_btcar", "drawable-xhdpi", activ.getPackageName()); // no va

        Resources res = rootView.getResources();
        Drawable f = null;
        // f = ResourcesCompat.getDrawable(rootView.getResources(),R.drawable.back0, null);
        f = ResourcesCompat.getDrawable(rootView.getResources(),intDrawable, null);
        //     tvIndex.setBackgroundDrawable(f);
        img.setImageDrawable(f);                           //    ----va
        return rootView;
        //return inflater.inflate(R.layout.fragment_screen_slide_page, container, false);
    }

    public int getResourceId(String pVariableName, String pResourcename, String pPackageName)
    {
        try {
            return getResources().getIdentifier(pVariableName, pResourcename, pPackageName);
        } catch (Exception e) {
            //   e.printStackTrace();
            return -1;
        }
    }

    public static StepFragment newInstance(Step step, int index, int size, StepsActivity stepsActivity) {
        activ = stepsActivity;
        // Instantiate a new fragment
        StepFragment fragment = new StepFragment();
        // Save the parameters
        Bundle bundle = new Bundle();
        bundle.putInt(INDEX, index);
        bundle.putInt(TOTAL, size);
        bundle.putString(IMGID,step.getImgId());
        bundle.putString(DESC,step.getDesc());
        fragment.setArguments(bundle);
        fragment.setRetainInstance(true);

        return fragment;
    }
    public void goBack(View v){

    }
}
