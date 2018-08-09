package com.goyo.tracking.tracking.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.Ack;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.goyo.tracking.tracking.adapters.vh_rec_adapter;
import com.goyo.tracking.tracking.globals.Global;
import com.goyo.tracking.tracking.interfaces.OnVehicleModelChangeListner;
import com.goyo.tracking.tracking.interfaces.OnVehiclesReadyListner;
import com.goyo.tracking.tracking.interfaces.VHOnStatusChangeListner;
import com.goyo.tracking.tracking.main;
import com.goyo.tracking.tracking.model.vts_vh_model;
import com.goyo.tracking.tracking.model.vts_vh_status_model;
import com.goyo.tracking.tracking.realmmodel.vehiclesettings_model;
import com.goyo.tracking.tracking.utils.SHP;
import com.goyo.tracking.tracking.utils.common;
import com.goyo.tracking.tracking.utils.deviceUtils;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import io.realm.Realm;
import io.realm.RealmResults;

public class livesocketService extends Service {


    //Variables
    public List<vts_vh_model> vhs;
    private List<String> vharr;
    private String strVehicles = "";
    private vts_vh_status_model _chsStsCount;
    private String dateformt = "yyyy-MM-dd'T'HH:mm:ss.SSS";
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
    private TimeZone tz = TimeZone.getTimeZone("GMT");
    private Context context;
    public boolean isVhReady = false;

    private Socket mSocket;

    private final IBinder mBinder = new LocalBinder();

    /**
     * Class used for the client Binder.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with IPC.
     */
    public class LocalBinder extends Binder {
        public livesocketService getService() {
            // Return this instance of LocalService so clients can call public methods
            return livesocketService.this;
        }
    }


    {
        try {
            mSocket = IO.socket(Global.VTS_URL);
        } catch (URISyntaxException e) {
        }
    }


    /*Events declaration*/
    //events
    private VHOnStatusChangeListner _vhOnStatusChangeListner;

    public void setVHOnStatusChangeListner(VHOnStatusChangeListner eventListener) {
        _vhOnStatusChangeListner = eventListener;
    }

    //events
    private OnVehicleModelChangeListner _onVehicleModelChangeListner;

    public void setOnVehicleModelChangeListner(OnVehicleModelChangeListner eventListener) {
        _onVehicleModelChangeListner = eventListener;
    }

    //events
    private OnVehiclesReadyListner _onVehiclesReadyListner;

    public void setOnVehiclesReadyListner(OnVehiclesReadyListner eventListener) {
        _onVehiclesReadyListner = eventListener;
    }


    @Override
    public void onCreate() {
        _chsStsCount = new vts_vh_status_model();
        context = this;

        SHP.set(context, SHP.ids.lastsynctime, common.dateandtime(context, dateformt));
        String uid = SHP.get(context, SHP.ids.uid, "0").toString();

        String commanduid = common.getUniqueID(uid);

        //if (SHP.get(context, SHP.ids.commanduid, "").equals("")) {
        SHP.set(context, SHP.ids.commanduid, commanduid);
        //}
        FirebaseMessaging.getInstance().subscribeToTopic("evt_" + commanduid);


        bindVehicles();

        super.onCreate();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }


    //update counters
    private Handler mHandler = new Handler();

    private Runnable updateRemainingTimeRunnable = new Runnable() {
        @Override
        public void run() {
            //Toast.makeText(_c, "Calling", Toast.LENGTH_SHORT).show();
            synchronized (vhs) {
                updateCounter(false);
            }
        }
    };

    public void updateCounter(boolean isinit) {
        _chsStsCount.setMoving(0);
        _chsStsCount.setOffline(0);
        _chsStsCount.setOnline(0);
        _chsStsCount.setSpeedvoi(0);
//        for (vts_vh_model holder : list) {


        //  if (isinit) return;
        for (int i = 0; i < vhs.size(); i++) {
            vts_vh_model holder = vhs.get(i);
            holder.timeago = updateTimeRemaining(holder);
        }
        if (_vhOnStatusChangeListner != null) {
            _vhOnStatusChangeListner.onCountChange(_chsStsCount);
        }
    }


