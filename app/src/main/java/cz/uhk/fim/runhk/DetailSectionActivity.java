package cz.uhk.fim.runhk;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class DetailSectionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_section);

        int section = getIntent().getIntExtra("section", 0);

        switch (section) {
            case R.id.txtRandom:
                DetailQuestFragment detailQuestFragment = new DetailQuestFragment();
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragmentContainer, detailQuestFragment) // kam to chci a co
                        .commit();

                break;
        }
    }
}
