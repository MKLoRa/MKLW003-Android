package com.moko.support.lw003;

import com.moko.ble.lib.task.OrderTask;
import com.moko.support.lw003.entity.ParamsKeyEnum;
import com.moko.support.lw003.task.GetFirmwareRevisionTask;
import com.moko.support.lw003.task.GetHardwareRevisionTask;
import com.moko.support.lw003.task.GetManufacturerNameTask;
import com.moko.support.lw003.task.GetModelNumberTask;
import com.moko.support.lw003.task.GetSoftwareRevisionTask;
import com.moko.support.lw003.task.ParamsTask;
import com.moko.support.lw003.task.SetPasswordTask;

import java.util.ArrayList;

public class OrderTaskAssembler {
    ///////////////////////////////////////////////////////////////////////////
    // READ
    ///////////////////////////////////////////////////////////////////////////

    public static OrderTask getManufacturer() {
        GetManufacturerNameTask getManufacturerTask = new GetManufacturerNameTask();
        return getManufacturerTask;
    }

    public static OrderTask getDeviceModel() {
        GetModelNumberTask getDeviceModelTask = new GetModelNumberTask();
        return getDeviceModelTask;
    }

    public static OrderTask getHardwareVersion() {
        GetHardwareRevisionTask getHardwareVersionTask = new GetHardwareRevisionTask();
        return getHardwareVersionTask;
    }

    public static OrderTask getFirmwareVersion() {
        GetFirmwareRevisionTask getFirmwareVersionTask = new GetFirmwareRevisionTask();
        return getFirmwareVersionTask;
    }

    public static OrderTask getSoftwareVersion() {
        GetSoftwareRevisionTask getSoftwareVersionTask = new GetSoftwareRevisionTask();
        return getSoftwareVersionTask;
    }

    public static OrderTask getBattery() {
        ParamsTask task = new ParamsTask();
        task.setData(ParamsKeyEnum.KEY_BATTERY);
        return task;
    }

    public static OrderTask getiBeaconUUID() {
        ParamsTask task = new ParamsTask();
        task.setData(ParamsKeyEnum.KEY_IBEACON_UUID);
        return task;
    }

    public static OrderTask getiBeaconMajor() {
        ParamsTask task = new ParamsTask();
        task.setData(ParamsKeyEnum.KEY_IBEACON_MAJOR);
        return task;
    }

    public static OrderTask getIBeaconMinor() {
        ParamsTask task = new ParamsTask();
        task.setData(ParamsKeyEnum.KEY_IBEACON_MINOR);
        return task;
    }

    public static OrderTask getMeasurePower() {
        ParamsTask task = new ParamsTask();
        task.setData(ParamsKeyEnum.KEY_MEASURE_POWER);
        return task;
    }

    public static OrderTask getTransmission() {
        ParamsTask task = new ParamsTask();
        task.setData(ParamsKeyEnum.KEY_TRANSMISSION);
        return task;
    }

    public static OrderTask getAdvInterval() {
        ParamsTask task = new ParamsTask();
        task.setData(ParamsKeyEnum.KEY_ADV_INTERVAL);
        return task;
    }

    public static OrderTask getAdvName() {
        ParamsTask task = new ParamsTask();
        task.setData(ParamsKeyEnum.KEY_ADV_NAME);
        return task;
    }

    public static OrderTask getScanInterval() {
        ParamsTask task = new ParamsTask();
        task.setData(ParamsKeyEnum.KEY_FILTER_VALID_INTERVAL);
        return task;
    }

    public static OrderTask getAlarmNotify() {
        ParamsTask task = new ParamsTask();
        task.setData(ParamsKeyEnum.KEY_ALARM_NOTIFY);
        return task;
    }

    public static OrderTask getAlarmRssi() {
        ParamsTask task = new ParamsTask();
        task.setData(ParamsKeyEnum.KEY_ALARM_RSSI);
        return task;
    }

    public static OrderTask getVibrationIntansity() {
        ParamsTask task = new ParamsTask();
        task.setData(ParamsKeyEnum.KEY_VIBRATION_INTENSITY);
        return task;
    }

    public static OrderTask getVibrationCycle() {
        ParamsTask task = new ParamsTask();
        task.setData(ParamsKeyEnum.KEY_VIBRATION_CYCLE);
        return task;
    }

    public static OrderTask getVibrationDuration() {
        ParamsTask task = new ParamsTask();
        task.setData(ParamsKeyEnum.KEY_VIBRATION_DURATION);
        return task;
    }

    public static OrderTask getWarningRssi() {
        ParamsTask task = new ParamsTask();
        task.setData(ParamsKeyEnum.KEY_WARNING_RSSI);
        return task;
    }

