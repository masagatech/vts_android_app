package com.goyo.tracking.track.realmmodel;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class vehiclesettings_model extends RealmObject {

    @PrimaryKey
    String vhId = "";
    String imei = "";
    boolean isSpeedAlert = true;
    boolean isActive = true;
    String vhname = "";
    String vhregno = "";
    String vhmodel = "";
    String settings1 = "";
    String settings2 = "";


    public String getVhId() {
        return vhId;
    }

    public void setVhId(String vhId) {
        this.vhId = vhId;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public boolean isSpeedAlert() {
        return isSpeedAlert;
    }

    public void setSpeedAlert(boolean speedAlert) {
        isSpeedAlert = speedAlert;
    }

    public String getSettings1() {
        return settings1;
    }

    public void setSettings1(String settings1) {
        this.settings1 = settings1;
    }

    public String getSettings2() {
        return settings2;
    }

    public void setSettings2(String settings2) {
        this.settings2 = settings2;
    }

    public String getVhname() {
        return vhname;
    }

    public void setVhname(String vhname) {
        this.vhname = vhname;
    }

    public String getVhregno() {
        return vhregno;
    }

    public void setVhregno(String vhregno) {
        this.vhregno = vhregno;
    }

    public String getVhmodel() {
        return vhmodel;
    }

    public void setVhmodel(String vhmodel) {
        this.vhmodel = vhmodel;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }
}
