package com.pinkump3.musiconline.activity;

import android.app.SearchManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.pinkump3.musiconline.R;
import com.pinkump3.musiconline.adapter.FragmentAdapter;
import com.pinkump3.musiconline.fragment.FragmenTop;
import com.pinkump3.musiconline.fragment.FragmentGenre;
import com.pinkump3.musiconline.fragment.FragmentSaved;
import com.facebook.ads.AdSize;
import com.google.android.gms.ads.AdRequest;

import android.os.Handler;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.GravityCompat;
import androidx.appcompat.app.ActionBarDrawerToggle;

import android.view.MenuItem;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    RelativeLayout banner;
    SearchView searchview;
    public static String  DOWNLOAD_DIRECTORY="/Download";

    TabLayout tabLayout;
    ViewPager viewPager;

    com.google.android.gms.ads.AdView adView;
    com.facebook.ads.AdView fbView;

    String search_qry, mode;
    public static boolean offline_mode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);

        banner = findViewById(R.id.bannerContainer);
        searchview = findViewById(R.id.search_Home_act);
        tabLayout = findViewById(R.id.tab);
        viewPager = findViewById(R.id.viewPager);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            mode = extras.getString("mode");
        }

        if (mode != null && mode.equals("offline")) {
            offline_mode = true;
        } else {
            offline_mode = false;
        }

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

        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchview.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchview.setMaxWidth(Integer.MAX_VALUE);

        searchview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchview.setIconified(false);
            }
        });

        searchview.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
//                Toast.makeText(MainActivity.this, "Search for : "+query, Toast.LENGTH_SHORT).show();
                if (!offline_mode) {
                    Intent search = new Intent(MainActivity.this, SearchActivity.class);
                    search.putExtra("query", query);
                    search.putExtra("cat", "search");
                    startActivity(search);
                    finish();
                } else {
                    Toast.makeText(MainActivity.this, "Sorry.. this feature is just for Online Mode. Please connect to internet and restart this apps.", Toast.LENGTH_LONG).show();
                }
                return true;

            }

            @Override
            public boolean onQueryTextChange(String newText) {

                return false;
            }
        });

        if (!offline_mode) {
            if (SplashActivity.ads_home_avail.equals("y")) {
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Ads_Home();
                    }
                }, 2000);
            }
        }

        setupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);

    }

    private void setupViewPager(ViewPager viewPager) {
        FragmentAdapter adapter = new FragmentAdapter(getSupportFragmentManager());
        if (!offline_mode) {
            adapter.addFragment(new FragmentSaved(), "PLAYLIST");
        } else {
            adapter.addFragment(new FragmentSaved(), "PLAYLIST");
        }
        viewPager.setAdapter(adapter);
    }

    private void Ads_Home() {

        AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
        View view = LayoutInflater.from(this).inflate(R.layout.ads_content, null);
        TextView ads_title = (TextView) view.findViewById(R.id.ads_title);
        TextView ads_desc = (TextView) view.findViewById(R.id.ads_desc);
        ImageView ads_icon = (ImageView) view.findViewById(R.id.ads_icon);
        ImageView ads_img = (ImageView) view.findViewById(R.id.ads_img_large);
        Button ads_exec = (Button)view.findViewById(R.id.ads_btn);

        ads_title.setText(SplashActivity.title_ads);
        ads_desc.setText(SplashActivity.desc_ads);
        Glide.with(this).load(SplashActivity.icon_ads).error(R.drawable.imgesplsh).into(ads_icon);
        Glide.with(this).load(SplashActivity.img_large_ads).into(ads_img);
        ads_exec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = Uri.parse("market://details?id=" + SplashActivity.package_ads);
                Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
                try {
                    startActivity(goToMarket);
                } catch (ActivityNotFoundException e) {
                    startActivity(new Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("http://play.google.com/store/apps/details?id="
                                    + SplashActivity.package_ads)));
                }
            }
        });


        alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                dialog.dismiss();
            }
        });

        alertDialog.setView(view);
        alertDialog.show();
    }



    @Override
    public void onBackPressed() {

        Intent i = new Intent(MainActivity.this, SplashActivity.class);
        startActivity(i);
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

    public void Share() {
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

    public void Rate() {
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

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        // Handle navigation view item clicks here.
        int id = menuItem.getItemId();

        if (id == R.id.nav_policy) {
            Intent i = new Intent(MainActivity.this, PolicyActivity.class);
            startActivity(i);
        } else if (id == R.id.nav_rate) {
            Rate();
        } else if (id == R.id.nav_share) {
            Share();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        return true;
    }

}
