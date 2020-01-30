package cz.uhk.fim.runhk.activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import cz.uhk.fim.runhk.R;

public class DifficultyActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_difficulty);

        final double avgDistance = getIntent().getDoubleExtra("distance", 0);
        final long avgTime = getIntent().getLongExtra("time", 0);
        final double avgElevation = getIntent().getDoubleExtra("elevation", 0);
        final int avgCalories = getIntent().getIntExtra("calories", 0);
        final int weight = getIntent().getIntExtra("weight", 0);

        Button btnEasy = findViewById(R.id.btnEasyRun);
        btnEasy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = getIntent(avgDistance, avgTime, avgElevation, avgCalories, weight);
                startActivity(intent);
                finish();
            }
        });

        Button btnCustom = findViewById(R.id.btnCustomRun);
        btnCustom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = getIntent(avgDistance, avgTime, avgElevation, avgCalories, weight);
                startActivity(intent);
                finish();
            }
        });
    }

    @NonNull
    private Intent getIntent(double avgDistance, long avgTime, double avgElevation, int avgCalories, int weight) {
        Intent intent = new Intent(DifficultyActivity.this, GeneratedMapActivity.class);
        intent.putExtra("distance", avgDistance);
        intent.putExtra("time", avgTime);
        intent.putExtra("elevation", avgElevation);
        intent.putExtra("calories", avgCalories);
        intent.putExtra("weight", weight);
        return intent;
    }
}
