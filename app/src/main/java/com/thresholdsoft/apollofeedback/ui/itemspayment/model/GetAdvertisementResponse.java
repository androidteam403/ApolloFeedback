package com.thresholdsoft.apollofeedback.ui.itemspayment.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class GetAdvertisementResponse implements Serializable {

    @SerializedName("advertisement")
    @Expose
    private List<Advertisement> advertisement = null;

    public List<Advertisement> getAdvertisement() {
        return advertisement;
    }

    public void setAdvertisement(List<Advertisement> advertisement) {
        this.advertisement = advertisement;
    }

    public class Advertisement implements Serializable {

        @SerializedName("advertisement_image")
        @Expose
        private String advertisementImage;

        public String getAdvertisementImage() {
            return advertisementImage;
        }

        public void setAdvertisementImage(String advertisementImage) {
            this.advertisementImage = advertisementImage;
        }

    }
}