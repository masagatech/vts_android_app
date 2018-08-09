package com.goyo.tracking.tracking.model;

/**
 * Created by llc on 11/2/2017.
 */

public class vts_vh_status_model {

    private int online;

    public int getOnline() {
        return online;
    }

    public void setOnline(int online) {
        this.online = online;
    }

    public void addOnline(int online) {
        this.online += online;
    }

    public int getOffline() {
        return offline;
    }

    public void setOffline(int offline) {
        this.offline = offline;
    }

    public void addOffline(int offline) {
        this.offline += offline;
    }

    public int getMoving() {
        return moving;
    }

    public void setMoving(int moving) {
        this.moving = moving;
    }

    public void addMoving(int moving) {
        this.moving += moving;
    }

    public int getAll() {
        return all;
    }

    public void setAll(int all) {
        this.all = all;
    }

    public int getSpeedvoi() {
        return speedvoi;
    }

    public void setSpeedvoi(int speedvoi) {
        this.speedvoi = speedvoi;
    }

    public void addSpeedvoi(int spd) {
        this.speedvoi += spd;
    }

    private int offline;
    private int moving;
    private int all;
    private int speedvoi;


}
