package azynias.study.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import java.util.ArrayList;

import azynias.study.DataHandlers.BracketsAdapter;
import azynias.study.DataHandlers.TierDBHandler;
import azynias.study.ObjectModels.Bracket;
import azynias.study.ObjectModels.Tier;
import azynias.study.R;

public class KanjiDBActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kanji_db);
        TierDBHandler tierDBHandler = TierDBHandler.getInstance(this);

        Tier bronzeTier = tierDBHandler.arrangeBracketsForTier("Bronze");
        ArrayList<Bracket> brackets = bronzeTier.getCharacters();
        Log.d("Size of brackets", Integer.toString(brackets.size()));
        BracketsAdapter adapter = new BracketsAdapter(this, brackets);
        RecyclerView rvBrackets = (RecyclerView) findViewById(R.id.rvContacts);

        rvBrackets.setAdapter(adapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        rvBrackets.setLayoutManager(layoutManager);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(rvBrackets.getContext(),
                layoutManager.getOrientation());
        rvBrackets.addItemDecoration(dividerItemDecoration);
    }
}
