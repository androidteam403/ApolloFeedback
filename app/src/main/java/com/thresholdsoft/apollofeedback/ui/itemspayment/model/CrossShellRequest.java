package com.thresholdsoft.apollofeedback.ui.itemspayment.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class CrossShellRequest  implements Serializable {



        @SerializedName("mobileno")
        @Expose
        private String mobileno;
        @SerializedName("storeId")
        @Expose
        private String storeId;

        public String getMobileno() {
            return mobileno;
        }

        public void setMobileno(String mobileno) {
            this.mobileno = mobileno;
        }

        public CrossShellRequest withMobileno(String mobileno) {
            this.mobileno = mobileno;
            return this;
        }

        public String getStoreId() {
            return storeId;
        }

        public void setStoreId(String storeId) {
            this.storeId = storeId;
        }

        public CrossShellRequest withStoreId(String storeId) {
            this.storeId = storeId;
            return this;
        }

    }



