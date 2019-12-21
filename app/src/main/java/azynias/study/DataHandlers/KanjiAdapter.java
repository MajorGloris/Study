package azynias.study.DataHandlers;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.List;

import azynias.study.ObjectModels.Kanji;
import azynias.study.R;

/**
 * Created by Albedo on 6/28/2017.
 */

public class KanjiAdapter extends RecyclerView.Adapter<KanjiAdapter.ViewHolder> {

    private List<Kanji> kanjiList;

    public class ViewHolder extends RecyclerView.ViewHolder {
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        public TextView nameTextView;
        public TextView kanjiInfo;
        public TextView kanjiDueDate;
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
        }
    }


    private Context mContext;

    // Pass in the contact array into the constructor
    public KanjiAdapter(Context context, List<Kanji> kanjis) {
        kanjiList = kanjis;
        mContext = context;
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

        kanjiInfo.append(kanji.getName() + "\n" + kanji.getExampleStory());

        TextView kanjiDueDate = viewHolder.kanjiDueDate;
        kanjiDueDate.setText(kanji.getDueDate().toString());

        ImageButton button = viewHolder.messageButton;

    }



    @Override
    public int getItemCount() {
        return kanjiList.size();
    }
}