package com.moko.support.lw003.task;

import com.moko.ble.lib.task.OrderTask;
import com.moko.support.lw003.entity.OrderCHAR;

public class GetBatteryTask extends OrderTask {

    public byte[] data;

    public GetBatteryTask() {
        super(OrderCHAR.CHAR_DEVICE_BATTERY, OrderTask.RESPONSE_TYPE_READ);
    }

    @Override
    public byte[] assemble() {
        return data;
    }
}
