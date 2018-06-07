package com.goyo.tracking.track.forms;

import android.Manifest;
import android.animation.ValueAnimator;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.sundeepk.compactcalendarview.CompactCalendarView;
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
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.google.maps.android.PolyUtil;
import com.goyo.tracking.track.R;
import com.goyo.tracking.track.adapters.pop_vh_adapter;
import com.goyo.tracking.track.adapters.vh_history_tripline;
import com.goyo.tracking.track.globals.Global;
import com.goyo.tracking.track.helpers.RootActivity;
import com.goyo.tracking.track.model.history_item_model;
import com.goyo.tracking.track.model.vts_vh_model;
import com.goyo.tracking.track.model.vts_vh_model_wrapper;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import org.w3c.dom.Text;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;


public class history extends RootActivity implements OnMapReadyCallback {
    List<Polyline> polylines = new ArrayList<Polyline>();

    private Toolbar toolbar;
    @BindView(R.id.compactcalendar_view)
    CompactCalendarView compactCalendarView;
    boolean shouldShow = false;
    private GoogleMap mMap;
    @BindView(R.id.lst_history)
    ListView lstHistory;
    @BindView(R.id.txtDistance)
    TextView txtDistance;
    @BindView(R.id.txtMaxSpeed)
    TextView txtMaxSpeed;

    @BindView(R.id.progressBar)
    ProgressBar spinner;

    @BindView(R.id.txtAvgSpeed)
    TextView txtAvgSpeed;

    @BindView(R.id.txttraveltime)
    TextView txttraveltime;

    Polyline polyline;
    Marker source, destination;
    ActionBar actbar;
    private SimpleDateFormat dateFormatForMonth = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());

    private SimpleDateFormat dateFormatForGetData = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());

    private Calendar currentCalender = Calendar.getInstance(Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_history);

//
//        toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

        ButterKnife.bind(this);
        compactCalendarView.hideCalendar();
        shouldShow = true;
        final ActionBar actionBar = getSupportActionBar();
        actbar = actionBar;
        // actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_white_24px);
        actionBar.setDisplayHomeAsUpEnabled(true);
        // Setting default toolbar title to empty
        actionBar.setTitle(null);

        //set initial title
        actionBar.setTitle(dateFormatForMonth.format(currentCalender.getTime()));

        getBundle(actionBar);

        googleMapInit();
        //set title on calendar scroll
        compactCalendarView.setListener(new CompactCalendarView.CompactCalendarViewListener() {
            @Override
            public void onDayClick(Date dateClicked) {
                Calendar c = Calendar.getInstance();
                if (dateClicked.after(c.getTime())) {
                    Toast.makeText(history.this, "Invalid Date", Toast.LENGTH_SHORT).show();
                    return;
                }
                spinner.setVisibility(View.VISIBLE);
                clearBoard();
                compactCalendarView.hideCalendar();

                shouldShow = true;
                actionBar.setTitle(dateFormatForMonth.format(dateClicked));
                //Toast.makeText(history.this, "Date : " + dateClicked.toString(), Toast.LENGTH_SHORT).show();
                String dt = dateFormatForGetData.format(dateClicked);
                //"2017-11-17T00:00:00+05:30"
                date = dt + "+05:30";
                getHistory(date);
            }


            @Override
            public void onMonthScroll(Date firstDayOfNewMonth) {
                // Changes toolbar title on monthChange


            }

        });


        lstHistory.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
