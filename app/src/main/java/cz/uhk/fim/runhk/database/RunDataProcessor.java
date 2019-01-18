package cz.uhk.fim.runhk.database;

import android.content.Context;

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

import cz.uhk.fim.runhk.activities.DifficultyActivity;
import cz.uhk.fim.runhk.activities.GeneratedMapActivity;
import cz.uhk.fim.runhk.model.Challenge;
import cz.uhk.fim.runhk.model.LocationModel;
import cz.uhk.fim.runhk.model.RunData;
import cz.uhk.fim.runhk.service.AsyncResponse;
import cz.uhk.fim.runhk.service.ElevationService;
import cz.uhk.fim.runhk.service.Math.MedianCounter;

/**
 * Vytahne vsechny behy uzivatele, zpracuje data a posle do MapsActivity data pro vygenerovani trasy
 */
public class RunDataProcessor {

    private FirebaseUser currentUser;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private DatabaseReference databaseReferenceRunData;

    private MedianCounter medianCounter;

    private List<Challenge> challengeList;
    private List<LatLng> distancePoints;

    public RunData runData = new RunData();

    public void processAndSaveRunData(final int weight) {
        challengeList = new ArrayList<>();
        distancePoints = new ArrayList<>();

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("user").child(currentUser.getUid()).child("finished");

        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Challenge challenge = snapshot.getValue(Challenge.class);
                    challengeList.add(challenge);

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
        medianCounter = new MedianCounter();
        for (Challenge challenge : challengeList) {
            elapsedTimeList.add(challenge.getElaspedTime());
            distances.add(challenge.getDistance());
            caloriesList.add(challenge.getCaloriesBurnt());
            elevationList.add(challenge.getElevationGain());
            for (LocationModel locationModel : challenge.getDistancePoints()) {
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
        databaseReferenceRunData = firebaseDatabase.getReference("user").child(currentUser.getUid()).child("runData");
        databaseReferenceRunData.setValue(runData);
        return runData;

    }
}
