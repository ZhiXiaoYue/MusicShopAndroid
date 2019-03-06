package com.example.jill.firsttry.Utils;

import android.annotation.SuppressLint;
import android.content.Context;

import com.example.jill.firsttry.model.Song;
import com.example.jill.firsttry.model.download.DownloadBean;
import com.google.gson.Gson;

import java.io.IOException;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class DownloadCompany {

    Context activityContext;
    DownloadUtil downloadUtil;
    Song keys;
    DownloadBean downloadBean;

    @SuppressLint("SdCardPath")
    public DownloadCompany(Context activityContext, Song keys){
        this.activityContext=activityContext;
        this.keys=keys;
        downloadUtil=new DownloadUtil(activityContext);

        String url="http://mrquin.space/musicshop/api/download?sid="+ keys.getSid();
        HttpUtil.sendOkHttpRequestWithHeader(url, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseString = response.body().string();
                responseString=responseString.replaceAll("\"\\{","\\{");
                responseString=responseString.replaceAll("\\}\"","\\}");
                responseString=responseString.replaceAll("\\\\\"","\"");
                //System.out.println(responseString);
                Gson gson = new Gson();
                downloadBean = gson.fromJson(responseString, DownloadBean.class);
                //System.out.println(downloadBean.getData().getFilePath());
                String lyricName=downloadBean.getData().getSname() + "-" + downloadBean.getData().getSingerName() + "-" +downloadBean.getData().getAlbum()+"-"+downloadBean.getData().getSid()+".krc";
                downloadUtil.download("http://58.87.73.51/musicshop/"+downloadBean.getData().getLyric(),"/mnt/sdcard/MusicShopDownLoad/Songs//",lyricName);
                String fileName=downloadBean.getData().getSname() + "-" + downloadBean.getData().getSingerName() + "-" +downloadBean.getData().getAlbum()+"-"+downloadBean.getData().getSid()+".mp3";
                downloadUtil.download("http://58.87.73.51/musicshop/"+downloadBean.getData().getFilePath(),"/mnt/sdcard/MusicShopDownLoad/Songs//",fileName);

            }
        },"token","13051393220^1537679154");


        //downloadUtil.download("http://mrquin.space/musicshop/"+downloadBean.getData().getFilePath(),"/mnt/sdcard/MusicShopDownLoad/Songs//",fileName);
    }

}
