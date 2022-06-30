package com.thresholdsoft.apollofeedback.commonmodels;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class FeedbackSystemRequest {

    @SerializedName("SiteId")
    @Expose
    private String siteId;
    @SerializedName("TerminalId")
    @Expose
    private String terminalId;
    @SerializedName("ISFeedback")
    @Expose
    private Integer iSFeedback;
    @SerializedName("FeedbackRate")
    @Expose
    private String feedbackRate;

    public String getSiteId() {
        return siteId;
    }

    public void setSiteId(String siteId) {
        this.siteId = siteId;
    }

    public String getTerminalId() {
        return terminalId;
    }

    public void setTerminalId(String terminalId) {
        this.terminalId = terminalId;
    }

    public Integer getISFeedback() {
        return iSFeedback;
    }

    public void setISFeedback(Integer iSFeedback) {
        this.iSFeedback = iSFeedback;
    }

    public String getFeedbackRate() {
        return feedbackRate;
    }

    public void setFeedbackRate(String feedbackRate) {
        this.feedbackRate = feedbackRate;
    }

}
