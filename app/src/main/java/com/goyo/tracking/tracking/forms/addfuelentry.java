package com.goyo.tracking.tracking.forms;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.cloudinary.android.MediaManager;
import com.cloudinary.android.callback.ErrorInfo;
import com.cloudinary.android.callback.UploadCallback;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.goyo.tracking.tracking.R;
import com.goyo.tracking.tracking.adapters.ddl_adapter;
import com.goyo.tracking.tracking.dialogs.dialogDatePicker;
import com.goyo.tracking.tracking.globals.Global;
import com.goyo.tracking.tracking.model.ddl_model;
import com.goyo.tracking.tracking.model.fuel_modal;
import com.goyo.tracking.tracking.model.vts_vh_model;
import com.goyo.tracking.tracking.service.livesocketService;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import net.yazeed44.imagepicker.model.ImageEntry;
import net.yazeed44.imagepicker.util.Picker;

import java.io.File;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.shaohui.advancedluban.Luban;

public class addfuelentry extends AppCompatActivity {

    private static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 2;
    @BindView(R.id.imgImage)
    ImageView selectedImageimg;

    @BindView(R.id.edtDate)
    EditText edtDate;

    @BindView(R.id.edtodometer)
    EditText edtodometer;

    @BindView(R.id.edtPricePerLtr)
    EditText edtPricePerLtr;

    @BindView(R.id.edtLiter)
    EditText edtLiter;

    @BindView(R.id.edtAmount)
    EditText edtAmount;

    @BindView(R.id.autocmpVehicle)
    AutoCompleteTextView autocmpVehicle;

    @BindView(R.id.spinner)
    Spinner spinner;

    @BindView(R.id.lblPriceuom)
    TextView lblPriceuom;

    @BindView(R.id.lblUOM)
    TextView lblUOM;

