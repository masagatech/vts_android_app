package com.goyo.tracking.track.dashboard;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.media.Image;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.goyo.tracking.track.R;
import com.goyo.tracking.track.forms.reportmilege;
import com.goyo.tracking.track.forms.history;
import com.goyo.tracking.track.forms.reportspeed;
import com.goyo.tracking.track.globals.Global;
import com.goyo.tracking.track.info.About;
import com.goyo.tracking.track.interfaces.VHOnStatusChangeListner;
import com.goyo.tracking.track.main;
import com.goyo.tracking.track.model.model_loginusr;
import com.goyo.tracking.track.model.vts_vh_model;
import com.goyo.tracking.track.model.vts_vh_status_model;
import com.goyo.tracking.track.realmmodel.vehiclesettings_model;
import com.goyo.tracking.track.service.livesocketService;
import com.goyo.tracking.track.settings.VehicleSettings;
import com.goyo.tracking.track.utils.SHP;
import com.goyo.tracking.track.utils.common;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import net.danlew.android.joda.JodaTimeAndroid;

import java.lang.reflect.Type;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;
import io.realm.RealmResults;


public class dash extends AppCompatActivity implements VHOnStatusChangeListner {

    private ProgressDialog loader;
    boolean mBound = false;
    livesocketService lvsservice;
    Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        JodaTimeAndroid.init(this);
        setContentView(R.layout.activity_dash);
        ButterKnife.bind(this);
        setTitle("VTS");
        loader = new ProgressDialog(this);
        loader.setMessage("Logging Out.. Please wait.");
        getSupportActionBar().setSubtitle(Global.loginusr.getFullname());
        checkPermission();
        realm = Realm.getDefaultInstance();
        //doVehicleDownload();
        String vhid = SHP.get(this, SHP.ids.selectedvh, "").toString();
        if (!vhid.equals("")) {
            Intent intent = new Intent(this, main.class);
            intent.putExtra("vhid", vhid);
            intent.putExtra("flag", "zoom");
            startActivity(intent);

        }
        animate();
    }

    @BindView(R.id.imgLive)
    ImageView imgLive;

    private void animate() {
        AlphaAnimation blinkanimation = new AlphaAnimation(1, 0); // Change alpha from fully visible to invisible
        blinkanimation.setDuration(1000); // duration - half a second
        blinkanimation.setInterpolator(new LinearInterpolator()); // do not alter animation rate
        blinkanimation.setRepeatCount(-1); // Repeat animation infinitely
        blinkanimation.setRepeatMode(Animation.REVERSE);

        imgLive.setAnimation(blinkanimation);
        //imgLive.startAnimation(blinkanimation);
    }

    final int PERMISSION_ALL = 1;

    private void checkPermission() {

        String[] PERMISSIONS = {Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
//                Manifest.permission.WRITE_EXTERNAL_STORAGE,
//                Manifest.permission.CAMERA,
                Manifest.permission.CALL_PHONE};
        if (!common.hasPermissions(this, PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_ALL: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {
                    finish();
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    @OnClick(R.id.livetracking)
    void livetracking_click() {
        Intent intent = new Intent(this, main.class);
        startActivity(intent);
    }

    @OnClick(R.id.history)
    void history_click() {
        Intent intent = new Intent(this, history.class);
        startActivity(intent);
    }

    @OnClick(R.id.milegerpt)
    void milegerpt_click() {
        Intent intent = new Intent(this, reportmilege.class);
        startActivity(intent);
    }

    @OnClick(R.id.settings)
    void settings_click() {
        Intent intent = new Intent(this, VehicleSettings.class);
        startActivity(intent);
    }

    @OnClick(R.id.speedreport)
    void speedreport_click() {
        Intent intent = new Intent(this, reportspeed.class);
        startActivity(intent);
    }

    @BindView(R.id.txtTotalCt)
    TextView txtTotalCt;

    @BindView(R.id.txtOnlineCt)
    TextView txtOnlineCt;

    @BindView(R.id.txtOfflineCt)
    TextView txtOfflineCt;

    @BindView(R.id.txtMovingCt)
    TextView txtMovingCt;


    @BindView(R.id.txtSpeed)
    TextView txtSpeed;


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_settings, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.about_us:
                Intent intent = new Intent(this, About.class);
                startActivity(intent);
                return true;
            case R.id.logout:
                //calling logout api
                LogOut();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }


    }

    private void LogOut() {
        String LogoutMessage = "Are you sure want Logout?";
//        if (Global.isOnline) {
//            isCallLogout = true;
//
//        }
//        else {
//            LogoutMessage = "Are you sure want Logout?";
//            isCallLogout = false;
//        }
        new AlertDialog.Builder(dash.this)
                .setTitle("Log Out")
                .setMessage(LogoutMessage)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        //calling logout api
                        String sessionid = SHP.get(dash.this, SHP.ids.sessionid, "-1").toString();
                        String uid = SHP.get(dash.this, SHP.ids.uid, "-1").toString();
                        JsonObject json = new JsonObject();
                        json.addProperty("sessionid", sessionid);
                        json.addProperty("email", uid);
                        Global.showProgress(loader);
                        Ion.with(dash.this)
                                .load(Global.urls.getlogout.value)

                                .setJsonObjectBody(json)
                                .asJsonObject()
                                .setCallback(new FutureCallback<JsonObject>() {
                                    @Override
                                    public void onCompleted(Exception e, JsonObject result) {
                                        // do stuff with the result or error
                                        try {
                                            if (result != null)
                                                Log.v("result", result.toString());
                                            // JSONObject jsnobject = new JSONObject(jsond);
                                            Gson gson = new Gson();
                                            Type listType = new TypeToken<List<model_loginusr>>() {
                                            }.getType();
                                            List<model_loginusr> login = (List<model_loginusr>) gson.fromJson(result.get("data"), listType);
                                            if (login.size() > 0) {
                                                model_loginusr m = login.get(0);
                                                if (m.getStatus() == 1) {
                                                    SHP.set(dash.this, SHP.ids.uid, "");
                                                    SHP.set(dash.this, SHP.ids.sessionid, "");
                                                    Intent i = new Intent(dash.this, com.goyo.tracking.track.initials.login.class);
                                                    removeSubscription();
                                                    stopService(new Intent(dash.this, livesocketService.class));
                                                    startActivity(i);
                                                    dash.this.finish();
                                                } else {
                                                    Toast.makeText(dash.this, "Faild to logout " + m.getErrcode() + " " + m.getErrmsg(), Toast.LENGTH_LONG).show();
                                                }
                                            } else {
                                                Toast.makeText(dash.this, "Oops there is some issue! please logout later!", Toast.LENGTH_SHORT).show();
                                            }
                                        } catch (Exception ea) {
                                            Toast.makeText(dash.this, "Oops there is some issue! Error: " + ea.getMessage(), Toast.LENGTH_LONG).show();
                                            ea.printStackTrace();
                                        }
                                        Global.hideProgress(loader);
                                    }
                                });

                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setCancelable(false)
                .setIcon(android.R.drawable.ic_lock_lock).show();
    }

    private void bindVehicles() {


        JsonObject json = new JsonObject();
        json.addProperty("flag", "vehicle_new");
        json.addProperty("enttid", Global.loginusr.getEnttid() + "");
        json.addProperty("uid", Global.loginusr.getDriverid() + "");
        json.addProperty("utype", Global.loginusr.getUtype());
        Ion.with(this)
                .load(Global.urls.gettrackboard.value)
                .setJsonObjectBody(json)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        // do stuff with the result or error
                        try {
                            if (result != null) Log.v("result", result.toString());
                            // JSONObject jsnobject = new JSONObject(jsond);
                            Gson gson = new Gson();
                            Type listType = new TypeToken<List<vts_vh_model>>() {
                            }.getType();
                            List<vts_vh_model> vhs = (List<vts_vh_model>) gson.fromJson(result.get("data"), listType);
                            for (int i = 0; i <= vhs.size(); i++) {
                                SubscribeToTopic(vhs.get(i));
                            }

                        } catch (Exception ea) {
                            ea.printStackTrace();
                        }
                        // menu_refresh.setEnabled(false);

                    }
                });
    }

    //inset new vehicle
    private void SubscribeToTopic(final vts_vh_model mod) {
        vehiclesettings_model data = realm.where(vehiclesettings_model.class).equalTo("vhId", mod.id + "").findFirst();
        if (data == null) {
            FirebaseMessaging.getInstance().subscribeToTopic("speed_" + mod.vhid);
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    vehiclesettings_model obj = realm.createObject(vehiclesettings_model.class, mod.id + "");
                    obj.setImei(mod.vhid);
                    obj.setVhname(mod.vno);
                    obj.setVhregno(mod.vno);
                    obj.setActive(true);
                    obj.setSpeedAlert(true);
                }
            });
        }
    }

    private void removeSubscription() {
        RealmResults<vehiclesettings_model> data = realm.where(vehiclesettings_model.class).findAll();
        final int size = data.size();
        for (int i = 0; i < size; i++) {
            vehiclesettings_model object = data.get(i);
            FirebaseMessaging.getInstance().unsubscribeFromTopic("speed_" + object.getImei());
            //do something with i
        }
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.delete(vehiclesettings_model.class);
            }
        });


    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (realm != null) {
            realm.close();
        }

        stopService(new Intent(this, livesocketService.class));
    }

    @Override
    protected void onResume() {
        super.onResume();

    }


    @Override
    protected void onStart() {
        super.onStart();
        // Bind to LocalService
        Intent intent = new Intent(this, livesocketService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unbindService(mConnection);
        mBound = false;
    }


    /**
     * Defines callbacks for service binding, passed to bindService()
     */
    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            livesocketService.LocalBinder binder = (livesocketService.LocalBinder) service;
            lvsservice = binder.getService();
            mBound = true;
            lvsservice.setVHOnStatusChangeListner(dash.this);


        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };


    private void doVehicleDownload() {
        // do something long
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                bindVehicles();
            }
        };
        new Thread(runnable).start();
    }

    @Override
    public void onCountChange(final vts_vh_status_model vh) {
        dash.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                txtTotalCt.setText(vh.getAll() + "");
                txtOfflineCt.setText(vh.getOffline() + "");
                txtOnlineCt.setText(vh.getOnline() + "");
                txtMovingCt.setText(vh.getMoving() + "");
                txtSpeed.setText(vh.getSpeedvoi() + "");
            }
        });


    }
}
