package com.pinkump3.musiconline.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.pinkump3.musiconline.R;
import com.pinkump3.musiconline.SharedPreference;
import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AudienceNetworkAds;
import com.facebook.ads.InterstitialAdExtendedListener;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.onesignal.OneSignal;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URL;
import java.util.Enumeration;

public class SplashActivity extends AppCompatActivity {


    static String PACKAGE_NAME;
    public static String id_inter_main = "";
    public static String id_banner_main = "";
    public static String ads_main = "";
    public static String sc_key = "";
    public static String availability = "";
    public static String ads_home_avail;
    public static String download_avail;
    public static String package_ads, title_ads, icon_ads, img_large_ads, desc_ads;
    public static String[] ip_detects;
    public static String deviceIpAddress = "";

    static String link_mv;
    public static int adShow, adCount;
    static String json = "";
    static boolean ip_block;

    ImageView splashImg;
    Animation anim;

    public static com.google.android.gms.ads.InterstitialAd interstitialAd;
    public static com.facebook.ads.InterstitialAd interstitialFb;
    SharedPreference sharedPreference;
    ProgressBar progress;
    Button startbtn, ratebtn;
    public static String folder;

    static int PERMISSIONCODE = 123123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }

        PACKAGE_NAME = getApplicationContext().getPackageName();
        json = getResources().getString(R.string.json);
        splashImg = findViewById(R.id.imgeSplash);
        progress = findViewById(R.id.splashProgress);
        startbtn = findViewById(R.id.startbtn);
        ratebtn = findViewById(R.id.ratebtn);
        sharedPreference = new SharedPreference(this);

        anim = AnimationUtils.loadAnimation(this, R.anim.fadein);
        folder = Environment.getExternalStorageDirectory() + File.separator + "Download";

        if (ConnectionCheck()) {
            new FetchData().execute();

            // OneSignal Initialization
            OneSignal.startInit(this)
                    .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                    .unsubscribeWhenNotificationsAreDisabled(true)
                    .init();

            SystemClock.sleep(3500);

            if (sharedPreference.getApp_runFirst().equals("FIRST")) {
                showPolicy();
            } else {
                startApp();
            }
        } else {
            ConnectionAlert();
        }

        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                splashImg.setVisibility(View.VISIBLE);
                splashImg.setImageResource(R.drawable.imgesplsh);
            }

            @Override
            public void onAnimationEnd(Animation animation) {

                if (availability.equals("y")) {

                    startbtn.setVisibility(View.VISIBLE);
                    ratebtn.setVisibility(View.VISIBLE);

//                        Toast.makeText(SplashActivity.this, "your IP : " + getLocalIpAddress(), Toast.LENGTH_SHORT).show();
//                    Toast.makeText(SplashActivity.this, "Ip for check : " + Arrays.toString(ip_detects), Toast.LENGTH_SHORT).show();
//                        Toast.makeText(SplashActivity.this, "Sorry., you not eligible to open this apps" + Arrays.toString(ip_detects), Toast.LENGTH_LONG).show();

                } else {
                    AppMove();
                }

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        startbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(SplashActivity.this, MainActivity.class);
                    LoadInter(i);
//                if (check_ip(ip_detects,getIPAddress())) {
//                    Intent i = new Intent(SplashActivity.this,OtherDataActivity.class);
//                    LoadInter(i);
//                } else {
//                    Intent i = new Intent(SplashActivity.this, MainActivity.class);
//                    LoadInter(i);
//                }
                startbtn.setVisibility(View.INVISIBLE);
                ratebtn.setVisibility(View.INVISIBLE);
            }
        });

        ratebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RateApps();
            }
        });



    }

    public void LoadInter( final Intent intent) {

        final ProgressDialog interload = new ProgressDialog(SplashActivity.this);
        interload.setIndeterminate(false);
        interload.setCancelable(false);
        interload.setMessage("Please wait while ads loading...");
        interload.show();

        progress.setVisibility(View.GONE);
        if (ads_main.equals("fb")) {
            AudienceNetworkAds.initialize(this);
            interstitialFb = new com.facebook.ads.InterstitialAd(this, id_inter_main);
            interstitialFb.setAdListener(new InterstitialAdExtendedListener() {
                @Override
                public void onInterstitialActivityDestroyed() {

                }

                @Override
                public void onInterstitialDisplayed(Ad ad) {

                }

                @Override
                public void onInterstitialDismissed(Ad ad) {
                    startActivity(intent);
                    finish();
                }

                @Override
                public void onError(Ad ad, AdError adError) {
                    interload.dismiss();
                    startActivity(intent);
                    finish();
                }

                @Override
                public void onAdLoaded(Ad ad) {
                    interload.dismiss();
                    interstitialFb.show();

                }

                @Override
                public void onAdClicked(Ad ad) {

                }

                @Override
                public void onLoggingImpression(Ad ad) {

                }

                @Override
                public void onRewardedAdCompleted() {

                }

                @Override
                public void onRewardedAdServerSucceeded() {

                }

                @Override
                public void onRewardedAdServerFailed() {

                }
            });
            interstitialFb.loadAd();

        } else {
            MobileAds.initialize(this);
            interstitialAd = new com.google.android.gms.ads.InterstitialAd(this);
            interstitialAd.setAdUnitId(id_inter_main);
            AdRequest adreqin = new AdRequest.Builder().build();
            interstitialAd.loadAd(adreqin);
            interstitialAd.setAdListener(new AdListener() {
                @Override
                public void onAdLoaded() {
                    super.onAdLoaded();
                    interload.dismiss();
                    interstitialAd.show();
                }

                @Override
                public void onAdClosed() {
                    super.onAdClosed();
                    startActivity(intent);
                    finish();
                }

                @Override
                public void onAdFailedToLoad(int i) {
                    super.onAdFailedToLoad(i);
                    interload.dismiss();
                    startActivity(intent);
                    finish();
                }
            });

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSIONCODE && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            File file = new File(folder);
            if (!file.exists()) {
                file.mkdirs();
            }
            splashImg.startAnimation(anim);
            splashImg.setVisibility(View.VISIBLE);
        } else {
            PolicyWarning("permission");
        }
    }

    public void showPolicy() {
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

        AlertDialog alertDialogPolicy = new AlertDialog.Builder(SplashActivity.this).create();
        alertDialogPolicy.setTitle("Privacy Policy");
        alertDialogPolicy.setIcon(R.mipmap.ic_launcher);
        alertDialogPolicy.setMessage(txt);
        alertDialogPolicy.setButton(AlertDialog.BUTTON_POSITIVE, "Accept",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        sharedPreference.setApp_runFirst("NO");
                        startApp();

                    }
                });
        alertDialogPolicy.setButton(AlertDialog.BUTTON_NEGATIVE, "Decline",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        PolicyWarning("policy");

                    }
                });
        alertDialogPolicy.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                PolicyWarning("policy");
            }
        });
        alertDialogPolicy.show();
    }

    public static String getLocalIpAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
                        return inetAddress.getHostAddress();
                    }
                }
            }
        } catch (SocketException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public static String getIPAddress() {
        if (TextUtils.isEmpty(deviceIpAddress))
            new PublicIPAddress().execute();
        return deviceIpAddress;
    }


    public static class PublicIPAddress extends AsyncTask<String, Void, String> {
        InetAddress localhost = null;

        protected String doInBackground(String... urls) {
            try {
                localhost = InetAddress.getLocalHost();
                URL url_name = new URL("https://ipinfo.io/ip");
                BufferedReader sc = new BufferedReader(new InputStreamReader(url_name.openStream()));
                deviceIpAddress = sc.readLine().trim();
            } catch (Exception e) {
                deviceIpAddress = "";
            }
            return deviceIpAddress;
        }

        protected void onPostExecute(String string) {
            Log.d("deviceIpAddress", string);
        }
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

    @SuppressLint("StaticFieldLeak")
    private class FetchData extends AsyncTask<Void, Void, Void> {

        String data = "";

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                URL url = new URL(json);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String line = "";
                while (line != null) {
                    line = bufferedReader.readLine();
                    data = data + line;
                }
                try {

                    JSONObject json = new JSONObject(data);

                    JSONArray getParam = json.getJSONArray("appsdata");
                    for (int i = 0; i < getParam.length(); i++) {
                        if (PACKAGE_NAME.equals(getParam.getJSONObject(i).getString("package"))) {
                            adShow = getParam.getJSONObject(i).getInt("adShow");
                            ads_main = getParam.getJSONObject(i).getString("ads_main");
                            availability = getParam.getJSONObject(i).getString("avail");
                            link_mv = getParam.getJSONObject(i).getString("move_package");
                            sc_key = getParam.getJSONObject(i).getString("sc_key");
                            ads_home_avail = getParam.getJSONObject(i).getString("ads_home_avail");
                            download_avail = getParam.getJSONObject(i).getString("download");

                            title_ads = getParam.getJSONObject(i).getString("ads_title");
                            icon_ads = getParam.getJSONObject(i).getString("ads_icon");
                            package_ads = getParam.getJSONObject(i).getString("ads_url_package");
                            img_large_ads = getParam.getJSONObject(i).getString("ads_img_large");
                            desc_ads = getParam.getJSONObject(i).getString("ads_desc");

                            JSONArray data_ip = getParam.getJSONObject(i).getJSONArray("ip_address");
                            ip_detects = new String[data_ip.length()];
                            for (int j = 0; j < ip_detects.length; j++) {
                                ip_detects[j] = data_ip.getString(j);
                            }

                            if (ads_main.equals("fb")) {
                                id_inter_main = getParam.getJSONObject(i).getString("fb_inter");
                                id_banner_main = getParam.getJSONObject(i).getString("fb_banner");
                            } else {
                                id_inter_main = getParam.getJSONObject(i).getString("ad_inter");
                                id_banner_main = getParam.getJSONObject(i).getString("ad_banner");
                            }

                        }

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

        }
    }

    private static boolean check_ip(String[] arr, String toCheckIp)
    {
        // check if the specified element
        // is present in the array or not
        // using Linear Search method

        for (String element : arr) {
            if (toCheckIp.equals(element)) {
                 return true;
            }
        }

        return false;
    }

    public void startApp() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.System.canWrite(this)) {
                requestPermissions(new String[]{
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                }, PERMISSIONCODE);

            } else {
                splashImg.startAnimation(anim);
            }
        } else {
            File file = new File(folder);
            if (!file.exists()) {
                file.mkdirs();
            }
            splashImg.startAnimation(anim);
            splashImg.setVisibility(View.VISIBLE);
        }
    }

    public void PolicyWarning(String warning) {
        if (sharedPreference.getApp_runFirst().equals("FIRST")) {
            AlertDialog alertDialogPolicy = new AlertDialog.Builder(SplashActivity.this).create();
            alertDialogPolicy.setIcon(R.mipmap.ic_launcher_round);
            if (warning.equals("policy")) {
                alertDialogPolicy.setTitle("Warning !");
                alertDialogPolicy.setMessage("Please accept our policy before use this App.\nThank You.");
            } else if (warning.equals("permission")) {
                alertDialogPolicy.setTitle("Permission Needed !");
                alertDialogPolicy.setMessage("Please accept permission to make apps running well.");
            }

            alertDialogPolicy.setButton(AlertDialog.BUTTON_POSITIVE, "Restart",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            Intent i = getBaseContext().getPackageManager()
                                    .getLaunchIntentForPackage(getBaseContext().getPackageName());
                            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(i);
                            finish();
                        }
                    });
            alertDialogPolicy.setButton(AlertDialog.BUTTON_NEGATIVE, "Quit",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            System.exit(1);
                            finish();
                        }
                    });
            alertDialogPolicy.show();
        }
    }

    public boolean ConnectionCheck() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        } else {
            return false;
        }
    }

    public void AppMove() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                new AlertDialog.Builder(SplashActivity.this)
                        .setTitle("Apps in Maintenance")
                        .setCancelable(false)
                        .setMessage("This App is on maintenance,\nPlease go to our new apps with new feature.")
                        .setIcon(R.mipmap.ic_launcher_round)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Uri uri = Uri.parse("market://details?id=" + link_mv);
                                Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
                                try {
                                    startActivity(goToMarket);
                                } catch (ActivityNotFoundException e) {
                                    startActivity(new Intent(
                                            Intent.ACTION_VIEW,
                                            Uri.parse("http://play.google.com/store/apps/details?id="
                                                    + link_mv)));
                                }
                                finish();
                            }
                        })
                        .show();
            }
        });
    }

    public void ConnectionAlert() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                new AlertDialog.Builder(SplashActivity.this)
                        .setTitle("Exit App")
                        .setMessage("Do you want to exit ?")
                        .setIcon(R.mipmap.ic_launcher_round)

                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                Intent i = getBaseContext().getPackageManager()
                                        .getLaunchIntentForPackage(getBaseContext().getPackageName());
                                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(i);
                                finish();
                            }
                        })
                        .setNeutralButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int i) {
                                dialog.dismiss();
                                System.exit(1);
                                finish();
                            }
                        })
                        .show();
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        super.onKeyDown(keyCode, event);
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            ConnectionAlert();
            return false;
        }

        return false;
    }
}
