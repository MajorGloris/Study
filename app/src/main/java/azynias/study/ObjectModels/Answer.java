package azynias.study.ObjectModels;

/**
 * Created by Albedo on 7/5/2017.
 */

public class Answer {
    private boolean correctAnswer;
    private Kanji kanji;
    private String answer;

    public Answer(Kanji kanji, boolean correctAnswer) {
        this.kanji = kanji;
        this.correctAnswer = correctAnswer;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public Kanji getKanji() {
        return kanji;
    }

    public void setKanji(Kanji kanji) {
        this.kanji = kanji;
    }

    public boolean isCorrectAnswer() {
        return correctAnswer;
    }

    public void setCorrectAnswer(boolean correctAnswer) {
        this.correctAnswer = correctAnswer;
    }




}
