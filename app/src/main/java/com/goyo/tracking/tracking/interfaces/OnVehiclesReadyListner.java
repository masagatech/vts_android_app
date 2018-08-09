package com.goyo.tracking.tracking.interfaces;

import com.goyo.tracking.tracking.model.vts_vh_model;

import java.util.List;

public interface OnVehiclesReadyListner {
    void onReady(List<vts_vh_model> vh);
}