//                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
//                    int index = lstHistory.getFirstVisiblePosition();
//
//                    if (adapter.getCount() - 4 == index) {
//                        Toast.makeText(history.this, "Test : " + adapter.getItem(index).trktyp, Toast.LENGTH_SHORT).show();
//                        view.setSelection(index + 1);
//                    } else {
//
//                        view.setSelection(index);
//                        Toast.makeText(history.this, "Date : " + adapter.getItem(index).trktyp, Toast.LENGTH_SHORT).show();
//                    }
//
//                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });

        lstHistory.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int h1 = lstHistory.getHeight();
                int h2 = view.getHeight();


                // lstHistory.setSelection(position);
                lstHistory.smoothScrollToPositionFromTop(position, h1 / 2 - h2 / 2, 1000);
                history_item_model vh = adapter.getItem(position);

                if (polyline != null) {
                    polyline.remove();
                    polyline = null;
                }
                List<LatLng> decodedPath = PolyUtil.decode(vh.encdpoly);

                if (source != null) {
                    source.remove();
                    source = null;
                }
                if (destination != null) {
                    destination.remove();
                    destination = null;
                }

                if (decodedPath.size() == 2) {
                    source = mMap.addMarker(new MarkerOptions()
                            .position(decodedPath.get(0))
                            .draggable(false)
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.stopmap)));

                } else {

                    source = mMap.addMarker(new MarkerOptions()
                            .position(decodedPath.get(0))
                            .draggable(false)
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.startmap)));

                    destination = mMap.addMarker(new MarkerOptions()
                            .position(decodedPath.get(decodedPath.size() - 1))
                            .draggable(false)
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.stopmap)));
                }
                PolylineOptions op = new PolylineOptions().addAll(decodedPath);
                op.width(10);
                if (vh.trktyp.equals("solid")) {
                    op.color(getResources().getColor(R.color.driving));
                } else {
                    op.color(getResources().getColor(R.color.stop));

                }


                polyline = mMap.addPolyline(op);


                zoomRoute(mMap, decodedPath);
            }
        });
