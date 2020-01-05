package azynias.study.ObjectModels;

/**
 * Created by Albedo on 8/4/2017.
 */

public class RecallQuestion extends Question {
    private Kanji character;
    private String answerChar;


    public RecallQuestion(Kanji character, String answer) {
        this.character = character;
        this.setKanji(character);
        this.setWrong(0);
        this.answerChar = answer;
        this.setRecall(true);

    }

    public Kanji getCharacter() {
        return character;
    }

    public void setCharacter(Kanji character) {
        this.character = character;
    }

    public String getAnswer() {
        return answerChar;
    }

    public void setAnswer(String answer) {
        this.answerChar = answer;
    }

   /* public Answer getAnswerDS() {
        return answerDS;
    }

    public void setAnswerDS(Answer answerDS) {
        this.answerDS = answerDS;
    } */
}