    public static OrderTask getLoRaConnectable() {
        ParamsTask task = new ParamsTask();
        task.setData(ParamsKeyEnum.KEY_NETWORK_STATUS);
        return task;
    }

    public static OrderTask getScanWindow() {
        ParamsTask task = new ParamsTask();
        task.setData(ParamsKeyEnum.KEY_SCAN_WINDOW);
        return task;
    }

    public static OrderTask getConnectable() {
        ParamsTask task = new ParamsTask();
        task.setData(ParamsKeyEnum.KEY_CONNECTABLE);
        return task;
    }

    public static OrderTask getLowBattery() {
        ParamsTask task = new ParamsTask();
        task.setData(ParamsKeyEnum.KEY_LOW_POWER_PERCENT);
        return task;
    }

    public static OrderTask getDeviceInfoInterval() {
        ParamsTask task = new ParamsTask();
        task.setData(ParamsKeyEnum.KEY_DEVICE_INFO_INTERVAL);
        return task;
    }

    public static OrderTask getMacAddress() {
        ParamsTask task = new ParamsTask();
        task.setData(ParamsKeyEnum.KEY_DEVICE_MAC);
        return task;
    }

    public static OrderTask getFilterSwitchA() {
        ParamsTask task = new ParamsTask();
        task.setData(ParamsKeyEnum.KEY_LOCATION_FILTER_SWITCH_A);
        return task;
    }

    public static OrderTask getFilterRssiA() {
        ParamsTask task = new ParamsTask();
        task.setData(ParamsKeyEnum.KEY_LOCATION_FILTER_RSSI_A);
        return task;
    }

    public static OrderTask getFilterMacA() {
        ParamsTask task = new ParamsTask();
        task.setData(ParamsKeyEnum.KEY_LOCATION_FILTER_MAC_A);
        return task;
    }

    public static OrderTask getFilterNameA() {
        ParamsTask task = new ParamsTask();
        task.setData(ParamsKeyEnum.KEY_LOCATION_FILTER_ADV_NAME_A);
        return task;
    }

    public static OrderTask getFilterUUIDA() {
        ParamsTask task = new ParamsTask();
        task.setData(ParamsKeyEnum.KEY_LOCATION_FILTER_UUID_A);
        return task;
    }

    public static OrderTask getFilterAdvRawDataA() {
        ParamsTask task = new ParamsTask();
        task.setData(ParamsKeyEnum.KEY_LOCATION_FILTER_ADV_RAW_DATA_A);
        return task;
    }

    public static OrderTask getFilterMajorRangeA() {
        ParamsTask task = new ParamsTask();
        task.setData(ParamsKeyEnum.KEY_LOCATION_FILTER_MAJOR_RANGE_A);
        return task;
    }

    public static OrderTask getFilterMinorRangeA() {
        ParamsTask task = new ParamsTask();
        task.setData(ParamsKeyEnum.KEY_LOCATION_FILTER_MINOR_RANGE_A);
        return task;
    }

    public static OrderTask getFilterSwitchB() {
        ParamsTask task = new ParamsTask();
        task.setData(ParamsKeyEnum.KEY_LOCATION_FILTER_SWITCH_B);
        return task;
    }

    public static OrderTask getFilterRssiB() {
        ParamsTask task = new ParamsTask();
        task.setData(ParamsKeyEnum.KEY_LOCATION_FILTER_RSSI_B);
        return task;
    }

    public static OrderTask getFilterMacB() {
        ParamsTask task = new ParamsTask();
        task.setData(ParamsKeyEnum.KEY_LOCATION_FILTER_MAC_B);
        return task;
    }

    public static OrderTask getFilterNameB() {
        ParamsTask task = new ParamsTask();
        task.setData(ParamsKeyEnum.KEY_LOCATION_FILTER_ADV_NAME_B);
        return task;
    }

    public static OrderTask getFilterUUIDB() {
        ParamsTask task = new ParamsTask();
        task.setData(ParamsKeyEnum.KEY_LOCATION_FILTER_UUID_B);
        return task;
    }

    public static OrderTask getFilterAdvRawDataB() {
        ParamsTask task = new ParamsTask();
        task.setData(ParamsKeyEnum.KEY_LOCATION_FILTER_ADV_RAW_DATA_B);
        return task;
    }

    public static OrderTask getFilterMajorRangeB() {
        ParamsTask task = new ParamsTask();
        task.setData(ParamsKeyEnum.KEY_LOCATION_FILTER_MAJOR_RANGE_B);
        return task;
    }

