package cz.uhk.fim.runhk.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import cz.uhk.fim.runhk.R;
import cz.uhk.fim.runhk.database.RunDataProvider;

public class MyRunDataActivity extends AppCompatActivity {

    private RunDataProvider runDataProvider;

    private double defaultDistance;
    private long defaultTime;
    private EditText txtDataTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_run_data);

        Intent intent = getIntent();
        double distance = intent.getDoubleExtra("distance", 0);
        defaultDistance = distance;
        long time = intent.getLongExtra("time", 0);
        defaultTime = time;
        double elevation = intent.getDoubleExtra("elevation", 0);
        int calories = intent.getIntExtra("calories", 0);

        runDataProvider = new RunDataProvider();

        final EditText txtRunDistance = findViewById(R.id.editTextDistance);
        txtRunDistance.setText(String.format("%.2f", distance / 1000));


        txtDataTime = findViewById(R.id.textViewTime);
        txtDataTime.setText(String.valueOf(time));

        EditText txtDataElevation = findViewById(R.id.textViewElevation);
        txtDataElevation.setText(String.valueOf(elevation) + " m");

        EditText txtDataCalories = findViewById(R.id.textViewCalories);
        txtDataCalories.setText(String.valueOf(calories) + " kcals");

        Button btnRecount = findViewById(R.id.btnRecount);
        btnRecount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recountData(String.valueOf(txtRunDistance.getText()));
            }
        });

        Button btnDefault = findViewById(R.id.btnSetDefault);
        btnDefault.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setDefault();
            }
        });

    }

    private void setDefault() {
        runDataProvider.processAndSaveRunData(61);
        finish();
        startActivity(getIntent());
    }

    private void recountData(String runDistance) {
        double distance = Double.parseDouble(runDistance);

        double newTime = ((distance + defaultTime) / defaultDistance);
        txtDataTime.setText(String.valueOf(newTime));

    }
}
