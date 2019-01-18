package cz.uhk.fim.runhk.model;

import java.util.ArrayList;

/**
 * Created by EvaKozakova on 02.04.2018.
 */

public class Challenge {

    private int level;
    private double distance;
    private double distanceToDo;
    private int exps;
    private String time;
    private double elaspedTime;
    private boolean finished;
    private String date;
    private ArrayList<LocationModel> distancePoints;
    private double caloriesBurnt;
    private double elevationGain;

    public Challenge(int level, double distance, int exps, String time, boolean finished) {
        this.level = level;
        this.distance = distance;
        this.exps = exps;
        this.time = time;
        this.finished = finished;
    }

    public Challenge() {
    }

    public double getElevationGain() {
        return elevationGain;
    }

    public void setElevationGain(double elevationGain) {
        this.elevationGain = elevationGain;
    }

    public double getCaloriesBurnt() {
        return caloriesBurnt;
    }

    public void setCaloriesBurnt(double caloriesBurnt) {
        this.caloriesBurnt = caloriesBurnt;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public int getExps() {
        return exps;
    }

    public void setExps(int exps) {
        this.exps = exps;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public double getDistanceToDo() {
        return distanceToDo;
    }

    public void setDistanceToDo(double distanceToDo) {
        this.distanceToDo = distanceToDo;
    }

    public boolean isFinished() {
        return finished;
    }

    public void setFinished(boolean finished) {
        this.finished = finished;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public ArrayList<LocationModel> getDistancePoints() {
        return distancePoints;
    }

    public void setDistancePoints(ArrayList<LocationModel> distancePoints) {
        this.distancePoints = distancePoints;
    }

    public double getElaspedTime() {
        return elaspedTime;
    }

    public void setElaspedTime(double elaspedTime) {
        this.elaspedTime = elaspedTime;
    }
}
