package com.thresholdsoft.apollofeedback.ui.storesetup;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;

import androidx.databinding.DataBindingUtil;

import com.thresholdsoft.apollofeedback.R;
import com.thresholdsoft.apollofeedback.base.BaseActivity;
import com.thresholdsoft.apollofeedback.databinding.ActivityStoreSetupBinding;
import com.thresholdsoft.apollofeedback.ui.model.DeviceRegistrationResponse;
import com.thresholdsoft.apollofeedback.ui.model.UserAddress;
import com.thresholdsoft.apollofeedback.ui.storesetup.dialog.GetStoresDialog;
import com.thresholdsoft.apollofeedback.ui.storesetup.model.StoreListResponseModel;
import com.thresholdsoft.apollofeedback.ui.storesetup.model.StoreSetupModel;
import com.thresholdsoft.apollofeedback.utils.CommonUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class StoreSetupActivity extends BaseActivity implements StoreSetupActivityCallback {

    ActivityStoreSetupBinding activityStoreSetupBinding;

    private String deviceId;
    private String registerDate = "";
    private String deviceType = "";
    double latitude;
    double longitude;
    private String userID = "";
    private LocationManager locationManager;
    private Location mylocation;
    //    private final static int REQUEST_CHECK_SETTINGS_GPS = 0x1;
//    private final static int REQUEST_ID_MULTIPLE_PERMISSIONS = 0x2;
    private StoreListResponseModel storeListObj = null;
    private StoreListResponseModel.StoreListObj selectedStoreId = null;
    private StoreListResponseModel.StoreListObj selectedStoreContactNum = null;
    StoreSetupModel storeSetupModel;
    StoreSetupActivityCallback storeSetupActivityCallback;
    String eposUrl = "http://online.apollopharmacy.org:51/FEEDBACK/";

    public static Intent getStartIntent(Context context) {
        Intent intent = new Intent(context, StoreSetupActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        return intent;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityStoreSetupBinding = DataBindingUtil.setContentView(this, R.layout.activity_store_setup);
        setUp();
    }


    private StoreSteupController storeSetupController;

    private void setUp() {
//        checkPermissions();

        activityStoreSetupBinding.setCallback(this);
        storeSetupController = new StoreSteupController(this, this);
        storeSetupController.getStoreList();

        deviceId = CommonUtils.getDeviceId(this);

        if (getIntent() != null) {
            Date c = Calendar.getInstance().getTime();
            SimpleDateFormat currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);
            registerDate = currentDate.format(c);
            deviceType = android.os.Build.MODEL;
            storeSetupModel = new StoreSetupModel();
            storeSetupModel.setMacId(deviceId);
            storeSetupModel.setDeviceName(deviceType);
            storeSetupModel.setDeviceType(Build.DEVICE);
            storeSetupModel.setStoreDate(registerDate);
            storeSetupModel.setStoreLatitude(latitude);
            storeSetupModel.setStoreLongitude(longitude);


            activityStoreSetupBinding.macId.setText(storeSetupModel.getMacId());
            activityStoreSetupBinding.deviceType.setText(storeSetupModel.getDeviceType());
            activityStoreSetupBinding.deviceName.setText(storeSetupModel.getDeviceName());
            activityStoreSetupBinding.date.setText(storeSetupModel.getStoreDate());
//            activityStoreSetupBinding.lattitude.setText(String.valueOf((int) storeSetupModel.getStoreLatitude()));
//            activityStoreSetupBinding.longitude.setText(String.valueOf((int) storeSetupModel.getStoreLongitude()));
            activityStoreSetupBinding.baseUrl.setText(eposUrl);


        }

        if (getDataManager().getTerminalId() != null && getDataManager().getEposUrl() != null) {
            activityStoreSetupBinding.terminalIdText.setText(getDataManager().getTerminalId());
            activityStoreSetupBinding.dcCode.setText(getDataManager().getDcCode());
//            activityStoreSetupBinding.baseUrl.setText(getDataManager().getEposUrl());
        }

        if (getDataManager().getSiteId() != null && !getDataManager().getSiteId().isEmpty()) {
            StoreListResponseModel.StoreListObj item = new StoreListResponseModel.StoreListObj();
            item.setStoreId(getDataManager().getSiteId());
            item.setStoreName(getDataManager().getStoreAddress());
            item.setAddress(getDataManager().getLabelAddress());
            this.selectedStoreId = item;
            activityStoreSetupBinding.setStoreinfo(item);
        }


    }

//    private void checkPermissions() {
//        if (ContextCompat.checkSelfPermission(StoreSetupActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
//                != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(StoreSetupActivity.this, new String[]{
//                    Manifest.permission.ACCESS_FINE_LOCATION
//            }, 100);
//        }
//        try {
//            locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
//            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 5, StoreSetupActivity.this);
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//
//    @Override
//    public void onLocationChanged(@NonNull Location location) {
//
//        latitude = location.getLatitude();
//        longitude = location.getLongitude();
//
////        activityStoreSetupBinding.lattitude.setText(String.valueOf(location.getLatitude()));
////        activityStoreSetupBinding.longitude.setText(String.valueOf(location.getLatitude()));
//
//
//        try {
//            Geocoder geocoder = new Geocoder(StoreSetupActivity.this, Locale.getDefault());
//            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
//            String address = addresses.get(0).getAddressLine(0);
//
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//
//    }

    private boolean isValidate() {
        String url = activityStoreSetupBinding.baseUrl.getText().toString().trim();
        String terminalId = activityStoreSetupBinding.terminalIdText.getText().toString().trim();
        String dcCode = activityStoreSetupBinding.dcCode.getText().toString().trim();

        if (terminalId.isEmpty()) {
            activityStoreSetupBinding.terminalIdText.setError("Please Enter Terminal Id");
            activityStoreSetupBinding.terminalIdText.requestFocus();
            return false;
        } else if (url.isEmpty()) {
            activityStoreSetupBinding.baseUrl.setError("Please Enter Epos Url");
            activityStoreSetupBinding.baseUrl.requestFocus();
            return false;
        } else if (dcCode.isEmpty()) {
            activityStoreSetupBinding.dcCode.setError("Please Enter DC Code");
            activityStoreSetupBinding.dcCode.requestFocus();
            return false;
        }
        return true;
    }


    @Override
    public void setStoresList(StoreListResponseModel storesList) {
        storeListObj = storesList;


    }

    String storeIdNumber;

    @Override
    public void onSelectStore(StoreListResponseModel.StoreListObj item) {
        activityStoreSetupBinding.setStoreinfo(item);
        storeIdNumber = item.getStoreId();
        selectedStoreId = item;
        selectedStoreContactNum = item;
        UserAddress userAddress = new UserAddress();
        userAddress.setId(Integer.parseInt(item.getStoreId()));
        userAddress.setCity(item.getCity());
        userAddress.setAddress1(item.getAddress());
        userAddress.setName(item.getStoreName());
        userAddress.setMobile(item.getContactNumber());
        userAddress.setState(item.getCity());
        userAddress.setPincode("000000");
//        SessionManager.INSTANCE.setUseraddress(userAddress);
    }

    @Override
    public void dialogCloseListiner() {
        isShowDialog = false;
    }

    @Override
    public void onCancelBtnClick() {
        selectedStoreId = null;
        activityStoreSetupBinding.setStoreinfo(selectedStoreId);
    }

    @Override
    public void onVerifyClick() {
        if (isValidate()) {
            storeSetupController.getDeviceRegistrationDetails(activityStoreSetupBinding.date.getText().toString(),
                    activityStoreSetupBinding.deviceType.getText().toString(), activityStoreSetupBinding.macId.getText().toString(), latitude, longitude,
                    activityStoreSetupBinding.storeId.getText().toString(), activityStoreSetupBinding.terminalIdText.getText().toString(), "admin");
        }
    }


    private boolean isShowDialog = false;

    @Override
    public void onSelectStoreSearch() {
        if (storeListObj != null) {
            if (storeListObj.getStoreListArr().size() > 0) {
                GetStoresDialog dialog = new GetStoresDialog(this);
                dialog.setStoreDetailsMvpView(this);
                dialog.setStoreListArray(storeListObj.getStoreListArr());
                if (!isShowDialog) {
                    isShowDialog = true;
                    dialog.show();
                }
            }
        }
    }


    @Override
    public String getDeviceId() {
        return deviceId;
    }

//    @Override
//    public String getFcmKey() {
//        return FirebaseInstanceId.getInstance().getToken();
//    }

    @Override
    public String getStoreId() {
        return selectedStoreId.getStoreId();
    }

    @Override
    public String getStoreContactNum() {
        return selectedStoreContactNum.getContactNumber();
    }

    @Override
    public StoreListResponseModel.StoreListObj getStoreDetails() {
        return activityStoreSetupBinding.getStoreinfo();
    }

    @Override
    public String getTerminalId() {
        return activityStoreSetupBinding.terminalIdText.getText().toString();
    }

    @Override
    public String getUserId() {
        return userID;
    }

    @Override
    public String getDeviceType() {
        return deviceType;
    }

    @Override
    public String getRegisteredDate() {
        return registerDate;
    }

    @Override
    public String getLatitude() {
        return String.valueOf(latitude);
    }

    @Override
    public String getLongitude() {
        return String.valueOf(longitude);
    }

    @Override
    public String getEposURL() {
        return activityStoreSetupBinding.baseUrl.getText().toString();
    }

    @Override
    public void getDeviceRegistrationDetails(DeviceRegistrationResponse deviceRegistrationResponse) {
        if (deviceRegistrationResponse != null && deviceRegistrationResponse.getStatus()) {
            getDataManager().setSiteId(activityStoreSetupBinding.storeId.getText().toString());
            getDataManager().setTerminalId(activityStoreSetupBinding.terminalIdText.getText().toString());
            getDataManager().setEposUrl(activityStoreSetupBinding.baseUrl.getText().toString());
            getDataManager().setStoreAddress(activityStoreSetupBinding.storeName.getText().toString());
            getDataManager().setLabelAddress(activityStoreSetupBinding.storeAddress.getText().toString());
            getDataManager().setDcCode(activityStoreSetupBinding.dcCode.getText().toString());
//            Toast.makeText(this, "" + deviceRegistrationResponse.getMessage(), Toast.LENGTH_SHORT).show();
//            startActivity(OffersNowActivity.getStartIntent(StoreSetupActivity.this));
//            overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
            Intent intent = new Intent();
            setResult(Activity.RESULT_OK, intent);
            finish();

        }
    }

    @Override
    public void closeIcon() {
        onBackPressed();
    }

    public void selectButton() {
        if (storeListObj != null) {
            if (storeListObj.getStoreListArr().size() > 0) {
                GetStoresDialog dialog = new GetStoresDialog(this);
                dialog.setStoreDetailsMvpView(this);
                dialog.setStoreListArray(storeListObj.getStoreListArr());
                if (!isShowDialog) {
                    isShowDialog = true;
                    dialog.show();
                }
            }
        }
    }


    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        setResult(Activity.RESULT_OK, intent);
        finish();

//        if (getDataManager().getTerminalId().isEmpty() || getDataManager().getSiteId().isEmpty()) {
//            finishAffinity();
//            System.exit(0);
//        } else {
//            super.onBackPressed();
//        }
    }
}