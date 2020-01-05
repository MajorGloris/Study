package azynias.study.Activities;

import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;

import azynias.study.Fragments.StudyQuizFragment;
import azynias.study.Fragments.normalQuizFragment;
import azynias.study.R;

public class QuizActivity extends AppCompatActivity {

    private normalQuizFragment normalQuizFragment;
    private StudyQuizFragment studyQuizFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

     //   studyQuizFragment = new StudyQuizFragment();
        normalQuizFragment = new normalQuizFragment();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.quiz_fragment, normalQuizFragment);

        ft.commit();


    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public void onRadioButtonClick(View view) {
        studyQuizFragment.onRadioButtonClick(view);
    }
}
