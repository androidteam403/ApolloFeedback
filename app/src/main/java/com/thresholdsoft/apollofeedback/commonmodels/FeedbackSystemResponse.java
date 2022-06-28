package com.thresholdsoft.apollofeedback.commonmodels;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;


public class FeedbackSystemResponse implements Serializable {

    @SerializedName("customerScreen")
    @Expose
    private CustomerScreen customerScreen;
    @SerializedName("customerofferScreen")
    @Expose
    private CustomerofferScreen customerofferScreen;
    @SerializedName("feedbackScreen")
    @Expose
    private FeedbackScreen feedbackScreen;
    @SerializedName("homeScreen")
    @Expose
    private HomeScreen homeScreen;
    @SerializedName("iscustomerScreen")
    @Expose
    private Boolean iscustomerScreen;
    @SerializedName("isfeedbackScreen")
    @Expose
    private Boolean isfeedbackScreen;
    @SerializedName("isofferScreen")
    @Expose
    private Boolean isofferScreen;
    @SerializedName("ispaymentScreen")
    @Expose
    private Boolean ispaymentScreen;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("status")
    @Expose
    private Boolean status;

    public CustomerScreen getCustomerScreen() {
        return customerScreen;
    }

    public void setCustomerScreen(CustomerScreen customerScreen) {
        this.customerScreen = customerScreen;
    }

    public CustomerofferScreen getCustomerofferScreen() {
        return customerofferScreen;
    }

    public void setCustomerofferScreen(CustomerofferScreen customerofferScreen) {
        this.customerofferScreen = customerofferScreen;
    }

    public FeedbackScreen getFeedbackScreen() {
        return feedbackScreen;
    }

    public void setFeedbackScreen(FeedbackScreen feedbackScreen) {
        this.feedbackScreen = feedbackScreen;
    }

    public HomeScreen getHomeScreen() {
        return homeScreen;
    }

    public void setHomeScreen(HomeScreen homeScreen) {
        this.homeScreen = homeScreen;
    }

    public Boolean getIscustomerScreen() {
        return iscustomerScreen;
    }

    public void setIscustomerScreen(Boolean iscustomerScreen) {
        this.iscustomerScreen = iscustomerScreen;
    }

    public Boolean getIsfeedbackScreen() {
        return isfeedbackScreen;
    }

    public void setIsfeedbackScreen(Boolean isfeedbackScreen) {
        this.isfeedbackScreen = isfeedbackScreen;
    }

    public Boolean getIsofferScreen() {
        return isofferScreen;
    }

    public void setIsofferScreen(Boolean isofferScreen) {
        this.isofferScreen = isofferScreen;
    }

    public Boolean getIspaymentScreen() {
        return ispaymentScreen;
    }

