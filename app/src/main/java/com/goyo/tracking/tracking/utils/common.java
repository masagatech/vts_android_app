package com.goyo.tracking.tracking.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.BatteryManager;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.widget.Toast;

import com.google.gson.JsonObject;


import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Random;

/**
 * Created by mTech on 04-Mar-2017.
 */
public class common {
    public static int TYPE_WIFI = 1;
    public static int TYPE_MOBILE = 2;
    public static int TYPE_NOT_CONNECTED = 0;


    public boolean getConnectivityStatus(Context context) {
        int conn = common.getConnectivityStatusInt(context);
        boolean status = false;
        if (conn == common.TYPE_WIFI) {
            status = true;
        } else if (conn == common.TYPE_MOBILE) {
            status = true;
        } else if (conn == common.TYPE_NOT_CONNECTED) {
            status = false;
        }
        return status;
    }

    private static int getConnectivityStatusInt(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (null != activeNetwork) {
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI)
                return TYPE_WIFI;

            if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE)
                return TYPE_MOBILE;
        }
        return TYPE_NOT_CONNECTED;
    }

    public static String getDateFormat(String presentfromat, String date, String reqFormat) {
        String strNewDate = "";
        try {
            DateFormat df = new SimpleDateFormat(presentfromat, Locale.ENGLISH);
            Date d = df.parse(date);
            SimpleDateFormat sdf = new SimpleDateFormat(reqFormat, Locale.ENGLISH);
            strNewDate = sdf.format(d);
//			Log.e("New Date", "Date==  "+strNewDate);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return strNewDate;
    }

    public static String deviceId(Context ctx) {

        String android_id = Settings.Secure.getString(ctx.getContentResolver(), Settings.Secure.ANDROID_ID);
        return android_id;

    }

    private boolean isDeviceSupportCamera(Context c) {
        if (c.getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_CAMERA)) {
            // this device has a camera
            return true;
        } else {
            // no camera on this device
            return false;
        }
    }


    public static String dateandtime(Context ctx) {

        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss.SSS");
        String currenttime = sdf.format(new Date());
        return currenttime;

    }

    public static String dateandtime(Context ctx, String Format) {

        SimpleDateFormat sdf = new SimpleDateFormat(Format);
        String currenttime = sdf.format(new Date());
        return currenttime;

    }

    public final static String Language(String key) {
        HashMap<String, String> strLang = new HashMap<>();
        strLang.put("en", "English");
        strLang.put("gu", "ગુજરાતી");
        strLang.put("hi", "हिंदी");
        strLang.put("mi", "मराठी");
        return strLang.get(key);
    }

    public static boolean setLanguage(Context appContext, Context baseContext) {
        boolean isLanguageSet = true;
        String languageToLoad = SHP.get(appContext, SHP.ids.sett_lang, "false").toString(); // your language
        if (languageToLoad.equals("false")) {
            languageToLoad = "en";
            isLanguageSet = false;
        }
        Locale locale = new Locale(languageToLoad);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        baseContext.getResources().updateConfiguration(config,
                baseContext.getResources().getDisplayMetrics());
        return isLanguageSet;
    }

    public static boolean isLocationEnabled(Context context) {
        int locationMode = 0;
        String locationProviders;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            try {
                locationMode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE);

            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
                return false;
            }

            return locationMode != Settings.Secure.LOCATION_MODE_OFF;

        } else {
            locationProviders = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
            return !TextUtils.isEmpty(locationProviders);
        }


    }

    public static boolean hasPermissions(Context context, String... permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }


    public static boolean checkPermission(String per, Activity contex) {
        int result = ContextCompat.checkSelfPermission(contex, per);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    // TODO move to util class?
    public static float getAverageSpeed(float distance, float timeTaken) {
        //float minutes = timeTaken/60;
        //float hours = minutes/60;
        float speed = 0;
        if (distance > 0) {
            float distancePerSecond = timeTaken > 0 ? distance / timeTaken : 0;
            float distancePerMinute = distancePerSecond * 60;
            float distancePerHour = distancePerMinute * 60;
            speed = distancePerHour > 0 ? (distancePerHour / 1000) : 0;
        }

        return speed;
    }

    public static float getBatteryLevel(Context c) {
        Intent batteryIntent = c.registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        int level = batteryIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = batteryIntent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

        // Error checking that probably isn't needed but added just in case.
        if (level == -1 || scale == -1) {
            return 50.0f;
        }

        return ((float) level / (float) scale) * 100.0f;
    }

//    public static String generateTripString(Context c, String tripid, String msttrpid, String speedLimit, String enttid, String vhid, String driverid) {
//        JsonObject x = new JsonObject();
//        x.addProperty("tripid", tripid);
//        x.addProperty("msttrpid", msttrpid);
//        x.addProperty("speedLimit", speedLimit);
//        x.addProperty("enttid", enttid);
//        x.addProperty("vhid", vhid);
//        x.addProperty("driverid", driverid);
//
//        String logentry = x.toString();
//        SHP.set(c, SHP.ids.tripdetail, logentry);
//        return logentry;
//    }

    public static boolean isServiceRunning(Context c, Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) c.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public static boolean isAppRunning(final Context context, final String packageName) {
        final ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        final List<ActivityManager.RunningAppProcessInfo> procInfos = activityManager.getRunningAppProcesses();
        if (procInfos != null) {
            for (final ActivityManager.RunningAppProcessInfo processInfo : procInfos) {
                if (processInfo.processName.equals(packageName)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static String getDeviceUniqueID(Activity activity) {
        String device_unique_id = Settings.Secure.getString(activity.getContentResolver(),
                Settings.Secure.ANDROID_ID);
        return device_unique_id;
    }

    public static String getUniqueID(String uid) {
        String schid = "100";

        return schid + uid;
    }

    public static boolean isOnline(Context c) {
        ConnectivityManager cm = (ConnectivityManager) c
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        } else {
            Toast.makeText(c, "No internet connection.", Toast.LENGTH_SHORT).show();
        }
        return false;
    }
}
