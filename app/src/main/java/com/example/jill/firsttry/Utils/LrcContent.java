package com.example.jill.firsttry.Utils;

public class LrcContent {
    private String lrcStr;  //歌词内容
    private int lrcTime;    //歌词当前时间
   // private int lrcLastingTime;//歌词持续时间
    public String getLrcStr() {
        return lrcStr;
    }
    public void setLrcStr(String lrcStr) {
        this.lrcStr = lrcStr;
    }
    public int getLrcTime() {
        return lrcTime;
    }
    public void setLrcTime(int lrcTime) {
        this.lrcTime = lrcTime;
    }

   /* public int getLrcLastingTime() {
        return lrcLastingTime;
    }
    public void setLrcLastingTime(int lrcLastingTime) {
        this.lrcLastingTime = lrcLastingTime;
    }*/
}