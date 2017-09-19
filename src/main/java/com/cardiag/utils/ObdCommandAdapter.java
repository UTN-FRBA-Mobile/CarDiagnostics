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


        velocimetro.setUnitsTextSize(40);

        switch(cmd.getName()){
            case "RPM del motor":
                velocimetro.setMaxSpeed(11);
                velocimetro.setMajorTickStep(1);
                velocimetro.setMinorTicks(1);
                velocimetro.clearColoredRanges();
                velocimetro.addColoredRange(0, 4, Color.GREEN);
                velocimetro.addColoredRange(4, 7, Color.YELLOW);
                velocimetro.addColoredRange(7, 11, Color.RED);
                velocimetro.setUnitsTextSize(27);
                velocimetro.setUnitsText(cmd.getResultUnit() + " x1000");

                break;

            //TODOS LOS COMANDOS CON DE PORCENTAJES 0-100%

            case "Carga calculada del motor":
            case "Ajuste de combustible a largo plazo—Banco 1":
            case "Ajuste de combustible a corto plazo—Banco 1":
            case "Ajuste de combustible a corto plazo—Banco 2":
            case "Ajuste de combustible a largo plazo—Banco 2":
            case "Avance del tiempo":
            case "Posición del acelerador":
            case "Nivel de combustible":
            case "Valor absoluto de carga":
                velocimetro.setMaxSpeed(100);
                velocimetro.setMajorTickStep(20);
                velocimetro.setMinorTicks(3);
                velocimetro.clearColoredRanges();
                velocimetro.addColoredRange(0, 30, Color.GREEN);
                velocimetro.addColoredRange(30, 70, Color.YELLOW);
                velocimetro.addColoredRange(70, 100, Color.RED);
                velocimetro.setUnitsText(cmd.getResultUnit());

                break;
           default:
                velocimetro.setMaxSpeed(200);
                velocimetro.setMajorTickStep(30);
                velocimetro.setMinorTicks(2);
                velocimetro.clearColoredRanges();
                velocimetro.addColoredRange(0, 60, Color.GREEN);
                velocimetro.addColoredRange(60, 120, Color.YELLOW);
                velocimetro.addColoredRange(120, 200, Color.RED);
                velocimetro.setUnitsText(cmd.getResultUnit());

               break;
        }

        velocimetro.setLabelConverter(new Velocimetro.LabelConverter() {
            @Override
            public String getLabelFor(double progress, double maxProgress) {
                return String.valueOf((int) Math.round(progress));
            }
        });

        velocidadMaxima = (double) velocimetro.getMaxSpeed();
        if( Double.parseDouble(cmd.getCalculatedResult()) < 0 ){
            velocimetro.setSpeed(0, 1000, 300);
        }
        if(Double.parseDouble(cmd.getCalculatedResult()) > velocidadMaxima){
                velocimetro.setSpeed(velocimetro.getMaxSpeed(), 1000, 300);
            }
        if(( Double.parseDouble(cmd.getCalculatedResult()) >= 0 ) && (Double.parseDouble(cmd.getCalculatedResult()) <= velocidadMaxima) && (!cmd.getName().equals("RPM del motor"))){
            velocimetro.setSpeed(Double.parseDouble(cmd.getCalculatedResult()), 1000, 300);
        }
        if(( Double.parseDouble(cmd.getCalculatedResult()) >= 0 ) && (Double.parseDouble(cmd.getCalculatedResult()) <= velocidadMaxima) && (cmd.getName().equals("RPM del motor"))){
            velocimetro.setSpeed(Double.parseDouble(cmd.getCalculatedResult())/1000, 1000, 300);
        }

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

