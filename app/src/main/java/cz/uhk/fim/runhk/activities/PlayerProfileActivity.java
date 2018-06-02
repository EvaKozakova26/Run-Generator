package cz.uhk.fim.runhk.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import cz.uhk.fim.runhk.R;
import cz.uhk.fim.runhk.model.Player;

public class PlayerProfileActivity extends AppCompatActivity {

    double lat;
    double lon;

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_profile);

        ProgressBar progressBar = findViewById(R.id.progress_exps);
        progressBar.setIndeterminate(false);

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
