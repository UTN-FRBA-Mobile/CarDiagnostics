package com.cardiag.activity;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.cardiag.R;
import com.cardiag.utils.ConfirmDialog;

public class MainActivity extends AppCompatActivity {

    private Button carStateBtn, errorCodesBtn, serviceFinderBtn;
    private ConfirmDialog dialogBuilder = new ConfirmDialog();
    private String aboutStr;
    private static final int REQUEST_ENABLE_BT_STATES = 1;
    private static final int REQUEST_ENABLE_BT_DTC = 2;
    private static final int REQUEST_ENABLE_BT_MAPS = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        carStateBtn = (Button) findViewById(R.id.car_state);
        errorCodesBtn = (Button) findViewById(R.id.error_codes);
        serviceFinderBtn = (Button) findViewById(R.id.service_finder);
        aboutStr = getAboutMessage();

        //Typeface type = Typeface.createFromAsset(getAssets(),"robotottf/Roboto-BoldItalic.ttf");
        //errorCodesBtn.setTypeface(type);
    }

    public boolean enableBluetooth(int requestCode) {

        if (getBluetoothAdapter() != null && !getBluetoothAdapter().isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, requestCode);
            return true;
        }
        return false;
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem selected) {

        switch (selected.getItemId()) {
            case R.id.select_device:
                startActivity(ConfigActivityMain.class);
                return true;

            case R.id.about:
                ConfirmDialog.showDialog(this, getString(R.string.about), aboutStr,getLayoutInflater());
                return true;

            default:
                return false;
        }
    }

    private void startActivity(Class clas) {
        startActivity(new Intent(this, clas));
    }

    public void startErrorCodesActivity(View v) {

        if (enableBluetooth(REQUEST_ENABLE_BT_DTC)) {
            return;
        }
        startActivity(new Intent(this, TroubleCodesActivity.class));
    }

    public void startStateActivity(View v) {

        if (enableBluetooth(REQUEST_ENABLE_BT_STATES)) {
            return;
        }
        startActivity(new Intent(this, StateActivity.class));
    }

    public void startMapsActivity(View v) {

        if(enableGps(REQUEST_ENABLE_BT_MAPS)){
            return;
        }
        startActivity(new Intent(this, MapsActivity.class));
    }

    public String getAboutMessage(){
        String msg;
        Resources res = getResources();
        String[] devs = res.getStringArray(R.array.developers);
        String[] mails = res.getStringArray(R.array.mails);
        String aboutMsg = getString(R.string.about_message);
        String aboutIssue = getString(R.string.about_issues);

        msg = aboutMsg+"\n";
        for (String dev: devs) {
            msg += "\n- "+dev;
        }
        msg += "\n\n"+aboutIssue+"\n";
        for (String mail: mails) {
            msg += "\n- "+mail;
        }

        return msg;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_ENABLE_BT_STATES) {
            if (resultCode == Activity.RESULT_OK) {
                this.startActivity(StateActivity.class);
            }
        }

        if (requestCode == REQUEST_ENABLE_BT_DTC) {
            if (resultCode == Activity.RESULT_OK) {
                this.startActivity(TroubleCodesActivity.class);
            }
        }

        if(requestCode == REQUEST_ENABLE_BT_MAPS){
            if (resultCode == Activity.RESULT_OK) {
                this.startActivity(MapsActivity.class);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public Button getCarStateBtn() {
        return carStateBtn;
    }

    public void setCarStateBtn(Button carStateBtn) {
        this.carStateBtn = carStateBtn;
    }

    public Button getErrorCodesBtn() {
        return errorCodesBtn;
    }

    public void setErrorCodesBtn(Button errorCodesBtn) {
        this.errorCodesBtn = errorCodesBtn;
    }

    public Button getServiceFinderBtn() {
        return serviceFinderBtn;
    }

    public void setServiceFinderBtn(Button serviceFinderBtn) {
        this.serviceFinderBtn = serviceFinderBtn;
    }

    public String getAboutStr() {
        return aboutStr;
    }

    public void setAboutStr(String aboutStr) {
        this.aboutStr = aboutStr;
    }

    @Override
    protected void onDestroy() {
        BluetoothAdapter ba = getBluetoothAdapter();
        if (ba != null && ba.isEnabled()) {
            ba.disable();
        }
        super.onDestroy();
    }


    private BluetoothAdapter getBluetoothAdapter() {
        return BluetoothAdapter.getDefaultAdapter();
    }

    private LocationManager getLocationManager(){
        return (LocationManager) getSystemService( Context.LOCATION_SERVICE );
    }

    private boolean enableGps(int requestCode){

        if(!getLocationManager().isProviderEnabled(LocationManager.GPS_PROVIDER)){
            ConfirmDialog.showGpsEnabledDialog(MainActivity.this, requestCode);
            return true;
        }
        return false;

    }

}
