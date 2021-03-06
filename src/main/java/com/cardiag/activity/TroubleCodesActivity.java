package com.cardiag.activity;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.transition.TransitionManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.cardiag.R;
import com.cardiag.models.commands.ObdCommand;
import com.cardiag.models.commands.control.TroubleCodesCommand;
import com.cardiag.models.commands.protocol.EchoOffCommand;
import com.cardiag.models.commands.protocol.LineFeedOffCommand;
import com.cardiag.models.commands.protocol.ObdResetCommand;
import com.cardiag.models.commands.protocol.ResetTroubleCodesCommand;
import com.cardiag.models.commands.protocol.SelectProtocolCommand;
import com.cardiag.models.exceptions.BadResponseException;
import com.cardiag.models.exceptions.MisunderstoodCommandException;
import com.cardiag.models.exceptions.NoDataException;
import com.cardiag.models.exceptions.UnableToConnectException;
import com.cardiag.models.solutions.Solution;
import com.cardiag.models.solutions.TroubleCode;
import com.cardiag.persistence.DataBaseService;
import com.cardiag.utils.BluetoothManager;
import com.cardiag.utils.ConfirmDialog;
import com.cardiag.utils.enums.ObdProtocols;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TroubleCodesActivity extends AppCompatActivity {

    private static final String TAG = TroubleCodesActivity.class.getName();
    private static final int NO_BLUETOOTH_DEVICE_SELECTED = 0;
    private static final int CANNOT_CONNECT_TO_DEVICE = 1;
    private static final int NO_DATA = 3;
    private static final int DATA_OK = 4;
    private static final int CLEAR_DTC = 5;
    private static final int OBD_COMMAND_FAILURE = 10;
    private static final int OBD_COMMAND_FAILURE_IO = 11;
    private static final int OBD_COMMAND_FAILURE_UTC = 12;
    private static final int OBD_COMMAND_FAILURE_IE = 13;
    private static final int OBD_COMMAND_FAILURE_MIS = 14;
    private static final int OBD_COMMAND_FAILURE_NODATA = 15;
    SharedPreferences prefs;
    private ProgressDialog progressDialog;
    private String remoteDevice;
    private GetTroubleCodesTask gtct;
    private BluetoothDevice dev = null;
    private BluetoothSocket sock = null;
    private Handler mHandler = new Handler(new Handler.Callback() {
        public boolean handleMessage(Message msg) {
            Log.d(TAG, "Message received on handler");
            String obdComandFailure = getString(R.string.text_obd_command_failure);
            switch (msg.what) {
                case NO_BLUETOOTH_DEVICE_SELECTED:
                    makeToast(getString(R.string.text_bluetooth_nodevice));
                    finish();
                    break;
                case CANNOT_CONNECT_TO_DEVICE:
                    makeToast(getString(R.string.text_bluetooth_error_connecting));
                    finish();
                    break;

                case OBD_COMMAND_FAILURE:
                    makeToast(obdComandFailure);
                    finish();
                    break;
                case OBD_COMMAND_FAILURE_IO:
                    makeToast(obdComandFailure + " IO");
                    finish();
                    break;
                case OBD_COMMAND_FAILURE_IE:
                    makeToast(obdComandFailure + " IE");
                    finish();
                    break;
                case OBD_COMMAND_FAILURE_MIS:
                    makeToast(obdComandFailure + " MIS");
                    finish();
                    break;
                case OBD_COMMAND_FAILURE_UTC:
                    makeToast(obdComandFailure + " UTC");
                    finish();
                    break;
                case OBD_COMMAND_FAILURE_NODATA:
                    makeToastLong(getString(R.string.text_noerrors));
                    //finish();
                    break;

                case NO_DATA:
                    makeToast(getString(R.string.text_dtc_no_data));
                    ///finish();
                    break;
                case DATA_OK:
                    dataOk((String) msg.obj);
                    break;

            }
            return false;
        }
    });
    private RecyclerView lvSolution;
    private ArrayList<Solution> solutions;
    private RecyclerView lv;
    private ArrayList<TroubleCode> troubleCodes = new ArrayList<TroubleCode>();
    private RecyclerView.Adapter solutionsAdapter;
    private RecyclerView.Adapter troubleCodesAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        setContentView(R.layout.trouble_codes);
        enableBluetooth();

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        remoteDevice = prefs.getString(ConfigActivity.BLUETOOTH_LIST_KEY, null);
        initialice();

        if (remoteDevice == null || "".equals(remoteDevice) /*|| remoteDevice.equals("1C:5A:3E:12:AB:5A")*/ ) {
            ConfirmDialog.showCancellingDialog(this, getString(R.string.error), getString(R.string.status_no_device_selected), true);
//            this.dataOkDummy();
//            Log.e(TAG, "No Bluetooth device has been selected.");
//            mHandler.obtainMessage(NO_BLUETOOTH_DEVICE_SELECTED).sendToTarget();
        } else {
            gtct = new GetTroubleCodesTask(this);
            gtct.execute(remoteDevice);
        }

    }

    public void enableBluetooth() {

        if(getBluetoothAdapter()!= null && !getBluetoothAdapter().isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, 1);
        }
    }

    public BluetoothAdapter getBluetoothAdapter() {
        return BluetoothAdapter.getDefaultAdapter();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.trouble_codes, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_clear_codes:
                troubleCodes.clear();
                troubleCodesAdapter.notifyDataSetChanged();
                solutions.clear();
                solutionsAdapter.notifyDataSetChanged();
                gtct = new GetTroubleCodesTask(this);
                gtct.execute(remoteDevice);
                break;
            case android.R.id.home:
                onBackPressed();
        }

        return true;
    }

    Map<String, String> getDict(int keyId, int valId) {
        String[] keys = getResources().getStringArray(keyId);
        String[] vals = getResources().getStringArray(valId);

        Map<String, String> dict = new HashMap<String, String>();
        for (int i = 0, l = keys.length; i < l; i++) {
            dict.put(keys[i], vals[i]);
        }

        return dict;
    }

    public void makeToast(String text) {
        Toast toast = Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT);
        toast.show();
    }
    public void makeToastLong(String text) {
        Toast toast = Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG);
        toast.show();
    }
    private void dataOk(String res) {
        DataBaseService db = new DataBaseService(this);
        Map<String, String> dtcVals = getDict(R.array.dtc_keys, R.array.dtc_values);

      //TODO replace below codes (res) with aboce dtcVals
        //String tmpVal = dtcVals.get(res.split("\n"));
        //String[] troubleCodes = new String[]{};
        //int i =1;
        if (!TextUtils.isEmpty(res)) {
            for (String dtcCode : res.split("\n")) {
                //troubleCodes.add(new TroubleCode(dtcCode , dtcVals.get(dtcCode)));  //--lo saca de un achivo en ingles
                troubleCodes.add(db.getTroubleCode(dtcCode));
                Log.d("TEST", dtcCode + " : " + dtcVals.get(dtcCode));
            }
        } else {
            troubleCodes.add(new TroubleCode(getString(R.string.trouble_codes_no_error),"", -1));
        }
        troubleCodesAdapter.notifyDataSetChanged();

    }

    public void select(int selectedErrorPosition) {
        TroubleCode troubleCode = troubleCodes.get(selectedErrorPosition);
        String errorDesc = troubleCode.getName();
        String stringDesc = getString(R.string.trouble_codes_no_error);
        if (TextUtils.equals(errorDesc, stringDesc)) {
            return;
        }

        View tvsol = findViewById(R.id.tvSolutions);
        tvsol.setVisibility(View.VISIBLE);
        solutions.removeAll(solutions);

        final ViewGroup transitionsContainer = (ViewGroup) findViewById(R.id.activity_troubles);
        TransitionManager.beginDelayedTransition(transitionsContainer);

        solutions.addAll(troubleCode.getSolutions());
        solutionsAdapter.notifyDataSetChanged();
    }

    public void selectSolution(int adapterPosition) {
        showSolution(solutions.get(adapterPosition));
    }


    public class ModifiedTroubleCodesObdCommand extends TroubleCodesCommand {
        @Override
        public String getResult() {
            // remove unwanted response from output since this results in erroneous error codes
            return rawData.replace("SEARCHING...", "").replace("NODATA", "");
        }
    }

    public class ClearDTC extends ResetTroubleCodesCommand {
        @Override
        public String getResult() {
            return rawData;
        }
    }

    private class GetTroubleCodesTask extends AsyncTask<String, Integer, String> {

        private TroubleCodesActivity troubleCodesActivity;
        public GetTroubleCodesTask(TroubleCodesActivity troubleCodesActivity) {
            this.troubleCodesActivity = troubleCodesActivity;
        }

        @Override
        protected void onPreExecute() {
            //Create a new progress dialog
            progressDialog = new ProgressDialog(TroubleCodesActivity.this);
            //Set the progress dialog to display a horizontal progress bar
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            //Set the dialog title to 'Loading...'
            progressDialog.setTitle(getString(R.string.dialog_loading_title));
            //Set the dialog message to 'Loading application View, please wait...'
            progressDialog.setMessage(getString(R.string.dialog_loading_body));
            //This dialog can't be canceled by pressing the back key
            progressDialog.setCancelable(false);
            //This dialog isn't indeterminate
            progressDialog.setIndeterminate(false);
            //The maximum number of items is 100
            progressDialog.setMax(5);
            //Set the current progress to zero
            progressDialog.setProgress(0);
            //Display the progress dialog
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            String result = "";
            if(params[0]==null || "".equals(params[0]))return "-";
            //Get the current thread's token
            synchronized (this) {
                Log.d(TAG, "Starting service..");
                // get the remote Bluetooth device

                final BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
                dev = btAdapter.getRemoteDevice(params[0]);

                Log.d(TAG, "Stopping Bluetooth discovery.");
                btAdapter.cancelDiscovery();

                Log.d(TAG, "Starting OBD connection..");

                // Instantiate a BluetoothSocket for the remote device and connect it.
                try {
                    sock = BluetoothManager.connect(dev);
                } catch (Exception e) {
                    Log.e(
                            TAG,
                            "There was an error while establishing connection. -> "
                                    + e.getMessage()
                    );
                    Log.d(TAG, "Message received on handler here");
                    mHandler.obtainMessage(CANNOT_CONNECT_TO_DEVICE).sendToTarget();
                    return null;
                }

                try {
                    // Let's configure the connection.
                    Log.d(TAG, "Queueing jobs for connection configuration..");
                    ArrayList<ObdCommand> cmds = new ArrayList<>();
                    publishProgress(1);

                    ObdResetCommand obdResetCommand = new ObdResetCommand();
                    cmds.add(obdResetCommand);
                    obdResetCommand.run(sock.getInputStream(), sock.getOutputStream());

                    publishProgress(2);

                    EchoOffCommand echoOffCommand = new EchoOffCommand();
                    cmds.add(echoOffCommand);
                    echoOffCommand.run(sock.getInputStream(), sock.getOutputStream());

                    publishProgress(3);

                    LineFeedOffCommand lineFeedOffCommand = new LineFeedOffCommand();
                    cmds.add(lineFeedOffCommand);
                    lineFeedOffCommand.run(sock.getInputStream(), sock.getOutputStream());

                    publishProgress(4);

                    SelectProtocolCommand selectProtocolCommand = new SelectProtocolCommand(ObdProtocols.AUTO);
                    cmds.add(selectProtocolCommand);
                    selectProtocolCommand.run(sock.getInputStream(), sock.getOutputStream());

                    publishProgress(5);

                    ModifiedTroubleCodesObdCommand tcoc = new ModifiedTroubleCodesObdCommand();
                    cmds.add(tcoc);
                    tcoc.run(sock.getInputStream(), sock.getOutputStream());
                    //Each command has an error flag that will be true because of timeout o bad response.
                    validateErrors(cmds);

                    result = tcoc.getFormattedResult();

                    publishProgress(6);

                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e("DTCERR", e.getMessage());
                    mHandler.obtainMessage(OBD_COMMAND_FAILURE_IO).sendToTarget();
                    return null;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    Log.e("DTCERR", e.getMessage());
                    mHandler.obtainMessage(OBD_COMMAND_FAILURE_IE).sendToTarget();
                    return null;
                } catch (UnableToConnectException e) {
                    e.printStackTrace();
                    Log.e("DTCERR", e.getMessage());
                    mHandler.obtainMessage(OBD_COMMAND_FAILURE_UTC).sendToTarget();
                    return null;
                } catch (MisunderstoodCommandException e) {
                    e.printStackTrace();
                    Log.e("DTCERR", e.getMessage());
                    mHandler.obtainMessage(OBD_COMMAND_FAILURE_MIS).sendToTarget();
                    return null;
                } catch (NoDataException e) {
                    Log.e("DTCERR", e.getMessage());
                    mHandler.obtainMessage(OBD_COMMAND_FAILURE_NODATA).sendToTarget();
                    return null;
//                } catch (Exception e) {
//                    Log.e("DTCERR", e.getMessage());
//                    mHandler.obtainMessage(OBD_COMMAND_FAILURE).sendToTarget();
                }catch (BadResponseException e){
                    return getString(R.string.error);
                } finally {

                    // close socket
                    closeSocket(sock);
                }

            }

            return result;
        }

        private void validateErrors(ArrayList<ObdCommand> cmds) {
            int count = 0;
            for (ObdCommand cmd: cmds) {
                if ( cmd.getError()) {
                    count ++;
                }
            }

            if (count == cmds.size()) {
                throw new BadResponseException();
            }
        }

        public void closeSocket(BluetoothSocket sock) {
            if (sock != null)
                // close socket
                try {
                    sock.close();
                } catch (IOException e) {
                    Log.e(TAG, e.getMessage());
                }
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            progressDialog.setProgress(values[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            progressDialog.dismiss();

            if (TextUtils.equals(result, getString(R.string.error))) {
                ConfirmDialog.getDialog(troubleCodesActivity, getString(R.string.error), getString(R.string.text_obd_command_exception2)).show();
                return;
            }

            mHandler.obtainMessage(DATA_OK, result).sendToTarget();

        }
    }

    private void dataOkDummy() {
        String res= "P0010\nP0008\nP057F\nP0212\nP0524\nP0453\nP0013\nP0171\nP2299";
        this.dataOk(res);
        return;
      //  lv.setTextFilterEnabled(true);
    }

    private void initialice() {
        lv = (RecyclerView) findViewById(R.id.lvErrors);

        lvSolution = (RecyclerView)  findViewById(R.id.lvSolutions);
        solutions = new ArrayList<Solution>();
        //solutionsAdapter = new ArrayAdapter<Solution>(this, android.R.layout.simple_list_item_1, solutions);
        solutionsAdapter = new SolutionsAdapter(this,solutions);

        final Context context = this;
        lv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                solutions.removeAll(solutions);
                //   solutions.addAll(new DataBaseService(context).getSolutions(troubleCodes.get(i)));
                //solutions.addAll(troubleCodes.get(i).getSolutions());
                solutionsAdapter.notifyDataSetChanged();
            }
        });
     /*   lvSolution.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                showSolution(solutions.get(i));
            }
        });
*/

        // use a linear layout manager
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        lv.setLayoutManager(mLayoutManager);
        LinearLayoutManager mLayoutManager2 = new LinearLayoutManager(this);
        lvSolution.setLayoutManager(mLayoutManager2);
        lvSolution.setAdapter(solutionsAdapter);

//        troubleCodesAdapter = new ArrayAdapter<TroubleCode>(this, android.R.layout.simple_list_item_1, troubleCodes);
        troubleCodesAdapter = new ErrorAdapter(this,troubleCodes);
        lv.setAdapter(troubleCodesAdapter);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void showSolution(Solution solution) {
        Intent intent = new Intent(this, solution.getActivity());
        intent.putExtra("solution",solution);
        startActivity(intent);
    }

}
