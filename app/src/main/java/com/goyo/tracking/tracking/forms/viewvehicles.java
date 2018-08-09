package com.goyo.tracking.tracking.forms;

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
import android.view.Menu;
import android.view.MenuItem;

import com.goyo.tracking.tracking.R;
import com.goyo.tracking.tracking.adapters.vh_list_adapter;
import com.goyo.tracking.tracking.adapters.vh_rec_adapter;
import com.goyo.tracking.tracking.main;
import com.goyo.tracking.tracking.model.vts_vh_model;
import com.goyo.tracking.tracking.service.livesocketService;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import butterknife.BindView;
import butterknife.ButterKnife;
public class viewvehicles extends AppCompatActivity {

    @BindView(R.id.lstVehicles)
    RecyclerView lstVehicles;

    private vh_list_adapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewvehicles);
        setTitle("Manage Vehicles");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ButterKnife.bind(this);
    }


//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_vehicleview, menu);
//
//        return true;
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.addVehicle:
                Intent i = new Intent(this, addvehicles.class);
                startActivity(i);
                return true;
            case R.id.vehicle:
                //calling logout api

                return true;
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }


    }


    private void bindRecyclerView() {
        if (adapter == null && lvsservice.getVhs() != null) {
            lstVehicles.setLayoutManager(new LinearLayoutManager(this));
            lstVehicles.setItemAnimator(new DefaultItemAnimator());
            adapter = new vh_list_adapter(lvsservice.getVhs(), getResources(), this);
            adapter.setOnItemClickListner(new vh_list_adapter.OnItemClickListener() {
                @Override
                public void onItemClick(vts_vh_model item) {

                    Intent i = new Intent(viewvehicles.this, addvehicles.class);
                    i.putExtra("vtsid", item.vtsid);
                    startActivity(i);
                }
            });
            lstVehicles.setAdapter(adapter);

            adapter.notifyDataSetChanged();
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
            bindRecyclerView();
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
        if (adapter != null) {
            adapter.setUpdatesList(lvsservice.getVhs());
            adapter.notifyDataSetChanged();
        }
    }
}
