package com.pinkump3.musiconline.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pinkump3.musiconline.R;
import com.facebook.ads.AdSize;
import com.google.android.gms.ads.AdRequest;

import java.io.IOException;
import java.io.InputStream;

import static com.pinkump3.musiconline.activity.MainActivity.offline_mode;

public class PolicyActivity extends AppCompatActivity {

    TextView textPolicy;
    RelativeLayout banner;
    com.google.android.gms.ads.AdView adView;
    com.facebook.ads.AdView fbView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_policy);

        textPolicy = findViewById(R.id.text_policy);
        banner = findViewById(R.id.bannerContainer);

        if (!offline_mode) {
            if (SplashActivity.ads_main.equals("fb")) {
                fbView = new com.facebook.ads.AdView(this, SplashActivity.id_banner_main, AdSize.BANNER_HEIGHT_50);
                banner.addView(fbView);
                fbView.loadAd();
            } else {
                adView = new com.google.android.gms.ads.AdView(this);
                adView.setAdUnitId(SplashActivity.id_banner_main);
                adView.setAdSize(com.google.android.gms.ads.AdSize.SMART_BANNER);
                banner.addView(adView);
                AdRequest adreq = new AdRequest.Builder().build();
                adView.loadAd(adreq);
            }
        }

        String txt;
        try {
            InputStream is = getAssets().open("policy.txt");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            txt = new String(buffer);
        } catch (IOException ex) {
            return;
        }
        textPolicy.setText(txt);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (SplashActivity.ads_main.equals("fb")) {
            fbView.destroy();
            fbView = null;
        } else {
            adView.destroy();
            adView = null;
        }
    }
}