    public String updateTimeRemaining(vts_vh_model vhs) {

        if (vhs.sertm == null) {

            return "";
        }

        dateFormat.setTimeZone(tz);
        try {
            Date date = dateFormat.parse(vhs.sertm);

            Calendar cal = Calendar.getInstance(tz);
            Long difference = cal.getTimeInMillis() - date.getTime();
            String res = deviceUtils.getPassedTimeFromCreation(difference, vhs);
            if (vhs.acc.equals("1")) {
                addcount(vh_rec_adapter.vhsts.engon, 1, vhs, cal);


            } else {
                addcount(vhs.vhst, 1, vhs, cal);
            }
            return res;

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "-";
    }

    Timer status_tmr;

    private void startUpdateTimer() {
        if (status_tmr != null) return;
        status_tmr = new Timer();
        status_tmr.schedule(new TimerTask() {
            @Override
            public void run() {
                mHandler.post(updateRemainingTimeRunnable);

            }
        }, 1000, 25000);
    }

    //add count method
    private void addcount(vh_rec_adapter.vhsts _s, int count, vts_vh_model actsts, Calendar cal) {
        if (_s.equals(vh_rec_adapter.vhsts.engon) && actsts.vhst.equals(vh_rec_adapter.vhsts.online)) {
            actsts.vhst = vh_rec_adapter.vhsts.engon;
            _chsStsCount.addOnline(count);
            _chsStsCount.addMoving(count);
        } else if (_s.equals(vh_rec_adapter.vhsts.online)) {
            _chsStsCount.addOnline(count);

        } else if (_s.equals(vh_rec_adapter.vhsts.offline)) {
            _chsStsCount.addOffline(count);
        } else if (_s.equals(vh_rec_adapter.vhsts.engon)) {
            _chsStsCount.addOffline(count);
        }

        actsts.isp = false;
        if (actsts.lstspdtm != null && !actsts.lstspdtm.isEmpty()) {
            try {
                Date spdtime = dateFormat.parse(actsts.lstspdtm);
                Long difference = cal.getTimeInMillis() - spdtime.getTime();
                Long min = TimeUnit.MILLISECONDS.toMinutes(difference);
                if (min < (24 * 60)) {
                    _chsStsCount.addSpeedvoi(1);
                    actsts.isp = true;
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }

    public void ResetALL(){
        mSocket.disconnect();
        status_tmr.cancel();
        status_tmr = null;
        mHandler.removeCallbacks(updateRemainingTimeRunnable);
        vhs = null;

        bindVehicles();
    }

    //get all vehicles from server user specific
    private void bindVehicles() {
         // {"status":200,"data":[{"vhid":"359857080397599","id":"243","vno":"test GT06F","vhtyp":"car","devtyp":"GT06F Plus","vmodl":"pp","vrg":"test GT06F","extra":null,"advance":[{"attr":{"isfri":true,"ismon":true,"issat":true,"isthu":true,"istue":true,"iswed":true,"totime1":"14:00","totime2":"18:00","frmtime1":"10:00","frmtime2":"15:00","isrmtctrl":true},"vehid":243,"vehname":"test GT06F","vehregno":"test GT06F - 359857080397599"}]}]}

        if (Global.loginusr == null) return;
        String url = Global.getLoginType(context, "normal") ? Global.urls.gettrackboard.value : Global.urls.getVehicleDetails_vts.value;
        JsonObject json = new JsonObject();
        json.addProperty("flag", "vehicle_new");
        json.addProperty("enttid", Global.loginusr.getEnttid() + "");
        json.addProperty("uid", Global.loginusr.getDriverid() + "");
        json.addProperty("utype", Global.loginusr.getUtype());
        Ion.with(this)
                .load(url)
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
                            vhs = (List<vts_vh_model>) gson.fromJson(result.get("data"), listType);
//                            for (int i = 0; i <= vhs.size(); i++) {
//                                SubscribeToTopic(vhs.get(i));
//                            }
                            if (vhs.size() <= 0) {
                                updateCounter(true);
                            }

                            _chsStsCount.setAll(vhs.size());
                            displayVehicles();

                        } catch (Exception ea) {
                            ea.printStackTrace();
                        }
                        // menu_refresh.setEnabled(false);

                    }
                });
    }


