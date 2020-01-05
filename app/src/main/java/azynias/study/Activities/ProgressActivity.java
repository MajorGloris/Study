package azynias.study.Activities;

/*
 The idea is to create a progress page that can inflate both quiz and practice report sessions. Perhaps include more
 information regarding users general progress here. Allah Akbar.
* */


import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;

import azynias.study.Fragments.ProgressFragment;
import azynias.study.R;

public class ProgressActivity extends AppCompatActivity {
    private ProgressFragment progressFragment;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progress);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        progressFragment = new ProgressFragment();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_progress_frame, progressFragment);

        ft.commit();
    }
}
