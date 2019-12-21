package azynias.study.Fragments;

import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import azynias.study.Activities.KanjiDashboardActivity;
import azynias.study.Activities.StudyQuizActivity;
import azynias.study.DataHandlers.TierDBHandler;
import azynias.study.ObjectModels.Answer;
import azynias.study.ObjectModels.KanjiQuestion;
import azynias.study.ObjectModels.Question;
import azynias.study.ObjectModels.QuizSession;
import azynias.study.R;

/**
 * Created by Britannia on 2017-07-10.
 * we will really need to figure out how to account for recall and recognition quizzes
 */

public class StudyQuizFragment extends Fragment {
    private QuizSession qs;
    private ImageButton next;
    private TextView quiz_area;

    private RadioGroup answerList;
    private RadioButton answer1;
    private RadioButton answer2;
    private RadioButton answer3;
    private RadioButton answer4;


    private int currentKanjiID = 0;
    private ArrayList<KanjiQuestion> kanjiQuestions;
    private int position = 0;
    private String userAnswer = "";
    private String rightAnswer; // the idea is to update these within every increment

    private KanjiQuestion currentKanjiQuestion;
    private int questionsAmount;
    private Question curQuestion;

    Random rand;

    TierDBHandler tierDBHandler = TierDBHandler.getInstance(getContext());

    final int ANSWERS_AMOUNT = 4;

    public void init(View view) {
        rand = new Random();
        qs = TierDBHandler.getInstance(getContext()).grabQuestions();
        kanjiQuestions = qs.getQuestions();
        questionsAmount = kanjiQuestions.size();

        next = (ImageButton) view.findViewById(R.id.quiz_next_question);
        quiz_area = (TextView) view.findViewById(R.id.quiz_area_kanji);

        answerList = (RadioGroup) view.findViewById(R.id.answer_list_radio);
        answer1 = (RadioButton) view.findViewById(R.id.answer_1);
        answer2 = (RadioButton) view.findViewById(R.id.answer_2);
        answer3 = (RadioButton) view.findViewById(R.id.answer_3);
        answer4 = (RadioButton) view.findViewById(R.id.answer_4);

        questionsHandler();

    }

    public void questionsHandler() {

        currentKanjiQuestion = kanjiQuestions.get(position);
        curQuestion = currentKanjiQuestion.getOnQuestion();
        ArrayList<Answer> answers = curQuestion.getAnswers();
        Answer correctAnswer = answers.get(curQuestion.getCorrectQuestionID());

        rightAnswer = curQuestion.getActualAnswer();
        currentKanjiID = correctAnswer.getKanji().getID();

        setTextsForMultipleChoice();

    }

    public void rightAnswer() {
        turnOff();
        if(currentKanjiQuestion.finished()) {
            int quality = calculateQuality(currentKanjiQuestion.getWrong());

            if(quality>=3)
                tierDBHandler.correctKanjiDate(quality, currentKanjiID);
            else {
                tierDBHandler.incorrectDueDate(currentKanjiID);
            }

            kanjiQuestions.remove(currentKanjiQuestion);
        }
        int max = kanjiQuestions.size();
        Log.d("max", ""+max);
        if(max==0) {
            finish();
            position = -5;
        }
        else {
            position = rand.nextInt(max);

            currentKanjiQuestion = kanjiQuestions.get(position);
            curQuestion = currentKanjiQuestion.getOnQuestion();

            rightAnswer = curQuestion.getActualAnswer();
            currentKanjiID = curQuestion.getAnswers().get(curQuestion.getCorrectQuestionID()).getKanji().getID();
        }
    }

    public void finish() {
        Intent i = new Intent(getActivity(), KanjiDashboardActivity.class);
        startActivity(i);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_quiz, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        init(view);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                incrementQuestion();
                answerList.clearCheck();
            }
        });
    }

    public void incrementQuestion() {
        if(position < kanjiQuestions.size() && userAnswer.equals(rightAnswer)) {
            Toast.makeText(getActivity(), "right", Toast.LENGTH_LONG).show();
            rightAnswer();
            if(position!=-5) setTextsForMultipleChoice();

        }
        else if (position < kanjiQuestions.size() && !(userAnswer.equals(rightAnswer))) {
            Toast.makeText(getActivity(), "wrong!", Toast.LENGTH_LONG).show();
            wrongAnswer();
            setTextsForMultipleChoice(); // maybe use list.add(list.remove(0));, what this does is moves it to the end of the list
        }
        else {
            Toast.makeText(getActivity(), "You done nigga! " + userAnswer.equals(rightAnswer), Toast.LENGTH_LONG).show();
        }
    }

    public void turnOff() {
        curQuestion.setOff();
    }

    public void wrongAnswer() {
        currentKanjiQuestion.setWrong(currentKanjiQuestion.getWrong()+1);
        Collections.rotate(kanjiQuestions, -1);

        int max = kanjiQuestions.size();
        int randomNum = rand.nextInt(max);
        Log.d("pos", ""+position);
        position = randomNum;
        currentKanjiQuestion = kanjiQuestions.get(position);
        curQuestion = currentKanjiQuestion.getOnQuestion();

        rightAnswer = curQuestion.getActualAnswer();
        currentKanjiID = curQuestion.getAnswers().get(curQuestion.getCorrectQuestionID()).getKanji().getID();
    }

    public int calculateQuality(int wrong) {
        if(wrong==0)
            return 5;
        else if(wrong==1)
            return 4;
        else if(wrong==2)
            return 3;
        else if(wrong==3)
            return 0;
        return 0;
    }

    public void setTextsForMultipleChoice() {
        int correctAnswer = curQuestion.getCorrectQuestionID();

        if(curQuestion.isRecall()) {
            for(int i = 0;i<ANSWERS_AMOUNT;i++) {
                View o = answerList.getChildAt(i);
                if(o instanceof RadioButton) {
                    ((RadioButton) o).setText(curQuestion.getAnswers().get(i).getKanji().getCharacter());

                }
            }
            quiz_area.setText(curQuestion.getAnswers().get(correctAnswer).getKanji().getName());
        }
        else {
            for(int i = 0;i<ANSWERS_AMOUNT;i++) {
                View o = answerList.getChildAt(i);
                if(o instanceof RadioButton) {
                    ((RadioButton) o).setText(curQuestion.getAnswers().get(i).getKanji().getName()); // will display 4 meanings.
                }
            }
            quiz_area.setText(curQuestion.getAnswers().get(correctAnswer).getKanji().getCharacter());
        }

    }

    public void onRadioButtonClick(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.answer_1:
                if (checked)

                    userAnswer = ((RadioButton) view).getText().toString();
                    boolean hi = userAnswer.equals(rightAnswer);
                    break;
            case R.id.answer_2:
                if (checked)
                    userAnswer = ((RadioButton) view).getText().toString();
                hi = userAnswer.equals(rightAnswer);
                Log.d("test", Boolean.toString(hi));
                break;
            case R.id.answer_3:
                if (checked)
                    userAnswer = ((RadioButton) view).getText().toString();
                hi = userAnswer.equals(rightAnswer);
                Log.d("test", Boolean.toString(hi));
                break;
            case R.id.answer_4:
                if (checked)
                    userAnswer = ((RadioButton) view).getText().toString();
                hi = userAnswer.equals(rightAnswer);
                Log.d("test", Boolean.toString(hi));
                break;
        }
    }


}
