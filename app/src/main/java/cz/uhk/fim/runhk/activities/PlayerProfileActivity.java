package cz.uhk.fim.runhk.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import cz.uhk.fim.runhk.R;
import cz.uhk.fim.runhk.service.helper.utils.LevelUtils;
import cz.uhk.fim.runhk.model.Player;
import cz.uhk.fim.runhk.model.RunData;

public class PlayerProfileActivity extends NavigationDrawerActivity {

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private FirebaseUser currentUser;
    private ProgressBar progressBar;

    private ImageView imageViewProfile;

    private FirebaseStorage storage;
    private StorageReference imgReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        getLayoutInflater().inflate(R.layout.activity_player_profile, frameLayout);
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();

        progressBar = findViewById(R.id.progress_exps);
        progressBar.setIndeterminate(false);

        imageViewProfile = findViewById(R.id.imgProfile);
        storage = FirebaseStorage.getInstance();

        setPlayerStatsAndInfo();

        Button btnQuests = findViewById(R.id.btnQuestList);
        btnQuests.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PlayerProfileActivity.this, RunsListActivity.class);
                startActivity(intent);
            }
        });

        Button btnRankings = findViewById(R.id.btnPlayersList);
        btnRankings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PlayerProfileActivity.this, RankingActivity.class);
                startActivity(intent);
            }
        });

        Button btnGenerate = findViewById(R.id.btnGenerateChallenge);
        btnGenerate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                databaseReference = firebaseDatabase.getReference("user").child(currentUser.getUid()).child("runData");
                ValueEventListener eventListener = new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        RunData runData = dataSnapshot.getValue(RunData.class);
                        System.out.println("intents");
                        Intent intent = new Intent(PlayerProfileActivity.this, DifficultyActivity.class);
                        intent.putExtra("distance", runData.getDistance());
                        intent.putExtra("time", runData.getTime());
                        intent.putExtra("elevation", runData.getElevation());
                        intent.putExtra("calories", runData.getCalories());
                        intent.putExtra("weight", runData.getPlayerWeight());
                        startActivity(intent);

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                };
                databaseReference.addValueEventListener(eventListener);
            }
        });

        Button btnMyData = findViewById(R.id.btnMyData);
        btnMyData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                databaseReference = firebaseDatabase.getReference("user").child(currentUser.getUid()).child("runData");
                ValueEventListener eventListener = new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        RunData runData = dataSnapshot.getValue(RunData.class);
                        Intent intent = new Intent(PlayerProfileActivity.this, MyRunDataActivity.class);
                        intent.putExtra("distance", runData.getDistance());
                        intent.putExtra("time", runData.getTime());
                        intent.putExtra("elevation", runData.getElevation());
                        intent.putExtra("calories", runData.getCalories());
                        startActivity(intent);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                };
                databaseReference.addValueEventListener(eventListener);
            }
        });

    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    private void setPlayerStatsAndInfo() {
        databaseReference = firebaseDatabase.getReference("user").child(currentUser.getUid());
        ValueEventListener posListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Player player = dataSnapshot.getValue(Player.class);

                HashMap<Integer, Integer> levelMap = LevelUtils.getLevelMap();
                int maxLevelExps =  levelMap.get(player.getLevel());
                progressBar.setMax(maxLevelExps);

                progressBar.setProgress(player.getExps());
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
        databaseReference.addValueEventListener(posListener);

        final DatabaseReference databaseReferencetemp = databaseReference.child("isMale");
        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean isMale = dataSnapshot.getValue(Boolean.class);

                if (isMale) {
                    imgReference = storage.getReference().child("images/male.png");
                    imgReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Picasso.get().load(uri).into(imageViewProfile);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    });

                } else {
                    imgReference = storage.getReference().child("images/female.png");
                    imgReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Picasso.get().load(uri).into(imageViewProfile);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        databaseReferencetemp.addListenerForSingleValueEvent(listener);

    }
}
