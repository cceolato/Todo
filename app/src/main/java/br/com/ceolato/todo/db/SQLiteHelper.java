package br.com.ceolato.todo.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.Calendar;

/**
 * Created by 1541714 on 13/10/2015.
 */
public class SQLiteHelper extends SQLiteOpenHelper {

    public static final String TAG = "Tarefa";
    public static final String DB = "tarefa";
    public static final String TAREFA_ID = "_id";
    public static final String TAREFA_TITLE = "title";
    public static final String TAREFA_DESCRIPTION = "description";
    public static final String TAREFA_DATE = "date";
    public static final String TAREFA_DONE = "done";
    public static final int TRUE = 1;
    public static final int FALSE = 0;

    public static final long DIA = 86400000;
    public static final long MINUTO = 60000;

    private final String[] SCRIPT_DATABASE_CREATE = new String[] {
        "create table " + DB + "(" + TAREFA_ID + " integer primary key autoincrement," +
                TAREFA_TITLE + " text not null, " + TAREFA_DESCRIPTION + " text not null," +
                TAREFA_DATE + " long not null, " + TAREFA_DONE + " int not null);",
        "insert into " + DB + "(" + TAREFA_TITLE + ", " + TAREFA_DESCRIPTION + ", " + TAREFA_DATE +
                ", " + TAREFA_DONE + ") values ('Test','Test ToDo Description', " +
                (Calendar.getInstance().getTimeInMillis() + DIA) + ", " + FALSE + ")",
        "insert into " + DB + "(" + TAREFA_TITLE + ", " + TAREFA_DESCRIPTION + ", " + TAREFA_DATE +
                ", " + TAREFA_DONE + ") values ('Test2','Test ToDo Description 2', " +
                (Calendar.getInstance().getTimeInMillis() - DIA) + ", " + FALSE + ")",
        "insert into " + DB + "(" + TAREFA_TITLE + ", " + TAREFA_DESCRIPTION + ", " + TAREFA_DATE +
                ", " + TAREFA_DONE + ") values ('Test3','Test ToDo Description 3', " +
                (Calendar.getInstance().getTimeInMillis() + MINUTO) + ", " + FALSE + ")",
    };

    public SQLiteHelper(Context context, String nomeDoBanco, int version) {
        super(context, nomeDoBanco, null, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.i(TAG, "Criando banco de dados");
        for(String sql : SCRIPT_DATABASE_CREATE){
            Log.i(TAG, sql);
            db.execSQL(sql);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
