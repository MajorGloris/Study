package azynias.study.DataHandlers;

/**
 * Created by Albedo on 6/25/2017.
 */

public class UserPreferences {
    private String tier;
    private int bracket;
    private int difficulty;
    private int unlockedTiers;
    private int position;

    private static UserPreferences sUserPreferences;

    public static synchronized UserPreferences getInstance() {
        if (sUserPreferences == null) {
            sUserPreferences = new UserPreferences();
        }
        return sUserPreferences;
    }

    private UserPreferences() {

    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getUnlockedTiers() {
        return unlockedTiers;
    }

    public void setUnlockedTiers(int unlockedTiers) {
        this.unlockedTiers = unlockedTiers;
    }

    public int getBracket() {
        return bracket;
    }

    public void setBracket(int bracket) {
        this.bracket = bracket;
    }

    public int getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(int difficulty) {
        this.difficulty = difficulty;
    }

    public String getTier() {
        return tier;
    }

    public void setTier(String tier) {
        this.tier = tier;
    }





}
