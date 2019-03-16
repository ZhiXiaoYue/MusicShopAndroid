package com.example.jill.firsttry.model;

/**
 *  存储获得的用户记录的数据
 */
public class OnlineSongBean {
    private String path; //歌曲路径
    private int rid;  //歌曲id
    private int score;  //分数
    private String time;  //歌曲时间

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getRid() {
        return rid;
    }

    public void setRid(int rid) {
        this.rid = rid;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
