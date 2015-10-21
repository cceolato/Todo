package br.com.ceolato.todo.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.sql.SQLException;

import br.com.ceolato.todo.dao.TarefaDAO;
import br.com.ceolato.todo.db.SQLiteHelper;
import br.com.ceolato.todo.entity.Tarefa;

public class UserNotified extends BroadcastReceiver {

    Tarefa tarefa;

    public UserNotified() {
    }

    @Override
    public void onReceive(final Context context, Intent intent) {
        tarefa = (Tarefa) intent.getExtras().get("tarefa");

        new Thread(new Runnable() {
            @Override
            public void run() {
                    TarefaDAO dao = new TarefaDAO(context);
                    dao.excluir(tarefa);
                    context.sendBroadcast(new Intent("UPDATE_LIST"));
            }
        }).start();
    }
}
