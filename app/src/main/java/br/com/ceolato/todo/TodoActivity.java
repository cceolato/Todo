package br.com.ceolato.todo;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import br.com.ceolato.todo.alarm.AlarmUtil;
import br.com.ceolato.todo.broadcast.TodoReceiver;
import br.com.ceolato.todo.dao.TarefaDAO;
import br.com.ceolato.todo.db.SQLiteHelper;
import br.com.ceolato.todo.entity.Tarefa;

public class TodoActivity extends AppCompatActivity {

    private EditText editTextTitle;
    private EditText editTextDescription;
    private EditText editTextDate;
    private EditText editTextTime;

    private CheckBox checkBoxDone;
    private CheckBox checkBoxImportant;

    private Button buttonDate;
    private Button buttonTime;

    private int year, month, day, hour, minute;
    private Long id;
    private Calendar cal;
    private boolean novo;

    private Tarefa tarefa;

    private static final int DATE_DIALOG_ID = 0;
    private static final int TIME_DIALOG_ID = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo);
        getSupportActionBar().setIcon(R.mipmap.ic_launcher);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        cal = Calendar.getInstance();
        this.inicializa();
        this.carregaTarefa();
        this.setListeners();
    }

    private void inicializa(){
        editTextTitle = (EditText) findViewById(R.id.editTextTitle);
        editTextDescription =  (EditText) findViewById(R.id.editTextDescription);
        editTextDate = (EditText) findViewById(R.id.editTextDate);
        editTextTime = (EditText) findViewById(R.id.editTextTime);
        checkBoxDone = (CheckBox) findViewById(R.id.checkBoxDone);
        checkBoxImportant = (CheckBox) findViewById(R.id.checkBoxImportant);
        buttonDate = (Button) findViewById(R.id.buttonDate);
        buttonTime = (Button) findViewById(R.id.buttonTime);

        year = cal.get(Calendar.YEAR);
        month = cal.get(Calendar.MONTH);
        day = cal.get(Calendar.DAY_OF_MONTH);
        hour = cal.get(Calendar.HOUR_OF_DAY);
        minute = cal.get(Calendar.MINUTE);
    }

    private void carregaTarefa() {
        long id = this.getIntent().getLongExtra("tarefa", -1);
        TarefaDAO dao = new TarefaDAO(this);
        if (id > -1) {
            tarefa = dao.consultar(id);
            editTextTitle.setText(tarefa.getTitle());
            editTextDescription.setText(tarefa.getDescription());
            editTextDate.setText(new SimpleDateFormat("dd/MM/yyyy").format(tarefa.getData()));
            editTextTime.setText(new SimpleDateFormat("HH:mm").format(tarefa.getData()));
            checkBoxDone.setChecked(tarefa.isDone());
            checkBoxImportant.setChecked(tarefa.isImportant());
        } else{
            editTextDate.setText(new SimpleDateFormat("dd/MM/yyyy").format(cal.getTime()));
            editTextTime.setText(new SimpleDateFormat("HH:mm").format(cal.getTime()));
        }
    }
    
    private void setListeners(){
        buttonDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(DATE_DIALOG_ID);
            }
        });

        buttonTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(TIME_DIALOG_ID);
            }
        });

    }

    public void saveToDo(View view){
        novo = false;
        if (tarefa == null){
            cal.set(year, month, day, hour, minute);
            tarefa = new Tarefa();
            novo = true;
        } else {
            try{
                String data = editTextDate.getText().toString();
                String time = editTextTime.getText().toString();
                cal.setTime(new SimpleDateFormat("dd/MM/yyyy HH:mm").parse(data + " " + time));
            } catch (ParseException p){
                Log.e(SQLiteHelper.TAG, "Erro no parser de data");
            }
        }
        tarefa.setTitle(editTextTitle.getText().toString());
        tarefa.setDescription(editTextDescription.getText().toString());
        tarefa.setData(cal.getTime());
        tarefa.setDone(checkBoxDone.isChecked());
        tarefa.setImportant(checkBoxImportant.isChecked());

        new Thread(new Runnable() {
            @Override
            public void run() {
                TarefaDAO dao = new TarefaDAO(TodoActivity.this);
                Intent intent = new Intent(TodoActivity.this, TodoReceiver.class);
                intent.putExtra("tarefa", tarefa);
                if (novo) {
                    tarefa.setId(dao.inserir(tarefa));
                }else{
                    dao.alterar(tarefa);
                }
                if ( !tarefa.isDone() && tarefa.getData().after(Calendar.getInstance().getTime()) ) {
                    AlarmUtil.schedule(TodoActivity.this, intent, tarefa.getData(), (int) tarefa.getId());
                }
                if(tarefa.isDone()){
                    AlarmUtil.cancel(TodoActivity.this, new Intent(TodoActivity.this, TodoReceiver.class), (int) tarefa.getId());
                }
                TodoActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(TodoActivity.this, getResources().getString(R.string.savedTodo), Toast.LENGTH_LONG).show();
                    }
                });
            }
        }).start();

    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DATE_DIALOG_ID:
                return new DatePickerDialog(this, mDateSetListener, year, month, day);
            case TIME_DIALOG_ID:
                return new TimePickerDialog(this, mTimeSetListener, hour, minute, true);
        }

        return null;
    }

    private DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int yearSelected, int monthOfYear, int dayOfMonth) {
            year = yearSelected;
            month = monthOfYear;
            day = dayOfMonth;
            cal.set(year, month, day);
            editTextDate.setText(new SimpleDateFormat("dd/MM/yyyy").format(cal.getTime()));
        }
    };

    private TimePickerDialog.OnTimeSetListener mTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
        public void onTimeSet(TimePicker view, int hourOfDay, int min) {
            hour = hourOfDay;
            minute = min;
            cal.set(Calendar.HOUR_OF_DAY, hour);
            cal.set(Calendar.MINUTE, minute);
            editTextTime.setText(new SimpleDateFormat("HH:mm").format(cal.getTime()));
        }
    };

    private boolean validaCampos(){
        if (editTextTitle.getText().toString().equals("")){
            Snackbar.make(findViewById(R.id.contentTodo), this.getResources().getText(R.string.errorTitle),
                    Snackbar.LENGTH_LONG).show();
            return false;
        } else if (editTextDescription.getText().toString().equals("")){
            Snackbar.make(findViewById(R.id.contentTodo), this.getResources().getText(R.string.errorDescription),
                    Snackbar.LENGTH_LONG).show();
            return false;
        } else if (editTextDate.getText().toString().equals("")) {
            Snackbar.make(findViewById(R.id.contentTodo), this.getResources().getText(R.string.errorDate),
                    Snackbar.LENGTH_LONG).show();
            return false;
        } else if (editTextTime.getText().toString().equals("")) {
            Snackbar.make(findViewById(R.id.contentTodo), this.getResources().getText(R.string.errorTime),
                    Snackbar.LENGTH_LONG).show();
            return false;
        }else {
            return true;
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_todo, menu);
        if (tarefa == null){
            menu.removeItem(R.id.action_delete);
            menu.removeItem(R.id.action_archive);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()){
            case R.id.action_settings:
                return true;
            case R.id.action_about:
                return true;
            case R.id.action_archive:
                arquivarTarefa();
                return true;
            case R.id.action_delete:
                excluiTarefa();
                return true;
            case R.id.action_save:
                if (validaCampos()){
                    saveToDo(item.getActionView());
                    finish();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void excluiTarefa(){
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle(getResources().getString(R.string.confirm_delete));
        dialog.setMessage(getResources().getString(R.string.confirm_delete_message, tarefa.getTitle(), tarefa.getDescription()));
        dialog.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                final TarefaDAO dao = new TarefaDAO(TodoActivity.this);
                dao.excluir(tarefa);
                AlarmUtil.cancel(TodoActivity.this, new Intent(TodoActivity.this, TodoReceiver.class), (int) tarefa.getId());
                Toast.makeText(TodoActivity.this, getResources().getString(R.string.deletedTodo), Toast.LENGTH_LONG).show();
                TodoActivity.this.sendBroadcast(new Intent("UPDATE_LIST"));
                finish();
            }
        });

        dialog.setNegativeButton("Não", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        AlertDialog confirma = dialog.create();
        confirma.show();
    }

    private void arquivarTarefa(){
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle(getResources().getString(R.string.confirm_archive));
        dialog.setMessage(getResources().getString(R.string.confirm_archive_message, tarefa.getTitle(), tarefa.getDescription()));
        dialog.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                final TarefaDAO dao = new TarefaDAO(TodoActivity.this);
                tarefa.setArchived(true);
                dao.alterar(tarefa);
                TodoActivity.this.sendBroadcast(new Intent("UPDATE_LIST"));
                Toast.makeText(TodoActivity.this, getResources().getString(R.string.archived), Toast.LENGTH_LONG).show();
                finish();
            }
        });

        dialog.setNegativeButton("Não", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        AlertDialog confirma = dialog.create();
        confirma.show();
    }
}
