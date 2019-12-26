package com.pinkump3.musiconline.fragment;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.pinkump3.musiconline.R;
import com.pinkump3.musiconline.RecyclerTouchListener;
import com.pinkump3.musiconline.activity.GenreActivity;
import com.pinkump3.musiconline.adapter.GenreAdapter;
import com.pinkump3.musiconline.item.Genre;

import java.util.ArrayList;
import java.util.List;

public class FragmentGenre extends Fragment {

    String[] listgenrevalue = {"soundcloud:genres:all-music",
            "soundcloud:genres:alternativerock",
            "soundcloud:genres:ambient",
            "soundcloud:genres:classical",
            "soundcloud:genres:country",
            "soundcloud:genres:danceedm",
            "soundcloud:genres:dancehall",
            "soundcloud:genres:deephouse",
            "soundcloud:genres:disco",
            "soundcloud:genres:drumbass",
            "soundcloud:genres:dubstep",
            "soundcloud:genres:electronic",
            "soundcloud:genres:folksingersongwriter",
            "soundcloud:genres:hiphoprap",
            "soundcloud:genres:house",
            "soundcloud:genres:indie",
            "soundcloud:genres:jazzblues",
            "soundcloud:genres:latin",
            "soundcloud:genres:metal",
            "soundcloud:genres:piano",
            "soundcloud:genres:pop",
            "soundcloud:genres:rbsoul",
            "soundcloud:genres:reggae",
            "soundcloud:genres:reggaeton",
            "soundcloud:genres:rock",
            "soundcloud:genres:soundtrack",
            "soundcloud:genres:techno",
            "soundcloud:genres:trance",
            "soundcloud:genres:trap",
            "soundcloud:genres:triphop",
            "soundcloud:genres:world"};
    String[] genrename;

    RecyclerView recView;
    List<Object> listGenre;
    GenreAdapter mAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        genrename = getResources().getStringArray(R.array.genres);
        recView = rootView.findViewById(R.id.recView);

        listGenre = new ArrayList<>();
        mAdapter = new GenreAdapter(getActivity(), listGenre);

        for (int i = 0; i < listgenrevalue.length ; i++) {
            String genrevalue = listgenrevalue[i];
            String genname = genrename[i];
            Genre genre = new Genre(genrevalue,genname);
            listGenre.add(genre);
        }

        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 3);
        recView.setLayoutManager(layoutManager);
        recView.addItemDecoration(new GridSpacingItemDecoration(3, dpToPx(5), true));
        recView.setItemAnimator(new DefaultItemAnimator());
        recView.setHasFixedSize(true);

        recView.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), recView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Genre genre = (Genre) listGenre.get(position);
                Intent i = new Intent(getActivity(), GenreActivity.class);
                i.putExtra("genre", genre.getGenValue());
                i.putExtra("genname", genre.getGenName());
                startActivity(i);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        recView.setAdapter(mAdapter);

        return rootView;

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
