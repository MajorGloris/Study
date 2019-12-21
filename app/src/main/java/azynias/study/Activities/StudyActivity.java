package azynias.study.Activities;

import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import azynias.study.Fragments.KanjiDisplayFragment;
import azynias.study.R;

public class StudyActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_study);

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.place, new KanjiDisplayFragment());

        ft.commit();

    }
}
