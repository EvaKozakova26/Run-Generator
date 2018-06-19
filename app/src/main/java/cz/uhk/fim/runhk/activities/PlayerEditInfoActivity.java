package cz.uhk.fim.runhk.activities;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import cz.uhk.fim.runhk.R;

public class PlayerEditInfoActivity extends AppCompatActivity {

    FirebaseDatabase firebaseDatabase;
    FirebaseUser currentUser;
    DatabaseReference databaseReference;

    ImageView imgFemale;
    ImageView imgMale;

    StorageReference femaleRefImg;
    StorageReference maleRefImg;

    private boolean isMale;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_edit_info);

        FirebaseStorage storage = FirebaseStorage.getInstance();

        femaleRefImg = storage.getReference().child("images/female.png");
        maleRefImg = storage.getReference().child("images/male.png");
        imgFemale = findViewById(R.id.imgViewFemale);
        imgMale = findViewById(R.id.imgViewMale);
        imgFemale.setAlpha(0.5f);
        imgMale.setAlpha(0.5f);

        createProfilePictures();

        imgFemale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isMale = false;
                imgFemale.setAlpha(1.0f);
                imgMale.setAlpha(0.5f);
            }
        });

        imgMale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isMale = true;
                imgFemale.setAlpha(0.5f);
                imgMale.setAlpha(1.0f);
            }
        });

        final EditText editTextNickname = findViewById(R.id.editTextNickname);

        Button btnDone = findViewById(R.id.btnEditingDone);
        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String nickname = String.valueOf(editTextNickname.getText());

                currentUser = FirebaseAuth.getInstance().getCurrentUser();
                firebaseDatabase = FirebaseDatabase.getInstance();
                databaseReference = firebaseDatabase.getReference("user").child(currentUser.getUid());
                databaseReference.child("nickname").setValue(nickname);
                databaseReference.child("isMale").setValue(isMale);


                Intent intent = new Intent(PlayerEditInfoActivity.this, PlayerProfileActivity.class);
                finish();
                startActivity(intent);
            }
        });

    }

    private void createProfilePictures() {
        femaleRefImg.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(imgFemale);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });

        maleRefImg.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(imgMale);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //
            }
        });
    }
}
