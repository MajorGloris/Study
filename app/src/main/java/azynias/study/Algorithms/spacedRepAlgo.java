package azynias.study.Algorithms;

import android.util.Log;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Created by Albedo on 7/3/2017.
 * account for quality ,
 * if fail = > new item
 */

public class spacedRepAlgo {

    public float calcEF(float prevEF, int quality) {
        double EF = prevEF + (0.1-(5-quality)*0.08+(5-quality)*0.02);

        if(EF<=1.3)
            return (float) 1.3;

        return (float) EF;
    }

    public int calcNextDay(int reps, float ef, int interval) {
        if(reps==1)
            return 1;
        else if(reps<1) {
            return 0;
        }
        else if(reps==2)
            return 3;
        else {
            int days = Math.round(interval * ef);
            Log.d("check", ""+days);
            return days;
        }
    }

    public int convertToDays(long date) {
        return (int) TimeUnit.MILLISECONDS.toDays(date);
    }

    public long convertToMS(int days) {
        return (long) TimeUnit.DAYS.toMillis(days);
    }

    public static Date beginOfDay(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        return cal.getTime();
    }

    public static Date endOfDay(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 999);

        return cal.getTime();
    }
}
