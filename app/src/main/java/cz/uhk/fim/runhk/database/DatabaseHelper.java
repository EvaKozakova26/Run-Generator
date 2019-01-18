package cz.uhk.fim.runhk.database;

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
import java.util.HashMap;
import java.util.List;

import cz.uhk.fim.runhk.model.Challenge;
import cz.uhk.fim.runhk.model.LocationModel;
import cz.uhk.fim.runhk.model.Player;
import cz.uhk.fim.runhk.model.RunData;
import cz.uhk.fim.runhk.service.AsyncResponse;
import cz.uhk.fim.runhk.service.ElevationService;
import cz.uhk.fim.runhk.service.LevelService;

public class DatabaseHelper implements AsyncResponse {

    private FirebaseUser currentUser;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private DatabaseReference questReference;
    private ElevationService elevationService;

    ChallengeResultInterface challengeResultInterface;
    LevelService levelService;
    private RunDataProcessor runDataProcessor = new RunDataProcessor();

    private boolean finished;
    private double distanceToDo;
    private int exps;
    private int currentQuestExps;
    private List<Double> elevations;
    private List<LocationModel> distancePoints;
    private Challenge finishedChallenge;
    private Player player;
    private double runDistance;
    private RunData runData;

    public void saveQuest(double distance, ArrayList<LocationModel> distancePointsList, String time, long elapsedTime) {
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        elevationService = new ElevationService();
        elevationService.delegate = this;
        elevations = new ArrayList<>();
        distancePoints = distancePointsList;
        finished = false;
        runDistance = distance;
        getVysledek(distance, distancePointsList, time, elapsedTime);

    }

