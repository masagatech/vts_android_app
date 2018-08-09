package com.goyo.tracking.tracking.model;
import com.google.gson.annotations.SerializedName;

/**
 * Created by mTech on 22-Apr-2017.
 */
public class model_mytrips {

    @SerializedName("id")
    public int id;

    @SerializedName("nm")
    public String nm;

    @SerializedName("date")
    public String date;

    @SerializedName("time")
    public String time;

    @SerializedName("btch")
    public String btch;

    @SerializedName("pd")
    public String pd;

    @SerializedName("sts")
    public String sts;

    @SerializedName("stsi")
    public String stsi;

    @SerializedName("trpid")
    public String tripid;

    @SerializedName("spdlmt")
    public String spdlmt;

    @SerializedName("entt")
    public String entt;

    @SerializedName("vhid")
    public String vhid;

    public model_mytrips()
    {}
}
