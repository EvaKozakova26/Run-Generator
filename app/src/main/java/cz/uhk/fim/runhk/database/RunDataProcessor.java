package cz.uhk.fim.runhk.database;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import cz.uhk.fim.runhk.model.Run;
import cz.uhk.fim.runhk.model.LocationModel;
import cz.uhk.fim.runhk.model.RunData;
import cz.uhk.fim.runhk.utils.MedianCounterUtils;
import cz.uhk.fim.runhk.utils.DatabaseUtils;

/**
 * Vytahne vsechny behy uzivatele, zpracuje data a posle do MapsActivity data pro vygenerovani trasy
 */
public class RunDataProcessor {

    void processAndSaveRunData(final int weight) {
        final List<Run> runList = new ArrayList<>();
        DatabaseReference userDatabaseReference = DatabaseUtils.getUserDatabaseReference().child("finished");

        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Run run = snapshot.getValue(Run.class);
                    runList.add(run);

                }
                updateRunData(recountRunData(weight, runList));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        userDatabaseReference.addValueEventListener(postListener);
    }


    private void updateRunData(RunData runData) {
        DatabaseReference databaseReferenceRunData = DatabaseUtils.getUserDatabaseReference().child("runData");
        databaseReferenceRunData.setValue(runData);
    }

    private RunData recountRunData(int weight, List<Run> runList) {
        List<Double> elapsedTimeList = new ArrayList<>();
        List<Double> distances = new ArrayList<>();
        List<Double> caloriesList = new ArrayList<>();
        List<Double> elevationList = new ArrayList<>();
        List<LatLng> distancePoints = new ArrayList<>();

        for (Run run : runList) {
            elapsedTimeList.add(run.getElaspedTime());
            distances.add(run.getDistance());
            caloriesList.add(run.getCaloriesBurnt());
            elevationList.add(run.getElevationGain());
            for (LocationModel locationModel : run.getDistancePoints()) {
                LatLng latLng = new LatLng(locationModel.getLatitude(), locationModel.getLongitude());
                distancePoints.add(latLng);
            }
        }

        long avgElapsedTime = (long) MedianCounterUtils.getMedian(elapsedTimeList);
        double avgDistance = MedianCounterUtils.getMedian(distances);
        int avgCalories = (int) MedianCounterUtils.getMedian(caloriesList);
        int avgElevationGain = (int) MedianCounterUtils.getMedian(elevationList);
        RunData runData = new RunData();
        runData.setDistance(avgDistance);
        runData.setTime(avgElapsedTime);
        runData.setCalories(avgCalories);
        runData.setElevation(avgElevationGain);
        runData.setPlayerWeight(weight);
        return runData;

    }
}
