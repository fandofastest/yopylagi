package com.pinkump3.musiconline.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pinkump3.musiconline.R;
import com.pinkump3.musiconline.item.Genre;

import java.util.List;

public class GenreAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Context context;
    List<Object> listGenre;

    public GenreAdapter(Context context, List<Object> listGenre) {
        this.context = context;
        this.listGenre = listGenre;
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        TextView textGenre;

        public ViewHolder(View v) {
            super(v);
            textGenre = v.findViewById(R.id.textGenre);
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_genre,parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        ViewHolder genreHolder = (ViewHolder)holder;
        Genre genre = (Genre)listGenre.get(position);

        genreHolder.textGenre.setText(genre.getGenName());

    }

    @Override
    public int getItemCount() {
        return listGenre.size();
    }
}
