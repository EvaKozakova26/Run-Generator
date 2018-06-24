package cz.uhk.fim.runhk.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cz.uhk.fim.runhk.R;
import cz.uhk.fim.runhk.adapters.ChallengeViewAdapter;
import cz.uhk.fim.runhk.adapters.OnItemClickedInterface;
import cz.uhk.fim.runhk.fragments.DetailChallengeFragment;
import cz.uhk.fim.runhk.model.Challenge;

public class ChallengesActivity extends AppCompatActivity implements OnItemClickedInterface {

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private FirebaseUser currentUser;

    private RecyclerView recyclerView;
    private ChallengeViewAdapter adapter;

    private RecyclerView.LayoutManager layoutManager;

    private List<Challenge> challengeList;

    boolean isLandscape;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_challenges);
        recyclerView = findViewById(R.id.recyclerView);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("user").child(currentUser.getUid()).child("finished");

        layoutManager = new LinearLayoutManager(this); // kontext - odkaz na pozadovanoou tridu
        recyclerView.setLayoutManager(layoutManager);

        challengeList = new ArrayList<>();

        if (findViewById(R.id.fragmentDetailContainer) != null) {
            isLandscape = true;
        }

        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Challenge challenge = snapshot.getValue(Challenge.class);
                    challengeList.add(challenge);

                }
                Collections.reverse(challengeList);
                adapter = new ChallengeViewAdapter(challengeList, isLandscape);
                adapter.setOnItemClickedInterface(ChallengesActivity.this);
                recyclerView.setAdapter(adapter);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        databaseReference.addValueEventListener(postListener);


    }

    @Override
    public void onButtonClicked(int position) {
        Challenge challenge = challengeList.get(position);
        Toast.makeText(this, String.valueOf(challenge.getDistance()), Toast.LENGTH_SHORT).show();

        if (isLandscape) {
            DetailChallengeFragment detailChallengeFragment = new DetailChallengeFragment();
            Bundle bundle = new Bundle();
            bundle.putDouble("distance", challenge.getDistance());
            bundle.putParcelableArrayList("points", challenge.getDistancePoints());
            bundle.putInt("exps", challenge.getExps());
            bundle.putString("time", challenge.getTime());
            detailChallengeFragment.setArguments(bundle);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragmentDetailContainer, detailChallengeFragment) // kam to chci a co
                    .commit();
        } else {
            Intent intent = new Intent(this, DetailSectionActivity.class);
            intent.putExtra("distance", challenge.getDistance());
            intent.putExtra("points", challenge.getDistancePoints());
            intent.putExtra("exps", challenge.getExps());
            intent.putExtra("time", challenge.getTime());
            startActivity(intent);
        }

    }
}
