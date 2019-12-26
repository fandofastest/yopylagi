package com.pinkump3.musiconline.fragment;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.pinkump3.musiconline.R;
import com.pinkump3.musiconline.RecyclerTouchListener;
import com.pinkump3.musiconline.activity.PlayerMusic;
import com.pinkump3.musiconline.activity.SplashActivity;
import com.pinkump3.musiconline.adapter.TrackAdapter;
import com.pinkump3.musiconline.item.Track;

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

public class FragmenTop extends Fragment {

    RecyclerView recView;
    public static List<Object> listOnlineTop;
    TrackAdapter mAdapter;
    ProgressDialog mProgressDialog;
    String apiv2, songTitle, songUrl, songImg, songArtist, cat;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        recView = rootView.findViewById(R.id.recView);
        apiv2 = getResources().getString(R.string.apiv2);
        listOnlineTop = new ArrayList<>();
        mAdapter = new TrackAdapter(getActivity(), listOnlineTop);
        new LoadData().execute();

        LinearLayoutManager layoutMan = new LinearLayoutManager(getContext(),RecyclerView.VERTICAL,false);
        recView.setLayoutManager(layoutMan);
        recView.setItemAnimator(new DefaultItemAnimator());
        recView.setHasFixedSize(true);

        recView.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), recView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Track track = (Track) listOnlineTop.get(position);

                Intent i = new Intent(getActivity(), PlayerMusic.class);
                i.putExtra(PlayerMusic.LIST_ONLINE, track);
                i.putExtra("cat", "online");
                i.putExtra("pos", position);
                i.putExtra("origin", "top");

                startActivity(i);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        recView.setAdapter(mAdapter);
        return rootView;
    }

    @SuppressLint("StaticFieldLeak")
    public class LoadData extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressDialog = new ProgressDialog(getActivity());
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.setCancelable(false);
            mProgressDialog.setMessage("Loading music list ...");
            mProgressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            String data = "";
            String title, song_url, artwork_url, song_user;
            int song_dur, song_like;
            try {
                URL url = new URL(apiv2 + "/charts?kind=top&genre=soundcloud:genres:all-music&client_id=" + SplashActivity.sc_key + "&offset=50&limit=50");
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
                        song_dur  = content.getInt("duration");
                        song_like = content.getInt("likes_count");

//                        Track track = new Track(artwork_url,title, song_user, song_url, song_dur, song_like);
//                        listOnlineTop.add(track);

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
}
