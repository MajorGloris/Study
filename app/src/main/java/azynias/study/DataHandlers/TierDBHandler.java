package azynias.study.DataHandlers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import azynias.study.Algorithms.spacedRepAlgo;
import azynias.study.ObjectModels.Answer;
import azynias.study.ObjectModels.Bracket;
import azynias.study.ObjectModels.RecallQuestion;
import azynias.study.ObjectModels.Kanji;
import azynias.study.ObjectModels.KanjiQuestion;
import azynias.study.ObjectModels.Question;
import azynias.study.ObjectModels.QuizSession;
import azynias.study.ObjectModels.RecogQuestion;
import azynias.study.ObjectModels.Tier;

/**
 * Created by Albedo on 6/24/2017.
 * NOTE - YOU MUST ALWAYS UPDATE USERPREFS WHEN UPDATING DATABASE.
 */

public class TierDBHandler extends SQLiteAssetHelper {
    private static final String DATABASE_NAME = "Tiers.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_TIERS = "Tiers";

    private static final String KEY_KANJI_ID = "id";
    private static final String RADICALS = "Radicals";
    private static final String KANJI_CHARACTER = "Character";
    private static final String KANJI_NAME = "Name";

    private static final String KANJI_STORY = "Story";
    private static final String KANJI_BRACKET = "bracket";
    private static final String KANJI_DUEDATE = "next_due_date";
    private static final String KANJI_EASINESS = "easiness";
    private static final String KANJI_CORRECT_ANSWERS = "correct_answers";
    private static final String KANJI_CONSECUTIVE_ANSWERED = "consecutive_answered";
    private static final String KANJI_DATE_REVIEWED = "prev_days_interval";


    private static final String USER_SETTINGS = "User_Settings";
    private static final String USER_TIER = "Tier";
    private static final String USER_BRACKET = "Bracket";
    private static final String USER_DIFFICULTY = "daily_exercise";
    private static final String USER_FREE = "free";
    private static final String USER_GRADE = "grade";
    private static final String USER_UNLOCKED_TIER = "unlocked_tier";
    private static final String USER_MULT_CHOICE = "mult_choice";


    private UserPreferences UserPrefs = UserPreferences.getInstance();
    private spacedRepAlgo smAlgo = new spacedRepAlgo();
    private static TierDBHandler sInstance;

    private static final int HOURS_8_MILI = 28800000;

    private TierDBHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static synchronized TierDBHandler getInstance(Context context) {
        // Use the application context, which will ensure that you
        // don't accidentally leak an Activity's context.
        // See this article for more information: http://bit.ly/6LRzfx
        if (sInstance == null) {
            sInstance = new TierDBHandler(context.getApplicationContext());
        }
        return sInstance;
    }

    public void updateGrade(int newGr) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        String query = "SELECT * FROM " + USER_SETTINGS;
        Cursor cursor = db.rawQuery(query, null);

        cursor.moveToFirst();
        double previous = cursor.getDouble(cursor.getColumnIndex(USER_GRADE));

