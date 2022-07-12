package com.thresholdsoft.apollofeedback.ui.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class DeviceRegistrationRequest implements Serializable {

    @SerializedName("DEVICEDATE")
    @Expose
    private String devicedate;
    @SerializedName("DEVICETYPE")
    @Expose
    private String devicetype;
    @SerializedName("FCMKEY")
    @Expose
    private String fcmkey;
    @SerializedName("LATITUDE")
    @Expose
    private String latitude;
    @SerializedName("LOGITUDE")
    @Expose
    private String logitude;
    @SerializedName("MACID")
    @Expose
    private String macid;
    @SerializedName("STOREID")
    @Expose
    private String storeid;
    @SerializedName("TERMINALID")
    @Expose
    private String terminalid;
    @SerializedName("USERID")
    @Expose
    private String userid;
    private final static long serialVersionUID = -1295438871402812597L;

    public String getDevicedate() {
        return devicedate;
    }

    public void setDevicedate(String devicedate) {
        this.devicedate = devicedate;
    }

    public String getDevicetype() {
        return devicetype;
    }

    public void setDevicetype(String devicetype) {
        this.devicetype = devicetype;
    }

    public String getFcmkey() {
        return fcmkey;
    }

    public void setFcmkey(String fcmkey) {
        this.fcmkey = fcmkey;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLogitude() {
        return logitude;
    }

    public void setLogitude(String logitude) {
        this.logitude = logitude;
    }

    public String getMacid() {
        return macid;
    }

    public void setMacid(String macid) {
        this.macid = macid;
    }

    public String getStoreid() {
        return storeid;
    }

    public void setStoreid(String storeid) {
        this.storeid = storeid;
    }

    public String getTerminalid() {
        return terminalid;
    }

    public void setTerminalid(String terminalid) {
        this.terminalid = terminalid;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }
}
