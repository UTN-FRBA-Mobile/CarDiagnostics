package com.cardiag.models.runnable;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;

import com.cardiag.R;
import com.cardiag.activity.ConfigActivityMain;
import com.cardiag.activity.StateActivity;
import com.cardiag.models.commands.ObdCommand;
import com.cardiag.models.commands.protocol.AvailablePidsCommand;
import com.cardiag.models.commands.protocol.AvailablePidsCommand_01_20;
import com.cardiag.models.commands.protocol.AvailablePidsCommand_21_40;
import com.cardiag.models.commands.protocol.AvailablePidsCommand_41_60;
import com.cardiag.models.commands.protocol.AvailablePidsCommand_61_80;
import com.cardiag.models.commands.protocol.AvailablePidsCommand_81_A0;
import com.cardiag.models.commands.protocol.EchoOffCommand;
import com.cardiag.models.commands.protocol.HeadersOffCommand;
import com.cardiag.models.commands.protocol.LineFeedOffCommand;
import com.cardiag.models.commands.protocol.ObdResetCommand;
import com.cardiag.models.commands.protocol.SelectProtocolCommand;
import com.cardiag.models.commands.protocol.SpacesOffCommand;
import com.cardiag.models.commands.protocol.TimeoutCommand;
import com.cardiag.models.config.ObdCommandSingleton;
import com.cardiag.persistence.DataBaseService;
import com.cardiag.persistence.ObdCommandContract;
import com.cardiag.utils.BluetoothManager;
import com.cardiag.utils.ConfirmDialog;
import com.cardiag.utils.enums.ObdProtocols;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by Leo on 29/7/2017.
 */

public class ConnectionConfigTask extends AsyncTask<String, ProgressData, ProgressData> {

    private StateActivity stateActivity;
    private SharedPreferences prefs;
    private String configured;
    private String configuring;
    private ArrayList<ObdCommand> commands = new ArrayList<ObdCommand>();
    private ArrayList<Boolean> flags = new ArrayList<Boolean>();
    private ProgressDialog progressDialog;
    private DataBaseService dataBaseService;
    private ConnectionConfigTask cct = this;
    private BluetoothSocket sock;
    private Integer waitTime = ObdCommandSingleton.WAIT_TIME;


    public ConnectionConfigTask(StateActivity stateActivity) {
        dataBaseService = stateActivity.getDbService();
        commands = dataBaseService.getCommands(null,null);
        this.stateActivity = stateActivity;
        //Create a new progress dialog
        progressDialog = new ProgressDialog(stateActivity);
        progressDialog.setTitle(stateActivity.getString(R.string.dialog_loading_title));

        this.configured = stateActivity.getString(R.string.status_obd_configured);
        this.configuring = stateActivity.getString(R.string.status_obd_configuring);

    }

