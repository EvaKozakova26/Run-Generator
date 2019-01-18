package cz.uhk.fim.runhk.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import cz.uhk.fim.runhk.R;

public class DifficultyActivity extends AppCompatActivity {
    private double avgDistance;
    private long avgTime;
    private double avgElevation;
    private int avgCalories;
    private int weight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_difficulty);

        avgDistance = getIntent().getDoubleExtra("distance", 0);
        avgTime = getIntent().getLongExtra("time", 0);
        avgElevation = getIntent().getDoubleExtra("elevation", 0);
        avgCalories = getIntent().getIntExtra("calories", 0);
        weight = getIntent().getIntExtra("weight", 0);

        Button btnEasy = findViewById(R.id.btnEasyRun);
        btnEasy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DifficultyActivity.this, GeneratedMapActivity.class);
                intent.putExtra("distance", avgDistance);
                intent.putExtra("time", avgTime);
                intent.putExtra("elevation", avgElevation);
                intent.putExtra("calories", avgCalories);
                intent.putExtra("weight", weight);
                startActivity(intent);
                finish();
            }
        });

        Button btnCustom = findViewById(R.id.btnCustomRun);
        btnCustom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DifficultyActivity.this, MapsActivity.class);
                intent.putExtra("distance", avgDistance);
                intent.putExtra("time", avgTime);
                intent.putExtra("elevation", avgElevation);
                intent.putExtra("calories", avgCalories);
                intent.putExtra("weight", weight);
                startActivity(intent);
                finish();
            }
        });
    }
}
