package com.goyo.tracking.tracking;


import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.os.SystemClock;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.flipboard.bottomsheet.BottomSheetLayout;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.google.maps.android.clustering.ClusterManager;

import com.goyo.tracking.tracking.adapters.vh_rec_adapter;
import com.goyo.tracking.tracking.common.Checker;
import com.goyo.tracking.tracking.dashboard.dash;
import com.goyo.tracking.tracking.dialogs.dialogCommands;
import com.goyo.tracking.tracking.globals.Global;
import com.goyo.tracking.tracking.helpers.ClusterMarkerItem;
import com.goyo.tracking.tracking.helpers.MarkerAnimation;
import com.goyo.tracking.tracking.helpers.RecyclerItemClickListner;
import com.goyo.tracking.tracking.helpers.RootActivity;
import com.goyo.tracking.tracking.interfaces.LatLngInterpolator;
import com.goyo.tracking.tracking.interfaces.OnVehicleModelChangeListner;
import com.goyo.tracking.tracking.interfaces.OnVehiclesReadyListner;
import com.goyo.tracking.tracking.interfaces.VHOnStatusChangeListner;
import com.goyo.tracking.tracking.model.vts_vh_model;
import com.goyo.tracking.tracking.model.vts_vh_status_model;
import com.goyo.tracking.tracking.realmmodel.vehiclesettings_model;
import com.goyo.tracking.tracking.service.livesocketService;
import com.goyo.tracking.tracking.utils.SHP;
import com.goyo.tracking.tracking.utils.common;
import com.goyo.tracking.tracking.utils.deviceUtils;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;

