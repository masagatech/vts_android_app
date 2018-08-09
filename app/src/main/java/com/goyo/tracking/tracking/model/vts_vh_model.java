package com.goyo.tracking.tracking.model;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.gson.annotations.SerializedName;
import com.goyo.tracking.tracking.adapters.vh_rec_adapter;

import java.util.List;

/**
 * Created by llc on 10/28/2017.
 */

public class vts_vh_model {

    public int getId() {

        if (id == 0) {
            this.id = vtsid;
        }
        return id;
    }

    @Override
     public String toString() {
        return  regno ;
    }


    public void setId(int id) {
        this.id = id;
    }

    @SerializedName("id")
    public int id;

    @SerializedName("vhid")
    public String vhid;

    @SerializedName("ico")
    public String ico;

    @SerializedName("btrst")
    public String btrst;

    @SerializedName("btr")
    public String btr;

    @SerializedName("rng")
    public String rng;

    @SerializedName("acc")
    public String acc;

    @SerializedName("vno")
    public String vno;

    @SerializedName("sertm")
    public String sertm;

    @SerializedName("lstspdtm")
    public String lstspdtm;

    @SerializedName("lstspd")
    public int lstspd;

    @SerializedName("speed")
    public int speed;

    @SerializedName("bearing")
    public Double bearing;

    @SerializedName("sat")
    public int sat;


    @SerializedName("alwspeed")
    public int alwspeed;

    @SerializedName("loc")
    public Double[] loc;

    @SerializedName("lon")
    public Double lon;

    @SerializedName("lat")
    public Double lat;

    @SerializedName("gsmsig")
    public String gsmsig;

    @SerializedName("actvt")
    public String actvt;

    @SerializedName("gpstm")
    public String gpstm;

    @SerializedName("isp")
    public boolean isp;

    @SerializedName("d1")
    public int d1;

    @SerializedName("oe")
    public int oe;

    @SerializedName("extra")
    public vts_vh_model_sub extra;

    @SerializedName("vhtyp")
    public String vehtyp;

    @SerializedName("devtyp")
    public String devtyp;

    @SerializedName("vtsid")
    public Integer vtsid;

    @SerializedName("sim")
    public String sim;

    @SerializedName("vrg")
    public String regno;

    @SerializedName("capacity")
    public String capacity = "0";
    @SerializedName("chasisno")
    public String chasisno = "";
    @SerializedName("insurance")
    public String insurance = "";
    @SerializedName("make")
    public String make = "";
    @SerializedName("model")
    public String model = "";
    @SerializedName("pucexp")
    public String pucexp = "";
    @SerializedName("tyrs")
    public String tyrs = "";

    @SerializedName("advanced")
    public List<vts_vh_advanced> advanced;


    public transient boolean ischeck = false;

    public transient int olsts = 0;
    public transient String timeago = "...";
    public transient vh_rec_adapter.vhsts vhst = vh_rec_adapter.vhsts.all;
    public transient Marker vhmarker;
    public transient boolean islststsAvail = false;
    public transient LatLng Lastloc;
    public transient boolean isselected;
    public transient int indexid;
    public transient String lastEvt = "";


    //public transient MarkerData markerData =  new MarkerData();

    public vts_vh_model() {
    }
}
