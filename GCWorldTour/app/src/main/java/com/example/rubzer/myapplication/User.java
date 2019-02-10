package com.example.rubzer.myapplication;

import java.util.ArrayList;

public class User {

    private String id, name, email, photo;
    private ArrayList<String> myEventList, myPurchasedList;

    public User(String id, String name, String email, String photo, ArrayList<String> myEventList
            , ArrayList<String> myPurchasedList){
        this.id = id;
        this.name = name;
        this.email = email;
        this.photo = photo;
        this.myEventList = myEventList;
        this.myPurchasedList = myPurchasedList;
    }

    public User(){}

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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public ArrayList<String> getMyEventList() {
        return myEventList;
    }

    public void setMyEventList(ArrayList<String> myEventList) {
        this.myEventList = myEventList;
    }

    public ArrayList<String> getMyPurchasedList() {
        return myPurchasedList;
    }

    public void setMyPurchasedList(ArrayList<String> myPurchasedList) {
        this.myPurchasedList = myPurchasedList;
    }
}
