package com.thresholdsoft.apollofeedback.ui.itemspayment.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class CrossShellResponse implements Serializable {


    @SerializedName("CHRONIC_CONDITION")
    @Expose
    private String chronicCondition;
    @SerializedName("RequestStatus")
    @Expose
    private Integer requestStatus;
    @SerializedName("ReturnMessage")
    @Expose
    private String returnMessage;
    @SerializedName("billval_3months_band")
    @Expose
    private String billval3monthsBand;
    @SerializedName("class")
    @Expose
    private String _class;
    @SerializedName("crossselling")
    @Expose
    private List<Crossselling> crossselling = null;
    @SerializedName("upselling")
    @Expose
    private List<Upselling> upselling = null;
    @SerializedName("user_active_status")
    @Expose
    private String userActiveStatus;
    @SerializedName("user_chronic_active_status")
    @Expose
    private String userChronicActiveStatus;

    public String getChronicCondition() {
        return chronicCondition;
    }

    public void setChronicCondition(String chronicCondition) {
        this.chronicCondition = chronicCondition;
    }

    public CrossShellResponse withChronicCondition(String chronicCondition) {
        this.chronicCondition = chronicCondition;
        return this;
    }

    public Integer getRequestStatus() {
        return requestStatus;
    }

    public void setRequestStatus(Integer requestStatus) {
        this.requestStatus = requestStatus;
    }

    public CrossShellResponse withRequestStatus(Integer requestStatus) {
        this.requestStatus = requestStatus;
        return this;
    }

    public String getReturnMessage() {
        return returnMessage;
    }

    public void setReturnMessage(String returnMessage) {
        this.returnMessage = returnMessage;
    }

    public CrossShellResponse withReturnMessage(String returnMessage) {
        this.returnMessage = returnMessage;
        return this;
    }

    public String getBillval3monthsBand() {
        return billval3monthsBand;
    }

    public void setBillval3monthsBand(String billval3monthsBand) {
        this.billval3monthsBand = billval3monthsBand;
    }

    public CrossShellResponse withBillval3monthsBand(String billval3monthsBand) {
        this.billval3monthsBand = billval3monthsBand;
        return this;
    }

    public String getClass_() {
        return _class;
    }

    public void setClass_(String _class) {
        this._class = _class;
    }

    public CrossShellResponse withClass(String _class) {
        this._class = _class;
        return this;
    }

    public List<Crossselling> getCrossselling() {
        return crossselling;
    }

    public void setCrossselling(List<Crossselling> crossselling) {
        this.crossselling = crossselling;
    }

    public CrossShellResponse withCrossselling(List<Crossselling> crossselling) {
        this.crossselling = crossselling;
        return this;
    }

    public List<Upselling> getUpselling() {
        return upselling;
    }

    public void setUpselling(List<Upselling> upselling) {
        this.upselling = upselling;
    }

    public CrossShellResponse withUpselling(List<Upselling> upselling) {
        this.upselling = upselling;
        return this;
    }

    public String getUserActiveStatus() {
        return userActiveStatus;
    }

    public void setUserActiveStatus(String userActiveStatus) {
        this.userActiveStatus = userActiveStatus;
    }

    public CrossShellResponse withUserActiveStatus(String userActiveStatus) {
        this.userActiveStatus = userActiveStatus;
        return this;
    }

    public String getUserChronicActiveStatus() {
        return userChronicActiveStatus;
    }

    public void setUserChronicActiveStatus(String userChronicActiveStatus) {
        this.userChronicActiveStatus = userChronicActiveStatus;
    }

    public CrossShellResponse withUserChronicActiveStatus(String userChronicActiveStatus) {
        this.userChronicActiveStatus = userChronicActiveStatus;
        return this;
    }


    public class Crossselling implements Serializable {

        @SerializedName("id")
        @Expose
        private Integer id;
        @SerializedName("itemid")
        @Expose
        private String itemid;
        @SerializedName("itemname")
        @Expose
        private String itemname;
        @SerializedName("reason")
        @Expose
        private String reason;
        @SerializedName("stockqty")
        @Expose
        private Double stockqty;

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public Crossselling withId(Integer id) {
            this.id = id;
            return this;
        }

        public String getItemid() {
            return itemid;
        }

        public void setItemid(String itemid) {
            this.itemid = itemid;
        }

        public Crossselling withItemid(String itemid) {
            this.itemid = itemid;
            return this;
        }

        public String getItemname() {
            return itemname;
        }

        public void setItemname(String itemname) {
            this.itemname = itemname;
        }

        public Crossselling withItemname(String itemname) {
            this.itemname = itemname;
            return this;
        }

        public String getReason() {
            return reason;
        }

        public void setReason(String reason) {
            this.reason = reason;
        }

        public Crossselling withReason(String reason) {
            this.reason = reason;
            return this;
        }

        public Double getStockqty() {
            return stockqty;
        }

        public void setStockqty(Double stockqty) {
            this.stockqty = stockqty;
        }

        public Crossselling withStockqty(Double stockqty) {
            this.stockqty = stockqty;
            return this;
        }

    }

    public class Upselling implements Serializable {

        @SerializedName("id")
        @Expose
        private Integer id;
        @SerializedName("itemid")
        @Expose
        private String itemid;
        @SerializedName("itemname")
        @Expose
        private String itemname;
        @SerializedName("reason")
        @Expose
        private String reason;
        @SerializedName("stockqty")
        @Expose
        private Double stockqty;

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public Upselling withId(Integer id) {
            this.id = id;
            return this;
        }

        public String getItemid() {
            return itemid;
        }

        public void setItemid(String itemid) {
            this.itemid = itemid;
        }

        public Upselling withItemid(String itemid) {
            this.itemid = itemid;
            return this;
        }

        public String getItemname() {
            return itemname;
        }

        public void setItemname(String itemname) {
            this.itemname = itemname;
        }

        public Upselling withItemname(String itemname) {
            this.itemname = itemname;
            return this;
        }

        public String getReason() {
            return reason;
        }

        public void setReason(String reason) {
            this.reason = reason;
        }

        public Upselling withReason(String reason) {
            this.reason = reason;
            return this;
        }

        public Double getStockqty() {
            return stockqty;
        }

        public void setStockqty(Double stockqty) {
            this.stockqty = stockqty;
        }

        public Upselling withStockqty(Double stockqty) {
            this.stockqty = stockqty;
            return this;
        }

    }

}







