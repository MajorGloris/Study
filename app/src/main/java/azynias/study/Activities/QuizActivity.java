package azynias.study.Activities;

import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import azynias.study.Fragments.StudyQuizFragment;
import azynias.study.R;

public class QuizActivity extends AppCompatActivity {

    private StudyQuizFragment studyQuizFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);


        studyQuizFragment = new StudyQuizFragment();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.quiz_fragment, studyQuizFragment);

        ft.commit();


    }

    public void onRadioButtonClick(View view) {
        studyQuizFragment.onRadioButtonClick(view);
    }
}
