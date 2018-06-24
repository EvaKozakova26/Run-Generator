package cz.uhk.fim.runhk.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cz.uhk.fim.runhk.R;
import cz.uhk.fim.runhk.adapters.RankingsViewAdapter;
import cz.uhk.fim.runhk.model.Player;

public class RankingActivity extends AppCompatActivity {

    private FirebaseDatabase firebaseDatabase;

    private RecyclerView recyclerView;
    private RankingsViewAdapter adapter;

    private RecyclerView.LayoutManager layoutManager;

    private List<Player> playerList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ranking);

        recyclerView = findViewById(R.id.recyclerViewRank);
        firebaseDatabase = FirebaseDatabase.getInstance();

        Query query = firebaseDatabase.getReference("user").orderByChild("exps");

        layoutManager = new LinearLayoutManager(this); // kontext - odkaz na pozadovanoou tridu
        recyclerView.setLayoutManager(layoutManager);

        playerList = new ArrayList<>();

        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Player player = snapshot.getValue(Player.class);
                    playerList.add(player);
                }
                List<Player> playerListReverse = playerList;
                Collections.reverse(playerListReverse);
                adapter = new RankingsViewAdapter(playerListReverse);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        query.addValueEventListener(postListener);
    }
}
