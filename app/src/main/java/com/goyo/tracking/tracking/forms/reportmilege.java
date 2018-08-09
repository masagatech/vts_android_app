package com.goyo.tracking.tracking.forms;

import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.goyo.tracking.tracking.R;
import com.goyo.tracking.tracking.adapters.pop_vh_adapter;
import com.goyo.tracking.tracking.adapters.rpt_mileage_adapter;
import com.goyo.tracking.tracking.globals.Global;
import com.goyo.tracking.tracking.helpers.RootActivity;
import com.goyo.tracking.tracking.model.rpt_mileage_model;
import com.goyo.tracking.tracking.model.vts_vh_model;
import com.goyo.tracking.tracking.service.livesocketService;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;

import com.borax12.materialdaterangepicker.date.DatePickerDialog;


public class reportmilege extends RootActivity implements DatePickerDialog.OnDateSetListener {

    boolean shouldShow = false;

    private SimpleDateFormat dateFormatForMonth = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());

    private SimpleDateFormat dateFormatForGetData = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

    private Calendar currentCalender = Calendar.getInstance(Locale.getDefault());

    @BindView(R.id.progressBar)
    ProgressBar prgloading;
    int allowDays = 60;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_milege);

        final android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Mileage Report");
        actionBar.setSubtitle("Past " + allowDays + " days supported.");
        ButterKnife.bind(this);
        shouldShow = true;
        bindVehicles();

    }

    private boolean mAutoHighlight;
    DatePickerDialog dpd;

    private void initCalenderEvents() {
        //set title on calendar scroll
        if (dpd == null) {
            Calendar now = Calendar.getInstance();
            dpd = com.borax12.materialdaterangepicker.date.DatePickerDialog.newInstance(
                    reportmilege.this,
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

    @OnCheckedChanged(R.id.swtchClubdtwise)
    void onClick() {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_milegerpt, menu);

        return true;
    }

    @Override
            public boolean onOptionsItemSelected(MenuItem item) {
                // Handle action bar item clicks here. The action bar will
                // automatically handle clicks on the Home/Up button, so long
                // as you specify a parent activity in AndroidManifest.xml.
                switch (item.getItemId()) {
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

    private void toggleCalender() {
        shouldShow = !shouldShow;
        initCalenderEvents();
    }

    List<vts_vh_model> vhs;
    Dialog dialogOut;
    ListView lstVehicles;
    pop_vh_adapter lspopadapter;
    String[] vhidArr;
    ProgressBar pop_spinner;

    private void bindVehicles() {


        if (dialogOut == null) {
            dialogOut = new Dialog(this);
            dialogOut.setContentView(R.layout.layout_vehiclelist);
            lstVehicles = (ListView) dialogOut.findViewById(R.id.lstVehicles);
            ((LinearLayout) dialogOut.findViewById(R.id.buttonTops)).setVisibility(View.VISIBLE);
            ((LinearLayout) dialogOut.findViewById(R.id.buttonBottom)).setVisibility(View.VISIBLE);
            pop_spinner =(ProgressBar) dialogOut.findViewById(R.id.pop_spinner);
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

//        JsonObject json = new JsonObject();
//        json.addProperty("flag", "vehicle_new");
//        json.addProperty("enttid", Global.loginusr.getEnttid() + "");
//        json.addProperty("uid", Global.loginusr.getDriverid() + "");
//        json.addProperty("utype", Global.loginusr.getUtype());
//        Ion.with(this)
//                .load(Global.urls.gettrackboard.value)
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
//                            vhs = (List<vts_vh_model>) gson.fromJson(result.get("data"), listType);
//                            pop_spinner.setVisibility(View.GONE);
                         bindList();
//                        } catch (Exception ea) {
//                            ea.printStackTrace();
//                        }
//                        // menu_refresh.setEnabled(false);
//
//                    }
//                });
    }

    private void bindList() {
        if (lstVehicles == null || vhs == null) {
            return;
        }
        pop_spinner.setVisibility(View.GONE);
        if (lspopadapter == null) {
            lspopadapter = new pop_vh_adapter(reportmilege.this, vhs, reportmilege.this.getResources());
            lspopadapter.multicheck = true;
            lstVehicles.setAdapter(lspopadapter);
            lspopadapter.notifyDataSetChanged();
        }



    }

    private void _bindReport() {
        if (objrpt_mile != null) {
            objrpt_mile.clear();
        }

        JsonObject params = new JsonObject();
        Gson gson = new GsonBuilder().create();
        JsonElement jsonArray = gson.toJsonTree(lspopadapter.getCheckdVehicle());


        params.add("vhid", jsonArray);


        //{ "date": {"$gte": "2018-07-11" , "$lte":"2018-07-13" }}


        params.addProperty("frmdt", _frmDt);
        params.addProperty("todate", _toDate);
//        params.addProperty("type", "datewise");

        JsonObject json = new JsonObject();
        json.addProperty("reporttyp", "milege");

        json.add("params", params);

        Ion.with(this)
                .load(Global.urls.getReport.value)
                .setJsonObjectBody(json)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        // do stuff with the result or error
                        try {
                            if (result != null) Log.v("result", result.toString());
                            // JSONObject jsnobject = new JSONObject(jsond);
                            JsonElement data = result.get("data");
                            if (data.equals(JsonNull.INSTANCE)) {
                                Toast.makeText(reportmilege.this, "No Data Found!", Toast.LENGTH_SHORT).show();

                            } else {
                                Gson gson = new Gson();
                                Type listType = new TypeToken<List<rpt_mileage_model>>() {
                                }.getType();
                                List<rpt_mileage_model> rptmilg = (List<rpt_mileage_model>) gson.fromJson(data, listType);
                                bindReport(rptmilg);
                            }
                        } catch (Exception ea) {
                            ea.printStackTrace();
                        }
                        // menu_refresh.setEnabled(false);
                        prgloading.setVisibility(View.GONE);
                    }
                });
    }

    @BindView(R.id.lstMileage)
    ListView lstMileage;
    rpt_mileage_adapter objrpt_mile;

    private void bindReport(List<rpt_mileage_model> lst) {
        if (objrpt_mile == null) {
            objrpt_mile = new rpt_mileage_adapter(reportmilege.this, lst, getResources());
            objrpt_mile.vhad = lspopadapter.vhsIndex;
            lstMileage.setAdapter(objrpt_mile);
            lstMileage.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    rpt_mileage_model r = (rpt_mileage_model) parent.getItemAtPosition(position);
                    Intent i = new Intent(reportmilege.this, reportmileagedaywise.class);
                    i.putExtra("imei", r.id.vhid);
                    i.putExtra("vhno", r.vno);
                    i.putExtra("frmdt", _frmDt);
                    i.putExtra("todt", _toDate);
                    i.putExtra("totalmlg", String.format("%.2f", r.milege));

                    startActivity(i);


                }
            });
        }
        objrpt_mile.setDataSource(lst);
        objrpt_mile.notifyDataSetChanged();


