package com.thresholdsoft.apollofeedback.ui.storesetup;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.databinding.DataBindingUtil;

import com.thresholdsoft.apollofeedback.R;
import com.thresholdsoft.apollofeedback.base.BaseActivity;
import com.thresholdsoft.apollofeedback.databinding.ActivityStoreSetupBinding;
import com.thresholdsoft.apollofeedback.databinding.DialogFeedbackCalibrationBinding;
import com.thresholdsoft.apollofeedback.databinding.DialogVoiceRecordCalibrationBinding;
import com.thresholdsoft.apollofeedback.ui.model.DeviceRegistrationResponse;
import com.thresholdsoft.apollofeedback.ui.model.UserAddress;
import com.thresholdsoft.apollofeedback.ui.offersnow.OffersNowActivity;
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
    String eposUrl = "http://online.apollopharmacy.org:51/MPOS/";

    private int ratingStatus = 1;

    private boolean isWebCam = false;

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
        getDataManager().setVoiceRecordKey("");
        if (isWebCam) {
            activityStoreSetupBinding.builtinCamera.setChecked(false);
            activityStoreSetupBinding.webcam.setChecked(true);
        } else {
            activityStoreSetupBinding.builtinCamera.setChecked(true);
            activityStoreSetupBinding.webcam.setChecked(false);
        }
        cameraSetupListener();
        activityStoreSetupBinding.setCallback(this);
        storeSetupController = new StoreSteupController(this, this);
        storeSetupController.getStoreList();

        deviceId = CommonUtils.getDeviceId(this);

        if (getIntent() != null) {
            Date c = Calendar.getInstance().getTime();
            SimpleDateFormat currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
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

    private void cameraSetupListener() {
        activityStoreSetupBinding.cameraSetupRadioGrp.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // checkedId is the RadioButton selected
                if (checkedId == R.id.builtin_camera) {
                    isWebCam = false;
//                    Toast.makeText(StoreSetupActivity.this, "Switched to builtin_camera - " + activityStoreSetupBinding.builtinCamera.isChecked(), Toast.LENGTH_SHORT).show();
                } else if (checkedId == R.id.webcam) {
                    isWebCam = true;
//                    Toast.makeText(StoreSetupActivity.this, "Switched webcam - " + activityStoreSetupBinding.webcam.isChecked(), Toast.LENGTH_SHORT).show();
                }

            }
        });
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
        String storeName = activityStoreSetupBinding.storeNameEdittext.getText().toString().trim();
        String voiceRecordKey = getDataManager().getVoiceRecordKey();

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
        } else if (storeName.isEmpty()) {
            activityStoreSetupBinding.storeNameEdittext.setError("Please Enter Store Name");
            activityStoreSetupBinding.storeNameEdittext.requestFocus();
            return false;
        } else if (getDataManager().getPoorKey().isEmpty()
                || getDataManager().getFairKey().isEmpty()
                || getDataManager().getAverageKey().isEmpty()
                || getDataManager().getHappyKey().isEmpty()
                || getDataManager().getExcellentKey().isEmpty()) {
            Toast.makeText(this, "Kindly finish Feedback Calibration", Toast.LENGTH_SHORT).show();
            return false;
        } else if (voiceRecordKey.isEmpty()) {
            Toast.makeText(this, "Kindly finish Voice Record Calibration", Toast.LENGTH_SHORT).show();
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
            storeSetupController.getDeviceRegistrationDetails(activityStoreSetupBinding.date.getText().toString(), activityStoreSetupBinding.deviceType.getText().toString(), activityStoreSetupBinding.macId.getText().toString(), latitude, longitude, activityStoreSetupBinding.storeId.getText().toString(), activityStoreSetupBinding.terminalIdText.getText().toString(), "admin");
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
            getDataManager().setStoreName(activityStoreSetupBinding.storeNameEdittext.getText().toString());
            getDataManager().setWebcam(isWebCam);
//            Toast.makeText(this, "" + deviceRegistrationResponse.getMessage(), Toast.LENGTH_SHORT).show();
//            startActivity(OffersNowActivity.getStartIntent(StoreSetupActivity.this));
//            overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
            /*Intent intent = new Intent();
            setResult(Activity.RESULT_OK, intent);*/
            startActivity(OffersNowActivity.getStartIntent(this));
            overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
            finish();

        }
    }

    @Override
    public void closeIcon() {
        onBackPressed();
    }

    @Override
    public void onClickFeedbackCalibration() {
        smileyDialog("enterRating");
    }

    @Override
    public void onClickFeedbackTest() {
        smileyDialog("test");
    }

    private String voiceRecordKeyText = "";
    private Dialog voiceRecordCalibrationDialog;

    @Override
    public void onClickVoiceRecordCalibration() {
        voiceRecordCalibrationDialog = new Dialog(this);
        DialogVoiceRecordCalibrationBinding dialogVoiceRecordCalibrationBinding = DataBindingUtil.inflate(LayoutInflater.from(this), R.layout.dialog_voice_record_calibration, null, false);
        voiceRecordCalibrationDialog.setContentView(dialogVoiceRecordCalibrationBinding.getRoot());
        voiceRecordCalibrationDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        dialogVoiceRecordCalibrationBinding.voiceRecordKeyEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().isEmpty() && s.toString().length() > 0) {
                    handler.removeCallbacks(voiceRecordKeySaveRunnable);
                    voiceRecordKeyText = s.toString();
                    handler.postDelayed(voiceRecordKeySaveRunnable, 300);
                }
            }
        });
        voiceRecordCalibrationDialog.show();
    }

    Handler handler = new Handler();
    Runnable voiceRecordKeySaveRunnable = new Runnable() {
        @Override
        public void run() {
            if (!voiceRecordKeyText.isEmpty()) {
                getDataManager().setVoiceRecordKey(voiceRecordKeyText);
                if (voiceRecordCalibrationDialog != null && voiceRecordCalibrationDialog.isShowing()) {
                    voiceRecordCalibrationDialog.dismiss();
                }
                handler.removeCallbacks(voiceRecordKeySaveRunnable);
            }
        }
    };
    private DialogFeedbackCalibrationBinding feedBackbinding;
    private Dialog calibrationDialog;
    private String feedbackRatingFromPhysical = "";

    void smileyDialog(String value) {
        ratingStatus = 1;
        calibrationDialog = new Dialog(this);
        calibrationDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        feedBackbinding = DataBindingUtil.inflate(LayoutInflater.from(this), R.layout.dialog_feedback_calibration, null, false);
        calibrationDialog.setContentView(feedBackbinding.getRoot());
        calibrationDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        calibrationDialog.setCancelable(false);
        Window window = calibrationDialog.getWindow();
        if (window != null) {
            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
            layoutParams.copyFrom(window.getAttributes());
            layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
            layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT;
            window.setAttributes(layoutParams);
        }
        if (!getDataManager().getPoorKey().isEmpty()
                && !getDataManager().getFairKey().isEmpty()
                && !getDataManager().getAverageKey().isEmpty()
                && !getDataManager().getHappyKey().isEmpty()
                && !getDataManager().getExcellentKey().isEmpty()) {
            feedBackbinding.ratingConfigSuggestMsg.setText("Feedback has been configured");
            feedBackbinding.editFeedbackConfiguration.setVisibility(View.VISIBLE);
            feedBackbinding.poorIcTick.setVisibility(View.VISIBLE);
            feedBackbinding.fairIcTick.setVisibility(View.VISIBLE);
            feedBackbinding.averageIcTick.setVisibility(View.VISIBLE);
            feedBackbinding.happyIcTick.setVisibility(View.VISIBLE);
            feedBackbinding.excellentIcTick.setVisibility(View.VISIBLE);

            feedBackbinding.test.setVisibility(View.VISIBLE);
            feedBackbinding.preview.setVisibility(View.GONE);
        } else {
            feedBackbinding.ratingConfigSuggestMsg.setText("Please configure Poor");
            feedBackbinding.editFeedbackConfiguration.setVisibility(View.GONE);
            feedBackbinding.poor.setVisibility(View.GONE);
            feedBackbinding.poorTick.setVisibility(View.VISIBLE);

            feedBackbinding.test.setVisibility(View.GONE);
            feedBackbinding.preview.setVisibility(View.GONE);
        }
        feedBackbinding.poorTick.setOnClickListener(v -> setRatingConfiguration(ratingStatus, "1"));
        feedBackbinding.fairtick.setOnClickListener(v -> setRatingConfiguration(ratingStatus, "2"));
        feedBackbinding.averagetick.setOnClickListener(v -> setRatingConfiguration(ratingStatus, "3"));
        feedBackbinding.happytick.setOnClickListener(v -> setRatingConfiguration(ratingStatus, "4"));
        feedBackbinding.excellenttick.setOnClickListener(v -> setRatingConfiguration(ratingStatus, "5"));
        feedBackbinding.closeWhiteRating.setOnClickListener(v -> calibrationDialog.dismiss());
        feedBackbinding.editFeedbackConfiguration.setOnClickListener(v -> resetFeedbackCofiguration());

        onFeedbackSecreteEditboxListener();

        feedBackbinding.test.setOnClickListener(v -> {
            feedBackbinding.ratingConfigSuggestMsg.setText("Review Feedback");

            feedBackbinding.test.setVisibility(View.GONE);
            feedBackbinding.preview.setVisibility(View.VISIBLE);

            feedBackbinding.poorIcTick.setVisibility(View.GONE);
            feedBackbinding.fairIcTick.setVisibility(View.GONE);
            feedBackbinding.averageIcTick.setVisibility(View.GONE);
            feedBackbinding.happyIcTick.setVisibility(View.GONE);
            feedBackbinding.excellentIcTick.setVisibility(View.GONE);
        });

        feedBackbinding.preview.setOnClickListener(v -> {
            feedBackbinding.ratingConfigSuggestMsg.setText("Feedback has been configured");

            feedBackbinding.test.setVisibility(View.VISIBLE);
            feedBackbinding.preview.setVisibility(View.GONE);

            feedBackbinding.poorIcTick.setVisibility(View.VISIBLE);
            feedBackbinding.fairIcTick.setVisibility(View.VISIBLE);
            feedBackbinding.averageIcTick.setVisibility(View.VISIBLE);
            feedBackbinding.happyIcTick.setVisibility(View.VISIBLE);
            feedBackbinding.excellentIcTick.setVisibility(View.VISIBLE);

            feedBackbinding.poor.setVisibility(View.VISIBLE);
            feedBackbinding.fair.setVisibility(View.VISIBLE);
            feedBackbinding.average.setVisibility(View.VISIBLE);
            feedBackbinding.happy.setVisibility(View.VISIBLE);
            feedBackbinding.excellent.setVisibility(View.VISIBLE);

            feedBackbinding.poorTick.setVisibility(View.GONE);
            feedBackbinding.fairtick.setVisibility(View.GONE);
            feedBackbinding.averagetick.setVisibility(View.GONE);
            feedBackbinding.happytick.setVisibility(View.GONE);
            feedBackbinding.excellenttick.setVisibility(View.GONE);

        });
        feedBackbinding.poor.setOnClickListener(v -> {
            if (!getDataManager().getPoorKey().isEmpty()
                    && !getDataManager().getFairKey().isEmpty()
                    && !getDataManager().getAverageKey().isEmpty()
                    && !getDataManager().getHappyKey().isEmpty()
                    && !getDataManager().getExcellentKey().isEmpty()) {
                if (feedBackbinding.preview.getVisibility() == View.VISIBLE) {
                    feedbackRatingFromPhysical = "1";
                    feedBackbinding.feedbackSecretEditbox.setText("");
                    feedbackEditboxHandler.removeCallbacks(feedbackEditboxRunnable);
                    feedbackEditboxHandler.postDelayed(feedbackEditboxRunnable, 1000);
                }
            }
        });
        feedBackbinding.fair.setOnClickListener(v -> {
            if (!getDataManager().getPoorKey().isEmpty()
                    && !getDataManager().getFairKey().isEmpty()
                    && !getDataManager().getAverageKey().isEmpty()
                    && !getDataManager().getHappyKey().isEmpty()
                    && !getDataManager().getExcellentKey().isEmpty()) {
                if (feedBackbinding.preview.getVisibility() == View.VISIBLE) {
                    feedbackRatingFromPhysical = "2";
                    feedBackbinding.feedbackSecretEditbox.setText("");
                    feedbackEditboxHandler.removeCallbacks(feedbackEditboxRunnable);
                    feedbackEditboxHandler.postDelayed(feedbackEditboxRunnable, 1000);
                }
            }
        });
        feedBackbinding.average.setOnClickListener(v -> {
            if (!getDataManager().getPoorKey().isEmpty()
                    && !getDataManager().getFairKey().isEmpty()
                    && !getDataManager().getAverageKey().isEmpty()
                    && !getDataManager().getHappyKey().isEmpty()
                    && !getDataManager().getExcellentKey().isEmpty()) {
                if (feedBackbinding.preview.getVisibility() == View.VISIBLE) {
                    feedbackRatingFromPhysical = "3";
                    feedBackbinding.feedbackSecretEditbox.setText("");
                    feedbackEditboxHandler.removeCallbacks(feedbackEditboxRunnable);
                    feedbackEditboxHandler.postDelayed(feedbackEditboxRunnable, 1000);
                }
            }
        });
        feedBackbinding.happy.setOnClickListener(v -> {
            if (!getDataManager().getPoorKey().isEmpty()
                    && !getDataManager().getFairKey().isEmpty()
                    && !getDataManager().getAverageKey().isEmpty()
                    && !getDataManager().getHappyKey().isEmpty()
                    && !getDataManager().getExcellentKey().isEmpty()) {
                if (feedBackbinding.preview.getVisibility() == View.VISIBLE) {
                    feedbackRatingFromPhysical = "4";
                    feedBackbinding.feedbackSecretEditbox.setText("");
                    feedbackEditboxHandler.removeCallbacks(feedbackEditboxRunnable);
                    feedbackEditboxHandler.postDelayed(feedbackEditboxRunnable, 1000);
                }
            }
        });
        feedBackbinding.excellent.setOnClickListener(v -> {
            if (!getDataManager().getPoorKey().isEmpty()
                    && !getDataManager().getFairKey().isEmpty()
                    && !getDataManager().getAverageKey().isEmpty()
                    && !getDataManager().getHappyKey().isEmpty()
                    && !getDataManager().getExcellentKey().isEmpty()) {
                if (feedBackbinding.preview.getVisibility() == View.VISIBLE) {
                    feedbackRatingFromPhysical = "5";
                    feedBackbinding.feedbackSecretEditbox.setText("");
                    feedbackEditboxHandler.removeCallbacks(feedbackEditboxRunnable);
                    feedbackEditboxHandler.postDelayed(feedbackEditboxRunnable, 1000);
                }
            }
        });
        feedBackbinding.closeWhiteRating.setOnClickListener(v -> calibrationDialog.dismiss());
        calibrationDialog.show();
    }

    private void resetFeedbackCofiguration() {
        this.ratingStatus = 1;

        feedBackbinding.test.setVisibility(View.GONE);
        feedBackbinding.preview.setVisibility(View.GONE);

        getDataManager().setPoorKey("");
        getDataManager().setFairKey("");
        getDataManager().setAverageKey("");
        getDataManager().setHappyKey("");
        getDataManager().setExcellentKey("");

        feedBackbinding.ratingConfigSuggestMsg.setText("Please configure Poor");
        feedBackbinding.editFeedbackConfiguration.setVisibility(View.GONE);

        feedBackbinding.poor.setVisibility(View.GONE);
        feedBackbinding.poorTick.setVisibility(View.VISIBLE);
        feedBackbinding.poorIcTick.setVisibility(View.GONE);

        feedBackbinding.fairtick.setVisibility(View.GONE);
        feedBackbinding.fair.setVisibility(View.VISIBLE);
        feedBackbinding.fairIcTick.setVisibility(View.GONE);

        feedBackbinding.averagetick.setVisibility(View.GONE);
        feedBackbinding.average.setVisibility(View.VISIBLE);
        feedBackbinding.averageIcTick.setVisibility(View.GONE);

        feedBackbinding.happytick.setVisibility(View.GONE);
        feedBackbinding.happy.setVisibility(View.VISIBLE);
        feedBackbinding.happyIcTick.setVisibility(View.GONE);

        feedBackbinding.excellenttick.setVisibility(View.GONE);
        feedBackbinding.excellent.setVisibility(View.VISIBLE);
        feedBackbinding.excellentIcTick.setVisibility(View.GONE);
    }

    private void setRatingConfiguration(int ratingStatus, String ratingKey) {
        switch (ratingStatus) {
            case 1:
                this.ratingStatus = 2;
                feedBackbinding.ratingConfigSuggestMsg.setText("Please configure Fair");
                getDataManager().setPoorKey(ratingKey);
                //getDataManager().setPoorKey(feedbackRatingFromPhysical);
                feedBackbinding.poorTick.setVisibility(View.GONE);
                feedBackbinding.poor.setVisibility(View.VISIBLE);
                feedBackbinding.poorIcTick.setVisibility(View.VISIBLE);
                feedBackbinding.fair.setVisibility(View.GONE);
                feedBackbinding.fairtick.setVisibility(View.VISIBLE);
                break;
            case 2:
                this.ratingStatus = 3;
                feedBackbinding.ratingConfigSuggestMsg.setText("Please configure Average");
                getDataManager().setFairKey(ratingKey);
                //getDataManager().setFairKey(feedbackRatingFromPhysical);
                feedBackbinding.fairtick.setVisibility(View.GONE);
                feedBackbinding.fair.setVisibility(View.VISIBLE);
                feedBackbinding.fairIcTick.setVisibility(View.VISIBLE);
                feedBackbinding.average.setVisibility(View.GONE);
                feedBackbinding.averagetick.setVisibility(View.VISIBLE);
                break;
            case 3:
                this.ratingStatus = 4;
                feedBackbinding.ratingConfigSuggestMsg.setText("Please configure Happy");
                getDataManager().setAverageKey(ratingKey);
                //getDataManager().setAverageKey(feedbackRatingFromPhysical);
                feedBackbinding.averagetick.setVisibility(View.GONE);
                feedBackbinding.average.setVisibility(View.VISIBLE);
                feedBackbinding.averageIcTick.setVisibility(View.VISIBLE);
                feedBackbinding.happy.setVisibility(View.GONE);
                feedBackbinding.happytick.setVisibility(View.VISIBLE);
                break;
            case 4:
                this.ratingStatus = 5;
                feedBackbinding.ratingConfigSuggestMsg.setText("Please configure Excellent");
                getDataManager().setHappyKey(ratingKey);
                //getDataManager().setHappyKey(feedbackRatingFromPhysical);
                feedBackbinding.happytick.setVisibility(View.GONE);
                feedBackbinding.happy.setVisibility(View.VISIBLE);
                feedBackbinding.happyIcTick.setVisibility(View.VISIBLE);
                feedBackbinding.excellent.setVisibility(View.GONE);
                feedBackbinding.excellenttick.setVisibility(View.VISIBLE);
                break;
            case 5:
                this.ratingStatus = 0;
                feedBackbinding.ratingConfigSuggestMsg.setText("Feedback has been configured");
                getDataManager().setExcellentKey(ratingKey);
                //getDataManager().setExcellentKey(feedbackRatingFromPhysical);
                feedBackbinding.excellenttick.setVisibility(View.GONE);
                feedBackbinding.excellent.setVisibility(View.VISIBLE);
                feedBackbinding.excellentIcTick.setVisibility(View.VISIBLE);
                feedBackbinding.editFeedbackConfiguration.setVisibility(View.VISIBLE);
                calibrationDialog.dismiss();
                Toast.makeText(this, "Feedback has been configured", Toast.LENGTH_SHORT).show();
                break;
            default:
        }
    }

    Handler feedbackEditboxHandler = new Handler();
    Runnable feedbackEditboxRunnable = new Runnable() {
        @Override
        public void run() {
            feedbackEditboxHandler.removeCallbacks(feedbackEditboxRunnable);
            if (!getDataManager().getPoorKey().isEmpty()
                    && !getDataManager().getFairKey().isEmpty()
                    && !getDataManager().getAverageKey().isEmpty()
                    && !getDataManager().getHappyKey().isEmpty()
                    && !getDataManager().getExcellentKey().isEmpty()) {
                String poor = getDataManager().getPoorKey();
                String fair = getDataManager().getFairKey();
                String average = getDataManager().getAverageKey();
                String happy = getDataManager().getHappyKey();
                String excellent = getDataManager().getExcellentKey();
                if (poor.equals(feedbackRatingFromPhysical)) {
                    feedbackRatingFromPhysical = "";
                    feedbackEditboxHandler.removeCallbacks(feedbackEditboxRunnable);
                    poor();
                } else if (fair.equals(feedbackRatingFromPhysical)) {
                    feedbackRatingFromPhysical = "";
                    feedbackEditboxHandler.removeCallbacks(feedbackEditboxRunnable);
                    fair();
                } else if (average.equals(feedbackRatingFromPhysical)) {
                    feedbackRatingFromPhysical = "";
                    feedbackEditboxHandler.removeCallbacks(feedbackEditboxRunnable);
                    average();
                } else if (happy.equals(feedbackRatingFromPhysical)) {
                    feedbackRatingFromPhysical = "";
                    feedbackEditboxHandler.removeCallbacks(feedbackEditboxRunnable);
                    happy();
                } else if (excellent.equals(feedbackRatingFromPhysical)) {
                    feedbackRatingFromPhysical = "";
                    feedbackEditboxHandler.removeCallbacks(feedbackEditboxRunnable);
                    excellent();
                }
            } else {
                setRatingConfiguration(ratingStatus, feedbackRatingFromPhysical);
            }
        }
    };

    private void poor() {
        feedBackbinding.fairtick.setVisibility(View.GONE);
        feedBackbinding.fair.setVisibility(View.VISIBLE);
        feedBackbinding.averagetick.setVisibility(View.GONE);
        feedBackbinding.average.setVisibility(View.VISIBLE);
        feedBackbinding.happytick.setVisibility(View.GONE);
        feedBackbinding.happy.setVisibility(View.VISIBLE);
        feedBackbinding.excellenttick.setVisibility(View.GONE);
        feedBackbinding.excellent.setVisibility(View.VISIBLE);
        feedBackbinding.poorTick.setVisibility(View.VISIBLE);
        feedBackbinding.poor.setVisibility(View.GONE);
    }

    private void fair() {
        feedBackbinding.poorTick.setVisibility(View.GONE);
        feedBackbinding.poor.setVisibility(View.VISIBLE);
        feedBackbinding.averagetick.setVisibility(View.GONE);
        feedBackbinding.average.setVisibility(View.VISIBLE);
        feedBackbinding.happytick.setVisibility(View.GONE);
        feedBackbinding.happy.setVisibility(View.VISIBLE);
        feedBackbinding.excellenttick.setVisibility(View.GONE);
        feedBackbinding.excellent.setVisibility(View.VISIBLE);
        feedBackbinding.fairtick.setVisibility(View.VISIBLE);
        feedBackbinding.fair.setVisibility(View.GONE);
    }

    private void average() {
        feedBackbinding.poorTick.setVisibility(View.GONE);
        feedBackbinding.poor.setVisibility(View.VISIBLE);
        feedBackbinding.fairtick.setVisibility(View.GONE);
        feedBackbinding.fair.setVisibility(View.VISIBLE);
        feedBackbinding.happytick.setVisibility(View.GONE);
        feedBackbinding.happy.setVisibility(View.VISIBLE);
        feedBackbinding.excellenttick.setVisibility(View.GONE);
        feedBackbinding.excellent.setVisibility(View.VISIBLE);
        feedBackbinding.averagetick.setVisibility(View.VISIBLE);
        feedBackbinding.average.setVisibility(View.GONE);
    }

    private void happy() {
        feedBackbinding.poorTick.setVisibility(View.GONE);
        feedBackbinding.poor.setVisibility(View.VISIBLE);
        feedBackbinding.fairtick.setVisibility(View.GONE);
        feedBackbinding.fair.setVisibility(View.VISIBLE);
        feedBackbinding.averagetick.setVisibility(View.GONE);
        feedBackbinding.average.setVisibility(View.VISIBLE);
        feedBackbinding.excellenttick.setVisibility(View.GONE);
        feedBackbinding.excellent.setVisibility(View.VISIBLE);
        feedBackbinding.happytick.setVisibility(View.VISIBLE);
        feedBackbinding.happy.setVisibility(View.GONE);
    }

    private void excellent() {
        feedBackbinding.poorTick.setVisibility(View.GONE);
        feedBackbinding.poor.setVisibility(View.VISIBLE);
        feedBackbinding.fairtick.setVisibility(View.GONE);
        feedBackbinding.fair.setVisibility(View.VISIBLE);
        feedBackbinding.averagetick.setVisibility(View.GONE);
        feedBackbinding.average.setVisibility(View.VISIBLE);
        feedBackbinding.happytick.setVisibility(View.GONE);
        feedBackbinding.happy.setVisibility(View.VISIBLE);
        feedBackbinding.excellenttick.setVisibility(View.VISIBLE);
        feedBackbinding.excellent.setVisibility(View.GONE);
    }

    private void onFeedbackSecreteEditboxListener() {
        feedBackbinding.feedbackSecretEditbox.requestFocus();
        feedBackbinding.feedbackSecretEditbox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s != null && !s.toString().isEmpty()) {
                    feedbackRatingFromPhysical = s.toString();
                    feedBackbinding.feedbackSecretEditbox.setText("");
                    feedbackEditboxHandler.removeCallbacks(feedbackEditboxRunnable);
                    feedbackEditboxHandler.postDelayed(feedbackEditboxRunnable, 1000);
                }
            }
        });
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