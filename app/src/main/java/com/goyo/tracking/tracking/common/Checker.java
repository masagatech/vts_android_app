package com.goyo.tracking.tracking.common;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.provider.Settings;

import java.util.Arrays;
import java.util.List;

/**
 * Created by mTech on 23-Apr-2017.
 */
public class Checker {

/* FIELDS */

    private Activity activity;
    private Pass pass;
    private List<Resource> resourcesList;

/* TYPES */

    public enum Resource {
        NETWORK, GPS, BLUETOOTH
    }

    public static abstract class Pass {
        public abstract void pass();
    }

/* API */

    public Checker(Activity activity) {
        this.activity = activity;
    }

    public void check(Resource... resources) {
        resourcesList = Arrays.asList(resources);
        if (resourcesList.contains(Resource.GPS) && !isGPSActivated(activity)) {
            new AlertDialog.Builder(activity).setMessage("GPS required.").setCancelable(false).setPositiveButton("GPS", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    activity.startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    //turnGPSOn();
                }
            }).setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.cancel();
                    activity.finish();
                }
            }).create().show();
        } else if (resourcesList.contains(Resource.NETWORK) && !isNetworkActivated(activity)) {
            new AlertDialog.Builder(activity).setMessage("Network required.").setCancelable(false).setPositiveButton("3G/4G/LTE", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    Intent intent = new Intent(Intent.ACTION_MAIN);
                    intent.setComponent(new ComponentName("com.android.settings",
                            "com.android.settings.Settings$DataUsageSummaryActivity"));
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    activity.startActivity(intent);
                }
            }).setNeutralButton("WiFi", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    activity.startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                }
            }).setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.cancel();
                    activity.finish();
                }
            }).create().show();
        } else if (resourcesList.contains(Resource.BLUETOOTH) && !isBluetoothActivated(activity)) {
            new AlertDialog.Builder(activity).setMessage("Bluetooth required.").setCancelable(false).setPositiveButton("Bluetooth",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            activity.startActivity(new Intent(Settings.ACTION_BLUETOOTH_SETTINGS));
                        }
                    }).setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.cancel();
                    activity.finish();
                }
            }).create().show();
        } else {
            pass.pass();
        }
    }

    public Checker pass(Pass pass) {
        this.pass = pass;
        return this;
    }

    /* PRIVATE */
    private void turnGPSOn() {
        String provider = Settings.Secure.getString(this.activity.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);

        if (!provider.contains("gps")) { //if gps is disabled
            final Intent poke = new Intent();
            poke.setClassName("com.android.settings", "com.android.settings.widget.SettingsAppWidgetProvider");
            poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
            poke.setData(Uri.parse("3"));
            this.activity.sendBroadcast(poke);
        }
    }

    private void turnGPSOff() {
        String provider = Settings.Secure.getString(this.activity.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);

        if (provider.contains("gps")) { //if gps is enabled
            final Intent poke = new Intent();
            poke.setClassName("com.android.settings", "com.android.settings.widget.SettingsAppWidgetProvider");
            poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
            poke.setData(Uri.parse("3"));
            this.activity.sendBroadcast(poke);
        }
    }


    private boolean isGPSActivated(Context context) {
        return ((LocationManager) context.getSystemService(Context.LOCATION_SERVICE)).isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    private boolean isBluetoothActivated(Context context) {
        //return BluetoothAdapter.getDefaultAdapter().isEnabled();
        return false;
    }

    private boolean isNetworkActivated(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }
}