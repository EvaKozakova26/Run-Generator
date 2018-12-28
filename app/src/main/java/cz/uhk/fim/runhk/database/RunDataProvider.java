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
public class RunDataProvider implements AsyncResponse {

    private FirebaseUser currentUser;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private DatabaseReference databaseReferenceRunData;
    private ElevationService elevationService;

    private List<Challenge> challengeList;
    private List<LatLng> distancePoints;
    private List<Double> elevations;

    public RunData runData = new RunData();

    public RunDataProvider() {
        //this to set delegate/listener back to this class
        elevationService = new ElevationService();
        elevationService.delegate = this;
    }

    public void processAndSaveRunData() {
        challengeList = new ArrayList<>();
        distancePoints = new ArrayList<>();
        elevations = new ArrayList<>();

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
        for (Challenge challenge : challengeList) {
            elapsedTimeList.add(challenge.getElaspedTime());
            distances.add(challenge.getDistance());
            for (LocationModel locationModel : challenge.getDistancePoints()) {
                LatLng latLng = new LatLng(locationModel.getLatitude(), locationModel.getLongitude());
                distancePoints.add(latLng);
            }
        }

        getElevationGain(distancePoints);

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

        runData.setDistance(avgDistance);
        runData.setTime(avgElapsedTime);

        System.out.println("return");
        return runData;

    }

    private void getElevationGain(List<LatLng> distancePoints) {
        for (LatLng point : distancePoints) {
            //spusti async task
            elevationService.getElevation(point.latitude, point.longitude);
        }
    }

    private double getAvgElevation(List<Double> elevations) {
        double elevationGain = 0;

        for (int i = 0; i < elevations.size() - 1; i++) {
            if (elevations.get(i + 1) > elevations.get(i)) {
                elevationGain = elevationGain + (elevations.get(i + 1) - elevations.get(i));
            }
        }
        return elevationGain / challengeList.size();
    }

    @Override
    public void processFinish(Double output) {
        System.out.println("process finish");
        elevations.add(output);
        if (elevations.size() == distancePoints.size()) {
            runData.setElevation(getAvgElevation(elevations));
            System.out.println("elev do rundata");
            databaseReferenceRunData = firebaseDatabase.getReference("user").child(currentUser.getUid()).child("runData");
            System.out.println("ukl8d8m");
            databaseReferenceRunData.setValue(runData);
        }
    }
}