    public static OrderTask getFilterMinorRangeB() {
        ParamsTask task = new ParamsTask();
        task.setData(ParamsKeyEnum.KEY_LOCATION_FILTER_MINOR_RANGE_B);
        return task;
    }

    public static OrderTask getFilterABRelation() {
        ParamsTask task = new ParamsTask();
        task.setData(ParamsKeyEnum.KEY_LOCATION_FILTER_A_B_RELATION);
        return task;
    }

    public static OrderTask getLoraMode() {
        ParamsTask task = new ParamsTask();
        task.setData(ParamsKeyEnum.KEY_LORA_MODE);
        return task;
    }

    public static OrderTask getLoraDevEUI() {
        ParamsTask task = new ParamsTask();
        task.setData(ParamsKeyEnum.KEY_LORA_DEV_EUI);
        return task;
    }

    public static OrderTask getLoraAppEUI() {
        ParamsTask task = new ParamsTask();
        task.setData(ParamsKeyEnum.KEY_LORA_APP_EUI);
        return task;
    }

    public static OrderTask getLoraAppKey() {
        ParamsTask task = new ParamsTask();
        task.setData(ParamsKeyEnum.KEY_LORA_APP_KEY);
        return task;
    }

    public static OrderTask getLoraDevAddr() {
        ParamsTask task = new ParamsTask();
        task.setData(ParamsKeyEnum.KEY_LORA_DEV_ADDR);
        return task;
    }

    public static OrderTask getLoraAppSKey() {
        ParamsTask task = new ParamsTask();
        task.setData(ParamsKeyEnum.KEY_LORA_APP_SKEY);
        return task;
    }

    public static OrderTask getLoraNwkSKey() {
        ParamsTask task = new ParamsTask();
        task.setData(ParamsKeyEnum.KEY_LORA_NWK_SKEY);
        return task;
    }

    public static OrderTask getLoraRegion() {
        ParamsTask task = new ParamsTask();
        task.setData(ParamsKeyEnum.KEY_LORA_REGION);
        return task;
    }

    public static OrderTask getLoraReportInterval() {
        ParamsTask task = new ParamsTask();
        task.setData(ParamsKeyEnum.KEY_LORA_REPORT_INTERVAL);
        return task;
    }

    public static OrderTask getLoraMessageType() {
        ParamsTask task = new ParamsTask();
        task.setData(ParamsKeyEnum.KEY_LORA_MESSAGE_TYPE);
        return task;
    }

    public static OrderTask getLoraCH() {
        ParamsTask task = new ParamsTask();
        task.setData(ParamsKeyEnum.KEY_LORA_CH);
        return task;
    }

    public static OrderTask getLoraDR() {
        ParamsTask task = new ParamsTask();
        task.setData(ParamsKeyEnum.KEY_LORA_DR);
        return task;
    }

    public static OrderTask getLoraADR() {
        ParamsTask task = new ParamsTask();
        task.setData(ParamsKeyEnum.KEY_LORA_ADR);
        return task;
    }

    ///////////////////////////////////////////////////////////////////////////
    // WRITE
    ///////////////////////////////////////////////////////////////////////////

    public static ParamsTask setWriteConfig(ParamsKeyEnum configKeyEnum) {
        ParamsTask writeConfigTask = new ParamsTask();
        writeConfigTask.setData(configKeyEnum);
        return writeConfigTask;
    }

    public static OrderTask setPassword(String password) {
        SetPasswordTask setPasswordTask = new SetPasswordTask();
        setPasswordTask.setData(password);
        return setPasswordTask;
    }

    public static ParamsTask setTime() {
        ParamsTask writeConfigTask = new ParamsTask();
        writeConfigTask.setTime();
        return writeConfigTask;
    }

    public static OrderTask setDeviceName(String deviceName) {
        ParamsTask writeConfigTask = new ParamsTask();
        writeConfigTask.setAdvName(deviceName);
        return writeConfigTask;
    }

    public static OrderTask setUUID(String uuid) {
        ParamsTask writeConfigTask = new ParamsTask();
        writeConfigTask.setUUID(uuid);
        return writeConfigTask;
    }

    public static OrderTask setMajor(int major) {
        ParamsTask writeConfigTask = new ParamsTask();
        writeConfigTask.setMajor(major);
        return writeConfigTask;
    }

    public static OrderTask setMinor(int minor) {
        ParamsTask writeConfigTask = new ParamsTask();
        writeConfigTask.setMinor(minor);
        return writeConfigTask;
    }

