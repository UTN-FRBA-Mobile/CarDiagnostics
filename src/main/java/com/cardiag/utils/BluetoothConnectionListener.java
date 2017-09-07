package com.cardiag.utils;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.cardiag.R;
import com.cardiag.activity.StateActivity;
import com.cardiag.models.runnable.ConnectionConfigTask;

/**
 * Created by Leo on 26/8/2017.
 */

public class BluetoothConnectionListener extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            StateActivity sta = (StateActivity) context;
            String action = intent.getAction();

            if (BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED.equals(action) || BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)) {
                sta.cancelTasks();
                ConnectionConfigTask cct = new ConnectionConfigTask(sta);
                ProgressDialog pDialog = cct.getProgressDialog();
                pDialog.setTitle(sta.getString(R.string.dialog_reconnecting_title));
                cct.execute();
            }
        }
}