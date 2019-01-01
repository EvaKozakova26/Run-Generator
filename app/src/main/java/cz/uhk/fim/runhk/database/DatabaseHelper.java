package cz.uhk.fim.runhk.database;

import com.google.android.gms.maps.model.LatLng;
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
import java.util.Random;
import java.util.concurrent.TimeUnit;

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
    private RunDataProvider runDataProvider = new RunDataProvider();

    private boolean finished;
    private double distanceToDo;
    private int exps;
    private int currentQuestExps;
    private List<Double> elevations;
    private List<LocationModel> distancePoints;
    private Challenge finishedChallenge;
    private Player player;
    private double runDistance;

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
                Challenge challenge = player.getChallengeToDo();
                currentQuestExps = challenge.getExps();
                if (!finished) {
                    if (distance >= challenge.getDistanceToDo()) {
                        finished = true;
                        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
                        Date date = new Date();
                        System.out.println(dateFormat.format(date));

                        // najit v dbquest, dát na true a uložit ho pod child("FINISHED");
                        challenge.setDate(dateFormat.format(date));
                        challenge.setFinished(true);
                        challenge.setDistance(distance);
                        challenge.setDistancePoints(distancePointsLocation);
                        challenge.setTime(time);
                        challenge.setElaspedTime(elapsedTime);
                        finishedChallenge = challenge;

                        getElevationGain(distancePointsLocation);
                    } else {
                        finished = false;
                    }

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
            }
        };
        questReference.addListenerForSingleValueEvent(postListener);
        return finished;
    }

    private void getElevationGain(List<LocationModel> distancePoints) {
        for (LocationModel point : distancePoints) {
            //spusti async task
            elevationService.getElevation(point.latitude, point.longitude);
        }
    }

    @Override
    public void processFinish(Double output) {
        elevations.add(output);
        if (elevations.size() == distancePoints.size()) {
            //TODO teprve ted ukladat vse
            int elevationGain = getElevationGainForRoute();
            finishedChallenge.setElevationGain(elevationGain);

            //TODO vyppocitat elevation gain uz zde a oite teprve spocitat kalorie
            finishedChallenge.setCaloriesBurnt(getCaloriesBurnt(player.getWeight(), finishedChallenge.getDistance(), finishedChallenge.getElaspedTime(), elevationGain));
            DatabaseReference databaseReferenceTemp = firebaseDatabase.getReference("user").child(currentUser.getUid()).child("finished");
            databaseReferenceTemp.push().setValue(finishedChallenge);

            //TODO dopocitat expy
            /*double distanceBonus = runDistance - challenge.getDistanceToDo();
            int bonusExps = (int) (distanceBonus * 0.1);*/
            // nstavit hodnoty plejerovi
            updatePlayer(0);
            questReference.child("challengeToDo'").removeValue();
            createQuest();
            runDataProvider.processAndSaveRunData();
        }
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

    //TODO elevationGain...
    private int getCaloriesBurnt(int weight, double distance, long elaspedTime, int elevationGain) {
        double METS = getMets(distance, elaspedTime);
        double duration = elaspedTime / 3600000.0;
        return (int) ((1.05 * METS * duration * weight) + (1.25 * elevationGain));
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


    private void createQuest() {
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference().child("user").child(currentUser.getUid()).child("level");

        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                int level = dataSnapshot.getValue(Integer.class);
                Random random = new Random();

                if (level >= 1 && level <= 5) {
                    distanceToDo = random.nextInt(1000) + 500;
                }
                if (level >= 6 && level <= 10) {
                    distanceToDo = random.nextInt(1500) + 1500;
                }
                if (level >= 11 && level <= 15) {
                    distanceToDo = random.nextInt(3000) + 3000;
                }
                if (level >= 16 && level <= 20) {
                    distanceToDo = random.nextInt(6000) + 6000;
                }

                exps = (int) (distanceToDo / 10);

                Challenge currentChallengeToDo = new Challenge();
                currentChallengeToDo.setDistanceToDo(distanceToDo);
                currentChallengeToDo.setExps(exps);

                firebaseDatabase = FirebaseDatabase.getInstance();
                databaseReference = firebaseDatabase.getReference().child("user").child(currentUser.getUid()).child("challengeToDo");
                databaseReference.setValue(currentChallengeToDo);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message

                // ...
            }
        };
        databaseReference.addListenerForSingleValueEvent(postListener);
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
