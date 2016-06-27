package com.lee42.wheeledwalksviewer.models;

/**
 * Created by David on 2/06/2016.
 */

public class TrackSegment {
    //start and stop times and coords
    private long startTimestamp;
    private Float startLattitude;
    private Float startLongitude;
    private long endTimestamp;
    private Float endLattitude;
    private Float endLongitude;

    //segment info - this is used for displaying purposes
    //TODO: actually implement for release 2;
    private int steepness; // the max steepness of the segment - 0 = flat, 100 = 45 degrees
    private int roll;      // the max sideways slope of the segment - 0 = flat, 100 = 45 degrees
    private int roughness; // the surface roughness 0=smooth, 100 = very very rough;

    private int difficulty; //the max difficulty based on above ratings: 1-100;

    public TrackSegment() {
    }

    public long getStartTimestamp() {
        return startTimestamp;
    }

    public void setStartTimestamp(long startTimestamp) {
        this.startTimestamp = startTimestamp;
    }

    public Float getStartLattitude() {
        return startLattitude;
    }

    public void setStartLattitude(Float startLattitude) {
        this.startLattitude = startLattitude;
    }

    public Float getStartLongitude() {
        return startLongitude;
    }

    public void setStartLongitude(Float startLongitude) {
        this.startLongitude = startLongitude;
    }

    public long getEndTimestamp() {
        return endTimestamp;
    }

    public void setEndTimestamp(long endTimestamp) {
        this.endTimestamp = endTimestamp;
    }

    public Float getEndLattitude() {
        return endLattitude;
    }

    public void setEndLattitude(Float endLattitude) {
        this.endLattitude = endLattitude;
    }

    public Float getEndLongitude() {
        return endLongitude;
    }

    public void setEndLongitude(Float endLongitude) {
        this.endLongitude = endLongitude;
    }

    public int getSteepness() {
        return steepness;
    }

    public void setSteepness(int steepness) {
        this.steepness = steepness;
    }

    public int getRoll() {
        return roll;
    }

    public void setRoll(int roll) {
        this.roll = roll;
    }

    public int getRoughness() {
        return roughness;
    }

    public void setRoughness(int roughness) {
        this.roughness = roughness;
    }

    public int getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(int difficulty) {
        this.difficulty = difficulty;
    }
}

