package com.cardiag.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.GridView;
import android.widget.Toast;

import com.cardiag.R;
import com.cardiag.models.commands.ObdCommand;
import com.cardiag.models.runnable.ConnectionConfigTask;
import com.cardiag.models.runnable.StateTask;
import com.cardiag.persistence.DataBaseService;
import com.cardiag.utils.ConfirmDialog;
import com.cardiag.utils.ObdCommandAdapter;
import com.cardiag.utils.ObdCommandCheckAdapter;

import java.util.ArrayList;
import java.util.List;

public class  StateActivity extends AppCompatActivity  {

    private static final String TAG = StateActivity.class.getName();
    private static final int START_LIVE_DATA = 0;
    private static final int STOP_LIVE_DATA = 1;
    private static final int SELECT_COMMANDS = 2;
    private static final int NO_ORIENTATION_SENSOR = 8;
    private static final int REQUEST_ENABLE_BT = 1234;
    private static Boolean bluetoothDefaultIsEnable = false;
    private GridView gridView;
    private SensorManager sensorManager;
    private PowerManager.WakeLock wakeLock;
//    private SharedPreferences prefs;
    private ArrayList<Boolean> availablePidsFlags = new ArrayList<Boolean>();
    private BluetoothSocket sock = null;
    private ArrayList<ObdCommand> commands = new ArrayList<ObdCommand>();
    private List<ObdCommand> selectedCommands = new ArrayList<ObdCommand>();
    StateTask stateTask;
    ConnectionConfigTask cct;
    private Sensor orientSensor = null;
    private DataBaseService dbService;
    private Menu menu;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.car_state);
        doBindings();
        dbService = new DataBaseService(this);

        final BluetoothAdapter btAdapter = getBluetoothAdapter();

