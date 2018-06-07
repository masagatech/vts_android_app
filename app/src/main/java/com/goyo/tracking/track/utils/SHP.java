package com.goyo.tracking.track.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by mTech on 28-Feb-2017.
 */
public class SHP {
    public static SharedPreferences preferences;
    public static  SharedPreferences.Editor editor;

    //set string
    public static void set(Context cnx, ids id, Object value) {

        preferences = PreferenceManager.getDefaultSharedPreferences(cnx);
        editor = preferences.edit();
        if( id.type =="bool"){
            editor.putBoolean(id.name, (Boolean)value);
        }else{
            editor.putString(id.name, value.toString());
        }

        editor.commit();
    }

    public  static Object get(Context cnx, ids id, Object _d){
        preferences = PreferenceManager.getDefaultSharedPreferences(cnx);
        if( id.type == "bool")
            return  preferences.getBoolean(id.name, (Boolean)_d);
        else
            return  preferences.getString(id.name, _d.toString());
    }



    public enum ids{
        shortcut("shortcut","bool"),
        uid("uid","str"),
        ucode("ucode","str"),
        sessionid("sessionid","str"),
        islogin("islogin","bool"),
        sett_lang("sett_lang","str"),
        selectedvh("selvh","str"),
        mapselecTheme("mapseltheme","str"),
        lastsynctime("lastsynctime","str"),
        commanduid("commanduid","str"),
        hsid("hsid","str");

        private String name;
        private String type;
        private ids(String toNameValue, String toTypeValue) {
            name = toNameValue;
            type = toTypeValue;
        }

    }

}


