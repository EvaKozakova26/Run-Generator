package cz.uhk.fim.runhk.database;

import android.support.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cz.uhk.fim.runhk.model.Run;
import cz.uhk.fim.runhk.model.LocationModel;
import cz.uhk.fim.runhk.model.Player;
import cz.uhk.fim.runhk.model.RunData;
import cz.uhk.fim.runhk.service.AsyncResponse;
import cz.uhk.fim.runhk.service.ElevationService;
import cz.uhk.fim.runhk.utils.LevelUtils;
import cz.uhk.fim.runhk.utils.MetsUtils;
import cz.uhk.fim.runhk.utils.DatabaseUtils;

public class RunDataHelper implements AsyncResponse {

    private FirebaseUser currentUser;
    private ElevationService elevationService;

    private RunDataProcessor runDataProcessor = new RunDataProcessor();

    private int currentQuestExps;
    private List<Double> elevations;
    private List<LocationModel> distancePoints;
    private Run finishedRun;
    private Player player;

    public void saveQuest(double distance, ArrayList<LocationModel> distancePointsList, String time, long elapsedTime) {
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        elevationService = new ElevationService();
        elevationService.delegate = this;
        elevations = new ArrayList<>();
        distancePoints = distancePointsList;
        getResult(distance, distancePointsList, time, elapsedTime);

    }

    private void getResult(final double distance, final ArrayList<LocationModel> distancePointsLocation, final String time, final long elapsedTime) {
        DatabaseReference userDatabaseReference = DatabaseUtils.getUserDatabaseReference();
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                player = dataSnapshot.getValue(Player.class);
                currentQuestExps = 100;
                finishedRun = getRun(distance, distancePointsLocation, time, elapsedTime);
                getElevationGain(distancePointsLocation);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
            }
        };
        userDatabaseReference.addListenerForSingleValueEvent(postListener);
    }

    @NonNull
    private Run getRun(double distance, ArrayList<LocationModel> distancePointsLocation, String time, long elapsedTime) {
        Run run = new Run();
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        Date date = new Date();
        run.setDate(dateFormat.format(date));
        run.setFinished(true);
        run.setDistance(distance);
        run.setDistancePoints(distancePointsLocation);
        run.setTime(time);
        run.setElaspedTime(elapsedTime);
        return run;
    }

    private void getElevationGain(List<LocationModel> distancePointsAll) {
        List<LocationModel> sortedDistancePoints = getSortedDistancePoints(distancePointsAll);

        for (LocationModel point : sortedDistancePoints) {
            //spusti async task
            elevationService.getElevation(point.latitude, point.longitude);
        }
    }

    private List<LocationModel> getSortedDistancePoints(List<LocationModel> distancePointsAll) {
        List<LocationModel> sortedDistancePoints = new ArrayList<>();
        for (int i = 0; i < distancePointsAll.size(); i += 2) {
            sortedDistancePoints.add(distancePointsAll.get(i));
        }
        distancePoints.clear();
        distancePoints.addAll(sortedDistancePoints);
        return sortedDistancePoints;
    }

    @Override
    public void processFinish(Double output) {
        elevations.add(output);
        if (elevations.size() == distancePoints.size()) {
            int elevationGain = getElevationGainForRoute();
            finishedRun.setElevationGain(elevationGain);
            finishedRun.setCaloriesBurnt(getCaloriesBurnt(player.getWeight(), finishedRun.getDistance(), (long) finishedRun.getElaspedTime(), elevationGain));

            DatabaseReference runDataReference = DatabaseUtils.getUserDatabaseReference().child(currentUser.getUid()).child("runData");
            ValueEventListener runDataListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    RunData runData = dataSnapshot.getValue(RunData.class);
                    int bonusExps = getBonusExps(finishedRun, runData);
                    finishedRun.setExps(bonusExps);
                    DatabaseReference databaseReferenceTemp = DatabaseUtils.getUserDatabaseReference().child(currentUser.getUid()).child("finished");
                    databaseReferenceTemp.push().setValue(finishedRun);
                    // nstavit hodnoty plejerovi
                    updatePlayer(bonusExps);
                    runDataProcessor.processAndSaveRunData(player.getWeight());
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };
            runDataReference.addListenerForSingleValueEvent(runDataListener);
        }
    }

    private int getBonusExps(Run finishedRun, RunData runData) {
        int bonusExps = 100;

        if (finishedRun.getDistance() > runData.getDistance()) {
            double distanceExps = 0.1 * (finishedRun.getDistance() - runData.getDistance());
            bonusExps = (int) (bonusExps + distanceExps);
        }

        if (finishedRun.getElevationGain() > runData.getElevation()) {
            double elevationExps = 0.2 * (finishedRun.getElevationGain() - runData.getElevation());
            bonusExps = (int) (bonusExps + elevationExps);
        }

        if (finishedRun.getElaspedTime() > runData.getTime()) {
            bonusExps = bonusExps + 100;

        }

        if (finishedRun.getCaloriesBurnt() > runData.getCalories()) {
            int caloriesExps = (int) (finishedRun.getCaloriesBurnt() - runData.getCalories());
            bonusExps = (bonusExps + caloriesExps);
        }

        return bonusExps;
    }


    private int getElevationGainForRoute() {
        int elevationGain = 0;
        for (int i = 0; i < elevations.size() - 1; i++) {
            if (elevations.get(i + 1) > elevations.get(i)) {
                elevationGain = (int) (elevationGain + (elevations.get(i + 1) - elevations.get(i)));
            }
        }
        return elevationGain;
    }

    public int getCaloriesBurnt(int weight, double distance, long elaspedTime, int elevationGain) {
        double METS = getMets(distance, elaspedTime);
        double duration = elaspedTime / 3600000.0;
        double result = ((1.05 * METS * duration * weight) + (1.25 * elevationGain));
        return (int) result;
    }

    private double getMets(double distance, long elaspedTime) {
        double distanceKm = distance / 1000;
        double elapsedTimeMins = elaspedTime / 60.0;
        double pace = elapsedTimeMins / distanceKm;
        return MetsUtils.getMETS(pace);
    }

    private void updatePlayer(final int bonusExps) {
        final DatabaseReference userDatabaseReference = DatabaseUtils.getUserDatabaseReference();
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Player player = dataSnapshot.getValue(Player.class);
                int finalExps = currentQuestExps + player.getExps() + bonusExps;
                userDatabaseReference.child("exps").setValue(finalExps);

                int playerLevel = player.getLevel();
                int currentLevel = LevelUtils.getCurrentLevel(finalExps, playerLevel);
                userDatabaseReference.child("level").setValue(currentLevel);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };
        userDatabaseReference.addListenerForSingleValueEvent(postListener);

    }

}
