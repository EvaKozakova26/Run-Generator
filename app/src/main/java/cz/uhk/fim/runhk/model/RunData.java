package cz.uhk.fim.runhk.model;

public class RunData {

    private long time;
    private double distance;
    private double elevation;
    private int calories;

    public int getPlayerWeight() {
        return playerWeight;
    }

    public void setPlayerWeight(int playerWeight) {
        this.playerWeight = playerWeight;
    }

    private int playerWeight;

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public double getElevation() {
        return elevation;
    }

    public void setElevation(double elevation) {
        this.elevation = elevation;
    }

    public int getCalories() {
        return calories;
    }

    public void setCalories(int calories) {
        this.calories = calories;
    }
}