public class main extends RootActivity implements
        OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener, GoogleMap.OnMarkerClickListener,
        OnVehicleModelChangeListner, OnVehiclesReadyListner {
//    @BindView(R.id.lstVehicles)
//    ListView lstVeh;


    @BindView(R.id.lstVehicles)
    RecyclerView lstVeh;


    @BindView(R.id.sliding_layout)
    SlidingUpPanelLayout mSlidLayout;

    @BindView(R.id.txtSpeed)
    TextView txtSpeed;

    @BindView(R.id.txtVhtitle)
    TextView txtVhtitle;

    @BindView(R.id.imgbattry)
    ImageView imgbattry;

    @BindView(R.id.imgnetwork)
    ImageView imgnetwork;

    @BindView(R.id.imgacc)
    ImageView imgacc;

    @BindView(R.id.txtSpd)
    TextView txtSpd;

    @BindView(R.id.txtD1)
    TextView txtD1;

    @BindView(R.id.imgDotsts1)
    ImageView imgDotsts;

    @BindView(R.id.infoTopPanel)
    RelativeLayout infoTopPanel;

    @BindView(R.id.bottomsheet)
    BottomSheetLayout bottomsheet;

    @BindView(R.id.imgInfo)
    ImageButton imgInfo;

    @BindView(R.id.imgoil)
    ImageView imgoil;

    @BindView(R.id.btnCommands)
    ImageButton btnCommands;

    @BindView(R.id.btnFleet)
    ImageButton btnFleet;

    //top buttons

    @BindView(R.id.stBtnAll)
    Button stBtnAll;
    @BindView(R.id.stBtnOnl)
    Button stBtnOnl;
    @BindView(R.id.stBtnOff)
    Button stBtnOff;
    @BindView(R.id.stBtnSPD)
    Button stBtnSPD;
    @BindView(R.id.stBtnIng)
    Button stBtnIng;


    //variables
    private List<vts_vh_model> vhs;
    private List<String> vharr;
    private String strVehicles = "";
    boolean infotopisclosed = false;

    // markers variables
    ArrayList<Marker> mrkers;

    //google map
    private GoogleMap mMap;

    //actionbar count
    Button action_bar_all, action_bar_online, action_bar_offline, action_bar_moving;
    String FollowCamera = "";

    Realm realm;

    final int normalCar = R.drawable.redcar;
    final int selectedCar = R.drawable.yellowcar;
    final int blueCar = R.drawable.blue;
    final int greenCar = R.drawable.green;

    Marker selectedCar_Mrk = null;
    vts_vh_model selecteCar_Model = null;

    final float tilt = 90;
    int zIndex = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        bindActionBar();


        //Realm instance
        realm = Realm.getDefaultInstance();

        //  RecyclerView recyclerView = (RecyclerView) findViewById(R.id.button);
        lstVeh.setLayoutManager(new LinearLayoutManager(this));
        lstVeh.setLayoutManager(new LinearLayoutManager(this));
        lstVeh.setItemAnimator(new DefaultItemAnimator());
        mSlidLayout.setPanelHeight(140);
        mSlidLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);

        //
        /**/
        updatePanel(null, "reset");

        lstVeh.addOnItemTouchListener(new RecyclerItemClickListner(this,
                new RecyclerItemClickListner.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, final int position) {
                        // TODO Handle item click

                        carSelect(position, null);

                    }
                })
        );

        infoTopPanel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selVh != null) {
                    FollowCamera = selVh.vhid;

                    if (selVh.lat != 0) {
                        LatLng ml = new LatLng(selVh.lat, selVh.lon);
                        mSlidLayout.setPanelHeight(150);
                        mSlidLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
                        //Toast.makeText(getApplicationContext(), position + "", Toast.LENGTH_SHORT).show();
                        CameraPosition position1 =
                                new CameraPosition.Builder().target(ml)
                                        .zoom(17)
                                        //.bearing(vh.bearing.floatValue())
                                        //.tilt(tilt)
                                        .build();


                        changeCameraPosition(position1, true);
                    }
                }
            }
        });

        selectButton(stBtnAll);
        googleMapInit();

        registerLocalBroadCast();
        apllyListViewItemClick();
        //throw new RuntimeException("This is a crash");

    }

    //top action bar button clicks

    @OnClick(R.id.stBtnAll)
    public void onStBtnAllClick(View v) {
        if (adapter != null) {
            //mSlidLayout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
            mSlidLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
            deSelectMarker();
            adapter.filter(vh_rec_adapter.vhsts.all, "");
            adapter.updateCounter();
            showMarkersInMap();
            selectButton(stBtnAll);
        }
    }

    @OnClick(R.id.stBtnOnl)
    public void onStBtnOnlClick(View v) {
        if (adapter != null) {
            //mSlidLayout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
            adapter.filter(vh_rec_adapter.vhsts.online, "");
            deSelectMarker();
            adapter.updateCounter();
            showMarkersInMap();
            selectButton(stBtnOnl);

        }

    }

    @OnClick(R.id.stBtnOff)
    public void onStBtnOffClick(View v) {
        if (adapter != null) {
            //mSlidLayout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
            adapter.filter(vh_rec_adapter.vhsts.offline, "");
            deSelectMarker();
            adapter.updateCounter();
            showMarkersInMap();
            selectButton(stBtnOff);
        }

    }

    @OnClick(R.id.stBtnIng)
    public void onStBtnIngClick(View v) {
        if (adapter != null) {
            //mSlidLayout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
            adapter.filter(vh_rec_adapter.vhsts.engon, "");
            deSelectMarker();
            adapter.updateCounter();
            showMarkersInMap();
            selectButton(stBtnIng);
        }

    }

    @OnClick(R.id.stBtnSPD)
    public void onStBtnSPDClick(View v) {
        if (adapter != null) {
            //mSlidLayout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
            adapter.filter(vh_rec_adapter.vhsts.spd, "");
            deSelectMarker();
            adapter.updateCounter();
            showMarkersInMap();
            selectButton(stBtnSPD);
        }

    }

    private void carSelect(int position, vts_vh_model _vh) {
        vts_vh_model vh;

        if (_vh == null) {
            vh = lvsservice.getVhs().get(position);
        } else {
            vh = _vh;
            position = _vh.indexid;
        }
        deSelectMarker();
        if (vh.lat != null) {


//            Toast.makeText(getBaseContext(), "Sorry, No Location Found", Toast.LENGTH_SHORT).show();
//            animateSlide(false);
//            return;


            LatLng ml = new LatLng(vh.lat, vh.lon);
            mSlidLayout.setPanelHeight(150);
            mSlidLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
            //Toast.makeText(getApplicationContext(), position + "", Toast.LENGTH_SHORT).show();
            CameraPosition position1 =
                    new CameraPosition.Builder().target(ml)
                            .zoom(17)
                            //.bearing(vh.bearing.floatValue())
                            //.tilt(tilt)
                            .build();


            changeCameraPosition(position1, true);
            FollowCamera = vh.vhid;
            vh.isselected = true;
            Marker m = mrkers.get(position);
            if (selectedCar_Mrk != null) {
                selectedCar_Mrk.hideInfoWindow();
            }

//            try {
//                if (m != null) {
//                    LatLng lastLoc = new LatLng(m.getPosition().latitude, m.getPosition().longitude);
//                    LatLng currentLoc = new LatLng(vh.loc[1], vh.loc[0]);
//                    double bearing = bearingBetweenLocations(lastLoc, currentLoc);
//                    moveMarker(vh.vhid, m, currentLoc.latitude, currentLoc.longitude, bearing, vh);
//                }
//
//            } catch (Exception EX) {
//
//            }

            zIndex += 1;
            m.setZIndex(zIndex);
            selectedCar_Mrk = m;

            btnCommands.setVisibility(View.VISIBLE);
            selectedCar_Mrk.setTitle(vh.vno);
            selectedCar_Mrk.setSnippet(vh.vhid);

            selectedCar_Mrk.showInfoWindow();
            zIndex += 1;
            m.setZIndex(zIndex);
        }

        selecteCar_Model = vh;
        selecteCar_Model.isselected = true;
        bottomsheet.dismissSheet();
        adapter.notifyItemChanged(selecteCar_Model.indexid);
        imgInfo.setVisibility(View.VISIBLE);

        //selectedCar_Mrk = m;
        updatePanel(vh, "onclick");
    }

    private void animateSlide(final boolean isShow) {

        if (infotopisclosed && !isShow) {
            return;
        }

        if (!infotopisclosed) {
            if (!isShow) {
                infotopisclosed = true;
                TranslateAnimation slide = new TranslateAnimation(0, 0, 0, -infoTopPanel.getHeight()); // seems you have a problem with 3rd param
                slide.setDuration(50);
                slide.setFillAfter(true);
                infoTopPanel.startAnimation(slide);
            }
            return;
        }


        infotopisclosed = true;
        TranslateAnimation slide = new TranslateAnimation(0, 0, 0, -infoTopPanel.getHeight()); // seems you have a problem with 3rd param
        slide.setDuration(50);
        slide.setFillAfter(true);
        slide.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (isShow) {
                    TranslateAnimation slide = new TranslateAnimation(0, 0, -infoTopPanel.getHeight(), 0); // seems you have a problem with 3rd param
                    slide.setDuration(1000);
                    slide.setFillAfter(true);
                    infotopisclosed = false;
                    infoTopPanel.startAnimation(slide);
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        infoTopPanel.startAnimation(slide);

    }


    private void deSelectMarker() {

        updatePanel(null, "reset");
        bottomsheet.dismissSheet();
        imgInfo.setVisibility(View.GONE);
        btnCommands.setVisibility(View.GONE);
        if (selecteCar_Model != null) {
            selecteCar_Model.isselected = false;
            adapter.notifyItemChanged(selecteCar_Model.indexid);

        }
        selectedCar_Mrk = null;
        selecteCar_Model = null;
        FollowCamera = "";
        animateSlide(false);
    }

    @OnClick(R.id.imgInfo)
    void onInfoClick(View v) {
        if (selectedCar_Mrk != null)
            ShowVehicleDetails((vts_vh_model) selectedCar_Mrk.getTag());
    }

    dialogCommands commaddialog;
    ProgressDialog progressDialog;
    Handler dialogdismisshandler;

    @OnClick(R.id.btnCommands)
    void onCommandClick(View v) {
        if (selectedCar_Mrk != null) {
            if (commaddialog == null) {

                progressDialog = new ProgressDialog(this);
                progressDialog.setMessage("Please wait...");
                progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                progressDialog.setTitle("Waiting for reply.");
                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progressDialog.setCancelable(false);
                commaddialog = new dialogCommands(this, lvsservice);
                commaddialog.setmCallback(new dialogCommands.OnDialogValuesListener() {
                    @Override
                    public void DialogValues(final String Message) {

                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                progressDialog.show();
                                Toast.makeText(main.this, Message, Toast.LENGTH_SHORT).show();
                                dialogdismisshandler = new Handler() {
                                    @Override
                                    public void handleMessage(Message message) {
                                        progressDialog.dismiss();
                                    }
                                };
                                dialogdismisshandler.sendMessageDelayed(new Message(), 10000);

                            }
                        });

                    }
                });
            }
            commaddialog.set_vh((vts_vh_model) selectedCar_Mrk.getTag());
            commaddialog.show();
        }
    }


    @OnClick(R.id.btnFleet)
    void onViewAllClick(View v) {
        if (adapter != null) {
            //mSlidLayout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
            deSelectMarker();
            adapter.filter(vh_rec_adapter.vhsts.all, "");
            adapter.updateCounter();
            showMarkersInMap();
            selectButton(stBtnAll);

        }
    }

    @BindView(R.id.btnDayNight)
    ImageView btnDayNight;

    @OnClick(R.id.btnDayNight)
    void onBtnDayNightClick(View v) {
        changeTheme(btnDayNight.getTag().toString());
    }

    /*Theme*/
    private void changeTheme(String theme) {
        switch (theme) {
            case "night": {
                boolean success = mMap.setMapStyle(
                        MapStyleOptions.loadRawResourceStyle(
                                this, R.raw.style_json));
                if (success) {
                    Log.e("Main", "Style parsing failed.");
                    btnDayNight.setTag("day");
                    btnDayNight.setImageResource(R.drawable.ic_night);
                    SHP.set(this, SHP.ids.mapselecTheme, "day");
                } else {
                    Toast.makeText(this, "Faild to change view", Toast.LENGTH_SHORT).show();
                }
            }
            break;
            case "day": {
                boolean success = mMap.setMapStyle(
                        MapStyleOptions.loadRawResourceStyle(
                                this, R.raw.style_black));
                if (success) {
                    Log.e("Main", "Style parsing failed.");
                    btnDayNight.setTag("night");
                    btnDayNight.setImageResource(R.drawable.ic_day);
                    SHP.set(this, SHP.ids.mapselecTheme, "night");
                } else {
                    Toast.makeText(this, "Faild to change view", Toast.LENGTH_SHORT).show();
                }

            }
            break;
        }
    }

    private void selectTheme(String theme) {
        switch (theme) {
            case "night": {
                boolean success = mMap.setMapStyle(
                        MapStyleOptions.loadRawResourceStyle(
                                this, R.raw.style_black));
                btnDayNight.setTag("night");
                btnDayNight.setImageResource(R.drawable.ic_day);
            }
            break;
            case "day": {
                boolean success = mMap.setMapStyle(
                        MapStyleOptions.loadRawResourceStyle(
                                this, R.raw.style_json));
                btnDayNight.setTag("day");
                btnDayNight.setImageResource(R.drawable.ic_night);
            }
            break;
        }
    }


    /*end theme*/


    private void bindActionBar() {
        // Inflate your custom layout
        final ViewGroup actionBarLayout = (ViewGroup) getLayoutInflater().inflate(
                R.layout.layout_actionbar,
                null);


        // Set up your ActionBar
        final android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setCustomView(actionBarLayout);
        actionBar.hide();
        // You customization
        final int actionBarColor = getResources().getColor(R.color.colorPrimary);
        actionBar.setBackgroundDrawable(new ColorDrawable(actionBarColor));

        Toolbar toolbar = (Toolbar) actionBar.getCustomView().getParent();
        toolbar.setContentInsetsAbsolute(0, 0);

        //action_bar_all = (Button) findViewById(R.id.action_bar_all);
        stBtnAll.setText("ALL-0");

        //action_bar_online = (Button) findViewById(R.id.action_bar_online);
        stBtnOnl.setText("ONL-0");

        //action_bar_offline = (Button) findViewById(R.id.action_bar_offline);
        stBtnOff.setText("OFF-0");

        //action_bar_moving = (Button) findViewById(R.id.action_bar_moving);
        stBtnIng.setText("SPD-0");

    }


    @Override
    public void onInfoWindowClick(Marker marker) {

        ShowVehicleDetails((vts_vh_model) marker.getTag());

    }

    //get vehicle details

    private void ShowVehicleDetails(vts_vh_model vhid) {
        getVehicleDetails(vhid);
    }

    private void ShowVehicleCommands(vts_vh_model vhid) {

    }


    private void apllyListViewItemClick() {

//        lstVeh.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//
//                vts_vh_model l = lvsservice.getVhs().get(position);
//
//
//            }
//        });
    }

    ///Google Map #######################################################################################
    private void googleMapInit() {
        SupportMapFragment mMap1 = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mMap1.getMapAsync(this);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;//get map object after ready
        mMap.setOnInfoWindowClickListener(this);
        mMap.setOnMarkerClickListener(this);
        //check permission

        String theme = SHP.get(this, SHP.ids.mapselecTheme, "night").toString();
        selectTheme(theme);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        LatLng l = new LatLng(22.861639, 78.257621);

        mMap.setMyLocationEnabled(false);
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.getUiSettings().setMapToolbarEnabled(false);
        mMap.setTrafficEnabled(true);
        mMap.setMaxZoomPreference(17);
        CameraPosition position1 = new CameraPosition.Builder().target(l)
                .zoom(5)
                .build();
        changeCameraPosition(position1, true);

        preCheckAndBindVehicles();

        //bindVehicles();
    }

    @Deprecated
    private void bindVehicles() {


        JsonObject json = new JsonObject();
        json.addProperty("flag", "vehicle");
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
                            vhs = (List<vts_vh_model>) gson.fromJson(result.get("data"), listType);
                            displayVehicles();

                        } catch (Exception ea) {
                            ea.printStackTrace();
                        }
                        // menu_refresh.setEnabled(false);

                    }
                });
    }

    private TimeZone tz = TimeZone.getTimeZone("GMT");
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

    Button btnTrackPop;

    private void getVehicleDetails(final vts_vh_model vh) {


        //vts_vh_model m = null;
//
//        try {
//            int i = vharr.indexOf(vh.vhid);
//            if (i == -1) return;
//            m = lvsservice.getVhs().get(i);
//
//        } catch (Exception ecx) {
//
//        }
//        final vts_vh_model vhm = m;

        String url = !Global.getLoginType(main.this, "normal") ?  Global.urls.getVehicleDetails_vts.value : Global.urls.getVehicleDetails.value;

        JsonObject json = new JsonObject();
        json.addProperty("flag", "vhdetail");
        json.addProperty("id", vh.id + "");
        json.addProperty("vhid", vh.vhid + "");
        Ion.with(this)
                .load(url)
                .setJsonObjectBody(json)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        // do stuff with the result or error
                        try {
                            if (result != null) {

                                //result.get("data")
                                JsonObject o = result.get("data").getAsJsonArray().get(0).getAsJsonObject();
                                final TextView txtAddress;
                                bottomsheet.showWithSheetView(LayoutInflater.from(main.this).inflate(R.layout.layout_vehicle_details, bottomsheet, false));
                                txtAddress = ((TextView) bottomsheet.findViewById(R.id.vhaddress));
                                txtAddress.setText("CLICK TO SHOW ADDRESS");
                                if (o.get("vehicleno") != JsonNull.INSTANCE)
                                    ((TextView) bottomsheet.findViewById(R.id.txtVhno)).setText(o.get("vehicleno").getAsString());

                                if (o.get("imei") != JsonNull.INSTANCE)
                                    ((TextView) bottomsheet.findViewById(R.id.txtimei)).setText(o.get("imei").getAsString());

                                if (o.get("vehregno") != JsonNull.INSTANCE)
                                    ((TextView) bottomsheet.findViewById(R.id.vhregno)).setText(o.get("vehregno").getAsString());

                                if (o.get("vehiclemake") != JsonNull.INSTANCE)
                                    ((TextView) bottomsheet.findViewById(R.id.vhmake)).setText(o.get("vehiclemake").getAsString());

                                if (o.get("vehiclemodel") != JsonNull.INSTANCE)
                                    ((TextView) bottomsheet.findViewById(R.id.vhmodel)).setText(o.get("vehiclemodel").getAsString());

                                if (o.get("capacity") != JsonNull.INSTANCE)
                                    ((TextView) bottomsheet.findViewById(R.id.vhcapacity)).setText(o.get("capacity").getAsString());

                                if (o.get("vehiclecondition") != JsonNull.INSTANCE)
                                    ((TextView) bottomsheet.findViewById(R.id.vhcondition)).setText(o.get("vehiclecondition").getAsString());

                                if (o.get("vehiclefacility") != JsonNull.INSTANCE)
                                    ((TextView) bottomsheet.findViewById(R.id.vhfacility)).setText(o.get("vehiclefacility").getAsString());

                                if (o.get("vhspeed") != JsonNull.INSTANCE)
                                    ((TextView) bottomsheet.findViewById(R.id.vhallwspd)).setText(o.get("vhspeed").getAsString());

                                if (o.get("simno") != JsonNull.INSTANCE)
                                    ((TextView) bottomsheet.findViewById(R.id.vhsimno)).setText(o.get("simno").getAsString());

                                if (vh != null) {

                                    if (vh.lstspd > 0) {
                                        ((TextView) bottomsheet.findViewById(R.id.vhlastviospd)).setText(vh.lstspd + "");

                                        try {

                                            dateFormat.setTimeZone(tz);
                                            Date date = dateFormat.parse(vh.lstspdtm);
                                            SimpleDateFormat dformatter = new SimpleDateFormat("dd MMM yyyy hh:mm:ss a");

                                            ((TextView) bottomsheet.findViewById(R.id.vhlastspdtm)).setText(dformatter.format(date));
                                        } catch (Exception ex) {

                                        }
                                    }
                                }
                                btnTrackPop = (Button) bottomsheet.findViewById(R.id.btnTrackPop);
                                btnTrackPop.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        carSelect(0, vh);
                                    }
                                });
                                txtAddress.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        String addr = "";
                                        try {
                                            addr = getAddress(vh.lat, vh.lon);
                                            txtAddress.setText(addr);
                                            txtAddress.setOnClickListener(null);
                                        } catch (Exception ex) {
                                            txtAddress.setText("Faild to get address");
                                        }
                                    }
                                });


                                //((TextView)bottomsheet.findViewById(R.id.vhsimno)).setText(o.get("createdon").toString());

                                bottomsheet.expandSheet();
                            }

                            ///Log.v("result", result.toString());


                        } catch (Exception ea) {
                            ea.printStackTrace();
                        }
                        // menu_refresh.setEnabled(false);

                    }
                });
    }

    private String getAddress(double lat, double lng) throws IOException {
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(this, Locale.getDefault());

        addresses = geocoder.getFromLocation(lat, lng, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5

        if (addresses.size() == 0) {
            return "";
        }

        String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
        String city = addresses.get(0).getLocality();
        String state = addresses.get(0).getAdminArea();
        String country = addresses.get(0).getCountryName();
        String knownName = addresses.get(0).getFeatureName();
        StringBuilder sb = new StringBuilder();
        sb.append(address);
        sb.append(",");
        sb.append(city);
        sb.append(",");
        sb.append(state);
        sb.append(",");
        sb.append(country);
        sb.append(",");
        sb.append(knownName);
        return sb.toString();
    }

    //inset new vehicle


    vh_rec_adapter adapter;
    private ClusterManager<ClusterMarkerItem> mClusterManager;

    @Deprecated
    private void displayVehicles() {
        //mClusterManager = new ClusterManager<ClusterMarkerItem>(this, mMap);

        if (lvsservice.getVhs().size() > 0) {
            mrkers = new ArrayList<>(lvsservice.getVhs().size());
//            for (int i = 0; i <= lvsservice.getVhs().size(); i++) {
//
//            }
            // findViewById(R.id.txtNodata).setVisibility(View.GONE);
            adapter = new vh_rec_adapter(lvsservice.getVhs(), getResources(), this);
            adapter.setVHOnStatusChangeListner(new VHOnStatusChangeListner() {
                @Override
                public void onCountChange(vts_vh_status_model vh) {
                    stBtnAll.setText("ALL-" + vh.getAll());
                    stBtnOnl.setText("ONL-" + vh.getOnline());
                    stBtnOff.setText("OFF-" + vh.getOffline());
                    stBtnIng.setText("IGN-" + vh.getMoving());
                    stBtnSPD.setText("SPD-" + vh.getSpeedvoi());
                }
            });


            lstVeh.setAdapter(adapter);
            adapter.notifyDataSetChanged();
            int arrsz = lvsservice.getVhs().size();
            vharr = new ArrayList<>(arrsz);


            for (int i = 0; i < arrsz; i++) {

                mrkers.add(i, null);
                vts_vh_model m = lvsservice.getVhs().get(i);

                vharr.add(i, m.vhid);


            }

            strVehicles = TextUtils.join("\",\"", vharr);
            strVehicles = "\"" + strVehicles + "\"";

            //strVehicles = TextUtils.join(",", vharr);
            ////getLastStatus();

            //    mSocket.on("msgd", onNewMessage);


//            final Handler handler = new Handler();
//            handler.postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    //Do something after 100ms
//                    mSocket.connect();
//                }
//            }, 1500);

        } else {
            // findViewById(R.id.txtNodata).setVisibility(View.VISIBLE);
        }

    }


    private void displayVehiclesNew() {
        //mClusterManager = new ClusterManager<ClusterMarkerItem>(this, mMap);

        if (lvsservice.getVhs().size() > 0 && mrkers == null) {
            mrkers = new ArrayList<>(lvsservice.getVhs().size());
//            for (int i = 0; i <= lvsservice.getVhs().size(); i++) {
//
//            }
            // findViewById(R.id.txtNodata).setVisibility(View.GONE);
            adapter = new vh_rec_adapter(lvsservice.getVhs(), getResources(), this);
            adapter.setVHOnStatusChangeListner(new VHOnStatusChangeListner() {
                @Override
                public void onCountChange(vts_vh_status_model vh) {
                    stBtnAll.setText("ALL-" + vh.getAll());
                    stBtnOnl.setText("ONL-" + vh.getOnline());
                    stBtnOff.setText("OFF-" + vh.getOffline());
                    stBtnIng.setText("IGN-" + vh.getMoving());
                    stBtnSPD.setText("SPD-" + vh.getSpeedvoi());
                }
            });


            lstVeh.setAdapter(adapter);
            adapter.notifyDataSetChanged();
            int arrsz = lvsservice.getVhs().size();
            vharr = new ArrayList<>(arrsz);

            long time = System.currentTimeMillis();

            for (int i = 0; i < arrsz; i++) {
                vts_vh_model vhm = lvsservice.getVhs().get(i);
                //int k = vharr.indexOf(vhm.vhid);
                LatLng l = null;
                if (vhm.loc != null && vhm.loc.length > 0) {

                    l = new LatLng(vhm.loc[1], vhm.loc[0]);
                } else {
                    l = new LatLng(0, 0);
                }

                mrkers.add(i, null);
                if (vhm.islststsAvail) {
                    Marker m = mMap.addMarker(
                            new MarkerOptions()
                                    .position(l)
                                    .title(vhm.vno)
                                    .icon(BitmapDescriptorFactory.fromResource(normalCar))
                                    .anchor(0.5f, 0.5f)

                    );

                    //
                    m.setTag(vhm);
                    mrkers.set(i, m);
                    vhm.vhmarker = m;
                    if (vhm.bearing != null) {
                        rotateMarker(m, vhm.bearing.floatValue());
                        //m.setRotation(vhm.bearing.floatValue());
                    }


//                    vharr.add(i, vhm.vhid);
                }
                showMarkersInMap();


            }


            strVehicles = TextUtils.join("\",\"", vharr);
            strVehicles = "\"" + strVehicles + "\"";

            //strVehicles = TextUtils.join(",", vharr);
            ////getLastStatus();

            //    mSocket.on("msgd", onNewMessage);


//            final Handler handler = new Handler();
//            handler.postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    //Do something after 100ms
//                    mSocket.connect();
//                }
//            }, 1500);

        } else {
            // findViewById(R.id.txtNodata).setVisibility(View.VISIBLE);
        }

    }

    Button selectedButton;

    private void selectButton(Button btn) {
        if (selectedButton != null) {
            selectedButton.setBackgroundResource(R.drawable.theme_button_light);
        }
        selectedButton = btn;
        selectedButton.setBackgroundResource(R.drawable.theme_button_light_selected);
    }


    private void updateList(JSONObject _ld) {


        try {
            String vhid = _ld.getString("vhid");
            int i = vharr.indexOf(vhid);
            if (i == -1) return;
            vts_vh_model m = lvsservice.getVhs().get(i);
            m.sertm = _ld.getString("sertm");
            switch (_ld.getString("actvt")) {
                case "hrtbt": {
                    m.acc = _ld.getString("acc");
                    m.btr = _ld.getString("btr");
                    m.gsmsig = _ld.getString("gsmsig");
                    m.btrst = _ld.getString("btrst");
                    adapter.notifyItemChanged(i);
                }
                break;
                case "loc": {

                    LatLng lastLoc = null;


                    if (m.lon != null && m.lon != 0) {
                        lastLoc = new LatLng(m.lat, m.lon);
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

                    adapter.notifyItemChanged(i);
                    Marker _m = null;

                    if (i <= mrkers.size() - 1) {
                        _m = mrkers.get(i);
                    }

                    Double bearing = m.bearing;
                    if (lastLoc != null) {
                        LatLng currentLoc = new LatLng(m.lat, m.lon);
                        bearing = bearingBetweenLocations(lastLoc, currentLoc);
                    }
                    if (_m != null)
                        moveMarker(vhid, _m, m.lat, m.lon, bearing, m);


                    if (FollowCamera.equals(vhid)) {
                        updatePanel(m, "onchange");
                    }

                }
                break;
                case "evt": {

                }
                break;
                case "login": {
                    adapter.notifyItemChanged(i);
                }
                break;
            }


        } catch (
                JSONException e)

        {
            e.printStackTrace();
        }

    }


    private void Event(String evt, vts_vh_model m, int pos) {
        switch (evt) {
            case "d1":
            case "dd": {
                if (FollowCamera.equals(m.vhid)) {
                    updatePanel(m, "onchange");
                }
                adapter.notifyItemChanged(pos);
            }

            break;
        }
    }


    private void moveMarker(String vhid, Marker m, Double lat, Double lon, Double bearing, vts_vh_model vtsm) {
        LatLng l = new LatLng(lat, lon);
        rotateMarker(m, bearing.floatValue());

        LatLngInterpolator latLngInterpolator = new LatLngInterpolator.Spherical();
        MarkerAnimation.animateMarker(m, l, latLngInterpolator);
        if (FollowCamera.equals(vhid)) {
            CameraPosition position1 =
                    new CameraPosition.Builder().target(l)
                            .zoom(17)
                            .build();
            changeCameraPosition(position1, true);

        }
    }

    vts_vh_model selVh;

    private void updatePanel(vts_vh_model vh, String type) {
        if (type.equals("reset")) {
            txtSpeed.setText("0 km/h");
            txtVhtitle.setText("select vehicle");
            imgDotsts.setImageDrawable(null);
            imgacc.setImageDrawable(null);
            imgoil.setImageDrawable(null);
            imgacc.setBackgroundDrawable(null);
            imgbattry.setImageDrawable(null);
            imgnetwork.setImageDrawable(null);
            txtSpd.setTextColor(getResources().getColor(R.color.colordisable));
            txtD1.setTextColor(getResources().getColor(R.color.colordisable));
            selVh = null;
            animateSlide(false);
            return;
        }

        selVh = vh;
        if (type.equals("onclick"))
            animateSlide(true);
        txtSpeed.setText(vh.speed + " km/h");
        txtVhtitle.setText(vh.vno);

        if (vh.olsts == 1) {
            imgDotsts.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_sts_on));
        } else if (vh.olsts == 2) {
            imgDotsts.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_sts_off));
        } else {
            imgDotsts.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_sts_off));
        }

        imgacc.setImageDrawable(null);
        imgacc.setBackgroundDrawable(getResources().getDrawable(deviceUtils.getAcc(vh.acc)));

        if (vh.btr != null) {
            imgbattry.setImageDrawable(null);
            if (vh.btrst.equals("CHRG")) {
                imgbattry.setImageDrawable(getResources().getDrawable(deviceUtils.getBatrry(vh.btrst)));
            } else {
                imgbattry.setImageDrawable(getResources().getDrawable(deviceUtils.getBatrry(vh.btr)));
            }
        }

        if (vh.gsmsig != null) {
            imgnetwork.setImageDrawable(null);
            imgnetwork.setImageDrawable(getResources().getDrawable(deviceUtils.getNetwork(vh.gsmsig)));
        }

        if (vh.isp) {
            txtSpd.setTextColor(getResources().getColor(R.color.colorHolo));
        } else {
            txtSpd.setTextColor(getResources().getColor(R.color.colordisable));
        }

        txtD1.setPaintFlags(0);
        if (vh.extra != null && vh.extra.d1str != null && !vh.extra.d1str.equals("")) {
            txtD1.setText(vh.extra.d1str);
            if (vh.d1 == 1) {
                txtD1.setTextColor(getResources().getColor(R.color.sts_ol));
            } else {
                txtD1.setTextColor(getResources().getColor(R.color.colordisable));
            }
        } else {
            txtD1.setTextColor(getResources().getColor(R.color.colordisable));
            txtD1.setText("AC");
            txtD1.setPaintFlags(txtD1.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }

        if (vh.oe == 1) {
            imgoil.setImageDrawable(getResources().getDrawable(R.drawable.ic_oil_discon));
        } else {
            imgoil.setImageDrawable(getResources().getDrawable(R.drawable.ic_oil_con));

        }


    }

    private void changeCameraPosition(CameraPosition cameraPosition, boolean animate) {
        CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition);

        if (animate) {
            mMap.animateCamera(cameraUpdate);
        } else {
            mMap.moveCamera(cameraUpdate);
        }

    }


