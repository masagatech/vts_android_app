package com.goyo.tracking.track.initials;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.reflect.TypeToken;
import com.goyo.tracking.track.R;
import com.goyo.tracking.track.common.Checker;
import com.goyo.tracking.track.dashboard.dash;
import com.goyo.tracking.track.globals.Global;
import com.goyo.tracking.track.main;
import com.goyo.tracking.track.model.model_loginusr;
import com.goyo.tracking.track.utils.SHP;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.lang.reflect.Type;
import java.util.List;

public class login extends AppCompatActivity implements View.OnClickListener {
    /* form variable */
    EditText edtUserName, edtPassword;
    Button btnLogin;
    ProgressDialog loader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setTitle("VTS Login");
        initAllControls();
    }


    /*fill all controls*/
    private void initAllControls() {
        /*Edit text box*/
        edtUserName = (EditText) findViewById(R.id.edtUserName);
        edtPassword = (EditText) findViewById(R.id.edtPassword);

        /*Button*/
        btnLogin = (Button) findViewById(R.id.btnLogin);

        /*Progress bar*/
        loader = new ProgressDialog(login.this);
        loader.setMessage("Login.. Please wait.");

        setClickListner();
    }

    /*Set all click lisners*/
    private void setClickListner() {
        btnLogin.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnLogin: {
                if (!validate()) {
                    return;
                }


                JsonObject json = new JsonObject();
                json.addProperty("email", edtUserName.getText().toString());
                json.addProperty("pwd", edtPassword.getText().toString());
                //json.addProperty("type", "driver");
                json.addProperty("otherdetails", "{}");
                json.addProperty("src", "m");
                Global.showProgress(loader);
                Ion.with(this)
                        .load(Global.urls.getlogin.value)
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
                                    Type listType = new TypeToken<List<model_loginusr>>() {
                                    }.getType();
                                    List<model_loginusr> login = (List<model_loginusr>) gson.fromJson(result.get("data"), listType);
                                    if (login.size() > 0) {
                                        Global.loginusr = login.get(0);
                                        if (Global.loginusr.getStatus() == 1) {
                                            SHP.set(login.this, SHP.ids.uid, Global.loginusr.getDriverid() + "");
                                            String g = Global.loginusr.getSessiondetails().toString();
                                            if(!g.equals("null")){
                                               String s =  ((LinkedTreeMap)Global.loginusr.getSessiondetails()).get("sessionid").toString();
                                                Global.loginusr.setSessiondetails( s.replace(".0",""));
                                                SHP.set(login.this, SHP.ids.sessionid, Global.loginusr.getSessiondetails().toString());
                                            }

                                             //Toast.makeText(login.this, "Login Successfully!", Toast.LENGTH_SHORT).show();
                                             Intent i = new Intent(login.this, dash.class);
                                            startActivity(i);
                                            login.this.finish();
                                        } else {
                                            Toast.makeText(login.this, "Login Failed! " + Global.loginusr.getErrmsg() , Toast.LENGTH_SHORT).show();
                                            edtPassword.setText("");
                                        }
                                    } else {
                                        Toast.makeText(login.this, "Oops there is some issue! please login later!", Toast.LENGTH_SHORT).show();
                                    }

                                } catch (Exception ea) {
                                    Toast.makeText(login.this, "Error: " + ea.getMessage(), Toast.LENGTH_LONG).show();
                                    ea.printStackTrace();
                                }
                                Global.hideProgress(loader);

                            }
                        });

            }
            break;
            default:
                break;
        }

    }


    private boolean validate() {
        if (edtUserName.getText().toString().trim().equals("")) {
            edtUserName.setError("Required!");
            return false;
        } else if (edtPassword.getText().toString().trim().equals("")) {
            edtPassword.setError("Required!");
            return false;
        }
        edtUserName.setError(null);
        edtPassword.setError(null);
        return true;
    }





    @Override
    public void onResume() {
        super.onResume();
        new Checker(this).pass(new Checker.Pass() {
            @Override
            public void pass() {

            }

        }).check(Checker.Resource.NETWORK );
    }
}


