package com.example.jill.firsttry.others;
import android.app.Application;

import com.example.jill.firsttry.model.Song;
import com.example.jill.firsttry.model.download.DownloadBean;
import com.example.jill.firsttry.model.search.Keys;

import java.util.ArrayList;

public class AppContext extends Application {  
    private String state; //保存字符串
    private UserBean user; //保存User实体对象

    private Song song;//保存Keys实体对象（有关点击到的歌曲的id,歌名，歌手名，专辑名等信息）

    public DownloadBean getDownloadBean() {
        return downloadBean;
    }

    public void setDownloadBean(DownloadBean downloadBean) {
        this.downloadBean = downloadBean;
    }

    private DownloadBean downloadBean;

    private ArrayList<Song> downLoadAccompanyment;//保存下载歌曲

    public Song getSong() {
        return song;
    }

    public void setSong(Song song) {
        this.song = song;
    }

    public void addDownLoadAccompanyment(Song keys) {
        this.downLoadAccompanyment.add(keys);
    }

    public ArrayList<Song> getDownLoadAccompanyment() {
        return downLoadAccompanyment;
    }

    public void onCreate() {
        //初始化字符串
        state = null;
        song = null;
        downLoadAccompanyment = new ArrayList<Song>();
        super.onCreate();
    }
  
    public String getState() {
        return state;
    }
    public void setState(String b) {
        this.state = b;
    }  
  
    public UserBean getUser() {
        return user;  
    }  
  
    public void setUser(UserBean user) {
        this.user = user;  
    }
      
}