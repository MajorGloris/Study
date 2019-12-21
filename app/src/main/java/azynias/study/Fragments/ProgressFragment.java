package azynias.study.Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import azynias.study.DataHandlers.TierDBHandler;
import azynias.study.R;


public class ProgressFragment extends Fragment {

    private TextView itemsDue;
    private TierDBHandler tierDBHandler = TierDBHandler.getInstance(getContext());

    private void init(View view) {
        itemsDue = (TextView) view.findViewById(R.id.quizzes_due);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_progress, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        init(view);
        setInfoUp();
    }

    public void setInfoUp() {
        int amt = tierDBHandler.itemsDueToday();
        itemsDue.setText(""+amt + " items are overdue.");
    }
}


