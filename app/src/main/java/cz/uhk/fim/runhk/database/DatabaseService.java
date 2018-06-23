package cz.uhk.fim.runhk.database;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by kozak on 23.06.2018.
 */

public class DatabaseService {

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    FirebaseUser currentUser;

    public DatabaseService() {
        firebaseDatabase = FirebaseDatabase.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
    }

    public void saveDistancePoints() {
        databaseReference = firebaseDatabase.getReference("user").child(currentUser.getUid()).child("finished");

    }

}
