package cz.uhk.fim.runhk.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import cz.uhk.fim.runhk.R;
import cz.uhk.fim.runhk.model.Player;

public class PlayerProfileActivity extends AppCompatActivity {

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    FirebaseUser currentUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_profile);
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();

        final ProgressBar progressBar = findViewById(R.id.progress_exps);
        progressBar.setIndeterminate(false);

        databaseReference = firebaseDatabase.getReference("user").child(currentUser.getUid());
        ValueEventListener posListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Player player = dataSnapshot.getValue(Player.class);
                progressBar.setProgress(player.getExps());

                DatabaseReference databaseReferenceTemp = firebaseDatabase.getReference("level").child(String.valueOf(player.getLevel()));
                ValueEventListener posListener = new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        int levelExps = dataSnapshot.getValue(Integer.class);
                        progressBar.setMax(levelExps);

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                };
                databaseReferenceTemp.addListenerForSingleValueEvent(posListener);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        databaseReference.addListenerForSingleValueEvent(posListener);





        databaseReference = firebaseDatabase.getReference("user").child(currentUser.getUid());

        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Player player = dataSnapshot.getValue(Player.class);

                TextView textViewLevel = findViewById(R.id.textViewPlayerLevel);
                TextView textViewNick = findViewById(R.id.textViewPlayerNickname);
                textViewNick.setTextSize(15);

                textViewLevel.setText("Level " + player.getLevel());
                textViewNick.setText(player.getNickname());

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        databaseReference.addListenerForSingleValueEvent(postListener);




        Button btnGo = findViewById(R.id.btnGo);
        btnGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PlayerProfileActivity.this, MapsActivity.class);
                startActivity(intent);
            }
        });

        Button btnQuests = findViewById(R.id.btnQuestList);
        btnQuests.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PlayerProfileActivity.this, QuestsActivity.class);
                startActivity(intent);
            }
        });
    }
}