    //

    private void SubscribeToTopic(final vts_vh_model mod, final long time) {

        Realm realm = Realm.getDefaultInstance();

        try {
            vehiclesettings_model data = realm.where(vehiclesettings_model.class).equalTo("vhId", mod.id + "").findFirst();
            if (data == null) {
                FirebaseMessaging.getInstance().subscribeToTopic("speed_" + mod.vhid);
                // FirebaseMessaging.getInstance().subscribeToTopic("evt_" + mod.vhid);
                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        vehiclesettings_model obj = realm.createObject(vehiclesettings_model.class, mod.id + "");
                        obj.setImei(mod.vhid);
                        obj.setVhname(mod.vno);
                        obj.setVhregno(mod.vno);
                        obj.setSettings1(time + "");

                        obj.setActive(true);
                        obj.setSpeedAlert(true);
                    }
                });
            } else {
                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        vehiclesettings_model dataq = realm.where(vehiclesettings_model.class).equalTo("vhId", mod.id + "").findFirst();
                        dataq.setSettings1(time + "");
                    }
                });
            }
        } catch (Exception ex) {

        } finally {
            realm.close();
        }
    }

    private void unsubscribenonavailveh(long time) {
        Realm realm = Realm.getDefaultInstance();

        try {
            final RealmResults<vehiclesettings_model> data = realm.where(vehiclesettings_model.class).notEqualTo("settings1", time + "").findAll();
            if (data != null && data.size() > 0) {
                for (int i = 0; i < data.size() - 1; i++) {
                    vehiclesettings_model mod = data.get(i);

                    FirebaseMessaging.getInstance().unsubscribeFromTopic("speed_" + mod.getImei());
                    //FirebaseMessaging.getInstance().unsubscribeFromTopic("evt_" + mod.getImei());

                }
                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        data.deleteAllFromRealm();
                    }
                });
            }
        } catch (Exception ex) {

        } finally {
            realm.close();
        }
    }


    private boolean isUpdate() {
        String Datetim = SHP.get(context, SHP.ids.lastsynctime, common.dateandtime(context, dateformt)).toString();
        SimpleDateFormat sdf = new SimpleDateFormat(dateformt);
        //sdf.setTimeZone(tz);

        try {
            Date date = sdf.parse(Datetim);

            Calendar cal = Calendar.getInstance();
            Long difference = cal.getTimeInMillis() - date.getTime();
            if (TimeUnit.MILLISECONDS.toMinutes(difference) > 3) {
                return true;
            } else {
                return false;
            }

        } catch (ParseException e) {
            e.printStackTrace();
            return true;
        }
    }

    private void getLastStatus(boolean isinit) {
        if (!isUpdate() && !isinit) return;

        JsonParser jsonParser = new JsonParser();


        JsonObject json = new JsonObject();
        json.add("vhids", jsonParser.parse("[" + strVehicles + "]"));
        Ion.with(this)
                .load(Global.urls.getvahicleupdates.value)
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
                            List<vts_vh_model> mod = (List<vts_vh_model>) gson.fromJson(result.get("data"), listType);
                            updateLastStatus(mod);

                        } catch (Exception ea) {
                            ea.printStackTrace();
                        }
                        // menu_refresh.setEnabled(false);
                        SHP.set(context, SHP.ids.lastsynctime, common.dateandtime(context, dateformt));

                    }
                });

    }

    private void displayVehicles() {
        //mClusterManager = new ClusterManager<ClusterMarkerItem>(this, mMap);

        if (vhs.size() > 0) {
            int arrsz = vhs.size();
            vharr = new ArrayList<>(arrsz);
            long time = System.currentTimeMillis();
            for (int i = 0; i < arrsz; i++) {
                vts_vh_model m = vhs.get(i);
                // m.timeago = updateTimeRemaining(m);
                m.indexid = i;
                vharr.add(i, m.vhid);
                SubscribeToTopic(m, time);
            }
            unsubscribenonavailveh(time);
            strVehicles = TextUtils.join("\",\"", vharr);
            strVehicles = "\"" + strVehicles + "\"";

            //strVehicles = TextUtils.join(",", vharr);
            getLastStatus(true);

            mSocket.on("msgd", onNewMessage);


            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mSocket.connect();
                }
            }, 1500);

        } else {
            // findViewById(R.id.txtNodata).setVisibility(View.VISIBLE);
        }

    }


    private void updateLastStatus(List<vts_vh_model> mod) {

        int size = mod.size();
        int selcpos = -1;
        String zoomid = SHP.get(this, SHP.ids.selectedvh, "").toString();
        SHP.set(this, SHP.ids.selectedvh, "");
        for (int i = 0; i < size; i++) {
            vts_vh_model vhm = mod.get(i);
            int k = vharr.indexOf(vhm.vhid);
            if (k > -1) {
                vts_vh_model a = vhs.get(k);
                Log.v("test- ere", k + " " + a.vno);
                a.acc = vhm.acc;
                a.gsmsig = vhm.gsmsig;
                a.btr = vhm.btr;
                a.actvt = vhm.actvt;
                a.btrst = vhm.btrst;
                a.sertm = vhm.sertm;
                a.gpstm = vhm.gpstm;
                a.speed = vhm.speed;
                a.alwspeed = vhm.alwspeed;
                a.lstspd = vhm.lstspd;
                a.lstspdtm = vhm.lstspdtm;
                a.oe = vhm.oe;

                a.d1 = vhm.d1;

                LatLng l = null;
                if (vhm.loc != null && vhm.loc.length > 0) {
                    a.lat = vhm.loc[1];
                    a.lon = vhm.loc[0];
                    a.islststsAvail = true;
                    a.loc = vhm.loc;
                    l = new LatLng(a.lat, a.lon);
                    a.bearing = vhm.bearing;
                } else {
                    l = new LatLng(0, 0);
                }
//                ClusterMarkerItem offsetItem = new ClusterMarkerItem(l.latitude, l.longitude);
//                mClusterManager.addItem(offsetItem);
            }

        }


        isVhReady = true;
        if (_onVehiclesReadyListner != null) {
            _onVehiclesReadyListner.onReady(vhs);

        }
        startUpdateTimer();

    }

    private Emitter.Listener onNewMessage = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            JSONObject data = (JSONObject) args[0];
            Decoder(data);
        }
    };

    private void Decoder(JSONObject data) {
        String evt = null;
        try {
            evt = data.getString("evt");
            switch (evt) {
                case "data": {
                    final JSONObject _ld = data.getJSONObject("data");
//                    livesocketService.this.runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
                    updateList(_ld);
//                        }
//                    });

                }
                break;
                case "regreq": {
//                    main.this.runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
                    if (vhs.size() > 0) {
                        mSocket.emit("reg_v", strVehicles.replace("\"", ""));
                    }
                    getLastStatus(false);

//                        }
//                    });

                }
                break;
                case "registered": {
                    // main.this.runOnUiThread(new Runnable() {
                    //@Override
                    // public void run() {
//                            Toast.makeText(livesocketService.this, "Registered", Toast.LENGTH_SHORT).show();
                    //   }
                    // });

                }
                break;
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void updateList(JSONObject _ld) {
        try {
            Log.i("SOcket Event", _ld.toString());
            String vhid = _ld.getString("vhid");
            int i = vharr.indexOf(vhid);
            if (i == -1) return;
            vts_vh_model m = vhs.get(i);
            m.sertm = _ld.getString("sertm");// server location time
            switch (_ld.getString("actvt")) {
                case "hrtbt": {
                    m.acc = _ld.getString("acc");
                    m.btr = _ld.getString("btr");
                    m.gsmsig = _ld.getString("gsmsig");
                    m.btrst = _ld.getString("btrst");
                    if (_ld.has("oe")) {
                        m.oe = _ld.getInt("oe");
                    }
                    if (_onVehicleModelChangeListner != null) {
                        _onVehicleModelChangeListner.onChange(m, i, "hrtbt");
                    }
                }
                break;
                case "loc": {

                    LatLng lastLoc = null;


                    if (m.lon != null && m.lon != 0) {
                        lastLoc = new LatLng(m.lat, m.lon);
                        m.Lastloc = lastLoc;
                    }


                    try {
                        JSONArray ar = _ld.getJSONArray("loc");// lat long in array
                        m.lon = ar.getDouble(0);
                        m.lat = ar.getDouble(1);
                    } catch (JSONException e) {

                    }

                    m.bearing = _ld.getDouble("bearing");// vehicle bearing
                    m.sat = _ld.getInt("sat"); // satlite counts
                    m.speed = _ld.getInt("speed");//speed of vehicle
                    m.gpstm = _ld.getString("gpstm"); // gps device time

                    if (_ld.getBoolean("isp")) {//is speed voilated flag
                        m.lstspdtm = _ld.getString("lstspdtm"); // last speed voilated time
                        m.lstspd = _ld.getInt("lstspd");//last voilated speed in integer
                    }

                    if (_ld.has("d1")) {
                        m.d1 = _ld.getInt("d1");
                    }

                    if (_ld.has("alwspeed")) {
                        m.alwspeed = _ld.getInt("alwspeed");//allow speed
                    } else {
                        m.alwspeed = 0;
                    }
//                    Double bearing = m.bearing;
//                    if (lastLoc != null) {
//                        LatLng currentLoc = new LatLng(m.lat, m.lon);
//
//                    }
                    if (_onVehicleModelChangeListner != null) {
                        _onVehicleModelChangeListner.onChange(m, i, "loc");
                    }
                }
                break;
                case "evt": {
                    //handle other events
                    Event(_ld.getString("evt"), _ld, m, i);
                }
                break;
                case "login": {

                }
                break;
            }


        } catch (
                JSONException e)

        {
            e.printStackTrace();
        }

    }

    private void Event(String evt, JSONObject _ld, vts_vh_model m, int pos) throws JSONException {
        switch (evt) {
            case "d1": {
                if (_ld.has("val")) {
                    m.d1 = _ld.getInt("val");
                    if (_onVehicleModelChangeListner != null) {
                        _onVehicleModelChangeListner.onChange(m, pos, "d1");
                    }
                }
            }
            break;
            case "dd": {
                if (_ld.has("oe")) {
                    m.oe = _ld.getInt("oe");
                    if (_onVehicleModelChangeListner != null) {
                        _onVehicleModelChangeListner.onChange(m, pos, "dd");
                    }
                }
            }
            break;
        }
    }


    @Override
    public void onDestroy() {

        if (mSocket != null) {

            mSocket.disconnect();
            mSocket.off("msgd", onNewMessage);

        }

        String uid = SHP.get(context, SHP.ids.uid, "0").toString();

        FirebaseMessaging.getInstance().unsubscribeFromTopic("evt_" + common.getUniqueID(uid));

        super.onDestroy();
    }

    /*PROPS*/
    public List<vts_vh_model> getVhs() {
        return vhs;
    }

    public void setVhs(List<vts_vh_model> vhs) {
        this.vhs = vhs;
    }


    /*Socket send data*/
    public void SendCommand(String cmd, Ack ack) {
        mSocket.emit("cmd", cmd, ack);
    }

}
