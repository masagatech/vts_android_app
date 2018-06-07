package com.goyo.tracking.track.model;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

/**
 * Created by llc on 11/17/2017.
 */

public class history_item_model {


    @SerializedName("dist")
    public double dist;

    @SerializedName("dur")
    public String dur;

    @SerializedName("sttm")
    public String sttm;

    @SerializedName("entm")
    public String entm;

    @SerializedName("encdpoly")
    public String encdpoly;

    @SerializedName("mxspd")
    public int mxspd;

    @SerializedName("trktyp")
    public String trktyp;

}
