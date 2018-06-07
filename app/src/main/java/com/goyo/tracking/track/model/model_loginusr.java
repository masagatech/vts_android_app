package com.goyo.tracking.track.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by mTech on 26-Apr-2017.
 */
public class model_loginusr {

    public long getDriverid() {
        return driverid;
    }

    public void setDriverid(long driverid) {
        this.driverid = driverid;
    }

    @SerializedName("uid")
    private long driverid = 0;


    @SerializedName("status")
    private int status;
    @SerializedName("errcode")
    private String errcode;
    @SerializedName("errmsg")
    private String errmsg;

    public int getAppver() {
        return appver;
    }

    public void setAppver(int appver) {
        this.appver = appver;
    }

    public String getConf() {
        return conf;
    }

    public void setConf(String conf) {
        this.conf = conf;
    }

    @SerializedName("conf")
    private String conf;

    @SerializedName("appver")
    private int appver;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getErrcode() {
        return errcode;
    }

    public void setErrcode(String errcode) {
        this.errcode = errcode;
    }

    public String getErrmsg() {
        return errmsg;
    }

    public void setErrmsg(String errmsg) {
        this.errmsg = errmsg;
    }

    public String getUcode() {
        return ucode;
    }

    public void setUcode(String ucode) {
        this.ucode = ucode;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getUtype() {
        return utype;
    }

    public void setUtype(String utype) {
        this.utype = utype;
    }

    public String getLastlogindt() {
        return lastlogindt;
    }

    public void setLastlogindt(String lastlogindt) {
        this.lastlogindt = lastlogindt;
    }

    @SerializedName("ucode")
    private String ucode;
    @SerializedName("mobile")
    private String mobile;
    @SerializedName("email")
    private String email;
    @SerializedName("fullname")
    private String fullname;
    @SerializedName("utype")
    private String utype;
    @SerializedName("lastlogindt")
    private String lastlogindt;

    public String getEnttid() {
        return enttid;
    }

    public void setEnttid(String enttid) {
        this.enttid = enttid;
    }

    @SerializedName("enttid")
    private String enttid;


    public Object getSessiondetails() {
        return sessiondetails;
    }

    public void setSessiondetails(String sessiondetails) {
        this.sessiondetails = sessiondetails;
    }

    @SerializedName("sessiondetails")
    public Object sessiondetails;



}