        int newGrade = newGr + (int)previous;
        values.put(USER_GRADE, newGrade/2);
        db.update(USER_SETTINGS, values, null, null);
        db.close();
        setUserPrefs();
    }

    public void setKanjiStory(String story, int kanjiID) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KANJI_STORY, story); // to change story of Kanji
        db.update(TABLE_TIERS, values, "id=" + kanjiID, null);

        db.close();
    }


    public void incorrectDueDate(int kanjiID) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KANJI_DUEDATE, System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(1440)); // WILL SET DUE date 24 hours from time
        values.put(KANJI_CONSECUTIVE_ANSWERED, 0);
        values.put(KANJI_DATE_REVIEWED, 1);
        db.update(TABLE_TIERS, values, "id=" + kanjiID, null);

        db.close();
    }

    public void updateTiersPurchase(String toWhatTier) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        Log.d("tierDB", "'"+ toWhatTier+ "'");
        values.put(USER_UNLOCKED_TIER, toWhatTier);
        db.update(USER_SETTINGS, values, null, null);
        db.close();
        setUserPrefs();
    }

    public void updateQuizChoice(String choice) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(USER_MULT_CHOICE, choice);
        db.update(USER_SETTINGS, values, null, null);
        db.close();
        setUserPrefs();
    }

    public void setInitialDueDate() {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        String where = "bracket=? AND Tier=?";
        String wheres = "bracket = " + UserPrefs.getBracket() + " AND Tier = " + UserPrefs.getTierDB();
        String[] whereArgs = new String[] {String.valueOf(UserPrefs.getBracket()), UserPrefs.getTierDB()}; // doesn't work for some reason...

        values.put(KANJI_DUEDATE, System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(1440));
        db.update(TABLE_TIERS, values, wheres, null);
        db.close();
    }

    public void updateDifficulty(int value) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(USER_DIFFICULTY, value);
        db.update(USER_SETTINGS, values, null, null);
        db.close();
        setUserPrefs();
    }

    public void correctKanjiDate(int quality, int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        String queryForInterval = "SELECT * FROM " + TABLE_TIERS + " WHERE id = " + id;
        Cursor cursor = db.rawQuery(queryForInterval, null);

        int correct = 0;
        float EF = 0f;
        float newEF = 0f;
        long curDate = 0;
        int dateReviewed = 0;
        long date = 0;
        long untouchedDate = 0;

        try {
            if(cursor.moveToFirst()) {
                correct = cursor.getInt(cursor.getColumnIndex(KANJI_CONSECUTIVE_ANSWERED));

                EF = cursor.getFloat(cursor.getColumnIndex(KANJI_EASINESS));
                curDate = cursor.getLong(cursor.getColumnIndex(KANJI_DUEDATE));
                dateReviewed = cursor.getInt(cursor.getColumnIndex(KANJI_DATE_REVIEWED));

                if(quality>=3) newEF = smAlgo.calcEF(EF, quality);
                else {
                    if(EF-0.6>1.3) {
                        newEF -= 0.45;
                    }
                }
                untouchedDate = curDate;


                curDate = smAlgo.calcNextDay(correct+1, newEF, dateReviewed);

                Log.d("ef", ""+newEF);
                date = smAlgo.convertToMS((int)curDate) + System.currentTimeMillis();

                Date dates = new Date();
                dates.setTime((long)date);

                Log.d("chesk", ""+dates.toString());
                dateReviewed = smAlgo.convertToDays(date) - smAlgo.convertToDays(System.currentTimeMillis());
                Log.d("days", ""+dateReviewed);

            }
        }
        catch (Exception e) {
            Log.d("Error setting date", e.toString());
        }
        finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        if(readyToIncrementDate(untouchedDate)) {
            values.put(KANJI_DUEDATE, date);
            values.put(KANJI_CONSECUTIVE_ANSWERED, correct+1);
            values.put(KANJI_DATE_REVIEWED, dateReviewed);
            values.put(KANJI_EASINESS, newEF);
        }
        else {
            values.put(KANJI_DUEDATE, untouchedDate+8640000); // this way, the algorithm for spacing wont get fucked up if user
        }

        db.update(TABLE_TIERS, values, KEY_KANJI_ID + " = " + id, null);
        db.close();
    }


    public boolean readyToIncrementDate(long date) {
        if(date>System.currentTimeMillis()) {
            return false;
        }
        return true;
    }

    public void arrangeTiers() {
        int bronzeMod = 266%8;
        int copperMod = (570-267)%8;
        int silverMod = (971 - 571)%8;
        int goldMod = (1372 - 972)%8;
        String[] tiers = {"Bronze", "Copper", "Silver", "Gold"};
        int[] howMany = {bronzeMod, copperMod, silverMod, goldMod};
        int[] brackets = {266/15, (570-267)/15, (971-571)/15, (1372-972)/15};
        SQLiteDatabase db = this.getWritableDatabase();
        int begin = 1;
        int end = begin + 15;
        int store = 0;

        for(int i = 0;i<4;i++) {
            String tier = tiers[i];
            int noob = howMany[i];
            int bracket = brackets[i];
            Log.d("bracket", bracket+" h");
            for(int j = 1;j<=bracket;j++) {
                ContentValues values = new ContentValues();
                values.put(KANJI_BRACKET, j);
                String where = "id>=? AND id<=? AND Tier=?";
                String[] whereArgs = {String.valueOf(begin), String.valueOf(end), tier};
                db.update(TABLE_TIERS, values, where, whereArgs);
                begin+=15;
                end+=15+begin;
                store = j;
                Log.d("begin end", ""+begin+" "+ end);
            }

            if(noob!=0) {
                ContentValues values = new ContentValues();
                values.put(KANJI_BRACKET, store+1);
                String where = "id > " + end + " AND Tier = '" + tier + "'";
                db.execSQL("UPDATE Tiers SET bracket = " + (store+1) + " WHERE id > " + end + " AND id <= " + (end+noob) + " AND Tier = '" + tier + "'");
              //  db.update(TABLE_TIERS, values, where, null);

                begin = noob + end + 1;
                end = begin + 15;
            }

        }

        db.close();
    }

    public void initialize() {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("beginner", "false");
        db.update(USER_SETTINGS, values, null, null);
    }



    public int getTierDBKanjiAmt() {
        String query = "SELECT Count(*) FROM " + TABLE_TIERS + " WHERE Tier = " + UserPrefs.getTierDB();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(query, null);

        cursor.moveToFirst();
        int count = cursor.getInt(cursor.getColumnIndex("Count(*)"));

        cursor.close();
        db.close();
        return count;
    }

    public void getNigger() {
        String grabInfoQuery = "SELECT bracket, id, next_due_date FROM Bronze";
        //  String grabInfoQuery = "SELECT Tier, Bracket FROM User_Settings";
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(grabInfoQuery, null);

        cursor.moveToFirst();
        try {

            do {
                int bracket = cursor.getInt(cursor.getColumnIndex("bracket"));
                Log.d("bracket", Integer.toString(bracket));
                ///Log.d("hi", Integer.toString(cursor.getColumnIndex("id")));
            } while(cursor.moveToNext());

        }
        catch (Exception e) {
            Log.d("Failed to set user", e.toString());
        }
        finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
                db.close();

            }
        }
    }

    public void setUserPrefs() {
        String grabInfoQuery = String.format("SELECT * FROM %s",
                USER_SETTINGS
                );
        Log.d("user", "run");
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(grabInfoQuery, null);

        cursor.moveToFirst();
        try {
            UserPrefs.setTier(cursor.getString(cursor.getColumnIndex(USER_TIER)));
            UserPrefs.setBracket(cursor.getInt(cursor.getColumnIndex(USER_BRACKET)));
            UserPrefs.setDifficulty(cursor.getInt(cursor.getColumnIndex(USER_DIFFICULTY)));
            UserPrefs.setReviewBracket(grabQuestions());
            UserPrefs.setUnlockedTier(cursor.getString(cursor.getColumnIndex(USER_UNLOCKED_TIER)));
            UserPrefs.setGrade(cursor.getInt(cursor.getColumnIndex(USER_GRADE)));
            UserPrefs.setMultChoice(cursor.getString(cursor.getColumnIndex(USER_MULT_CHOICE)));

            String beginner = cursor.getString(cursor.getColumnIndex("beginner"));

            if(beginner.equals("true")) {
                UserPrefs.setBeginner(true);
            }
            else {
                UserPrefs.setBeginner(false);
            }


            UserPrefs.setStudyBracket(arrangeStudyLevel());
        }
        catch (Exception e) {
            Log.d("Failed to set user", e.toString());
        }
        finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
                db.close();

            }
        }
    }

    public int itemsDueToday() {
        SQLiteDatabase db = this.getReadableDatabase();
        Long currentDay = System.currentTimeMillis();
        int ctr = 0;

        String query = "SELECT " + KANJI_DUEDATE + " FROM " + TABLE_TIERS + " WHERE " + KANJI_DUEDATE
                + " < " + currentDay;
        Cursor cursor = db.rawQuery(query, null);

        try {
            if (cursor.moveToFirst()) {
                do {
                    ctr++;
                } while (cursor.moveToNext());
            }
        }
        catch (Exception e) {
            Log.d("error due items", e.toString());
        }
        finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
                db.close();
            }
        }
        return ctr;
    }


    public Tier arrangeBracketsForTier(String userSelectedTier) {
        SQLiteDatabase db = this.getReadableDatabase();
        Tier selectedTier = new Tier(userSelectedTier);

        int initialBracket = 1;

        Bracket bracket = new Bracket(initialBracket, userSelectedTier);
        String tier = userSelectedTier;
        String queryForTier = "SELECT * FROM Tiers WHERE Tier = '" +  tier.substring(0,1).toUpperCase() + tier.substring(1) + "'";
        Cursor cursor = db.rawQuery(queryForTier, null);
        Log.d("cursor size", ""+cursor.getCount());
        try {
            if(cursor.moveToFirst()) {
                do {
                    if (initialBracket == cursor.getInt(cursor.getColumnIndex("bracket"))) {
                        Kanji kanjiOfRow = grabKanji(cursor);
                        bracket.addKanjiToBracket(kanjiOfRow);
                    } else {
                        selectedTier.addBracket(bracket);
                        initialBracket++;
                        bracket = new Bracket(initialBracket, userSelectedTier);

                        Kanji kanjiOfRow = grabKanji(cursor);
                        bracket.addKanjiToBracket(kanjiOfRow); // for some reason, this isn't grabbing the last row
                    }

                } while (cursor.moveToNext());
            }
        }
        catch (Exception e) {
            Log.d("ERROR-ARRANGE BRACKET", "There was an error arranging the brackets.");
        }
        finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
                db.close();

            }
        }
        return selectedTier;

    }

    public Bracket arrangeStudyLevel() {

        Bracket userLevelBracket = new Bracket(UserPrefs.getBracket(), UserPrefs.getTierDB());
        SQLiteDatabase db = this.getReadableDatabase();

        String queryForBracketLevel = String.format("SELECT * FROM %s WHERE %s = %s AND Tier = %s",
                TABLE_TIERS, KANJI_BRACKET, userLevelBracket.getId(), UserPrefs.getTierDB());

        Cursor cursor = db.rawQuery(queryForBracketLevel, null);

        try {
            if(cursor.moveToFirst()) {
                do {
                    Kanji kanjiRow = grabKanji(cursor);
                    userLevelBracket.addKanjiToBracket(kanjiRow);
                } while(cursor.moveToNext());
            }
        }
        catch (Exception e) {
            Log.e("Bracket level", "Error arranging for bracket level on study.");
        }
        finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
                db.close();
            }
        }
        return userLevelBracket;
    }

    public Kanji grabKanji(Cursor cursor) {
        Kanji kanjiRow = new Kanji(
                cursor.getString(cursor.getColumnIndex("Radicals")), cursor.getString(cursor.getColumnIndex("Name")),
                cursor.getInt(cursor.getColumnIndex("id")), cursor.getString(cursor.getColumnIndex("Story")),
                cursor.getInt(cursor.getColumnIndex("bracket"))
        );
        kanjiRow.setCharacter(cursor.getString(cursor.getColumnIndex("Character")));

        long date = cursor.getLong(cursor.getColumnIndex(KANJI_DUEDATE));
        Date dates = new Date();
        dates.setTime(date);
        kanjiRow.setDueDate(dates);
        return kanjiRow;
    }

    public void incrementBracket() {
        boolean isTierIncremented = incrementTier();
        SQLiteDatabase db = this.getReadableDatabase();

        ContentValues values = new ContentValues();
        if(!isTierIncremented) { // basically not time to increment tier

            values.put(USER_BRACKET, UserPrefs.getBracket()+1);
            db.update(USER_SETTINGS, values, null, null);
            UserPrefs.setBracket(UserPrefs.getBracket()+1);
        }
        db.close();
    }

    public boolean incrementTier() {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        String maxQuery = String.format("SELECT MAX(%s) FROM %s WHERE Tier = %s", KANJI_BRACKET, TABLE_TIERS, UserPrefs.getTierDB());
        Cursor cursor = db.rawQuery(maxQuery, null);

        cursor.moveToFirst();
        int max = cursor.getInt(cursor.getColumnIndex("MAX(bracket)"));

        if(UserPrefs.getBracket() >= max) {
            String newTier = tierHierachy(UserPrefs.getTier());
            values.put(USER_TIER, newTier); // will return upper tier, make sure this staples where user is at in tiers
            values.put(USER_BRACKET, 1);
            db.update(USER_SETTINGS, values, null, null);
            UserPrefs.setTier(newTier);
            db.close();
            cursor.close();
            return true;
        }
        db.close();
        cursor.close();
        return false;
    }


    public String tierHierachy(String preceedingTier) {

        if(preceedingTier.equals("Bronze"))
            return "Copper";
        if(preceedingTier.equals("Copper"))
            return "Silver";
        if(preceedingTier.equals("Silver"))
            return "Gold";
        if(preceedingTier.equals("Gold"))
            return "Platinum";
        if(preceedingTier.equals("Platinum"))
            return "Diamond";
        return "";
    }


    public ArrayList<Question> grabNormalQuestions() {
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT * FROM ( SELECT * FROM Tiers ORDER BY next_due_date ASC) WHERE next_due_date IS NOT NULL AND next_due_date > 0;";
        Cursor cursor = db.rawQuery(query, null);

        ArrayList<Question> questions = new ArrayList<Question>();
        Log.d("lists", Integer.toString(cursor.getCount()));
        int lim = 0;
        QuizSession session;

        try {
            if(cursor.moveToFirst()) {
                do {
                    lim++;
                    Kanji kanjiOfRow = grabKanji(cursor);
                    RecallQuestion questionOfRow = new RecallQuestion(kanjiOfRow, kanjiOfRow.getCharacter());
                    RecogQuestion recQuestionOfRow = new RecogQuestion(kanjiOfRow, kanjiOfRow.getName());
                    questions.add(questionOfRow);
                    questions.add(recQuestionOfRow);


                } while(cursor.moveToNext() & lim < 3);
            }


        }
        catch(Exception e) {

        }
        finally {
            Collections.shuffle(questions);

        }
        return questions;
    }

    public QuizSession grabQuestions() {

        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT * FROM ( SELECT * FROM Tiers ORDER BY next_due_date ASC) WHERE next_due_date IS NOT NULL AND next_due_date > 0;";
        Cursor cursor = db.rawQuery(query, null);
        ArrayList<String> chars = new ArrayList<String>();

        ArrayList<KanjiQuestion> questions = new ArrayList<KanjiQuestion>();
        Log.d("hi", Integer.toString(cursor.getCount()));
        int lim = 0;
        QuizSession session;
        try {
            if(cursor.moveToFirst()) {
                do {
                    lim++;
                    chars.add(cursor.getString(cursor.getColumnIndex(KANJI_CHARACTER)));
                    Kanji kanjiOfRow = grabKanji(cursor);
                    Answer rightAnswer = new Answer(kanjiOfRow, true);
                    ArrayList<Answer> wrongAnswers = grab3WrongAnswers(rightAnswer.getKanji().getID());
                    wrongAnswers.add(rightAnswer); // this is to shuffle it.
                    Collections.shuffle(wrongAnswers);

                    Question recallQuestionOfRow = new Question(true); // true = recall
                    recallQuestionOfRow.addAnswers(wrongAnswers);

                    String recallAnswer = rightAnswer.getKanji().getCharacter();
                    recallQuestionOfRow.setActualAnswer(recallAnswer);

                    Question recognitionQuestionOfRow = new Question(false); // false = recognition
                    recognitionQuestionOfRow.addAnswers(wrongAnswers);

                    String recogAnswer = rightAnswer.getKanji().getName(); // meaning
                    recognitionQuestionOfRow.setActualAnswer(recogAnswer);

                    KanjiQuestion questionOfRow = new KanjiQuestion(recallAnswer, recogAnswer);
                    questionOfRow.addQuestion(recallQuestionOfRow);
                    questionOfRow.addQuestion(recognitionQuestionOfRow);
                    questionOfRow.setAttachedKanji(kanjiOfRow);
                    questions.add(questionOfRow);


                } while(cursor.moveToNext() && lim<8);
            }
        }
        catch (Exception e) {
            Log.e("Quiz Session Exception", e.toString());
        }
        finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
                db.close();
            }
            Collections.shuffle(questions);
            session = new QuizSession(questions);
            session.setChars(chars);
        }
        Log.d("Checking contents of st", session.toString());
        return session;
    }



    public ArrayList<Answer> grab3WrongAnswers(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Random rand = new Random();
        int random = rand.nextInt(1300) + 5; // account for possibility of random actually getting right answer in range later.
        ArrayList<Answer> wrongAnswers3 = new ArrayList<Answer>();

        Cursor cursor = db.rawQuery("SELECT * FROM Tiers WHERE ID < " + random + " AND ID > " + (random-5) + " AND ID IS NOT " + id, null);

        try {
            if(cursor.moveToFirst()) {
                do {
                    Kanji kanjiOfRow = grabKanji(cursor);
                    Answer answerOfRow = new Answer(kanjiOfRow, false);

                    wrongAnswers3.add(answerOfRow);
                } while(cursor.moveToNext());
            }
        }
        catch (Exception e) {
            Log.e("d", "no");
        }
        finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
                db.close();
            }
        }
        return wrongAnswers3;
    }

    public Kanji grabIdKanjiChar(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Kanji currentKanji = new Kanji();

        String query = String.format("SELECT * FROM %s WHERE %s = %s",
                TABLE_TIERS, KEY_KANJI_ID, id // format it later to account for variable tier
        );

        Cursor cursor = db.rawQuery(query, null);

        try {
            if (cursor.moveToFirst()) {
                do {

                    String elements = cursor.getString(cursor.getColumnIndex("Radicals"));
                    String name = cursor.getString(cursor.getColumnIndex(("Name")));
                    String character = cursor.getString(cursor.getColumnIndex("Character"));
                    currentKanji.setID(cursor.getInt(cursor.getColumnIndex(KEY_KANJI_ID)));
                    currentKanji.setElements(elements);
                    currentKanji.setCharacter(character);
                    currentKanji.setName(name);

                } while(cursor.moveToNext());
            }
        }
        catch (Exception e) {
            // Log.d(TAG, "Error while trying to get posts");
        }
        finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
                db.close();

            }
        }
        return currentKanji;
    }
}
