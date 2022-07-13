package com.thresholdsoft.apollofeedback.ui.storesetup.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class StoreListResponseModel {
    @Expose
    @SerializedName("status")
    private boolean status;
    @Expose
    @SerializedName("message")
    private String message;
    @Expose
    @SerializedName("StoreList")
    private ArrayList<StoreListObj> storeListArr;

    public boolean isStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public ArrayList<StoreListObj> getStoreListArr() {
        return storeListArr;
    }

    public static class StoreListObj implements Serializable {
        @Expose
        @SerializedName("StoreId")
        private String storeId;
        @Expose
        @SerializedName("StoreName")
        private String storeName;
        @Expose
        @SerializedName("Address")
        private String address;
        @Expose
        @SerializedName("ContactNumber")
        private String contactNumber;
        @Expose
        @SerializedName("City")
        private String city;
        @Expose
        @SerializedName("DataAreaId")
        private String dataAreaId;

        public void setStoreId(String storeId) {
            this.storeId = storeId;
        }

        public void setStoreName(String storeName) {
            this.storeName = storeName;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public void setContactNumber(String contactNumber) {
            this.contactNumber = contactNumber;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public String getStoreId() {
            return storeId;
        }

        public String getStoreName() {
            return storeName;
        }

        public String getAddress() {
            return address;
        }

        public String getContactNumber() {
            return contactNumber;
        }

        public String getCity() {
            return city;
        }

        public String getDataAreaId() {
            return dataAreaId;
        }

        @Override
        public String toString() {
            return storeName;
        }
    }
}
