package com.example.rubzer.myapplication;

import java.util.ArrayList;
import java.util.List;

public class NewList {

    private List<New> newList;

    public NewList(){
        newList = new ArrayList<>();
    }

    public void addNew(New n){
        for(int i = 0; i< newList.size(); i++){
            if(newList.get(i).equals(n))
                return;
        }
        newList.add(n);
    }

    public List<New> getNewList() {
        return newList;
    }

    public New getNewByTitle(String title){
        for(int i = 0; i< newList.size(); i++)
            if(newList.get(i).getTitle().equals(title))
                return newList.get(i);
        return null;
    }
}
