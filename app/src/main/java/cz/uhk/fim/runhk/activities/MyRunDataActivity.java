package cz.uhk.fim.runhk.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import cz.uhk.fim.runhk.R;
import cz.uhk.fim.runhk.database.RunDataProcessor;

public class MyRunDataActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_run_data);

        Intent intent = getIntent();
        double distance = intent.getDoubleExtra("distance", 0);
        long time = intent.getLongExtra("time", 0);
        double elevation = intent.getDoubleExtra("elevation", 0);
        int calories = intent.getIntExtra("calories", 0);

        final EditText txtRunDistance = findViewById(R.id.editTextDistance);
        txtRunDistance.setText(String.format("%.2f", distance / 1000));

        double elapsedTimeMins = time / 60.0;
        double pace = elapsedTimeMins / distance;
        double decimals = pace % 1;
        int seconds = (int) (decimals * 60);
        int minutes = (int) pace;

        EditText txtDataTime = findViewById(R.id.textViewTime);
        txtDataTime.setText((minutes) + ":" + (seconds));

        EditText txtDataElevation = findViewById(R.id.textViewElevation);
        txtDataElevation.setText((elevation) + " m");

        EditText txtDataCalories = findViewById(R.id.textViewCalories);
        txtDataCalories.setText((calories) + " kcals");

    }

}
