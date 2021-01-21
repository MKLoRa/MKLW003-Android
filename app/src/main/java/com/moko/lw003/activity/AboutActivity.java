package com.moko.lw003.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.moko.lw003.BuildConfig;
import com.moko.lw003.R;
import com.moko.lw003.R2;
import com.moko.lw003.utils.Utils;

import butterknife.BindView;
import butterknife.ButterKnife;


public class AboutActivity extends BaseActivity {
    @BindView(R2.id.app_version)
    TextView appVersion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lw003_activity_about);
        ButterKnife.bind(this);
        if (!BuildConfig.IS_LIBRARY) {
            appVersion.setText(String.format("Version:V%s", Utils.getVersionInfo(this)));
        }
    }

    public void onBack(View view) {
        finish();
    }

    public void onCompanyWebsite(View view) {
        Uri uri = Uri.parse("https://" + getString(R.string.company_website));
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }
}
