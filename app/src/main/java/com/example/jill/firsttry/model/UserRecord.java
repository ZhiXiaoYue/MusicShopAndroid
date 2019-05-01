package com.example.jill.firsttry.model;

public class UserRecord extends Base {
    private String filepath;//歌曲id
    private Song music;
    private int rid;
    private String score;
    private String time;

    public String getFilepath() {
        return filepath;
    }

    public void setFilepath(String filepath) {
        this.filepath = filepath;
    }

    public Song getMusic() {
        return music;
    }

    public void setMusic(Song music) {
        this.music = music;
    }

    public int getRid() {
        return rid;
    }

    public void setRid(int rid) {
        this.rid = rid;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }



    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
