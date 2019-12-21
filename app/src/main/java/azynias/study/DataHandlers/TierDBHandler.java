package azynias.study.DataHandlers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import azynias.study.Algorithms.spacedRepAlgo;
import azynias.study.ObjectModels.Answer;
import azynias.study.ObjectModels.Bracket;
import azynias.study.ObjectModels.Kanji;
import azynias.study.ObjectModels.KanjiQuestion;
import azynias.study.ObjectModels.Question;
import azynias.study.ObjectModels.QuizSession;
import azynias.study.ObjectModels.Tier;

/**
 * Created by Albedo on 6/24/2017.
 * NOTE - YOU MUST ALWAYS UPDATE USERPREFS WHEN UPDATING DATABASE.
 */

public class TierDBHandler extends SQLiteAssetHelper {
    private static final String DATABASE_NAME = "Tiers.db";
    private static final int DATABASE_VERSION = 1;

    private static final String KEY_KANJI_ID = "id";
    private static final String RADICALS = "Radicals";
    private static final String KANJI_CHARACTER = "Character";
    private static final String KANJI_NAME = "Name";

    private static final String KANJI_STORY = "Story";
    private static final String KANJI_BRACKET = "bracket";
    private static final String KANJI_DUEDATE = "next_due_date";
    private static final String KANJI_EASINESS = "easiness";
    private static final String KANJI_CORRECT_ANSWERS = "correct_answers";
    private static final String KANJI_PREV_DUEDATE = "prev_due_date";
    private static final String KANJI_CONSECUTIVE_ANSWERED = "consecutive_answered";
    private static final String KANJI_DATE_REVIEWED = "prev_days_interval";


    private static final String USER_SETTINGS = "User_Settings";
    private static final String USER_TIER = "Tier";
    private static final String USER_BRACKET = "Bracket";
    private static final String USER_DIFFICULTY = "daily_exercise";

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

    public void setKanjiStory(String story, int kanjiID) {
        SQLiteDatabase db = this.getWritableDatabase();
        Log.d("hello", story);
        ContentValues values = new ContentValues();
        values.put(KANJI_STORY, story); // to change story of Kanji
        db.update(UserPrefs.getTier(), values, "id=" + kanjiID, null);

        db.close();
    }

