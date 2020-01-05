package azynias.study.ObjectModels;

/**
 * Created by Albedo on 8/31/2017.
 */

public class RecogQuestion extends Question {
    private Kanji kanji;
    private String nameOfKanji;

    public RecogQuestion(Kanji kanji, String nameOfKanji) {
        this.kanji = kanji;
        this.setKanji(kanji);
        this.setWrong(0);
        this.nameOfKanji = nameOfKanji;
        this.setRecall(false);
    }

    public Kanji getKanji() {
        return kanji;
    }

    public void setKanji(Kanji kanji) {
        this.kanji = kanji;
    }

    public String getNameOfKanji() {
        return nameOfKanji;
    }

    public void setNameOfKanji(String nameOfKanji) {
        this.nameOfKanji = nameOfKanji;
    }
}
