package azynias.study.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kanji_recycler_view);

        TierDBHandler tierDBHandler = TierDBHandler.getInstance(this);
        Intent i = getIntent();
        Bundle b = i.getExtras();


        //List<Kanji> kanjis = tierDBHandler.getTiersKanji("Bronze");
        KanjiDetailsWrapper wrap = (KanjiDetailsWrapper) getIntent().getSerializableExtra("kanji_chars");
        ArrayList<Kanji> list = wrap.getItemDetails();

        KanjiAdapter adapter = new KanjiAdapter(this, list);
        RecyclerView rvBrackets = (RecyclerView) findViewById(R.id.rvContacts);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        rvBrackets.setAdapter(adapter);
        rvBrackets.setLayoutManager(layoutManager);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(rvBrackets.getContext(),
                layoutManager.getOrientation());
        rvBrackets.addItemDecoration(dividerItemDecoration);
    }
}
