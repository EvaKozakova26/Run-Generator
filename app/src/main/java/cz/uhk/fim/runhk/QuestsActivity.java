package cz.uhk.fim.runhk;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class QuestsActivity extends AppCompatActivity implements QuestListFragment.OnItemSelectedInterface {

    private boolean isLandscape;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quests);

        QuestListFragment questListFragment = (QuestListFragment) getSupportFragmentManager().findFragmentById(R.id.fragmentList);
        questListFragment.setOnItemSelectedInterface(this);

        if (findViewById(R.id.fragmentContainer) != null) {
            isLandscape = true;
        }
    }


    @Override
    public void onItemSelected(View view) {
        if (isLandscape) {
            switch (view.getId()) {
                case R.id.txtRandom:
                    DetailQuestFragment detailQuestFragment = new DetailQuestFragment();
                    getSupportFragmentManager().beginTransaction()
                            .add(R.id.fragmentContainer, detailQuestFragment)
                            .commit();
                    break;

            }

            }else {
            Intent intent = new Intent(this, DetailSectionActivity.class);
            intent.putExtra("section", view.getId());
            startActivity(intent);
        }
    }
}
