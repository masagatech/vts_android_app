package com.goyo.tracking.tracking.forms;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.CountDownTimer;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.goyo.tracking.tracking.R;
import com.goyo.tracking.tracking.common.LoginClass;
import com.goyo.tracking.tracking.dashboard.dash;
import com.goyo.tracking.tracking.globals.Global;
import com.goyo.tracking.tracking.initials.login;
import com.goyo.tracking.tracking.model.model_loginusr_vts;
import com.goyo.tracking.tracking.utils.common;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

public class verifyotp extends AppCompatActivity {
    private static final int SMS_PERMISSION_CODE = 3434;
    private ImageView mBack;
    private TextView mMobileNo;
    private TextView mVarifyCode;
    private Button mVeryfy;
    private Button mResendOtp;
    private ProgressBar mProgressBar;
    private String id = "";
    private String mobile = "",email="";
    ProgressDialog loader;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verifyotp);

        loader = new ProgressDialog(this);

        mMobileNo = (TextView) findViewById(R.id.txt_mobile);
        mVarifyCode = (TextView) findViewById(R.id.txt_varify_code);
        mVeryfy = (Button) findViewById(R.id.btn_veryfy);
        mResendOtp = (Button) findViewById(R.id.btn_resend_otp);
        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);
        if (getIntent().getExtras() != null) {
            //id = getIntent().getExtras().getString("id", "");
            mobile = getIntent().getExtras().getString("mobile", "");
            email = getIntent().getExtras().getString("email", "");
            if(getIntent().getExtras().getBoolean("resend")){
                resend_otp_api(mobile);
            }


            mMobileNo.setText(mobile);
        }
//        mBack.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                finish();
//            }
//        });
        mVeryfy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (common.isOnline(verifyotp.this)) {
                    verify_mobileno_api(mMobileNo.getText().toString(), mVarifyCode.getText().toString());
                }
            }
        });
        mResendOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (common.isOnline(verifyotp.this)) {
                    resend_otp_api(mMobileNo.getText().toString());
                }
            }
        });

        mVarifyCode.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (s.toString().trim().length() == 0) {
                    mVeryfy.setEnabled(false);
                } else {
                    mVeryfy.setEnabled(true);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub

            }
        });
        mVarifyCode.setText("");


        disableButtonFor30secs();
    }

    private void resend_otp_api(String mobile) {
        JsonObject json = new JsonObject();
        json.addProperty("mob", mobile);
        json.addProperty("email", email);
        loader.setMessage("Seding OTP.. Please wait.");
        Global.showProgress(loader);
        Ion.with(this)
                .load(Global.urls.resendOtp_vts.value)
                .setJsonObjectBody(json)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        // do stuff with the result or error
                        try {
                            if (result != null) Log.v("result", result.toString());
                            // JSONObject jsnobject = new JSONObject(jsond);
                            if (result.get("status").getAsBoolean()) {
                                Toast.makeText(verifyotp.this, result.get("msg").getAsString(), Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(verifyotp.this, result.get("msg").getAsString(), Toast.LENGTH_LONG).show();
                            }
                        } catch (Exception ex) {
                            Toast.makeText(verifyotp.this,"Error "+  ex.getMessage(), Toast.LENGTH_LONG).show();

                        }
                        Global.hideProgress(loader);
                    }


                });
    }

    private void verify_mobileno_api(String mob, String otp) {
        JsonObject json = new JsonObject();
        json.addProperty("mob", mob);
        json.addProperty("email", email);
        json.addProperty("otp", otp);
        loader.setMessage("Verifying OTP.. Please wait.");
        Global.showProgress(loader);
        Ion.with(this)
                .load(Global.urls.veryfyOtp_vts.value)
                .setJsonObjectBody(json)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        // do stuff with the result or error
                        try {
                            if (result != null) Log.v("result", result.toString());
                            // JSONObject jsnobject = new JSONObject(jsond);
                            if (result.get("status").getAsBoolean()) {

                                Login();

                            } else {
                                Toast.makeText(verifyotp.this, result.get("msg").getAsString(), Toast.LENGTH_LONG).show();
                            }
                        } catch (Exception ex) {
                            Toast.makeText(verifyotp.this,"Error "+  ex.getMessage(), Toast.LENGTH_LONG).show();

                        }
                        Global.hideProgress(loader);
                    }


                });


    }


    private void Login(){
        LoginClass c = new LoginClass();
        c.SetOnLoginCallback(new LoginClass.LoginCallBack() {
            @Override
            public void onDone(model_loginusr_vts user, int statusCode,String Error, boolean status) {
                if (statusCode == 2) {

                }else if(statusCode == 1){
                    Intent i = new Intent(verifyotp.this, dash.class);
                    startActivity(i);
                    verifyotp.this.finish();
                }else{

                }
            }
        });

        JsonObject json = new JsonObject();
        json.addProperty("email", email);
        json.addProperty("mob", mobile);
        json.addProperty("pwd", "");
        //json.addProperty("type", "driver");
        json.addProperty("otherdetails", "{}");
        json.addProperty("src", "android");

        c.Login(json, loader,this);
    }

    int ct = 30;
    CountDownTimer ctime;

    private void disableButtonFor30secs() {
        mResendOtp.setEnabled(false);
        ct = 30;
        ctime = new CountDownTimer((ct * 1000), 1000) {

            public void onTick(long millisUntilFinished) {
                ct -= 1;
                mResendOtp.setText("RESEND OTP ALLOW AFTER (" + ct + ")");
                //here you can have your logic to set text to edittext
            }

            public void onFinish() {
                ctime = null;
                mResendOtp.setText("RESEND OTP");
                mResendOtp.setEnabled(true);
            }

        }.start();
    }


    private void showAlertDialog(String message, final boolean isSuccess) {
        final AlertDialog.Builder builder1 = new AlertDialog.Builder(verifyotp.this);
        builder1.setMessage(message);
        builder1.setCancelable(true);
        builder1.setPositiveButton(
                "Ok",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (isSuccess) {
                            Intent intent = new Intent(getApplicationContext(), login.class);
                            startActivity(intent);
                            dialog.cancel();
                        } else {
                            dialog.cancel();
                        }
                    }
                });
        AlertDialog alertDialogg = builder1.create();
        alertDialogg.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter iff = new IntentFilter("com.goyo.in.smsveryfy");
        LocalBroadcastManager.getInstance(this).registerReceiver(onNotice, iff);
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(onNotice);
    }

    private BroadcastReceiver onNotice = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            // intent can contain anydata
            if (intent.getExtras() != null) {
                String otp = intent.getExtras().getString("otp");
                mVarifyCode.setText(otp);
            }
        }
    };


    // Activity

    /**
     * Check if we have SMS permission
     */
    public boolean isSmsPermissionGranted() {
        return ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * Request runtime SMS permission
     */
    private void requestReadAndSendSmsPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.READ_SMS)) {
            // You may display a non-blocking explanation here, read more in the documentation:
            // https://developer.android.com/training/permissions/requesting.html
        }
        ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_SMS}, SMS_PERMISSION_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case SMS_PERMISSION_CODE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // SMS related task you need to do.

                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
            // other 'case' lines to check for other
            // permissions this app might request
        }
    }
}
