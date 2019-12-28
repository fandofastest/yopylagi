package com.pinkump3.musiconline.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
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
import android.widget.SearchView;

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

public class SearchActivity extends AppCompatActivity {

    SearchView searchView;
    RecyclerView recView;
    RelativeLayout banner;
    ProgressDialog mProgressDialog;
    public static List<Object> listSearch;
    TrackAdapter mAdapter;
    String search_query, apiv1,songTitle, songUrl, songImg, songArtist, cat;
     String trackid;
    com.google.android.gms.ads.AdView adView;
    com.facebook.ads.AdView fbView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        searchView = findViewById(R.id.searchView);
        recView = findViewById(R.id.recViewSearch);
        banner = findViewById(R.id.banner);
        apiv1 = getResources().getString(R.string.api);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            search_query = extras.getString("query");
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

        final SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setMaxWidth(Integer.MAX_VALUE);

        // listening to search query text change
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // filter recycler view when query submitted
                search_query = query;
                new LoadData().execute();
                setTitle("Search : "+search_query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                // filter recycler view when text is changed

                return false;
            }
        });

        searchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchView.setIconified(false);
            }
        });

        listSearch = new ArrayList<>();
        mAdapter = new TrackAdapter(this,listSearch);

        if (search_query !=null) {
            new LoadData().execute();
            setTitle("Search : "+search_query);
        }


        LinearLayoutManager layoutMan = new LinearLayoutManager(this,RecyclerView.VERTICAL,false);
        recView.setLayoutManager(layoutMan);
        recView.setItemAnimator(new DefaultItemAnimator());
        recView.setHasFixedSize(true);

        recView.addOnItemTouchListener(new RecyclerTouchListener(this, recView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Track track = (Track)listSearch.get(position);


//                Toast.makeText(SearchActivity.this, "Song with Title :"+songTitle+" will play", Toast.LENGTH_SHORT).show();
                Intent i = new Intent(SearchActivity.this, PlayerMusic.class);
                i.putExtra(PlayerMusic.LIST_ONLINE, track);
                i.putExtra("cat", "online");
                i.putExtra("pos", position);
                i.putExtra("origin", "search");
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
            mProgressDialog = new ProgressDialog(SearchActivity.this);
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.setMessage("Loading music list ...");
            mProgressDialog.show();
            listSearch.clear();
        }

        @Override
        protected Void doInBackground(Void... params) {
            String data = "";
            String title, song_url, artwork_url, song_user,song_id;
            int song_dur, song_like;
            try {
//                URL url = new URL(apiv1 + "/tracks.json?q="+search_query.replaceAll(" ", "+")+"&client_id="+SplashActivity.sc_key+"&limit=100");
                URL url = new URL("https://api-v2.soundcloud.com/search/tracks?user_id=698189-13257-213778-325874&limit=10&offset=0&linked_partitioning=1&app_version=d8c55ad&q="+search_query.replaceAll(" ", "+")+"&client_id="+SplashActivity.sc_key+"&limit=100");

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

                    JSONArray getParam = json.getJSONArray("collection");
                    for (int i = 0; i < getParam.length(); i++) {

                        title = getParam.getJSONObject(i).getString("title");
                        artwork_url = getParam.getJSONObject(i).getString("artwork_url");
                        song_url = getParam.getJSONObject(i).getString("uri");
                        song_id=getParam.getJSONObject(i).getString("id");

                        song_dur = getParam.getJSONObject(i).getInt("full_duration");
                        song_like = getParam.getJSONObject(i).getInt("likes_count");

                        JSONObject getValueUser = getParam.getJSONObject(i).getJSONObject("user");
                        song_user = getValueUser.getString("username");

                        Track track = new Track (song_id,artwork_url, title, song_user, song_url, song_dur, song_like);
                        listSearch.add(track);

                        Log.d("idtrack", song_id);
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
            Intent i = new Intent(SearchActivity.this, PolicyActivity.class);
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

    public void onBackPressed() {
        Intent i = new Intent(SearchActivity.this, MainActivity.class);
        startActivity(i);
        finish();
    }
}
