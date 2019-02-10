package com.example.rubzer.myapplication;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ArtistList implements Serializable {

    private List<Artist> artistList;

    public ArtistList(){
        artistList = new ArrayList<>();
    }

    public void addArtist(Artist artist){
        for(int i=0; i<artistList.size(); i++){
            if(artistList.get(i).equals(artist))
                return;
        }
        artistList.add(artist);
    }

    public Artist getArtistByName(String name){
        Artist artist = null;
        for(int i=0; i<artistList.size(); i++){
            if(name.equals(artistList.get(i).getName()))
                artist = artistList.get(i);
        }
        return artist;
    }

    public List<Artist> getArtistList() {
        return artistList;
    }
}
