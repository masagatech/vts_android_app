package com.goyo.tracking.track.globals;

import android.app.ProgressDialog;

import com.goyo.tracking.track.model.model_loginusr;

/**
 * Created by llc on 10/28/2017.
 */

public class Global {
    public final static String REST_URL = "http://track.goyo.in:8082/goyoapi";
    public final static String REST_REPORT_URL = "http://track.goyo.in:8085";
    public final static String VTS_URL = "http://35.154.114.229:6979/";
   // public final static String VTS_URL = "http://35.154.51.217:6979/";


    public enum urls {
        testurl("testurl", REST_URL),
        uploadimage("", Global.REST_URL + "/uploads"),
        getlogin("getlogin", REST_URL + "/getLogin"),
        getlogout("getlogout", REST_URL + "/getLogout"),
        getvahicleupdates("getvahicleupdates", VTS_URL + "goyoapi/tripapi/getvahicleupdates"),
        gettrackboard("gettrackboard", REST_URL + "/tripapi/gettrackboard"),
        gettrackboardHistory("gettrackboardHistory", VTS_URL + "/goyoapi/tripapi/getHistory"),
        getReport("getreport", VTS_URL + "/goyoapi/tripapi/report"),
        getReportSchool("getReports", REST_REPORT_URL + "/PostReports"),
        getVehicleDetails("getvehicledetails", REST_URL + "/getVehicleDetails");

        public String key;
        public String value;

        private urls(String toKey, String toValue) {
            key = toKey;
            value = toValue;
        }

    }
    public static model_loginusr loginusr;
    public static ProgressDialog prgdialog;


    public static void showProgress(ProgressDialog prd) {
        prd.setCancelable(false);
        if (!prd.isShowing()) prd.show();
    }
    public static void hideProgress(ProgressDialog prd) {
        prd.dismiss();
    }



}
