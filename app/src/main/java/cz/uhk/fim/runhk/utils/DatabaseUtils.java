package cz.uhk.fim.runhk.utils;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DatabaseUtils {

    private static final String USER = "user";

    private DatabaseUtils() {
    }

    public static DatabaseReference getUserDatabaseReference() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        return firebaseDatabase.getReference(USER).child(currentUser.getUid());
    }

}
