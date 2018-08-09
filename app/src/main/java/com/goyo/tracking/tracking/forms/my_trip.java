package com.goyo.tracking.tracking.forms;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TabHost;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.goyo.tracking.tracking.R;
import com.goyo.tracking.tracking.adapters.my_trip_listAdapter;
import com.goyo.tracking.tracking.common.Checker;
import com.goyo.tracking.tracking.globals.Global;
import com.goyo.tracking.tracking.model.model_mytrips;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.lang.reflect.Type;
import java.util.List;

public class my_trip extends AppCompatActivity {
    ProgressDialog loader;
    /* form variable */
    TabHost tabHost;
    ListView lstCurrentTrip;
    MenuItem menu_refresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_trip);
        setTitle(getResources().getString(R.string.mytrp_todaytrip));
        initAllControls();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        menu_refresh = menu.findItem(R.id.menu_refresh);
        return true;
    }

    //set action bar button menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_todaystrips_activity, menu);
        return true;
    }

    //action bar menu button click
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_refresh:
                HttpBindTrips();
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }


    private void HttpBindTrips() {

        new Checker(this).pass(new Checker.Pass() {
            @Override
            public void pass() {
                PostHttpBindTrips();
            }
        }).check(Checker.Resource.NETWORK);
    }


    private void PostHttpBindTrips() {


        JsonObject json = new JsonObject();
        json.addProperty("driverid", Global.loginusr.getDriverid());
        Global.showProgress(loader);
        //menu_refresh.setEnabled(false);
        Ion.with(this)
                .load(Global.urls.getmytrips.value)

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
                            Type listType = new TypeToken<List<model_mytrips>>() {
                            }.getType();
                            List<model_mytrips> events = (List<model_mytrips>) gson.fromJson(result.get("data"), listType);
                            bindCurrentTrips(events);


                        } catch (Exception ea) {
                            ea.printStackTrace();
                        }
                        // menu_refresh.setEnabled(false);
                        Global.hideProgress(loader);
                    }
                });
    }


    /*fill all controls*/
    private void initAllControls() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    /*        tabHost = (TabHost)findViewById(R.id.tabHost);
        tabHost.setup();

        addTabs();
*/
        loader = new ProgressDialog(this);
        lstCurrentTrip = (ListView) findViewById(R.id.lst_mytrip_list);
        setListners();
    }

    private void setListners() {
        lstCurrentTrip.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                model_mytrips o = (model_mytrips) parent.getItemAtPosition(position);
                String stat = o.stsi;
                String msttrpid = o.id + "";

//                Intent i = new Intent(my_trip.this, pickupcrew_gfa.class);
//                i.putExtra("stat", stat);
//                i.putExtra("msttrpid", msttrpid);
//                i.putExtra("tripid", o.tripid);
//                i.putExtra("title", o.nm);
//                i.putExtra("pd", o.pd);
//                i.putExtra("spdlmt", o.spdlmt);
//                i.putExtra("enttid", o.entt);
//                i.putExtra("vhid", o.vhid);
//
//                String _drop = getResources().getString(R.string.drop);
//                String _pickup = getResources().getString(R.string.pickup);
//
//                i.putExtra("subtitle", (o.pd.equals("p") ? _pickup : _drop) + " - " + o.time + " - " + o.btch);
//                startActivity(i);
            }
        });


    }


    private void addTabs() {
        //Tab 1
//        TabHost.TabSpec spec = tabHost.newTabSpec(getResources().getString(R.string.mytrp_todaytrip));
//        spec.setContent(R.id.tab1);
//        spec.setIndicator(getResources().getString(R.string.mytrp_todaytrip));
//        tabHost.addTab(spec);
//
//        //Tab 2
//        spec = tabHost.newTabSpec(getResources().getString(R.string.mytrp_history));
//        spec.setContent(R.id.tab2);
//        spec.setIndicator(getResources().getString(R.string.mytrp_history));
//        tabHost.addTab(spec);


    }

    private void bindCurrentTrips(List<model_mytrips> lst) {
        if (lst.size() > 0) {
            findViewById(R.id.txtNodata).setVisibility(View.GONE);
            my_trip_listAdapter adapter = new my_trip_listAdapter(this, lst, getResources(),1);
            lstCurrentTrip.setVisibility(View.VISIBLE);
            lstCurrentTrip.setAdapter(adapter);
            adapter.notifyDataSetChanged();

        } else {
            lstCurrentTrip.setVisibility(View.GONE);
            findViewById(R.id.txtNodata).setVisibility(View.VISIBLE);
        }
    }


    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }


    @Override
    public void onResume() {
        super.onResume();
        HttpBindTrips();
    }

}
