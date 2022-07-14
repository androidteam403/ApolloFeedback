package com.thresholdsoft.apollofeedback.ui.offersnow.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class DcOffersNowResponse implements Serializable
    {
        @SerializedName("message")
        @Expose
        private Object message;
        @SerializedName("success")
        @Expose
        private Boolean success;
        @SerializedName("data")
        @Expose
        private Data data;
        @SerializedName("zcServerDateTime")
        @Expose
        private String zcServerDateTime;
        @SerializedName("zcServerIp")
        @Expose
        private String zcServerIp;
        @SerializedName("zcServerHost")
        @Expose
        private String zcServerHost;

        public Object getMessage() {
            return message;
        }

        public void setMessage(Object message) {
            this.message = message;
        }

        public Boolean getSuccess() {
            return success;
        }

        public void setSuccess(Boolean success) {
            this.success = success;
        }

        public Data getData() {
            return data;
        }

        public void setData(Data data) {
            this.data = data;
        }

        public class Data implements Serializable
        {

            @SerializedName("listData")
            @Expose
            private ListData listData;
            @SerializedName("zcDebugLogs")
            @Expose
            private ZcDebugLogs zcDebugLogs;
            private final static long serialVersionUID = 8472433513837169585L;

            public ListData getListData() {
                return listData;
            }

            public void setListData(ListData listData) {
                this.listData = listData;
            }

            public class ListData implements Serializable
            {

                @SerializedName("records")
                @Expose
                private String records;
                @SerializedName("select")
                @Expose
                private Boolean select;
                @SerializedName("total")
                @Expose
                private Integer total;
                @SerializedName("page")
                @Expose
                private Integer page;
                @SerializedName("rows")
                @Expose
                private List<Row> rows = null;
                @SerializedName("zc_extra")
                @Expose
                private Object zcExtra;
                @SerializedName("pivotData")
                @Expose
                private Object pivotData;
                @SerializedName("aggregation")
                @Expose
                private Object aggregation;
                @SerializedName("size")
                @Expose
                private Integer size;
                private final static long serialVersionUID = 1657761506120015720L;

                public String getRecords() {
                    return records;
                }

                public void setRecords(String records) {
                    this.records = records;
                }

                public Boolean getSelect() {
                    return select;
                }

                public void setSelect(Boolean select) {
                    this.select = select;
                }

                public Integer getTotal() {
                    return total;
                }

                public void setTotal(Integer total) {
                    this.total = total;
                }

                public Integer getPage() {
                    return page;
                }

                public void setPage(Integer page) {
                    this.page = page;
                }

                public List<Row> getRows() {
                    return rows;
                }

                public void setRows(List<Row> rows) {
                    this.rows = rows;
                }

                public class Row implements Serializable
                {

                    @SerializedName("uid")
                    @Expose
                    private String uid;
                    @SerializedName("title")
                    @Expose
                    private String title;
                    @SerializedName("status")
                    @Expose
                    private Status status;
                    @SerializedName("exception_sites")
                    @Expose
                    private String exceptionSites;
                    @SerializedName("dc_pos_offer")
                    @Expose
                    private DcPosOffer dcPosOffer;
                    @SerializedName("pos_media_library")
                    @Expose
                    private List<PosMediaLibrary> posMediaLibrary = null;

                    public String getUid() {
                        return uid;
                    }

                    public void setUid(String uid) {
                        this.uid = uid;
                    }

                    public String getTitle() {
                        return title;
                    }

                    public void setTitle(String title) {
                        this.title = title;
                    }

                    public Status getStatus() {
                        return status;
                    }

                    public void setStatus(Status status) {
                        this.status = status;
                    }

                    public class Status implements Serializable
                    {

                        @SerializedName("uid")
                        @Expose
                        private String uid;
                        @SerializedName("name")
                        @Expose
                        private String name;
                        @SerializedName("other")
                        @Expose
                        private Other other;
                        @SerializedName("icon")
                        @Expose
                        private Object icon;

                        public String getUid() {
                            return uid;
                        }

                        public void setUid(String uid) {
                            this.uid = uid;
                        }

                        public String getName() {
                            return name;
                        }

                        public void setName(String name) {
                            this.name = name;
                        }

                        public Other getOther() {
                            return other;
                        }

                        public void setOther(Other other) {
                            this.other = other;
                        }

                        public class Other implements Serializable
                        {

                            @SerializedName("color")
                            @Expose
                            private Object color;
                            private final static long serialVersionUID = 7439370009338612798L;

                            public Object getColor() {
                                return color;
                            }

                            public void setColor(Object color) {
                                this.color = color;
                            }

                        }

                        public Object getIcon() {
                            return icon;
                        }

                        public void setIcon(Object icon) {
                            this.icon = icon;
                        }

                    }

                    public String getExceptionSites() {
                        return exceptionSites;
                    }

                    public void setExceptionSites(String exceptionSites) {
                        this.exceptionSites = exceptionSites;
                    }

                    public DcPosOffer getDcPosOffer() {
                        return dcPosOffer;
                    }

                    public void setDcPosOffer(DcPosOffer dcPosOffer) {
                        this.dcPosOffer = dcPosOffer;
                    }

                    public class DcPosOffer implements Serializable
                    {

                        @SerializedName("uid")
                        @Expose
                        private String uid;
                        @SerializedName("master_dc")
                        @Expose
                        private MasterDc masterDc;
                        private final static long serialVersionUID = 8140487802645849686L;

                        public String getUid() {
                            return uid;
                        }

                        public void setUid(String uid) {
                            this.uid = uid;
                        }

                        public MasterDc getMasterDc() {
                            return masterDc;
                        }

                        public void setMasterDc(MasterDc masterDc) {
                            this.masterDc = masterDc;
                        }
                        public class MasterDc implements Serializable
                        {

                            @SerializedName("uid")
                            @Expose
                            private String uid;
                            @SerializedName("code")
                            @Expose
                            private String code;
                            private final static long serialVersionUID = 1634789410226539341L;

                            public String getUid() {
                                return uid;
                            }

                            public void setUid(String uid) {
                                this.uid = uid;
                            }

                            public String getCode() {
                                return code;
                            }

                            public void setCode(String code) {
                                this.code = code;
                            }

                        }

                    }

                    public List<PosMediaLibrary> getPosMediaLibrary() {
                        return posMediaLibrary;
                    }

                    public void setPosMediaLibrary(List<PosMediaLibrary> posMediaLibrary) {
                        this.posMediaLibrary = posMediaLibrary;
                    }

                    public class PosMediaLibrary implements Serializable
                    {

                        @SerializedName("uid")
                        @Expose
                        private String uid;
                        @SerializedName("name")
                        @Expose
                        private String name;
                        @SerializedName("file")
                        @Expose
                        private List<File> file = null;
                        private final static long serialVersionUID = -2617385933043887563L;

                        public String getUid() {
                            return uid;
                        }

                        public void setUid(String uid) {
                            this.uid = uid;
                        }

                        public String getName() {
                            return name;
                        }

                        public void setName(String name) {
                            this.name = name;
                        }

                        public List<File> getFile() {
                            return file;
                        }

                        public void setFile(List<File> file) {
                            this.file = file;
                        }

                        public class File implements Serializable
                        {

                            @SerializedName("size")
                            @Expose
                            private Integer size;
                            @SerializedName("saved")
                            @Expose
                            private Boolean saved;
                            @SerializedName("name")
                            @Expose
                            private String name;
                            @SerializedName("contentType")
                            @Expose
                            private String contentType;
                            @SerializedName("path")
                            @Expose
                            private String path;
                            @SerializedName("fullPath")
                            @Expose
                            private String fullPath;
                            @SerializedName("created_info")
                            @Expose
                            private CreatedInfo createdInfo;

                            public Integer getSize() {
                                return size;
                            }

                            public void setSize(Integer size) {
                                this.size = size;
                            }

                            public Boolean getSaved() {
                                return saved;
                            }

                            public void setSaved(Boolean saved) {
                                this.saved = saved;
                            }

                            public String getName() {
                                return name;
                            }

                            public void setName(String name) {
                                this.name = name;
                            }

                            public String getContentType() {
                                return contentType;
                            }

                            public void setContentType(String contentType) {
                                this.contentType = contentType;
                            }

                            public String getPath() {
                                return path;
                            }

                            public void setPath(String path) {
                                this.path = path;
                            }

                            public String getFullPath() {
                                return fullPath;
                            }

                            public void setFullPath(String fullPath) {
                                this.fullPath = fullPath;
                            }

                            public CreatedInfo getCreatedInfo() {
                                return createdInfo;
                            }

                            public void setCreatedInfo(CreatedInfo createdInfo) {
                                this.createdInfo = createdInfo;
                            }

                            public class CreatedInfo implements Serializable
                            {

                                @SerializedName("created_on")
                                @Expose
                                private Long createdOn;
                                @SerializedName("user_id")
                                @Expose
                                private String userId;
                                @SerializedName("user_code")
                                @Expose
                                private String userCode;
                                @SerializedName("user_name")
                                @Expose
                                private String userName;
                                @SerializedName("login_unique")
                                @Expose
                                private String loginUnique;
                                @SerializedName("email")
                                @Expose
                                private String email;
                                @SerializedName("phone")
                                @Expose
                                private String phone;
                                @SerializedName("role_code")
                                @Expose
                                private String roleCode;
                                @SerializedName("role_name")
                                @Expose
                                private String roleName;
                                private final static long serialVersionUID = -7375378895420160359L;

                                public Long getCreatedOn() {
                                    return createdOn;
                                }

                                public void setCreatedOn(Long createdOn) {
                                    this.createdOn = createdOn;
                                }

                                public String getUserId() {
                                    return userId;
                                }

                                public void setUserId(String userId) {
                                    this.userId = userId;
                                }

                                public String getUserCode() {
                                    return userCode;
                                }

                                public void setUserCode(String userCode) {
                                    this.userCode = userCode;
                                }

                                public String getUserName() {
                                    return userName;
                                }

                                public void setUserName(String userName) {
                                    this.userName = userName;
                                }

                                public String getLoginUnique() {
                                    return loginUnique;
                                }

                                public void setLoginUnique(String loginUnique) {
                                    this.loginUnique = loginUnique;
                                }

                                public String getEmail() {
                                    return email;
                                }

                                public void setEmail(String email) {
                                    this.email = email;
                                }

                                public String getPhone() {
                                    return phone;
                                }

                                public void setPhone(String phone) {
                                    this.phone = phone;
                                }

                                public String getRoleCode() {
                                    return roleCode;
                                }

                                public void setRoleCode(String roleCode) {
                                    this.roleCode = roleCode;
                                }

                                public String getRoleName() {
                                    return roleName;
                                }

                                public void setRoleName(String roleName) {
                                    this.roleName = roleName;
                                }

                            }


                        }


                    }

                }

                public Object getZcExtra() {
                    return zcExtra;
                }

                public void setZcExtra(Object zcExtra) {
                    this.zcExtra = zcExtra;
                }

                public Object getPivotData() {
                    return pivotData;
                }

                public void setPivotData(Object pivotData) {
                    this.pivotData = pivotData;
                }

                public Object getAggregation() {
                    return aggregation;
                }

                public void setAggregation(Object aggregation) {
                    this.aggregation = aggregation;
                }

                public Integer getSize() {
                    return size;
                }

                public void setSize(Integer size) {
                    this.size = size;
                }

            }

            public ZcDebugLogs getZcDebugLogs() {
                return zcDebugLogs;
            }

            public void setZcDebugLogs(ZcDebugLogs zcDebugLogs) {
                this.zcDebugLogs = zcDebugLogs;
            }

            public class ZcDebugLogs implements Serializable
            {

                @SerializedName("1")
                @Expose
                private List<_1> _1 = null;


                public List<_1> get1() {
                    return _1;
                }

                public void set1(List<_1> _1) {
                    this._1 = _1;
                }

                public class _1 implements Serializable
                {

                    @SerializedName("context")
                    @Expose
                    private String context;
                    @SerializedName("time")
                    @Expose
                    private String time;
                    @SerializedName("place")
                    @Expose
                    private String place;
                    @SerializedName("log")
                    @Expose
                    private String log;

                    public String getContext() {
                        return context;
                    }

                    public void setContext(String context) {
                        this.context = context;
                    }

                    public String getTime() {
                        return time;
                    }

                    public void setTime(String time) {
                        this.time = time;
                    }

                    public String getPlace() {
                        return place;
                    }

                    public void setPlace(String place) {
                        this.place = place;
                    }

                    public String getLog() {
                        return log;
                    }

                    public void setLog(String log) {
                        this.log = log;
                    }

                }

            }

        }

        public String getZcServerDateTime() {
            return zcServerDateTime;
        }

        public void setZcServerDateTime(String zcServerDateTime) {
            this.zcServerDateTime = zcServerDateTime;
        }

        public String getZcServerIp() {
            return zcServerIp;
        }

        public void setZcServerIp(String zcServerIp) {
            this.zcServerIp = zcServerIp;
        }

        public String getZcServerHost() {
            return zcServerHost;
        }

        public void setZcServerHost(String zcServerHost) {
            this.zcServerHost = zcServerHost;
        }

    }








