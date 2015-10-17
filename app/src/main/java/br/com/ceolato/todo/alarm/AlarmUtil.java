package br.com.ceolato.todo.alarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by CarlosAlberto on 16/10/2015.
 */
public class AlarmUtil {

    public static void schedule(Context context, Intent intent, Date triggerDate){
        Calendar cal = Calendar.getInstance();
        PendingIntent pending = PendingIntent.getBroadcast(context, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        cal.setTime(triggerDate);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pending);
    }

    public static void scheduleRepeat(Context context, Intent intent, Date triggerDate, long intervalMillis) {
        Calendar cal = Calendar.getInstance();
        PendingIntent pending = PendingIntent.getBroadcast(context, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        cal.setTime(triggerDate);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), intervalMillis, pending);
    }

    public static void cancel(Context context, Intent intent){
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        PendingIntent pending = PendingIntent.getBroadcast(context, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.cancel(pending);
    }

}
