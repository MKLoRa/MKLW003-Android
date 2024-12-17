package com.moko.lw003.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;

import com.moko.ble.lib.task.OrderTask;
import com.moko.lw003.activity.DeviceInfoActivity;
import com.moko.lw003.databinding.Lw003FragmentScannerBinding;
import com.moko.support.lw003.LoRaLW003MokoSupport;
import com.moko.support.lw003.OrderTaskAssembler;

import java.util.ArrayList;

public class ScannerFragment extends Fragment implements SeekBar.OnSeekBarChangeListener {
    private static final String TAG = ScannerFragment.class.getSimpleName();
    private Lw003FragmentScannerBinding mBind;
    private DeviceInfoActivity activity;

    public ScannerFragment() {
    }


    public static ScannerFragment newInstance() {
        ScannerFragment fragment = new ScannerFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "onCreate: ");
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i(TAG, "onCreateView: ");
        mBind = Lw003FragmentScannerBinding.inflate(inflater, container, false);
        activity = (DeviceInfoActivity) getActivity();
        mBind.sbOverLimitRssi.setOnSeekBarChangeListener(this);
        mBind.cbScanSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            mBind.clScan.setVisibility(isChecked ? View.VISIBLE : View.GONE);
        });
        mBind.cbOverLimitIndication.setOnCheckedChangeListener((buttonView, isChecked) -> {
            mBind.clOverLimit.setVisibility(isChecked ? View.VISIBLE : View.GONE);
        });
        return mBind.getRoot();
    }

    @Override
    public void onPause() {
        Log.i(TAG, "onPause: ");
        super.onPause();
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "onDestroy: ");
        super.onDestroy();
    }

    public boolean isValid() {
        if (mBind.cbScanSwitch.isChecked()) {
            final String scanWindowStr = mBind.etScanWindow.getText().toString();
            if (TextUtils.isEmpty(scanWindowStr))
                return false;
            final int scanWindow = Integer.parseInt(scanWindowStr);
            if (scanWindow < 1 || scanWindow > 16)
                return false;
        }
        if (mBind.cbOverLimitIndication.isChecked()) {
            final String qtyStr = mBind.etOverLimitMacQty.getText().toString();
            if (TextUtils.isEmpty(qtyStr))
                return false;
            final int qty = Integer.parseInt(qtyStr);
            if (qty < 1 || qty > 255)
                return false;
            final String durationStr = mBind.etOverLimitDuration.getText().toString();
            if (TextUtils.isEmpty(durationStr))
                return false;
            final int duration = Integer.parseInt(durationStr);
            if (duration < 1 || duration > 600)
                return false;
        }
        return true;
    }

    public void saveParams() {
        ArrayList<OrderTask> orderTasks = new ArrayList<>();
        if (mBind.cbScanSwitch.isChecked()) {
            final String scanWindowStr = mBind.etScanWindow.getText().toString();
            final int scanWindow = Integer.parseInt(scanWindowStr);
            orderTasks.add(OrderTaskAssembler.setScanParams(scanWindow));
            orderTasks.add(OrderTaskAssembler.setScanEnable(1));
        } else {
            orderTasks.add(OrderTaskAssembler.setScanEnable(0));
        }
        if (mBind.cbOverLimitIndication.isChecked()) {
            final String qtyStr = mBind.etOverLimitMacQty.getText().toString();
            final String durationStr = mBind.etOverLimitDuration.getText().toString();
            final int qty = Integer.parseInt(qtyStr);
            final int duration = Integer.parseInt(durationStr);
            final int rssi = mBind.sbOverLimitRssi.getProgress() - 127;

            orderTasks.add(OrderTaskAssembler.setOverLimitRssi(rssi));
            orderTasks.add(OrderTaskAssembler.setOverLimitQty(qty));
            orderTasks.add(OrderTaskAssembler.setOverLimitDuration(duration));
            orderTasks.add(OrderTaskAssembler.setOverLimitEnable(1));
        } else {
            orderTasks.add(OrderTaskAssembler.setOverLimitEnable(0));
        }
        LoRaLW003MokoSupport.getInstance().sendOrder(orderTasks.toArray(new OrderTask[]{}));
    }

    public void setScanEnable(int enable) {
        mBind.cbScanSwitch.setChecked(enable == 1);
    }

    public void setScanParams(int window) {
        mBind.etScanWindow.setText(String.valueOf(window));
    }

    public void setOverLimitEnable(int enable) {
        mBind.cbOverLimitIndication.setChecked(enable == 1);
    }


    public void setOverLimitRssi(int rssi) {
        int progress = rssi + 127;
        mBind.sbOverLimitRssi.setProgress(progress);
        mBind.tvOverLimitRssiValue.setText(String.format("%ddBm", rssi));
    }

    public void setOverLimitQty(int qty) {
        mBind.etOverLimitMacQty.setText(String.valueOf(qty));
    }

    public void setOverLimitDuration(int duration) {
        mBind.etOverLimitDuration.setText(String.valueOf(duration));
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        int value = progress - 127;
        mBind.tvOverLimitRssiValue.setText(String.format("%ddBm", value));
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}
