package cz.uhk.fim.runhk.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import cz.uhk.fim.runhk.R;

public class PlayerEditInfoActivity extends AppCompatActivity {

    FirebaseDatabase firebaseDatabase;
    FirebaseUser currentUser;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_edit_info);

        final EditText editTextNickname = findViewById(R.id.editTextNickname);

        Button btnDone = findViewById(R.id.btnEditingDone);
        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String nickname = String.valueOf(editTextNickname.getText());

                currentUser = FirebaseAuth.getInstance().getCurrentUser();
                firebaseDatabase = FirebaseDatabase.getInstance();
                databaseReference = firebaseDatabase.getReference("user").child(currentUser.getUid()).child("nickname");
                databaseReference.setValue(nickname);

                Intent intent = new Intent(PlayerEditInfoActivity.this, PlayerProfileActivity.class);
                finish();
                startActivity(intent);
            }
        });
    }
}
