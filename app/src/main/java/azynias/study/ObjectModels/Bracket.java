package azynias.study.ObjectModels;

import java.util.ArrayList;

/**
 * Created by Albedo on 6/27/2017.
 */

public class Bracket {
    private ArrayList<Kanji> kanjiChars;
    private int id;
    private String tierBelong;
    private int beginPosition = 0;
    private int endPosition = 0;


    public Bracket(int id, String tierBelong) {
        this.kanjiChars = new ArrayList<Kanji>();
        this.id = id;
        this.tierBelong = tierBelong;
    }

    public String getTierBelong() {
        return tierBelong;
    }

    public void setTierBelong(String tierBelong) {
        this.tierBelong = tierBelong;
    }

    public ArrayList<Kanji> getKanjiChars() {
        return kanjiChars;
    }

    public void setKanjiChars(ArrayList<Kanji> kanjiChars) {
        this.kanjiChars = kanjiChars;
    }

    public void addKanjiToBracket(Kanji kanji) {
        this.kanjiChars.add(kanji);
        endPosition++;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getEndPosition() {
        return endPosition;
    }

    public void setEndPosition(int endPosition) {
        this.endPosition = endPosition;
    }

    public int getBeginPosition() {
        return beginPosition;
    }

    public void setBeginPosition(int beginPosition) {
        this.beginPosition = beginPosition;
    }



    @Override
    public String toString() {
        StringBuilder listOfChars = new StringBuilder();

        for(Kanji kanji : this.kanjiChars) {
            String character = kanji.getCharacter();
            listOfChars.append(character + " ");
        }

        return listOfChars.toString();
    }

}
