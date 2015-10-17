package br.com.ceolato.todo.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import br.com.ceolato.todo.entity.Tarefa;
import br.com.ceolato.todo.notification.NotificationUtil;

public class TodoReceiver extends BroadcastReceiver {
    public TodoReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Tarefa tarefa = (Tarefa) intent.getExtras().get("tarefa");
        Intent it = new Intent(context, UserNotified.class);
        it.putExtra("tarefa", tarefa);
        NotificationUtil.sendBroadcastNotification(context, it, tarefa.getTitle(), tarefa.getDescription(), (int) tarefa.getId() );
    }
}
