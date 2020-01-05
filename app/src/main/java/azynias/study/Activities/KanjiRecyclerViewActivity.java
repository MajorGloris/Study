package azynias.study.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import azynias.study.DataHandlers.BracketsAdapter;
import azynias.study.DataHandlers.KanjiAdapter;
import azynias.study.DataHandlers.TierDBHandler;
import azynias.study.ObjectModels.Bracket;
import azynias.study.ObjectModels.Kanji;
import azynias.study.ObjectModels.KanjiDetailsWrapper;
import azynias.study.ObjectModels.Tier;
import azynias.study.R;

public class KanjiRecyclerViewActivity extends AppCompatActivity {

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kanji_recycler_view);

        TierDBHandler tierDBHandler = TierDBHandler.getInstance(this);
        Intent i = getIntent();
        Bundle b = i.getExtras();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //List<Kanji> kanjis = tierDBHandler.getTiersKanji("Bronze");
        KanjiDetailsWrapper wrap = (KanjiDetailsWrapper) i.getSerializableExtra("kanji_chars");
        ArrayList<Kanji> list = wrap.getItemDetails();


        KanjiAdapter adapter = new KanjiAdapter(this, list, getIntent().getStringExtra("the_tier"));
        RecyclerView rvKanji = (RecyclerView) findViewById(R.id.rvContacts);
        rvKanji.setHasFixedSize(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        rvKanji.setAdapter(adapter);
        rvKanji.setLayoutManager(layoutManager);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(rvKanji.getContext(),
                layoutManager.getOrientation());
        rvKanji.addItemDecoration(dividerItemDecoration);
    }
}
