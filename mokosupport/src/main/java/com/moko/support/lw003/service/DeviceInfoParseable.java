package com.moko.support.lw003.service;

import com.moko.support.lw003.entity.DeviceInfo;

public interface DeviceInfoParseable<T> {
    T parseDeviceInfo(DeviceInfo deviceInfo);
}
