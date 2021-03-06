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
            SQLiteHelper.TAREFA_DESCRIPTION, SQLiteHelper.TAREFA_DATE, SQLiteHelper.TAREFA_DONE,
            SQLiteHelper.TAREFA_IMPORTANT, SQLiteHelper.TAREFA_ARCHIVED};

    public TarefaDAO(Context context){
        dbHelper = new SQLiteHelper(context, SQLiteHelper.DB);
    }

    public void open() throws SQLException {
        db = dbHelper.getWritableDatabase();
    }

    public void close(){
        dbHelper.close();
    }

    public long inserir (Tarefa tarefa) {
        long inserted = 0;
        try {
            Calendar cal = Calendar.getInstance();
            cal.setTime(tarefa.getData());
            ContentValues cv = new ContentValues();
            cv.put(SQLiteHelper.TAREFA_TITLE, tarefa.getTitle());
            cv.put(SQLiteHelper.TAREFA_DESCRIPTION, tarefa.getDescription());
            cv.put(SQLiteHelper.TAREFA_DATE, cal.getTimeInMillis());
            cv.put(SQLiteHelper.TAREFA_DONE, this.booleanToInt(tarefa.isDone()));
            cv.put(SQLiteHelper.TAREFA_IMPORTANT, this.booleanToInt(tarefa.isImportant()));
            cv.put(SQLiteHelper.TAREFA_ARCHIVED, this.booleanToInt(tarefa.isArchived()));
            this.open();
            inserted = db.insert(SQLiteHelper.DB, null, cv);
            this.close();
        }catch (SQLException s){
        }
        return inserted;
    }

    public int alterar (Tarefa tarefa) {
        int updated = 0;
        try {
            long id = tarefa.getId();
            Calendar cal = Calendar.getInstance();
            cal.setTime(tarefa.getData());
            ContentValues cv = new ContentValues();
            cv.put(SQLiteHelper.TAREFA_TITLE, tarefa.getTitle());
            cv.put(SQLiteHelper.TAREFA_DESCRIPTION, tarefa.getDescription());
            cv.put(SQLiteHelper.TAREFA_DATE, cal.getTimeInMillis());
            cv.put(SQLiteHelper.TAREFA_DONE, this.booleanToInt(tarefa.isDone()));
            cv.put(SQLiteHelper.TAREFA_IMPORTANT, this.booleanToInt(tarefa.isImportant()));
            cv.put(SQLiteHelper.TAREFA_ARCHIVED, this.booleanToInt(tarefa.isArchived()));
            this.open();
            updated = db.update(SQLiteHelper.DB, cv, SQLiteHelper.TAREFA_ID + " = ?", new String[]{String.valueOf(id)});
            this.close();
        }catch (SQLException s){

        }
        return updated;
    }

    public void excluir(Tarefa tarefa) {
        try {
            long id = tarefa.getId();
            this.open();
            db.delete(SQLiteHelper.DB, SQLiteHelper.TAREFA_ID + " = " + id, null);
            this.close();
        }catch (SQLException s){

        }
    }

    public List<Tarefa> consultar() {
        List<Tarefa> lista = new ArrayList<>();
        try {
            this.open();
            Cursor cursor = db.query(SQLiteHelper.DB, colunas, null, null, null, null, null);
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                Tarefa tarefa = criaTarefa(cursor);
                lista.add(tarefa);
                cursor.moveToNext();
            }
            cursor.close();
            this.close();
        }catch (SQLException s){

        }
        return lista;
    }

    public List<Tarefa> consultar(String where, String[]whereArgs, String orderBy) {
        List<Tarefa> lista = new ArrayList<>();
        try {
            this.open();
            Cursor cursor = db.query(SQLiteHelper.DB, colunas, where, whereArgs, null, null, orderBy);
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                Tarefa tarefa = criaTarefa(cursor);
                lista.add(tarefa);
                cursor.moveToNext();
            }
            cursor.close();
            this.close();
        }catch (SQLException s){

        }
        return lista;
    }

    public Tarefa consultar(long id)  {
        Tarefa tarefa = new Tarefa();
        try {
            this.open();
            Cursor cursor = db.query(SQLiteHelper.DB, colunas, "_id = ?", new String[]{String.valueOf(id)}, null, null, null);
            cursor.moveToFirst();
            this.close();
            tarefa = criaTarefa(cursor);
        }catch (SQLException s){

        }
        return tarefa;
    }

    public List<String> consultarMensagensPerdidas() {
        List<String> lista = new ArrayList<>();
        try {
            this.open();
            String where = SQLiteHelper.TAREFA_DONE + " = ? AND " + SQLiteHelper.TAREFA_DATE + " <= ?";
            String[] wherArgs = new String[]{String.valueOf(SQLiteHelper.FALSE), String.valueOf(Calendar.getInstance().getTimeInMillis())};
            Cursor cursor = db.query(SQLiteHelper.DB, colunas, where, wherArgs, null, null, null);
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                Tarefa tarefa = criaTarefa(cursor);
                lista.add(tarefa.getTitle());
                cursor.moveToNext();
            }
            cursor.close();
            this.close();
        }catch (SQLException s){

        }
        return lista;
    }

    private Tarefa criaTarefa(Cursor c){
        Tarefa tarefa = new Tarefa();
        Calendar cal = Calendar.getInstance();
        tarefa.setId(c.getLong(0));
        tarefa.setTitle(c.getString(1));
        tarefa.setDescription(c.getString(2));
        cal.setTimeInMillis(c.getLong(3));
        Date d = cal.getTime();
        tarefa.setData(d);
        tarefa.setDone(this.intToBoolean(c.getInt(4)));
        tarefa.setImportant(this.intToBoolean(c.getInt(5)));
        tarefa.setArchived(this.intToBoolean(c.getInt(6)));
        return tarefa;
    }

    private int booleanToInt(boolean b){
        return b ? SQLiteHelper.TRUE : SQLiteHelper.FALSE;
    }

    private boolean intToBoolean (int i){
        return i == SQLiteHelper.TRUE;
    }
}
