package br.com.ceolato.todo.notification;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.NotificationCompat;

import br.com.ceolato.todo.R;

/**
 * Created by CarlosAlberto on 16/10/2015.
 */
public class NotificationUtil {

    private static PendingIntent getPendingIntent(Context context, Intent intent, int id){
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(intent.getComponent());
        stackBuilder.addNextIntent(intent);
        PendingIntent p = stackBuilder.getPendingIntent(id, PendingIntent.FLAG_UPDATE_CURRENT);
        return  p;
    }

    public static void create (Context context, Intent intent, String contentTitle, String contentText, int id){
        PendingIntent pendingIntent = getPendingIntent(context, intent, id);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setDefaults(Notification.DEFAULT_ALL);
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setContentTitle(contentTitle);
        builder.setContentText(contentText);
        builder.setContentIntent(pendingIntent);
        builder.setAutoCancel(true);
        builder.setVisibility(NotificationCompat.VISIBILITY_PUBLIC);
        NotificationManagerCompat nm = NotificationManagerCompat.from(context);
        nm.notify(id, builder.build());
    }

    public static void creatHeadsUpNotification (Context context, Intent intent, String contentTitle, String contentText, int id){
        PendingIntent pendingIntent = getPendingIntent(context, intent, id);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setDefaults(Notification.DEFAULT_ALL);
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setContentTitle(contentTitle);
        builder.setContentText(contentText);
        builder.setContentIntent(pendingIntent);
        builder.setAutoCancel(true);
        builder.setColor(Color.BLUE);
        builder.setFullScreenIntent(pendingIntent, false);
        NotificationManagerCompat nm = NotificationManagerCompat.from(context);
        nm.notify(id, builder.build());
    }

    public static void sendBroadcastNotification(Context context, Intent intent, String contentTitle, String contentText, int id){
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setDefaults(Notification.DEFAULT_ALL);
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setContentTitle(contentTitle);
        builder.setContentText(contentText);
        builder.setContentIntent(pendingIntent);
        builder.setAutoCancel(true);
        builder.setVisibility(NotificationCompat.VISIBILITY_PUBLIC);
        Notification n = builder.build();
        manager.notify(id, n);
    }

    public static void cancel (Context context, int id){
        NotificationManagerCompat nm = NotificationManagerCompat.from(context);
        nm.cancel(id);
    }

    public static void cancelAll (Context context){
        NotificationManagerCompat nm = NotificationManagerCompat.from(context);
        nm.cancelAll();
    }

}
