package com.moko.lw003.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.moko.lw003.activity.DeviceInfoActivity;
import com.moko.lw003.databinding.Lw003FragmentDeviceBinding;

public class DeviceFragment extends Fragment {
    private static final String TAG = DeviceFragment.class.getSimpleName();
    private Lw003FragmentDeviceBinding mBind;

    private DeviceInfoActivity activity;

    public DeviceFragment() {
    }


    public static DeviceFragment newInstance() {
        DeviceFragment fragment = new DeviceFragment();
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
        mBind = Lw003FragmentDeviceBinding.inflate(inflater, container, false);
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

    public void setBatteryValtage(int battery) {
        mBind.tvBatteryVoltage.setText(String.format("%d%%", battery));
    }

    public void setMacAddress(String macAddress) {
        mBind.tvMacAddress.setText(macAddress);
    }

    public void setProductModel(String productModel) {
        mBind.tvProductModel.setText(productModel);
    }

    public void setSoftwareVersion(String softwareVersion) {
        mBind.tvSoftwareVersion.setText(softwareVersion);
    }

    public void setFirmwareVersion(String firmwareVersion) {
        mBind.tvFirmwareVersion.setText(firmwareVersion);
    }

    public void setHardwareVersion(String hardwareVersion) {
        mBind.tvHardwareVersion.setText(hardwareVersion);
    }

    public void setManufacture(String manufacture) {
        mBind.tvManufacture.setText(manufacture);
    }
}
