package azynias.study.DataHandlers;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import azynias.study.Activities.KanjiRecyclerViewActivity;
import azynias.study.ObjectModels.Bracket;
import azynias.study.ObjectModels.Kanji;
import azynias.study.ObjectModels.KanjiDetailsWrapper;
import azynias.study.R;

/**
 * Created by Albedo on 6/28/2017.
 */

public class BracketsAdapter extends RecyclerView.Adapter<BracketsAdapter.ViewHolder> {

    private List<Bracket> bracketsList;

    public class ViewHolder extends RecyclerView.ViewHolder {
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        public TextView nameTextView;
        public Button messageButton;
        public TextView bracketNumber;

        public LinearLayout layout;


        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);

            nameTextView = (TextView) itemView.findViewById(R.id.kanji_info);
            messageButton = (Button) itemView.findViewById(R.id.view_bracket_button);
            bracketNumber = (TextView) itemView.findViewById(R.id.bracket_number);
            layout = (LinearLayout) itemView.findViewById(R.id.layout);
        }
    }


    private Context mContext;

    public BracketsAdapter(Context context, List<Bracket> brackets) {
        bracketsList = brackets;
        mContext = context;
    }

    // Easy access to the context object in the recyclerview
    private Context getContext() {
        return mContext;
    }

    @Override
    public BracketsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View bracketView = inflater.inflate(R.layout.item_brackets, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(bracketView);
        return viewHolder;
    }

    public BracketsAdapter(List<Bracket> bracketsList) {
        this.bracketsList = bracketsList;
    }

    @Override
    public void onBindViewHolder(BracketsAdapter.ViewHolder viewHolder, int position) {
        // Get the data model based on position
        Bracket bracket = bracketsList.get(position);
        int color = UserPreferences.getInstance().getBracket();


        // Set item views based on your views and data model
        LinearLayout layout = viewHolder.layout;
        TextView bracketNum = viewHolder.bracketNumber;

        bracketNum.setText("Bracket " + bracket.getId());

        TextView textView = viewHolder.nameTextView;
        textView.setText(bracket.toString());

        if(color < bracket.getId()) {

            bracketNum.setTextColor(Color.parseColor("#003459"));


            textView.setTextColor(Color.parseColor("#00A8E8"));
        }
        else {
            bracketNum.setTextColor(Color.parseColor("#F15025"));


            textView.setTextColor(Color.parseColor("#F15025"));
        }

        Button button = viewHolder.messageButton;
        button.setText("View Bracket Kanji");
        sendToKanjiAdapter(button, bracket.getKanjiChars());
    }



    @Override
    public int getItemCount() {
        return bracketsList.size();
    }

    public void sendToKanjiAdapter(final Button btn, final ArrayList<Kanji> characters) {
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                KanjiDetailsWrapper wrapper = new KanjiDetailsWrapper(characters);
                Intent i = new Intent(mContext, KanjiRecyclerViewActivity.class);
                i.putExtra("kanji_chars", wrapper);
                mContext.startActivity(i);
            }
        });
    }
}