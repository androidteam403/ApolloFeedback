package com.thresholdsoft.apollofeedback.ui.offersnow.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class OneApolloAPITransactionRequest implements Serializable {

    @SerializedName("RequestData")
    @Expose
    private RequestData requestData;

    public RequestData getRequestData() {
        return requestData;
    }

    public void setRequestData(RequestData requestData) {
        this.requestData = requestData;
    }

    public static class RequestData implements Serializable {

        @SerializedName("Action")
        @Expose
        private String action;
        @SerializedName("Coupon")
        @Expose
        private String coupon;
        @SerializedName("CustomerID")
        @Expose
        private String customerID;
        @SerializedName("DocNum")
        @Expose
        private String docNum;
        @SerializedName("MobileNum")
        @Expose
        private String mobileNum;
        @SerializedName("OTP")
        @Expose
        private String otp;
        @SerializedName("Points")
        @Expose
        private String points;
        @SerializedName("ReqBy")
        @Expose
        private String reqBy;
        @SerializedName("RRNO")
        @Expose
        private String rrno;
        @SerializedName("StoreId")
        @Expose
        private String storeId;
        @SerializedName("Type")
        @Expose
        private String type;
        @SerializedName("Url")
        @Expose
        private String url;

        public String getAction() {
            return action;
        }

        public void setAction(String action) {
            this.action = action;
        }

        public String getCoupon() {
            return coupon;
        }

        public void setCoupon(String coupon) {
            this.coupon = coupon;
        }

        public String getCustomerID() {
            return customerID;
        }

        public void setCustomerID(String customerID) {
            this.customerID = customerID;
        }

        public String getDocNum() {
            return docNum;
        }

        public void setDocNum(String docNum) {
            this.docNum = docNum;
        }

        public String getMobileNum() {
            return mobileNum;
        }

        public void setMobileNum(String mobileNum) {
            this.mobileNum = mobileNum;
        }

        public String getOtp() {
            return otp;
        }

        public void setOtp(String otp) {
            this.otp = otp;
        }

        public String getPoints() {
            return points;
        }

        public void setPoints(String points) {
            this.points = points;
        }

        public String getReqBy() {
            return reqBy;
        }

        public void setReqBy(String reqBy) {
            this.reqBy = reqBy;
        }

        public String getRrno() {
            return rrno;
        }

        public void setRrno(String rrno) {
            this.rrno = rrno;
        }

        public String getStoreId() {
            return storeId;
        }

        public void setStoreId(String storeId) {
            this.storeId = storeId;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

    }
}