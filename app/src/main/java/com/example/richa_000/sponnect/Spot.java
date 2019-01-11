package com.example.richa_000.sponnect;

import java.io.Serializable;

class Spot implements Serializable {

    private String info;
    private String title;
    private String address;
    private String date;
    private String time;
    private double latitude;
    private double longitude;

    private String[] participants;

    public Spot() {
        //Need to be here for reasons...
    }

    public Spot(String title, String info, String address,String date, String time, double latitude, double longitude) {
        this.title = title;
        this.address = address;
        this.info = info;
        this.date = date;
        this.time = time;
        this.latitude = latitude;
        this.longitude = longitude;
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

    public String[] getParticipants() { return participants; }

    public void setParticipants(String[] participants) { this.participants = participants; }
}
