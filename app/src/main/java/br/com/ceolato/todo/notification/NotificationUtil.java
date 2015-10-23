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

import java.util.List;

import br.com.ceolato.todo.R;
import br.com.ceolato.todo.TodoListActivity;
import br.com.ceolato.todo.entity.Tarefa;

/**
 * Created by CarlosAlberto on 16/10/2015.
 */
public class NotificationUtil {

    private static PendingIntent getPendingIntent(Context context, Intent intent, int id){
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(intent.getComponent());
        stackBuilder.addNextIntent(intent);
        return stackBuilder.getPendingIntent(id, PendingIntent.FLAG_UPDATE_CURRENT);
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
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, id, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setDefaults(Notification.DEFAULT_ALL);
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setContentTitle(contentTitle);
        builder.setContentText(contentText);
        builder.setContentIntent(pendingIntent);
        builder.setVibrate(new long[]{200});
        builder.setAutoCancel(true);
        builder.setVisibility(NotificationCompat.VISIBILITY_PUBLIC);
        Notification n = builder.build();
        manager.notify(id, n);
    }

    public static void sendBroadcastHeadsUpNotification(Context context, Intent intent, String contentTitle, String contentText, int id){
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, id, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setDefaults(Notification.DEFAULT_ALL);
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setContentTitle(contentTitle);
        builder.setContentText(contentText);
        builder.setContentIntent(pendingIntent);
        builder.setVibrate(new long[]{200});
        builder.setAutoCancel(true);
        builder.setVisibility(NotificationCompat.VISIBILITY_PUBLIC);
        builder.setColor(Color.BLUE);
        builder.setFullScreenIntent(pendingIntent, false);
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

    public static void createBigNotification(Context context, Intent intent, String contentTitle,
                                             String contentText, List<String> lista, int id) {
//        PendingIntent pendingIntent = getPendingIntent(context, intent, id);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
                new Intent(context, TodoListActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);
        int size = lista.size();
        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
        inboxStyle.setBigContentTitle(contentTitle);
        for(String s : lista){
            inboxStyle.addLine(s);
        }
        inboxStyle.setSummaryText(contentText);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setDefaults(Notification.DEFAULT_ALL);
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setContentTitle(contentTitle);
        builder.setContentText(contentText);
        builder.setContentIntent(pendingIntent);
        builder.setAutoCancel(true);
        builder.setVisibility(NotificationCompat.VISIBILITY_PUBLIC);
        builder.setNumber(size);
        builder.setStyle(inboxStyle);
        NotificationManagerCompat nm = NotificationManagerCompat.from(context);
        nm.notify(id, builder.build());
    }
}
