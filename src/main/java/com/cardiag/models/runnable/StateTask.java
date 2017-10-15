package com.cardiag.models.runnable;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.view.LayoutInflater;

import com.cardiag.R;
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
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Leo on 29/7/2017.
 */

public class StateTask extends AsyncTask<String, ObdCommand, String> {

    private StateActivity stateActivity;
    private BluetoothSocket sock = null;
    private Integer waitTime = ObdCommandSingleton.waitTime;

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
        } catch(SocketException e){
            return stateActivity.getString(R.string.status_bluetooth_error_connecting);
        } catch (IOException e) {
            return stateActivity.getString(R.string.text_obd_command_failure);
        } catch (InterruptedException e) {
            return stateActivity.getString(R.string.text_obd_command_failure);
        } catch (BadResponseException e) {
            try {
                sock.close();
            } catch (IOException e1) {
                e.printStackTrace();
            }
            return  stateActivity.getString(R.string.status_obd_ready);
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

    @Override
    protected void onPostExecute(String result) {

        String ok = stateActivity.getString(R.string.status_obd_ready);

        if (TextUtils.equals(result, ok)) {
            stateActivity.setObdDataStatusText(stateActivity.getString(R.string.status_obd_data_stopped));
        } else {
            String title = stateActivity.getString(R.string.error);
            stateActivity.setObdStatusText(stateActivity.getString(R.string.status_obd_disconnected));
            stateActivity.setObdDataStatusText(stateActivity.getString(R.string.status_obd_data_stopped));
            stateActivity.prepareButtons(false);
            ConfirmDialog.showCancellingDialog(stateActivity, title, result, false);

        }
    }

}
