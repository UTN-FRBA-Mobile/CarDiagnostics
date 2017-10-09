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
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.cardiag.R;
import com.cardiag.models.commands.ObdCommand;
import com.cardiag.models.commands.entities.Category;
import com.cardiag.models.runnable.ConnectionConfigTask;
import com.cardiag.models.runnable.StateTask;
import com.cardiag.persistence.DataBaseService;
import com.cardiag.utils.ConfirmDialog;
import com.cardiag.utils.ObdCommandAdapter;
import com.cardiag.utils.ObdCommandCheckAdapter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class  StateActivity extends AppCompatActivity  {

    private static final String TAG = StateActivity.class.getName();
    private static final int SELECT_COMMANDS = 0;
    private static final int CUSTOMIZED = 3;
    private static final int NO_ORIENTATION_SENSOR = 8;
    private static final int RECONNECT = 1;
    private static final int REQUEST_ENABLE_BT = 1234;
    private static Boolean bluetoothDefaultIsEnable = false;
    private GridView gridView;
    private MenuItem reconnect;
    private MenuItem selectCommands;
    private Button botonPlay;
    private Button botonStop;
    private TextView obdStatus;
    private TextView obdDataStatus;
    private SensorManager sensorManager;
    private PowerManager.WakeLock wakeLock;
//    private SharedPreferences prefs;
    private ArrayList<Boolean> availablePidsFlags = new ArrayList<Boolean>();
    private BluetoothSocket sock = null;
    private ArrayList<ObdCommand> commands = new ArrayList<ObdCommand>();
    private List<ObdCommand> selectedCommands = new ArrayList<ObdCommand>();
    private List<Category> categories = new ArrayList<Category>();
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
            ConfirmDialog.showCancellingDialog(this, error, msg, true);
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
        categories = dbService.getGroups(null,null);
        cct = new ConnectionConfigTask(this);
        cct.execute();

//        selectedCommands = dbService.getCommands(null, null);
//        gridView.setAdapter(new ObdCommandAdapter(selectedCommands, this));
    }

    private void doBindings() {
        gridView = (GridView) findViewById(R.id.data_grid);
        botonPlay = (Button) findViewById(R.id.botonPlay);
        botonStop = (Button) findViewById(R.id.botonStop);
        obdStatus = (TextView) findViewById(R.id.obd_status);
        obdDataStatus = (TextView) findViewById(R.id.obd_data_status);
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
        Collections.sort(selectedCommands);
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
        SubMenu submenu = menu.addSubMenu(Menu.NONE, SELECT_COMMANDS, Menu.NONE, getString(R.string.select_commands));

        menu.add(Menu.NONE, RECONNECT, Menu.NONE, getString(R.string.menu_reconnect)).setIcon(android.R.drawable.stat_notify_sync_noanim).setEnabled(false)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        reconnect = menu.getItem(RECONNECT);
        selectCommands = menu.getItem(SELECT_COMMANDS);

        for (Category g: categories) {
            submenu.add(Menu.NONE, g.getId(), Menu.NONE, g.getName());
        }
        submenu.add(Menu.NONE, CUSTOMIZED, Menu.NONE, getString(R.string.menu_customized));

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case CUSTOMIZED:
                AlertDialog dialog = getCheckBoxDialog();
                dialog.show();
                return true;
            case RECONNECT:
                cct = new ConnectionConfigTask(this);
                cct.execute();
                return true;
         }

        for (Category g: categories) {
            if (item.getItemId() == g.getId().intValue()) {
                selectCommands(g.getName());
                return true;
            }
        }

        return false;
    }
    private void selectCommands(String menu_item_description) {
        dbService.resetSelection();

        String[] args = new String[]{menu_item_description};
        ArrayList<ObdCommand> nuevaList = dbService.getCategoryCommands(args);
        List<ObdCommand> auxList = mergeCommandLists(nuevaList);
        selectedCommands.clear();
        selectedCommands.addAll(auxList);
        stateUpdate();
    }

    public void startLiveData(View view) {
        botonPlay.setEnabled(false);
        botonStop.setEnabled(true);
        selectCommands.setEnabled(false);
        obdDataStatus.setText(getString(R.string.status_obd_data));
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        if (stateTask != null && !stateTask.isCancelled()) {
            stateTask.cancel(true);
            stateTask = null;
        }

        stateTask = new StateTask(this);
        stateTask.execute();
    }

    public void stopLiveData(View view) {
        botonPlay.setEnabled(true);
        botonStop.setEnabled(false);
        selectCommands.setEnabled(true);
        obdDataStatus.setText(getString(R.string.status_obd_data_stopped));
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        if (stateTask != null) {
            stateTask.cancel(true);
            stateTask = null;
        }
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
        ArrayList<ObdCommand> auxSelected = mergeCommandLists(adapterSelected);

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

    private ArrayList<ObdCommand> mergeCommandLists(ArrayList<ObdCommand> newList) {
        ArrayList<ObdCommand> auxSelected = new ArrayList<ObdCommand>();

        for (ObdCommand cmd : newList) {
            cmd.setSelected(true);
            dbService.updateCommand(cmd, null);
        }

        for (ObdCommand cmd : selectedCommands) {
            if (contains(cmd, newList)) {
                auxSelected.add(cmd);
            }
        }

        for (ObdCommand cmd : newList) {
            if (!contains(cmd, selectedCommands)) {
                auxSelected.add(cmd);
            }
        }
        return auxSelected;
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
        if (cct != null && (!cct.isCancelled() || cct.getStatus() == AsyncTask.Status.RUNNING)) {
            cct.cancel(true);
            cct = null;
        }
        if(stateTask !=null && (!stateTask.isCancelled()|| stateTask.getStatus() == AsyncTask.Status.RUNNING)) {
            stateTask.cancel(true);
            stateTask = null;
        }

        if (sock != null) {
            try {
                sock.close();
            } catch (IOException e) {
                showToast(e.getMessage());
            }
        }
    }

    public MenuItem getReconnect() {
        return reconnect;
    }

    public void prepareButtons(Boolean prepare){
        botonPlay.setEnabled(prepare);
        botonStop.setEnabled(prepare);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        selectCommands.setEnabled(prepare);
        reconnect.setEnabled(!prepare);
        }
    }

    public void setObdStatusText(String text) {
        obdStatus.setText(text);
    }
    public void setObdDataStatusText(String text) {
        obdDataStatus.setText(text);
    }

    @Override
    public void onBackPressed() {
        cancelTasks();
        super.onBackPressed();
    }
}

