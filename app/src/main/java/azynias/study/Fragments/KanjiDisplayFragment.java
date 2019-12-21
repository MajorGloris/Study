package azynias.study.Fragments;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import azynias.study.DataHandlers.TierDBHandler;
import azynias.study.ObjectModels.Bracket;
import azynias.study.R;

public class KanjiDisplayFragment extends Fragment {
    private TextView characterKanji;
    private TextView radicals;
    private TextView meaning;
    private TextView story;
    private TextView positionCounter;

    private boolean initial = true;
    private ImageButton next;
    private ImageButton back;
    private EditText changeStory;
    private Button setStory;
    private Button finish;

    private int position = 0;
    private int elementsMax = 0;
    TierDBHandler tierDBHandler;
    private Bracket bracket;

    FragmentActivity listener;

    private void init(View view) {
        tierDBHandler = TierDBHandler.getInstance(getContext());
        bracket = tierDBHandler.arrangeStudyLevel();
        elementsMax = bracket.getKanjiChars().size();
        positionCounter = (TextView) view.findViewById(R.id.position_study_fragment);
        characterKanji = (TextView) view.findViewById(R.id.kanji);
        radicals = (TextView) view.findViewById(R.id.radical_info);
        meaning = (TextView) view.findViewById(R.id.meaning);
        story = (TextView) view.findViewById(R.id.example_story);
        changeStory = (EditText) view.findViewById(R.id.change_story);
        changeStory.setText("Enter your story here, using the elements from above!");
        next = (ImageButton) view.findViewById(R.id.next);
        back = (ImageButton) view.findViewById(R.id.back);
        setStory = (Button) view.findViewById(R.id.new_story);
        finish = (Button) view.findViewById(R.id.done);

        characterKanji.setText(bracket.getKanjiChars().get(KanjiDisplayFragment.this.position).getCharacter());
        radicals.setText("Elements: " + bracket.getKanjiChars().get(KanjiDisplayFragment.this.position).getElements());
        story.setText(bracket.getKanjiChars().get(KanjiDisplayFragment.this.position).getExampleStory());
        bordersForText();
        positionCounter.setText(""+(position+1)+"/"+(elementsMax));
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof Activity){
            this.listener = (FragmentActivity) context;
        }
    }
    // The onCreateView method is called when Fragment should create its View object hierarchy,
    // either dynamically or via XML layout inflation. 
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        // Defines the xml file for the fragment
        return inflater.inflate(R.layout.fragment_kanji_display, parent, false);
    }

    // This event is triggered soon after onCreateView().
    // Any view setup should occur here.  E.g., view lookups and attaching view listeners.
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        TierDBHandler.getInstance(getContext()).setUserPrefs();
        init(view);

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                KanjiDisplayFragment.this.alterPosition(false);
                characterKanji.setText(bracket.getKanjiChars().get(KanjiDisplayFragment.this.position).getCharacter());
                radicals.setText("Elements: " + bracket.getKanjiChars().get(KanjiDisplayFragment.this.position).getElements());
                story.setText(bracket.getKanjiChars().get(KanjiDisplayFragment.this.position).getExampleStory());
                positionCounter.setText(""+(position+1)+"/"+(elementsMax));

            }
        });

        changeStory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeStory.setText("");
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                KanjiDisplayFragment.this.alterPosition(true);
                characterKanji.setText(bracket.getKanjiChars().get(KanjiDisplayFragment.this.position).getCharacter());
                radicals.setText("Elements: " +bracket.getKanjiChars().get(KanjiDisplayFragment.this.position).getElements());
                story.setText(bracket.getKanjiChars().get(KanjiDisplayFragment.this.position).getExampleStory());
                positionCounter.setText(""+(position+1)+"/"+(elementsMax));
            }
        });

        setStory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userStory = changeStory.getText().toString();
                tierDBHandler.setKanjiStory(userStory, bracket.getKanjiChars().get(KanjiDisplayFragment.this.position).getID());
                story.setText(userStory);
                changeStory.setText("");
            }
        });

        finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tierDBHandler.setInitialDueDate();
                tierDBHandler.incrementBracket();
            }
        });
    }


    public void alterPosition(boolean decrease) {
        if (decrease && this.position > bracket.getBeginPosition()) {
            KanjiDisplayFragment.this.position--;
        }
        else if(!decrease && this.position < bracket.getEndPosition()-1) {
            KanjiDisplayFragment.this.position++;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        this.listener = null;
    }

    // This method is called after the parent Activity's onCreate() method has completed.
    // Accessing the view hierarchy of the parent activity must be done in the onActivityCreated.
    // At this point, it is safe to search for activity View objects by their ID, for example.
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    public void bordersForText() {
        GradientDrawable border = new GradientDrawable();
        border.setColor(0xFFFFFFFF); //white background
        border.setStroke(1, 0xFF000000); //black border with full opacity

        radicals.setBackground(border);
        meaning.setBackground(border);
        story.setBackground(border);
        changeStory.setBackground(border);
    }
}