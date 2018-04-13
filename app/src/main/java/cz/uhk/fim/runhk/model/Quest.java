package cz.uhk.fim.runhk.model;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by EvaKozakova on 02.04.2018.
 */

public class Quest {

    private int level;
    private double distance;
    private int exps;
    private double time;

    public Quest(int level, double distance, int exps, double time) {
        this.level = level;
        this.distance = distance;
        this.exps = exps;
        this.time = time;
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
}
