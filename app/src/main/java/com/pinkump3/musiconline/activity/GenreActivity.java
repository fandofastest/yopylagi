package com.pinkump3.musiconline.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Rect;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;

import com.pinkump3.musiconline.R;
import com.pinkump3.musiconline.RecyclerTouchListener;
import com.pinkump3.musiconline.adapter.TrackAdapter;
import com.pinkump3.musiconline.item.Track;
import com.facebook.ads.AdSize;
import com.google.android.gms.ads.AdRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class GenreActivity extends AppCompatActivity {

    RelativeLayout banner;
    RecyclerView recView;
    public static List<Object> listGenre;
    TrackAdapter mAdapter;
    ProgressDialog mProgressDialog;
    String apiv2, musicgenre, genrename, songTitle, songUrl, songImg, songArtist, cat;

    com.google.android.gms.ads.AdView adView;
    com.facebook.ads.AdView fbView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_genre);

        banner = findViewById(R.id.banner);
        recView = findViewById(R.id.recViewGenre);
        apiv2 = getResources().getString(R.string.apiv2);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            musicgenre = extras.getString("genre");
            genrename = extras.getString("genname");
        }

        if (SplashActivity.ads_main.equals("fb")) {
            fbView = new com.facebook.ads.AdView(this,SplashActivity.id_banner_main, AdSize.BANNER_HEIGHT_50);
            banner.addView(fbView);
            fbView.loadAd();
        } else {
            adView = new com.google.android.gms.ads.AdView(this);
            adView.setAdUnitId(SplashActivity.id_banner_main);
            adView.setAdSize(com.google.android.gms.ads.AdSize.SMART_BANNER);
            AdRequest adreq = new AdRequest.Builder().build();
            banner.addView(adView);
            adView.loadAd(adreq);
        }

        setTitle("Top Song by Genre");
        getSupportActionBar().setSubtitle("Genre : "+genrename);
        listGenre= new ArrayList<>();
        mAdapter = new TrackAdapter(this,listGenre);
        new LoadData().execute();

        LinearLayoutManager layoutMan = new LinearLayoutManager(this,RecyclerView.VERTICAL,false);
        recView.setLayoutManager(layoutMan);
        recView.setItemAnimator(new DefaultItemAnimator());
        recView.setHasFixedSize(true);

        recView.addOnItemTouchListener(new RecyclerTouchListener(this, recView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Track track;
                track = (Track)listGenre.get(position);

                Intent i = new Intent(GenreActivity.this, PlayerMusic.class);
                i.putExtra(PlayerMusic.LIST_ONLINE, track);
                i.putExtra("cat", "online");
                i.putExtra("pos", position);
                i.putExtra("origin", "genre");
                startActivity(i);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        recView.setAdapter(mAdapter);

    }

    @SuppressLint("StaticFieldLeak")
    public class LoadData extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressDialog = new ProgressDialog(GenreActivity.this);
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.setMessage("Loading music list ...");
            mProgressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            String data = "";
            String title, song_url, artwork_url, song_user;
            int song_dur, song_like;
            try {
                URL url = new URL(apiv2 + "/charts?kind=top&genre="+musicgenre+"&client_id=" + SplashActivity.sc_key + "&offset=50&limit=50");
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
                    JSONArray jsondata = json.getJSONArray("collection");

                    for (int i = 0; i < jsondata.length(); i++) {

                        JSONObject content = jsondata.getJSONObject(i).getJSONObject("track");

                        title = content.getString("title");
                        artwork_url = content.getString("artwork_url");
                        song_url = content.getString("uri");
                        JSONObject getuser = content.getJSONObject("user");
                        song_user = getuser.getString("username");
                        song_dur = content.getInt("duration");
                        song_like = content.getInt("likes_count");

//                        Track track = new Track(artwork_url, title, song_user, song_url, song_dur, song_like);
//                        listGenre.add(track);

                        Log.d("Video Title", title);
//                        Log.d("Thumb value", tthhhummbbb);
//                        Log.d("Video Url", vvviiiidddurl);
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
            mAdapter.notifyDataSetChanged();
            mProgressDialog.dismiss();
        }

    }

    public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

        private int spanCount;
        private int spacing;
        private boolean includeEdge;

        public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view); // item position
            int column = position % spanCount; // item column

            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount; // spacing - column * ((1f / spanCount) * spacing)
                outRect.right = (column + 1) * spacing / spanCount; // (column + 1) * ((1f / spanCount) * spacing)

                if (position < spanCount) { // top edge
                    outRect.top = spacing;
                }
                outRect.bottom = spacing; // item bottom
            } else {
                outRect.left = column * spacing / spanCount; // column * ((1f / spanCount) * spacing)
                outRect.right = spacing - (column + 1) * spacing / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
                if (position >= spanCount) {
                    outRect.top = spacing; // item top
                }
            }
        }
    }

    /**
     * Converting dp to pixel
     */
    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
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
            Intent i = new Intent(GenreActivity.this, PolicyActivity.class);
            startActivity(i);
        } else if (id == R.id.menu_share){
            ShareApp();
        } else if (id == R.id.menu_rate){
            RateApps();
        }

        return super.onOptionsItemSelected(item);
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
