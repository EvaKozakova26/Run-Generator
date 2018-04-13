package cz.uhk.fim.runhk.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import cz.uhk.fim.runhk.fragments.DetailQuestFragment;
import cz.uhk.fim.runhk.fragments.QuestListFragment;
import cz.uhk.fim.runhk.R;

public class QuestsActivity extends AppCompatActivity implements QuestListFragment.OnItemSelectedInterface {

    private boolean isLandscape;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quests);

        QuestListFragment questListFragment = (QuestListFragment) getSupportFragmentManager().findFragmentById(R.id.fragmentList);
        questListFragment.setOnItemSelectedInterface(this);

    }


    @Override
    public void onItemSelected(View view) {
            Intent intent = new Intent(this, DetailSectionActivity.class);
            intent.putExtra("section", view.getId());
            startActivity(intent);
        }
}
