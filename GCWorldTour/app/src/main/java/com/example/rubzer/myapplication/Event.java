package com.example.rubzer.myapplication;

import java.io.Serializable;

public class Event implements Serializable {

    private String name, genre, date, location, poster, video, firebaseId, latlong;
    private Artist artist;

    public Event(String name, String genre, String date, String location
                , String poster, String video, String firebaseId, Artist artist, String latlong){
        this.name = name;
        this.genre = genre;
        this.date = date;
        this.location = location;
        this.poster = poster;
        this.video = video;
        this.firebaseId = firebaseId;
        this.artist = artist;
        this.latlong = latlong;
    }

    public Event(){

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getPoster() {
        return poster;
    }

    public void setPoster(String poster) {
        this.poster = poster;
    }

    public String getVideo() {
        return video;
    }

    public void setVideo(String video) {
        this.video = video;
    }

    public String getFirebaseId() {
        return firebaseId;
    }

    public void setFirebaseId(String firebaseId) {
        this.firebaseId = firebaseId;
    }

    public Artist getArtist() {
        return artist;
    }

    public void setArtist(Artist artist) {
        this.artist = artist;
    }

    public String getLatlong() {
        return latlong;
    }

    public void setLatlong(String latlong) {
        this.latlong = latlong;
    }
}
