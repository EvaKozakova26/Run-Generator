package cz.uhk.fim.runhk.model;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by EvaKozakova on 02.04.2018.
 */

public class Challenge {

    private int level;
    private double distance;
    private double distanceToDo;
    private int exps;
    private double time;
    private boolean finished;
    private String date;

    public Challenge(int level, double distance, int exps, double time, boolean finished) {
        this.level = level;
        this.distance = distance;
        this.exps = exps;
        this.time = time;
        this.finished = finished;
    }

    public Challenge() {
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

    public double getTime() {
        return time;
    }

    public void setTime(double time) {
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
}
