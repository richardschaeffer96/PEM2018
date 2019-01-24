package com.example.richa_000.sponnect;

import java.io.Serializable;
import java.util.HashMap;

class Spot implements Serializable {

    private String info;
    private String title;
    private String address;
    private String date;
    private String time;
    private String creator;
    private double latitude;
    private double longitude;

    private String id;

    private HashMap<String, Integer> participants;

    public Spot() {
        //Need to be here for reasons...
    }

    public Spot(String title, String info, String address,String date, String time, String creator, double latitude, double longitude) {
        this.title = title;
        this.address = address;
        this.info = info;
        this.date = date;
        this.time = time;
        this.creator = creator;
        this.latitude = latitude;
        this.longitude = longitude;
        this.participants = new HashMap<>();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getcreator() { return creator; }

    public void setCreator(String creator) { this.creator = creator; }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public HashMap<String, Integer> getParticipants() {
        return participants;
    }

    public void setParticipants(HashMap<String,Integer> participants) {
        this.participants = participants;
    }
}
