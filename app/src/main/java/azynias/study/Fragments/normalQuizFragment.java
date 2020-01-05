package azynias.study.Fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.googlecode.tesseract.android.TessBaseAPI;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import azynias.study.Activities.FrontActivity;
import azynias.study.DataHandlers.TierDBHandler;
import azynias.study.ObjectModels.Kanji;
import azynias.study.ObjectModels.Question;
import azynias.study.ObjectModels.QuizSession;
import azynias.study.ObjectModels.RecallQuestion;
import azynias.study.ObjectModels.RecogQuestion;
import azynias.study.ObjectModels.SimpleDrawView;
import azynias.study.R;

public class normalQuizFragment extends Fragment {
    private QuizSession qs;
    private SimpleDrawView draw;
    private ImageButton next;
    private TextView quiz_area;
    private EditText answerET;
    private ImageButton clearDraw;
    private TextView questions_wrong;
    private TextView questions_left;

    private ArrayList<Question> questions;
    private HashMap<Kanji, Integer> popQueue;

    private int currentKanjiID = 0;
    private int position = 0;
    private int wrong = 0;
    private int questionsLeft;
    private int total = 0;
    private String userAnswer = "";
    private String rightAnswer; // the idea is to update these within every increment

    private HashMap<Kanji, Integer> wrongChars;

    private int questionsAmount;
    private Question curQuestion;

    protected View mView;

    private Bitmap image;
    private String datapath = "";
    private TessBaseAPI tess;
    Random rand;

    TierDBHandler tierDBHandler = TierDBHandler.getInstance(getContext());

    public void init(View view) {
        rand = new Random();

        questions = TierDBHandler.getInstance(getContext()).grabNormalQuestions();
        wrongChars = new HashMap<>();
        popQueue = new HashMap<>();
        curQuestion = questions.get(position);
        questionsLeft = questions.size();
        total = questions.size();
        questionsLeft = total;

        next = (ImageButton) view.findViewById(R.id.normal_quiz_next_question);
        quiz_area = (TextView) view.findViewById(R.id.normal_quiz_area_kanji);
        clearDraw = (ImageButton) view.findViewById(R.id.clear_quiz_normal);
        answerET = (EditText) view.findViewById(R.id.normal_answer_et);
        draw = (SimpleDrawView) view.findViewById(R.id.normal_quiz_draw);

        questions_left = (TextView) view.findViewById(R.id.normal_questions_left_quiz);
        questions_left.setText(questionsLeft + " kanji questions left.");
        questions_wrong = (TextView) view.findViewById(R.id.normal_questions_wrong_quiz);
        questions_wrong.setText(wrong + " incorrect answers.");

        setQuestion();

    }

