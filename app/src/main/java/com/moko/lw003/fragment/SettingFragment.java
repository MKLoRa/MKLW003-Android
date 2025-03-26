package com.moko.lw003.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.moko.lib.loraui.dialog.BottomDialog;
import com.moko.lw003.activity.DeviceInfoActivity;
import com.moko.lw003.databinding.Lw003FragmentSettingBinding;
import com.moko.lw003.dialog.TriggerSensitivityDialog;
import com.moko.support.lw003.LoRaLW003MokoSupport;
import com.moko.support.lw003.OrderTaskAssembler;

import java.util.ArrayList;

public class SettingFragment extends Fragment {
    private static final String TAG = SettingFragment.class.getSimpleName();
    private Lw003FragmentSettingBinding mBind;
    private DeviceInfoActivity activity;
    private ArrayList<String> mPowerStatusValues;
    private int mSelectedPowerStatus;
    private int mTriggerSensitivity;

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
        mBind = Lw003FragmentSettingBinding.inflate(inflater, container, false);
        activity = (DeviceInfoActivity) getActivity();
        mPowerStatusValues = new ArrayList<>();
        mPowerStatusValues.add("Switch Off");
        mPowerStatusValues.add("Switch On");
        mPowerStatusValues.add("Revert to last status");
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

    public void setDeviceName(String deviceName) {
        mBind.tvAdvInfo.setText(deviceName);
    }

    public void setTamperDetection(int enable, int triggerSensitivity) {
        mTriggerSensitivity = triggerSensitivity;
        if (enable == 0) {
            mBind.tvTamperDetection.setText("OFF");
        } else {
            mBind.tvTamperDetection.setText(String.valueOf(triggerSensitivity));
        }
    }

    public void setPowerStatus(int status) {
        mSelectedPowerStatus = status;
        mBind.tvDefaultPowerStatus.setText(mPowerStatusValues.get(status));
    }

    public void showTamperDetectionDialog() {
        final TriggerSensitivityDialog dialog = new TriggerSensitivityDialog(getActivity());
        dialog.setData(mTriggerSensitivity);
        dialog.setOnSensitivityClicked(sensitivity -> {
            mTriggerSensitivity = sensitivity;
            if (sensitivity == 0) {
                mBind.tvTamperDetection.setText("OFF");
            } else {
                mBind.tvTamperDetection.setText(String.valueOf(sensitivity));
            }
            activity.showSyncingProgressDialog();
            LoRaLW003MokoSupport.getInstance().sendOrder(
                    OrderTaskAssembler.setTamperDetection(sensitivity == 0 ? 0 : 1, sensitivity));
        });
        dialog.show();
    }

    public void showPowerStatusDialog() {
        BottomDialog dialog = new BottomDialog();
        dialog.setDatas(mPowerStatusValues, mSelectedPowerStatus);
        dialog.setListener(value -> {
            mBind.tvDefaultPowerStatus.setText(mPowerStatusValues.get(value));
            activity.showSyncingProgressDialog();
            LoRaLW003MokoSupport.getInstance().sendOrder(OrderTaskAssembler.setPowerStatus(value));
        });
        dialog.show(activity.getSupportFragmentManager());
    }
}
