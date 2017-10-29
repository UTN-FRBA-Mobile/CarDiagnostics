package com.cardiag.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;

import com.cardiag.activity.MainActivity;
import com.cardiag.activity.StateActivity;


public class ConfirmDialog {


    public static void showDialog(Context context, String title, String msg, LayoutInflater inflater) {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                context);

//       View view = inflater.inflate(R.layout.custom_dialog, null);

        // set dialog message
        alertDialogBuilder
                .setTitle(title)
                .setMessage(msg)
                .setCancelable(false)
                .setPositiveButton("Ok",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        dialog.cancel();
                    }
                });
//                .setCustomTitle(view);

        AlertDialog alertDialog = alertDialogBuilder.create();

        alertDialog.show();
    }

    public static void showGpsEnabledDialog(final Context context, final int requestCode) {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                context);

        final MainActivity mainActivity = (MainActivity) context;
        // set dialog message
        alertDialogBuilder
                .setMessage("Una aplicación quiere activar el GPS en este dispositivo")
                .setPositiveButton("Permitir",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        Intent enableBtIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        mainActivity.startActivityForResult(enableBtIntent, requestCode);

                    }
                })
                .setNegativeButton("Rechazar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();

        alertDialog.show();
    }

    public static void showCancellingDialog(final Context context, String title, String msg, final Boolean finish) {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                context);

        final AppCompatActivity sta = (AppCompatActivity) context;
        // set dialog message
        alertDialogBuilder
                .setTitle(title)
                .setMessage(msg)
                .setCancelable(false)
                .setPositiveButton("Ok",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        dialog.cancel();
                        if (finish) {
                            sta.finish();
                        }
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();

        alertDialog.show();
    }

    public static AlertDialog.Builder getDialog(final Context context, String title, String msg) {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                context);

        // set dialog message
        alertDialogBuilder
                .setTitle(title)
                .setMessage(msg)
                .setCancelable(false)
                .setPositiveButton("Ok",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        dialog.cancel();
                    }
                });


        return alertDialogBuilder;
    }

}
