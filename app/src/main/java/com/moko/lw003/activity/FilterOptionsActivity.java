package com.moko.lw003.activity;


import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.moko.ble.lib.MokoConstants;
import com.moko.ble.lib.event.ConnectStatusEvent;
import com.moko.ble.lib.event.OrderTaskResponseEvent;
import com.moko.ble.lib.task.OrderTask;
import com.moko.ble.lib.task.OrderTaskResponse;
import com.moko.lw003.R;
import com.moko.lw003.R2;
import com.moko.lw003.dialog.AlertMessageDialog;
import com.moko.lw003.dialog.BottomDialog;
import com.moko.lw003.dialog.LoadingMessageDialog;
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

import butterknife.BindView;
import butterknife.ButterKnife;

public class FilterOptionsActivity extends BaseActivity {

    @BindView(R2.id.tv_condition_a)
    TextView tvConditionA;
    @BindView(R2.id.tv_condition_b)
    TextView tvConditionB;
    @BindView(R2.id.tv_relation)
    TextView tvRelation;
    private boolean mReceiverTag = false;
    private boolean savedParamsError;
    private ArrayList<String> mValues;
    private int mSelected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lw003_activity_filter_relation);
        ButterKnife.bind(this);
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
            LoRaLW003MokoSupport.getInstance().sendOrder(orderTasks.toArray(new OrderTask[]{}));
        }
        mValues = new ArrayList<>();
        mValues.add("And");
        mValues.add("Or");
    }

    @Subscribe(threadMode = ThreadMode.POSTING, priority = 200)
    public void onConnectStatusEvent(ConnectStatusEvent event) {
        final String action = event.getAction();
        runOnUiThread(() -> {
            if (MokoConstants.ACTION_DISCONNECTED.equals(action)) {
                setResult(RESULT_OK);
                finish();
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.POSTING, priority = 200)
    public void onOrderTaskResponseEvent(OrderTaskResponseEvent event) {
        EventBus.getDefault().cancelEventDelivery(event);
        final String action = event.getAction();
        runOnUiThread(() -> {
            if (MokoConstants.ACTION_ORDER_TIMEOUT.equals(action)) {
            }
            if (MokoConstants.ACTION_ORDER_FINISH.equals(action)) {
                dismissSyncProgressDialog();
            }
            if (MokoConstants.ACTION_ORDER_RESULT.equals(action)) {
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
                                    case KEY_TRACKING_FILTER_A_B_RELATION:
                                        if (length == 1) {
                                            final int relation = value[4] & 0xFF;
                                            tvRelation.setText(relation == 0 ? "And" : "Or");
                                            mSelected = relation;
                                        }
                                        break;
                                    case KEY_TRACKING_FILTER_SWITCH_A:
                                        if (length == 1) {
                                            final int enable = value[4] & 0xFF;
                                            tvConditionA.setText(enable == 0 ? "OFF" : "ON");
                                        }
                                        break;
                                    case KEY_TRACKING_FILTER_SWITCH_B:
                                        if (length == 1) {
                                            final int enable = value[4] & 0xFF;
                                            tvConditionB.setText(enable == 0 ? "OFF" : "ON");
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
                            FilterOptionsActivity.this.setResult(RESULT_OK);
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

    private LoadingMessageDialog mLoadingMessageDialog;

    public void showSyncingProgressDialog() {
        mLoadingMessageDialog = new LoadingMessageDialog();
        mLoadingMessageDialog.setMessage("Syncing..");
        mLoadingMessageDialog.show(getSupportFragmentManager());

    }

    public void dismissSyncProgressDialog() {
        if (mLoadingMessageDialog != null)
            mLoadingMessageDialog.dismissAllowingStateLoss();
    }

    public void onBack(View view) {
        finish();
    }

    public void onRelation(View view) {
        BottomDialog dialog = new BottomDialog();
        dialog.setDatas(mValues, mSelected);
        dialog.setListener(value -> {
            tvRelation.setText(value == 0 ? "And" : "Or");
            mSelected = value;
            showSyncingProgressDialog();
            LoRaLW003MokoSupport.getInstance().sendOrder(OrderTaskAssembler.setFilterABRelation(value));
        });
        dialog.show(getSupportFragmentManager());
    }
}
