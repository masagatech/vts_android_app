package com.goyo.tracking.track.fragment;


import android.icu.text.DisplayContext;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.goyo.tracking.track.R;
import com.goyo.tracking.track.adapters.vh_adapter;
import com.goyo.tracking.track.globals.Global;
import com.goyo.tracking.track.model.vts_vh_model;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.lang.reflect.Type;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class vehicles extends Fragment {

    @BindView(R.id.lstVehicles)
    ListView lstVeh;

    //variables
   private List<vts_vh_model> vhs;

    public vehicles() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.vehicles, container, false);
        ButterKnife.bind(this, view);
        // binding vehicles
        bindVehicles();
        return view;
    }

    private void bindVehicles(){


        JsonObject json = new JsonObject();
        json.addProperty("flag", "vehicle");
        json.addProperty("enttid", "1");
        json.addProperty("uid", "5");
        json.addProperty("utype", "admin");
        json.addProperty("issysadmin", "false");
        json.addProperty("wsautoid", "5");
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
                            displayVehicles();

                        } catch (Exception ea) {
                            ea.printStackTrace();
                        }
                        // menu_refresh.setEnabled(false);

                    }
                });
    }


    private void displayVehicles(){

        if (vhs.size() > 0) {
           // findViewById(R.id.txtNodata).setVisibility(View.GONE);
            vh_adapter adapter = new vh_adapter(this.getActivity(), vhs, getResources());
            lstVeh.setAdapter(adapter);
            adapter.notifyDataSetChanged();

        } else {
           // findViewById(R.id.txtNodata).setVisibility(View.VISIBLE);
        }

    }

}
