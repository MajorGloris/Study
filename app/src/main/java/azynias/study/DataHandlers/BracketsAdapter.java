package azynias.study.DataHandlers;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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
        public TextView bracketNumber;
        public View card;

        public LinearLayout layout;


        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);

            card = itemView.findViewById(R.id.card_db);
            nameTextView = (TextView) itemView.findViewById(R.id.kanji_info);
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

        boolean greater = greaterTier(bracket.getTierBelong());
        Log.d("Greater", greater+"");
        if(bracket.getId()<color || greater) {

           if(greater) {
               bracketNum.setTextColor(Color.BLUE);
               textView.setTextColor(Color.BLUE);
           }
           else if(UserPreferences.getInstance().getTier().equals(bracket.getTierBelong()) && bracket.getId()<color) {
               bracketNum.setTextColor(Color.BLUE);
               textView.setTextColor(Color.BLUE);
           }
        }
        else {

            bracketNum.setTextColor(Color.BLACK);
            bracketNum.append(" - LOCKED");
            textView.setTextColor(Color.BLACK);
        }

        View card = viewHolder.card;
        sendToKanjiAdapter(card, bracket.getKanjiChars(), bracket.getTierBelong());
    }

    public boolean greaterTier(String tier) {
        String userTier = UserPreferences.getInstance().getTier();

        int selectedTier = tiers(tier);
        int userTierVal = tiers(userTier);

        if(userTierVal>selectedTier) {
            return true;
        }
        return false;
    }

    private int tiers(String preceedingTier) {
        if(preceedingTier.equals("Bronze"))
            return 1;
        if(preceedingTier.equals("Copper"))
            return 2;
        if(preceedingTier.equals("Silver"))
            return 3;
        if(preceedingTier.equals("Gold"))
            return 4;
        if(preceedingTier.equals("Platinum"))
            return 5;
        if(preceedingTier.equals("Diamond"))
            return 6;
        return 0;
    }

    @Override
    public int getItemCount() {
        return bracketsList.size();
    }

    private void sendToKanjiAdapter(final View view, final ArrayList<Kanji> characters, final String tier) {
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                KanjiDetailsWrapper wrapper = new KanjiDetailsWrapper(characters);
                Intent i = new Intent(mContext, KanjiRecyclerViewActivity.class);
                i.putExtra("kanji_chars", wrapper);
                i.putExtra("the_tier", tier);
                mContext.startActivity(i);
            }
        });
    }
}