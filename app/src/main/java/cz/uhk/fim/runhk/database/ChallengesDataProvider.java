package cz.uhk.fim.runhk.database;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import cz.uhk.fim.runhk.model.Challenge;
import cz.uhk.fim.runhk.model.RunData;

/**
 * Vytahne vsechny behy uzivatele, zpracuje data a posle do MapsActivity data pro vygenerovani trasy
 */
public class ChallengesDataProvider {

    private FirebaseUser currentUser;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private List<Challenge> challengeList;
    public RunData runData = new RunData();

    public ChallengesDataProvider() {
    }

    public void getAllChallenges() {
        challengeList = new ArrayList<>();
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
        List<Integer> elapsedTimeList = new ArrayList<>();
        List<Double> distances = new ArrayList<>();
        for (Challenge challenge : challengeList) {
            elapsedTimeList.add(challenge.getElaspedTime());
            distances.add(challenge.getDistance());
        }

        int sumTime = 0;
        for (int i = 0; i < elapsedTimeList.size(); i++) {
            sumTime = sumTime + elapsedTimeList.get(i);
        }
        int avgElapsedTime = sumTime / elapsedTimeList.size();

        double sumDistance = 0;
        for (int i = 0; i < distances.size(); i++) {
            sumDistance = sumDistance + distances.get(i);
        }
        double avgDistance = sumDistance / distances.size();

        runData.setDistance(avgDistance);
        runData.setTime(avgElapsedTime);

        return runData;

    }


}
