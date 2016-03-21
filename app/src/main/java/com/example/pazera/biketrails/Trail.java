package com.example.pazera.biketrails;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pazera on 19-08-2015.
 */
public class Trail {

    private int user_ID;
    private String name;
    private double distance;
    private int time;
    private double calories;
    private double avgSpeed;
    private List<Double> latitudeList = new ArrayList<Double>();
    private List<Double> longtitudeList = new ArrayList<Double>();

    public List<Double> getLatitudeList() {
        return latitudeList;
    }

    public List<Double> getLongtitudeList() {
        return longtitudeList;
    }

    public Double getLongtitude(int i) {
        return longtitudeList.get(i);
    }

    public Double getLatitude(int i) {
        return latitudeList.get(i);
    }

    public void setLongtitude(double longtitude) {
        this.longtitudeList.add(longtitude);
    }

    public void setLatitude(double latitude) {
        this.latitudeList.add(latitude);
    }

    public int getListSize() {return longtitudeList.size();}

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public double getCalories() {
        return calories;
    }

    public void setCalories(double calories) {
        this.calories = calories;
    }

    public double getAvgSpeed() {
        return avgSpeed;
    }

    public void setAvgSpeed(double avgSpeed) {
        this.avgSpeed = avgSpeed;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getUser_ID() {
        return user_ID;
    }

    public void setUser_ID(int user_ID) {
        this.user_ID = user_ID;
    }
}
