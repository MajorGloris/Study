package azynias.study.ObjectModels;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Albedo on 7/18/2017.
 */

public class KanjiQuestion {
    private int wrong;
    private int right;
    private String rightKanjiChar;
    private String rightKanjiMeaning;
    private List<Question> questions;
    private Kanji attachedKanji;

    public KanjiQuestion(String rightKanjiChar, String rightKanjiMeaning) { // recog, recall
        this.wrong = 0;
        this.right = 0;
        this.rightKanjiChar = rightKanjiChar;
        this.rightKanjiMeaning = rightKanjiMeaning;
        this.questions = new ArrayList<Question>();
    }

    public Kanji getAttachedKanji() {
        return attachedKanji;
    }

    public void setAttachedKanji(Kanji attachedKanji) {
        this.attachedKanji = attachedKanji;
    }

    public int getWrong() {
        return wrong;
    }

    public void setWrong(int wrong) {
        this.wrong = wrong;
    }

    public int getRight() {
        return right;
    }

    public void setRight(int right) {
        this.right = right;
    }

    public void addQuestion(Question question) {
        this.questions.add(question);
    }

    public String getRightKanjiChar() {
        return rightKanjiChar;
    }

    public void setRightKanjiChar(String rightKanjiChar) {
        this.rightKanjiChar = rightKanjiChar;
    }

    public String getRightKanjiMeaning() {
        return rightKanjiMeaning;
    }

    public void setRightKanjiMeaning(String rightKanjiMeaning) {
        this.rightKanjiMeaning = rightKanjiMeaning;
    }

    public List<Question> getQuestions() {
        return questions;
    }


    public Question getRecallQuestion() {
        for(Question x : this.questions) {
            if(x.isRecall()) {
                return x;
            }
        }
        return null;
    }

    public Question getRecogQuestion() {
        for(Question x : this.questions) {
            if(!(x.isRecall())) {
                return x;
            }
        }
        return null;
    }

    public Question getOnQuestion() {
        for(Question x : this.questions) {
            if(x.isOn()) {
                return x;
            }
        }
        return null;
    }

    public boolean finished() {
        for(Question x : this.questions) {
            if(x.isOn()) {
                return false;
            }
        }
        return true;
    }
}
