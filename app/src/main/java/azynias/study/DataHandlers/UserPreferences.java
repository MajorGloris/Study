package azynias.study.DataHandlers;

import azynias.study.ObjectModels.Bracket;
import azynias.study.ObjectModels.QuizSession;

/**
 * Created by Albedo on 6/25/2017.
 */

public class UserPreferences {
    private String tier;
    private int bracket;
    private int difficulty;
    private int position;
    private QuizSession reviewBracket;
    private Bracket studyBracket;
    private Boolean beginner;
    private int grade;
    private boolean multChoice;
    private String unlockedTier;

    private static UserPreferences sUserPreferences;

    public static synchronized UserPreferences getInstance() {
        if (sUserPreferences == null) {
            sUserPreferences = new UserPreferences();
        }
        return sUserPreferences;
    }

    private UserPreferences() {

    }

    public boolean isMultChoice() {
        return multChoice;
    }

    public void setMultChoice(String multChoice) {
        if(multChoice.contains("true")) {
            this.multChoice = true;
        }
        else {
            this.multChoice = false;
        }
    }

    public int getGrade() {
        return grade;
    }

    public void setGrade(int grade) {
        this.grade = grade;
    }

    public String getUnlockedTier() {
        return unlockedTier;
    }

    public void setUnlockedTier(String unlockedTier) {
        this.unlockedTier = unlockedTier;
    }

    public Boolean getBeginner() {
        return beginner;
    }

    public void setBeginner(Boolean beginner) {
        this.beginner = beginner;
    }

    public QuizSession getReviewBracket() {
        return reviewBracket;
    }

    public Bracket getStudyBracket() {
        return studyBracket;
    }

    public void setReviewBracket(QuizSession reviewBracket) {
        this.reviewBracket = reviewBracket;
    }

    public void setStudyBracket(Bracket studyBracket) {
        this.studyBracket = studyBracket;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
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

    public String getTierDB()
    {
        return "'" + tier + "'";
    }

    public String getTier() {
        return tier;
    }

    public void setTier(String tier) {
        this.tier = tier;
    }





}
