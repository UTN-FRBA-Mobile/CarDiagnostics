package com.cardiag.activity;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.widget.TextView;
import android.widget.Toast;

import com.cardiag.R;
import java.util.ArrayList;
import java.util.Set;


public class ConfigActivityMain extends PreferenceActivity  {

    public static final String BLUETOOTH_LIST_KEY = "bluetooth_list_preference";
    private static final int BLUETOOTH_REQUEST = 1;
//    private TextView selectedDevice;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        selectedDevice = (TextView) findViewById(R.id.selected_device);

        if (!enableBluetooth(BLUETOOTH_REQUEST)) {
            initiateConfiguration();
        }

    }

    private void initiateConfiguration() {
    /*
     * Read preferences resources available at res/xml/preferences.xml
     */
        addPreferencesFromResource(R.xml.preferences_main);

        ArrayList<CharSequence> pairedDeviceStrings = new ArrayList<>();
        ArrayList<CharSequence> vals = new ArrayList<>();
        final ListPreference listBtDevices = (ListPreference) findPreference(BLUETOOTH_LIST_KEY);
        final EditTextPreference editTextPref = (EditTextPreference)findPreference("edit_text_preference_1");
        if(listBtDevices.getEntry() != null){
            editTextPref.setTitle("Dispositivo seleccionado:");
            editTextPref.setSummary(listBtDevices.getEntry());
        }
    /*
     * Let's use this device Bluetooth adapter to select which paired OBD-II
     * compliant device we'll use.
     */
        final BluetoothAdapter mBtAdapter = getBluetoothAdapter();
        if (mBtAdapter == null) {
            listBtDevices
                    .setEntries(pairedDeviceStrings.toArray(new CharSequence[0]));
            listBtDevices.setEntryValues(vals.toArray(new CharSequence[0]));

            // we shouldn't get here, still warn user
            Toast.makeText(this, getString(R.string.text_no_bluetooth_id),
                    Toast.LENGTH_LONG).show();

            return;
        }

        final Activity thisActivity = this;
        listBtDevices.setEntries(new CharSequence[1]);
        listBtDevices.setEntryValues(new CharSequence[1]);
        listBtDevices.setOnPreferenceClickListener(new OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                if (mBtAdapter == null || !mBtAdapter.isEnabled()) {
                    Toast.makeText(thisActivity,
                            "This device does not support Bluetooth or it is disabled.",
                            Toast.LENGTH_LONG).show();
                    return false;
                }
                return true;
            }
        });

    /*
     * Get paired devices and populate preference list.
     */
        Set<BluetoothDevice> pairedDevices = mBtAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                pairedDeviceStrings.add(device.getName() + "\n" + device.getAddress());
                vals.add(device.getAddress());
            }
        }
        listBtDevices.setEntries(pairedDeviceStrings.toArray(new CharSequence[0]));
        listBtDevices.setEntryValues(vals.toArray(new CharSequence[0]));

        listBtDevices.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                editTextPref.setTitle("Dispositivo seleccionado:");
                for(int i = 0; i < listBtDevices.getEntries().length; i++){
                    String entry = (String) listBtDevices.getEntries()[i];
                    if(entry.contains(newValue.toString())){
                        editTextPref.setSummary(entry);
                    }
                }
                return true;
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == BLUETOOTH_REQUEST) {
            if (resultCode == Activity.RESULT_OK) {
                initiateConfiguration();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private BluetoothAdapter getBluetoothAdapter() {
        return BluetoothAdapter.getDefaultAdapter();
    }

    public boolean enableBluetooth(int requestCode) {

        if (getBluetoothAdapter() != null && !getBluetoothAdapter().isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, requestCode);
            return true;
        }
        return false;
    }
}
