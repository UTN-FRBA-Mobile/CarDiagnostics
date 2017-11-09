package com.cardiag.models.runnable;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.cardiag.R;
import com.cardiag.activity.ConfigActivityMain;
import com.cardiag.activity.StateActivity;
import com.cardiag.models.commands.ObdCommand;
import com.cardiag.models.config.ObdCommandSingleton;
import com.cardiag.models.exceptions.BadResponseException;
import com.cardiag.utils.BluetoothManager;
import com.cardiag.utils.ConfirmDialog;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.SocketException;
import java.util.List;

/**
 * Created by Leo on 29/7/2017.
 */

public class StateTask extends AsyncTask<String, ObdCommand, String> {

    private StateActivity stateActivity;
    private BluetoothSocket sock = null;
    private Integer waitTime = ObdCommandSingleton.WAIT_TIME;
    private SharedPreferences prefs;

    public StateTask(StateActivity stateActivity) {
        this.stateActivity = stateActivity;
        this.sock = stateActivity.getSock();
    }

    @Override
    protected String doInBackground(String... strings) {

        BluetoothAdapter ba = stateActivity.getBluetoothAdapter();
        if (ba == null || !ba.isEnabled()) {
            return stateActivity.getString(R.string.text_bluetooth_disabled);
        }

        try {
            synchronized (this) {
                initiateConnection();
                List<ObdCommand> commands;

                while (!isCancelled()) {

                    if (sock == null || !sock.isConnected()) {
                        return stateActivity.getString(R.string.status_bluetooth_error_connecting);
                    }

                    InputStream inputStream = sock.getInputStream();
                    OutputStream outputStream = sock.getOutputStream();
//                    InputStream inputStream = null;
//                    OutputStream outputStream = null;

                    commands = stateActivity.getSelectedCommands();

                    runCommands(commands, inputStream, outputStream);
                    if (allError(commands)) {
                        throw new BadResponseException(stateActivity.getString(R.string.text_obd_command_exception));
                    }
                }
            }
        }catch (Exception e) {
            return  stateActivity.getString(R.string.error);
    }

        return stateActivity.getString(R.string.status_obd_ready);
    }

    private boolean allError(List<ObdCommand> commands) {
        Integer count = 0;
        for (ObdCommand cmd: commands) {
            count += (cmd.getError()) ? 1 : 0;
        }
        if (count.intValue() == commands.size()) {
            return true;
        }

        return false;
    }


    @Override
    protected void onProgressUpdate(ObdCommand... progress) {
        ObdCommand command = progress[0];
        stateActivity.stateUpdate();
    }

    public void runCommands(List<ObdCommand> cmds, InputStream is, OutputStream os) throws IOException, InterruptedException {

            for (ObdCommand cmd : cmds) {
                if (isCancelled()) {
                    break;
                }

                if ( sock != null && ! sock.isConnected() ) {
                    throw new IOException(stateActivity.getString(R.string.status_bluetooth_error_connecting));
                }

                Thread.sleep(waitTime);
                cmd.run(is, os);
                publishProgress(cmd);
            }
    }

    private BluetoothSocket initiateConnection() throws IOException {
        prefs = PreferenceManager.getDefaultSharedPreferences(stateActivity);

        // get the remote Bluetooth device
        final String remoteDevice = prefs.getString(ConfigActivityMain.BLUETOOTH_LIST_KEY, null);
        final BluetoothAdapter btAdapter = stateActivity.getBluetoothAdapter();

        if ( btAdapter == null || !btAdapter.isEnabled() ) {
            throw new IOException(stateActivity.getString(R.string.status_bluetooth_error_connecting));
        }

        if ( remoteDevice == null || "".equals(remoteDevice) ) {
            throw new IOException(stateActivity.getString(R.string.status_no_device_selected));
        }

        BluetoothDevice dev = btAdapter.getRemoteDevice(remoteDevice);

        btAdapter.cancelDiscovery();

        try {
            sock = BluetoothManager.connect(dev);
        } catch (IOException e) {
            throw new IOException(stateActivity.getString(R.string.state_dialog_error_initiating));
        }

        return sock;
    }

    @Override
    protected void onPostExecute(String result) {
        String ok = stateActivity.getString(R.string.status_obd_ready);
        String error = stateActivity.getString(R.string.error);
        closeSocket();

        if (TextUtils.equals(result, ok)) {
            stateActivity.setObdDataStatusText(stateActivity.getString(R.string.status_obd_data_stopped));
            return;
        }

        if (TextUtils.equals(result, error)) {
            stateActivity.prepareButtons(false);
            ConnectionConfigTask cct = new ConnectionConfigTask(stateActivity);
            ProgressDialog pDialog = cct.getProgressDialog();
            pDialog.setTitle(stateActivity.getString(R.string.dialog_reconnecting_title));
            cct.execute();
            stateActivity.setObdDataStatusText(stateActivity.getString(R.string.status_obd_data_stopped));
            return;
        }

        String title = stateActivity.getString(R.string.error);
        stateActivity.setObdStatusText(stateActivity.getString(R.string.status_obd_disconnected));
        stateActivity.setObdDataStatusText(stateActivity.getString(R.string.status_obd_data_stopped));
        stateActivity.prepareButtons(false);
        ConfirmDialog.showCancellingDialog(stateActivity, title, result, false);

    }

    protected void closeSocket() {
        try {
            sock.getInputStream().close();
            sock.getOutputStream().close();
            sock.close();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }


    @Override
    protected void onCancelled() {
        super.onCancelled();

        try {
            sock.getInputStream().close();
            sock.getOutputStream().close();
            sock.close();
            stateActivity.setObdDataStatusText(stateActivity.getString(R.string.status_obd_data_stopped));
        } catch (IOException e1) {
            e1.printStackTrace();
        }

    }
}
