package com.moko.support.lw003.callback;

import com.moko.support.lw003.entity.DeviceInfo;

public interface MokoScanDeviceCallback {
    void onStartScan();

    void onScanDevice(DeviceInfo device);

    void onStopScan();
}
