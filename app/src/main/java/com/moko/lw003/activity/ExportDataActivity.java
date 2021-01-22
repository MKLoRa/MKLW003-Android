package com.moko.lw003.activity;


import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.moko.ble.lib.MokoConstants;
import com.moko.ble.lib.event.ConnectStatusEvent;
import com.moko.ble.lib.event.OrderTaskResponseEvent;
import com.moko.ble.lib.task.OrderTaskResponse;
import com.moko.ble.lib.utils.MokoUtils;
import com.moko.lw003.AppConstants;
import com.moko.lw003.BuildConfig;
import com.moko.lw003.R;
import com.moko.lw003.R2;
import com.moko.lw003.adapter.ExportDataListAdapter;
import com.moko.lw003.dialog.AlertMessageDialog;
import com.moko.lw003.dialog.LoadingMessageDialog;
import com.moko.lw003.utils.ToastUtils;
import com.moko.lw003.utils.Utils;
import com.moko.support.lw003.LoRaLW003MokoSupport;
import com.moko.support.lw003.OrderTaskAssembler;
import com.moko.support.lw003.entity.ExportData;
import com.moko.support.lw003.entity.OrderCHAR;
import com.moko.support.lw003.entity.ParamsKeyEnum;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class ExportDataActivity extends BaseActivity {

    private static final String TRACKED_FILE = "tracked.txt";

    private static String PATH_LOGCAT;
    @BindView(R2.id.et_time)
    EditText etTime;
    @BindView(R2.id.tv_start)
    TextView tvStart;
    @BindView(R2.id.iv_sync)
    ImageView ivSync;
    @BindView(R2.id.tv_sync)
    TextView tvSync;
    @BindView(R2.id.tv_empty)
    TextView tvEmpty;
    @BindView(R2.id.tv_export)
    TextView tvExport;
    @BindView(R2.id.tv_sum)
    TextView tvSum;
    @BindView(R2.id.tv_count)
    TextView tvCount;
    @BindView(R2.id.rv_export_data)
    RecyclerView rvExportData;
    private boolean mReceiverTag = false;
    private StringBuilder storeString;
    private ArrayList<ExportData> exportDatas;
    private boolean mIsSync;
    private ExportDataListAdapter adapter;
    private boolean mIsBack;
    private Handler mHandler;
    private boolean mIsStart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lw003_activity_export_data);
        ButterKnife.bind(this);
        exportDatas = LoRaLW003MokoSupport.getInstance().exportDatas;
        storeString = LoRaLW003MokoSupport.getInstance().storeString;
        if (exportDatas != null && exportDatas.size() > 0 && storeString != null) {
            mIsStart = true;
            tvCount.setText(String.format("Count:%d", exportDatas.size()));
            tvExport.setEnabled(true);
            tvEmpty.setEnabled(true);
            tvStart.setEnabled(false);
        } else {
            exportDatas = new ArrayList<>();
            storeString = new StringBuilder();
        }
        mHandler = new Handler();
        adapter = new ExportDataListAdapter();
        adapter.openLoadAnimation();
        adapter.replaceData(exportDatas);
        rvExportData.setLayoutManager(new LinearLayoutManager(this));
        rvExportData.setAdapter(adapter);
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            // 优先保存到SD卡中
            PATH_LOGCAT = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + (BuildConfig.IS_LIBRARY ? "MKLoRa" : "LW003") + File.separator + TRACKED_FILE;
        } else {
            // 如果SD卡不存在，就保存到本应用的目录下
            PATH_LOGCAT = getFilesDir().getAbsolutePath() + File.separator + (BuildConfig.IS_LIBRARY ? "MKLoRa" : "LW003") + File.separator + TRACKED_FILE;
        }
        EventBus.getDefault().register(this);
        // 注册广播接收器
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(mReceiver, filter);
        mReceiverTag = true;
        if (!LoRaLW003MokoSupport.getInstance().isBluetoothOpen()) {
            // 蓝牙未打开，开启蓝牙
            LoRaLW003MokoSupport.getInstance().enableBluetooth();
        }
    }

    @Subscribe(threadMode = ThreadMode.POSTING, priority = 200)
    public void onConnectStatusEvent(ConnectStatusEvent event) {
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
            if (MokoConstants.ACTION_CURRENT_DATA.equals(action)) {
                OrderTaskResponse response = event.getResponse();
                OrderCHAR orderCHAR = (OrderCHAR) response.orderCHAR;
                int responseType = response.responseType;
                byte[] value = response.responseValue;
                switch (orderCHAR) {
                    case CHAR_STORAGE_DATA_NOTIFY:
                        final int length = value.length;
                        int header = value[0] & 0xFF;
                        int flag = value[1] & 0xFF;// notify
                        int cmd = value[2] & 0xFF;
                        int len = value[3] & 0xFF;
                        if (header == 0xED && flag == 0x02 && cmd == 0x01) {
                            int dataCount = value[4] & 0xFF;
                            if (dataCount > 0) {
                                int index = 5;
                                while (index < length) {
                                    int dataLength = value[index];
                                    index += 1;
                                    byte[] timeBytes = Arrays.copyOfRange(value, index, index + 7);
                                    byte[] macBytes = Arrays.copyOfRange(value, index + 7, index + 13);

                                    byte[] yearBytes = Arrays.copyOfRange(timeBytes, 0, 2);
                                    int year = MokoUtils.toInt(yearBytes);
                                    int month = timeBytes[2] & 0xff;
                                    int day = timeBytes[3] & 0xff;
                                    int hour = timeBytes[4] & 0xff;
                                    int minute = timeBytes[5] & 0xff;
                                    int second = timeBytes[6] & 0xff;

                                    Calendar calendar = Calendar.getInstance();
                                    calendar.set(Calendar.YEAR, year);
                                    calendar.set(Calendar.MONTH, month - 1);
                                    calendar.set(Calendar.DAY_OF_MONTH, day);
                                    calendar.set(Calendar.HOUR_OF_DAY, hour);
                                    calendar.set(Calendar.MINUTE, minute);
                                    calendar.set(Calendar.SECOND, second);
                                    final String time = Utils.calendar2strDate(calendar, AppConstants.PATTERN_YYYY_MM_DD_HH_MM_SS);

                                    StringBuffer stringBuffer = new StringBuffer();
                                    for (int i = 0, l = macBytes.length; i < l; i++) {
                                        stringBuffer.append(MokoUtils.byte2HexString(macBytes[i]));
                                        if (i < (l - 1))
                                            stringBuffer.append(":");
                                    }
                                    final String mac = stringBuffer.toString();

                                    final int rssi = value[index + 13];
                                    final String rssiStr = String.format("%ddBm", rssi);

                                    String rawData = "";
                                    if (dataLength > 14) {
                                        byte[] rawDataBytes = Arrays.copyOfRange(value, index + 14, index + dataLength);
                                        rawData = MokoUtils.bytesToHexString(rawDataBytes);
                                    }

                                    ExportData exportData = new ExportData();

                                    exportData.time = time;
                                    exportData.rssi = rssi;
                                    exportData.mac = mac;
                                    exportData.rawData = rawData;
                                    exportDatas.add(exportData);
                                    tvCount.setText(String.format("Count:%d", exportDatas.size()));

                                    storeString.append(String.format("Time:%s", time));
                                    storeString.append("\n");
                                    storeString.append(String.format("Mac Address:%s", mac));
                                    storeString.append("\n");
                                    storeString.append(String.format("RSSI:%s", rssiStr));
                                    storeString.append("\n");
                                    if (!TextUtils.isEmpty(rawData)) {
                                        storeString.append(String.format("Raw Data:%s", rawData));
                                        storeString.append("\n");
                                    }
                                    storeString.append("\n");
                                    index += dataLength;
                                }
                                adapter.replaceData(exportDatas);
                            } else {
                                byte[] sumBytes = Arrays.copyOfRange(value, 5, length);
                                int sum = MokoUtils.toInt(sumBytes);
                                tvSum.setText(String.format("Sum:%d", sum));
                            }

                            if (mIsBack && !mIsSync) {
                                if (mHandler.hasMessages(0)) {
                                    mHandler.removeMessages(0);
                                    mHandler.postDelayed(() -> {
                                        dismissSyncProgressDialog();
                                        LoRaLW003MokoSupport.getInstance().exportDatas = exportDatas;
                                        LoRaLW003MokoSupport.getInstance().storeString = storeString;
                                        finish();
                                    }, 2000);
                                }
                            }
                        }
                        break;
                }
            }
            if (MokoConstants.ACTION_ORDER_TIMEOUT.equals(action)) {
            }
            if (MokoConstants.ACTION_ORDER_FINISH.equals(action)) {
                if (!mIsBack) {
                    dismissSyncProgressDialog();
                }
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
                                    case KEY_CLEAR_STORAGE_DATA:
                                        if (result != 1) {
                                            ToastUtils.showToast(this, "Failed");
                                        } else {
                                            storeString = new StringBuilder();
                                            writeTrackedFile("");
                                            exportDatas.clear();
                                            adapter.replaceData(exportDatas);
                                            tvExport.setEnabled(false);
                                            tvSum.setText("Sum:N/A");
                                            tvCount.setText("Count:N/A");
                                            ToastUtils.showToast(this, "Empty success!");
                                        }
                                        break;
                                    case KEY_SYNC_ENABLE:
                                        if (result != 1) {
                                            ToastUtils.showToast(this, "Failed");
                                        } else {
                                            if (mIsBack) {
                                                mHandler.postDelayed(() -> {
                                                    dismissSyncProgressDialog();
                                                    LoRaLW003MokoSupport.getInstance().exportDatas = exportDatas;
                                                    LoRaLW003MokoSupport.getInstance().storeString = storeString;
                                                    finish();
                                                }, 2000);
                                            }
                                            if (!mIsSync) {
                                                mIsSync = true;
                                                tvEmpty.setEnabled(false);
                                                tvExport.setEnabled(false);
                                                Animation animation = AnimationUtils.loadAnimation(this, R.anim.lw003_rotate_refresh);
                                                ivSync.startAnimation(animation);
                                                tvSync.setText("Stop");
                                            } else {
                                                mIsSync = false;
                                                tvEmpty.setEnabled(true);
                                                if (exportDatas != null && exportDatas.size() > 0 && storeString != null) {
                                                    tvExport.setEnabled(true);
                                                }
                                                ivSync.clearAnimation();
                                                tvSync.setText("Sync");
                                            }
                                        }
                                        break;
                                    case KEY_READ_STORAGE_DATA:
                                        if (result != 1) {
                                            ToastUtils.showToast(this, "Failed");
                                        } else {
                                            mIsStart = true;
                                            mIsSync = true;
                                            tvStart.setEnabled(false);
                                            tvEmpty.setEnabled(false);
                                            tvExport.setEnabled(false);
                                            Animation animation = AnimationUtils.loadAnimation(this, R.anim.lw003_rotate_refresh);
                                            ivSync.startAnimation(animation);
                                            tvSync.setText("Stop");
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

    private void back() {
        if (mIsSync) {
            mIsBack = true;
            showSyncingProgressDialog();
            LoRaLW003MokoSupport.getInstance().sendOrder(OrderTaskAssembler.setSyncEnable(0));
            return;
        }
        LoRaLW003MokoSupport.getInstance().exportDatas = exportDatas;
        LoRaLW003MokoSupport.getInstance().storeString = storeString;
        finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            back();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void onStart(View view) {
        final String timeStr = etTime.getText().toString();
        if (TextUtils.isEmpty(timeStr)) {
            ToastUtils.showToast(this, "Opps！Save failed. Please check the input characters and try again.");
            return;
        }
        int time = Integer.parseInt(timeStr);
        if (time < 1 || time > 65535) {
            ToastUtils.showToast(this, "Opps！Save failed. Please check the input characters and try again.");
            return;
        }
        showSyncingProgressDialog();
        LoRaLW003MokoSupport.getInstance().sendOrder(OrderTaskAssembler.readStorageData(time));
    }

    public void onSync(View view) {
        if (!mIsStart)
            return;
        if (isWindowLocked())
            return;
        showSyncingProgressDialog();
        if (!mIsSync) {
            LoRaLW003MokoSupport.getInstance().sendOrder(OrderTaskAssembler.setSyncEnable(1));
        } else {
            LoRaLW003MokoSupport.getInstance().sendOrder(OrderTaskAssembler.setSyncEnable(0));
        }
    }

    public void onEmpty(View view) {
        AlertMessageDialog dialog = new AlertMessageDialog();
        dialog.setTitle("Warning!");
        dialog.setMessage("Are you sure to empty the saved tracked datas?");
        dialog.setOnAlertConfirmListener(() -> {
            showSyncingProgressDialog();
            LoRaLW003MokoSupport.getInstance().sendOrder(OrderTaskAssembler.clearStorageData());
        });
        dialog.show(getSupportFragmentManager());
    }

    public void onExport(View view) {
        showSyncingProgressDialog();
        writeTrackedFile("");
        tvExport.postDelayed(() -> {
            dismissSyncProgressDialog();
            final String log = storeString.toString();
            if (!TextUtils.isEmpty(log)) {
                writeTrackedFile(log);
                File file = getTrackedFile();
                // 发送邮件
                String address = "Development@mokotechnology.com";
                String title = "Tracked Log";
                String content = title;
                Utils.sendEmail(ExportDataActivity.this, address, content, title, "Choose Email Client", file);
            }
        }, 500);
    }

    public void onBack(View view) {
        back();
    }


    public static void writeTrackedFile(String thLog) {
        File file = new File(PATH_LOGCAT);
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write(thLog);
            fileWriter.flush();
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static File getTrackedFile() {
        File file = new File(PATH_LOGCAT);
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }
}
