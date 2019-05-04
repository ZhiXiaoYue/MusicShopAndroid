package com.example.jill.firsttry.model;
import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.RequiresApi;

import com.example.jill.firsttry.model.response.BaseResponse;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Recommandation {
    private ArrayList<Song> recommendation = new ArrayList<>();

    public static final int GET_DATA_SUCCESS = 1;
    public Recommandation() {
        ArrayList<Integer> recList = new ArrayList<>();
        recList.add(48);
        recList.add(49);
        recList.add(50);
        recList.add(51);
        for (Integer recId : recList) {
            @SuppressLint("HandlerLeak") final Handler mHandler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    if (msg.what == 1) {
                        String returnMessage = (String) msg.obj;
                        BaseResponse bsr = new Gson().fromJson(returnMessage, BaseResponse.class);
                        returnMessage = bsr.getData();
                        returnMessage = returnMessage.replaceAll("\\\\\"", "\"");
                        returnMessage = returnMessage.replaceAll("\"\\[", "");
                        returnMessage = returnMessage.replaceAll("]\"", "");
                        returnMessage = returnMessage.replace("[","");
                        returnMessage = returnMessage.replace("]","");
                        Song s = new Gson().fromJson(returnMessage, Song.class);
                        recommendation.add(s);
                    }
                }
            };
            final OkHttpClient client = new OkHttpClient();
            @SuppressLint("DefaultLocale") String mUrl = String.format("http://58.87.73.51:8080/musicshop/api/search?keyword=%d&type=4", recId);
            final Request request = new Request.Builder()
                    .addHeader("token", "13108296618^1552615513")
                    .url(mUrl)
                    .build();
            Thread thread = new Thread(new Runnable() {
                @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                @Override
                public void run() {
                    Response response;
                    try {
                        //回调
                        response = client.newCall(request).execute();
                        if (response.isSuccessful()) {
                            //将服务器响应的参数response.body().string())发送到hanlder中，并更新ui
                            mHandler.obtainMessage(1, Objects.requireNonNull(response.body()).string()).sendToTarget();
                        } else {
                            throw new IOException("Unexpected code:" + response);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            thread.start();
        }
    }

    public ArrayList<Song> getRecommendation() {
        return recommendation;
    }

    public void setRecommendation(ArrayList<Song> recommendation) {
        this.recommendation = recommendation;
    }
//    /**
//     * 装一点假的数据，后来删掉就好
//     */
//    private Song recomendation1(){
//        Song song = new Song();
//        song.setSname("原谅");
//        song.setSingerName("刘瑞琦");
//        song.setSid(48);
//        song.setAlbum("头号粉丝");
//        song.setAlbumPic("static/album_thumbnails/刘瑞琦-头号粉丝.jpg");
//        song.setFilePath("static/music/原谅-刘瑞琦.mp3");
//        song.setInstrumental("static/instru/原谅刘瑞琦.mp3");
//        song.setLyric("static/lyric/原谅刘瑞琦.krc");
//        return song;
//    }
}
