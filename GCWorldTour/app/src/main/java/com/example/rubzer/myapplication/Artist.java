package com.example.rubzer.myapplication;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Artist implements Serializable {

    private String name, proverb, genre, description, firebaseId;
    private ArrayList<String> youtubeVideos;
    private HashMap<String, String> social;

    public Artist(){}

    public Artist( String name, String proverb, String genre, String description
            , String firebaseId, ArrayList<String> youtubeVideos, HashMap<String, String> social){
        this.name = name;
        this.proverb = proverb;
        this.genre = genre;
        this.description = description;
        this.firebaseId = firebaseId;
        this.youtubeVideos = youtubeVideos;
        this.social = social;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProverb() {
        return proverb;
    }

    public void setProverb(String proverb) {
        this.proverb = proverb;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getFirebaseId() {
        return firebaseId;
    }

    public void setFirebaseId(String firebaseId) {
        this.firebaseId = firebaseId;
    }

    public List<String> getYoutubeVideos() {
        return youtubeVideos;
    }

    public void setYoutubeVideos(ArrayList<String> youtubeVideos) {
        this.youtubeVideos = youtubeVideos;
    }

    public HashMap<String, String> getSocial() {
        return social;
    }

    public void setSocial(HashMap<String, String> social) {
        this.social = social;
    }
}
