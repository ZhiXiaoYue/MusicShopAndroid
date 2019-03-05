package com.example.jill.firsttry.model.search;


import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;


/**
 * Created by 46639 on 2018/3/22.
 */

public class Keys {
    private String id;
    private String name;
    private ArrayList<String> artists;
    private String album;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<String> getArtists() {
        return artists;
    }

    public void setArtists(ArrayList<String> artists) {
        this.artists = artists;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getAllArtists(){
        StringBuilder artistsString= new StringBuilder();
        int i;
        for(i=0;i<artists.size();i++)
        {
            if(i==0) artistsString.append(artists.get(i));
            else artistsString.append("/").append(artists.get(i));
        }
        return artistsString.toString();
    }

    public void setAllArtists(String artistsString){
        String[] allArtistsStringList=artistsString.split("\\\\");
        ArrayList<String> allArtistsList= new ArrayList<>();
        Collections.addAll(allArtistsList, allArtistsStringList);
        this.artists=allArtistsList;
    }

}


