package com.moko.lw003.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.moko.lw003.activity.DeviceInfoActivity;
import com.moko.lw003.databinding.Lw003FragmentLoraBinding;
import com.moko.support.lw003.LoRaLW003MokoSupport;
import com.moko.support.lw003.OrderTaskAssembler;

public class LoRaFragment extends Fragment {
    private static final String TAG = LoRaFragment.class.getSimpleName();
    private Lw003FragmentLoraBinding mBind;

    private DeviceInfoActivity activity;

    public LoRaFragment() {
    }


    public static LoRaFragment newInstance() {
        LoRaFragment fragment = new LoRaFragment();
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
        mBind = Lw003FragmentLoraBinding.inflate(inflater, container, false);
        activity = (DeviceInfoActivity) getActivity();
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

    public void setLoRaInfo(String loraInfo) {
        mBind.tvLoraInfo.setText(loraInfo);
    }

    public void setNetworkCheck(int networkCheck) {
        String networkCheckDisPlay = "";
        switch (networkCheck) {
            case 0:
                networkCheckDisPlay = "Disconnected";
                break;
            case 1:
                networkCheckDisPlay = "Connecting";
                break;
            case 2:
                networkCheckDisPlay = "Connected";
                break;
        }
        mBind.tvNetworkCheck.setText(networkCheckDisPlay);
    }

    public void setMulticastEnable(int enable) {
        mBind.tvMulticastSetting.setText(enable == 1 ? "ON" : "OFF");
    }

    public boolean isValid() {
        final String timeSyncIntervalStr = mBind.etTimeSyncInterval.getText().toString();
        if (TextUtils.isEmpty(timeSyncIntervalStr))
            return false;
        final int timeSyncInterval = Integer.parseInt(timeSyncIntervalStr);
        if (timeSyncInterval > 240)
            return false;
        return true;
    }


    public void saveParams() {
        final String timeSyncIntervalStr = mBind.etTimeSyncInterval.getText().toString();
        final int timeSyncInterval = Integer.parseInt(timeSyncIntervalStr);
        LoRaLW003MokoSupport.getInstance().sendOrder(
                OrderTaskAssembler.setTimeSyncInterval(timeSyncInterval));
    }

    public void setTimeSyncInterval(int interval) {
        mBind.etTimeSyncInterval.setText(String.valueOf(interval));
    }
}