//    private void getLastStatus() {
//
//        JsonParser jsonParser = new JsonParser();
//
//
//        JsonObject json = new JsonObject();
//        json.add("vhids", jsonParser.parse("[" + strVehicles + "]"));
//        Ion.with(this)
//                .load(Global.urls.getvahicleupdates.value)
//                .setJsonObjectBody(json)
//                .asJsonObject()
//                .setCallback(new FutureCallback<JsonObject>() {
//                    @Override
//                    public void onCompleted(Exception e, JsonObject result) {
//                        // do stuff with the result or error
//                        try {
//                            if (result != null) Log.v("result", result.toString());
//                            // JSONObject jsnobject = new JSONObject(jsond);
//                            Gson gson = new Gson();
//                            Type listType = new TypeToken<List<vts_vh_model>>() {
//                            }.getType();
//                            List<vts_vh_model> mod = (List<vts_vh_model>) gson.fromJson(result.get("data"), listType);
//                            updateLastStatus(mod);
//
//                        } catch (Exception ea) {
//                            ea.printStackTrace();
//                        }
//                        // menu_refresh.setEnabled(false);
//
//                    }
//                });
//
//    }
//
//
//    private void updateLastStatus(List<vts_vh_model> mod) {
//
//        int size = mod.size();
//        int selcpos = -1;
//        String zoomid = SHP.get(this, SHP.ids.selectedvh, "").toString();
//        SHP.set(this, SHP.ids.selectedvh, "");
//        for (int i = 0; i < size; i++) {
//
////        }
////        for (vts_vh_model vhm : mod) {
//            vts_vh_model vhm = mod.get(i);
//
//            int k = vharr.indexOf(vhm.vhid);
//
//
//            if (k > -1) {
//                vts_vh_model a = lvsservice.getVhs().get(k);
//                Log.v("test- ere", k + " " + a.vno);
//                a.acc = vhm.acc;
//                a.gsmsig = vhm.gsmsig;
//                a.btr = vhm.btr;
//                a.actvt = vhm.actvt;
//                a.btrst = vhm.btrst;
//                a.sertm = vhm.sertm;
//                a.gpstm = vhm.gpstm;
//                a.speed = vhm.speed;
//                a.alwspeed = vhm.alwspeed;
//                a.lstspd = vhm.lstspd;
//                a.lstspdtm = vhm.lstspdtm;
//                a.d1 = vhm.d1;
//
//                LatLng l = null;
//                if (vhm.loc != null && vhm.loc.length > 0) {
//                    a.lat = vhm.loc[1];
//                    a.lon = vhm.loc[0];
//
//                    l = new LatLng(a.lat, a.lon);
//                } else {
//                    l = new LatLng(0, 0);
//                }
//
//                Marker m = mMap.addMarker(
//                        new MarkerOptions()
//                                .position(l)
//                                .title(vhm.vno)
//                                .icon(BitmapDescriptorFactory.fromResource(normalCar))
//                                .anchor(0.5f, 0.5f)
//                        //                .flat(true)
//                );
//
//
//                m.setTag(a);
//                mrkers.set(k, m);
//                a.vhmarker = m;
//                if (vhm.bearing != null) {
//                    rotateMarker(m, vhm.bearing.floatValue());
//                    //m.setRotation(vhm.bearing.floatValue());
//                }
//                if (zoomid.equals(vhm.vhid)) {
//                    selcpos = k;
//                }
//
//                m.showInfoWindow();
//
////                ClusterMarkerItem offsetItem = new ClusterMarkerItem(l.latitude, l.longitude);
////                mClusterManager.addItem(offsetItem);
//            }
//
//        }
//
//        if (selcpos != -1) {
//            carSelect(selcpos, null);
//        } else {
//            showMarkersInMap();
//        }
//
//        adapter.notifyDataSetChanged();
//    }

    private void showMarkersInMap() {
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        boolean isicons = false;
        for (Marker m : mrkers) {
            if (m != null) {
                isicons = true;
                //d\ad\\\
                builder.include(m.getPosition());
            }
        }
        if (isicons) {
            /**initialize the padding for map boundary*/
            int padding = 50;
            /**create the bounds from latlngBuilder to set into map camera*/

            LatLngBounds bounds = builder.build();
            /**create the camera with bounds and padding to set into map*/
            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
            mMap.animateCamera(cu);
        }
    }


