package azynias.study.Fragments;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutCompat;
import android.text.Html;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import azynias.study.Activities.FrontActivity;
import azynias.study.Activities.KanjiDashboardActivity;
import azynias.study.DataHandlers.TierDBHandler;
import azynias.study.DataHandlers.UserPreferences;
import azynias.study.ObjectModels.Answer;
import azynias.study.ObjectModels.Kanji;
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
    private TextView questions_wrong;
    private TextView questions_left;


    private RadioGroup answerList;
    private RadioButton answer1;
    private RadioButton answer2;
    private RadioButton answer3;
    private RadioButton answer4;
    private RadioButton answer5;

    private int currentKanjiID = 0;
    private ArrayList<KanjiQuestion> kanjiQuestions;
    private int position = 0;
    private String userAnswer = "";
    private String rightAnswer; // the idea is to update these within every increment
    private int wrong = 0;
    private int size = 0;
    private HashMap<Kanji, Integer> wrongChars;
    private int total = 0;

    private KanjiQuestion currentKanjiQuestion;
    private int questionsAmount;
    private Question curQuestion;

    private CardView cardView;
    protected View mView;

    Random rand;

    TierDBHandler tierDBHandler = TierDBHandler.getInstance(getContext());

    final int ANSWERS_AMOUNT = 5;

    public void init(View view) {
        rand = new Random();
        qs = UserPreferences.getInstance().getReviewBracket();

        wrongChars = new HashMap<>();
        kanjiQuestions = qs.getQuestions();
        questionsAmount = kanjiQuestions.size();
        total = questionsAmount*2;
        size = questionsAmount;
        cardView = (CardView) view.findViewById(R.id.shake_animate);
        next = (ImageButton) view.findViewById(R.id.quiz_next_question);
        quiz_area = (TextView) view.findViewById(R.id.quiz_area_kanji);

        questions_left = (TextView) view.findViewById(R.id.questions_left_quiz);
        questions_left.setText(size + " kanji questions left.");
        questions_wrong = (TextView) view.findViewById(R.id.questions_wrong_quiz);
        questions_wrong.setText(wrong + " incorrect answers.");

        answerList = (RadioGroup) view.findViewById(R.id.answer_list_radio);
        answer1 = (RadioButton) view.findViewById(R.id.answer_1);
        answer2 = (RadioButton) view.findViewById(R.id.answer_2);
        answer3 = (RadioButton) view.findViewById(R.id.answer_3);
        answer4 = (RadioButton) view.findViewById(R.id.answer_4);
        answer5 = (RadioButton) view.findViewById(R.id.answer_5);

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
            size--;
            questions_left.setText(size + " kanji questions left.");
            if(quality>=3)
                tierDBHandler.correctKanjiDate(quality, currentKanjiID);
            else {
                tierDBHandler.incorrectDueDate(currentKanjiID);
            }

            kanjiQuestions.remove(currentKanjiQuestion);
        }
        int max = kanjiQuestions.size();
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
        progFinish();
    }

    public void progFinish() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_progress, null);

        TextView title = (TextView) view.findViewById(R.id.title_dialog);
        int totalS = wrongChars.size();
        LinearLayout linearLayout = (LinearLayout) view.findViewById(R.id.prog_dialog);


        for(Map.Entry<Kanji, Integer> entry : wrongChars.entrySet()) {
            LinearLayout layout = new LinearLayout(view.getContext());
            layout.setOrientation(LinearLayout.HORIZONTAL);
            layout.setLayoutParams(new LinearLayoutCompat.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

            TextView wrongChar = new TextView(view.getContext());
            TextView story = new TextView(view.getContext());
            TextView incorrect = new TextView(view.getContext());

            wrongChar.setText(entry.getKey().getCharacter()+ " ");
            wrongChar.setTextColor(Color.BLACK);

            wrongChar.setTextSize(TypedValue.COMPLEX_UNIT_SP,40);

            incorrect.setText("answered incorrectly: " + entry.getValue() + " times(s)");
            incorrect.setPaddingRelative(0, 0, 0, 5);
            story.setText(entry.getKey().getStory());
            story.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);

            incorrect.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);

            try{
                layout.addView(wrongChar);
                layout.addView(incorrect);
                linearLayout.addView(layout);
                linearLayout.addView(story);

            }catch(Exception e){
                Log.d("exce", e.toString());
            }
        }

        Double grade = 100 - ((wrong * 100.0) / total);
        if(grade<0) grade = 0.0;
        title.append(": " + (int) grade.doubleValue() + "%");
        TextView gradeTv = new TextView(view.getContext());
        gradeTv.setTextSize(TypedValue.COMPLEX_UNIT_SP,15);
        gradeTv.setText(" ");
        linearLayout.addView(gradeTv);

        TierDBHandler.getInstance(getContext()).updateGrade((int)grade.doubleValue());

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

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_quiz, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        mView = view;
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
            rightAnswer();
            if(position!=-5) setTextsForMultipleChoice();

        }
        else if (position < kanjiQuestions.size() && !(userAnswer.equals(rightAnswer))) {
            animateIncorrectAns();
            wrongAnswer();
            setTextsForMultipleChoice();
        }
    }

    public void animateIncorrectAns() {
        final Animation animShake = AnimationUtils.loadAnimation(getContext(), R.anim.shake);
        cardView.startAnimation(animShake);
    }

    public void turnOff() {
        curQuestion.setOff();
    }

    public void wrongAnswer() {

        currentKanjiQuestion.setWrong(currentKanjiQuestion.getWrong()+1);

        if(wrongChars.get(currentKanjiQuestion.getAttachedKanji())==null) {
            wrongChars.put(currentKanjiQuestion.getAttachedKanji(), 1);
        }
        else {
            wrongChars.put(currentKanjiQuestion.getAttachedKanji(), 1+wrongChars.get(currentKanjiQuestion.getAttachedKanji()));
        }

        int max = kanjiQuestions.size();
        int randomNum = 0;
        if(max>1) {
            randomNum = newRand(position);
        }
        wrong++;
        questions_wrong.setText(wrong + " incorrect answers.");
        Collections.rotate(kanjiQuestions, -1);
        position = randomNum;
        currentKanjiQuestion = kanjiQuestions.get(position);
        curQuestion = currentKanjiQuestion.getOnQuestion();

        rightAnswer = curQuestion.getActualAnswer();
        currentKanjiID = curQuestion.getAnswers().get(curQuestion.getCorrectQuestionID()).getKanji().getID();
    }

    public int newRand(int exclude) {
        int max = kanjiQuestions.size();
        int randomNum = rand.nextInt(max);

        if(exclude==randomNum) {
            return newRand(exclude);
        }
        return randomNum;
    }

    public int calculateQuality(int wrong) {
        if(wrong==0)
            return 5;
        else if(wrong==1)
            return 3;
        else if(wrong==2)
            return 1;
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
        boolean checked = ((RadioButton) view).isChecked();

        switch(view.getId()) {
            case R.id.answer_1:
                if (checked)
                    userAnswer = ((RadioButton) view).getText().toString();
                    break;
            case R.id.answer_2:
                if (checked)
                    userAnswer = ((RadioButton) view).getText().toString();
                break;
            case R.id.answer_3:
                if (checked)
                    userAnswer = ((RadioButton) view).getText().toString();
                break;
            case R.id.answer_4:
                if (checked)
                    userAnswer = ((RadioButton) view).getText().toString();
                break;
            case R.id.answer_5:
                if (checked)
                    userAnswer = ((RadioButton) view).getText().toString();
                break;
        }
    }


}
