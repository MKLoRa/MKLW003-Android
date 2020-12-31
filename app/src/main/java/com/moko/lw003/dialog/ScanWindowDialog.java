package com.moko.lw003.dialog;

import android.content.Context;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import com.moko.lw003.R;
import com.moko.lw003.R2;

import butterknife.BindView;
import butterknife.OnClick;

public class ScanWindowDialog extends BaseDialog<Integer> implements SeekBar.OnSeekBarChangeListener {

    @BindView(R2.id.sb_scan_window)
    SeekBar sbScanWindow;
    @BindView(R2.id.tv_scan_window_value)
    TextView tvScanWindowValue;

    public ScanWindowDialog(Context context) {
        super(context);
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.lw003_dialog_scan_window;
    }

    @Override
    protected void renderConvertView(View convertView, Integer progress) {
        switch (progress) {
            case 0:
                tvScanWindowValue.setText("0");
                break;
            case 2:
                tvScanWindowValue.setText("1/2");
                break;
            case 3:
                tvScanWindowValue.setText("1/4");
                break;
            case 4:
                tvScanWindowValue.setText("1/8");
                break;
        }
        if (progress > 0) {
            progress -= 2;
        } else {
            progress = 4;
        }
        sbScanWindow.setProgress(progress);
        sbScanWindow.setOnSeekBarChangeListener(this);
    }

    @OnClick(R2.id.tv_cancel)
    public void onCancel(View view) {
        dismiss();
    }

    @OnClick(R2.id.tv_ensure)
    public void onEnsure(View view) {
        int progress = sbScanWindow.getProgress();
        dismiss();
        if (scanWindowListener != null)
            scanWindowListener.onEnsure(progress);
    }

    private ScanWindowListener scanWindowListener;

    public void setOnScanWindowClicked(ScanWindowListener scanWindowListener) {
        this.scanWindowListener = scanWindowListener;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        switch (progress) {
            case 3:
                tvScanWindowValue.setText("0");
                break;
            case 0:
                tvScanWindowValue.setText("1/2");
                break;
            case 1:
                tvScanWindowValue.setText("1/4");
                break;
            case 2:
                tvScanWindowValue.setText("1/8");
                break;
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    public interface ScanWindowListener {

        void onEnsure(int scanMode);
    }
}