//    private Emitter.Listener onNewMessage = new Emitter.Listener() {
//        @Override
//        public void call(Object... args) {
//            JSONObject data = (JSONObject) args[0];
//            Decoder(data);
//        }
//    };
//
//
//    private void Decoder(JSONObject data) {
//        String evt = null;
//        try {
//            evt = data.getString("evt");
//            switch (evt) {
//                case "data": {
//                    final JSONObject _ld = data.getJSONObject("data");
//                    main.this.runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            updateList(_ld);
//                        }
//                    });
//
//                }
//                break;
//                case "regreq": {
////                    main.this.runOnUiThread(new Runnable() {
////                        @Override
////                        public void run() {
//                    if (lvsservice.getVhs().size() > 0) {
//                        mSocket.emit("reg_v", strVehicles.replace("\"", ""));
//                    }
////                        }
////                    });
//
//                }
//                break;
//                case "registered": {
//                    main.this.runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            Toast.makeText(main.this, "Registered", Toast.LENGTH_SHORT).show();
//                        }
//                    });
//
//                }
//                break;
//            }
//
//
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//    }
//
//
//    private Socket mSocket;

//    {
//        try {
//            mSocket = IO.socket(Global.VTS_URL);
//        } catch (URISyntaxException e) {
//        }
//    }

    boolean isMarkerRotating = false;
    final Handler handler = new Handler();

    private void rotateMarker(final Marker marker, final float toRotation) {
        if (!isMarkerRotating) {

            final long start = SystemClock.uptimeMillis();
            final float startRotation = marker.getRotation();
            final long duration = 1000;

            final Interpolator interpolator = new LinearInterpolator();

            handler.post(new Runnable() {
                @Override
                public void run() {
                    isMarkerRotating = true;

                    long elapsed = SystemClock.uptimeMillis() - start;
                    float t = interpolator.getInterpolation((float) elapsed / duration);

                    float rot = t * toRotation + (1 - t) * startRotation;

                    marker.setRotation(-rot > 180 ? rot / 2 : rot);
                    if (t < 1.0) {
                        handler.postDelayed(this, 16);
                    } else {
                        isMarkerRotating = false;
                    }
                }
            });
        }
    }


    private void animateMarkerNew(final LatLng endPosition, final Marker marker, final String vhid, final vts_vh_model vtsm) {


        if (marker != null) {
//            if (vtsm.markerData.isinanimation) {
//                vtsm.markerData.markers.add(endPosition);
//                return;
//            }
//
//

//            vtsm.markerData.isinanimation = true;
            final LatLng startPosition = marker.getPosition();
            float bearing = getBearing(startPosition, new LatLng(endPosition.latitude, endPosition.longitude));
            rotateMarker(marker, bearing);
            //final LatLng endPosition = new LatLng(destination.getLatitude(), destination.getLongitude());

            final float startRotation = marker.getRotation();
            final LatLngInterpolatorNew latLngInterpolator = new LatLngInterpolatorNew.LinearFixed();

            ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 1);
            valueAnimator.setDuration(3000); // duration 3 second
            valueAnimator.setInterpolator(new LinearInterpolator());
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    try {


                        float v = animation.getAnimatedFraction();
                        LatLng newPosition = latLngInterpolator.interpolate(v, startPosition, endPosition);
                        marker.setPosition(newPosition);
                        Log.v("animatore : ", newPosition.latitude + " - " + newPosition.longitude);
                        if (FollowCamera.equals(vhid)) {
                            mMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder()
                                    .target(newPosition)
                                    .zoom(15.5f)
                                    .build()));
                        }

                        //
                        marker.setRotation(getBearing(startPosition, new LatLng(endPosition.latitude, endPosition.longitude)));
                    } catch (Exception ex) {
                        //I don't care atm..
                    }
                }
            });
            valueAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
