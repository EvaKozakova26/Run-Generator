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
import java.util.Objects;
import java.util.Random;

import cz.uhk.fim.runhk.model.Challenge;
import cz.uhk.fim.runhk.model.LocationModel;
import cz.uhk.fim.runhk.model.Player;

public class DatabaseHelper {

    private FirebaseUser currentUser;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private DatabaseReference questReference;

    ChallengeResultInterface challengeResultInterface;
    LevelService levelService;

    private boolean finished;
    private double distanceToDo;
    private int exps;
    private int currentQuestExps;

    public void saveQuest(double distance, ArrayList<LocationModel> distancePointsList, String time) {
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        finished = false;
        getVysledek(distance, distancePointsList, time);
    }

    public boolean getVysledek(final double distance, final ArrayList<LocationModel> distancePointsLocation, final String time) {
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        questReference = firebaseDatabase.getReference("user").child(currentUser.getUid()).child("challengeToDo");

        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                Challenge challenge = dataSnapshot.getValue(Challenge.class);
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
                        DatabaseReference databaseReferenceTemp = firebaseDatabase.getReference("user").child(currentUser.getUid()).child("finished");
                        databaseReferenceTemp.push().setValue(challenge);


                        double distanceBonus = distance - challenge.getDistanceToDo();
                        int bonusExps = (int) (distanceBonus * 0.1);
                        // nstavit hodjnoty plejerovi
                        updatePlayer(bonusExps);
                        questReference.removeValue();
                        createQuest();
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
