package br.com.ceolato.todo;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import br.com.ceolato.todo.alarm.AlarmUtil;
import br.com.ceolato.todo.dao.TarefaDAO;
import br.com.ceolato.todo.entity.Tarefa;
import br.com.ceolato.todo.fragment.DatePickerFragment;

public class TodoActivity extends AppCompatActivity {

    private EditText editTextTitle;
    private EditText editTextDescription;
    private EditText editTextDate;
    private EditText editTextTime;

    private CheckBox checkBoxDone;

    private Button buttonDate;
    private Button buttonTime;
    private Button buttonSave;

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
        cal = Calendar.getInstance();
        try{
            this.inicializa();
            this.carregaTarefa();
            this.setListeners();
        }catch (SQLException s){

        }
    }

    private void inicializa(){
        editTextTitle = (EditText) findViewById(R.id.editTextTitle);
        editTextDescription =  (EditText) findViewById(R.id.editTextDescription);
        editTextDate = (EditText) findViewById(R.id.editTextDate);
        editTextTime = (EditText) findViewById(R.id.editTextTime);
        checkBoxDone = (CheckBox) findViewById(R.id.checkBoxDone);
        buttonDate = (Button) findViewById(R.id.buttonDate);
        buttonTime = (Button) findViewById(R.id.buttonTime);
        buttonSave = (Button) findViewById(R.id.buttonSave);

        year = cal.get(Calendar.YEAR);
        month = cal.get(Calendar.MONTH);
        day = cal.get(Calendar.DAY_OF_MONTH);
        hour = cal.get(Calendar.HOUR_OF_DAY);
        minute = cal.get(Calendar.MINUTE);
    }

    private void carregaTarefa() throws SQLException {
        long id = this.getIntent().getLongExtra("tarefa", -1);
        TarefaDAO dao = new TarefaDAO(this);
        if (id > -1) {
            tarefa = dao.consultar(id);
            editTextTitle.setText(tarefa.getTitle());
            editTextDescription.setText(tarefa.getDescription());
            editTextDate.setText(new SimpleDateFormat("dd/MM/yyyy").format(tarefa.getData()));
            editTextTime.setText(new SimpleDateFormat("HH:mm").format(tarefa.getData()));
            checkBoxDone.setChecked(tarefa.getDone());
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

        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validaCampos()){
                    saveToDo(v);
                    finish();
                }
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
            cal.setTime(tarefa.getData());
        }
        tarefa.setTitle(editTextTitle.getText().toString());
        tarefa.setDescription(editTextDescription.getText().toString());
        /* TODO: tem que pegar a data do editText e criar*/
        tarefa.setData(cal.getTime());
        tarefa.setDone(checkBoxDone.isChecked());

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    TarefaDAO dao = new TarefaDAO(TodoActivity.this);
                    Intent intent = new Intent("TAREFA");
                    intent.putExtra("tarefa", tarefa);
                    if (novo) {
                        tarefa.setId(dao.inserir(tarefa));
                    }else{
                        dao.alterar(tarefa);
                    }
                    AlarmUtil.schedule(TodoActivity.this, intent, tarefa.getData());
                    Snackbar.make(findViewById(R.id.snackbarPosition), getResources().getString(R.string.savedTodo),
                            Snackbar.LENGTH_LONG).show();
                } catch (SQLException s){
                }
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
            Snackbar.make(findViewById(R.id.snackbarPosition), this.getResources().getText(R.string.errorTitle),
                    Snackbar.LENGTH_LONG).show();
            return false;
        } else if (editTextDescription.getText().toString().equals("")){
            Snackbar.make(findViewById(R.id.snackbarPosition), this.getResources().getText(R.string.errorDescription),
                    Snackbar.LENGTH_LONG).show();
            return false;
        } else if (editTextDate.getText().toString().equals("")) {
            Snackbar.make(findViewById(R.id.snackbarPosition), this.getResources().getText(R.string.errorDate),
                    Snackbar.LENGTH_LONG).show();
            return false;
        } else if (editTextTime.getText().toString().equals("")) {
            Snackbar.make(findViewById(R.id.snackbarPosition), this.getResources().getText(R.string.errorTime),
                    Snackbar.LENGTH_LONG).show();
            return false;
        }else {
            return true;
        }
    }

}
