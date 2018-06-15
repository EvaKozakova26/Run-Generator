package cz.uhk.fim.runhk.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import cz.uhk.fim.runhk.fragments.DetailQuestFragment;
import cz.uhk.fim.runhk.fragments.QuestListFragment;
import cz.uhk.fim.runhk.R;
import cz.uhk.fim.runhk.model.Player;

public class QuestsActivity extends AppCompatActivity implements QuestListFragment.OnItemSelectedInterface {

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference playerReference;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quests);

        QuestListFragment questListFragment = (QuestListFragment) getSupportFragmentManager().findFragmentById(R.id.fragmentList);
        questListFragment.setOnItemSelectedInterface(this);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        playerReference = firebaseDatabase.getReference("user").child(currentUser.getUid());


        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                Player player = dataSnapshot.getValue(Player.class);
                Toast.makeText(QuestsActivity.this, player.getEmail(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message

                // ...
            }
        };
        playerReference.addValueEventListener(postListener);

    }


    @Override
    public void onItemSelected(View view) {
            Intent intent = new Intent(this, DetailSectionActivity.class);
            intent.putExtra("section", view.getId());
            startActivity(intent);
        }
}
