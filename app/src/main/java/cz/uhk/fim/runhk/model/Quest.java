package cz.uhk.fim.runhk.model;

/**
 * Created by EvaKozakova on 02.04.2018.
 */

public class Quest {

    private int level;
    private double distance;
    private int exps;

    public Quest(int level, double distance, int exps) {
        this.level = level;
        this.distance = distance;
        this.exps = exps;
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
}
