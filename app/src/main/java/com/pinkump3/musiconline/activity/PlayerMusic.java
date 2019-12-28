package com.pinkump3.musiconline.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.PowerManager;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import com.bumptech.glide.Glide;
import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AudienceNetworkAds;
import com.facebook.ads.InterstitialAdExtendedListener;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.pinkump3.musiconline.R;
import com.pinkump3.musiconline.Utilities;
import com.pinkump3.musiconline.item.Track;
import com.pinkump3.musiconline.item.TrackOff;
import com.facebook.ads.AdSize;
import com.google.android.gms.ads.AdRequest;
import com.squareup.picasso.Picasso;



import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import jp.wasabeef.picasso.transformations.BlurTransformation;

import static com.pinkump3.musiconline.activity.GenreActivity.listGenre;
import static com.pinkump3.musiconline.activity.MainActivity.DOWNLOAD_DIRECTORY;
import static com.pinkump3.musiconline.activity.MainActivity.offline_mode;
import static com.pinkump3.musiconline.activity.SearchActivity.listSearch;
import static com.pinkump3.musiconline.activity.SplashActivity.ads_main;
import static com.pinkump3.musiconline.activity.SplashActivity.id_inter_main;
import static com.pinkump3.musiconline.activity.SplashActivity.sc_key;
import static com.pinkump3.musiconline.fragment.FragmenTop.listOnlineTop;
import static com.pinkump3.musiconline.fragment.FragmentSaved.listOffline;

public class PlayerMusic extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener {

    InterstitialAd interstitialAd;
    com.facebook.ads.InterstitialAd interstitialFb;
    RelativeLayout banner;
    AppCompatImageView btn_download, btn_next, btn_prev;
    AppCompatImageView btn_repeat, btn_play;
    TextView song_duration, song_time, player_song_title;
    SeekBar seekBar;
    ImageView player_img, blurImgPlayer;
    AudioManager audioManager;
    ProgressBar progress_player;
    LinearLayout repeat_state;
    public ProgressDialog mProgressDialog;

    MediaPlayer mediaPlayer;
    Utilities util;
    Handler mHandler = new Handler();
    boolean repeat_status;

    private int seekForwardTime = 5000; // 5000 milliseconds
    private int seekBackwardTime = 5000; // 5000 milliseconds

    com.google.android.gms.ads.AdView adView;
    com.facebook.ads.AdView fbView;

    ProgressBar progress;
    public static int pos;
    String api, search_query, state, file_title, cat, itsgonnabeatitle,newtrackid;

    public static final String LIST_ONLINE = "listOnline";
    public static final String LIST_OFFLINE = "listOffline";
    public static int interShow = 0;

    String trackTitle, trackImg, trackArtist, origin,id;
    private static String trackUrl, dnl_link;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadInter();
        setContentView(R.layout.music_player);

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

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        api = getResources().getString(R.string.api);
        banner = findViewById(R.id.bannerContainer);
        btn_download = findViewById(R.id.download);
        btn_repeat = findViewById(R.id.repeat);
        btn_play = findViewById(R.id.play);
        btn_next = findViewById(R.id.next);
        btn_prev = findViewById(R.id.prev);
        player_song_title = findViewById(R.id.full_player_title);
        player_img = findViewById(R.id.full_player_img);
        song_duration = findViewById(R.id.time_dur);
        song_time = findViewById(R.id.time_rest);
        seekBar = findViewById(R.id.player_seekbar);
        progress_player = findViewById(R.id.progress_play);
        blurImgPlayer = findViewById(R.id.blurImglayer);
        repeat_state = findViewById(R.id.repeat_state);

