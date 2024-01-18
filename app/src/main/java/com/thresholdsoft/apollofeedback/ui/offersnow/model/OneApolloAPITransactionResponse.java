package com.thresholdsoft.apollofeedback.ui.offersnow.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class OneApolloAPITransactionResponse implements Serializable {

    @SerializedName("OneApolloProcessResult")
    @Expose
    private OneApolloProcessResult oneApolloProcessResult;
    @SerializedName("RequestStatus")
    @Expose
    private Integer requestStatus;
    @SerializedName("ReturnMessage")
    @Expose
    private String returnMessage;
    private final static long serialVersionUID = 5639857730061869213L;

    public OneApolloProcessResult getOneApolloProcessResult() {
        return oneApolloProcessResult;
    }

    public void setOneApolloProcessResult(OneApolloProcessResult oneApolloProcessResult) {
        this.oneApolloProcessResult = oneApolloProcessResult;
    }

    public Integer getRequestStatus() {
        return requestStatus;
    }

    public void setRequestStatus(Integer requestStatus) {
        this.requestStatus = requestStatus;
    }

    public String getReturnMessage() {
        return returnMessage;
    }

    public void setReturnMessage(String returnMessage) {
        this.returnMessage = returnMessage;
    }

    public class OneApolloProcessResult implements Serializable {

        @SerializedName("Action")
        @Expose
        private String action;
        @SerializedName("AvailableCredits")
        @Expose
        private Integer availableCredits;
        @SerializedName("AvailablePoints")
        @Expose
        private String availablePoints;
        @SerializedName("BlockedCredits")
        @Expose
        private Integer blockedCredits;
        @SerializedName("BurnedCredits")
        @Expose
        private Integer burnedCredits;
        @SerializedName("DiscountAmount")
        @Expose
        private Object discountAmount;
        @SerializedName("DiscountPercentage")
        @Expose
        private Object discountPercentage;
        @SerializedName("EarnedCredits")
        @Expose
        private Float earnedCredits;
        @SerializedName("ExpiredCredits")
        @Expose
        private Integer expiredCredits;
        @SerializedName("Message")
        @Expose
        private String message;
        @SerializedName("MigratedCredits")
        @Expose
        private Integer migratedCredits;
        @SerializedName("MobileNum")
        @Expose
        private String mobileNum;
        @SerializedName("Name")
        @Expose
        private String name;
        @SerializedName("OTP")
        @Expose
        private Object otp;
        @SerializedName("PromotionalHCs")
        @Expose
        private Integer promotionalHCs;
        @SerializedName("RRNO")
        @Expose
        private Object rrno;
        @SerializedName("RedeemPoints")
        @Expose
        private Object redeemPoints;
        @SerializedName("Tier")
        @Expose
        private String tier;
        @SerializedName("TotalAvailableHCs")
        @Expose
        private Float totalAvailableHCs;
        @SerializedName("TotalSpent")
        @Expose
        private Integer totalSpent;
        @SerializedName("VoucherCode")
        @Expose
        private Object voucherCode;
        @SerializedName("status")
        @Expose
        private String status;
        private final static long serialVersionUID = 6461863538111386411L;

        public String getAction() {
            return action;
        }

        public void setAction(String action) {
            this.action = action;
        }

        public Integer getAvailableCredits() {
            return availableCredits;
        }

        public void setAvailableCredits(Integer availableCredits) {
            this.availableCredits = availableCredits;
        }

        public String getAvailablePoints() {
            return availablePoints;
        }

        public void setAvailablePoints(String availablePoints) {
            this.availablePoints = availablePoints;
        }

        public Integer getBlockedCredits() {
            return blockedCredits;
        }

        public void setBlockedCredits(Integer blockedCredits) {
            this.blockedCredits = blockedCredits;
        }

        public Integer getBurnedCredits() {
            return burnedCredits;
        }

        public void setBurnedCredits(Integer burnedCredits) {
            this.burnedCredits = burnedCredits;
        }

        public Object getDiscountAmount() {
            return discountAmount;
        }

        public void setDiscountAmount(Object discountAmount) {
            this.discountAmount = discountAmount;
        }

        public Object getDiscountPercentage() {
            return discountPercentage;
        }

        public void setDiscountPercentage(Object discountPercentage) {
            this.discountPercentage = discountPercentage;
        }

        public Float getEarnedCredits() {
            return earnedCredits;
        }

        public void setEarnedCredits(Float earnedCredits) {
            this.earnedCredits = earnedCredits;
        }

        public Integer getExpiredCredits() {
            return expiredCredits;
        }

        public void setExpiredCredits(Integer expiredCredits) {
            this.expiredCredits = expiredCredits;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public Integer getMigratedCredits() {
            return migratedCredits;
        }

        public void setMigratedCredits(Integer migratedCredits) {
            this.migratedCredits = migratedCredits;
        }

        public String getMobileNum() {
            return mobileNum;
        }

        public void setMobileNum(String mobileNum) {
            this.mobileNum = mobileNum;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Object getOtp() {
            return otp;
        }

        public void setOtp(Object otp) {
            this.otp = otp;
        }

        public Integer getPromotionalHCs() {
            return promotionalHCs;
        }

        public void setPromotionalHCs(Integer promotionalHCs) {
            this.promotionalHCs = promotionalHCs;
        }

        public Object getRrno() {
            return rrno;
        }

        public void setRrno(Object rrno) {
            this.rrno = rrno;
        }

        public Object getRedeemPoints() {
            return redeemPoints;
        }

        public void setRedeemPoints(Object redeemPoints) {
            this.redeemPoints = redeemPoints;
        }

        public String getTier() {
            return tier;
        }

        public void setTier(String tier) {
            this.tier = tier;
        }

        public Float getTotalAvailableHCs() {
            return totalAvailableHCs;
        }

        public void setTotalAvailableHCs(Float totalAvailableHCs) {
            this.totalAvailableHCs = totalAvailableHCs;
        }

        public Integer getTotalSpent() {
            return totalSpent;
        }

        public void setTotalSpent(Integer totalSpent) {
            this.totalSpent = totalSpent;
        }

        public Object getVoucherCode() {
            return voucherCode;
        }

        public void setVoucherCode(Object voucherCode) {
            this.voucherCode = voucherCode;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

    }
}
