package azynias.study.ObjectModels;

import java.util.ArrayList;

import azynias.study.ObjectModels.Bracket;

/**
 * Created by Albedo on 6/24/2017.
 */

public class Tier {
    private String tierName;
    private int kanjiCount;
    private int spectrum;
    private boolean free;
    private ArrayList<Bracket> characters;


    public Tier(String tierName) {
        this.tierName = tierName;
        this.characters = new ArrayList<Bracket>();

    }

    public ArrayList<Bracket> getCharacters() {
        return this.characters;
    }

    public void setCharacters(ArrayList<Bracket> characters) {
        this.characters = characters;
    }

    public void addBracket(Bracket bracket) {
        this.characters.add(bracket);
    }

    public String getTierName() {
        return tierName;
    }

    public void setTierName(String tierName) {
        this.tierName = tierName;
    }

    public int getKanjiCount() {
        return kanjiCount;
    }

    public void setKanjiCount(int kanjiCount) {
        this.kanjiCount = kanjiCount;
    }

    public int getSpectrum() {
        return spectrum;
    }

    public void setSpectrum(int spectrum) {
        this.spectrum = spectrum;
    }

    public boolean isFree() {
        return free;
    }

    public void setFree(boolean free) {
        this.free = free;
    }

}