    public void incorrectDueDate(int kanjiID) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KANJI_DUEDATE, System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(1440)); // WILL SET DUE date 24 hours from time
        values.put(KANJI_CONSECUTIVE_ANSWERED, 0);
        values.put(KANJI_DATE_REVIEWED, 1);
        db.update(UserPrefs.getTier(), values, "id=" + kanjiID, null);

        db.close();
    }

    public void setInitialDueDate() {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(KANJI_DUEDATE, System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(1440));
        db.update(UserPrefs.getTier(), values, KANJI_BRACKET + " = " + UserPrefs.getBracket(), null);
        db.close();
    }

    public void correctKanjiDate(int quality, int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        String queryForInterval = "SELECT * FROM " + UserPrefs.getTier() + " WHERE id = " + id;
        Cursor cursor = db.rawQuery(queryForInterval, null);

        int correct = 0;
        float EF = 0f;
        float newEF = 0f;
        long curDate = 0;
        int dateReviewed = 0;
        long date = 0;

        try {
            if(cursor.moveToFirst()) {
                correct = cursor.getInt(cursor.getColumnIndex(KANJI_CONSECUTIVE_ANSWERED));

                EF = cursor.getFloat(cursor.getColumnIndex(KANJI_EASINESS));
                curDate = cursor.getLong(cursor.getColumnIndex(KANJI_DUEDATE));
                dateReviewed = cursor.getInt(cursor.getColumnIndex(KANJI_DATE_REVIEWED));

                if(quality>=3) newEF = smAlgo.calcEF(EF, quality);

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
        if(readyToIncrementDate(curDate)) {

            values.put(KANJI_DUEDATE, date);
            values.put(KANJI_CONSECUTIVE_ANSWERED, correct+1);
            values.put(KANJI_DATE_REVIEWED, dateReviewed);
            values.put(KANJI_EASINESS, newEF);
        }
        else {

            values.put(KANJI_DUEDATE, curDate+86400000); // this way, the algorithm for spacing wont get fucked up if user
            values.put(KANJI_PREV_DUEDATE, curDate); // decides to quiz himself early. Maybe do this earlier for better eff.
        }

        db.update(UserPrefs.getTier(), values, KEY_KANJI_ID + " = " + id, null);
        db.close();
    }

   // public int itemsStudied() {
     //   String query = "SELECT * FROM " + UserPrefs.getTier() + " WHERE next_"
    //}

    public boolean readyToIncrementDate(long date) {
        if(date>System.currentTimeMillis()) {
            return false;
        }
        return true;
    }

    public void arrangeDifficulty(int num) {
        int kanjiCount = getTierKanjiAmt();
        SQLiteDatabase db = this.getWritableDatabase();
        int modulus = kanjiCount % UserPrefs.getDifficulty();
        int howMany = kanjiCount / UserPrefs.getDifficulty();

        int end = UserPrefs.getDifficulty();
        int begin = 0;
        int fuck = 0;

        for(int i = 1; i<=howMany; i++) {
            db.execSQL("UPDATE " + UserPrefs.getTier() + " SET " + KANJI_BRACKET + " = " + i + " WHERE id >= " + begin + " AND " +
                    "id <=" + end);
            Log.d("Exec", Integer.toString(begin));
            end+= UserPrefs.getDifficulty();
            begin += UserPrefs.getDifficulty();
            fuck = i;
        }
        if(!(modulus==0)) {
            fuck++;
            db.execSQL("UPDATE " + UserPrefs.getTier() + " SET " + USER_BRACKET + " = 1 WHERE ID <= "
                    + UserPrefs.getDifficulty());

            db.execSQL("UPDATE " + UserPrefs.getTier() + " SET " + USER_BRACKET + " = " + fuck +
                    " WHERE id > " + --begin);
        }

        db.close();
    }

    public int getTierKanjiAmt() {
        String query = "SELECT Count(*) FROM " + UserPrefs.getTier();
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
      //  String grabInfoQuery = "SELECT Tier, Bracket FROM User_Settings";
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(grabInfoQuery, null);

        cursor.moveToFirst();
        try {
            UserPrefs.setTier(cursor.getString(cursor.getColumnIndex(USER_TIER)));
            UserPrefs.setBracket(cursor.getInt(cursor.getColumnIndex(USER_BRACKET)));
            UserPrefs.setDifficulty(cursor.getInt(cursor.getColumnIndex(USER_DIFFICULTY)));
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

        String query = "SELECT " + KANJI_DUEDATE + " FROM " + UserPrefs.getTier() + " WHERE " + KANJI_DUEDATE
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

    public List<Kanji> getTiersKanji(String tier) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + tier;

        Cursor cursor = db.rawQuery(query, null);
        List<Kanji> kanjiChars = new ArrayList<Kanji>();

        try {
            if(cursor.moveToFirst()) {
                do {
                    Kanji kanjiRow = grabKanji(cursor);
                    kanjiChars.add(kanjiRow);
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
        return kanjiChars;
    }

    public Tier arrangeBracketsForTier(String userSelectedTier) {
        SQLiteDatabase db = this.getReadableDatabase();
        Tier selectedTier = new Tier(userSelectedTier);

        int initialBracket = 1;

        Bracket bracket = new Bracket(initialBracket, userSelectedTier);
        String queryForTier = String.format("SELECT * FROM %s", userSelectedTier);

        Cursor cursor = db.rawQuery(queryForTier, null);


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

        Bracket userLevelBracket = new Bracket(UserPrefs.getBracket(), UserPrefs.getTier());
        SQLiteDatabase db = this.getReadableDatabase();

        String queryForBracketLevel = String.format("SELECT * FROM %s WHERE %s = %s",
                UserPrefs.getTier(), KANJI_BRACKET, userLevelBracket.getId());

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
            Log.d("UserPrefs bracket", Integer.toString(UserPrefs.getBracket()));
        }
        db.close();
    }

    public boolean incrementTier() {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        String maxQuery = String.format("SELECT MAX(%s) FROM %s", KANJI_BRACKET, UserPrefs.getTier());
        Cursor cursor = db.rawQuery(maxQuery, null);

        cursor.moveToFirst();
        int max = cursor.getInt(cursor.getColumnIndex("MAX(bracket)"));

        if(UserPrefs.getBracket() >= max) {
            String newTier = tierHierachy(UserPrefs.getTier());
            values.put(USER_TIER, newTier); // will return upper tier
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
            return "Platinum";
        if(preceedingTier.equals("Platinum"))
            return "Diamond";
        if(preceedingTier.equals("Diamond"))
            return "Master";
        return "";
    }

    public QuizSession grabQuestions() {

        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM ( SELECT * FROM Bronze ORDER BY next_due_date ASC) WHERE next_due_date IS NOT NULL AND next_due_date > 0;";
        Cursor cursor = db.rawQuery(query, null);


        ArrayList<KanjiQuestion> questions = new ArrayList<KanjiQuestion>();
        Log.d("hi", Integer.toString(cursor.getCount()));
        int lim = 0;
        QuizSession session;
        try {
            if(cursor.moveToFirst() && lim < 2) {
                do {
                    lim++;

                    Answer rightAnswer = new Answer(grabKanji(cursor), true);
                    ArrayList<Answer> wrongAnswers = grab3WrongAnswers();
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

                    KanjiQuestion questionOfRow = new KanjiQuestion(recogAnswer, recallAnswer);
                    questionOfRow.addQuestion(recallQuestionOfRow);
                    questionOfRow.addQuestion(recognitionQuestionOfRow);
                    questions.add(questionOfRow);

                } while(cursor.moveToNext() && lim<3);
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
        }
        Log.d("Checking contents of st", session.toString());
        return session;
    }



    public ArrayList<Answer> grab3WrongAnswers() {
        SQLiteDatabase db = this.getReadableDatabase();
        Random rand = new Random();
        int random = rand.nextInt(100) + 1; // account for possibility of random actually getting right answer in range later.
        ArrayList<Answer> wrongAnswers3 = new ArrayList<Answer>();

        Cursor cursor = db.rawQuery("SELECT * FROM Bronze WHERE ID < " + random + " AND ID > " + (random-4), null);

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
                UserPrefs.getTier(), KEY_KANJI_ID, id // format it later to account for variable tier
        );

        Cursor cursor = db.rawQuery(query, null);

        try {
            if (cursor.moveToFirst()) {
                do {

                    String elements = cursor.getString(cursor.getColumnIndex("Radicals"));
                    String name = cursor.getString(cursor.getColumnIndex(("Name")));
                    String character = cursor.getString(cursor.getColumnIndex("Character"));

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


/* possibly needed methods
public Kanji grabKanjiChar() {
        SQLiteDatabase db = this.getReadableDatabase();
        Kanji currentKanji = new Kanji();

        String query = String.format("SELECT * FROM %s WHERE %s = %s",
                UserPrefs.getTier(), KEY_KANJI_ID, UserPrefs.getPosition() // format it later to account for variable tier
                );

        String query2 = "SELECT * FROM Bronze";

        Cursor cursor = db.rawQuery(query2, null);
        Log.d("cursor count", Integer.toString(cursor.getCount()));
        try {
            if (cursor.moveToFirst()) {
                do {

                    String elements = cursor.getString(cursor.getColumnIndex("Radicals"));
                    String name = cursor.getString(cursor.getColumnIndex(("Name")));
                    String character = cursor.getString(cursor.getColumnIndex("Character"));
                    String story = cursor.getString(cursor.getColumnIndex("Story"));
                    currentKanji.setElements(elements);
                    currentKanji.setCharacter(character);
                    currentKanji.setName(name);
                    currentKanji.setExampleStory(story);
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

    public Kanji grabIdKanjiChar(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Kanji currentKanji = new Kanji();

        String query = String.format("SELECT * FROM %s WHERE %s = %s",
                UserPrefs.getTier(), KEY_KANJI_ID, id // format it later to account for variable tier
        );

        Cursor cursor = db.rawQuery(query, null);

        try {
            if (cursor.moveToFirst()) {
                do {

                    String elements = cursor.getString(cursor.getColumnIndex("Radicals"));
                    String name = cursor.getString(cursor.getColumnIndex(("Name")));
                    String character = cursor.getString(cursor.getColumnIndex("Character"));

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


 */