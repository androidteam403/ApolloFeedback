package com.thresholdsoft.apollofeedback.ui.itemspayment.model;

public class UpsellCrosssellModel {

    private Integer id;
    private String itemid;
    private String itemname;
    private String reason;
    private Double stockqty;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getItemid() {
        return itemid;
    }

    public void setItemid(String itemid) {
        this.itemid = itemid;
    }

    public String getItemname() {
        return itemname;
    }

    public void setItemname(String itemname) {
        this.itemname = itemname;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Double getStockqty() {
        return stockqty;
    }

    public void setStockqty(Double stockqty) {
        this.stockqty = stockqty;
    }
}
