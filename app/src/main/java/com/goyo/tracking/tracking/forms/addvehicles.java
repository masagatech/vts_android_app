package com.goyo.tracking.tracking.forms;


import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.app.ProgressDialog;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.google.zxing.Result;
import com.goyo.tracking.tracking.R;
import com.goyo.tracking.tracking.adapters.ddl_adapter;
import com.goyo.tracking.tracking.dashboard.dash;
import com.goyo.tracking.tracking.dialogs.dialogDatePicker;
import com.goyo.tracking.tracking.globals.Global;
import com.goyo.tracking.tracking.helpers.ScalingScannerActivity;
import com.goyo.tracking.tracking.model.ddl_model;
import com.goyo.tracking.tracking.model.model_loginusr;
import com.goyo.tracking.tracking.model.vts_vh_model;
import com.goyo.tracking.tracking.service.livesocketService;
import com.goyo.tracking.tracking.utils.SHP;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class addvehicles extends AppCompatActivity implements ZXingScannerView.ResultHandler {

    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS = 897;
    @BindView(R.id.spinner)
    Spinner spinner;
    @BindView(R.id.spnrD1Selection)
    Spinner spnrD1Selection;

    @BindView(R.id.edtImei)
    EditText edtImei;
    @BindView(R.id.edtVhName)
    EditText edtVhName;
    @BindView(R.id.edtRegNo)
    EditText edtRegNo;
    @BindView(R.id.edtSpdAlert)
    EditText edtSpdAlert;
    @BindView(R.id.edtSimNumber)
    EditText edtSimNumber;


    @BindView(R.id.edtChasisNo)
    EditText edtChasisNo;

    @BindView(R.id.edtModel)
    EditText edtModel;

    @BindView(R.id.edtPucEpireDt)
    EditText edtPucEpireDt;

    @BindView(R.id.edtInsuExpireDt)
    EditText edtInsuExpireDt;

    @BindView(R.id.edtMake)
    EditText edtMake;

    @BindView(R.id.edtCapacity)
    EditText edtCapacity;

    @BindView(R.id.edtNoOfTyres)
    EditText edtNoOfTyres;

    @BindView(R.id.btnSave)
    Button btnSave;
    ProgressDialog loader;

    String VhType = "";
    String D1Function = "";
    int vtsid = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addvehicles);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ButterKnife.bind(this);
        setTitle("Vehicle Details");

        btnSave.setVisibility(View.GONE);

        loader = new ProgressDialog(this);
        loader.setMessage("Please wait.");


        Events();
        bindVehicleTypes();
        bindD1Function();
        disabledAll();

        Bundle bdl = getIntent().getExtras();
        if (bdl != null) {
            vtsid = bdl.getInt("vtsid");
            if (vtsid > 0)
                FillEditForm(vtsid);
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_addedit_vehicleview, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.saveVehicle:
                if (btnSave.isEnabled()) {
                    SaveVehicle();
                }
                return true;
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }


    }


    boolean isDisabletextchange = false;

    private void Events() {
        edtImei.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {


            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (edtImei.getText().length() == 15 && !isDisabletextchange) {
                    GetDeviceInfo();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @OnClick(R.id.btnScanBarcode)
    void btnScanBarcode_Click(View v){
        startActivity(new Intent(this, ScalingScannerActivity.class));
    }

    @OnClick(R.id.btnServer)
    void OnServerClick(View v) {
        if (edtSimNumber.getText().length() < 10) {
            Toast.makeText(getApplicationContext(), "Invalid mobile number",
                    Toast.LENGTH_LONG).show();
            return;
        }
        sendSMSMessage();

    }

    String phoneNo;
    String message = "SERVER,0,35.154.114.229,6969,0#";

    protected void sendSMSMessage() {
        phoneNo = edtSimNumber.getText().toString();


        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.SEND_SMS)) {
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.SEND_SMS},
                        MY_PERMISSIONS_REQUEST_SEND_SMS);
            }
        } else {
            SendSMS();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_SEND_SMS: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    SendSMS();
                } else {
                    Toast.makeText(getApplicationContext(),
                            "SMS faild, please try again.", Toast.LENGTH_LONG).show();
                    return;
                }
            }
        }

    }

    private void SendSMS() {
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(phoneNo, null, message, null, null);
        Toast.makeText(getApplicationContext(), "SMS sent for activation.",
                Toast.LENGTH_LONG).show();
    }


    List<ddl_model> vehtypes;

    private void bindVehicleTypes() {
        vehtypes = new ArrayList<>();
        vehtypes.add(new ddl_model("none", "Select Vehicle Type"));
        vehtypes.add(new ddl_model("bike", "Bike"));
        vehtypes.add(new ddl_model("car", "Car"));
        vehtypes.add(new ddl_model("bus", "Bus"));
        vehtypes.add(new ddl_model("truck", "Truck"));
        vehtypes.add(new ddl_model("tractor", "Tractor"));
        vehtypes.add(new ddl_model("rik", "Auto Rickshow"));

        ddl_adapter dataAdapter = new ddl_adapter(this, vehtypes, getResources());
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                VhType = ((ddl_model) parent.getItemAtPosition(position)).Key;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spinner.setAdapter(dataAdapter);
    }

    private int getDdlIndex(String key, List<ddl_model> lst) {
        int index = 0;
        for (int i = 0; i < lst.size(); i++) {
            ddl_model d = lst.get(i);
            if (d.Key.equals(key)) {
                return i;
            }
        }
        return index;
    }


    List<ddl_model> lstd1;

    private void bindD1Function() {

        lstd1 = new ArrayList<>();
        lstd1.add(new ddl_model("NONE", "None"));
        lstd1.add(new ddl_model("DOOR", "Door"));
        lstd1.add(new ddl_model("AC", "AC"));
        ddl_adapter dataAdapter = new ddl_adapter(this, lstd1, getResources());
        spnrD1Selection.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                D1Function = ((ddl_model) parent.getItemAtPosition(position)).Key;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spnrD1Selection.setAdapter(dataAdapter);
    }

    private void disabledAll() {
        spinner.setEnabled(false);
        spnrD1Selection.setEnabled(false);
//        edtImei.setEnabled(false);
        edtSimNumber.setEnabled(false);
        edtVhName.setEnabled(false);
        edtRegNo.setEnabled(false);
        edtSpdAlert.setEnabled(false);
        btnSave.setEnabled(false);
        edtMake.setEnabled(false);
        edtNoOfTyres.setEnabled(false);
        edtChasisNo.setEnabled(false);
        edtModel.setEnabled(false);
        edtCapacity.setEnabled(false);
    }

    private void enableAll() {
        spinner.setEnabled(true);
        spnrD1Selection.setEnabled(true);
        edtSimNumber.setEnabled(true);
//        edtImei.setEnabled(false);
        edtVhName.setEnabled(true);
        edtRegNo.setEnabled(true);
        edtSpdAlert.setEnabled(true);
        btnSave.setEnabled(true);
        edtMake.setEnabled(true);
        edtNoOfTyres.setEnabled(true);
        edtChasisNo.setEnabled(true);
        edtModel.setEnabled(true);
        edtCapacity.setEnabled(true);
    }


    @OnClick(R.id.btnSave)
    void btnSave_Click(View v) {
        SaveVehicle();
    }


    private void SaveVehicle() {
        loader.setMessage("Saving....");
        Global.showProgress(loader);

        String alwSpd = edtSpdAlert.getText().toString().isEmpty() ? "0" : edtSpdAlert.getText().toString();

        JsonObject o = new JsonObject();
        o.addProperty("vtsid", vtsid);
        o.addProperty("vhid", edtImei.getText().toString());
        o.addProperty("sim", edtSimNumber.getText().toString());
        o.addProperty("vhname", edtVhName.getText().toString());
        o.addProperty("vhtyp", VhType);
        o.addProperty("devtyp", "XX");
        o.addProperty("alwspeed", Integer.parseInt(alwSpd));

        o.addProperty("vhregno", edtRegNo.getText().toString());
        o.addProperty("uid", SHP.get(this, SHP.ids.uid, "0").toString());
        o.addProperty("activate", "false");

        JsonObject oExtra = new JsonObject();
        oExtra.addProperty("d1str", D1Function);
        o.add("extra", oExtra);


        JsonObject oSub = new JsonObject();

        oSub.addProperty("vhregno", edtRegNo.getText().toString());
        oSub.addProperty("make", edtMake.getText().toString());
        oSub.addProperty("model", edtModel.getText().toString());
        oSub.addProperty("capacity", edtCapacity.getText().toString());
        oSub.addProperty("chasisno", edtChasisNo.getText().toString());
        oSub.addProperty("pucexp", edtPucEpireDt.getText().toString());
        oSub.addProperty("insurance", edtInsuExpireDt.getText().toString());
        oSub.addProperty("tyrs", edtNoOfTyres.getText().toString());
        o.add("vhd", oSub);


        Ion.with(addvehicles.this)
                .load(Global.urls.savevehicle.value)
                .setJsonObjectBody(o)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        // do stuff with the result or error
                        try {
                            if (result != null)
                                Log.v("result", result.toString());
                            JsonObject data = result.getAsJsonObject("data");
                            boolean Status = data.get("status").getAsBoolean();
                            if (Status) {
                                String Message = data.get("msg").getAsString();
                                // Toast.makeText(dash.this, "Faild to logout " + m.getErrcode() + " " + m.getErrmsg(), Toast.LENGTH_LONG).show();
                                Toast.makeText(addvehicles.this, Message, Toast.LENGTH_SHORT).show();
                                lvsservice.ResetALL();
                                ClearViews();

                            } else {
                                String Error = data.get("error").getAsString();
                                Toast.makeText(addvehicles.this, "Error " + Error, Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception ea) {
                            Toast.makeText(addvehicles.this, "Error: " + ea.getMessage(), Toast.LENGTH_LONG).show();
                            ea.printStackTrace();
                        }
                        Global.hideProgress(loader);
                    }
                });
    }


    private void GetDeviceInfo() {
        loader.setMessage("Checking Device Info....");
        Global.showProgress(loader);
        JsonObject o = new JsonObject();
        o.addProperty("vhid", edtImei.getText().toString());
        Ion.with(addvehicles.this)
                .load(Global.urls.deviceCheck.value)
                .setJsonObjectBody(o)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        // do stuff with the result or error
                        try {
                            if (result != null)
                                Log.v("result", result.toString());
                            JsonObject data = result.getAsJsonObject("data");
                            boolean Status = data.get("status").getAsBoolean();


                            if (Status) {
                                enableAll();
                                if (data.has("data")) {
                                    edtSimNumber.setText(data.get("data").getAsString());
                                }
                                if (edtSimNumber.getText().toString().equals("")) {
                                    edtSimNumber.requestFocus();
                                } else {
                                    edtVhName.requestFocus();
                                }
                                String Message = data.get("msg").getAsString();
                                // Toast.makeText(dash.this, "Faild to logout " + m.getErrcode() + " " + m.getErrmsg(), Toast.LENGTH_LONG).show();
                                Toast.makeText(addvehicles.this, Message, Toast.LENGTH_SHORT).show();
                            } else {
                                disabledAll();
                                String Error = data.get("error").getAsString();
                                Toast.makeText(addvehicles.this, "Error " + Error, Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception ea) {
                            disabledAll();
                            Toast.makeText(addvehicles.this, "Error: " + ea.getMessage(), Toast.LENGTH_LONG).show();
                            ea.printStackTrace();
                        }
                        Global.hideProgress(loader);
                    }
                });
    }


    @OnClick(R.id.btnPucCalender)
    void btnPucCalender_Click(View v) {
        voidShowDatePicker("puc");
    }


    @OnClick(R.id.btnInsuranceCalender)
    void btnInsuranceCalender_Click(View v) {
        voidShowDatePicker("ins");
    }

    private void voidShowDatePicker(final String setDateIn) {
        dialogDatePicker newFragment = new dialogDatePicker();
        newFragment.setOnDateChangedListner(new dialogDatePicker.OnDateChanged() {
            @Override
            public void OnChange(int Year, int Month, int Day) {
                switch (setDateIn) {
                    case "puc":
                        edtPucEpireDt.setText(Year + "-" + String.format("%02d", (Month + 1)) + "-" + String.format("%02d", Day));
                        break;
                    case "ins":
                        edtInsuExpireDt.setText(Year + "-" + String.format("%02d", (Month + 1)) + "-" + String.format("%02d", Day));
                        break;
                }
            }
        });
        newFragment.show(getSupportFragmentManager(), "date picker");

    }

    private void ClearViews() {
        vtsid = 0;
        disabledAll();
        spinner.setSelection(0);
        spnrD1Selection.setSelection(0);
        edtImei.setText("");
        edtSimNumber.setText("");
        edtVhName.setText("");
        edtRegNo.setText("");
        edtSpdAlert.setText("");
        edtMake.setText("");
        edtCapacity.setText("");
        edtNoOfTyres.setText("");
        edtChasisNo.setText("");
        edtModel.setText("");

    }


    private void FillEditForm(Integer vtsid) {
        loader.setMessage("Checking Device Info....");
        Global.showProgress(loader);
        JsonObject o = new JsonObject();
        o.addProperty("vtsid", vtsid.intValue());
        Ion.with(addvehicles.this)
                .load(Global.urls.getvehicledetails.value)
                .setJsonObjectBody(o)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        // do stuff with the result or error
                        try {
                            if (result != null)
                                Log.v("result", result.toString());
                            JsonObject data = result.getAsJsonObject("data");
                            Gson gson = new Gson();
                            Type listType = new TypeToken<vts_vh_model>() {
                            }.getType();
                            vts_vh_model vhs = (vts_vh_model) gson.fromJson(result.get("data"), listType);
                            fillform(vhs);

                        } catch (Exception ea) {
                            disabledAll();
                            Toast.makeText(addvehicles.this, "Error: " + ea.getMessage(), Toast.LENGTH_LONG).show();
                            ea.printStackTrace();
                        }
                        Global.hideProgress(loader);
                    }
                });

    }

    private void fillform(vts_vh_model vhs) {
        enableAll();
        edtImei.setEnabled(false);
        isDisabletextchange = true;
        edtImei.setText(vhs.vhid);


        int indx = getDdlIndex(vhs.ico, vehtypes);
        spinner.setSelection(indx);

        if (vhs.extra != null) {
            int indx1 = getDdlIndex(vhs.extra.d1str, lstd1);
            spnrD1Selection.setSelection(indx1);
        }
        isDisabletextchange = false;
        edtSimNumber.setText(vhs.sim);
        edtVhName.setText(vhs.vno);
        //edtRegNo.setText(vhs);
        edtSpdAlert.setText(vhs.alwspeed + "");
        edtMake.setText(vhs.make);
        edtCapacity.setText(vhs.capacity);
        edtNoOfTyres.setText(vhs.tyrs);
        edtChasisNo.setText(vhs.chasisno);
        edtModel.setText(vhs.model);
        edtPucEpireDt.setText(vhs.pucexp);
        edtRegNo.setText(vhs.regno);
        edtInsuExpireDt.setText(vhs.insurance);


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
    public void handleResult(Result result) {

    }
}
