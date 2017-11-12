package com.cardiag.utils;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cardiag.R;
import com.cardiag.models.commands.ObdCommand;
import com.cardiag.velocimetro.Velocimetro;

import java.util.List;

/**
 * Created by Leo on 30/7/2017.
 */

public class ObdCommandAdapter extends RecyclerView.Adapter<ObdCommandAdapter.OBDCommandViewHolder> {

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
    public OBDCommandViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ObdCommandAdapter.OBDCommandViewHolder(LayoutInflater.from(mContext).inflate(R.layout.list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(OBDCommandViewHolder holder, int position) {

        TextView cmdName = holder.cmdName;
        TextView cmdValue = holder.cmdValue;
        velocimetro = holder.velocimetro;

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

    }

    @Override
    public int getItemCount() {
        return cmds.size();
    }

    public List<ObdCommand> getCmds() {
        return cmds;
    }

    public void setCmds(List<ObdCommand> cmds) {
        this.cmds = cmds;
    }

    static class OBDCommandViewHolder extends RecyclerView.ViewHolder {

        private TextView cmdName;
        private TextView cmdValue;
        private Velocimetro velocimetro;
        public OBDCommandViewHolder(View itemView) {
            super(itemView);

            cmdName = (TextView) itemView.findViewById(R.id.textview_cmd_name);
            cmdValue = (TextView) itemView.findViewById(R.id.textview_cmd_value);
            velocimetro = (Velocimetro) itemView.findViewById(R.id.velocimetro);
        }
    }
}

