package com.thresholdsoft.apollofeedback.ui.epsonsdk;

import com.epson.epsonscansdk.usb.UsbProfile;

import java.util.List;

/**
 * Copyright (c) 2016 Seiko Epson Corporation. All rights reserved.
 * Created by Takehiko YOSHIDA on 2016/10/11.
 */

public interface FindScannerCallback {
    void onFindUsbDevices(List<UsbProfile> devices);

    void onNoUsbDevicesFound();
}
