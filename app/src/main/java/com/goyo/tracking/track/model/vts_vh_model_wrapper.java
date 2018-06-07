package com.goyo.tracking.track.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by llc on 11/22/2017.
 */

public class vts_vh_model_wrapper {

    @SerializedName("total_distance")
    public Double total_distance;

    @SerializedName("segment")
    public List<history_item_model> vhlist;

    @SerializedName("mx_spd")
    public Double mx_spd;

    @SerializedName("travel_tm")
    public String travel_tm;

    @SerializedName("avg_spd")
    public Integer  avg_spd;

}
