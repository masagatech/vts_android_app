package com.goyo.tracking.tracking.model;

import com.google.gson.Gson;

public class vts_vh_command_model {

    private String Cmd;
    private String Imei;
    private String Extra;
    private String UID;
    private String Ucode;
    private String Platform;
    private String IP;
    private String DeviceID;
    private String Src;
    private long Uniqid;


    /*Properties*/

    public long getUniqid() {
        return Uniqid;
    }

    public void setUniqid(long commandUniqid) {
        Uniqid = commandUniqid;
    }

    public String getCmd() {
        return Cmd;
    }

    public void setCmd(String cmd) {
        Cmd = cmd;
    }

    public String getImei() {
        return Imei;
    }

    public void setImei(String imei) {
        Imei = imei;
    }

    public String getExtra() {
        return Extra;
    }

    public void setExtra(String extra) {
        Extra = extra;
    }

    public String getUID() {
        return UID;
    }

    public void setUID(String UID) {
        this.UID = UID;
    }

    public String getUcode() {
        return Ucode;
    }

    public void setUcode(String ucode) {
        Ucode = ucode;
    }

    public String getPlatform() {
        return Platform;
    }

    public void setPlatform(String platform) {
        Platform = platform;
    }

    public String getIP() {
        return IP;
    }

    public void setIP(String IP) {
        this.IP = IP;
    }

    public String getDeviceID() {
        return DeviceID;
    }

    public void setDeviceID(String deviceID) {
        DeviceID = deviceID;
    }

    public String getSrc() {
        return Src;
    }

    public void setSrc(String src) {
        Src = src;
    }

    public String toJsonString() {
        Gson g = new Gson();

        return g.toJson(this);
    }


}
