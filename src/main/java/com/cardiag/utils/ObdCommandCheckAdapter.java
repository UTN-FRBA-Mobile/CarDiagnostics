package com.cardiag.utils;

import android.content.Context;
import android.graphics.Color;
import android.text.BoringLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CheckedTextView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.cardiag.R;
import com.cardiag.activity.StateActivity;
import com.cardiag.models.commands.ObdCommand;
import com.cardiag.persistence.DataBaseService;
import com.cardiag.persistence.ObdCommandContract;

import java.util.ArrayList;

/**
 * Created by Leo on 30/7/2017.
 */

public class ObdCommandCheckAdapter extends BaseAdapter {

    ArrayList<ObdCommand> cmds;
    ArrayList<ObdCommand> selectedCmds = new ArrayList<ObdCommand>();
    private Context mContext;
    private DataBaseService db;
    StateActivity sta;

      public ObdCommandCheckAdapter(Context context) {
        sta = (StateActivity) context;
        this.mContext = sta;
        this.db = sta.getDbService();

        String where = ObdCommandContract.CommandEntry.AVAILABILITY+"=?";
        String[] values = new String[]{"1"};
        this.cmds = db.getCommands(where, values);
//          this.cmds = db.getCommands(null, null);

          for (ObdCommand cmd: cmds) {
              if (cmd.getSelected()) {
                  selectedCmds.add(cmd);
              }
          }
      }

    @Override
    public int getCount() {
        return cmds.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {

        final LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        convertView = layoutInflater.inflate(R.layout.list_check_item, null);

        final ObdCommand cmd = cmds.get(position);
        final CheckedTextView checkedTextView = (CheckedTextView) convertView.findViewById(R.id.selected_command);

        if (cmd.getSelected()) {
            checkedTextView.setChecked(cmd.getSelected());
            checkedTextView.setCheckMarkDrawable(android.R.drawable.checkbox_on_background);
        }

        checkedTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CheckedTextView checkedTextView1 = (CheckedTextView) view;
                if (cmd.getSelected()) {
                    checkedTextView1.setCheckMarkDrawable(android.R.drawable.checkbox_off_background);
                    cmd.setSelected(false);
                    selectedCmds.remove(cmd);
                } else {
                    cmd.setSelected(true);
                    checkedTextView1.setCheckMarkDrawable(android.R.drawable.checkbox_on_background);
                    selectedCmds.add(cmd);
                }
            }
        });

        checkedTextView.setText(cmd.getName());

        return convertView;
    }

    @Override
    public void notifyDataSetChanged() {
       super.notifyDataSetChanged();
    }

    public ArrayList<ObdCommand> getSelectedCmds() {
        return selectedCmds;
    }

    public ArrayList<ObdCommand> getCmds() {
        return cmds;
    }
}

