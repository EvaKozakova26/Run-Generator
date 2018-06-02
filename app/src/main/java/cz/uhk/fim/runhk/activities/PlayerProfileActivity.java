package cz.uhk.fim.runhk.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.location.Location;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import cz.uhk.fim.runhk.R;
import cz.uhk.fim.runhk.model.Player;

public class PlayerProfileActivity extends AppCompatActivity {

    FusedLocationProviderClient fusedLocationProviderClient;

    double lat;
    double lon;

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_profile);

        ProgressBar progressBar = findViewById(R.id.progress_exps);
        progressBar.setIndeterminate(false);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        Player player = new Player("bezdyjoe", "kozakev26@gmail.com", "Voldemort26", 10, 78);

        progressBar.setProgress(player.getExps());
        TextView textViewLevel = findViewById(R.id.textViewPlayerLevel);
        TextView textViewNick = findViewById(R.id.textViewPlayerNickname);

        textViewLevel.setText("Level " + player.getLevel());
        textViewNick.setText(player.getNickname());
        textViewNick.setTextSize(15);

        Button btnGo = findViewById(R.id.btnGo);
        btnGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                fusedLocationProviderClient.getLastLocation()
                        .addOnSuccessListener(PlayerProfileActivity.this, new OnSuccessListener<Location>() {
                            @Override
                            public void onSuccess(Location location) {

                                if (location == null) {
                                    lat = 50;
                                    lon = 50;
                                } else {
                                    lat = location.getLatitude();
                                    lon = location.getLongitude();
                                }

                            }
                        });

                Toast.makeText(PlayerProfileActivity.this, lat + " " + lon, Toast.LENGTH_LONG).show();

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Intent intent = new Intent(PlayerProfileActivity.this, MapsActivity.class);
                intent.putExtra("lat", (float) lat);
                intent.putExtra("lon", (float) lon);
                startActivity(intent);

            }
        });

        Button btnQuests = findViewById(R.id.btnQuestList);
        btnQuests.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PlayerProfileActivity.this, QuestsActivity.class);
                startActivity(intent);
            }
        });
    }
}
