package com.goyo.tracking.track.interfaces;

import com.goyo.tracking.track.model.vts_vh_model;

import java.util.List;

public interface OnVehiclesReadyListner {
    void onReady(List<vts_vh_model> vh);
}
