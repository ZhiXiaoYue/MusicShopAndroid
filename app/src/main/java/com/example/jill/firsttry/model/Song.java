package com.example.jill.firsttry.model;

import java.util.ArrayList;

public class Song {
    private String album;
    private String sid;
    private String singerName;
    private String sname;
    private ArrayList<String> userRecords;

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public String getSingerName() {
        return singerName;
    }

    public void setSingerName(String singerName) {
        this.singerName = singerName;
    }

    public String getSname() {
        return sname;
    }

    public void setSname(String sname) {
        this.sname = sname;
    }

    public ArrayList<String> getUserRecords() {
        return userRecords;
    }

    public void setUserRecords(ArrayList<String> userRecords) {
        this.userRecords = userRecords;
    }
}
