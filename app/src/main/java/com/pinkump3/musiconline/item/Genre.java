package com.pinkump3.musiconline.item;

public class Genre  {
    private String genValue, genName;

    public Genre(String genValue, String genName) {
        this.genValue = genValue;
        this.genName = genName;
    }

    public String getGenValue() {
        return genValue;
    }

    public void setGenValue(String genValue) {
        this.genValue = genValue;
    }

    public String getGenName() {
        return genName;
    }

    public void setGenName(String genName) {
        this.genName = genName;
    }
}
