package cz.uhk.fim.runhk.database;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import cz.uhk.fim.runhk.model.Run;
import cz.uhk.fim.runhk.model.LocationModel;
import cz.uhk.fim.runhk.model.RunData;
import cz.uhk.fim.runhk.service.helper.utils.MedianCounter;

/**
 * Vytahne vsechny behy uzivatele, zpracuje data a posle do MapsActivity data pro vygenerovani trasy
 */
public class RunDataProcessor {

    private FirebaseUser currentUser;
    private FirebaseDatabase firebaseDatabase;

    private List<Run> runList;
    private List<LatLng> distancePoints;

    private RunData runData = new RunData();

    void processAndSaveRunData(final int weight) {
        runList = new ArrayList<>();
        distancePoints = new ArrayList<>();

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference("user").child(currentUser.getUid()).child("finished");

        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Run run = snapshot.getValue(Run.class);
                    runList.add(run);

                }
                processData(weight);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        databaseReference.addValueEventListener(postListener);
    }

    private RunData processData(int weight) {
        List<Double> elapsedTimeList = new ArrayList<>();
        List<Double> distances = new ArrayList<>();
        List<Double> caloriesList = new ArrayList<>();
        List<Double> elevationList = new ArrayList<>();
        MedianCounter medianCounter = new MedianCounter();
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

        long avgElapsedTime = (long) medianCounter.getMedian(elapsedTimeList);
        double avgDistance = medianCounter.getMedian(distances);
        int avgCalories = (int) medianCounter.getMedian(caloriesList);
        int avgElevationGain = (int) medianCounter.getMedian(elevationList);

        runData.setDistance(avgDistance);
        runData.setTime(avgElapsedTime);
        runData.setCalories(avgCalories);
        runData.setElevation(avgElevationGain);
        runData.setPlayerWeight(weight);
        DatabaseReference databaseReferenceRunData = firebaseDatabase.getReference("user").child(currentUser.getUid()).child("runData");
        databaseReferenceRunData.setValue(runData);
        return runData;

    }
}
