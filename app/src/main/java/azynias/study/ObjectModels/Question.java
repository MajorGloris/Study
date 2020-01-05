package azynias.study.ObjectModels;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Albedo on 7/5/2017.
 */

public class Question {
    private ArrayList<Answer> answers;

    private int correctQuestionID;
    private int wrong = 0;
    private boolean recall; // true = recall, false = recognition
    private boolean on = true; // true = on, false = off
    private Kanji kanji;
    private String actualAnswer;

    public Question(boolean recall) {
        this.recall = recall;
        this.answers = new ArrayList<Answer>();
    }

    public Question() {

    }

    public Kanji getKanji() {
        return kanji;
    }

    public void setKanji(Kanji kanji) {
        this.kanji = kanji;
    }

    public boolean isOn() {
        return on;
    }

    public void setOff() {
        this.on = false;
    }

    public String getActualAnswer() {
        return actualAnswer;
    }

    public void setActualAnswer(String actualAnswer) {
        this.actualAnswer = actualAnswer;
    }

    public boolean isRecall() {
        return recall;
    }

    public void setRecall(boolean recall) {
        this.recall = recall;
    }

    public int getWrong() {
        return wrong;
    }

    public void setWrong(int wrong) {
        this.wrong = wrong;
    }

    public int getQuality() {
        return wrong;
    }

    public void setQuality(int quality) {
        this.wrong = quality;
    }

    public void incWrong() {
        this.wrong = this.wrong+1;
    }

    public void addAnswers(ArrayList<Answer> answers) {
        this.answers = answers;
    }

    public int getCorrectQuestionID() {
        for(int i = 0;i<answers.size();i++) {
            if(answers.get(i).isCorrectAnswer()) {
                return i;
            }
        }
        return -1;
    }

    public void setCorrectQuestionID(int correctQuestionID) {
        this.correctQuestionID = correctQuestionID;
    }

    public ArrayList<Answer> getAnswers() {
        return answers;
    }

    public void addAnswer(Answer answer) {
        this.answers.add(answer);
    }

}
