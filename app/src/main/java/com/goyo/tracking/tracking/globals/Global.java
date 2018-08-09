package com.goyo.tracking.tracking.globals;

import android.app.ProgressDialog;
import android.content.Context;

import com.goyo.tracking.tracking.dashboard.dash;
import com.goyo.tracking.tracking.model.model_loginusr;
import com.goyo.tracking.tracking.utils.SHP;

/**
 * Created by llc on 10/28/2017.
 */

public class Global {
    public final static String REST_URL = "http://track.goyo.in:8082/goyoapi";
    //public final static String REST_URL = "http://192.168.1.100:8082/goyoapi";
    public final static String REST_REPORT_URL = "http://track.goyo.in:8085";
    public final static String UPLOAD_IMAGE = "http://192.168.43.10:6979/";
    public final static String VTS_URL = "http://35.154.114.229:6979/";
    //public final static String VTS_URL = "http://192.168.1.101:6979/";
    //public final static String REST_REPORT_URL = "http://192.168.1.103:8085";

    public enum urls {
        testurl("testurl", REST_URL),
        uploadimage("", Global.REST_URL + "/uploads"),
        getlogin("getlogin", REST_URL + "/getLogin"),
        getlogout("getlogout", REST_URL + "/getLogout"),

        login_vts("login", VTS_URL + "goyoapi/tripapi/user/login"),
        session_vts("loginsession", VTS_URL + "goyoapi/tripapi/user/loginsession"),
        logout_vts("logout", VTS_URL + "goyoapi/tripapi/user/logout"),
        register_vts("register", VTS_URL + "goyoapi/tripapi/user/register"),
        veryfyOtp_vts("verifyotp", VTS_URL + "goyoapi/tripapi/user/verifyotp"),
        resendOtp_vts("resendotp", VTS_URL + "goyoapi/tripapi/user/resendotp"),
        deviceCheck("deviceCheck", VTS_URL + "goyoapi/tripapi/device/check"),
        savevehicle("savevehicle", VTS_URL + "goyoapi/tripapi/device/activate"),
        getvehicledetails("getvehicledetails", VTS_URL + "goyoapi/tripapi/vehicle/getVehicleDetails"),

        //savefuel("savefuel", VTS_URL + "goyoapi/tripapi/fuel/add"),
        savefuel("savefuel", REST_URL + "/saveFuelEntry"),
        //getfuel("getfuel", VTS_URL + "goyoapi/tripapi/fuel/get"),
        getFuelEntry("getFuelEntry", REST_URL + "/getFuelEntry"),
        editfuel("editfuel", VTS_URL + "goyoapi/tripapi/fuel/get/edit"),



        getmytrips("getmytrips", REST_URL + "/tripapi"),

        getvahicleupdates("getvahicleupdates", VTS_URL + "goyoapi/tripapi/getvahicleupdates"),        gettrackboard("gettrackboard", REST_URL + "/tripapi/gettrackboard"),
        gettrackboardHistory("gettrackboardHistory", VTS_URL + "/goyoapi/tripapi/getHistory"),
        getReport("getreport", VTS_URL + "/goyoapi/tripapi/report"),
        getReportSchool("getReports", REST_REPORT_URL + "/PostReports"),

        uplodimages("UPLOAD_IMAGE", REST_URL + "/getVehicleDetails"),

        getVehicleDetails("getvehicledetails", REST_URL + "/getVehicleDetails"),
        getVehicleDetails_vts("getvehicledetails_vts", VTS_URL + "goyoapi/tripapi/vehicle/getVehicleByUID"),
        getScheduleReports("getScheduleReports", REST_REPORT_URL + "/getScheduleReports")
        ;

        public String key;
        public String value;

        private urls(String toKey, String toValue) {
            key = toKey;
            value = toValue;
        }

    }


    public final static String start = "1";
    public final static String done = "2";
    public final static String pause = "pause";
    public final static String cancel = "3";
    public final static String pending = "0";

    public final static String pickedupdrop = "1";
    public final static String absent = "2";

    public static model_loginusr loginusr;
    public static ProgressDialog prgdialog;


    public static void showProgress(ProgressDialog prd) {
        prd.setCancelable(false);
        if (!prd.isShowing()) prd.show();
    }
    public static void hideProgress(ProgressDialog prd) {
        prd.dismiss();
    }

    public static boolean getLoginType(Context ct, String str){
       return SHP.get(ct, SHP.ids.signedact,"normal").equals(str);
    }

}
