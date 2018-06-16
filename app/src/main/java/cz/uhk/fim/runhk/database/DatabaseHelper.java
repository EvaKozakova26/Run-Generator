package cz.uhk.fim.runhk.database;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Random;

import cz.uhk.fim.runhk.model.Quest;

public class DatabaseHelper {

    private FirebaseUser currentUser;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private DatabaseReference questReference;

    boolean finished;
    boolean getVysledekDone;
    double distanceToDo;
    int exps;

    public void saveQuest(double distance) {
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
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
                Quest quest = dataSnapshot.getValue(Quest.class);

                if (!finished) {
                    if (distance >= quest.getDistanceToDo()) {

                        //...
                        // najit v dbquest, dát na true a uložit ho pod child("FINISHED");
                        quest.setFinished(true);
                        quest.setDistance(distance);
                        DatabaseReference databaseReferenceTemp = firebaseDatabase.getReference("user").child(currentUser.getUid()).child("finished");
                        databaseReferenceTemp.setValue(quest);

                        // nstavit hodjnoty plejerovi
                        updatePlayer();

                        // createQuest();
                        System.out.println("vysledek je true");
                        finished = true;

                        questReference.removeValue();
                        createQuest();

                    } else {
                        // nic se nestancem quest zustane false a zavloa se jen hlaska, musíš to zkusit znovu :D
                        System.out.println("vysledek je false");
                        finished = false;
                    }

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message

                // ...
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
                /////////////////////////

                exps = (int) (distanceToDo / 10);


                Quest currentQuestToDo = new Quest();
                currentQuestToDo.setDistanceToDo(distanceToDo);
                currentQuestToDo.setExps(exps);

                firebaseDatabase = FirebaseDatabase.getInstance();
                databaseReference = firebaseDatabase.getReference().child("user").child(currentUser.getUid()).child("questToDo");
                databaseReference.setValue(currentQuestToDo);


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
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("user").child(currentUser.getUid()).child("finished");

        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Quest quest = dataSnapshot.getValue(Quest.class);

                DatabaseReference databaseReferenceTemp = firebaseDatabase.getReference("user").child(currentUser.getUid()).child("exps");
                databaseReferenceTemp.setValue(quest.getExps());

                ValueEventListener postListener = new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        int exps = dataSnapshot.getValue(Integer.class);
                        if (exps >= 50) {
                            DatabaseReference databaseReferenceTemp = firebaseDatabase.getReference("user").child(currentUser.getUid()).child("level");
                            databaseReferenceTemp.setValue(2);
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                };
                databaseReferenceTemp.addListenerForSingleValueEvent(postListener);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message

                // ...
            }
        };
        databaseReference.addListenerForSingleValueEvent(postListener);


    }

}
