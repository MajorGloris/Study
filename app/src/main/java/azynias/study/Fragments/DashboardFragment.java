package azynias.study.Fragments;

import android.app.Activity;
import android.content.Context;
import android.media.Image;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import azynias.study.DataHandlers.TierDBHandler;
import azynias.study.R;

public class DashboardFragment extends Fragment {
    private ImageButton study;
    private ImageButton practice;
    private ImageButton database;
    private ImageButton test_dash;
    private ImageButton progress;
   // private Button progress;

    private OnItemSelectedListener buttonListener;

    private void init(View view) {
        TierDBHandler.getInstance(getContext()).setUserPrefs();
        study = (ImageButton) view.findViewById(R.id.study);
        practice = (ImageButton) view.findViewById(R.id.practice);
        database = (ImageButton) view.findViewById(R.id.dashboard_database);
      //  test_dash = (ImageButton) view.findViewById(R.id.test_dashboard);
        progress = (ImageButton) view.findViewById(R.id.progress_dashboard);
   //     progress = (Button) view.findViewById(R.id.progress_dashboard);

    }

    public interface OnItemSelectedListener {
        public void onItemSelected(int id);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        // Defines the xml file for the fragment
        return inflater.inflate(R.layout.dashboard_fragment, parent, false);
    }

    // This event is triggered soon after onCreateView().
    // Any view setup should occur here.  E.g., view lookups and attaching view listeners.
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        init(view);

        study.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonListener.onItemSelected(1); // id = 1 for study
            }
        });

        practice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonListener.onItemSelected(2); // id = 2 for practice
            }
        });

        database.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonListener.onItemSelected(3); // id = 2 for practice
            }
        });

     /*   test_dash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TierDBHandler.getInstance(getContext()).arrangeDifficulty(10);
                TierDBHandler.getInstance(getContext()).getNigger();
            }
        });*/

        progress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonListener.onItemSelected(4);
            }
        });

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            buttonListener = (OnItemSelectedListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnSelectedListener");
        }
    }
}