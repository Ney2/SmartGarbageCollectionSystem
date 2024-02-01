package com.example.sgcs;

public class Garbage {
    private String GarbageID;
    private double Latitude;
    private double Longitude;
    private String Level;

    public Garbage() {
    }

    public Garbage(String id, double latitude, double longitude, String level) {
        this.GarbageID = id;
        this.Latitude = latitude;
        this.Longitude = longitude;
        this.Level = level;
    }

    public String getId() {
        return GarbageID;
    }

    public void setId(String id) {
        this.GarbageID = id;
    }

    public double getLatitude() {
        return Latitude;
    }

    public void setLatitude(double latitude) {
        this.Latitude = latitude;
    }

    public double getLongitude() {
        return Longitude;
    }

    public void setLongitude(double longitude) {
        this.Longitude= longitude;
    }

    public String getLevel() {
        return Level;
    }

    public void setLevel(String level) {
        this.Level = level;
    }
}