    public void setIspaymentScreen(Boolean ispaymentScreen) {
        this.ispaymentScreen = ispaymentScreen;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public class CustomerScreen implements Serializable {

        @SerializedName("billNumber")
        @Expose
        private String billNumber;
        @SerializedName("docnum")
        @Expose
        private String docnum;
        @SerializedName("imageURL")
        @Expose
        private List<Object> imageURL = null;
        @SerializedName("noofSkus")
        @Expose
        private Integer noofSkus;
        @SerializedName("payment")
        @Expose
        private Payment payment;

        public String getBillNumber() {
            return billNumber;
        }

        public void setBillNumber(String billNumber) {
            this.billNumber = billNumber;
        }

        public String getDocnum() {
            return docnum;
        }

        public void setDocnum(String docnum) {
            this.docnum = docnum;
        }

        public List<Object> getImageURL() {
            return imageURL;
        }

        public void setImageURL(List<Object> imageURL) {
            this.imageURL = imageURL;
        }

        public Integer getNoofSkus() {
            return noofSkus;
        }

        public void setNoofSkus(Integer noofSkus) {
            this.noofSkus = noofSkus;
        }

        public Payment getPayment() {
            return payment;
        }

        public void setPayment(Payment payment) {
            this.payment = payment;
        }

    }

    public class CustomerofferScreen implements Serializable {

        @SerializedName("customerName")
        @Expose
        private String customerName;
        @SerializedName("customerType")
        @Expose
        private String customerType;
        @SerializedName("imageUrl")
        @Expose
        private List<ImageUrl> imageUrl = null;

        public String getCustomerName() {
            return customerName;
        }

        public void setCustomerName(String customerName) {
            this.customerName = customerName;
        }

        public String getCustomerType() {
            return customerType;
        }

        public void setCustomerType(String customerType) {
            this.customerType = customerType;
        }

        public List<ImageUrl> getImageUrl() {
            return imageUrl;
        }

        public void setImageUrl(List<ImageUrl> imageUrl) {
            this.imageUrl = imageUrl;
        }

    }

    public class FeedbackScreen implements Serializable {

        @SerializedName("feedbackstatus")
        @Expose
        private Integer feedbackstatus;
        @SerializedName("feedbackurl")
        @Expose
        private Object feedbackurl;
        @SerializedName("paymentStatus")
        @Expose
        private Boolean paymentStatus;
        @SerializedName("transactionid")
        @Expose
        private Object transactionid;

        public Integer getFeedbackstatus() {
            return feedbackstatus;
        }

        public void setFeedbackstatus(Integer feedbackstatus) {
            this.feedbackstatus = feedbackstatus;
        }

        public Object getFeedbackurl() {
            return feedbackurl;
        }

        public void setFeedbackurl(Object feedbackurl) {
            this.feedbackurl = feedbackurl;
        }

        public Boolean getPaymentStatus() {
            return paymentStatus;
        }

        public void setPaymentStatus(Boolean paymentStatus) {
            this.paymentStatus = paymentStatus;
        }

        public Object getTransactionid() {
            return transactionid;
        }

        public void setTransactionid(Object transactionid) {
            this.transactionid = transactionid;
        }

    }

    public class HomeScreen implements Serializable {

        @SerializedName("City")
        @Expose
        private String city;
        @SerializedName("State")
        @Expose
        private String state;
        @SerializedName("imageUrl")
        @Expose
        private List<Object> imageUrl = null;
        @SerializedName("siteAddress")
        @Expose
        private String siteAddress;
        @SerializedName("siteId")
        @Expose
        private String siteId;
        @SerializedName("siteName")
        @Expose
        private String siteName;
        @SerializedName("terminal")
        @Expose
        private String terminal;

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public String getState() {
            return state;
        }

        public void setState(String state) {
            this.state = state;
        }

        public List<Object> getImageUrl() {
            return imageUrl;
        }

        public void setImageUrl(List<Object> imageUrl) {
            this.imageUrl = imageUrl;
        }

        public String getSiteAddress() {
            return siteAddress;
        }

        public void setSiteAddress(String siteAddress) {
            this.siteAddress = siteAddress;
        }

        public String getSiteId() {
            return siteId;
        }

        public void setSiteId(String siteId) {
            this.siteId = siteId;
        }

        public String getSiteName() {
            return siteName;
        }

        public void setSiteName(String siteName) {
            this.siteName = siteName;
        }

        public String getTerminal() {
            return terminal;
        }

        public void setTerminal(String terminal) {
            this.terminal = terminal;
        }

    }

    public class ImageUrl implements Serializable {

        @SerializedName("name")
        @Expose
        private String name;
        @SerializedName("sorting")
        @Expose
        private String sorting;
        @SerializedName("url")
        @Expose
        private String url;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getSorting() {
            return sorting;
        }

        public void setSorting(String sorting) {
            this.sorting = sorting;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

    }

    public class Payment implements Serializable {

        @SerializedName("amouttobeCollected")
        @Expose
        private String amouttobeCollected = "0.0";
        ;
        @SerializedName("collectedAmount")
        @Expose
        private String collectedAmount;
        @SerializedName("discountValue")
        @Expose
        private String discountValue = "0.0";
        @SerializedName("giftAmount")
        @Expose
        private String giftAmount;
        @SerializedName("qrCode")
        @Expose
        private String qrCode;
        @SerializedName("totalValue")
        @Expose
        private String totalValue;

        public String getAmouttobeCollected() {
            return amouttobeCollected;
        }

        public void setAmouttobeCollected(String amouttobeCollected) {
            this.amouttobeCollected = amouttobeCollected;
        }

        public String getCollectedAmount() {
            return collectedAmount;
        }

        public void setCollectedAmount(String collectedAmount) {
            this.collectedAmount = collectedAmount;
        }

        public String getDiscountValue() {
            return discountValue;
        }

        public void setDiscountValue(String discountValue) {
            this.discountValue = discountValue;
        }

        public String getGiftAmount() {
            return giftAmount;
        }

        public void setGiftAmount(String giftAmount) {
            this.giftAmount = giftAmount;
        }

        public String getQrCode() {
            return qrCode;
        }

        public void setQrCode(String qrCode) {
            this.qrCode = qrCode;
        }

        public String getTotalValue() {
            return totalValue;
        }

        public void setTotalValue(String totalValue) {
            this.totalValue = totalValue;
        }

    }
}