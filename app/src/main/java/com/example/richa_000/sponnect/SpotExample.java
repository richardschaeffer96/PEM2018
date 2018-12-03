package com.example.richa_000.sponnect;

public class SpotExample {
    private String spot_name;
    private String spot_date;
    private String spot_time;

    public SpotExample(String spot_name, String spot_date, String spot_time) {
        this.spot_name = spot_name;
        this.spot_date = spot_date;
        this.spot_time = spot_time;
    }

    public String getSpot_name() {
        return spot_name;
    }

    public void setSpot_name(String spot_name) {
        this.spot_name = spot_name;
    }

    public String getSpot_date() {
        return spot_date;
    }

    public void setSpot_date(String spot_date) {
        this.spot_date = spot_date;
    }

    public String getSpot_time() {
        return spot_time;
    }

    public void setSpot_time(String spot_time) {
        this.spot_time = spot_time;
    }
}
