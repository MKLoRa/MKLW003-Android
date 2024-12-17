package com.moko.lw003.activity;


import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;

import com.moko.ble.lib.MokoConstants;
import com.moko.ble.lib.event.ConnectStatusEvent;
import com.moko.ble.lib.event.OrderTaskResponseEvent;
import com.moko.ble.lib.task.OrderTask;
import com.moko.ble.lib.task.OrderTaskResponse;
import com.moko.lw003.AppConstants;
import com.moko.lw003.databinding.Lw003ActivityFilterRelationBinding;
import com.moko.lw003.dialog.AlertMessageDialog;
import com.moko.lw003.dialog.BottomDialog;
import com.moko.lw003.utils.ToastUtils;
import com.moko.support.lw003.LoRaLW003MokoSupport;
import com.moko.support.lw003.OrderTaskAssembler;
import com.moko.support.lw003.entity.OrderCHAR;
import com.moko.support.lw003.entity.ParamsKeyEnum;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;

public class FilterOptionsActivity extends BaseActivity {

    private Lw003ActivityFilterRelationBinding mBind;
    private boolean mReceiverTag = false;
    private boolean savedParamsError;
    private ArrayList<String> mValues;
    private int mSelected;
    private ArrayList<String> mRepeatValues;
    private int mRepeatSelected;

    private boolean isFilterAEnable;
    private boolean isFilterBEnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBind = Lw003ActivityFilterRelationBinding.inflate(getLayoutInflater());
        setContentView(mBind.getRoot());
        EventBus.getDefault().register(this);
        // 注册广播接收器
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(mReceiver, filter);
        mReceiverTag = true;
        if (!LoRaLW003MokoSupport.getInstance().isBluetoothOpen()) {
            LoRaLW003MokoSupport.getInstance().enableBluetooth();
        } else {
            showSyncingProgressDialog();
            List<OrderTask> orderTasks = new ArrayList<>();
            orderTasks.add(OrderTaskAssembler.getFilterSwitchA());
            orderTasks.add(OrderTaskAssembler.getFilterSwitchB());
            orderTasks.add(OrderTaskAssembler.getFilterABRelation());
            orderTasks.add(OrderTaskAssembler.getFilterRepeat());
            LoRaLW003MokoSupport.getInstance().sendOrder(orderTasks.toArray(new OrderTask[]{}));
        }
        mValues = new ArrayList<>();
        mValues.add("Or");
        mValues.add("And");
        mRepeatValues = new ArrayList<>();
        mRepeatValues.add("No");
        mRepeatValues.add("MAC");
        mRepeatValues.add("MAC+Data Type");
        mRepeatValues.add("MAC+Raw Data");
    }

    @Subscribe(threadMode = ThreadMode.POSTING, priority = 200)
    public void onConneStatusEvent(ConnectStatusEvent event) {
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
                                    case KEY_TRACKING_FILTER_REPEAT:
                                    case KEY_TRACKING_FILTER_A_B_RELATION:
                                        if (result != 1) {
                                            savedParamsError = true;
                                        }
                                        if (savedParamsError) {
                                            ToastUtils.showToast(FilterOptionsActivity.this, "Opps！Save failed. Please check the input characters and try again.");
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
                                    case KEY_TRACKING_FILTER_SWITCH_A:
                                        if (length == 1) {
                                            final int enable = value[4] & 0xFF;
                                            mBind.tvConditionA.setText(enable == 0 ? "OFF" : "ON");
                                            isFilterAEnable = enable == 1;
                                        }
                                        break;
                                    case KEY_TRACKING_FILTER_SWITCH_B:
                                        if (length == 1) {
                                            final int enable = value[4] & 0xFF;
                                            mBind.tvConditionB.setText(enable == 0 ? "OFF" : "ON");
                                            isFilterBEnable = enable == 1;
                                            if (isFilterAEnable && isFilterBEnable) {
                                                mBind.tvRelation.setEnabled(true);
                                            } else {
                                                mBind.tvRelation.setEnabled(false);
                                            }
                                        }
                                        break;
                                    case KEY_TRACKING_FILTER_A_B_RELATION:
                                        if (length == 1) {
                                            final int relation = value[4] & 0xFF;
                                            mBind.tvRelation.setText(relation == 1 ? "And" : "Or");
                                            mSelected = relation;
                                        }
                                        break;
                                    case KEY_TRACKING_FILTER_REPEAT:
                                        if (length == 1) {
                                            final int repeat = value[4] & 0xFF;
                                            mBind.tvRepeat.setText(mRepeatValues.get(repeat));
                                            mRepeatSelected = repeat;
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

    public void onRelation(View view) {
        if (isWindowLocked())
            return;
        BottomDialog dialog = new BottomDialog();
        dialog.setDatas(mValues, mSelected);
        dialog.setListener(value -> {
            mBind.tvRelation.setText(value == 1 ? "And" : "Or");
            mSelected = value;
            showSyncingProgressDialog();
            LoRaLW003MokoSupport.getInstance().sendOrder(OrderTaskAssembler.setFilterABRelation(value));
        });
        dialog.show(getSupportFragmentManager());
    }

    public void onRepeat(View view) {
        if (isWindowLocked())
            return;
        BottomDialog dialog = new BottomDialog();
        dialog.setDatas(mRepeatValues, mRepeatSelected);
        dialog.setListener(value -> {
            mBind.tvRepeat.setText(mRepeatValues.get(value));
            mRepeatSelected = value;
            showSyncingProgressDialog();
            LoRaLW003MokoSupport.getInstance().sendOrder(OrderTaskAssembler.setFilterRepeat(value));
        });
        dialog.show(getSupportFragmentManager());
    }

    public void onFilterA(View view) {
        if (isWindowLocked())
            return;
        startActivityForResult(new Intent(this, FilterOptionsAActivity.class), AppConstants.REQUEST_CODE_FILTER);
    }

    public void onFilterB(View view) {
        if (isWindowLocked())
            return;
        startActivityForResult(new Intent(this, FilterOptionsBActivity.class), AppConstants.REQUEST_CODE_FILTER);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AppConstants.REQUEST_CODE_FILTER) {
            mBind.tvRelation.postDelayed(() -> {
                showSyncingProgressDialog();
                List<OrderTask> orderTasks = new ArrayList<>();
                orderTasks.add(OrderTaskAssembler.getFilterSwitchA());
                orderTasks.add(OrderTaskAssembler.getFilterSwitchB());
                orderTasks.add(OrderTaskAssembler.getFilterABRelation());
                LoRaLW003MokoSupport.getInstance().sendOrder(orderTasks.toArray(new OrderTask[]{}));
            }, 500);
        }
    }
}
