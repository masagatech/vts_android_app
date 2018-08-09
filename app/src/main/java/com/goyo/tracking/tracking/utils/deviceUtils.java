package com.goyo.tracking.tracking.utils;

import com.goyo.tracking.tracking.R;
import com.goyo.tracking.tracking.adapters.vh_rec_adapter;
import com.goyo.tracking.tracking.model.vts_vh_model;

import java.util.concurrent.TimeUnit;

/**
 * Created by llc on 11/3/2017.
 */

public class deviceUtils {

    public static int getBatrry(String btry) {
        switch (btry) {
            case "CHRG":
                return R.drawable.ic_action_btrychrg;
            case "100":
                return R.drawable.ic_action_btry100;
            case "70":
                return R.drawable.ic_action_btry80;
            case "50":
                return R.drawable.ic_action_btry50;
            case "30":
                return R.drawable.ic_action_btry20;
            case "10":
                return R.drawable.ic_action_btry20;
            case "1":
                return R.drawable.ic_action_btry0;
            case "0":
                return R.drawable.ic_action_btry0;
            default:
                return R.drawable.ic_action_btry0;

        }

    }

    public static int getNetwork(String ntwrk) {
        switch (ntwrk) {
            case "100":
                return R.drawable.ic_action_netw4;
            case "75":
                return R.drawable.ic_action_netw3;
            case "50":
                return R.drawable.ic_action_netw2;
            case "25":
                return R.drawable.ic_action_netw1;
            case "0":
                return R.drawable.ic_action_netw0;
            default:
                return R.drawable.ic_action_netw0;
        }

    }

    public static int getAcc(String acc) {
        if(acc == null) acc = "";
        switch (acc) {
            case "1":
                return R.drawable.ic_action_online_acc;
            case "0":
                return R.drawable.ic_action_offline;
            default:
                return R.drawable.ic_action_offline;
        }

    }

    public static String getPassedTimeFromCreation(Long currentTime, vts_vh_model mod) {
        mod.olsts = 3;

        if (TimeUnit.MILLISECONDS.toSeconds(currentTime) <= 45) {
            mod.olsts = 1;
            mod.vhst = vh_rec_adapter.vhsts.online;
            return "a few seconds ago";
        } else if (TimeUnit.MILLISECONDS.toSeconds(currentTime) <= 90) {
            mod.olsts = 1;
            mod.vhst = vh_rec_adapter.vhsts.online;
            return "a minute ago";
        } else if (TimeUnit.MILLISECONDS.toMinutes(currentTime) <= 45) {
            if (TimeUnit.MILLISECONDS.toMinutes(currentTime) <= 6) {
                mod.olsts = 1;
                mod.vhst = vh_rec_adapter.vhsts.online;
            } else if (TimeUnit.MILLISECONDS.toMinutes(currentTime) <= 8) {
                mod.olsts = 2;
                mod.vhst = vh_rec_adapter.vhsts.online;
            } else {
                mod.olsts = 3;
                mod.vhst = vh_rec_adapter.vhsts.offline;
            }
            return TimeUnit.MILLISECONDS.toMinutes(currentTime) + " minutes ago";
        } else if (TimeUnit.MILLISECONDS.toMinutes(currentTime) <= 90) {
            mod.olsts = 3;
            mod.vhst = vh_rec_adapter.vhsts.offline;
            return "an hour ago";
        } else if (TimeUnit.MILLISECONDS.toHours(currentTime) <= 22) {
            mod.olsts = 3;
            mod.vhst = vh_rec_adapter.vhsts.offline;
            return TimeUnit.MILLISECONDS.toHours(currentTime) + " hours ago";
        } else if (TimeUnit.MILLISECONDS.toHours(currentTime) <= 36) {
            mod.olsts = 3;
            mod.vhst = vh_rec_adapter.vhsts.offline;
            return "a day ago";
        } else if (TimeUnit.MILLISECONDS.toDays(currentTime) <= 25) {
            mod.olsts = 3;
            mod.vhst = vh_rec_adapter.vhsts.offline;
            return TimeUnit.MILLISECONDS.toDays(currentTime) + " days ago";
        } else if (TimeUnit.MILLISECONDS.toDays(currentTime) <= 45) {
            mod.olsts = 3;
            mod.vhst = vh_rec_adapter.vhsts.offline;
            return "a month ago";
        } else if (TimeUnit.MILLISECONDS.toDays(currentTime) <= 345) {
            mod.olsts = 3;
            mod.vhst = vh_rec_adapter.vhsts.offline;
            return TimeUnit.MILLISECONDS.toDays(currentTime) + " months ago";
        } else if (TimeUnit.MILLISECONDS.toDays(currentTime) <= 545) {
            mod.olsts = 3;
            mod.vhst = vh_rec_adapter.vhsts.offline;
            return "a year ago";
        } else { // (days > 545)
            mod.olsts = 3;
            mod.vhst = vh_rec_adapter.vhsts.offline;
            return 2 + " years ago";
        }

    }
}
