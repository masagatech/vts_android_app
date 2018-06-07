package com.goyo.tracking.track.settings;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.flipboard.bottomsheet.BottomSheetLayout;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.goyo.tracking.track.R;
import com.goyo.tracking.track.adapters.vh_settings_adapter;
import com.goyo.tracking.track.globals.Global;
import com.goyo.tracking.track.main;
import com.goyo.tracking.track.realmmodel.vehiclesettings_model;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;

public class VehicleSettings extends AppCompatActivity {

    vh_settings_adapter adapter;

    Realm realm;

    @BindView(R.id.bottomsheet)
    BottomSheetLayout bottomsheet;

    @BindView(R.id.lstVehicles)
    RecyclerView recyclerView;

    @BindView(R.id.chkSelectAll)
    CheckBox chkSelectAll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehicle_settings);
        final android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        ButterKnife.bind(this);

        setTitle("Settings");
        realm = Realm.getDefaultInstance();

        bindvehicles();

        chkSelectAll.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                    adapter.selectAll(realm);
                else
                    adapter.desSelectAll(realm);
            }
        });
    }

    private void bindvehicles() {

        adapter = new vh_settings_adapter(realm.where(vehiclesettings_model.class).findAll());

        adapter.setOnItemClickLitner(new vh_settings_adapter.onItemClickLitner() {
            @Override
            public void onClick(String vhid) {
                getVehicleDetails(vhid);
            }
        });

        adapter.setOnSpeedAlertListner(new vh_settings_adapter.onSpeedAlertListner() {
            @Override
            public void onClick(final vehiclesettings_model vh, final boolean checked) {
                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        vehiclesettings_model m = realm.where(vehiclesettings_model.class).equalTo("vhId", vh.getVhId()).findFirst();
                        m.setSpeedAlert(checked);

                    }
                });
                if (checked) {
                    FirebaseMessaging.getInstance().subscribeToTopic("speed_" + vh.getImei());
                } else {
                    FirebaseMessaging.getInstance().unsubscribeFromTopic("speed_" + vh.getImei());
                }
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

//        TouchHelperCallback touchHelperCallback = new TouchHelperCallback();
//        ItemTouchHelper touchHelper = new ItemTouchHelper(touchHelperCallback);
//        touchHelper.attachToRecyclerView(recyclerView);

    }


    private void getVehicleDetails(String vhid) {


        JsonObject json = new JsonObject();
        json.addProperty("flag", "vhdetail");
        json.addProperty("id", vhid);
        Ion.with(this)
                .load(Global.urls.getVehicleDetails.value)
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


                                bottomsheet.showWithSheetView(LayoutInflater.from(VehicleSettings.this).inflate(R.layout.layout_vehicle_details, bottomsheet, false));

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

    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {

            case android.R.id.home:
                if (bottomsheet.getState().equals(BottomSheetLayout.State.EXPANDED) || bottomsheet.getState().equals(BottomSheetLayout.State.PEEKED)) {
                    bottomsheet.dismissSheet();
                    return false;
                }
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }


    }


    @Override
    public void onBackPressed() {
        if (bottomsheet.getState().equals(BottomSheetLayout.State.EXPANDED) || bottomsheet.getState().equals(BottomSheetLayout.State.PEEKED)) {
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
    }
}
