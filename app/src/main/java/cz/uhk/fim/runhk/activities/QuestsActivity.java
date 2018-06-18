package cz.uhk.fim.runhk.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import cz.uhk.fim.runhk.adapters.OnItemClickedInterface;
import cz.uhk.fim.runhk.adapters.QuestViewAdapter;
import cz.uhk.fim.runhk.R;
import cz.uhk.fim.runhk.fragments.DetailQuestFragment;
import cz.uhk.fim.runhk.model.Quest;

public class QuestsActivity extends AppCompatActivity implements OnItemClickedInterface {

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private FirebaseUser currentUser;

    private RecyclerView recyclerView;
    private QuestViewAdapter adapter;

    private RecyclerView.LayoutManager layoutManager;

    private List<Quest> questList;

    boolean isLandscape;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quests);
        recyclerView = findViewById(R.id.recyclerView);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("user").child(currentUser.getUid()).child("finished");

        layoutManager = new LinearLayoutManager(this); // kontext - odkaz na pozadovanoou tridu
        recyclerView.setLayoutManager(layoutManager);

        questList = new ArrayList<>();

        if (findViewById(R.id.fragmentDetailContainer) != null) {
            isLandscape = true;
        }

        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Quest quest = snapshot.getValue(Quest.class);
                    questList.add(quest);

                    adapter = new QuestViewAdapter(questList);
                    adapter.setOnItemClickedInterface(QuestsActivity.this);
                    recyclerView.setAdapter(adapter);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        databaseReference.addValueEventListener(postListener);



    }

    @Override
    public void onButtonClicked(int position) {
        Quest quest = questList.get(position);
        Toast.makeText(this, String.valueOf(quest.getDistance()), Toast.LENGTH_SHORT).show();

        if (isLandscape) {
            DetailQuestFragment detailQuestFragment = new DetailQuestFragment();
            Bundle bundle = new Bundle();
            bundle.putDouble("distance", quest.getDistance());
            detailQuestFragment.setArguments(bundle);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragmentDetailContainer, detailQuestFragment) // kam to chci a co
                    .commit();
        } else {
            Intent intent = new Intent(this, DetailSectionActivity.class);
            intent.putExtra("distance", quest.getDistance());
            startActivity(intent);
        }



    }
}
