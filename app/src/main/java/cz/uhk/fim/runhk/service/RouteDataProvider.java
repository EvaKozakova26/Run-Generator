package cz.uhk.fim.runhk.service;

import com.google.maps.DirectionsApi;
import com.google.maps.GeoApiContext;
import com.google.maps.errors.ApiException;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.TravelMode;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import cz.uhk.fim.runhk.database.DatabaseHelper;

public class RouteDataProvider {

    private DatabaseHelper databaseHelper = new DatabaseHelper();

    public DirectionsResult createDirectionResult(String startAddress, String waypoint, String endAddress) {
        DirectionsResult directionsResult = new DirectionsResult();
        try {
            directionsResult = DirectionsApi.newRequest(getGeoContext())
                    .mode(TravelMode.WALKING)
                    .origin(startAddress)
                    .destination(endAddress)
                    .alternatives(true)
                    .waypoints(waypoint)
                    .await();
        } catch (ApiException | InterruptedException | IOException e) {
            e.printStackTrace();
        }
        return directionsResult;
    }

    private GeoApiContext getGeoContext() {
        GeoApiContext geoApiContext = new GeoApiContext();
        return geoApiContext.setQueryRateLimit(3)
                .setApiKey("AIzaSyDS6vXNVTJFOUkJTcVhHfsEhuFOwmtkNxk")
                .setConnectTimeout(1, TimeUnit.SECONDS)
                .setReadTimeout(1, TimeUnit.SECONDS)
                .setWriteTimeout(1, TimeUnit.SECONDS);

    }

    public int getExpectedCaloriesBurn(int weight, double expectedDistance, double expectedDuration, int expectedElevationGain) {
        return databaseHelper.getCaloriesBurnt(weight, expectedDistance, (long) expectedDuration, expectedElevationGain);
    }

    public double getExpectedDuration(long avgTotalTime, long expectedDistance, double avgDistance) {
        double duration = (avgTotalTime / 1000.0) / 60.0; //tominutes
        double avgPace = duration / (avgDistance / 1000); // pace per minute
        return avgPace * (expectedDistance / 1000.0);
    }

}