    @BindView(R.id.edtRemark)
    EditText edtRemark;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fuelform);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ButterKnife.bind(this);
        setTitle("Fuel Entry");
        endisbleAll(false);
        loader = new ProgressDialog(this);
        loader.setMessage("Please wait.");
        initEvents();
        clearForm();
        bindUOMTypes();
        OnEdit();

    }


    private void OnEdit() {
        Bundle bundle = getIntent().getExtras();

        if (bundle != null) {
            if (bundle.get("autoid") != null) {
                bindFuelEntry(bundle.get("autoid").toString());
            }
        }


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
                if (validateForm()) {
                    SaveFuel();
                }
                return true;
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }


    }

    List<ddl_model> uom;

    ddl_model selectedUOM;

    private void bindUOMTypes() {
        uom = new ArrayList<>();
        uom.add(new ddl_model("petrol", "PETROL", "LITER"));
        uom.add(new ddl_model("cng", "CNG", "KG"));
        uom.add(new ddl_model("lpg", "LPG", "KG"));
        uom.add(new ddl_model("disel", "DISEL", "LITER"));

        ddl_adapter dataAdapter = new ddl_adapter(this, uom, getResources());
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedUOM = (ddl_model) parent.getItemAtPosition(position);
                lblPriceuom.setText("Price/" + selectedUOM.Extra1);
                lblUOM.setText(selectedUOM.Extra1);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //selectedUOM = null;
            }
        });
        spinner.setAdapter(dataAdapter);
        spinner.setSelection(0);
    }


    vts_vh_model selected;
    double pricePerLiter = 0, liter = 0;

    private void initEvents() {

        edtPricePerLtr.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                pricePerLiter = Double.parseDouble(s.toString().length() == 0 ? "0" : s.toString());
                CalculateAmount();

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        edtLiter.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                liter = Double.parseDouble(s.toString().length() == 0 ? "0" : s.toString());
                CalculateAmount();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        autocmpVehicle.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selected = (vts_vh_model) parent.getAdapter().getItem(position);
                imei = selected.vhid;
                vhnm = selected.vno;
                vtsid = selected.id;
                regno = selected.regno;
            }
        });
    }

    private void CalculateAmount() {
        edtAmount.setText(String.format("%.2f", (pricePerLiter * liter)));
    }

    @OnClick(R.id.Date)
    void onDateSelect(View view) {
        voidShowDatePicker("date");
    }

    @OnClick(R.id.btnPickImage)
    void PickImage(View v) {
        int PERMISSION_ALL = 1;
        String[] PERMISSIONS = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA};

        if (!hasPermissions(this, PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        } else {
            pickImages();
        }

    }

    public static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_ID_MULTIPLE_PERMISSIONS: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    pickImages();
                } else {
                    String perStr = "";
                    for (String per : permissions) {
                        perStr += "\n" + per;
                    }
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
                    // permissions list of don't granted permission
                }
                return;
            }
        }

    }

    String Selectedurl = "";

    private void pickImages() {
        Selectedurl = "";
        //You can change many settings in builder like limit , Pick mode and colors
        new Picker.Builder(addfuelentry.this, new MyPickListener(), R.style.MIP_theme)
                .setPickMode(Picker.PickMode.SINGLE_IMAGE)
                .setLimit(1)
                .build()
                .startActivity();
    }

    ProgressDialog prgdialog;

    private void ShowProgress() {
        if (prgdialog == null) {
            prgdialog = new ProgressDialog(this);
            prgdialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            prgdialog.setCancelable(false);

        }
        prgdialog.setTitle("Uploading Image");
        prgdialog.setProgress(0);
        prgdialog.setMessage("compressing...");
        prgdialog.show();
    }

    File selectedImage;

    private class MyPickListener implements Picker.PickListener {

        @Override
        public void onPickedSuccessfully(final ArrayList<ImageEntry> images) {
            ShowProgress();

            selectedImage = new File(images.get(0).path);
            //getting selected images
//            for (int i = 0; i < images.size(); i++) {
//                //CompressedImage.add(new File(images.get(i).path));
//            }

            //compress selected image
            Luban.compress(addfuelentry.this, selectedImage)
                    .putGear(Luban.CUSTOM_GEAR)
                    .setMaxHeight(800)
                    .setMaxWidth(800)
                    .asListObservable()
                    .subscribe(new io.reactivex.functions.Consumer<List<File>>() {
                        @Override
                        public void accept(List<File> files) throws Exception {
                            int size = files.size();
                            while (size-- > 0) {
                                prgdialog.setMessage("uploading");
                                //arrFilePaths[selectedImage_Ind] = files.get(size).toString();
                                uploadImage(new File(files.get(size).toString()));
                            }

                        }
                    }, new io.reactivex.functions.Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            throwable.printStackTrace();
                            prgdialog.hide();
                        }
                    });

        }

        @Override
        public void onCancel() {
            Toast.makeText(addfuelentry.this, "There was an Error", Toast.LENGTH_SHORT).show();

        }
    }

    private boolean validateForm() {
        if (autocmpVehicle.getText().toString().isEmpty()) {
            Toast.makeText(addfuelentry.this, "Select Vehicle", Toast.LENGTH_SHORT).show();
            autocmpVehicle.requestFocus();
            return false;
        } else if (edtodometer.getText().toString().isEmpty()) {
            Toast.makeText(addfuelentry.this, "Enter Odometer", Toast.LENGTH_SHORT).show();
            edtodometer.requestFocus();
            return false;
        } else if (Double.parseDouble(edtAmount.getText().toString()) == 0) {
            Toast.makeText(addfuelentry.this, "Enter Valid Value. Amount should not be zero.", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void clearForm() {
        autocmpVehicle.requestFocus();
        autocmpVehicle.setText("");
        imei = "";
        vtsid = 0;
        selected = null;
        edtAmount.setText("0");
        setTodaysDate();
        selectedImage = null;
        selectedImageimg.setImageResource(0);
        edtLiter.setText("0");
        edtPricePerLtr.setText("0");

        edtodometer.setText("");
    }

    //save fuel entry
    ProgressDialog loader;
    Integer autoid = 0, vtsid = 0;
    String imei = "", vhnm = "", regno = "";

    private void SaveFuel() {
        loader.setMessage("Saving....");
        Global.showProgress(loader);


        JsonObject o = new JsonObject();
        o.addProperty("autoid", autoid);
        o.addProperty("imei", imei);
        o.addProperty("vhname", vhnm);
        o.addProperty("regno", regno);
        o.addProperty("uom", selectedUOM.Extra1);
        o.addProperty("fueltype", selectedUOM.Key);
        o.addProperty("odocurrent", Integer.parseInt(edtodometer.getText().toString() == "" ? "0" : edtodometer.getText().toString()));
        o.addProperty("uid", Global.loginusr.getDriverid());
        o.addProperty("vhid", vtsid);
        o.addProperty("utype", Global.loginusr.getUtype());
        o.addProperty("date", edtDate.getText().toString());
        o.addProperty("prcperltr", Double.parseDouble(edtPricePerLtr.getText().equals("") ? "0" : edtPricePerLtr.getText().toString()));
        o.addProperty("quantity", Double.parseDouble(edtLiter.getText().equals("") ? "0" : edtLiter.getText().toString()));
        o.addProperty("amount", Double.parseDouble(edtAmount.getText().equals("") ? "0" : edtAmount.getText().toString()));
        o.addProperty("attach", Selectedurl);
//        o.addProperty("latlon", );
        o.addProperty("remark", edtRemark.getText().toString());
        o.addProperty("cuid", Global.loginusr.getUcode() + "");

        Ion.with(addfuelentry.this)
                .load(Global.urls.savefuel.value)
                .setJsonObjectBody(o)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        // do stuff with the result or error
                        try {
                            if (e != null) {
                                Toast.makeText(addfuelentry.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            } else {
                                Log.v("result", result.toString());
                                String data = result.getAsJsonArray("data").get(0).getAsJsonObject().get("funsave_fuelentry").getAsJsonObject().get("msg").getAsString();
                                Toast.makeText(addfuelentry.this, data, Toast.LENGTH_SHORT).show();
                                clearForm();
                            }
                        } catch (Exception ea) {
                            Toast.makeText(addfuelentry.this, "Error: " + ea.getMessage(), Toast.LENGTH_LONG).show();
                            ea.printStackTrace();
                        }
                        Global.hideProgress(loader);
                    }
                });
    }


    private void bindFuelEntry(String autoid) {

        JsonObject o = new JsonObject();
        o.addProperty("flag", "edit");
        o.addProperty("autoid", autoid);
        o.addProperty("uid", Global.loginusr.getDriverid() + "");
        o.addProperty("utype", Global.loginusr.getUtype());


//        JsonParser parser = new JsonParser();
//        String query = "{\"autoid\": " + autoid + " }";
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

                            //result = result.get("data").getAsJsonObject();
                            // JSONObject jsnobject = new JSONObject(jsond);
                            Gson gson = new Gson();
                            Type listType = new TypeToken<List<fuel_modal>>() {
                            }.getType();
                            List<fuel_modal> fuleentry = (List<fuel_modal>) gson.fromJson(result.get("data"), listType);
                            fillEditForm(fuleentry);

                        } catch (Exception ea) {
                            ea.printStackTrace();
                        }
                        // menu_refresh.setEnabled(false);

                    }

                });
    }


    private void fillEditForm(List<fuel_modal> fuleentry) {
        if (fuleentry == null) {
            Toast.makeText(addfuelentry.this, "Invalid Entry", Toast.LENGTH_SHORT).show();
        }
        if (fuleentry.size() > 0) {
            fuel_modal mod = fuleentry.get(0);

            edtDate.setText(mod.date);
            edtAmount.setText(String.format("%.2f", mod.amount));
            edtLiter.setText(String.format("%.2f", mod.liter));
            edtPricePerLtr.setText(String.format("%.2f", mod.prcperltr));
            edtodometer.setText(mod.odo + "");
            autocmpVehicle.setText(mod.vhnm);
            imei = mod.imei;
            vtsid = mod.vtsid;
            regno = mod.regno;
            autoid = mod.autoid;
            edtRemark.setText(mod.remark);
            vhnm = mod.vhnm;

            int indx = getDdlIndex(mod.fueltyp, uom);
            spinner.setSelection(indx);

            try {
                Selectedurl = mod.attch;
                Glide.with(addfuelentry.this).load(Selectedurl)
                        .thumbnail(0.5f)
                        .crossFade()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(selectedImageimg);
            } catch (Exception ex) {

            }

        } else {
            Toast.makeText(addfuelentry.this, "Invalid Entry", Toast.LENGTH_SHORT).show();
        }
    }

    //
    private void uploadImage(File file) {
        String requestId = MediaManager.get().upload(file.getPath()).callback(new UploadCallback() {
            @Override
            public void onStart(String requestId) {

            }

            @Override
            public void onProgress(String requestId, long bytes, long totalBytes) {
                Double progress = ((double) bytes / totalBytes) * 100;
                prgdialog.setProgress(progress.intValue());
            }

            @Override
            public void onSuccess(String requestId, Map resultData) {
                prgdialog.hide();
                Selectedurl = resultData.get("url").toString();
                Glide.with(addfuelentry.this).load(Selectedurl)
                        .thumbnail(0.5f)
                        .crossFade()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(selectedImageimg);
                Toast.makeText(addfuelentry.this, "Success", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(String requestId, ErrorInfo error) {
                prgdialog.hide();
                Toast.makeText(addfuelentry.this, "Error" + error.getDescription(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onReschedule(String requestId, ErrorInfo error) {

            }
        }).dispatch(this);
    }

    private void setTodaysDate() {
        Date c = Calendar.getInstance().getTime();

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String formattedDate = df.format(c);
        edtDate.setText(formattedDate);
    }

    private void voidShowDatePicker(final String setDateIn) {
        dialogDatePicker newFragment = new dialogDatePicker();
        newFragment.setOnDateChangedListner(new dialogDatePicker.OnDateChanged() {
            @Override
            public void OnChange(int Year, int Month, int Day) {
                switch (setDateIn) {
                    case "date":
                        edtDate.setText(Year + "-" + String.format("%02d", (Month + 1)) + "-" + String.format("%02d", Day));
                        break;

                }
            }
        });
        newFragment.show(getSupportFragmentManager(), "date picker");

    }


    private void endisbleAll(boolean enabled) {
        autocmpVehicle.setEnabled(enabled);
        //edtAmount.setEnabled(enabled);
        edtLiter.setEnabled(enabled);
        edtPricePerLtr.setEnabled(enabled);
        //edtDate.setEnabled(enabled);
        edtodometer.setEnabled(enabled);


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


            ArrayAdapter<vts_vh_model> adapter = new ArrayAdapter<>(
                    addfuelentry.this, android.R.layout.simple_dropdown_item_1line, lvsservice.getVhs());

            autocmpVehicle.setAdapter(adapter);
            endisbleAll(true);

            if(lvsservice.getVhs().size() == 1){
                selected = lvsservice.getVhs().get(0);
                imei = selected.vhid;
                vhnm = selected.vno;
                vtsid = selected.id;
                regno = selected.regno;
                autocmpVehicle.setText(vhnm);

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
