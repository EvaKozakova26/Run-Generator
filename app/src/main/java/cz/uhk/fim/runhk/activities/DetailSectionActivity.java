package cz.uhk.fim.runhk.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import cz.uhk.fim.runhk.fragments.DetailQuestFragment;
import cz.uhk.fim.runhk.R;

public class DetailSectionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_section);

        double distance = getIntent().getDoubleExtra("distance", 0);


        Bundle bundle = new Bundle();
        bundle.putDouble("distance", distance);
        DetailQuestFragment detailQuestFragment = new DetailQuestFragment();
        detailQuestFragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentDetailContainer, detailQuestFragment) // kam to chci a co
                .commit();

    }
}
