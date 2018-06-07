package com.goyo.tracking.track.forms;

import android.app.Dialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.goyo.tracking.track.R;
import com.goyo.tracking.track.adapters.pop_vh_adapter;
import com.goyo.tracking.track.adapters.rpt_mileage_adapter;
import com.goyo.tracking.track.globals.Global;
import com.goyo.tracking.track.model.rpt_mileage_model;
import com.goyo.tracking.track.model.vts_vh_model;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;


public class reportmileagedaywise extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reportmileagedaywise);
        final android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        ButterKnife.bind(this);

        getBundle();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }


    }

    List<vts_vh_model> vhs;
    Dialog dialogOut;
    ListView lstVehicles;
    HashMap<String, String> vhad = new HashMap<>();
    pop_vh_adapter lspopadapter;
    String[] vhidArr;
    private void _bindReport(String imei) {
        if (objrpt_mile != null) {
            objrpt_mile.clear();
        }

        JsonObject params = new JsonObject();
        Gson gson = new GsonBuilder().create();
        ArrayList<String> vhlist = new ArrayList<String>();
        vhlist.add(imei);
        JsonElement jsonArray = gson.toJsonTree(vhlist);

        params.add("vhid", jsonArray);
        params.addProperty("frmdt", _frmDt);
        params.addProperty("todate", _toDate);
        params.addProperty("type", "datewise");

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
                                Toast.makeText(reportmileagedaywise.this, "No Data Found!", Toast.LENGTH_SHORT).show();

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
                        //prgloading.setVisibility(View.GONE);
                    }
                });
    }
    @BindView(R.id.lstMileage)
    ListView lstMileage;
    rpt_mileage_adapter objrpt_mile;
    private SimpleDateFormat dateFormatForMonth = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());

    private SimpleDateFormat dateFormatForGetData = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
    private void bindReport(List<rpt_mileage_model> lst) {
        if (objrpt_mile == null) {
            objrpt_mile = new rpt_mileage_adapter(reportmileagedaywise.this, lst, getResources());
            objrpt_mile.vhad = vhad;
            lstMileage.setAdapter(objrpt_mile);
            lstMileage.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    rpt_mileage_model rp =  (rpt_mileage_model)parent.getItemAtPosition(position);
                    Intent i = new Intent(reportmileagedaywise.this, history.class);
                    i.putExtra("imei", rp.id.vhid);
                    i.putExtra("vhnm", rp.vno);
                    i.putExtra("date", rp.frmdate);

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
    private void getBundle(){
        Bundle bdl = getIntent().getExtras();
       String Imei = bdl.getString("imei");

       _frmDt = bdl.getString("frmdt");
        _toDate = bdl.getString("todt");
        String vno = bdl.getString("vhno");
        String totalmlg = "Total Mileage " + bdl.getString("totalmlg") +" KM";
        vhad.put(Imei,vno);
        getSupportActionBar().setTitle(vno);
        getSupportActionBar().setSubtitle(totalmlg);

        _bindReport(Imei);
    }
}
