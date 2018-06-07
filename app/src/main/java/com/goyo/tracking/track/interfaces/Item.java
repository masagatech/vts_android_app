package com.goyo.tracking.track.interfaces;

import android.view.LayoutInflater;
import android.view.View;

/**
 * Created by llc on 12/11/2017.
 */

public interface Item {
    public int getViewType();
    public View getView(LayoutInflater inflater, View convertView);
}