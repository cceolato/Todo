package br.com.ceolato.todo.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import br.com.ceolato.todo.db.SQLiteHelper;
import br.com.ceolato.todo.entity.Tarefa;

/**
 * Created by 1541714 on 13/10/2015.
 */
public class TarefaDAO {

    private SQLiteDatabase db;
    private SQLiteHelper dbHelper;

    private final String[] colunas = {SQLiteHelper.TAREFA_ID, SQLiteHelper.TAREFA_TITLE,
            SQLiteHelper.TAREFA_DESCRIPTION, SQLiteHelper.TAREFA_DATE};

    public TarefaDAO(Context context){
        dbHelper = new SQLiteHelper(context, SQLiteHelper.DB, 1);
    }

    public void open() throws SQLException {
        db = dbHelper.getWritableDatabase();
    }

    public void close(){
        dbHelper.close();
    }

    public long inserir (Tarefa tarefa){
        Calendar cal = Calendar.getInstance();
        cal.setTime(tarefa.getData());
        ContentValues cv = new ContentValues();
        cv.put(SQLiteHelper.TAREFA_TITLE, tarefa.getTitle());
        cv.put(SQLiteHelper.TAREFA_DESCRIPTION, tarefa.getDescription());
        cv.put(SQLiteHelper.TAREFA_DATE, cal.getTimeInMillis());

        return db.insert(SQLiteHelper.DB, null, cv);
    }

    public int alterar(Tarefa tarefa){
        long id = tarefa.getId();
        Calendar cal = Calendar.getInstance();
        cal.setTime(tarefa.getData());
        ContentValues cv = new ContentValues();
        cv.put(SQLiteHelper.TAREFA_TITLE, tarefa.getTitle());
        cv.put(SQLiteHelper.TAREFA_DESCRIPTION, tarefa.getDescription());
        cv.put(SQLiteHelper.TAREFA_DATE, cal.getTimeInMillis());

        return db.update(SQLiteHelper.DB, cv, SQLiteHelper.TAREFA_ID + " = " + id, null);
    }

    public void excluir(Tarefa tarefa){
        long id = tarefa.getId();
        db.delete(SQLiteHelper.DB, SQLiteHelper.TAREFA_ID + " = " + id, null);
    }

    public List<Tarefa> consultar(){
        List<Tarefa> lista = new ArrayList<Tarefa>();

        Cursor cursor = db.query(SQLiteHelper.DB, colunas, null, null, null, null, null);
        cursor.moveToFirst();
        while(!cursor.isAfterLast()){
            Tarefa tarefa = criaContato(cursor);
            lista.add(tarefa);
            cursor.moveToNext();
        }
        cursor.close();
        return lista;
    }

    private Tarefa criaContato(Cursor c){
        Tarefa tarefa = new Tarefa();
        Calendar cal = Calendar.getInstance();
        tarefa.setId(c.getLong(0));
        tarefa.setTitle(c.getString(1));
        tarefa.setDescription(c.getString(2));
        cal.setTimeInMillis(c.getLong(3));
        Date d = cal.getTime();
        tarefa.setData(d);
        return tarefa;
    }
}
