package com.moko.lw003.dialog;

import android.text.TextUtils;
import android.view.View;

import com.moko.lw003.R;
import com.moko.lw003.R2;
import com.moko.lw003.entity.Region;
import com.moko.lw003.view.WheelView;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RegionBottomDialog extends MokoBaseDialog {


    @BindView(R2.id.wv_bottom)
    WheelView wvBottom;
    private ArrayList<Region> mDatas;
    private HashMap<Integer, Region> regionHashMap;
    private ArrayList<String> mNames;
    private int mIndex;


    @Override
    public int getLayoutRes() {
        return R.layout.lw003_dialog_bottom;
    }

    @Override
    public void bindView(View v) {
        ButterKnife.bind(this, v);
        regionHashMap = new HashMap<>();
        mNames = new ArrayList<>();
        for (int i = 0, size = mDatas.size(); i < size; i++) {
            Region region = mDatas.get(i);
            regionHashMap.put(i, region);
            if (mIndex == region.value) {
                mIndex = i;
            }
            mNames.add(region.name);
        }
        wvBottom.setData(mNames);
        wvBottom.setDefault(mIndex);
    }

    @Override
    public float getDimAmount() {
        return 0.7f;
    }

    @OnClick(R2.id.tv_cancel)
    public void onCancel(View view) {
        dismiss();
    }

    @OnClick(R2.id.tv_confirm)
    public void onConfirm(View view) {
        if (TextUtils.isEmpty(wvBottom.getSelectedText())) {
            return;
        }
        dismiss();
        final int selected = wvBottom.getSelected();
        if (listener != null) {
            Region region = regionHashMap.get(selected);
            listener.onValueSelected(region.value);
        }
    }

    public void setDatas(ArrayList<Region> datas, int index) {
        this.mDatas = datas;
        this.mIndex = index;
    }

    private OnBottomListener listener;

    public void setListener(OnBottomListener listener) {
        this.listener = listener;
    }

    public interface OnBottomListener {
        void onValueSelected(int value);
    }
}
