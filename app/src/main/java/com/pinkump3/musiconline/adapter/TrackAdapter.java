package com.pinkump3.musiconline.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.pinkump3.musiconline.R;
import com.pinkump3.musiconline.item.Track;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class TrackAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Context context;
    List<Object> listTrack;

    public TrackAdapter(Context context, List<Object> listTrack) {
        this.context = context;
        this.listTrack = listTrack;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView track_img;
        TextView track_title, track_artist, track_dur, track_like;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

//            track_img = itemView.findViewById(R.id.track_img);
            track_title = itemView.findViewById(R.id.track_title);
            track_artist = itemView.findViewById(R.id.track_artist);
            track_dur = itemView.findViewById(R.id.track_duration);
            track_like = itemView.findViewById(R.id.track_like);
        }
    }

    public static String formatValue(Number number) {
        char[] suffix = {' ', 'k', 'M', 'B', 'T', 'P', 'E'};
        long numValue = number.longValue();
        int value = (int) Math.floor(Math.log10(numValue));
        int base = value / 3;
        if (value >= 3 && base < suffix.length) {
            return new DecimalFormat("#0.#").format(numValue / Math.pow(10, base * 3)) + suffix[base];
        } else {
            return new DecimalFormat("#,##0").format(numValue);
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_track, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ViewHolder trackholder = (ViewHolder)holder;
        Track track = (Track)listTrack.get(position);

//        Glide.with(context).load(track.getTrackImg()).error(R.drawable.icon_msc).into(trackholder.track_img);
        trackholder.track_title.setText(track.getTrackTitle());
        trackholder.track_artist.setText(track.getTrackArtist());
        long time = track.getTrackDur();
        @SuppressLint("SimpleDateFormat") String data = new SimpleDateFormat("mm:ss").format(new Date(time));
        trackholder.track_dur.setText(data);
        trackholder.track_like.setText(formatValue(track.getTrackLike()));

    }

    @Override
    public int getItemCount() {
        return listTrack.size();
    }
}
