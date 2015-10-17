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
        NotificationUtil.sendBroadcastNotification(context, intent, tarefa.getTitle(), tarefa.getDescription(), (int) tarefa.getId() );
    }
}
