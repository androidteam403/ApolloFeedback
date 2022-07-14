package com.thresholdsoft.apollofeedback.ui.offersnow.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class DcOffersNowRequest implements Serializable {


    @SerializedName("dc_code")
    @Expose
    private String dcCode;

    public String getDcCode() {
        return dcCode;
    }

    public void setDcCode(String dcCode) {
        this.dcCode = dcCode;
    }


}