    public void progFinish() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_progress, null);

        TextView title = (TextView) view.findViewById(R.id.title_dialog);
        int totalS = wrongChars.size();
        LinearLayout linearLayout = (LinearLayout) view.findViewById(R.id.prog_dialog);


        for(Map.Entry<Kanji, Integer> entry : wrongChars.entrySet()) {
            LinearLayout layout = new LinearLayout(view.getContext());
            layout.setOrientation(LinearLayout.VERTICAL);
            layout.setLayoutParams(new LinearLayoutCompat.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

            LinearLayoutCompat.LayoutParams centerStory = new LinearLayoutCompat.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
            centerStory.gravity = Gravity.CENTER;

            TextView wrongChar = new TextView(view.getContext());
            TextView story = new TextView(view.getContext());
            wrongChar.setLayoutParams(centerStory);
            TextView incorrect = new TextView(view.getContext());
            wrongChar.setText(entry.getKey().getCharacter());
            wrongChar.setTextColor(Color.BLACK);

            wrongChar.setTextSize(TypedValue.COMPLEX_UNIT_SP,40);

            incorrect.setText("answered incorrectly: " + entry.getValue() + " times(s)");
            incorrect.setPaddingRelative(0, 0, 0, 5);
            story.setText(entry.getKey().getStory());
            story.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);

            incorrect.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);

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

    public void setQuestion() {
        if(curQuestion.isRecall()) {
            String kanjiNameQuestion = ((RecallQuestion) curQuestion).getCharacter().getName();
            Log.d("Hi", kanjiNameQuestion);
            quiz_area.setTextSize(TypedValue.COMPLEX_UNIT_SP, 50);
            quiz_area.setText(kanjiNameQuestion);

            initializeDrawingBoard();
        }
        else {
            String kanjiCharacterQuestion = ((RecogQuestion) curQuestion).getKanji().getCharacter();
            quiz_area.setTextSize(TypedValue.COMPLEX_UNIT_SP, 85);

            quiz_area.setText(kanjiCharacterQuestion);
            initAnswerBoard();
        }
    }


    public void initializeDrawingBoard() {
        hideSoftKeyboard();
        answerET.setVisibility(View.GONE);
        draw.setVisibility(View.VISIBLE);
    }

    public void initAnswerBoard() {
        draw.setVisibility(View.GONE);
        answerET.setVisibility(View.VISIBLE);
    }

    public void nextQuestion(boolean right) {
        if(right) {
            popAnswer(curQuestion.getKanji(), curQuestion.getWrong());
            questions.remove(position);
            questionsLeft--;
            questions_left.setText(questionsLeft + " kanji questions left.");
            if(questions.size()<=0) {
                progFinish();
            }
            else {
                curQuestion = questions.get(position);
                Log.d("Size of ques", "" + questions.size());
                setQuestion();
            }
        }
        else {
            if(wrongChars.get(curQuestion.getKanji())==null) {
                wrongChars.put(curQuestion.getKanji(), 1);
            }
            else {
                wrongChars.put(curQuestion.getKanji(), 1+wrongChars.get(curQuestion.getKanji()));
            }
            animateIncorrectAns();
            wrong++;
            questions_wrong.setText(wrong + " incorrect answers.");
            curQuestion.setWrong(curQuestion.getWrong()+1);
            Collections.rotate(questions, -1);
            curQuestion = questions.get(position);
            setQuestion();
        }
    }

    public void animateIncorrectAns() {
        final Animation animShake = AnimationUtils.loadAnimation(getContext(), R.anim.shake);
        getView().findViewById(R.id.normal_shake).startAnimation(animShake);
    }

    public void popAnswer(Kanji kanji, int wrong) {
        if(evaluatePop(kanji)) {
            int totalWrong = wrong + popQueue.get(kanji);
            sendToDB(kanji, totalWrong);
            popQueue.remove(kanji);
        }
        popQueue.put(kanji, wrong);
    }

    public boolean evaluatePop(Kanji kanji) {
        if(popQueue.get(kanji)!=null) {
            return true;
        }
        return false;
    }

    public void sendToDB(Kanji kanji, int totalWrong) {
        int quality = calculateQuality(totalWrong);
        if(quality>=3)
            tierDBHandler.correctKanjiDate(quality, kanji.getID());
        else {
            tierDBHandler.incorrectDueDate(kanji.getID());
        }
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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_normal_quiz, container, false);

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        mView = view;
        tess = new TessBaseAPI();
        datapath = getContext().getFilesDir()+ "/tesseract/";
        checkFile(new File(datapath + "tessdata/"), "jpn");
        checkFile(new File(datapath + "tessdata/"), "jpn2");
        setVariances();
        tess.init(datapath, "jpn+jpn2");
        init(view);
        clearDraw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(draw.getVisibility()==View.VISIBLE) {
                    draw.clear();
                }
                else {
                    Toast msg = Toast.makeText(getContext(), "Drawing board is not open!", Toast.LENGTH_SHORT);
                    msg.show();
                }
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                nextQuestion(rightOrWrong());
                answerET.setText("");
            }
        });

    }

    public void hideSoftKeyboard() {
        InputMethodManager inputManager = (InputMethodManager) getContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE);

        // check if no view has focus:
        View v = ((Activity) getContext()).getCurrentFocus();
        if (v == null)
            return;

        inputManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

    public boolean rightOrWrong() {
        if(curQuestion.isRecall()) {
            String userAnswer = processImage();
            String rightAnswer = ((RecallQuestion) curQuestion).getAnswer();
            Log.d("Run r-o-w", userAnswer + " | " + rightAnswer);
            if(userAnswer.equals(rightAnswer)) {
                return true;
            }
            return false;
        }
        else {
            String userAnswer = answerET.getText().toString().toLowerCase();
            String rightAnswer = ((RecogQuestion) curQuestion).getNameOfKanji().toLowerCase();
            Log.d("Run r-o-w RECOG", userAnswer + " | " + rightAnswer);

            if(userAnswer.equals(rightAnswer)) {
                return true;
            }
            return false;
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    private void copyFiles(String fileLan) {
        try {
            //location we want the file to be at
            String filepath = datapath + "/tessdata/" + fileLan + ".traineddata";

            //get access to AssetManager
            AssetManager assetManager = getContext().getAssets();

            //open byte streams for reading/writing
            InputStream instream = assetManager.open("tessdata/jpn.traineddata");
            OutputStream outstream = new FileOutputStream(filepath);

            //copy the file to the location specified by filepath
            byte[] buffer = new byte[1024];
            int read;
            while ((read = instream.read(buffer)) != -1) {
                outstream.write(buffer, 0, read);
            }
            outstream.flush();
            outstream.close();
            instream.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String processImage(){
        Bitmap well = draw.getBitmap();
        Bitmap save = Bitmap.createBitmap(300, 140, Bitmap.Config.ARGB_8888);
        Paint paint = new Paint();
        paint.setColor(Color.WHITE);
        Canvas now = new Canvas(save);
        now.drawRect(new Rect(0,0,300,140), paint);
        now.drawBitmap(well, new Rect(0,0,well.getWidth(),well.getHeight()), new Rect(0,0,300,140), null);
        tess.setPageSegMode(TessBaseAPI.PageSegMode.PSM_SINGLE_CHAR); // for some reason this behaves better when its after init.
        tess.setImage(save);


        String ocrCheck = tess.getUTF8Text();
        Log.d("hey", tess.meanConfidence()+ " confidence");

        return ocrCheck;
    }

    public void setVariances() {
        /*tess.setVariable("chop_enable", "T");
        tess.setVariable("use_new_state_cost","F");
        tess.setVariable("segment_segcost_rating","F");
        tess.setVariable("enable_new_segsearch","0");
not sure if this is still useful on your trained data.
        tess.setVariable("language_model_ngram_on","0");
        tess.setVariable("textord_force_make_prop_words","F");
        tess.setVariable("edges_max_children_per_outline","40");*/
        String charsBlackList = "…ー】』ロ《‥\\ん]ル〕U〔ヽ”";
        tess.setVariable(TessBaseAPI.VAR_CHAR_BLACKLIST, charsBlackList);
    }

    private void checkFile(File dir, String fileLan) {
        //directory does not exist, but we can successfully create it
        if (!dir.exists()&& dir.mkdirs()){
            copyFiles(fileLan);
        }
        //The directory exists, but there is no data file in it
        if(dir.exists()) {
            String datafilepath = datapath+ "/tessdata/jpn.traineddata";
            String datafilespath = datapath+ "/tessdata/" + fileLan + ".traineddata";
            File datafile = new File(datafilespath);
            if (!datafile.exists()) {
                copyFiles(fileLan);
            }
        }
    }


}
