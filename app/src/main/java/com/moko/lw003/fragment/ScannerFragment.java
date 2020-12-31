package com.moko.lw003.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

import com.moko.ble.lib.task.OrderTask;
import com.moko.lw003.R;
import com.moko.lw003.R2;
import com.moko.lw003.activity.DeviceInfoActivity;
import com.moko.lw003.dialog.AlertMessageDialog;
import com.moko.lw003.dialog.BottomDialog;
import com.moko.support.lw003.LoRaLW003MokoSupport;
import com.moko.support.lw003.OrderTaskAssembler;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.carbswang.android.numberpickerview.library.NumberPickerView;

public class ScannerFragment extends Fragment {
    private static final String TAG = ScannerFragment.class.getSimpleName();
    @BindView(R2.id.sb_scan_interval)
    SeekBar sbScanInterval;
    @BindView(R2.id.tv_scan_interval_value)
    TextView tvScanIntervalValue;
    @BindView(R2.id.tv_scan_interval_tips)
    TextView tvScanIntervalTips;
    @BindView(R2.id.npv_alarm_notify)
    NumberPickerView npvAlarmNotify;
    @BindView(R2.id.sb_alarm_trigger_rssi)
    SeekBar sbAlarmTriggerRssi;
    @BindView(R2.id.tv_alarm_trigger_rssi_value)
    TextView tvAlarmTriggerRssiValue;
    @BindView(R2.id.tv_alarm_trigger_rssi_tips)
    TextView tvAlarmTriggerRssiTips;
    @BindView(R2.id.et_vibration_cycle)
    EditText etVibrationCycle;
    @BindView(R2.id.et_vibration_duration)
    EditText etVibrationDuration;
    @BindView(R2.id.npv_vibration_intensity)
    NumberPickerView npvVibrationIntensity;
    @BindView(R2.id.tv_warning_range)
    TextView tvWarningRange;
    @BindView(R2.id.tv_warning_value)
    TextView tvWarningValue;
    @BindView(R2.id.tv_warning_tips)
    TextView tvWarningTips;

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
        View view = inflater.inflate(R.layout.lw003_fragment_scanner, container, false);
        ButterKnife.bind(this, view);
        activity = (DeviceInfoActivity) getActivity();
        sbScanInterval.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tvScanIntervalValue.setText(String.format("%ds", progress));
                tvScanIntervalTips.setText(getString(R.string.storage_interval, progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        sbAlarmTriggerRssi.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int rssi = progress - 127;
                tvAlarmTriggerRssiValue.setText(String.format("%dBm", rssi));
                tvAlarmTriggerRssiTips.setText(getString(R.string.alarm_trigger_rssi, rssi));
                warningMax = rssi;
                tvWarningRange.setText(getString(R.string.warning_range, rssi));
                createWarningRssiList();
                if (warningRssiValue >= -127 && warningRssiValue > rssi - 3) {
                    warningRssiValue = rssi - 3;
                    tvWarningValue.setText(String.valueOf(warningRssiValue));
                    tvWarningTips.setText(getString(R.string.warning_tips, warningRssiValue));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        npvAlarmNotify.setDisplayedValues(getResources().getStringArray(R.array.tracking_notify));
        npvAlarmNotify.setMaxValue(3);
        npvAlarmNotify.setMinValue(0);
        npvAlarmNotify.setValue(0);

        npvVibrationIntensity.setDisplayedValues(getResources().getStringArray(R.array.vibration_intensity));
        npvVibrationIntensity.setMaxValue(2);
        npvVibrationIntensity.setMinValue(0);
        npvVibrationIntensity.setValue(0);
        return view;
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

    public void showWarningRssiDialog() {
        BottomDialog dialog = new BottomDialog();
        dialog.setDatas(warningRssiList, warningRssiListIndex);
        dialog.setListener(value -> {
            warningRssiListIndex = value;
            warningRssiValue = value - 127;
            tvWarningValue.setText(String.valueOf(warningRssiValue));
            tvWarningTips.setText(getString(R.string.warning_tips, warningRssiValue));
        });
        dialog.show(activity.getSupportFragmentManager());
    }

    public boolean isValid() {
        final String durationStr = etVibrationDuration.getText().toString();
        final String cycleStr = etVibrationCycle.getText().toString();
        if (TextUtils.isEmpty(durationStr))
            return false;
        if (TextUtils.isEmpty(cycleStr))
            return false;
        int duration = Integer.parseInt(durationStr);
        if (duration > 10)
            return false;
        if (TextUtils.isEmpty(cycleStr))
            return false;
        int cycle = Integer.parseInt(cycleStr);
        if (cycle < 1 || cycle > 600)
            return false;
        return true;
    }

    public boolean isDurationLessThanCycle() {
        final String durationStr = etVibrationDuration.getText().toString();
        final String cycleStr = etVibrationCycle.getText().toString();
        int duration = Integer.parseInt(durationStr);
        int cycle = Integer.parseInt(cycleStr);
        if (duration > cycle) {
            AlertMessageDialog dialog = new AlertMessageDialog();
            dialog.setCancelGone();
            dialog.setMessage("Vibration Cycle should be no less than Duration of  Vibration");
            dialog.setConfirm("OK");
            dialog.show(activity.getSupportFragmentManager());
            return false;
        }
        return true;
    }

    public void saveParams() {
        final int scanIntervalProgress = sbScanInterval.getProgress();
        final int alarmNotify = npvAlarmNotify.getValue();
        final int alarmTriggerRssiProgress = sbAlarmTriggerRssi.getProgress();
        final int intensityValue = npvVibrationIntensity.getValue();
        int intensity = 0;
        switch (intensityValue) {
            case 0:
                intensity = 10;
                break;
            case 1:
                intensity = 50;
                break;
            case 2:
                intensity = 100;
                break;
        }
        final String durationStr = etVibrationDuration.getText().toString();
        final String cycleStr = etVibrationCycle.getText().toString();
        int duration = Integer.parseInt(durationStr);
        int cycle = Integer.parseInt(cycleStr);
        int rssi = alarmTriggerRssiProgress - 127;
        List<OrderTask> orderTasks = new ArrayList<>();

        orderTasks.add(OrderTaskAssembler.setScanInterval(scanIntervalProgress));
        orderTasks.add(OrderTaskAssembler.setAlarmNotify(alarmNotify));
        orderTasks.add(OrderTaskAssembler.setVibrationIntensity(intensity));
        orderTasks.add(OrderTaskAssembler.setVibrationDuration(duration));
        orderTasks.add(OrderTaskAssembler.setVibrationCycle(cycle));
        orderTasks.add(OrderTaskAssembler.setWarningRssi(rssi));
        orderTasks.add(OrderTaskAssembler.setAlarmTriggerRssi(rssi));

        LoRaLW003MokoSupport.getInstance().sendOrder(orderTasks.toArray(new OrderTask[]{}));
    }

    public void setScanInterval(int time) {
        if (time <= 600)
            sbScanInterval.setProgress(time);
    }

    public void setVibrationIntansity(int intansity) {
        switch (intansity) {
            case 10:
                npvVibrationIntensity.setValue(0);
                break;
            case 50:
                npvVibrationIntensity.setValue(1);
                break;
            case 100:
                npvVibrationIntensity.setValue(2);
                break;
        }
    }

    private int warningMax;
    private int warningRssiValue;
    private ArrayList<String> warningRssiList;
    private int warningRssiListIndex;

    public void setAlarmTriggerRssi(int rssi) {
        int progress = rssi + 127;
        if (progress >= 0 && progress <= 127) {
            sbAlarmTriggerRssi.setProgress(progress);
            int value = progress - 127;
            tvAlarmTriggerRssiValue.setText(String.format("%dBm", value));
            tvAlarmTriggerRssiTips.setText(getString(R.string.alarm_trigger_rssi, value));
            warningMax = rssi;
            tvWarningRange.setText(getString(R.string.warning_range, rssi));
            createWarningRssiList();
        }
    }

    private void createWarningRssiList() {
        if (warningRssiList == null) {
            warningRssiList = new ArrayList<>();
        } else {
            warningRssiList.clear();
        }
        int warningRssiMax = warningMax + 127;
        for (int i = 0; i <= warningRssiMax; i++) {
            String rssiStr = String.valueOf(i - 127);
            warningRssiList.add(0, rssiStr);
        }
    }

    public void setWarningRssi(int rssi) {
        warningRssiValue = rssi;
        warningRssiListIndex = rssi + 127;
        tvWarningValue.setText(String.valueOf(warningRssiValue));
        tvWarningTips.setText(getString(R.string.warning_tips, warningRssiValue));
    }

    public void setAlarmNotify(int alarmNotify) {
        if (alarmNotify <= 3)
            npvAlarmNotify.setValue(alarmNotify);
    }

    public void setVibrationDuration(int duration) {
        etVibrationDuration.setText(String.valueOf(duration));
    }

    public void setVibrationCycle(int cycle) {
        if (cycle >= 1 && cycle <= 600) {
            etVibrationCycle.setText(String.valueOf(cycle));
        }
    }
}
