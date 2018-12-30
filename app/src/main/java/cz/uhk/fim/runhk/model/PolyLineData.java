package cz.uhk.fim.runhk.model;


import com.google.android.gms.maps.model.LatLng;

import java.util.List;

public class PolyLineData {

    private long distance;
    private int time;
    private int calories;
    private int elevationGain;
    private List<LatLng> polyLinePoints;

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    private int index;

    public long getDistance() {
        return distance;
    }

    public void setDistance(long distance) {
        this.distance = distance;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public int getCalories() {
        return calories;
    }

    public void setCalories(int calories) {
        this.calories = calories;
    }

    public int getElevationGain() {
        return elevationGain;
    }

    public void setElevationGain(int elevationGain) {
        this.elevationGain = elevationGain;
    }

    public List<LatLng> getPolyLinePoints() {
        return polyLinePoints;
    }

    public void setPolyLinePoints(List<LatLng> polyLinePoints) {
        this.polyLinePoints = polyLinePoints;
    }
}
