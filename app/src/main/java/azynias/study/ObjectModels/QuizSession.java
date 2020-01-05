package azynias.study.ObjectModels;

import java.util.ArrayList;

/**
 * Created by Albedo on 7/5/2017.
 */

public class QuizSession {
    private ArrayList<String> kanjiChars;
    private ArrayList<KanjiQuestion> questions;
    private int maxQuestions = 8;

    public QuizSession(ArrayList<KanjiQuestion> questions) {
        this.questions = questions;
    //    this.maxQuestions = maxQuestions;
    }



    public void addQuestion(KanjiQuestion question) {
        this.questions.add(question);
    }

    public ArrayList<String> getKanjiChars() {
        return kanjiChars;
    }


    public void setChars(ArrayList<String> chars) {
        this.kanjiChars = chars;
    }

    public ArrayList<KanjiQuestion> getQuestions() {
        return questions;
    }

    public void setQuestions(ArrayList<KanjiQuestion> questions) {
        this.questions = questions;
    }

    public int getMaxQuestions() {
        return maxQuestions;
    }

    public void setMaxQuestions(int maxQuestions) {
        this.maxQuestions = maxQuestions;
    }

    @Override
    public String toString() {
        StringBuilder x = new StringBuilder();
        for(KanjiQuestion question : questions) {
            for(Question xs : question.getQuestions()) {
                x.append(xs.getActualAnswer());
            }
        }
        return x.toString();
    }

}
