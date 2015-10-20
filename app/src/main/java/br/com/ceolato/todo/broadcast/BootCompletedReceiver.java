package br.com.ceolato.todo.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.sql.SQLException;
import java.util.List;

import br.com.ceolato.todo.R;
import br.com.ceolato.todo.dao.TarefaDAO;
import br.com.ceolato.todo.db.SQLiteHelper;
import br.com.ceolato.todo.entity.Tarefa;
import br.com.ceolato.todo.notification.NotificationUtil;

public class BootCompletedReceiver extends BroadcastReceiver {

    public BootCompletedReceiver() {
    }

    @Override
    public void onReceive(final Context context, final Intent intent) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    TarefaDAO dao = new TarefaDAO(context);
                    List<String> lista = dao.consultarMensagensPerdidas();
                    NotificationUtil.createBigNotification(context, intent, context.getResources()
                            .getString(R.string.tarefas_perdidas), context.getResources()
                            .getString(R.string.mensagem_tarefas_perdidas), lista, 0);
                }catch (SQLException s){
                    Log.e(SQLiteHelper.TAG, "Erro de SQL");
                }
            }
        }).start();
    }
}
