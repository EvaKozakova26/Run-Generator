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
import cz.uhk.fim.runhk.model.Run;

public class RunsListActivity extends AppCompatActivity implements OnItemClickedInterface {

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private FirebaseUser currentUser;

    private RecyclerView recyclerView;
    private ChallengeViewAdapter adapter;

    private RecyclerView.LayoutManager layoutManager;

    private List<Run> runList;

    boolean isLandscape;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_challenges);
        recyclerView = findViewById(R.id.recyclerView);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        //TODO refactor - used more times
        databaseReference = firebaseDatabase.getReference("user").child(currentUser.getUid()).child("finished");

        layoutManager = new LinearLayoutManager(this); // kontext - odkaz na pozadovanoou tridu
        recyclerView.setLayoutManager(layoutManager);

        runList = new ArrayList<>();

        if (findViewById(R.id.fragmentDetailContainer) != null) {
            isLandscape = true;
        }

        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Run run = snapshot.getValue(Run.class);
                    runList.add(run);

                }
                Collections.reverse(runList);
                adapter = new ChallengeViewAdapter(runList, isLandscape);
                adapter.setOnItemClickedInterface(RunsListActivity.this);
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
        Run run = runList.get(position);
        Toast.makeText(this, String.valueOf(run.getDistance()), Toast.LENGTH_SHORT).show();

        if (isLandscape) {
            DetailChallengeFragment detailChallengeFragment = new DetailChallengeFragment();
            Bundle bundle = new Bundle();
            bundle.putDouble("distance", run.getDistance());
            bundle.putInt("calories", (int) run.getCaloriesBurnt());
            bundle.putInt("elevation", (int) run.getElevationGain());
            bundle.putParcelableArrayList("points", run.getDistancePoints());
            bundle.putInt("exps", run.getExps());
            bundle.putString("time", run.getTime());
            detailChallengeFragment.setArguments(bundle);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragmentDetailContainer, detailChallengeFragment) // kam to chci a co
                    .commit();
        } else {
            Intent intent = new Intent(this, DetailSectionActivity.class);
            intent.putExtra("distance", run.getDistance());
            intent.putExtra("points", run.getDistancePoints());
            intent.putExtra("exps", run.getExps());
            intent.putExtra("time", run.getTime());
            intent.putExtra("calories", run.getCaloriesBurnt());
            intent.putExtra("elevation", run.getElevationGain());
            startActivity(intent);
        }

    }
}