//                    vtsm.markerData.isinanimation = false;
//                    if(vtsm.markerData.markers.size() > 0){
//
//                        animateMarkerNew(vtsm.markerData.markers.get(0),marker,vhid, vtsm);
//                        vtsm.markerData.markers.remove(0);
//
//
//                    }

                    // if (mMarker != null) {
                    // mMarker.remove();
                    // }
                    // mMarker = googleMap.addMarker(new MarkerOptions().position(endPosition).icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_car)));

                }
            });
            valueAnimator.start();
        }
    }

    //Method for finding bearing between two points
    private float getBearing(LatLng begin, LatLng end) {
        double lat = Math.abs(begin.latitude - end.latitude);
        double lng = Math.abs(begin.longitude - end.longitude);

        if (begin.latitude < end.latitude && begin.longitude < end.longitude)
            return (float) (Math.toDegrees(Math.atan(lng / lat)));
        else if (begin.latitude >= end.latitude && begin.longitude < end.longitude)
            return (float) ((90 - Math.toDegrees(Math.atan(lng / lat))) + 90);
        else if (begin.latitude >= end.latitude && begin.longitude >= end.longitude)
            return (float) (Math.toDegrees(Math.atan(lng / lat)) + 180);
        else if (begin.latitude < end.latitude && begin.longitude >= end.longitude)
            return (float) ((90 - Math.toDegrees(Math.atan(lng / lat))) + 270);
        return -1;
    }

    private interface LatLngInterpolatorNew {
        LatLng interpolate(float fraction, LatLng a, LatLng b);

        class LinearFixed implements LatLngInterpolatorNew {
            @Override
            public LatLng interpolate(float fraction, LatLng a, LatLng b) {
                double lat = (b.latitude - a.latitude) * fraction + a.latitude;
                double lngDelta = b.longitude - a.longitude;
                // Take the shortest path across the 180th meridian.
                if (Math.abs(lngDelta) > 180) {
                    lngDelta -= Math.signum(lngDelta) * 360;
                }
                double lng = lngDelta * fraction + a.longitude;
                return new LatLng(lat, lng);
            }
        }
    }


    private double bearingBetweenLocations(LatLng latLng1, LatLng latLng2) {

        double PI = 3.14159;
        double lat1 = latLng1.latitude * PI / 180;
        double long1 = latLng1.longitude * PI / 180;
        double lat2 = latLng2.latitude * PI / 180;
        double long2 = latLng2.longitude * PI / 180;

        double dLon = (long2 - long1);

        double y = Math.sin(dLon) * Math.cos(lat2);
        double x = Math.cos(lat1) * Math.sin(lat2) - Math.sin(lat1)
                * Math.cos(lat2) * Math.cos(dLon);

        double brng = Math.atan2(y, x);

        brng = Math.toDegrees(brng);
        brng = (brng + 360) % 360;

        return brng;
    }

    private void checkPermission() {
        int PERMISSION_ALL = 1;
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
    public void onBackPressed() {

        if (mSlidLayout.getPanelState().equals(SlidingUpPanelLayout.PanelState.EXPANDED)) {
            mSlidLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
            return;
        }
        if (bottomsheet.getState().equals(BottomSheetLayout.State.EXPANDED)) {
            bottomsheet.dismissSheet();
            return;
        }
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (realm != null) {
            realm.close();
            realm = null;
        }
        if (lvsservice != null) {
            lvsservice.setOnVehiclesReadyListner(null);
            lvsservice.setOnVehicleModelChangeListner(null);
            lvsservice = null;
        }
        deSelectMarker();
        if (adapter != null) {
            adapter.killAll();
            adapter = null;
        }

//        if (mSocket != null) {
//
//            mSocket.disconnect();
//            mSocket.off("msgd", onNewMessage);
//
//        }

        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);

    }

    @Override
    public void onResume() {
        super.onResume();
        if (lvsservice != null) {
            if (lvsservice.getVhs() == null && lvsservice != null && lvsservice.getVhs().size() > 0 && isModelchangeAssign) {
                //vhs = new ArrayList<vts_vh_model>(lvsservice.getVhs());
                adapter.notifyDataSetChanged();
            }
        }

        new Checker(this).pass(new Checker.Pass() {
            @Override
            public void pass() {
                // bindVehicles();
                //checkExtras();
            }

        }).check(Checker.Resource.NETWORK);


    }

    private String zoomvhid = "";

    private void checkExtras() {
        Bundle b = getIntent().getExtras();
        if (b != null) {
            if (b.getString("flag").equals("zoom")) {
                zoomvhid = b.getString("vhid");
            }


        }
    }


    @Override
    public boolean onMarkerClick(Marker marker) {
        ShowVehicleDetails((vts_vh_model) marker.getTag());

        return false;
    }

    //service connecter
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

    boolean mBound = false;
    livesocketService lvsservice;
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
            lvsservice.setOnVehiclesReadyListner(main.this);
            if (vhs == null) {
                //vhs = new ArrayList<vts_vh_model>(lvsservice.getVhs());
                vhs = lvsservice.getVhs();
            }

            preCheckAndBindVehicles();


        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };

    @Override
    public void onChange(final vts_vh_model vh, final int position, final String evt) {
//        vts_vh_model vs = lvsservice.getVhs().get(position);
//        copyVaues(vh, vs);
        runOnUiThread(new Runnable() {

            @Override
            public void run() {

                // Stuff that updates the UI
                if (adapter != null) {
                    adapter.notifyItemChanged(position);
                    uichanger(vh, position, evt);
                }

            }
        });

    }

    Double _CalcBearing = 0.0;

    private void uichanger(final vts_vh_model m, final int position, String evt) {
        switch (evt) {
            case "hrtbt": {

            }
            break;
            case "loc": {
                LatLng lastLoc = m.Lastloc;

                Marker _m = null;
                if (position <= mrkers.size() - 1) {
                    _m = mrkers.get(position);
                }
                final Marker _mm = _m;
                _CalcBearing = 0.0;
                if (lastLoc != null) {
                    LatLng currentLoc = new LatLng(m.lat, m.lon);
                    _CalcBearing = bearingBetweenLocations(lastLoc, currentLoc);
                }
                if (_mm != null) {

                    main.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            moveMarker(m.vhid, _mm, m.lat, m.lon, _CalcBearing, m);
                        }
                    });
                }


                if (FollowCamera.equals(m.vhid)) {
                    updatePanel(m, "onchange");
                }

            }
            break;
            case "login": {

            }
            break;
            default: {
                Event(evt, m, position);

            }
            break;
        }
    }


    @Override
    public void onReady(List<vts_vh_model> vh) {
        if (vhs == null) {
            //vhs = new ArrayList<vts_vh_model>(lvsservice.getVhs());
            vhs = lvsservice.getVhs();
        }
        preCheckAndBindVehicles();
    }

    boolean isModelchangeAssign = false;

    void preCheckAndBindVehicles() {
        if (lvsservice != null) {
            if (lvsservice.getVhs() != null && mMap != null && lvsservice.isVhReady) {
                if (!isModelchangeAssign) {
                    displayVehiclesNew();
                    lvsservice.setOnVehicleModelChangeListner(main.this);
                    isModelchangeAssign = true;
                }
            }
        }
    }


    private void copyVaues(vts_vh_model source, vts_vh_model destination) {
        destination.acc = source.acc;
        destination.gsmsig = source.gsmsig;
        destination.btr = source.btr;
        destination.actvt = source.actvt;
        destination.btrst = source.btrst;
        destination.sertm = source.sertm;
        destination.gpstm = source.gpstm;
        destination.speed = source.speed;
        destination.alwspeed = source.alwspeed;
        destination.lstspd = source.lstspd;
        destination.lstspdtm = source.lstspdtm;
        destination.oe = source.oe;
        destination.d1 = source.d1;
        destination.lat = source.lat;
        destination.lon = source.lon;
        destination.islststsAvail = true;
        destination.loc = source.loc;
        destination.bearing = source.bearing;
        destination.isp = source.isp;

    }

    private void registerLocalBroadCast() {
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter("command_reply"));
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            String message = intent.getStringExtra("message");
            Log.d("receiver", "Got message: " + message);
            if (dialogdismisshandler != null) {
                dialogdismisshandler.removeCallbacks(null);
            }
            progressDialog.dismiss();

        }
    };

}
