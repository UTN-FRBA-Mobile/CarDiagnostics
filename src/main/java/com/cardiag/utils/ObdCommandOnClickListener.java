package com.cardiag.utils;

import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckedTextView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.cardiag.R;
import com.cardiag.models.commands.ObdCommand;

/**
 * Created by Leo on 12/8/2017.
 */

public class ObdCommandOnClickListener implements AdapterView.OnItemClickListener {

    private ObdCommandCheckAdapter adapter;

    public ObdCommandOnClickListener(ObdCommandCheckAdapter adapter) {
        this.adapter = adapter;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        ListView lv = (ListView) adapterView;
        LinearLayout lchild = (LinearLayout) lv.getChildAt(i);
//        CheckedTextView checkedTextView1 = (CheckedTextView) lchild.getChildAt(2);
        CheckedTextView checkedTextView1 = (CheckedTextView) adapterView.findViewById(R.id.selected_command);
        ObdCommand cmd = adapter.getCmds().get(i);

        if (cmd.getSelected()) {
            checkedTextView1.setCheckMarkDrawable(android.R.drawable.checkbox_off_background);
            cmd.setSelected(false);
            adapter.getSelectedCmds().remove(cmd);
        } else {
            cmd.setSelected(true);
            checkedTextView1.setCheckMarkDrawable(android.R.drawable.checkbox_on_background);
            adapter.getSelectedCmds().add(cmd);
        }
    }
}
