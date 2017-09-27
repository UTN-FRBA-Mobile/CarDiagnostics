package com.cardiag.activity;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cardiag.R;
import com.cardiag.models.solutions.TroubleCode;

import java.util.ArrayList;

/**
 * Created by leo on 22/09/17.
 */

class ErrorAdapter extends RecyclerView.Adapter<ErrorAdapter.ViewHolder> {

    private final TroubleCodesActivity context;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public Button mTextView;
        private TroubleCodesActivity context;

        public ViewHolder(View v) {
            super(v);
            mTextView = (Button) v.findViewById(R.id.tvError);
            mTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    context.select(getAdapterPosition());
                //    mTextView.setText(String.valueOf(getAdapterPosition()));
                }
            });
        }

        private void setTrouble(TroubleCode s) {
            mTextView.setText(s.toString());
        }

        public void setContext(TroubleCodesActivity context) {
            this.context = context;
        }
    }


    private final ArrayList<TroubleCode> troubles;

    public ErrorAdapter(TroubleCodesActivity myContext,ArrayList<TroubleCode> troubleCodes) {
        troubles=troubleCodes;
        context=myContext;
    }

    @Override
    public ErrorAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_trouble_code, parent, false);
        // set the view's size, margins, paddings and layout parameters
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        // params.setMargins(0,4,0,0);
        final ViewHolder vh = new ViewHolder(v);

        v.setLayoutParams(params);

        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.setTrouble(troubles.get(position));
        holder.setContext(context);
    }


    @Override
    public int getItemCount() {
        return troubles.size();
    }
}
