package com.goyo.tracking.tracking.forms;

import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.borax12.materialdaterangepicker.date.DatePickerDialog;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.goyo.tracking.tracking.R;
import com.goyo.tracking.tracking.adapters.pop_vh_adapter;
import com.goyo.tracking.tracking.globals.Global;
import com.goyo.tracking.tracking.model.rpt_mileage_model;
import com.goyo.tracking.tracking.model.vts_vh_model;
import com.goyo.tracking.tracking.service.livesocketService;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class reportspeed extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    @BindView(R.id.webView)
    WebView webView;

    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    @BindView(R.id.rgbtype)
    RadioGroup rgb;

    boolean shouldShow = false;

    private SimpleDateFormat dateFormatForMonth = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());

    private SimpleDateFormat dateFormatForGetData = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());

    private Calendar currentCalender = Calendar.getInstance(Locale.getDefault());

    private float m_downX;
    private String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reportspeed);

        ButterKnife.bind(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        webView.getSettings().setSupportZoom(false);
        webView.getSettings().setBuiltInZoomControls(false);
        webView.getSettings().setDisplayZoomControls(false);
        shouldShow = true;

        final android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Speed Report");
        actionBar.setSubtitle("Past " + allowDays + " days supported.");
        progressBar.setVisibility(View.GONE);
        initWebView();
        //webView.postUrl();
//        getLoadUrl();
        bindVehicles();
        rgb.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (vhs.size() > 0) {
                    toggleCalender();
                }
            }
        });
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
            lspopadapter = new pop_vh_adapter(reportspeed.this, vhs, reportspeed.this.getResources());
            lspopadapter.multicheck = true;
            lstVehicles.setAdapter(lspopadapter);
            lspopadapter.notifyDataSetChanged();
        }



    }


    private boolean mAutoHighlight;
    DatePickerDialog dpd;

    int allowDays = 30;

    private void initCalenderEvents() {
        //set title on calendar scroll
        if (dpd == null) {
            Calendar now = Calendar.getInstance();
            dpd = com.borax12.materialdaterangepicker.date.DatePickerDialog.newInstance(
                    reportspeed.this,
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

    List<vts_vh_model> vhs;
    Dialog dialogOut;
    ListView lstVehicles;
    pop_vh_adapter lspopadapter;
    String[] vhidArr;
    ProgressBar pop_spinner;

    private void toggleCalender() {
        shouldShow = !shouldShow;
        initCalenderEvents();
    }

    String _frmDt = "", _toDate = "";

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth,
                          int yearEnd, int monthOfYearEnd, int dayOfMonthEnd) {

        _frmDt = year + "-" + String.format("%02d", (++monthOfYear)) + "-" + String.format("%02d", dayOfMonth) + "T00:00:00+05:30";
        _toDate = yearEnd + "-" + String.format("%02d", (++monthOfYearEnd)) + "-" + String.format("%02d", dayOfMonthEnd) + "T00:00:00+05:30";
        if (_frmDt.equals(_toDate)) {
            _toDate = yearEnd + "-" + String.format("%02d", (++monthOfYearEnd)) + "-" + String.format("%02d", dayOfMonthEnd) + "T23:59:59+05:30";
        }

        //"2017-11-11T00:00:00+05:30";

        validateReport();
//        Toast.makeText(reportmilege.this, date, Toast.LENGTH_SHORT).show();

    }


    private void validateReport() {

        if (_frmDt.equals("")) {
            Toast.makeText(reportspeed.this, "Please select from date", Toast.LENGTH_SHORT).show();
            return;
        }

        if (_toDate.equals("")) {
            Toast.makeText(reportspeed.this, "Please select to date", Toast.LENGTH_SHORT).show();
            return;
        }

        if (lspopadapter.getCheckdVehicle().length == 0) {
            Toast.makeText(reportspeed.this, "Please select vehicles", Toast.LENGTH_SHORT).show();
            return;
        }
        progressBar.setVisibility(View.VISIBLE);
        _bindReport();
    }

    private void _bindReport() {
        JsonObject params = new JsonObject();
        Gson gson = new GsonBuilder().create();
        JsonElement jsonArray = gson.toJsonTree(lspopadapter.getCheckdVehicle());
        webView.clearCache(true);
        webView.loadUrl("about:blank");

        int selectedId = rgb.getCheckedRadioButtonId();
        String flag = ((RadioButton) findViewById(selectedId)).getText().toString().toLowerCase();

        params.add("vhid", jsonArray);
        params.addProperty("vwtype", "download");
        params.addProperty("format", "html");
        params.addProperty("frmdt", _frmDt);
        params.addProperty("todt", _toDate);
        params.addProperty("flag", flag);
        params.addProperty("rpttype", "speed");

//        JsonObject json = new JsonObject();
//        json.addProperty("rpttyp", "speed");

//        json.add("params", params);

        Ion.with(this)
                .load(Global.urls.getReportSchool.value)
                .setJsonObjectBody(params)
                .asString()
                .setCallback(new FutureCallback<String>() {
                    @Override
                    public void onCompleted(Exception e, String result) {
                        // do stuff with the result or error
                        try {

                            webView.loadData(result, "text/html; charset=utf-8", "UTF-8");


                        } catch (Exception ea) {
                            ea.printStackTrace();
                        }
                        // menu_refresh.setEnabled(false);
                        progressBar.setVisibility(View.GONE);
                    }
                });
    }


    private void initWebView() {
        webView.setWebChromeClient(new MyWebChromeClient(this));
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                progressBar.setVisibility(View.VISIBLE);
                invalidateOptionsMenu();
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                webView.loadUrl(url);
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                progressBar.setVisibility(View.GONE);
                invalidateOptionsMenu();
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
                progressBar.setVisibility(View.GONE);
                invalidateOptionsMenu();
            }
        });
        webView.clearCache(true);
        webView.clearHistory();
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setHorizontalScrollBarEnabled(true);
//        webView.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//
//                if (event.getPointerCount() > 1) {
//                    //Multi touch detected
//                    return true;
//                }
//
//                switch (event.getAction()) {
//                    case MotionEvent.ACTION_DOWN: {
//                        // save the x
//                        m_downX = event.getX();
//                    }
//                    break;
//
//                    case MotionEvent.ACTION_MOVE:
//                    case MotionEvent.ACTION_CANCEL:
//                    case MotionEvent.ACTION_UP: {
//                        // set x so that it doesn't move
//                        event.setLocation(m_downX, event.getY());
//                    }
//                    break;
//                }
//
//                return false;
//            }
//        });
    }

    private class MyWebChromeClient extends WebChromeClient {
        Context context;

        public MyWebChromeClient(Context context) {
            super();
            this.context = context;
        }


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
//        overridePendingTransition(R.anim.stay, R.anim.exit);

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

            vhs = lvsservice.getVhs();
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
}
