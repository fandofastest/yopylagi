package com.pinkump3.musiconline.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pinkump3.musiconline.R;
import com.pinkump3.musiconline.item.TrackOff;

import java.util.List;

public class TrackAdapterLib extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Context context;
    List<Object> listTrackOff;

    public TrackAdapterLib(Context context, List<Object> listTrackOff) {
        this.context = context;
        this.listTrackOff = listTrackOff;
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView song_title, song_artist;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            song_title = itemView.findViewById(R.id.track_title);
            song_artist = itemView.findViewById(R.id.track_artist);
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_track_offline,parent,false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ViewHolder trackoffholder = (ViewHolder)holder;
        TrackOff trackOff = (TrackOff)listTrackOff.get(position);

        trackoffholder.song_title.setText(trackOff.getSong_title());
        trackoffholder.song_artist.setText(trackOff.getCategory());


    }

    @Override
    public int getItemCount() {
        return listTrackOff.size();
    }
}
