package com.example.jill.firsttry.model;


public class LyricsBean {

    private int songStatus;
    private int lyricVersion;
    private String lyric;
    private int code;
    public void setSongStatus(int songStatus) {
        this.songStatus = songStatus;
    }
    public int getSongStatus() {
        return songStatus;
    }

    public void setLyricVersion(int lyricVersion) {
        this.lyricVersion = lyricVersion;
    }
    public int getLyricVersion() {
        return lyricVersion;
    }

    public void setLyric(String lyric) {
        this.lyric = lyric;
    }
    public String getLyric() {
        return lyric;
    }

    public void setCode(int code) {
        this.code = code;
    }
    public int getCode() {
        return code;
    }

}

