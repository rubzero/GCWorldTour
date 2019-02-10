package com.example.rubzer.myapplication;

import java.util.ArrayList;
import java.util.List;

public class EventList {

    private List<Event> eventList;

    public EventList(){
        eventList = new ArrayList<>();
    }

    public void addEvent(Event event){
        for(int i=0; i<eventList.size(); i++){
            if(eventList.get(i).equals(event))
                return;
        }
        eventList.add(event);
    }

    public List<Event> getEventList() {
        return eventList;
    }

    public Event getEventByName(String name){
        for(int i=0; i<eventList.size(); i++)
            if(eventList.get(i).getName().equals(name))
                return eventList.get(i);
        return null;
    }
}
