package com.goyo.tracking.track.initials;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.goyo.tracking.track.R;
import com.goyo.tracking.track.globals.Global;
import com.goyo.tracking.track.main;
import com.goyo.tracking.track.utils.SHP;

import io.fabric.sdk.android.Fabric;

//import com.crashlytics.android.Crashlytics;
//import com.goyo.parent.R;
//import com.goyo.parent.gloabls.Global;
//import com.goyo.parent.utils.SHP;
//
//import io.fabric.sdk.android.Fabric;

public class splash_screen extends AppCompatActivity {

    private static int SPLASH_TIME_OUT = 2000;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());

        setContentView(R.layout.activity_splash_screen);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        SetColor();

        Global.prgdialog = di();
        try {
            Integer VersionCode = getApplicationContext().getPackageManager().getPackageInfo(getApplicationContext().getPackageName(), 0).versionCode;
            String versionName = getApplicationContext().getPackageManager().getPackageInfo(getApplicationContext().getPackageName(), 0).versionName;
            TextView tvversionCode = (TextView) findViewById(R.id.txtVersionNo);
            tvversionCode.setText("V : " + versionName + "    ");

            if (!((Boolean) SHP.get(getApplicationContext(), SHP.ids.shortcut, false))) {
                addShortcut();
                SHP.set(getApplicationContext(), SHP.ids.shortcut, true);
            }

//            SQLBase dataaseInitiate = new SQLBase(splash_screen.this);
//            dataaseInitiate.close();

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        SHP.set(this, SHP.ids.selectedvh, "");
        checkNotificaionData();


    }

    private void checkNotificaionData() {
        if (getIntent().getExtras() != null) {
            Bundle b = getIntent().getExtras();
            String vhid = b.getString("vhid");
            if (vhid != null) {
                SHP.set(this, SHP.ids.selectedvh, vhid);
                //Toast.makeText(this, vhid, Toast.LENGTH_SHORT).show();
//            Intent intent = new Intent(this, main.class);
//            intent.putExtra("vhid", vhid);
//            startActivity(intent);
            }

        }


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent i = new Intent(splash_screen.this, sessionchecker.class);
                startActivity(i);
                finish();
            }
        }, SPLASH_TIME_OUT);

    }

    private void addShortcut() {
        //Adding shortcut for MainActivity
        //on Home screen
        Intent shortcutIntent = new Intent(getApplicationContext(),
                splash_screen.class);

        shortcutIntent.setAction(Intent.ACTION_MAIN);

        Intent addIntent = new Intent();
        addIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
        addIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, getString(R.string.app_name));
        addIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE,
                Intent.ShortcutIconResource.fromContext(getApplicationContext(),
                        R.drawable.goyo));

        addIntent
                .setAction("com.android.launcher.action.INSTALL_SHORTCUT");
        getApplicationContext().sendBroadcast(addIntent);
    }

    private void SetColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            View decor = getWindow().getDecorView();
            decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
    }

    private ProgressDialog di() {
        ProgressDialog d = new ProgressDialog(getApplicationContext());
        d.setCancelable(false);
        d.setMessage("Loading");
        return d;
    }

}
