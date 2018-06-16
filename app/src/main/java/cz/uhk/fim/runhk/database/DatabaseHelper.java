package cz.uhk.fim.runhk.database;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import cz.uhk.fim.runhk.model.Quest;

public class DatabaseHelper {

    private FirebaseUser currentUser;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private DatabaseReference questReference;

    boolean finished;
    boolean getVysledekDone;

    public void saveQuest(double distance) {
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
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
        questReference.addValueEventListener(postListener);
        return finished;
    }

    public void createQuest() {
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        Quest currentQuest = new Quest();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference().child("quest").child("current");
        databaseReference.child(currentUser.getUid()).setValue(currentQuest);
    }

    private void updatePlayer() {
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("user").child(currentUser.getUid());
        databaseReference.child("exps").setValue(10);

    }

/*
    private void setQuestFinished() {
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        questReference = firebaseDatabase.getReference("user").child(currentUser.getUid()).child("questToDo");


        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                Quest quest = dataSnapshot.getValue(Quest.class);
                quest.setFinished(true);


                // nasetuju tam finished
                DatabaseReference databaseReferenceTemp = firebaseDatabase.getReference("user").child(currentUser.getUid()).child("finished");
                databaseReferenceTemp.setValue(quest);

                questReference.removeValue(); //smaze quest to do

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message

                // ...
            }
        };
        questReference.addValueEventListener(postListener);
    }
*/

 /*   private double getDistanceToDo() {
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        questReference = firebaseDatabase.getReference("user").child(currentUser.getUid()).child("questToDo");


        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                Quest quest = dataSnapshot.getValue(Quest.class);
                distanceToDo = quest.getDistance();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message

                // ...
            }
        };
        questReference.addValueEventListener(postListener);
        return distanceToDo;
    }*/
/*
    private double getCurrentDistance() {
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        questReference = firebaseDatabase.getReference("user").child(currentUser.getUid()).child("current");


        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                Quest quest = dataSnapshot.getValue(Quest.class);
                currentDistance = quest.getDistance();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message

                // ...
            }
        };
        questReference.addValueEventListener(postListener);
        return currentDistance;
    }*/
}
