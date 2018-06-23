package cz.uhk.fim.runhk.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;

import cz.uhk.fim.runhk.R;
import cz.uhk.fim.runhk.fragments.DetailChallengeFragment;
import cz.uhk.fim.runhk.model.LocationModel;

public class DetailSectionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_section);

        double distance = getIntent().getDoubleExtra("distance", 0);
        ArrayList<LocationModel> pointsList = getIntent().getParcelableArrayListExtra("points");


        Bundle bundle = new Bundle();
        bundle.putDouble("distance", distance);
        bundle.putParcelableArrayList("points", pointsList);
        DetailChallengeFragment detailChallengeFragment = new DetailChallengeFragment();
        detailChallengeFragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentDetailContainer, detailChallengeFragment) // kam to chci a co
                .commit();

    }
}
