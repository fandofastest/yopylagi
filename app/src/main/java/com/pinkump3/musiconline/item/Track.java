package com.pinkump3.musiconline.item;

import android.os.Parcel;
import android.os.Parcelable;

public class Track implements Parcelable {
    private String trackImg, trackTitle, trackArtist, trackUrl,trackid;
    private int trackDur, trackLike;

    public Track(String trackid,String trackImg, String trackTitle, String trackArtist, String trackUrl, int trackDur, int trackLike) {
        this.trackImg = trackImg;
        this.trackTitle = trackTitle;
        this.trackArtist = trackArtist;
        this.trackUrl = trackUrl;
        this.trackDur = trackDur;
        this.trackLike = trackLike;
        this.trackid=trackid;
    }

    public String getTrackid() {
        return trackid;
    }

    public void setTrackid(String trackid) {
        this.trackid = trackid;
    }

    public String getTrackImg() {
        return trackImg;
    }

    public void setTrackImg(String trackImg) {
        this.trackImg = trackImg;
    }

    public String getTrackTitle() {
        return trackTitle;
    }

    public void setTrackTitle(String trackTitle) {
        this.trackTitle = trackTitle;
    }

    public String getTrackArtist() {
        return trackArtist;
    }

    public void setTrackArtist(String trackArtist) {
        this.trackArtist = trackArtist;
    }

    public String getTrackUrl() {
        return trackUrl;
    }

    public void setTrackUrl(String trackUrl) {
        this.trackUrl = trackUrl;
    }

    public int getTrackDur() {
        return trackDur;
    }

    public void setTrackDur(int trackDur) {
        this.trackDur = trackDur;
    }

    public int getTrackLike() {
        return trackLike;
    }

    public void setTrackLike(int trackLike) {
        this.trackLike = trackLike;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.trackImg);
        dest.writeString(this.trackTitle);
        dest.writeString(this.trackArtist);
        dest.writeString(this.trackUrl);
        dest.writeInt(this.trackDur);
        dest.writeInt(this.trackLike);
    }

    protected Track(Parcel in) {
        this.trackImg = in.readString();
        this.trackTitle = in.readString();
        this.trackArtist = in.readString();
        this.trackUrl = in.readString();
        this.trackDur = in.readInt();
        this.trackLike = in.readInt();
    }

    public static final Parcelable.Creator<Track> CREATOR = new Parcelable.Creator<Track>() {
        @Override
        public Track createFromParcel(Parcel source) {
            return new Track(source);
        }

        @Override
        public Track[] newArray(int size) {
            return new Track[size];
        }
    };
}
