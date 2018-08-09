package com.goyo.tracking.tracking.helpers;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

/**
 * Created by llc on 10/30/2017.
 */

public class ClusterMarkerItem implements ClusterItem {

    private final LatLng mPosition;
    private  String mTitle = null;
    private  String mSnippet = null;

    public ClusterMarkerItem(double lat, double lng) {
        mPosition = new LatLng(lat, lng);
    }

    public ClusterMarkerItem(double lat, double lng, String title, String snippet) {
        mPosition = new LatLng(lat, lng);
        mTitle = title;
        mSnippet = snippet;
    }

    @Override
    public LatLng getPosition() {
        return mPosition;
    }


    public String getTitle() {
        return mTitle;
    }


    public String getSnippet() {
        return mSnippet;
    }

}
