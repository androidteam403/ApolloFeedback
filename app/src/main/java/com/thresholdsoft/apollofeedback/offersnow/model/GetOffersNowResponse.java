package com.thresholdsoft.apollofeedback.offersnow.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class GetOffersNowResponse implements Serializable {

    @SerializedName("offers_now")
    @Expose
    private List<OffersNow> offersNow = null;

    public List<OffersNow> getOffersNow() {
        return offersNow;
    }

    public void setOffersNow(List<OffersNow> offersNow) {
        this.offersNow = offersNow;
    }

    public class OffersNow implements Serializable {

        @SerializedName("image")
        @Expose
        private String image;

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }

    }
}
