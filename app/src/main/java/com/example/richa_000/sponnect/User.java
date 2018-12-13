package com.example.richa_000.sponnect;

import android.support.v7.app.AppCompatActivity;

public class User extends AppCompatActivity {

    private String eMail;
    private String nickname;
    private String password;
    private String gender;
    private int age;

    //optional
    private String socailMedia;

    public User() {
        //public empty constructor needed for fireStore
    }

    public User(String eMail, String nickname, String password, String gender, int age){
        this.eMail = eMail;
        this.nickname = nickname;
        this.password = password;
        this.gender = gender;
        this. age = age;
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
}