//        for (int i = 0; i <= lst.size() - 1; i++) {
//            rpt_mileage_model _rmm = lst.get(i);
//            String vhid = _rmm.id.vhid;
//
//
//        }

    }

    String _frmDt = "", _toDate = "";

    private void validateReport() {

        if (_frmDt.equals("")) {
            Toast.makeText(reportmilege.this, "Please select from date", Toast.LENGTH_SHORT).show();
            return;
        }

        if (_toDate.equals("")) {
            Toast.makeText(reportmilege.this, "Please select to date", Toast.LENGTH_SHORT).show();
            return;
        }

        if (lspopadapter.getCheckdVehicle().length == 0) {
            Toast.makeText(reportmilege.this, "Please select vehicles", Toast.LENGTH_SHORT).show();
            return;
        }
        prgloading.setVisibility(View.VISIBLE);
        _bindReport();
    }


    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth,
                          int yearEnd, int monthOfYearEnd, int dayOfMonthEnd) {

        _frmDt = year + "-" + String.format("%02d", (++monthOfYear)) + "-" + String.format("%02d", dayOfMonth) + "T00:00:00+05:30";
        _toDate = yearEnd + "-" + String.format("%02d", (++monthOfYearEnd)) + "-" + String.format("%02d", dayOfMonthEnd) + "T00:00:00+05:30";
        //"2017-11-11T00:00:00+05:30";

        validateReport();
//        Toast.makeText(reportmilege.this, date, Toast.LENGTH_SHORT).show();

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
            if(vhs == null) {
                vhs = new ArrayList<>(lvsservice.getVhs());
                bindList();
            }

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

}


