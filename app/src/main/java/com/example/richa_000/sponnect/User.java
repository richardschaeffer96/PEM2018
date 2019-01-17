package com.example.richa_000.sponnect;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class User implements Serializable {

    private String eMail;
    private String nickname;
    private String password;
    private String gender;
    int age;
    private HashMap<String, Integer> mySpots;

    //optional
    private String[] socialMedia;
    private String id;

    public User() {
        //public empty constructor needed for fireStore
    }

    public User(String eMail, String nickname, String password, String gender, int age){
        this.eMail = eMail;
        this.nickname = nickname;
        this.password = password;
        this.gender = gender;
        this.age = age;
        this.mySpots = new HashMap<>();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String geteMail() {
        return eMail;
    }

    public void seteMail(String eMail) {
        this.eMail = eMail;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public HashMap<String, Integer> getMySpots() { return mySpots; }

    public void setMySpots(HashMap<String, Integer> mySpots) { this.mySpots = mySpots; }
}
