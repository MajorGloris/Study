package azynias.study.Fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Map;

import azynias.study.Activities.FrontActivity;
import azynias.study.DataHandlers.TierDBHandler;
import azynias.study.DataHandlers.UserPreferences;
import azynias.study.ObjectModels.Bracket;
import azynias.study.R;

public class KanjiDisplayFragment extends Fragment {
    private TextView characterKanji;
    private TextView radicals;
    private TextView meaning;
    private TextView story;
    private TextView positionCounter;
    private LinearLayout layout;

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
        bracket = UserPreferences.getInstance().getStudyBracket();


        elementsMax = bracket.getKanjiChars().size();

        positionCounter = (TextView) view.findViewById(R.id.position_study_fragment);
        layout = (LinearLayout) view.findViewById(R.id.study_layout_focus);
        characterKanji = (TextView) view.findViewById(R.id.kanji);
        radicals = (TextView) view.findViewById(R.id.radical_info);
        meaning = (TextView) view.findViewById(R.id.meaning);
        story = (TextView) view.findViewById(R.id.example_story);
        changeStory = (EditText) view.findViewById(R.id.change_story);
        changeStory.clearFocus();
        changeStory.setHint("Enter your story here, using the parts from above!");
        changeStory.setTextColor(Color.BLACK);
        next = (ImageButton) view.findViewById(R.id.next);
        back = (ImageButton) view.findViewById(R.id.back);
        setStory = (Button) view.findViewById(R.id.new_story);
        finish = (Button) view.findViewById(R.id.done);

        characterKanji.setText(bracket.getKanjiChars().get(KanjiDisplayFragment.this.position).getCharacter());
        radicals.setText("Parts: " + bracket.getKanjiChars().get(KanjiDisplayFragment.this.position).getElements());
        story.setText("Story: " + bracket.getKanjiChars().get(KanjiDisplayFragment.this.position).getExampleStory());
        bordersForText();
        positionCounter.setText(""+(position+1)+"/"+(elementsMax));
        meaning.setText(bracket.getKanjiChars().get(KanjiDisplayFragment.this.position).getName());
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
                setTexts();

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
                setTexts();

            }
        });

        setStory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userStory = changeStory.getText().toString();
                tierDBHandler.setKanjiStory(userStory, bracket.getKanjiChars().get(KanjiDisplayFragment.this.position).getID());
                story.setText("Story: " + userStory);
                changeStory.setText("");
                hideSoftKeyboard();

            }
        });

        finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tierDBHandler.setInitialDueDate();
                tierDBHandler.incrementBracket();
                startActivity(new Intent(getContext(), FrontActivity.class));
            }
        });
    }

    public void hideSoftKeyboard() {
        InputMethodManager inputMethodManager =
                (InputMethodManager) getActivity().getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(
                getActivity().getCurrentFocus().getWindowToken(), 0);
    }

    public void setTexts() {
        characterKanji.setText(bracket.getKanjiChars().get(KanjiDisplayFragment.this.position).getCharacter());
        radicals.setText("Parts: " +bracket.getKanjiChars().get(KanjiDisplayFragment.this.position).getElements());
        story.setText("Story: " + bracket.getKanjiChars().get(KanjiDisplayFragment.this.position).getExampleStory());
        positionCounter.setText(""+(position+1)+"/"+(elementsMax));
        meaning.setText(bracket.getKanjiChars().get(KanjiDisplayFragment.this.position).getName());
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
        /*GradientDrawable border = new GradientDrawable();
        border.setColor(0xFFFFFFFF); //white background
        border.setStroke(1, 0xFF000000); //black border with full opacity

        radicals.setBackground(border);
        meaning.setBackground(border);
        story.setBackground(border);
        changeStory.setBackground(border);*/
    }

    /*public void progFinish() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_progress, null);

        TextView title = (TextView) view.findViewById(R.id.title_dialog);
        int totalS = wrongChars.size();
        LinearLayout linearLayout = (LinearLayout) view.findViewById(R.id.prog_dialog);


        for(Map.Entry<String, Integer> entry : wrongChars.entrySet()) {
            TextView wrongChar = new TextView(view.getContext());
            wrongChar.setTextSize(TypedValue.COMPLEX_UNIT_SP,18);
            String theStr = "<big><b>" + entry.getKey() +"</big></b>" + "\t" + " answered incorrectly " + entry.getValue() + " times(s)";
            wrongChar.setText(Html.fromHtml(theStr));
            wrongChar.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            try{
                linearLayout.addView(wrongChar);
            }catch(Exception e){
                Log.d("exce", e.toString());
            }
        }

        Double grade = 100 - ((wrong * 100.0) / total);
        title.append(": " + (int) grade.doubleValue() + "%");
        TextView gradeTv = new TextView(view.getContext());
        gradeTv.setTextSize(TypedValue.COMPLEX_UNIT_SP,15);
        gradeTv.setText(" ");
        linearLayout.addView(gradeTv);

        Button finish = new Button(view.getContext());

        finish.setBackgroundColor(Color.RED);
        finish.setText("Finish");
        finish.setGravity(Gravity.CENTER);
        finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), FrontActivity.class);
                startActivity(i);
            }
        });
        linearLayout.addView(finish);

        builder.setView(view);
        AlertDialog dialog = builder.create();
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

    }*/
}