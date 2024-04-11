package com.mkology.thelearningapp.helper;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import com.mkology.thelearningapp.R;
import com.mkology.thelearningapp.loginPage.MainActivity;
import com.mkology.thelearningapp.saveUserData.SaveSharedPreference;

public class CustomizeAlertDialogs {


    public void showLogoutPopup (Context context, String title, String message) {
        MaterialAlertDialogBuilder alertDialog =  new MaterialAlertDialogBuilder(context, R.style.AlertDialogTheme)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        SaveSharedPreference.setLoggedIn(context, false);
                        Intent intent = new Intent(context, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        context.startActivity(intent);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
        alertDialog.setCancelable(false);
        alertDialog.setBackground(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.show();
    }

    public void showPopup (Context context, String title, String message) {
        MaterialAlertDialogBuilder alertDialog =  new MaterialAlertDialogBuilder(context, R.style.AlertDialogTheme)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        SaveSharedPreference.setLoggedIn(context, false);
                        Intent intent = new Intent(context, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        context.startActivity(intent);
                    }
                });

        alertDialog.setCancelable(false);
        alertDialog.setBackground(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.show();
    }
}
