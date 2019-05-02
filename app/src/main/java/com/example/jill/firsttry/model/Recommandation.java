package com.example.jill.firsttry.model;

import java.util.ArrayList;

public class Recommandation {
    private ArrayList<Song> recommendation = new ArrayList<>();

    public Recommandation() {
        recommendation.add(recomendation1());
        recommendation.add(recomendation1());
        recommendation.add(recomendation1());
    }

    public ArrayList<Song> getRecommendation() {
        return recommendation;
    }

    public void setRecommendation(ArrayList<Song> recommendation) {
        this.recommendation = recommendation;
    }
    /**
     * 装一点假的数据，后来删掉就好
     */
    private Song recomendation1(){
        Song song = new Song();
        song.setSname("原谅");
        song.setSingerName("刘瑞琦");
        song.setSid(38);
        song.setAlbum("头号粉丝");
        song.setAlbumPic("static/album_thumbnails/刘瑞琦-头号粉丝.jpg");
        song.setFilePath("static/music/原谅-刘瑞琦.mp3");
        song.setInstrumental("static/instru/原谅刘瑞琦.mp3");
        song.setLyric("static/lyric/原谅刘瑞琦.krc");
        return song;
    }
}
