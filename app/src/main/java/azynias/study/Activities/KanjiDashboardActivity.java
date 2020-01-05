package azynias.study.Activities;

import android.content.Intent;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import azynias.study.Fragments.DashboardFragment;
import azynias.study.R;

public class KanjiDashboardActivity extends AppCompatActivity implements DashboardFragment.OnItemSelectedListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kanji_dashboard);

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.place, new DashboardFragment());

        ft.commit();
    }

    public void onItemSelected(int id) {
        int[] buttonIDs = {1, 2, 3, 4}; // 1 = study, 2 = practice, 3 = database/progress, 4 = progress

        if(id==1) {
            Intent i = new Intent(KanjiDashboardActivity.this, StudyActivity.class);

            startActivity(i);
        }
        if (id==2) {
            Intent i = new Intent(KanjiDashboardActivity.this, QuizActivity.class);
            startActivity(i);
        }
        if (id==3) {
            Intent i = new Intent(KanjiDashboardActivity.this, KanjiDBActivity.class);
            startActivity(i);
        }
        if (id==4) {
            Intent i = new Intent(KanjiDashboardActivity.this, ProgressActivity.class);
            startActivity(i);
        }

    }
}
