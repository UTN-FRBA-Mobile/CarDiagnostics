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
                }
            }
        } catch(SocketException e){
            return stateActivity.getString(R.string.status_bluetooth_error_connecting);
        } catch (IOException e) {
            return stateActivity.getString(R.string.text_obd_command_failure);
        } catch (InterruptedException e) {
            return stateActivity.getString(R.string.text_obd_command_failure);
        }

        return stateActivity.getString(R.string.status_obd_ready);
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

                Thread.sleep(400);
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
            sock = null;

            AlertDialog.Builder builder = ConfirmDialog.getDialog(stateActivity, title, result);

            builder.setPositiveButton(ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.cancel();
                    ConnectionConfigTask cct = new ConnectionConfigTask(stateActivity);
                    cct.execute();
                }
            });
        }
    }

}
