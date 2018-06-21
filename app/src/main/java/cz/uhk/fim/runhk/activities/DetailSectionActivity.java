package cz.uhk.fim.runhk.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import cz.uhk.fim.runhk.fragments.DetailChallengeFragment;
import cz.uhk.fim.runhk.R;

public class DetailSectionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_section);

        double distance = getIntent().getDoubleExtra("distance", 0);


        Bundle bundle = new Bundle();
        bundle.putDouble("distance", distance);
        DetailChallengeFragment detailChallengeFragment = new DetailChallengeFragment();
        detailChallengeFragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentDetailContainer, detailChallengeFragment) // kam to chci a co
                .commit();

    }
}
