package com.cardiag.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;

import com.cardiag.R;
import com.cardiag.activity.StateActivity;
import com.cardiag.models.commands.ObdCommand;
import com.cardiag.models.runnable.StateTask;

import java.util.ArrayList;


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

    public static void showCancellingDialog(final Context context, String title, String msg) {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                context);

        final StateActivity sta = (StateActivity) context;
        // set dialog message
        alertDialogBuilder
                .setTitle(title)
                .setMessage(msg)
                .setCancelable(false)
                .setPositiveButton("Ok",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        dialog.cancel();
                        sta.finish();
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();

        alertDialog.show();
    }

    public static AlertDialog.Builder getDialog(final Context context, String title, String msg) {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                context);

        final StateActivity sta = (StateActivity) context;
        // set dialog message
        alertDialogBuilder
                .setTitle(title)
                .setMessage(msg)
                .setCancelable(false);

        return alertDialogBuilder;
    }

}
