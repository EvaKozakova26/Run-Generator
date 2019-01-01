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

/**
 * Vytahne vsechny behy uzivatele, zpracuje data a posle do MapsActivity data pro vygenerovani trasy
 */
public class RunDataProvider {

    private FirebaseUser currentUser;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private DatabaseReference databaseReferenceRunData;

    private List<Challenge> challengeList;
    private List<LatLng> distancePoints;

    public RunData runData = new RunData();

    public void processAndSaveRunData() {
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
                processData();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        databaseReference.addValueEventListener(postListener);
    }

    private RunData processData() {
        List<Long> elapsedTimeList = new ArrayList<>();
        List<Double> distances = new ArrayList<>();
        List<Integer> caloriesList = new ArrayList<>();
        List<Integer> elevationList = new ArrayList<>();
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

        long sumTime = 0;
        for (int i = 0; i < elapsedTimeList.size(); i++) {
            sumTime = sumTime + elapsedTimeList.get(i);
        }
        long avgElapsedTime = sumTime / elapsedTimeList.size();

        double sumDistance = 0;
        for (int i = 0; i < distances.size(); i++) {
            sumDistance = sumDistance + distances.get(i);
        }
        double avgDistance = sumDistance / distances.size();

        int sumCalories = 0;
        for (int i = 0; i < caloriesList.size(); i++) {
            sumCalories = sumCalories + caloriesList.get(i);
        }
        int avgCalories = sumCalories / caloriesList.size();

        int sumElevations = 0;
        for (int i = 0; i < elevationList.size(); i++) {
            sumElevations = sumElevations + elevationList.get(i);
        }
        int avgElevationGain = sumElevations / elevationList.size();

        runData.setDistance(avgDistance);
        runData.setTime(avgElapsedTime);
        runData.setCalories(avgCalories);
        runData.setElevation(avgElevationGain);
        databaseReferenceRunData = firebaseDatabase.getReference("user").child(currentUser.getUid()).child("runData");
        databaseReferenceRunData.setValue(runData);
        return runData;

    }
}
