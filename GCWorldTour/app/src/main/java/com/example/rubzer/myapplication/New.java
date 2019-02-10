package com.example.rubzer.myapplication;

import java.io.Serializable;

public class New implements Serializable {

    private String title, firebaseId, type, name;

    public New(String title, String firebaseId, String type, String name){
        this.title = title;
        this.firebaseId = firebaseId;
        this.type = type;
        this.name = name;
    }

    public New(){

    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getFirebaseId() {
        return firebaseId;
    }

    public void setFirebaseId(String firebaseId) {
        this.firebaseId = firebaseId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