//        initiateConfiguration();

        if (btAdapter != null && btAdapter.isEnabled()) {
            bluetoothDefaultIsEnable = btAdapter.isEnabled();
            initiateConfiguration();
        } else {
            String error = getString(R.string.error);
            String msg = getString(R.string.text_bluetooth_disabled);
            ConfirmDialog.showCancellingDialog(this, error, msg);
        }

    }

    private void initiateConfiguration() {

        // get Orientation sensor
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        List<Sensor> sensors = sensorManager.getSensorList(Sensor.TYPE_ORIENTATION);
        if (sensors.size() > 0) {
            orientSensor = sensors.get(0);
        } else {
            showDialog(NO_ORIENTATION_SENSOR);
        }

        cct = new ConnectionConfigTask(this);
        cct.execute();

//        selectedCommands = dbService.getCommands(null, null);
//        gridView.setAdapter(new ObdCommandAdapter(selectedCommands, this));
    }

    private void doBindings() {
    //TODO Para cuando agreguemos la vista en serio.
        gridView = (GridView) findViewById(R.id.data_grid);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (sock != null ){
            sock = null;
        }

        if ( cct != null && !cct.isCancelled() ) {
            cct.cancel(true);
        }

        if ( stateTask != null && !stateTask.isCancelled() ) {
            stateTask.cancel(true);
        }

    }

    public void stateUpdate() {

//        ObdCommandAdapter adapter = new ObdCommandAdapter(selectedCommands, this);
//        gridView.setAdapter(adapter);
//
        ObdCommandAdapter adapter = (ObdCommandAdapter) gridView.getAdapter();
        adapter.notifyDataSetChanged();
    }

    public void multiStateUpdate() {
        ObdCommandAdapter adapter = new ObdCommandAdapter(selectedCommands, this);
        gridView.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.state,menu);
        this.menu = menu;
        menu.add(0, START_LIVE_DATA, 0, getString(R.string.menu_start_live_data));
        menu.add(0, STOP_LIVE_DATA, 0, getString(R.string.menu_stop_live_data));
        menu.add(0, SELECT_COMMANDS, 0, getString(R.string.select_commands));

        menu.getItem(STOP_LIVE_DATA).setEnabled(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case START_LIVE_DATA:
                menu.getItem(STOP_LIVE_DATA).setEnabled(true);
                menu.getItem(START_LIVE_DATA).setEnabled(false);
                startLiveData();
                return true;
            case STOP_LIVE_DATA:
                menu.getItem(STOP_LIVE_DATA).setEnabled(false);
                menu.getItem(START_LIVE_DATA).setEnabled(true);
                stopLiveData();
                return true;
            case SELECT_COMMANDS:
                AlertDialog dialog = getCheckBoxDialog();
                dialog.show();
                return true;
        }
        return false;
    }

    private void startLiveData() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        showToast(getString(R.string.starting_live_data));

        if (stateTask != null && !stateTask.isCancelled()) {
            stateTask.cancel(true);
            stateTask = null;
        }

        stateTask = new StateTask(this);
        stateTask.execute();
    }

    private void stopLiveData() {
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        if (stateTask != null) {
            stateTask.cancel(true);
            stateTask = null;
        }
        showToast(getString(R.string.live_data_stopped));
    }

    public void showToast(String msg) {
        Toast.makeText(getApplicationContext(),msg, Toast.LENGTH_LONG).show();
    }

    public BluetoothAdapter getBluetoothAdapter() {
        return BluetoothAdapter.getDefaultAdapter();
    }

    //This method runs when a startActivityForResult finishes
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode == Activity.RESULT_OK) {
             } else {
                Toast.makeText(this, R.string.text_bluetooth_disabled, Toast.LENGTH_LONG).show();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public AlertDialog getCheckBoxDialog(){

        String accept = getString(R.string.accept);
        String cancel = getString(R.string.cancel);
        final StateActivity sta = this;
        LayoutInflater inflater = getLayoutInflater();
        View title = inflater.inflate(R.layout.select_command_title, null);

        final ObdCommandCheckAdapter checkAdapter = new ObdCommandCheckAdapter(this);
        RecyclerView recyclerView = (RecyclerView) inflater.inflate(R.layout.custom_list, null);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(checkAdapter);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setCustomTitle(title)
                .setView(recyclerView)
                .setPositiveButton(accept, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        onPositiveClick(checkAdapter);
                    }
                })
                .setNegativeButton(cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        checkAdapter.getSelectedCmds().clear();
                        dialog.cancel();
                    }
                })
                .create();
       return dialog;
    }

    private void onPositiveClick(ObdCommandCheckAdapter checkAdapter) {
        dbService.resetSelection();
        ArrayList<ObdCommand> adapterSelected = checkAdapter.getSelectedCmds();
        ArrayList<ObdCommand> auxSelected = new ArrayList<ObdCommand>();

        for (ObdCommand cmd : adapterSelected) {
            dbService.updateCommand(cmd, null);
        }

        for (ObdCommand cmd : selectedCommands) {
            if (contains(cmd, adapterSelected)) {
                auxSelected.add(cmd);
            }
        }

        for (ObdCommand cmd : adapterSelected) {
            if (!contains(cmd, selectedCommands)) {
                auxSelected.add(cmd);
            }
        }

        if (stateTask != null && stateTask.getStatus() == AsyncTask.Status.RUNNING) {
            stateTask.cancel(true);
            selectedCommands.clear();
            selectedCommands.addAll(auxSelected);
            stateUpdate();
            stateTask = new StateTask(this);
            stateTask.execute();
        } else {
            selectedCommands.clear();
            selectedCommands.addAll(auxSelected);
            stateUpdate();
        }
    }

    public BluetoothSocket getSock() {
        return sock;
    }

    public void setSock(BluetoothSocket sock) {
        this.sock = sock;
    }

    public ArrayList<ObdCommand> getCommands() {
        return commands;
    }

    public void setCommands(ArrayList<ObdCommand> commands) {
        this.commands = commands;
    }

    public void setSelectedCommands(ArrayList<ObdCommand> selectedCommands) {
            this.selectedCommands.clear();
            this.selectedCommands.addAll(selectedCommands);
    }

    public DataBaseService getDbService() {
        return dbService;
    }

    public List<ObdCommand> getSelectedCommands() {
        return selectedCommands;
    }

    public Boolean contains(ObdCommand cmd, List<ObdCommand> cmds) {

        for (ObdCommand command: cmds) {
            if ( TextUtils.equals(cmd.getCmd(), command.getCmd())) {
                return true;
            }
        }

        return false;
    }

    public void cancelTasks() {
        if (cct != null && !cct.isCancelled()) {
            cct.cancel(true);
            cct = null;
        }
        if(stateTask !=null && !stateTask.isCancelled()) {
            stateTask.cancel(true);
            stateTask = null;
        }
    }

    @Override
    public void onBackPressed() {
        cancelTasks();
        super.onBackPressed();
    }
}

