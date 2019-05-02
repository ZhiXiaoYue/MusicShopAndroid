package com.example.jill.firstry.event;

import com.example.jill.firsttry.model.Song;

public class OnSearchByIdFinishEvent {

    public OnSearchByIdFinishEvent(Song song){
        this.song=song;
    }
    public Song getSong() {
        return song;
    }

    public void setSong(Song song) {
        this.song = song;
    }

    Song song;

}
