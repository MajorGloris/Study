package azynias.study.DataHandlers;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import azynias.study.ObjectModels.Kanji;
import azynias.study.R;

/**
 * Created by Albedo on 6/28/2017.
 */

public class KanjiAdapter extends RecyclerView.Adapter<KanjiAdapter.ViewHolder> {

    private List<Kanji> kanjiList;
    private String mTier;

    public class ViewHolder extends RecyclerView.ViewHolder {
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        public TextView nameTextView;
        public TextView kanjiInfo;
        public TextView kanjiDueDate;
        public TextView kanjiParts;
        public TextView kanjiStory;
        public ImageButton messageButton;


        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);
            kanjiDueDate = (TextView) itemView.findViewById(R.id.kanji_rv_duedate);
            kanjiInfo = (TextView) itemView.findViewById(R.id.kanji_info_rv);
            nameTextView = (TextView) itemView.findViewById(R.id.kanji_rv);
            messageButton = (ImageButton) itemView.findViewById(R.id.change_kanji_info);
            kanjiParts = (TextView) itemView.findViewById(R.id.kanji_parts_adapt);

            kanjiStory = (TextView) itemView.findViewById(R.id.kanji_story_adapt);
        }
    }


    private Context mContext;

    // Pass in the contact array into the constructor
    public KanjiAdapter(Context context, List<Kanji> kanjis, String mTiers) {
        kanjiList = kanjis;
        mContext = context;
        mTier = mTiers;
    }

    // Easy access to the context object in the recyclerview
    private Context getContext() {
        return mContext;
    }

    @Override
    public KanjiAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View bracketView = inflater.inflate(R.layout.item_kanjis, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(bracketView);
        return viewHolder;
    }

    public KanjiAdapter(List<Kanji> kanjiList) {
        this.kanjiList = kanjiList;
    }

    @Override
    public void onBindViewHolder(KanjiAdapter.ViewHolder viewHolder, int position) {
        // Get the data model based on position
        Kanji kanji = kanjiList.get(position);
        int color = UserPreferences.getInstance().getBracket();

        TextView kanjiInfo = viewHolder.kanjiInfo;
        TextView textView = viewHolder.nameTextView;
        textView.setText(kanji.getCharacter());

        kanjiInfo.append("Meaning: " + kanji.getName());
        TextView kanjiParts = viewHolder.kanjiParts;
        TextView kanjiStory = viewHolder.kanjiStory;

        kanjiParts.setText("Parts: " + kanji.getElements());
        kanjiStory.setText("Story: " + kanji.getStory());

        TextView kanjiDueDate = viewHolder.kanjiDueDate;
        kanjiDueDate.setText(kanji.getDueDate().toString());



        ImageButton button = viewHolder.messageButton;
        sendKanjiChange(button, kanji, mTier);
        viewHolder.setIsRecyclable(false);
    }



    @Override
    public int getItemCount() {
        return kanjiList.size();
    }

    public void sendKanjiChange(final View view, final Kanji character, final String theTier) {
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                final View view = LayoutInflater.from(mContext).inflate(R.layout.kanji_card_dialog, null);

                TextView kanjiDisplay = (TextView) view.findViewById(R.id.kanji_char_card);
                kanjiDisplay.setText(character.getCharacter());

                final EditText changeStory = (EditText) view.findViewById(R.id.change_story_editText);
                changeStory.setHint(character.getStory());

                TextView parts_kanji = (TextView) view.findViewById(R.id.card_parts_kanji);
                parts_kanji.setText("Parts: " + character.getElements());
                TextView meaning_kanji = (TextView) view.findViewById(R.id.meaning_card_kanji);
                meaning_kanji.setText("Meaning: " + character.getName());

                Button apply = (Button)view.findViewById(R.id.apply_card_changes);
                apply.setBackgroundColor(Color.RED);
                apply.setText("Apply");

                builder.setView(view);
                final AlertDialog dialog = builder.create();

                dialog.show();

                apply.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String newStory = changeStory.getText().toString();
                        TierDBHandler.getInstance(mContext).setKanjiStory(newStory, character.getID());
                        dialog.dismiss();
                    }
                });
            }
        });
    }
}