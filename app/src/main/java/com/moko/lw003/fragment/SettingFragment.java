package com.moko.lw003.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.moko.lw003.R;
import com.moko.lw003.R2;
import com.moko.lw003.activity.DeviceInfoActivity;
import com.moko.lw003.dialog.BottomDialog;
import com.moko.lw003.dialog.ChangePasswordDialog;
import com.moko.support.lw003.LoRaLW003MokoSupport;
import com.moko.support.lw003.OrderTaskAssembler;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SettingFragment extends Fragment {
    private static final String TAG = SettingFragment.class.getSimpleName();
    @BindView(R2.id.tv_adv_info)
    TextView tvAdvInfo;
    @BindView(R2.id.tv_local_data_sync)
    TextView tvLocalDataSync;
    @BindView(R2.id.tv_tamper_detection)
    TextView tvTamperDetection;
    @BindView(R2.id.tv_default_power_status)
    TextView tvDefaultPowerStatus;

    private DeviceInfoActivity activity;
    private ArrayList<String> mPowerStatusValues;

    public SettingFragment() {
    }


    public static SettingFragment newInstance() {
        SettingFragment fragment = new SettingFragment();
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
        View view = inflater.inflate(R.layout.lw003_fragment_setting, container, false);
        ButterKnife.bind(this, view);
        activity = (DeviceInfoActivity) getActivity();
        mPowerStatusValues = new ArrayList<>();
        mPowerStatusValues.add("Switch Off");
        mPowerStatusValues.add("Switch On");
        mPowerStatusValues.add("Revert to last status");
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

    public void setDeviceName(String deviceName) {
        tvAdvInfo.setText(deviceName);
    }

    public void setTamperDetection(int enable, int triggerSensitivity) {
        if (enable == 0) {
            tvTamperDetection.setText("OFF");
        } else {
            tvTamperDetection.setText(String.valueOf(triggerSensitivity));
        }
    }

    public void setPowerStatus(int status) {
        BottomDialog dialog = new BottomDialog();
        dialog.setDatas(mPowerStatusValues, status);
        dialog.setListener(value -> {
            tvDefaultPowerStatus.setText(mPowerStatusValues.get(status));
            activity.showSyncingProgressDialog();
            LoRaLW003MokoSupport.getInstance().sendOrder(OrderTaskAssembler.setPowerStatus(value));
        });
        dialog.show(activity.getSupportFragmentManager());
    }
}
