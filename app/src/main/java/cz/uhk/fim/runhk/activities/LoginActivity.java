package cz.uhk.fim.runhk.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import cz.uhk.fim.runhk.R;
import cz.uhk.fim.runhk.model.Run;
import cz.uhk.fim.runhk.model.LocationModel;
import cz.uhk.fim.runhk.model.Player;
import cz.uhk.fim.runhk.model.RunData;
import cz.uhk.fim.runhk.utils.Constants;

public class LoginActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 123;

    private FirebaseUser currentUser;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        //pro testovani
       /* Button btnLogin = findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(
                        AuthUI.getInstance()
                                .createSignInIntentBuilder()
                                .setLogo(R.drawable.ic_person_black_24dp)      // Set logo drawable
                                .setTheme(R.style.Theme_AppCompat_Light)
                                .build(),
                        RC_SIGN_IN);
            }
        });*/

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            Intent intent = new Intent(LoginActivity.this, PlayerProfileActivity.class);
            startActivity(intent);
        } else {
            Button btnLogin = findViewById(R.id.btnLogin);
            btnLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivityForResult(
                            AuthUI.getInstance()
                                    .createSignInIntentBuilder()
                                    .setLogo(R.drawable.ic_person_black_24dp)      // Set logo drawable
                                    .setTheme(R.style.Theme_AppCompat_Light)
                                    .build(),
                            RC_SIGN_IN);
                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK) {
                // Successfully signed in
                currentUser = FirebaseAuth.getInstance().getCurrentUser();
                DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
                DatabaseReference userNameRef = rootRef.child("user").child(currentUser.getUid());
                ValueEventListener eventListener = new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (!dataSnapshot.exists()) {
                            firebaseDatabase = FirebaseDatabase.getInstance();
                            // ulozi uzivatele do db

                            ArrayList<LocationModel> distancePointsList = new ArrayList<>();
                            LocationModel locationModel = new LocationModel(0, 0);
                            distancePointsList.add(locationModel);

                            // ulozi pvni challenge do db
                            Run run = getDefaultRun(distancePointsList);

                            // ulozi hrace
                            Player player = createPlayer(run);
                            databaseReference = firebaseDatabase.getReference("user");
                            databaseReference.child(currentUser.getUid()).setValue(player);

                            RunData runData = createDefaultRunData();
                            databaseReference = firebaseDatabase.getReference("user").child(currentUser.getUid()).child("runData");
                            databaseReference.setValue(runData);


                            Intent intent = new Intent(LoginActivity.this, PlayerEditInfoActivity.class);
                            finish();
                            startActivity(intent);

                        } else {
                            Intent intent = new Intent(LoginActivity.this, PlayerProfileActivity.class);
                            finish();
                            startActivity(intent);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                };
                userNameRef.addListenerForSingleValueEvent(eventListener);
            } else {
                Toast.makeText(this, Constants.TRY_AGAIN, Toast.LENGTH_SHORT).show();

            }
        }
    }

    @NonNull
    private RunData createDefaultRunData() {
        RunData runData = new RunData();
        runData.setElevation(0);
        runData.setCalories(0);
        runData.setDistance(0);
        runData.setTime(0);
        return runData;
    }

    @NonNull
    private Player createPlayer(Run run) {
        Player player = new Player("", currentUser.getEmail(), "", 1, 10, new ArrayList<Run>());
        player.setRunToDo(run);
        player.setAge(0);
        player.setWeight(0);
        return player;
    }

    @NonNull
    private Run getDefaultRun(ArrayList<LocationModel> distancePointsList) {
        Run run = new Run();
        run.setDistanceToDo(1000);
        run.setExps(50);
        run.setFinished(false);
        run.setLevel(1);
        run.setDate("");
        run.setTime("");
        run.setDistancePoints(distancePointsList);
        return run;
    }
}