    public boolean getVysledek(final double distance, final ArrayList<LocationModel> distancePointsLocation, final String time, final long elapsedTime) {
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        questReference = firebaseDatabase.getReference("user").child(currentUser.getUid());

        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                player = dataSnapshot.getValue(Player.class);
                Challenge challenge = new Challenge();
                currentQuestExps = 100;

                finished = true;
                DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
                Date date = new Date();
                System.out.println(dateFormat.format(date));

                challenge.setDate(dateFormat.format(date));
                challenge.setFinished(true);
                challenge.setDistance(distance);
                challenge.setDistancePoints(distancePointsLocation);
                challenge.setTime(time);
                challenge.setElaspedTime(elapsedTime);
                finishedChallenge = challenge;
                getElevationGain(distancePointsLocation);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
            }
        };
        questReference.addListenerForSingleValueEvent(postListener);
        return finished;
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
            finishedChallenge.setElevationGain(elevationGain);
            finishedChallenge.setCaloriesBurnt(getCaloriesBurnt(player.getWeight(), finishedChallenge.getDistance(), (long) finishedChallenge.getElaspedTime(), elevationGain));

            DatabaseReference runDataReference = firebaseDatabase.getReference("user").child(currentUser.getUid()).child("runData");
            ValueEventListener runDataListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    RunData runData = dataSnapshot.getValue(RunData.class);
                    int bonusExps = getBonusExps(finishedChallenge, runData);
                    finishedChallenge.setExps(bonusExps);
                    DatabaseReference databaseReferenceTemp = firebaseDatabase.getReference("user").child(currentUser.getUid()).child("finished");
                    databaseReferenceTemp.push().setValue(finishedChallenge);
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

    private int getBonusExps(Challenge finishedChallenge, RunData runData) {
        int bonusExps = 100;

        if (finishedChallenge.getDistance() > runData.getDistance()) {
            double distanceExps = 0.1 * (finishedChallenge.getDistance() - runData.getDistance());
            bonusExps = (int) (bonusExps + distanceExps);
        }

        if (finishedChallenge.getElevationGain() > runData.getElevation()) {
            double elevationExps = 0.2 * (finishedChallenge.getElevationGain() - runData.getElevation());
            bonusExps = (int) (bonusExps + elevationExps);
        }

        if (finishedChallenge.getElaspedTime() > runData.getTime()) {
            bonusExps = bonusExps + 100;

        }

        if (finishedChallenge.getCaloriesBurnt() > runData.getCalories()) {
            int caloriesExps = (int) (finishedChallenge.getCaloriesBurnt() - runData.getCalories());
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
        double METS = 0;
        double distanceKm = distance / 1000;
        double elapsedTimeMins = elaspedTime / 60;
        double pace = elapsedTimeMins / distanceKm;

        // TODO / do nejake strukturz...
        if (pace < 3.4) METS = 18;
        if (pace < 3.75 && pace > 3.4) METS = 16;
        if (pace < 4 && pace > 3.75) METS = 15;
        if (pace < 4.4 && pace > 4) METS = 14;
        if (pace < 4.7 && pace > 4.4) METS = 13.5;
        if (pace < 5 && pace > 4.7) METS = 12.5;
        if (pace < 5.3 && pace > 5) METS = 11.5;
        if (pace < 5.6 && pace > 5.3) METS = 11;
        if (pace < 6.25 && pace > 5.6) METS = 10;
        if (pace < 7.2 && pace > 6.25) METS = 9;
        if (pace > 7.2) METS = 8;

        return METS;
    }

    private void updatePlayer(final int bonusExps) {
        final DatabaseReference databaseReferenceTemp = firebaseDatabase.getReference("user").child(currentUser.getUid());
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Player player = dataSnapshot.getValue(Player.class);
                int finalExps = currentQuestExps + player.getExps() + bonusExps;
                databaseReferenceTemp.child("exps").setValue(finalExps);

                int playerLevel = player.getLevel();
                levelService = new LevelService();
                HashMap<Integer, Integer> levelMap = levelService.getLevelMap();

                try {
                    if (finalExps > levelMap.get(playerLevel)) {
                        databaseReferenceTemp.child("level").setValue(playerLevel + 1);
                    }
                    if (finalExps > levelMap.get(playerLevel + 1)) {
                        databaseReferenceTemp.child("level").setValue(playerLevel + 2);
                    }
                    if (finalExps > levelMap.get(playerLevel + 2)) {
                        databaseReferenceTemp.child("level").setValue(playerLevel + 3);
                    }
                    if (finalExps > levelMap.get(playerLevel + 3)) {
                        databaseReferenceTemp.child("level").setValue(playerLevel + 4);
                    }
                    if (finalExps > levelMap.get(playerLevel + 5)) {
                        databaseReferenceTemp.child("level").setValue(playerLevel + 6);
                    }
                    if (finalExps > levelMap.get(playerLevel + 7)) {
                        databaseReferenceTemp.child("level").setValue(playerLevel + 8);
                    }
                    if (finalExps > levelMap.get(playerLevel + 9)) {
                        databaseReferenceTemp.child("level").setValue(playerLevel + 9);
                    }
                } catch (RuntimeException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };
        databaseReferenceTemp.addListenerForSingleValueEvent(postListener);

            }

    private void setLevel(final int exps) {
        final DatabaseReference databaseReferenceTemp = firebaseDatabase.getReference("user").child(currentUser.getUid()).child("level");
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int playerLevel = dataSnapshot.getValue(Integer.class);

                levelService = new LevelService();
                HashMap<Integer, Integer> levelMap = levelService.getLevelMap();
                String maxLevelExps = levelMap.get(playerLevel).toString();
                int levelExps = Integer.parseInt(maxLevelExps);

                if (exps > levelExps) {
                    databaseReferenceTemp.setValue(playerLevel + 1);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        databaseReferenceTemp.addListenerForSingleValueEvent(postListener);
    }

    public void setChallengeResultInterface(ChallengeResultInterface challengeResultInterface) {
        this.challengeResultInterface = challengeResultInterface;
    }

    public interface ChallengeResultInterface {
        void onChallengeResultCalled(boolean finished);
    }

}
