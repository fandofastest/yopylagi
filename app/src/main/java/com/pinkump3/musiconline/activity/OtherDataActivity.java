package com.pinkump3.musiconline.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.pinkump3.musiconline.R;
import com.facebook.ads.AdSize;
import com.facebook.ads.AudienceNetworkAds;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;

public class OtherDataActivity extends AppCompatActivity {

    WebView webView;
    RelativeLayout banner;
    ProgressBar progresswv;
    String webUrl;

    com.google.android.gms.ads.AdView adView;
    com.facebook.ads.AdView fbView;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other_data);

        webView = findViewById(R.id.webView);
        banner = findViewById(R.id.banner);
        progresswv = findViewById(R.id.progressWebView);
        webUrl = "https://www.jamendo.com/start";

        if (SplashActivity.ads_main.equals("fb")) {
            AudienceNetworkAds.initialize(this);
            fbView = new com.facebook.ads.AdView(this,SplashActivity.id_banner_main, AdSize.BANNER_HEIGHT_90);
            banner.addView(fbView);
            fbView.loadAd();
        } else {
            MobileAds.initialize(this);
            adView = new com.google.android.gms.ads.AdView(this);
            adView.setAdSize(com.google.android.gms.ads.AdSize.SMART_BANNER);
            adView.setAdUnitId(SplashActivity.id_banner_main);
            AdRequest adreqban = new AdRequest.Builder().build();
            banner.addView(adView);
            adView.loadAd(adreqban);
        }

        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().getAllowContentAccess();
        webView.getSettings().setPluginState(WebSettings.PluginState.ON);
        webView.getSettings().setSupportZoom(false);
        webView.getSettings().setBuiltInZoomControls(false);
        webView.getSettings().setSupportMultipleWindows(true);
        webView.setWebViewClient(new MyWebViewClient() {
        });

        final Activity MyActivity = this;

        webView.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress) {
                // Menghilangkan progressbar dan menampilkan tampilan Loading
                MyActivity.setTitle("Loading...");
                progresswv.setVisibility(View.VISIBLE);

                if (progress == 100) {
                    MyActivity.setTitle(R.string.app_name);
                    progresswv.setVisibility(View.GONE);
                }
            }
        });

        webView.loadUrl(webUrl);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && webView.canGoBack()) {
            webView.goBack();
            return true;
        } else  if (keyCode == KeyEvent.KEYCODE_BACK) {
            Exit();

        }
        return super.onKeyDown(keyCode, event);

    }

    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
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

    public void ShareApp() {
        Intent share = new Intent(Intent.ACTION_SEND);
        final String appName = this.getPackageName();
        share.setType("text/plain");
        share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);

        // Add data to the intent, the receiving app will decide
        // what to do with it.
        share.putExtra(Intent.EXTRA_SUBJECT, "Download and enjoy this good application");
        share.putExtra(Intent.EXTRA_TEXT, "http://play.google.com/store/apps/details?id="
                + appName);

        startActivity(Intent.createChooser(share, "Share and invite your friends to View this Apps !!"));
    }

    public void RateApps() {
        Uri uri = Uri.parse("market://details?id=" + getPackageName());
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        try {
            startActivity(goToMarket);
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id="
                            + getPackageName())));
        }
    }

    public void Exit() {
        new AlertDialog.Builder(OtherDataActivity.this)
                .setTitle("Are you sure to exit.?")
                .setMessage("Please take your time to support us by rating and leave a review on PlayStore. Thanks.")
                .setIcon(R.mipmap.ic_launcher_round)
                .setPositiveButton("Rate", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        RateApps();
                        webView.destroy();
                        finish();
                    }
                })
                .setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        webView.destroy();
                        System.exit(1);
                        finish();
                    }
                })
                .show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.menu_policy) {
            Intent i = new Intent(getApplicationContext(), PolicyActivity.class);
            startActivity(i);
        } else if (id == R.id.menu_share) {
            ShareApp();
        } else if (id == R.id.menu_rate) {
            RateApps();
        }

        return super.onOptionsItemSelected(item);
    }
}
