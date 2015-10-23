package br.com.ceolato.todo.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import java.sql.SQLException;
import java.util.Calendar;
import java.util.List;

import br.com.ceolato.todo.R;
import br.com.ceolato.todo.alarm.AlarmUtil;
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
                TarefaDAO dao = new TarefaDAO(context);
                List<String> lista = dao.consultarMensagensPerdidas();
                NotificationUtil.createBigNotification(context, intent, context.getResources()
                        .getString(R.string.tarefas_perdidas), context.getResources()
                        .getString(R.string.mensagem_tarefas_perdidas), lista, 0);

                String where = SQLiteHelper.TAREFA_DONE + " = ? AND " + SQLiteHelper.TAREFA_DATE + " >= ?";
                String[] whereArgs = new String[]{String.valueOf(SQLiteHelper.FALSE),String.valueOf(Calendar.getInstance().getTimeInMillis())};
                List<Tarefa> listaTarefas = dao.consultar(where, whereArgs, null);
                for(Tarefa tarefa : listaTarefas){
                    Intent intent = new Intent(context, TodoReceiver.class);
                    intent.putExtra("tarefa", tarefa);
                    AlarmUtil.schedule(context, intent, tarefa.getData(), (int) tarefa.getId());
                }
            }
        }).start();
    }
}