    public static OrderTask setAdvInterval(int advInterval) {
        ParamsTask writeConfigTask = new ParamsTask();
        writeConfigTask.setAdvInterval(advInterval);
        return writeConfigTask;
    }


    public static OrderTask setTransmission(int transmission) {
        ParamsTask writeConfigTask = new ParamsTask();
        writeConfigTask.setTransmission(transmission);
        return writeConfigTask;
    }

    public static OrderTask setMeasurePower(int measurePower) {
        ParamsTask writeConfigTask = new ParamsTask();
        writeConfigTask.setMeasurePower(measurePower);
        return writeConfigTask;
    }

    public static ParamsTask setScanInterval(int seconds) {
        ParamsTask writeConfigTask = new ParamsTask();
        writeConfigTask.setScanInterval(seconds);
        return writeConfigTask;
    }

    public static ParamsTask setAlarmNotify(int notify) {
        ParamsTask writeConfigTask = new ParamsTask();
        writeConfigTask.setAlarmNotify(notify);
        return writeConfigTask;
    }

    public static ParamsTask setWarningRssi(int rssi) {
        ParamsTask writeConfigTask = new ParamsTask();
        writeConfigTask.setWarningRssi(rssi);
        return writeConfigTask;
    }

    public static ParamsTask setAlarmTriggerRssi(int rssi) {
        ParamsTask writeConfigTask = new ParamsTask();
        writeConfigTask.setAlarmTirggerRssi(rssi);
        return writeConfigTask;
    }

    public static OrderTask setConnectionMode(int connectionMode) {
        ParamsTask writeConfigTask = new ParamsTask();
        writeConfigTask.setConnectable(connectionMode);
        return writeConfigTask;
    }

    public static OrderTask setLowBattery(int lowBattery) {
        ParamsTask writeConfigTask = new ParamsTask();
        writeConfigTask.setLowBattery(lowBattery);
        return writeConfigTask;
    }

    public static OrderTask setDeviceInfoInterval(int interval) {
        ParamsTask writeConfigTask = new ParamsTask();
        writeConfigTask.setDeviceInfoInterval(interval);
        return writeConfigTask;
    }

    public static OrderTask changePassword(String password) {
        ParamsTask writeConfigTask = new ParamsTask();
        writeConfigTask.changePassword(password);
        return writeConfigTask;
    }

    public static OrderTask setReset() {
        ParamsTask writeConfigTask = new ParamsTask();
        writeConfigTask.reset();
        return writeConfigTask;
    }

    public static OrderTask setScanWindow(int scannerState, int startTime) {
        ParamsTask writeConfigTask = new ParamsTask();
        writeConfigTask.setScanWinow(scannerState, startTime);
        return writeConfigTask;
    }

    public static OrderTask closePower() {
        ParamsTask task = new ParamsTask();
        task.setClosePower();
        return task;
    }

    public static OrderTask setFilterRssiA(int rssi) {
        ParamsTask task = new ParamsTask();
        task.setFilterRssiA(rssi);
        return task;
    }

    public static OrderTask setFilterMacA(String mac, boolean isReverse) {
        ParamsTask task = new ParamsTask();
        task.setFilterMacA(mac, isReverse);
        return task;
    }

    public static OrderTask setFilterNameA(String name, boolean isReverse) {
        ParamsTask task = new ParamsTask();
        task.setFilterNameA(name, isReverse);
        return task;
    }

    public static OrderTask setFilterUUIDA(String uuid, boolean isReverse) {
        ParamsTask task = new ParamsTask();
        task.setFilterUUIDA(uuid, isReverse);
        return task;
    }

    public static OrderTask setFilterAdvRawDataA(ArrayList<String> filterRawDatas, boolean isReverse) {
        ParamsTask task = new ParamsTask();
        task.setFilterRawDataA(filterRawDatas, isReverse);
        return task;
    }

    public static OrderTask setFilterMajorRangeA(int enable, int majorMin, int majorMax, boolean isReverse) {
        ParamsTask task = new ParamsTask();
        task.setFilterMajorRangeA(enable, majorMin, majorMax, isReverse);
        return task;
    }

    public static OrderTask setFilterMinorRangeA(int enable, int majorMin, int majorMax, boolean isReverse) {
        ParamsTask task = new ParamsTask();
        task.setFilterMinorRangeA(enable, majorMin, majorMax, isReverse);
        return task;
    }

    public static OrderTask setFilterSwitchA(int enable) {
        ParamsTask task = new ParamsTask();
        task.setFilterSwitchA(enable);
        return task;
    }

    public static OrderTask setFilterRssiB(int rssi) {
        ParamsTask task = new ParamsTask();
        task.setFilterRssiB(rssi);
        return task;
    }

