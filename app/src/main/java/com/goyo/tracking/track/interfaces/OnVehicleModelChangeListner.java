package com.goyo.tracking.track.interfaces;

import com.goyo.tracking.track.model.vts_vh_model;
import com.goyo.tracking.track.model.vts_vh_status_model;

public interface OnVehicleModelChangeListner {

        void onChange(vts_vh_model vh, int position, String evt);
}
