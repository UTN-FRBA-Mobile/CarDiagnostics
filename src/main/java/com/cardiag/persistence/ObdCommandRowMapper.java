package com.cardiag.persistence;

import android.database.Cursor;

import com.cardiag.models.commands.ObdCommand;
import com.cardiag.models.config.ObdCommandSingleton;

import java.util.LinkedHashMap;

/**
 * Created by Leo on 4/8/2017.
 */

public class ObdCommandRowMapper {

    public static ObdCommand getCommand(Cursor c){
        ObdCommand cmd = null;
        ObdCommandSingleton ins = ObdCommandSingleton.getInstance();
        LinkedHashMap<String, Class<? extends ObdCommand>> map = ins.getMap();
        Class<? extends ObdCommand> cla = map.get(c.getString(0));

        try {
            cmd = cla.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            return null;
        }
        cmd.setName(c.getString(1));

        Boolean sel = false;
        if (c.getInt(2) == 1) {
            sel = true;
        }
        cmd.setSelected(sel);

        return cmd;
    }
}
