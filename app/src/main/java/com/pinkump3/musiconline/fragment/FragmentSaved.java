package com.pinkump3.musiconline.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.pinkump3.musiconline.R;
import com.pinkump3.musiconline.RecyclerTouchListener;
import com.pinkump3.musiconline.activity.PlayerMusic;
import com.pinkump3.musiconline.activity.SplashActivity;
import com.pinkump3.musiconline.adapter.TrackAdapterLib;
import com.pinkump3.musiconline.item.TrackOff;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FragmentSaved extends Fragment {
    RecyclerView recView;
    TrackAdapterLib mAdapter;
    public static List<Object> listOffline;
    String songTitle, songUrl;
    RelativeLayout nosong;

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

        LinearLayoutManager layoutMan = new LinearLayoutManager(getContext(),RecyclerView.VERTICAL,false);
        recView.setLayoutManager(layoutMan);
        recView.setItemAnimator(new DefaultItemAnimator());
        recView.setHasFixedSize(true);

//        nosong.setVisibility(View.GONE);
        listOffline = new ArrayList<>();
        mAdapter = new TrackAdapterLib(getActivity(),listOffline);

        new LoadOfflineSong().execute();
        recView.setAdapter(mAdapter);

        recView.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), recView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                TrackOff track = (TrackOff) listOffline.get(position);

                Intent i = new Intent(getActivity(), PlayerMusic.class);
                i.putExtra(PlayerMusic.LIST_OFFLINE, track);
                i.putExtra("cat", "offline");
                i.putExtra("pos", position);
                i.putExtra("origin", "saved");
                startActivity(i);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        return rootView;
    }

    @SuppressLint("StaticFieldLeak")
    public class LoadOfflineSong extends AsyncTask<Void, Void, Void> {
        TrackOff trackOffline;
        String card = SplashActivity.folder;
        ArrayList<String> files = GetFiles(card);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            listOffline.clear();

            if (files != null) {
                for (int x = 0; x < files.size(); x++) {
                    String judullagu = files.get(x);
                    String url_song = card + "/" + files.get(x);
                    trackOffline = new TrackOff(judullagu, url_song, "Saved Music");
                    listOffline.add(trackOffline);
                    Log.d("Title Songs", url_song);
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (listOffline.size() < 1) {
                listOffline.clear();
//                nosong.setVisibility(View.VISIBLE);
//                Toast.makeText(getActivity(), "Sorry., there is no song downloaded.", Toast.LENGTH_LONG).show();
            } else {
//                nosong.setVisibility(View.GONE);
                mAdapter.notifyDataSetChanged();
            }
        }

    }

    public ArrayList<String> GetFiles(String directorypath) {
        ArrayList<String> Myfiles = new ArrayList<String>();
        File f = new File(directorypath);

        File[] files = f.listFiles();
        for (int i = 0; i < files.length; i++) {

            String filename = files[i].getName();
            String ext = filename.substring(filename.lastIndexOf('.') + 1, filename.length());
            if (ext.equals("MP3") || ext.equals("mp3")) {
                Myfiles.add(files[i].getName());
            }
        }

        return Myfiles;
    }
}
