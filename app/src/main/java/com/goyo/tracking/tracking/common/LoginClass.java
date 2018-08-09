package com.goyo.tracking.tracking.common;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.goyo.tracking.tracking.dashboard.dash;
import com.goyo.tracking.tracking.forms.registrationinfo;
import com.goyo.tracking.tracking.forms.verifyotp;
import com.goyo.tracking.tracking.globals.Global;
import com.goyo.tracking.tracking.initials.login;
import com.goyo.tracking.tracking.model.model_loginusr;
import com.goyo.tracking.tracking.model.model_loginusr_vts;
import com.goyo.tracking.tracking.utils.SHP;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.lang.reflect.Type;

public class LoginClass  {

   public interface LoginCallBack{
        void onDone(model_loginusr_vts user, int statusCode, String Error, boolean status);
    }

    public LoginCallBack lcallback;

    public  void SetOnLoginCallback(LoginCallBack callbackClass){
        lcallback = callbackClass;
    }


    public void Login(final JsonObject  params,
                      final ProgressDialog loader,
                      final Context context){
        Global.showProgress(loader);
        Ion.with(context)
                .load(Global.urls.login_vts.value)
                .setJsonObjectBody(params)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        // do stuff with the result or error
                        try {
                            if (result != null) Log.v("result", result.toString());
                            // JSONObject jsnobject = new JSONObject(jsond);
                            int statusCode = result.get("statuscode").getAsInt();
                            boolean status = result.get("status").getAsBoolean();

                            if(!status){
                                if(statusCode == 3){
                                    lcallback.onDone(null,3, result.get("msg").getAsString(), status);
                                    Intent i = new Intent(context,verifyotp.class);
                                    i.putExtra("mobile",result.get("data").getAsString());
                                    i.putExtra("email",params.get("email").getAsString());
                                    i.putExtra("resend",true);
                                    context.startActivity(i);
                                    ((Activity)context).finish();
                                    return;
                                }
                            }
                            if (statusCode == 2) {
                                //registered_vts();
                                if(lcallback!=null){
                                    lcallback.onDone(null,2 ,result.get("msg").getAsString(), status);
                                }
                                return;
                            }

                            Gson gson = new Gson();
                            Type listType = new TypeToken<model_loginusr_vts>() {
                            }.getType();
                            model_loginusr_vts login = gson.fromJson(result.get("data"), listType);
                            model_loginusr lo = new model_loginusr();
                            lo.setDriverid(login.getDriverid());
                            lo.setEmail(login.getEmail());
                            lo.setFullname(login.getFullname());
                            lo.setSessiondetails(login.getSessiondetails() + "");
                            lo.setStatus((result.get("status").getAsBoolean() ? 1 : 0));

                            Global.loginusr = lo;


                            if (Global.loginusr.getStatus() == 1) {
                                SHP.set(context, SHP.ids.uid, Global.loginusr.getDriverid() + "");
                                String g = Global.loginusr.getSessiondetails().toString();

                                SHP.set(context, SHP.ids.sessionid, g);

                                //Toast.makeText(login.this, "Login Successfully!", Toast.LENGTH_SHORT).show();
                                Intent i = new Intent(context, dash.class);
                                if(lcallback!=null){
                                    lcallback.onDone(login,1, result.get("msg").getAsString(), status);
                                }
                            } else {
                                Toast.makeText(context, "Login Failed! " + Global.loginusr.getErrmsg(), Toast.LENGTH_SHORT).show();
                                if(lcallback!=null){
                                    lcallback.onDone(login,3, result.get("msg").getAsString(), status);
                                }
                            }

                        } catch (Exception ea) {
                            Toast.makeText(context, "Error: " + ea.getMessage(), Toast.LENGTH_LONG).show();
                            ea.printStackTrace();
                            if(lcallback!=null){
                                lcallback.onDone(null,4, ea.getMessage(), false);
                            }
                        }
                        Global.hideProgress(loader);

                    }
                });
    }

}