        mediaPlayer = new MediaPlayer();
        util = new Utilities();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        if (!offline_mode) {
            if (ads_main.equals("fb")) {
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

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            cat = extras.getString("cat");
            pos = extras.getInt("pos");

            origin = extras.getString("origin");
        }

        Intent xxx = getIntent();
        if (cat.equals("online")) {

            Track track = (Track) xxx.getParcelableExtra(LIST_ONLINE);
            trackTitle = track.getTrackTitle();
            id = "https://fando.id/soundcloud.php?id="+track.getTrackid()+"&key=YUKXoArFcqrlQn9tfNHvvyfnDISj04zk";
//            Toast.makeText(getApplicationContext(),track.getTrackid(),Toast.LENGTH_LONG).show();
            trackImg = track.getTrackImg();
        } else if (cat.equals("offline")) {
            TrackOff trackoff = (TrackOff) xxx.getParcelableExtra(LIST_OFFLINE);
            trackTitle = trackoff.getSong_title();
            trackUrl = trackoff.getSong_url();
            trackImg = String.valueOf(getResources().getDrawable(R.drawable.bg_player_blur));
        }

        player_song_title.setText(trackTitle);
        player_song_title.setSelected(true);
        player_song_title.setSingleLine(true);
        player_song_title.setEllipsize(TextUtils.TruncateAt.MARQUEE);

        Glide.with(this).load(trackImg).error(R.drawable.imgesplsh).into(player_img);
        Picasso.with(this).load(trackImg).error(R.drawable.bg_player_blur).transform(new BlurTransformation(this, 10)).into(blurImgPlayer);

        mHandler.removeCallbacks(mUpdateTimeTask);
        if (mediaPlayer.isPlaying() || mediaPlayer.isLooping() || mediaPlayer != null) {

            mediaPlayer.stop();
            mediaPlayer.reset();
        }

        try {
            mediaPlayer.reset();
            if (cat.equals("offline")) {
                mediaPlayer.setDataSource(trackUrl);
                mediaPlayer.prepare();
            } else {
                mediaPlayer.setDataSource(id);
                mediaPlayer.prepareAsync();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {

                if (progress_player.getVisibility() == View.VISIBLE) {
                    progress_player.setVisibility(View.GONE);
                }

                if (btn_play.getVisibility() == View.INVISIBLE || btn_play.getVisibility() == View.GONE) {
                    btn_play.setVisibility(View.VISIBLE);
                }

                if (btn_repeat.getVisibility() == View.INVISIBLE || btn_repeat.getVisibility() == View.GONE) {
                    btn_repeat.setVisibility(View.VISIBLE);
                }

                if (btn_download.getVisibility() == View.INVISIBLE || btn_download.getVisibility() == View.GONE) {
                    btn_download.setVisibility(View.VISIBLE);
                }

                btn_repeat.setImageResource(R.drawable.ic_repeat);

                updateProgressBar();
                playpausebutton();
                if (SplashActivity.download_avail.equals("y")) {
                    if (cat.equals("online")) {
                        btn_download.setImageResource(R.drawable.ic_download);
                    } else {
                        btn_download.setImageResource(R.drawable.ic_stop);
                    }
                } else {
                    btn_download.setImageResource(R.drawable.ic_stop);
                }
            }
        });

        mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
                mediaPlayer.reset();
                player_song_title.setText("Sorry.. the server is in maintenance. Please try again later.");
                return true;
            }
        });

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                if (repeat_status) {
                    completingplay();
                } else {
                    nextSong(cat, origin, pos);
                }
            }
        });

        btn_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playpausebutton();
            }
        });

        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nextSong(cat, origin, pos);
            }
        });

        btn_prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                prefSong(cat, origin, pos);
            }
        });

        btn_repeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                repeatButton();
            }
        });

        btn_download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (SplashActivity.download_avail.equals("y")) {
                    if (cat.equals("online")) {

                        // Download Process
                        if (mediaPlayer.isPlaying() || mediaPlayer.isLooping() || mediaPlayer != null) {
                            mediaPlayer.pause();
                            btn_play.setImageResource(R.drawable.ic_play);
                        }
                        String cutTitle =trackTitle;
                        cutTitle = cutTitle.replace(" ", "-").replace(".", "-") + ".mp3";
                        DownloadManager downloadManager = (DownloadManager) getApplication().getSystemService(Context.DOWNLOAD_SERVICE);
                        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(id));
                        request.setTitle(trackTitle);
                        request.setDescription("Downloading");
                        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                        request.setDestinationInExternalPublicDir(DOWNLOAD_DIRECTORY, cutTitle);
                        request.allowScanningByMediaScanner();
                        long downloadID = downloadManager.enqueue(request);

                        Toast.makeText(getApplicationContext(), "Downloading Start", Toast.LENGTH_SHORT).show();



                    } else {
                        mediaPlayer.stop();
                        mediaPlayer.reset();
                        mHandler.removeCallbacks(mUpdateTimeTask);
                        onBackPressed();
                    }
                } else {
                    mediaPlayer.stop();
                    mediaPlayer.reset();
                    mHandler.removeCallbacks(mUpdateTimeTask);
                    onBackPressed();
                }
            }
        });

        seekBar.setOnSeekBarChangeListener(this);

    }


    public void nextSong(final String category, String origin, int position) {

        progress_player.setVisibility(View.VISIBLE);
        btn_play.setVisibility(View.INVISIBLE);
        btn_repeat.setVisibility(View.INVISIBLE);
        btn_download.setVisibility(View.INVISIBLE);

        pos = position + 1;

        if (origin.equals("top")) {
            if (pos >= listOnlineTop.size()) {
                Toast.makeText(this, "This is the last songs", Toast.LENGTH_SHORT).show();
                progress_player.setVisibility(View.GONE);
                btn_play.setVisibility(View.VISIBLE);
                btn_play.setImageResource(R.drawable.ic_pause);
                pos = position;
            } else {

                Track track = (Track) listOnlineTop.get(pos);
                trackTitle = track.getTrackTitle();
                trackUrl = track.getTrackUrl();
                trackImg = track.getTrackImg();
                startToPlay(category, trackTitle, trackUrl, trackImg);
            }
        } else if (origin.equals("search")) {
            if (pos >= listSearch.size()) {
                Toast.makeText(this, "This is the last songs", Toast.LENGTH_SHORT).show();
                progress_player.setVisibility(View.GONE);
                btn_play.setVisibility(View.VISIBLE);
                btn_play.setImageResource(R.drawable.ic_pause);
                pos = position;
            } else {

                Track track = (Track) listSearch.get(pos);
                trackTitle = track.getTrackTitle();
                id = "http://fando.id/soundcloud.php?id="+track.getTrackid()+"&key="+sc_key;
                trackImg = track.getTrackImg();
                startToPlay(category, trackTitle, id, trackImg);
            }
        } else if (origin.equals("saved")) {
            if (pos >= listOffline.size()) {
                Toast.makeText(this, "This is the last songs", Toast.LENGTH_SHORT).show();
                progress_player.setVisibility(View.GONE);
                btn_play.setVisibility(View.VISIBLE);
                btn_play.setImageResource(R.drawable.ic_pause);
                pos = position;
            } else {

                TrackOff track = (TrackOff) listOffline.get(pos);
                trackTitle = track.getSong_title();
                trackUrl = track.getSong_url();
                startToPlay(category, trackTitle, trackUrl, String.valueOf(getResources().getDrawable(R.drawable.bg_player_blur)));
            }
        } else if (origin.equals("genre")) {
            if (pos >= listGenre.size()) {
                Toast.makeText(this, "This is the last songs", Toast.LENGTH_SHORT).show();
                progress_player.setVisibility(View.GONE);
                btn_play.setVisibility(View.VISIBLE);
                btn_play.setImageResource(R.drawable.ic_pause);
                pos = position;
            } else {
                Track track = (Track) listGenre.get(pos);
                trackTitle = track.getTrackTitle();
                trackUrl = track.getTrackUrl();
                trackImg = track.getTrackImg();
                startToPlay(category, trackTitle, trackUrl, trackImg);
            }

        }
    }

    public void prefSong(String category, String origin, int position) {
        if (pos > 0) {
            pos = position - 1;
        } else {
            pos = 0;
        }

        int currentPosition = mediaPlayer.getCurrentPosition();
        if (currentPosition - seekBackwardTime >= 0) {
            mediaPlayer.seekTo(0);
        } else {
            progress_player.setVisibility(View.VISIBLE);
            btn_play.setVisibility(View.INVISIBLE);
            btn_repeat.setVisibility(View.INVISIBLE);
            btn_download.setVisibility(View.INVISIBLE);

            if (origin.equals("top")) {
                if (pos < 0) {
                    Toast.makeText(this, "This is the first songs", Toast.LENGTH_SHORT).show();
                    mHandler.removeCallbacks(mUpdateTimeTask);
                    if (mediaPlayer.isPlaying() || mediaPlayer.isLooping() || mediaPlayer != null) {

                        mediaPlayer.stop();
                        mediaPlayer.reset();
                    }
                } else {
                    Track track = (Track) listOnlineTop.get(pos);
                    trackTitle = track.getTrackTitle();
                    trackUrl = track.getTrackUrl();
                    trackImg = track.getTrackImg();
                    startToPlay(category, trackTitle, trackUrl, trackImg);
                }
            } else if (origin.equals("search")) {
                if (pos < 0) {
                    Toast.makeText(this, "This is the first songs", Toast.LENGTH_SHORT).show();
                    mHandler.removeCallbacks(mUpdateTimeTask);
                    if (mediaPlayer.isPlaying() || mediaPlayer.isLooping() || mediaPlayer != null) {

                        mediaPlayer.stop();
                        mediaPlayer.reset();
                    }
                } else {
                    Track track = (Track) listSearch.get(pos);
                    trackTitle = track.getTrackTitle();
                    id = "http://fando.id/soundcloud.php?id="+track.getTrackid()+"&key="+sc_key;
                    trackImg = track.getTrackImg();
                    startToPlay(category, trackTitle, id, trackImg);
                }
            } else if (origin.equals("saved")) {
                if (pos < 0) {
                    Toast.makeText(this, "This is the first songs", Toast.LENGTH_SHORT).show();
                    mHandler.removeCallbacks(mUpdateTimeTask);
                    if (mediaPlayer.isPlaying() || mediaPlayer.isLooping() || mediaPlayer != null) {

                        mediaPlayer.stop();
                        mediaPlayer.reset();
                    }
                } else {
                    TrackOff track = (TrackOff) listOffline.get(pos);
                    trackTitle = track.getSong_title();
                    trackUrl = track.getSong_url();
                    startToPlay(category, trackTitle, trackUrl, String.valueOf(getResources().getDrawable(R.drawable.bg_player_blur)));
                }
            } else if (origin.equals("genre")) {
                if (pos < 0) {
                    Toast.makeText(this, "This is the first songs", Toast.LENGTH_SHORT).show();
                    mHandler.removeCallbacks(mUpdateTimeTask);
                    if (mediaPlayer.isPlaying() || mediaPlayer.isLooping() || mediaPlayer != null) {

                        mediaPlayer.stop();
                        mediaPlayer.reset();
                    }
                } else {
                    Track track = (Track) listGenre.get(pos);
                    trackTitle = track.getTrackTitle();
                    trackUrl = track.getTrackUrl();
                    trackImg = track.getTrackImg();
                    startToPlay(category, trackTitle, trackUrl, trackImg);
                }

            }

        }

    }

    public void startToPlay(String category, String title, String strurl, String img) {

        player_song_title.setText(title);
        Glide.with(this).load(img).error(R.drawable.imgesplsh).into(player_img);
        Picasso.with(this).load(img).error(R.drawable.bg_player_blur).transform(new BlurTransformation(this, 10)).into(blurImgPlayer);

        mHandler.removeCallbacks(mUpdateTimeTask);
        if (mediaPlayer.isPlaying() || mediaPlayer.isLooping() || mediaPlayer != null) {

            mediaPlayer.stop();
            mediaPlayer.reset();
        }

        try {
            mediaPlayer.reset();
            if (category.equals("offline")) {
                mediaPlayer.setDataSource(strurl);
                mediaPlayer.prepare();
            } else {
                mediaPlayer.setDataSource(strurl);
                mediaPlayer.prepareAsync();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void repeatButton() {
        if (repeat_status) {
            btn_repeat.setImageResource(R.drawable.ic_repeat);
            repeat_status = false;
            if (repeat_state.getVisibility() == View.VISIBLE) {
                repeat_state.setVisibility(View.GONE);
            }
//            Toast.makeText(ListMusicActivity.this, "Repeat is Off", Toast.LENGTH_SHORT).show();
        } else {
            btn_repeat.setImageResource(R.drawable.ic_repeat_active);
            repeat_status = true;
            if (repeat_state.getVisibility() != View.VISIBLE) {
                repeat_state.setVisibility(View.VISIBLE);
            }
//            Toast.makeText(ListMusicActivity.this, "Repeat is On", Toast.LENGTH_SHORT).show();
        }
    }

    public void completingplay() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.reset();
            mediaPlayer.release();
        } else {
            playpausebutton();
            updateProgressBar();
        }
    }

    public void playpausebutton() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            btn_play.setImageResource(R.drawable.ic_play);
        } else {
            mediaPlayer.start();
            btn_play.setImageResource(R.drawable.ic_pause);
        }
    }

    public void updateProgressBar() {
        mHandler.postDelayed(mUpdateTimeTask, 100);
    }

    public Runnable mUpdateTimeTask = new Runnable() {
        public void run() {
            long totalDuration = mediaPlayer.getDuration();
            long currentDuration = mediaPlayer.getCurrentPosition();

            song_duration.setText("" + util.milliSecondsToTimer(totalDuration));
            song_time.setText("" + util.milliSecondsToTimer(currentDuration));

            int progress = (int) (util.getProgressPercentage(currentDuration, totalDuration));
            seekBar.setProgress(progress);

            mHandler.postDelayed(this, 100);
        }
    };

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        mHandler.removeCallbacks(mUpdateTimeTask);
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        mHandler.removeCallbacks(mUpdateTimeTask);
        int totalDuration = mediaPlayer.getDuration();
        int currentPosition = util.progressToTimer(seekBar.getProgress(), totalDuration);
        mediaPlayer.seekTo(currentPosition);
        updateProgressBar();
    }

    @SuppressLint("StaticFieldLeak")
    private class DownloadProcess extends AsyncTask<String, Integer, String> {

        private Context context;
        private PowerManager.WakeLock mWakeLock;
        private String folder;

        public DownloadProcess(Context context) {
            this.context = context;
        }

        @Override
        protected String doInBackground(String... sUrl) {

            InputStream input = null;
            OutputStream output = null;
            HttpURLConnection connection = null;
            try {
                URL url = new URL(sUrl[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                // expect HTTP 200 OK, so we don't mistakenly save error report
                // instead of the file
                if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    return "Server returned HTTP " + connection.getResponseCode()
                            + " " + connection.getResponseMessage();

                }

                // this will be useful to display download percentage
                // might be -1: server did not report the length
                int fileLength = connection.getContentLength();

                //String filename = Jud +".mp3";
                folder = Environment.getExternalStorageDirectory() + File.separator + "Download/";

                File file = new File(folder);
                if (!file.exists()) {
                    file.mkdirs();
                }

                String filesave = trackTitle.replaceAll("\"", "_").replaceAll("\'", "_");
                String filename = filesave + ".mp3";
                File saveFile = new File(file, filename);

                input = connection.getInputStream();
                output = new FileOutputStream(saveFile);

//                byte data[] = new byte[4096];
                byte data[] = new byte[8192];
                long total = 0;
                int count;
                while ((count = input.read(data)) != -1) {
                    // allow canceling with back button
                    if (isCancelled()) {
                        input.close();
                        return null;
                    }
                    total += count;
                    // publishing the progress....
                    if (fileLength > 0) // only if total length is known
                        publishProgress((int) (total * 100 / fileLength));
                    output.write(data, 0, count);
                }
            } catch (Exception e) {
                return e.toString();
            } finally {
                try {
                    if (output != null)
                        output.close();
                    if (input != null)
                        input.close();
                } catch (IOException ignored) {
                }

                if (connection != null)
                    connection.disconnect();
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // take CPU lock to prevent CPU from going off if the user
            // presses the power button during download
            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                    getClass().getName());
            mWakeLock.acquire();
            mProgressDialog.show();
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            mProgressDialog.setMessage("Add to Playlist...");
            super.onProgressUpdate(progress);
            // if we get here, length is known, now set indeterminate to false
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.setCanceledOnTouchOutside(false);
            mProgressDialog.setMax(100);
            mProgressDialog.setProgress(progress[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            mWakeLock.release();
            mProgressDialog.dismiss();
            if (result != null) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (!Settings.System.canWrite(PlayerMusic.this)) {
                        requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.READ_EXTERNAL_STORAGE}, SplashActivity.PERMISSIONCODE);
                        Toast.makeText(context, "Please allow app to access the storage file", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, "Sorry. You can't add to playlist this file due to Copyright policy.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(context, "Sorry. You can't add to playlist this file due to Copyright policy.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(context, "Succsesfully added to playlist", Toast.LENGTH_SHORT).show();
                showInterstitial();
            }
        }

    }

    public void loadInter(){
        if (SplashActivity.ads_main.equals("fb")) {

            interstitialFb = new com.facebook.ads.InterstitialAd(this, SplashActivity.id_inter_main);
            interstitialFb.setAdListener(new InterstitialAdExtendedListener() {

                @Override
                public void onRewardedAdCompleted() {

                }

                @Override
                public void onRewardedAdServerSucceeded() {

                }

                @Override
                public void onRewardedAdServerFailed() {

                }

                @Override
                public void onError(Ad ad, AdError adError) {

                }

                @Override
                public void onAdLoaded(Ad ad) {

                }

                @Override
                public void onAdClicked(Ad ad) {

                }

                @Override
                public void onLoggingImpression(Ad ad) {

                }

                @Override
                public void onInterstitialDisplayed(Ad ad) {

                }

                @Override
                public void onInterstitialDismissed(Ad ad) {

                }

                @Override
                public void onInterstitialActivityDestroyed() {

                }
            });
            interstitialFb.loadAd();
        } else {

            interstitialAd = new InterstitialAd(this);
            interstitialAd.setAdUnitId(SplashActivity.id_inter_main);
            interstitialAd.loadAd(new AdRequest.Builder().build());
        }
    }

    public void showInterstitial() {
        if (SplashActivity.ads_main.equals("fb")) {
            if (interstitialFb.isAdLoaded()) {
                interstitialFb.show();
            }
        } else {
            if(interstitialAd.isLoaded())
                interstitialAd.show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacks(mUpdateTimeTask);
        if (progress_player != null) {
            progress_player = null;
        }
        if (mediaPlayer != null) {
            if (mediaPlayer.isLooping() || mediaPlayer.isPlaying()) {

                mediaPlayer.stop();
                mediaPlayer.reset();
                mediaPlayer.release();
                mediaPlayer = null;
            }
        }
        if (ads_main.equals("fb")) {
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
}
