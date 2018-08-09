package com.goyo.tracking.tracking.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by mTech on 26-Apr-2017.
 */
public class model_loginusr_vts {

    public long getDriverid() {
        return driverid;
    }

    public void setDriverid(long driverid) {
        this.driverid = driverid;
    }

    @SerializedName("id")
    private long driverid = 0;


    @SerializedName("active")
    private boolean status;
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

    public boolean getStatus() {
        return status;
    }

    public void setStatus(boolean status) {
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
    @SerializedName("display_name")
    private String fullname;
    @SerializedName("utype")
    private String utype;
    @SerializedName("lstlogin")
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

    public void setSessiondetails(int sessiondetails) {
        this.sessiondetails = sessiondetails;
    }

    @SerializedName("session")
    public int sessiondetails;



}