//        lstHistory.setOnScrollListener(new RecyclerView.OnScrollListener() {
//            //int currentTopVisible;
//            @Override
//            public void onScrollStateChanged(AbsListView view, int scrollState) {
//                int index = lstHistory.getFirstVisiblePosition();
//                lstHistory.setSelection(index);
//            }
//        });




    }

    private void clearBoard() {

        if (source != null) {
            source.remove();
            source = null;
        }
        if (destination != null) {
            destination.remove();
            destination = null;
        }

        if (polyline != null) {
            polyline.remove();
            polyline = null;
        }
        if (lspopadapter != null) {
            //   lspopadapter.clear();
            lstHistory.clearFocus();
            lstHistory.clearChoices();
        }

    }


    private void googleMapInit() {
        SupportMapFragment mMap1 = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mMap1.getMapAsync(this);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;//get map object after ready
        //check permission
        boolean success = googleMap.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                        this, R.raw.style_json));

        if (!success) {
            Log.e("Main", "Style parsing failed.");
        }
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
        mMap.setTrafficEnabled(false);
        mMap.setMaxZoomPreference(17);
        CameraPosition position1 = new CameraPosition.Builder().target(l)
                .zoom(4)
                .build();
        changeCameraPosition(position1, true);


    }

    private void changeCameraPosition(CameraPosition cameraPosition, boolean animate) {
        CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition);

        if (animate) {
            mMap.animateCamera(cameraUpdate);
        } else {
            mMap.moveCamera(cameraUpdate);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_histroy, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.calender:
                compactCalendarView.setVisibility(View.VISIBLE);
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
        if (actbar.getSubtitle() == null) {
            Toast.makeText(this, "Please select vehicle", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!compactCalendarView.isAnimating()) {
            if (shouldShow) {
                compactCalendarView.showCalendar();
            } else {
                compactCalendarView.hideCalendar();
            }
            shouldShow = !shouldShow;
        }
    }

    private void getHistory(String frmdt) {
        JsonObject json = new JsonObject();
        json.addProperty("vhid", imei);
        json.addProperty("uid", Global.loginusr.getDriverid() + "");
        json.addProperty("frmdt", frmdt);
        Ion.with(this)
                .load(Global.urls.gettrackboardHistory.value)
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
                            Type listType = new TypeToken<vts_vh_model_wrapper>() {
                            }.getType();
                            vts_vh_model_wrapper vhs_wrap = (vts_vh_model_wrapper) gson.fromJson(result.get("data"), listType);
                            List<history_item_model> vhs = vhs_wrap.vhlist;
                            bindhistory(vhs);
                            showMilageAndTime(vhs_wrap);

                            spinner.setVisibility(View.GONE);
                        } catch (Exception ea) {
                            ea.printStackTrace();
                            spinner.setVisibility(View.GONE);
                        }
                        // menu_refresh.setEnabled(false);

                    }
                });
    }


    private void showMilageAndTime(vts_vh_model_wrapper lst) {
        animateTextView(0, lst.total_distance.intValue(), txtDistance);
        txtMaxSpeed.setText(lst.mx_spd.intValue() + " km/h");

        //animateTextView(0, lst.avg_spd, txtAvgSpeed);
        txtAvgSpeed.setText(lst.avg_spd + " km/h");

        txttraveltime.setText(lst.travel_tm);
    }

    vh_history_tripline adapter;

    private void bindhistory(List<history_item_model> vhs) {
        if (vhs == null) {
            adapter.clear();
            Toast.makeText(this, "No data found", Toast.LENGTH_SHORT).show();
            return;
        }
        if (adapter == null) {
            adapter = new vh_history_tripline(this, vhs, getResources());

            lstHistory.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        } else {

            adapter.setDataSource(vhs);
            adapter.notifyDataSetChanged();
        }

        if (vhs.size() > 0) {
            lstHistory.smoothScrollToPositionFromTop(0, 0);
        }

    }

    public void zoomRoute(GoogleMap googleMap, List<LatLng> lstLatLngRoute) {

        if (googleMap == null || lstLatLngRoute == null || lstLatLngRoute.isEmpty()) return;

        LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();
        for (LatLng latLngPoint : lstLatLngRoute)
            boundsBuilder.include(latLngPoint);

        int routePadding = 120;
        LatLngBounds latLngBounds = boundsBuilder.build();

        int width = getResources().getDisplayMetrics().widthPixels;
        int height = getResources().getDisplayMetrics().heightPixels;


        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(latLngBounds, width,  300, 10);
        //googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, routePadding));
        googleMap.animateCamera(cu);
    }

    String imei = "", date = "";

    private void getBundle(ActionBar ab) {
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            imei = extras.getString("imei");
            String vhname = extras.getString("vhnm");
            date = extras.getString("date");
            //2017-11-22T00:00:00+05:30

            SimpleDateFormat dateFormatForGetData_a = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
            try {
                Date dt = dateFormatForGetData_a.parse(date.replace("+05:30", ""));
                ab.setTitle(dateFormatForMonth.format(dt.getTime()));

            } catch (ParseException e) {
                e.printStackTrace();
            }

            ab.setSubtitle(vhname);
            getHistory(date);
        } else {
            bindVehicles();
        }


    }


    public void animateTextView(int initialValue, int finalValue, final TextView textview) {

        ValueAnimator valueAnimator = ValueAnimator.ofInt(initialValue, finalValue);
        valueAnimator.setDuration(2000);

        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {

                textview.setText(valueAnimator.getAnimatedValue().toString());

            }


        });
        valueAnimator.start();

    }


    List<vts_vh_model> vhs;
    Dialog dialogOut;
    ListView lstVehicles;
    pop_vh_adapter lspopadapter;
    ProgressBar pop_spinner;

    private void bindVehicles() {


        if (dialogOut == null) {
            dialogOut = new Dialog(this);
            dialogOut.setContentView(R.layout.layout_vehiclelist);
            lstVehicles = (ListView) dialogOut.findViewById(R.id.lstVehicles);
            pop_spinner =(ProgressBar) dialogOut.findViewById(R.id.pop_spinner);
            ((Button) dialogOut.findViewById(R.id.btnselectAll)).setVisibility(View.GONE);
            ((Button) dialogOut.findViewById(R.id.btnDeslectAll)).setVisibility(View.GONE);
            ((Button) dialogOut.findViewById(R.id.btnOk)).setVisibility(View.GONE);
            ((Button) dialogOut.findViewById(R.id.btnCancel)).setVisibility(View.GONE);


            dialogOut.setCancelable(true);
            lstVehicles.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    vts_vh_model vh = (vts_vh_model) parent.getItemAtPosition(position);
                    actbar.setSubtitle(vh.vno);
                    imei = vh.vhid;
                    compactCalendarView.setVisibility(View.VISIBLE);
                    compactCalendarView.showCalendar();
                    shouldShow = false;
                    dialogOut.dismiss();
                }
            });
        }
        dialogOut.show();


        if (vhs != null && vhs.size() > 0) {


            return;
        }

        pop_spinner.setVisibility(View.VISIBLE);
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
                            vhs = (List<vts_vh_model>) gson.fromJson(result.get("data"), listType);
                            bindList();
                        } catch (Exception ea) {
                            ea.printStackTrace();
                        }
                        // menu_refresh.setEnabled(false);
                        pop_spinner.setVisibility(View.GONE);
                    }
                });
    }


    private void bindList() {
        if (lspopadapter == null) {
            lspopadapter = new pop_vh_adapter(history.this, vhs, history.this.getResources());
            lstVehicles.setAdapter(lspopadapter);
        } else {
            lspopadapter.setDataSource(vhs);
        }

        lspopadapter.notifyDataSetChanged();

    }


}