    @Override
    protected void onPreExecute() {
        //Set the progress dialog to display a horizontal progress bar
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        //Set the dialog message to 'Loading application View, please wait...'
        progressDialog.setMessage(stateActivity.getString(R.string.state_dialog_initiating));
        //This dialog can be canceled by pressing the back key
        progressDialog.setCancelable(true);
        //This dialog isn't indeterminate
        progressDialog.setIndeterminate(false);
        //The maximum number of items is 100
        progressDialog.setMax(3);
        //Set the current progress to zero
        progressDialog.setProgress(0);
        //Display the progress dialog
        progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
//                stateActivity.setObdStatusText(stateActivity.getString(R.string.status_obd_disconnected));
                stateActivity.showDisconectedMsg(true);
                stateActivity.prepareButtons(false);
                cct.closeSocket();
                stateActivity.showToast(stateActivity.getString(R.string.state_dialog_error_initiating));
                cct.cancel(true);
            }
        });
        progressDialog.show();
    }

    @Override
    protected ProgressData doInBackground(String... strings) {

        ProgressData result = new ProgressData();

        try {
            BluetoothSocket sock = initiateConnection();
            publishProgress(new ProgressData(1,stateActivity.getString(R.string.state_dialog_configuring_body)));
            configureConnection(sock);
            publishProgress(new ProgressData(2,stateActivity.getString(R.string.state_dialog_filtering)));
            filterAvailableCommands();
            publishProgress(new ProgressData(3,configured));
        } catch (Exception e) {
            result.setError(true);
            result.setProgressMessage(e.getMessage());
            return result;
        }

        String where = ObdCommandContract.CommandEntry.SELECTED + "= ? AND " + ObdCommandContract.CommandEntry.AVAILABILITY + "= ?";
        String[] whereValues = new String[] {"1", "1"};
        ArrayList<ObdCommand> filteredAndSelected = dataBaseService.getCommands(where, whereValues);
        //Get "Tablero" sensors if none selected
        if (filteredAndSelected.size() == 0) {
            String[] values = new String[]{"Tablero"};
            filteredAndSelected = dataBaseService.getCategoryCommands(values);
            setSelected(filteredAndSelected);
        }
        Collections.sort(filteredAndSelected);
        result.setCommands(filteredAndSelected);
        result.setError(false);

        return result;
    }

    private void setSelected(ArrayList<ObdCommand> filteredAndSelected) {
        for (ObdCommand cmd: filteredAndSelected) {
            cmd.setSelected(true);
            dataBaseService.updateCommand(cmd,null);
        }
    }

    private void filterAvailableCommands() {

        dataBaseService.resetAvailability();

        for (ObdCommand cmd: commands) {
            Boolean av = cmd.isAvailable(flags);
            if ( av ) {
                dataBaseService.updateCommand(cmd, av);
            }
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
            if (sock != null) {
                closeSocket();
            }
            sock = BluetoothManager.connect(dev);
        } catch (IOException e) {
            throw new IOException(stateActivity.getString(R.string.state_dialog_error_initiating));
        }

        stateActivity.setSock(sock);
        return sock;
    }

    private void configureConnection(BluetoothSocket sock) throws Exception {
        try {
            InputStream inputStream = sock.getInputStream();
            OutputStream outputStream = sock.getOutputStream();
            ArrayList<ObdCommand> cmds = new ArrayList<ObdCommand>();


            // Let's configure the connection.
            ObdResetCommand obdResetCommand = new ObdResetCommand();
            obdResetCommand.run(inputStream, outputStream);

            //Below is to give the adapter enough time to reset before sending the commands, otherwise the first startup commands could be ignored.
            Thread.sleep(500);

            EchoOffCommand echoOffCommand1 = new EchoOffCommand();
            cmds.add(echoOffCommand1);
            /*
             * Will send second-time based on tests.
             */
            EchoOffCommand echoOffCommand2 = new EchoOffCommand();
            cmds.add(echoOffCommand2);

            LineFeedOffCommand lineFeedOffCommand = new LineFeedOffCommand();
            cmds.add(lineFeedOffCommand);

            SpacesOffCommand spacesOffCommand = new SpacesOffCommand();
            cmds.add(spacesOffCommand);

            HeadersOffCommand headersOffCommand = new HeadersOffCommand();
            cmds.add(headersOffCommand);

            TimeoutCommand timeoutCommand = new TimeoutCommand(62);
            cmds.add(timeoutCommand);

            SelectProtocolCommand selectProtocol = new SelectProtocolCommand(ObdProtocols.valueOf("AUTO"));
            cmds.add(selectProtocol);

            runCommands(cmds, inputStream, outputStream);

            setAvailablePIDs(inputStream, outputStream);
        } catch (Exception e) {
            throw new Exception(stateActivity.getString(R.string.state_dialog_error_configuring));
        }
    }

    public void setAvailablePIDs(InputStream inputStream, OutputStream outputStream) throws IOException, InterruptedException {

        ArrayList<AvailablePidsCommand> availablePIDsCommand = new ArrayList<AvailablePidsCommand>();
        Integer errorCount = 0;

        AvailablePidsCommand_01_20 av1 = new AvailablePidsCommand_01_20();
        AvailablePidsCommand_21_40 av2 = new AvailablePidsCommand_21_40();
        availablePIDsCommand.add(av2);
        AvailablePidsCommand_41_60 av3 = new AvailablePidsCommand_41_60();
        availablePIDsCommand.add(av3);
        AvailablePidsCommand_61_80 av4 = new AvailablePidsCommand_61_80();
        availablePIDsCommand.add(av4);
        AvailablePidsCommand_81_A0 av5 = new AvailablePidsCommand_81_A0();
        availablePIDsCommand.add(av5);

        av1.run(inputStream, outputStream);
        if (av1.getError()) {
            throw new IOException();
        }
        flags.addAll(av1.getFlags());
        //Run availability commands, checking for the next to be available
        for (AvailablePidsCommand cmd: availablePIDsCommand) {
            if (! cmd.isAvailable(flags)) {
                break;
            }
            Thread.sleep(waitTime);
            cmd.run(inputStream, outputStream);
            if (cmd.getError()) {
                throw new IOException();
            }
            flags.addAll(cmd.getFlags());
        }
    }

    public void runCommands(ArrayList<ObdCommand> cmds, InputStream is, OutputStream os) throws IOException, InterruptedException {

        int count = 0;
        for (ObdCommand cmd : cmds) {
            Thread.sleep(waitTime);
            cmd.run(is, os);
            if (cmd.getError()) {
                count ++;
            }
        }
        //If all commands run with errors, then throw exception to stop connection
        if (count == cmds.size()) {
            throw new IOException();
        }
    }

    @Override
    protected void onProgressUpdate(ProgressData... progress) {
        ProgressData pd = progress[0];
        progressDialog.setProgress(pd.getProgress());
        progressDialog.setMessage(pd.getProgressMessage());

    }

    @Override
    protected void onPostExecute(ProgressData progress) {

        Boolean error = progress.getError();
        ArrayList<ObdCommand> commands = progress.getCommands();

        progressDialog.dismiss();
        closeSocket();

        if(!error) {
            stateActivity.setSelectedCommands(commands);
            stateActivity.multiStateUpdate();
            stateActivity.prepareButtons(true);
//            stateActivity.setObdStatusText(stateActivity.getString(R.string.status_obd_connected));
            stateActivity.showDisconectedMsg(false);

        } else {
            String title = stateActivity.getString(R.string.error);
            stateActivity.prepareButtons(false);
//            stateActivity.setObdStatusText(stateActivity.getString(R.string.status_obd_disconnected));
            stateActivity.showDisconectedMsg(true);
            ConfirmDialog.showCancellingDialog(stateActivity, title, progress.getProgressMessage(), false);
        }
    }

    @Override
    protected void onCancelled() {
        closeSocket();
        progressDialog.dismiss();
        super.onCancelled();
    }
    private void closeSocket()  {
        if (sock != null) {
            try {
                sock.getInputStream().close();
                sock.getOutputStream().close();
                sock.close();
            } catch (IOException e) {
                stateActivity.showToast(e.getMessage());
            }
        }
    }
    public ProgressDialog getProgressDialog() {
        return progressDialog;
    }

}


