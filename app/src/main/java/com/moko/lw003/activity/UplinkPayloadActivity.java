package com.moko.lw003.activity;


import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.moko.ble.lib.MokoConstants;
import com.moko.ble.lib.event.ConnectStatusEvent;
import com.moko.ble.lib.event.OrderTaskResponseEvent;
import com.moko.ble.lib.task.OrderTask;
import com.moko.ble.lib.task.OrderTaskResponse;
import com.moko.ble.lib.utils.MokoUtils;
import com.moko.lib.loraui.dialog.AlertMessageDialog;
import com.moko.lib.loraui.dialog.BottomDialog;
import com.moko.lw003.databinding.Lw003ActivityUplinkPayloadBinding;
import com.moko.lw003.utils.ToastUtils;
import com.moko.support.lw003.LoRaLW003MokoSupport;
import com.moko.support.lw003.OrderTaskAssembler;
import com.moko.support.lw003.entity.OrderCHAR;
import com.moko.support.lw003.entity.ParamsKeyEnum;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class UplinkPayloadActivity extends BaseActivity {

    // report data type
    private static final int OPTIONAL_PAYLOAD_UNKNOW = 1;
    private static final int OPTIONAL_PAYLOAD_IBEACON = 2;
    private static final int OPTIONAL_PAYLOAD_EDDYSTONE = 4;
    // report data content
    private static final int OPTIONAL_PAYLOAD_RESPONSE_RAW_DATA = 1;
    private static final int OPTIONAL_PAYLOAD_BROADCAST_RAW_DATA = 2;
    private static final int OPTIONAL_PAYLOAD_RSSI = 4;
    private static final int OPTIONAL_PAYLOAD_MAC = 8;
    private static final int OPTIONAL_PAYLOAD_TIMESTAMP = 16;

    private Lw003ActivityUplinkPayloadBinding mBind;

    private boolean mReceiverTag = false;
    private boolean savedParamsError;

    private ArrayList<String> mReportMaxLengthValues;
    private int mReportMaxLengthSelected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBind = Lw003ActivityUplinkPayloadBinding.inflate(getLayoutInflater());
        setContentView(mBind.getRoot());
        EventBus.getDefault().register(this);
        mReportMaxLengthValues = new ArrayList<>();
        mReportMaxLengthValues.add("1");
        mReportMaxLengthValues.add("2");
        // 注册广播接收器
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(mReceiver, filter);
        mReceiverTag = true;
        showSyncingProgressDialog();
        List<OrderTask> orderTasks = new ArrayList<>();
        orderTasks.add(OrderTaskAssembler.getDeviceInfoInterval());
        orderTasks.add(OrderTaskAssembler.getUplinkDataType());
        orderTasks.add(OrderTaskAssembler.getUplinkDataContent());
        orderTasks.add(OrderTaskAssembler.getUplinkDataMaxLength());
        orderTasks.add(OrderTaskAssembler.getDataReportInterval());
        LoRaLW003MokoSupport.getInstance().sendOrder(orderTasks.toArray(new OrderTask[]{}));
    }

    @Subscribe(threadMode = ThreadMode.POSTING, priority = 200)
    public void onConnectStatusEvent(ConnectStatusEvent event) {
        final String action = event.getAction();
        runOnUiThread(() -> {
            if (MokoConstants.ACTION_DISCONNECTED.equals(action)) {
                finish();
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.POSTING, priority = 200)
    public void onOrderTaskResponseEvent(OrderTaskResponseEvent event) {
        final String action = event.getAction();
        runOnUiThread(() -> {
            if (MokoConstants.ACTION_ORDER_TIMEOUT.equals(action)) {
            }
            if (MokoConstants.ACTION_ORDER_FINISH.equals(action)) {
                dismissSyncProgressDialog();
            }
            if (MokoConstants.ACTION_ORDER_RESULT.equals(action)) {
                EventBus.getDefault().cancelEventDelivery(event);
                OrderTaskResponse response = event.getResponse();
                OrderCHAR orderCHAR = (OrderCHAR) response.orderCHAR;
                int responseType = response.responseType;
                byte[] value = response.responseValue;
                switch (orderCHAR) {
                    case CHAR_PARAMS:
                        if (value.length >= 4) {
                            int header = value[0] & 0xFF;// 0xED
                            int flag = value[1] & 0xFF;// read or write
                            int cmd = value[2] & 0xFF;
                            if (header != 0xED)
                                return;
                            ParamsKeyEnum configKeyEnum = ParamsKeyEnum.fromParamKey(cmd);
                            if (configKeyEnum == null) {
                                return;
                            }
                            int length = value[3] & 0xFF;
                            if (flag == 0x01) {
                                // write
                                int result = value[4] & 0xFF;
                                switch (configKeyEnum) {
                                    case KEY_DEVICE_INFO_INTERVAL:
                                    case KEY_UPLINK_DATA_TYPE:
                                    case KEY_UPLINK_DATA_CONTENT:
                                    case KEY_UPLINK_DATA_MAX_LENGTH:
                                        if (result != 1) {
                                            savedParamsError = true;
                                        }
                                        break;
                                    case KEY_DATA_REPORT_INTERVAL:
                                        if (result != 1) {
                                            savedParamsError = true;
                                        }
                                        if (savedParamsError) {
                                            ToastUtils.showToast(UplinkPayloadActivity.this, "Opps！Save failed. Please check the input characters and try again.");
                                        } else {
                                            AlertMessageDialog dialog = new AlertMessageDialog();
                                            dialog.setMessage("Saved Successfully！");
                                            dialog.setConfirm("OK");
                                            dialog.setCancelGone();
                                            dialog.show(getSupportFragmentManager());
                                        }
                                        break;
                                }
                            }
                            if (flag == 0x00) {
                                // read
                                switch (configKeyEnum) {
                                    case KEY_DEVICE_INFO_INTERVAL:
                                        if (length > 0) {
                                            byte[] rawDataBytes = Arrays.copyOfRange(value, 4, 4 + length);
                                            final int interval = MokoUtils.toInt(rawDataBytes);
                                            mBind.etDeviceInfoReportInterval.setText(String.valueOf(interval));
                                        }
                                        break;
                                    case KEY_UPLINK_DATA_TYPE:
                                        if (length > 0) {
                                            int optionalPayload = value[4] & 0xFF;
                                            mBind.cbReportTypeIbeacon.setChecked((optionalPayload & OPTIONAL_PAYLOAD_IBEACON)
                                                    == OPTIONAL_PAYLOAD_IBEACON);
                                            mBind.cbReportTypeEddystone.setChecked((optionalPayload & OPTIONAL_PAYLOAD_EDDYSTONE)
                                                    == OPTIONAL_PAYLOAD_EDDYSTONE);
                                            mBind.cbReportTypeUnknow.setChecked((optionalPayload & OPTIONAL_PAYLOAD_UNKNOW)
                                                    == OPTIONAL_PAYLOAD_UNKNOW);
                                        }
                                        break;
                                    case KEY_UPLINK_DATA_CONTENT:
                                        if (length > 0) {
                                            int optionalPayload = value[4] & 0xFF;
                                            mBind.cbReportContentTimestamp.setChecked((optionalPayload & OPTIONAL_PAYLOAD_TIMESTAMP)
                                                    == OPTIONAL_PAYLOAD_TIMESTAMP);
                                            mBind.cbReportContentMac.setChecked((optionalPayload & OPTIONAL_PAYLOAD_MAC)
                                                    == OPTIONAL_PAYLOAD_MAC);
                                            mBind.cbReportContentRssi.setChecked((optionalPayload & OPTIONAL_PAYLOAD_RSSI)
                                                    == OPTIONAL_PAYLOAD_RSSI);
                                            mBind.cbReportContentBroadcast.setChecked((optionalPayload & OPTIONAL_PAYLOAD_BROADCAST_RAW_DATA)
                                                    == OPTIONAL_PAYLOAD_BROADCAST_RAW_DATA);
                                            mBind.cbReportContentResponse.setChecked((optionalPayload & OPTIONAL_PAYLOAD_RESPONSE_RAW_DATA)
                                                    == OPTIONAL_PAYLOAD_RESPONSE_RAW_DATA);
                                        }
                                        break;
                                    case KEY_UPLINK_DATA_MAX_LENGTH:
                                        if (length > 0) {
                                            int maxLength = value[4] & 0xFF;
                                            mReportMaxLengthSelected = maxLength;
                                            mBind.tvReportDataMaxLength.setText(mReportMaxLengthValues.get(maxLength));
                                        }
                                        break;
                                    case KEY_DATA_REPORT_INTERVAL:
                                        if (length > 0) {
                                            byte[] rawDataBytes = Arrays.copyOfRange(value, 4, 4 + length);
                                            int interval = MokoUtils.toInt(rawDataBytes);
                                            mBind.etDataReportInterval.setText(String.valueOf(interval));
                                        }
                                        break;
                                }
                            }
                        }
                        break;
                }
            }
        });
    }

    public void onSave(View view) {
        if (isWindowLocked())
            return;
        if (isValid()) {
            showSyncingProgressDialog();
            saveParams();
        } else {
            ToastUtils.showToast(this, "Opps！Save failed. Please check the input characters and try again.");
        }
    }

    private boolean isValid() {
        final String deviceIntervalStr = mBind.etDeviceInfoReportInterval.getText().toString();
        if (TextUtils.isEmpty(deviceIntervalStr))
            return false;
        final int deviceInterval = Integer.parseInt(deviceIntervalStr);
        if (deviceInterval < 1 || deviceInterval > 14400)
            return false;
        final String dataIntervalStr = mBind.etDataReportInterval.getText().toString();
        if (TextUtils.isEmpty(dataIntervalStr))
            return false;
        final int dataInterval = Integer.parseInt(dataIntervalStr);
        if (dataInterval < 10 || dataInterval > 65535)
            return false;
        return true;
    }


    private void saveParams() {
        final String deviceIntervalStr = mBind.etDeviceInfoReportInterval.getText().toString();
        final String dataIntervalStr = mBind.etDataReportInterval.getText().toString();
        final int deviceInterval = Integer.parseInt(deviceIntervalStr);
        final int dataInterval = Integer.parseInt(dataIntervalStr);
        List<OrderTask> orderTasks = new ArrayList<>();
        // device info
        orderTasks.add(OrderTaskAssembler.setDeviceInfoInterval(deviceInterval));
        int iBeacon = 0;
        int eddystone = 0;
        int unknow = 0;
        if (mBind.cbReportTypeIbeacon.isChecked())
            iBeacon = OPTIONAL_PAYLOAD_IBEACON;
        if (mBind.cbReportTypeEddystone.isChecked())
            eddystone = OPTIONAL_PAYLOAD_EDDYSTONE;
        if (mBind.cbReportTypeUnknow.isChecked())
            unknow = OPTIONAL_PAYLOAD_UNKNOW;
        orderTasks.add(OrderTaskAssembler.setUplinkDataType(unknow | iBeacon | eddystone));
        int timestamp = 0;
        int mac = 0;
        int rssi = 0;
        int broadcast = 0;
        int response = 0;
        if (mBind.cbReportContentTimestamp.isChecked())
            timestamp = OPTIONAL_PAYLOAD_TIMESTAMP;
        if (mBind.cbReportContentMac.isChecked())
            mac = OPTIONAL_PAYLOAD_MAC;
        if (mBind.cbReportContentRssi.isChecked())
            rssi = OPTIONAL_PAYLOAD_RSSI;
        if (mBind.cbReportContentBroadcast.isChecked())
            broadcast = OPTIONAL_PAYLOAD_BROADCAST_RAW_DATA;
        if (mBind.cbReportContentResponse.isChecked())
            response = OPTIONAL_PAYLOAD_RESPONSE_RAW_DATA;
        orderTasks.add(OrderTaskAssembler.setUplinkDataContent(timestamp | mac | rssi | broadcast | response));
        orderTasks.add(OrderTaskAssembler.setUplinkDataMaxLength(mReportMaxLengthSelected));
        orderTasks.add(OrderTaskAssembler.setDataReportInterval(dataInterval));
        LoRaLW003MokoSupport.getInstance().sendOrder(orderTasks.toArray(new OrderTask[]{}));
    }


    private BroadcastReceiver mReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent != null) {
                String action = intent.getAction();
                if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
                    int blueState = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, 0);
                    switch (blueState) {
                        case BluetoothAdapter.STATE_TURNING_OFF:
                            dismissSyncProgressDialog();
                            finish();
                            break;
                    }
                }
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mReceiverTag) {
            mReceiverTag = false;
            // 注销广播
            unregisterReceiver(mReceiver);
        }
        EventBus.getDefault().unregister(this);
    }

    public void onBack(View view) {
        finish();
    }

    public void onSelectReportDataMaxLength(View view) {
        BottomDialog dialog = new BottomDialog();
        dialog.setDatas(mReportMaxLengthValues, mReportMaxLengthSelected);
        dialog.setListener(value -> {
            mReportMaxLengthSelected = value;
            mBind.tvReportDataMaxLength.setText(mReportMaxLengthValues.get(value));
        });
        dialog.show(getSupportFragmentManager());
    }
}
