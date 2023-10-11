package com.thresholdsoft.apollofeedback.ui.scannedprescriptions.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class KioskSelfCheckOutTransactionRequest implements Serializable {

    @SerializedName("PRESC_STRING")
    @Expose
    private String prescString;
    @SerializedName("PRESC_ID")
    @Expose
    private String prescId;
    @SerializedName("FROMDATE")
    @Expose
    private String fromdate;
    @SerializedName("TODATE")
    @Expose
    private String todate;
    @SerializedName("STOREID")
    @Expose
    private String storeid;
    @SerializedName("KIOSKID")
    @Expose
    private String kioskid;
    @SerializedName("CUSTOMERNAME")
    @Expose
    private String customername;
    @SerializedName("MOBILENO")
    @Expose
    private String mobileno;
    @SerializedName("KIOSKLINK")
    @Expose
    private String kiosklink;
    @SerializedName("CREATEDDATE")
    @Expose
    private String createddate;
    @SerializedName("STATUSID")
    @Expose
    private Integer statusid;
    @SerializedName("REQUESTTYPE")
    @Expose
    private String requesttype;
    private final static long serialVersionUID = 5159723451107369013L;

    public String getPrescString() {
        return prescString;
    }

    public void setPrescString(String prescString) {
        this.prescString = prescString;
    }

    public String getPrescId() {
        return prescId;
    }

    public void setPrescId(String prescId) {
        this.prescId = prescId;
    }

    public String getFromdate() {
        return fromdate;
    }

    public void setFromdate(String fromdate) {
        this.fromdate = fromdate;
    }

    public String getTodate() {
        return todate;
    }

    public void setTodate(String todate) {
        this.todate = todate;
    }

    public String getStoreid() {
        return storeid;
    }

    public void setStoreid(String storeid) {
        this.storeid = storeid;
    }

    public String getKioskid() {
        return kioskid;
    }

    public void setKioskid(String kioskid) {
        this.kioskid = kioskid;
    }

    public String getCustomername() {
        return customername;
    }

    public void setCustomername(String customername) {
        this.customername = customername;
    }

    public String getMobileno() {
        return mobileno;
    }

    public void setMobileno(String mobileno) {
        this.mobileno = mobileno;
    }

    public String getKiosklink() {
        return kiosklink;
    }

    public void setKiosklink(String kiosklink) {
        this.kiosklink = kiosklink;
    }

    public String getCreateddate() {
        return createddate;
    }

    public void setCreateddate(String createddate) {
        this.createddate = createddate;
    }

    public Integer getStatusid() {
        return statusid;
    }

    public void setStatusid(Integer statusid) {
        this.statusid = statusid;
    }

    public String getRequesttype() {
        return requesttype;
    }

    public void setRequesttype(String requesttype) {
        this.requesttype = requesttype;
    }

}
