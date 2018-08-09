package com.goyo.tracking.tracking.forms;

import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.borax12.materialdaterangepicker.date.DatePickerDialog;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.goyo.tracking.tracking.R;
import com.goyo.tracking.tracking.adapters.fuel_list_adapter;
import com.goyo.tracking.tracking.adapters.pop_vh_adapter;
import com.goyo.tracking.tracking.globals.Global;
import com.goyo.tracking.tracking.model.fuel_modal;
import com.goyo.tracking.tracking.model.vts_vh_model;
import com.goyo.tracking.tracking.service.livesocketService;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class viewfuelentry extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    @BindView(R.id.lstFuelEntry)
    RecyclerView lstFuelEntry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewfuelentry);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Fuel Entry");
        ButterKnife.bind(this);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_fuel_view, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.addfuel:
                Intent i = new Intent(this, addfuelentry.class);
                startActivity(i);
                return true;
            case R.id.calender:
                toggleCalender();
                return true;
            case R.id.vehicle:
                //calling logout api
                bindVehicles();
                return true;
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }


    }


    private void bindVehicles() {


        if (dialogOut == null) {
            dialogOut = new Dialog(this);
            dialogOut.setContentView(R.layout.layout_vehiclelist);
            lstVehicles = (ListView) dialogOut.findViewById(R.id.lstVehicles);
            ((LinearLayout) dialogOut.findViewById(R.id.buttonTops)).setVisibility(View.VISIBLE);
            ((LinearLayout) dialogOut.findViewById(R.id.buttonBottom)).setVisibility(View.VISIBLE);
            pop_spinner = (ProgressBar) dialogOut.findViewById(R.id.pop_spinner);
            Button cancel = (Button) dialogOut.findViewById(R.id.btnCancel);
            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialogOut.dismiss();
                }
            });
            Button ok = (Button) dialogOut.findViewById(R.id.btnOk);
            ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Toast.makeText(reportmilege.this , lspopadapter.getCheckdVehicle() + "", Toast.LENGTH_SHORT).show();
                    initCalenderEvents();
                    dialogOut.dismiss();

                }
            });
            Button btnDeslectAll = (Button) dialogOut.findViewById(R.id.btnDeslectAll);
            btnDeslectAll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    for (int i = 0; i < vhs.size(); i++) {
                        vhs.get(i).ischeck = false;
                    }
                    lspopadapter.notifyDataSetChanged();
                }
            });
            Button btnselectAll = (Button) dialogOut.findViewById(R.id.btnselectAll);
            btnselectAll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    for (int i = 0; i < vhs.size(); i++) {
                        vhs.get(i).ischeck = true;
                    }
                    lspopadapter.notifyDataSetChanged();
                }
            });
            dialogOut.setCancelable(true);

            lstVehicles.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    vts_vh_model m = (vts_vh_model) parent.getItemAtPosition(position);
                    m.ischeck = !m.ischeck;
                    lspopadapter.notifyDataSetChanged();
                }
            });
        }
        dialogOut.show();


        pop_spinner.setVisibility(View.VISIBLE);

        bindList();

    }


    private void bindList() {
        if (lstVehicles == null || vhs == null) {
            return;
        }
        pop_spinner.setVisibility(View.GONE);
        if (lspopadapter == null) {
            lspopadapter = new pop_vh_adapter(viewfuelentry.this, vhs, viewfuelentry.this.getResources());
            lspopadapter.multicheck = true;
            lstVehicles.setAdapter(lspopadapter);
            lspopadapter.notifyDataSetChanged();
        }

    }


    private boolean mAutoHighlight;
    DatePickerDialog dpd;

    private void initCalenderEvents() {
        //set title on calendar scroll
        if (dpd == null) {
            Calendar now = Calendar.getInstance();
            dpd = com.borax12.materialdaterangepicker.date.DatePickerDialog.newInstance(
                    viewfuelentry.this,
                    now.get(Calendar.YEAR),
                    now.get(Calendar.MONTH),
                    now.get(Calendar.DAY_OF_MONTH)
            );
            dpd.setAutoHighlight(mAutoHighlight);
            now.add(Calendar.DAY_OF_MONTH, -allowDays);
            dpd.setMinDate(now);
        }
        dpd.show(getFragmentManager(), "Datepickerdialog");
    }

    boolean shouldShow = false;

    private void toggleCalender() {
        shouldShow = !shouldShow;
        initCalenderEvents();
    }

    String _frmDt = "", _toDate = "";

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth,
                          int yearEnd, int monthOfYearEnd, int dayOfMonthEnd) {

        _frmDt = year + "-" + String.format("%02d", (++monthOfYear)) + "-" + String.format("%02d", dayOfMonth);
        _toDate = yearEnd + "-" + String.format("%02d", (++monthOfYearEnd)) + "-" + String.format("%02d", dayOfMonthEnd);
        //"2017-11-11T00:00:00+05:30";

        validateReport();
//        Toast.makeText(reportmilege.this, date, Toast.LENGTH_SHORT).show();

    }

    List<vts_vh_model> vhs;
    Dialog dialogOut;
    ListView lstVehicles;
    pop_vh_adapter lspopadapter;
    String[] vhidArr;
    ProgressBar pop_spinner;

    @BindView(R.id.progressBar)
    ProgressBar prgloading;
    int allowDays = 60;

    private void validateReport() {
        if (lspopadapter == null || lspopadapter.getCheckdVehicle().length == 0) {
            Toast.makeText(viewfuelentry.this, "Please select vehicles to view entries", Toast.LENGTH_SHORT).show();
            //bindVehicles();
            return;
        }

        if (_frmDt.equals("")) {
            Toast.makeText(viewfuelentry.this, "Please select from date", Toast.LENGTH_SHORT).show();
            return;
        }

        if (_toDate.equals("")) {
            Toast.makeText(viewfuelentry.this, "Please select to date", Toast.LENGTH_SHORT).show();
            return;
        }


        prgloading.setVisibility(View.VISIBLE);
        bindFuelEntry();
    }

    private void bindFuelEntry() {

        JsonObject o = new JsonObject();
        o.addProperty("flag", "all");
        o.addProperty("uid", Global.loginusr.getDriverid() + "");
        o.addProperty("utype", Global.loginusr.getUtype());
        o.addProperty("frmdt", _frmDt);
        o.addProperty("todt", _toDate);
        String strvh = TextUtils.join(",", lspopadapter.getCheckdVehicleVTSId());
        o.addProperty("vhids", "{" + strvh + "}");

//        o.addProperty("vhname", vhnm);
//
//
//        JsonParser parser = new JsonParser();
//        String strvh = TextUtils.join(",", lspopadapter.getCheckdVehicleVTSId());
//
//        String query = "{\"vtsid\": {\"$in\":[" + strvh + "]}, \"date\": {\"$gte\": \"" + _frmDt + "\" , \"$lte\":\"" + _toDate + "\" }}";
//        JsonObject params = null;
//        params = parser.parse(query).getAsJsonObject();


        Ion.with(this)
                .load(Global.urls.getFuelEntry.value)
                .setJsonObjectBody(o)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        // do stuff with the result or error
                        try {
                            if (result != null) Log.v("result", result.toString());
                            Gson gson = new Gson();
                            Type listType = new TypeToken<List<fuel_modal>>() {
                            }.getType();
                            List<fuel_modal> fuleentry = (List<fuel_modal>) gson.fromJson(result.get("data"), listType);
                            bindFuels(fuleentry);

                        } catch (Exception ea) {
                            ea.printStackTrace();
                        }
                        // menu_refresh.setEnabled(false);
                        prgloading.setVisibility(View.GONE);
                    }

                });
    }

    fuel_list_adapter adapter;

    private void bindFuels(List<fuel_modal> fuleentry) {
        if (adapter == null) {
            lstFuelEntry.setLayoutManager(new LinearLayoutManager(this));
            lstFuelEntry.setItemAnimator(new DefaultItemAnimator());

            adapter = new fuel_list_adapter(fuleentry, getResources(), viewfuelentry.this);
            adapter.setOnItemClickListner(new fuel_list_adapter.OnItemClickListener() {
                @Override
                public void onItemClick(fuel_modal item) {
                    Intent i = new Intent(viewfuelentry.this, addfuelentry.class);
                    i.putExtra("autoid", item.autoid);
                    startActivity(i);
                }
            });
            lstFuelEntry.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        } else {
            adapter.setDataSource(fuleentry);
        }
    }


    boolean mBound = false;
    livesocketService lvsservice;
    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            livesocketService.LocalBinder binder = (livesocketService.LocalBinder) service;
            lvsservice = binder.getService();
            mBound = true;

            vhs = new ArrayList<>(lvsservice.getVhs());
            bindList();


        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };

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


    @Override
    protected void onResume() {
        super.onResume();
        validateReport();
    }
}
