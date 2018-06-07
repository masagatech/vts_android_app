package com.goyo.tracking.track.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by llc on 12/5/2017.
 */

public class rpt_mileage_model {
    @SerializedName("_id")
    public ids id;

    @SerializedName("vno")
    public String vno;

    @SerializedName("avgspd")
    public Double avgspd;

    @SerializedName("frmdate")
    public String frmdate;

    @SerializedName("todate")
    public String todate;

    @SerializedName("maxspd")
    public Integer maxspd;

    @SerializedName("milege")
    public Double milege;


    public class ids{
        @SerializedName("vhid")
        public String vhid;
    }
}
