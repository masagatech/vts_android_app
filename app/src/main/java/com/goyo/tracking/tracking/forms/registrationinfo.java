package com.goyo.tracking.tracking.forms;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.goyo.tracking.tracking.R;
import com.goyo.tracking.tracking.globals.Global;
import com.goyo.tracking.tracking.initials.login;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class registrationinfo extends AppCompatActivity {

    @BindView(R.id.et_full_name)
    EditText et_full_name;
    @BindView(R.id.et_email)
    EditText et_email;
    @BindView(R.id.et_mo_no)
    EditText et_mo_no;
    @BindView(R.id.et_pasword)
    EditText et_pasword;
    ProgressDialog loader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrationinfo);
        setTitle("Sign Up");
        ButterKnife.bind(this);
        initAllControls();
        getBundle();

    }


    /*fill all controls*/
    private void initAllControls() {
        /*Progress bar*/
        et_mo_no.requestFocus();

        loader = new ProgressDialog(registrationinfo.this);
        loader.setMessage("Login.. Please wait.");


    }


    String fname, lname, name, email, src,photo;

    private void getBundle() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            fname = bundle.getString("fname");
            lname = bundle.getString("lname");
            name = bundle.getString("fullname");
            email = bundle.getString("email");
            photo = bundle.getString("photo");
            src = bundle.getString("src");
            et_email.setText(email);
            et_full_name.setText(name);
        }
    }

    @OnClick(R.id.bt_submit)
    void OnSumbmit_Click(View v){
        registered_vts();
    }

    private void registered_vts() {
        JsonObject json = new JsonObject();
        json.addProperty("email", email);
        json.addProperty("pwd", "");
        json.addProperty("first_name", fname);
        json.addProperty("last_name", lname);
        json.addProperty("display_name", name);
        json.addProperty("mob", et_mo_no.getText().toString());
        json.addProperty("prof_pic", photo);
        json.addProperty("otherdetails", "{}");
        json.addProperty("src", src);
        Global.showProgress(loader);
        Ion.with(this)
                .load(Global.urls.register_vts.value)
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

                                //Toast.makeText(registrationinfo.this, result.get("msg").getAsString(), Toast.LENGTH_LONG).show();
                                Intent i = new Intent(registrationinfo.this,verifyotp.class);
                                i.putExtra("mobile",et_mo_no.getText().toString());
                                i.putExtra("email",email);
                                startActivity(i);
                                finish();

                            } else {
                                Toast.makeText(registrationinfo.this, result.get("msg").getAsString(), Toast.LENGTH_LONG).show();

                            }
                        } catch (Exception ex) {

                        }
                    }


                });
    }
}
