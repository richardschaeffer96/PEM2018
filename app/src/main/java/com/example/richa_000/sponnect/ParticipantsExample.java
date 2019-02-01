package com.example.richa_000.sponnect;

public class ParticipantsExample {
    private String id;
    private String name;
    private String gender;
    private String age;
    private String img;
    private int state;

    public ParticipantsExample(String name, String gender, String age, String img, int state, String id) {
        this.name = name;
        this.gender = gender;
        this.age = age;
        this.img = img;
        this.state = state;
        this.id = id;
    }

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

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }
}
