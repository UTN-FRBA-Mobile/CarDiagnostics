package com.cardiag.utils;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.cardiag.R;
import com.cardiag.models.commands.ObdCommand;
import com.cardiag.velocimetro.Velocimetro;

import java.util.List;

/**
 * Created by Leo on 30/7/2017.
 */

public class ObdCommandAdapter extends BaseAdapter {

    private List<ObdCommand> cmds;
    private Context mContext;
    private Velocimetro velocimetro;
    double velocidadMaxima;
    double velocidadActual;
    boolean esRPM;
    /**
     * Default constructor
     * @param items to fill data to
     */
    public ObdCommandAdapter(final List<ObdCommand> items, Context context) {
        this.cmds = items;
        this.mContext = context;
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

        if (convertView == null) {
            final LayoutInflater layoutInflater = LayoutInflater.from(mContext);
            convertView = layoutInflater.inflate(R.layout.list_item, null);
        }

        TextView cmdName = (TextView) convertView.findViewById(R.id.textview_cmd_name);
        TextView cmdValue = (TextView) convertView.findViewById(R.id.textview_cmd_value);
        velocimetro = (Velocimetro) convertView.findViewById(R.id.velocimetro);

        ObdCommand cmd = cmds.get(position);
        cmdName.setText(cmd.getName() + ": ");
        cmdValue.setText(cmd.getFormattedResult());
        cmdValue.setTextColor(Color.BLACK);
        if (cmd.getError()) {
            cmdValue.setTextColor(Color.RED);
        }

        velocidadMaxima = (double) velocimetro.getMaxSpeed();
        velocidadActual =  Double.parseDouble(cmd.getCalculatedResult());


        cmd.setVelocimetroProperties(velocimetro, velocidadActual);

        velocimetro.setLabelConverter(new Velocimetro.LabelConverter() {
            @Override
            public String getLabelFor(double progress, double maxProgress) {
                return String.valueOf((int) Math.round(progress));
            }
        });



        return convertView;
    }

    @Override
    public void notifyDataSetChanged() {
       super.notifyDataSetChanged();
    }

    public List<ObdCommand> getCmds() {
        return cmds;
    }

    public void setCmds(List<ObdCommand> cmds) {
        this.cmds = cmds;
    }
}

