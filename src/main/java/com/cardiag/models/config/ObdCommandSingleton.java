package com.cardiag.models.config;

import com.cardiag.models.commands.ObdCommand;
import com.cardiag.models.commands.SpeedCommand;
import com.cardiag.models.commands.control.DistanceMILOnCommand;
import com.cardiag.models.commands.control.DtcNumberCommand;
import com.cardiag.models.commands.control.ModuleVoltageCommand;
import com.cardiag.models.commands.control.TimingAdvanceCommand;
import com.cardiag.models.commands.control.TroubleCodesCommand;
import com.cardiag.models.commands.control.VinCommand;
import com.cardiag.models.commands.engine.AbsoluteLoadCommand;
import com.cardiag.models.commands.engine.LoadCommand;
import com.cardiag.models.commands.engine.MassAirFlowCommand;
import com.cardiag.models.commands.engine.OilTempCommand;
import com.cardiag.models.commands.engine.RPMCommand;
import com.cardiag.models.commands.engine.RuntimeCommand;
import com.cardiag.models.commands.engine.ThrottlePositionCommand;
import com.cardiag.models.commands.fuel.AirFuelRatioCommand;
import com.cardiag.models.commands.fuel.ConsumptionRateCommand;
import com.cardiag.models.commands.fuel.FindFuelTypeCommand;
import com.cardiag.models.commands.fuel.FuelLevelCommand;
import com.cardiag.models.commands.fuel.FuelTrimLTB1;
import com.cardiag.models.commands.fuel.FuelTrimLTB2;
import com.cardiag.models.commands.fuel.FuelTrimSTB1;
import com.cardiag.models.commands.fuel.FuelTrimSTB2;
import com.cardiag.models.commands.fuel.WidebandAirFuelRatioCommand;
import com.cardiag.models.commands.pressure.BarometricPressureCommand;
import com.cardiag.models.commands.pressure.FuelPressureCommand;
import com.cardiag.models.commands.pressure.FuelRailPressureCommand;
import com.cardiag.models.commands.pressure.IntakeManifoldPressureCommand;
import com.cardiag.models.commands.temperature.AirIntakeTemperatureCommand;
import com.cardiag.models.commands.temperature.AmbientAirTemperatureCommand;
import com.cardiag.models.commands.temperature.EngineCoolantTemperatureCommand;

import java.util.ArrayList;
import java.util.LinkedHashMap;

/**
 * TODO put description
 */
public final class ObdCommandSingleton {

    private ArrayList<ObdCommand> cmds = new ArrayList<>();
    private LinkedHashMap<String, Class<? extends ObdCommand>> map = new LinkedHashMap<String, Class<? extends ObdCommand>>();
    private static ObdCommandSingleton instance;
    public static final Integer WAIT_TIME = 100;
    public static final Long RESPONSE_DELAY = 0L;
    public static final Integer TIME_OUT = 2; // In secs

    private ObdCommandSingleton() {
        setCommands();
        setMapper();
    }

    public static ObdCommandSingleton getInstance() {

        if (instance == null) {
            instance = new ObdCommandSingleton();
        }

        return instance;
    }

    private void setCommands() {

        // Control
        cmds.add(new ModuleVoltageCommand());
//        cmds.add(new EquivalentRatioCommand());
        cmds.add(new DistanceMILOnCommand());
        cmds.add(new DtcNumberCommand());
        cmds.add(new TimingAdvanceCommand());
        cmds.add(new TroubleCodesCommand());
        cmds.add(new VinCommand());
        cmds.add(new AbsoluteLoadCommand());

        // Engine
        cmds.add(new LoadCommand());
        cmds.add(new RPMCommand());
        cmds.add(new RuntimeCommand());
        cmds.add(new MassAirFlowCommand());
        cmds.add(new ThrottlePositionCommand());
        cmds.add(new OilTempCommand());

        // Fuel
        cmds.add(new FindFuelTypeCommand());
        cmds.add(new ConsumptionRateCommand());
        cmds.add(new FuelLevelCommand());
        cmds.add(new FuelTrimLTB1());
        cmds.add(new FuelTrimLTB2());
        cmds.add(new FuelTrimSTB1());
        cmds.add(new FuelTrimSTB2());

        cmds.add(new AirFuelRatioCommand());
        cmds.add(new WidebandAirFuelRatioCommand());
        cmds.add(new OilTempCommand());

        // Pressure
        cmds.add(new BarometricPressureCommand());
        cmds.add(new FuelPressureCommand());
        cmds.add(new FuelRailPressureCommand());
        cmds.add(new IntakeManifoldPressureCommand());

        // Temperature
        cmds.add(new AirIntakeTemperatureCommand());
        cmds.add(new AmbientAirTemperatureCommand());
        cmds.add(new EngineCoolantTemperatureCommand());

        // Misc
        cmds.add(new SpeedCommand());

    }

    public void setMapper() {

        for (ObdCommand cmd: cmds) {
            map.put(cmd.getCmd(),cmd.getClass());
        }

    }

    public ArrayList<ObdCommand> getCommands (){
        return cmds;
    }

    public LinkedHashMap<String, Class<? extends ObdCommand>> getMap() {
        return map;
    }
}
