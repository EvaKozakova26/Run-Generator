package cz.uhk.fim.runhk.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import cz.uhk.fim.runhk.R;

public class NewChallengeDetailActivity extends AppCompatActivity {

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    FirebaseUser currentUser;

    private double distanceToDo;
    private TextView txtViewDisToDo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_challenge_detail);

        firebaseDatabase = FirebaseDatabase.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        txtViewDisToDo = findViewById(R.id.textViewDistanceToDo);


        databaseReference = firebaseDatabase.getReference("user").child(currentUser.getUid()).child("challengeToDo").child("distanceToDo");
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                distanceToDo = dataSnapshot.getValue(Double.class);
                txtViewDisToDo.setText(String.format("%.2f", distanceToDo) + " metres");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        databaseReference.addListenerForSingleValueEvent(postListener);

        Button btnStartChallenge = findViewById(R.id.btnStartChallenge);
        btnStartChallenge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(NewChallengeDetailActivity.this, MapsActivity.class);
                finish();
                startActivity(intent);
            }
        });

    }

}
