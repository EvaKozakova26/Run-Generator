package cz.uhk.fim.runhk.database;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Random;

import cz.uhk.fim.runhk.model.Challenge;

public class DatabaseHelper {

    private FirebaseUser currentUser;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private DatabaseReference questReference;

    boolean finished;
    double distanceToDo;
    int exps;
    int currentQuestExps;

    public void saveQuest(double distance) {
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        finished = false;
        getVysledek(distance);
    }

    public boolean getVysledek(final double distance) {
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        questReference = firebaseDatabase.getReference("user").child(currentUser.getUid()).child("questToDo");

        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                Challenge challenge = dataSnapshot.getValue(Challenge.class);
                currentQuestExps = challenge.getExps();
                if (!finished) {
                    if (distance >= challenge.getDistanceToDo()) {

                        //...
                        // najit v dbquest, dát na true a uložit ho pod child("FINISHED");
                        challenge.setFinished(true);
                        challenge.setDistance(distance);
                        DatabaseReference databaseReferenceTemp = firebaseDatabase.getReference("user").child(currentUser.getUid()).child("finished");
                        databaseReferenceTemp.push().setValue(challenge);

                        // nstavit hodjnoty plejerovi
                        updatePlayer();

                        finished = true;

                        questReference.removeValue();
                        createQuest();

                    } else {
                        // nic se nestancem challenge zustane false a zavloa se jen hlaska, musíš to zkusit znovu :D
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
                databaseReference = firebaseDatabase.getReference().child("user").child(currentUser.getUid()).child("questToDo");
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

    private void updatePlayer() {
        final DatabaseReference databaseReferenceTemp = firebaseDatabase.getReference("user").child(currentUser.getUid()).child("exps");
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                databaseReferenceTemp.setValue(currentQuestExps + dataSnapshot.getValue(Integer.class));
                setLevel((dataSnapshot.getValue(Integer.class)) + currentQuestExps);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };
        databaseReferenceTemp.addListenerForSingleValueEvent(postListener);

            }

    private void setLevel(final int exps) {
        DatabaseReference databaseReferenceTemp = firebaseDatabase.getReference("user").child(currentUser.getUid()).child("level");
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final int level = dataSnapshot.getValue(Integer.class);
                DatabaseReference dbReference = firebaseDatabase.getReference("level").child(String.valueOf(level));
                ValueEventListener postListener = new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        int levelExps = dataSnapshot.getValue(Integer.class);
                        if (exps > levelExps) {
                            DatabaseReference levelPlayerReference = firebaseDatabase.getReference("user").child(currentUser.getUid()).child("level");
                            levelPlayerReference.setValue(level + 1);
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                };
                dbReference.addListenerForSingleValueEvent(postListener);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        databaseReferenceTemp.addListenerForSingleValueEvent(postListener);
    }

}