    public static OrderTask setFilterMacB(String mac, boolean isReverse) {
        ParamsTask task = new ParamsTask();
        task.setFilterMacB(mac, isReverse);
        return task;
    }

    public static OrderTask setFilterNameB(String name, boolean isReverse) {
        ParamsTask task = new ParamsTask();
        task.setFilterNameB(name, isReverse);
        return task;
    }

    public static OrderTask setFilterUUIDB(String uuid, boolean isReverse) {
        ParamsTask task = new ParamsTask();
        task.setFilterUUIDB(uuid, isReverse);
        return task;
    }

    public static OrderTask setFilterAdvRawDataB(ArrayList<String> filterRawDatas, boolean isReverse) {
        ParamsTask task = new ParamsTask();
        task.setFilterRawDataB(filterRawDatas, isReverse);
        return task;
    }

    public static OrderTask setFilterMajorRangeB(int enable, int majorMin, int majorMax, boolean isReverse) {
        ParamsTask task = new ParamsTask();
        task.setFilterMajorRangeB(enable, majorMin, majorMax, isReverse);
        return task;
    }

    public static OrderTask setFilterMinorRangeB(int enable, int majorMin, int majorMax, boolean isReverse) {
        ParamsTask task = new ParamsTask();
        task.setFilterMinorRangeB(enable, majorMin, majorMax, isReverse);
        return task;
    }

    public static OrderTask setFilterSwitchB(int enable) {
        ParamsTask task = new ParamsTask();
        task.setFilterSwitchB(enable);
        return task;
    }

    public static OrderTask setFilterABRelation(int relation) {
        ParamsTask task = new ParamsTask();
        task.setFilterABRelation(relation);
        return task;
    }

    public static OrderTask setLoraDevAddr(String devAddr) {
        ParamsTask task = new ParamsTask();
        task.setLoraDevAddr(devAddr);
        return task;
    }

    public static OrderTask setLoraNwkSKey(String nwkSKey) {
        ParamsTask task = new ParamsTask();
        task.setLoraNwkSKey(nwkSKey);
        return task;
    }

    public static OrderTask setLoraAppSKey(String appSKey) {
        ParamsTask task = new ParamsTask();
        task.setLoraAppSKey(appSKey);
        return task;
    }

    public static OrderTask setLoraAppEui(String appEui) {
        ParamsTask task = new ParamsTask();
        task.setLoraAppEui(appEui);
        return task;
    }

    public static OrderTask setLoraDevEui(String devEui) {
        ParamsTask task = new ParamsTask();
        task.setLoraDevEui(devEui);
        return task;
    }

    public static OrderTask setLoraAppKey(String appKey) {
        ParamsTask task = new ParamsTask();
        task.setLoraAppKey(appKey);
        return task;
    }

    public static OrderTask setLoraUploadMode(int mode) {
        ParamsTask task = new ParamsTask();
        task.setLoraUploadMode(mode);
        return task;
    }

    public static OrderTask setLoraUploadInterval(int interval) {
        ParamsTask task = new ParamsTask();
        task.setLoraUploadInterval(interval);
        return task;
    }

    public static OrderTask setLoraMessageType(int type) {
        ParamsTask task = new ParamsTask();
        task.setLoraMessageType(type);
        return task;
    }

    public static OrderTask setLoraRegion(int region) {
        ParamsTask task = new ParamsTask();
        task.setLoraRegion(region);
        return task;
    }

    public static OrderTask setLoraCH(int ch1, int ch2) {
        ParamsTask task = new ParamsTask();
        task.setLoraCH(ch1, ch2);
        return task;
    }

    public static OrderTask setLoraDR(int dr1) {
        ParamsTask task = new ParamsTask();
        task.setLoraDR(dr1);
        return task;
    }

    public static OrderTask setLoraADR(int adr) {
        ParamsTask task = new ParamsTask();
        task.setLoraADR(adr);
        return task;
    }

    public static OrderTask setLoraConnect() {
        ParamsTask task = new ParamsTask();
        task.setLoraConnect();
        return task;
    }

    public static OrderTask setVibrationIntensity(int intensity) {
        ParamsTask task = new ParamsTask();
        task.setVibrationIntensity(intensity);
        return task;
    }

    public static OrderTask setVibrationDuration(int duration) {
        ParamsTask task = new ParamsTask();
        task.setVibrationDuration(duration);
        return task;
    }

    public static OrderTask setVibrationCycle(int cycle) {
        ParamsTask task = new ParamsTask();
        task.setVibrationCycle(cycle);
        return task;
    }
}
