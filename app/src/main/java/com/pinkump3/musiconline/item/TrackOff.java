package com.pinkump3.musiconline.item;

import android.os.Parcel;
import android.os.Parcelable;

public class TrackOff implements Parcelable {
    private String song_title, song_url, category;

    public TrackOff(String song_title, String song_url, String category) {
        this.song_title = song_title;
        this.song_url = song_url;
        this.category = category;
    }

    public String getSong_title() {
        return song_title;
    }

    public void setSong_title(String song_title) {
        this.song_title = song_title;
    }

    public String getSong_url() {
        return song_url;
    }

    public void setSong_url(String song_url) {
        this.song_url = song_url;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.song_title);
        dest.writeString(this.song_url);
        dest.writeString(this.category);
    }

    protected TrackOff(Parcel in) {
        this.song_title = in.readString();
        this.song_url = in.readString();
        this.category = in.readString();
    }

    public static final Parcelable.Creator<TrackOff> CREATOR = new Parcelable.Creator<TrackOff>() {
        @Override
        public TrackOff createFromParcel(Parcel source) {
            return new TrackOff(source);
        }

        @Override
        public TrackOff[] newArray(int size) {
            return new TrackOff[size];
        }
    };
}
