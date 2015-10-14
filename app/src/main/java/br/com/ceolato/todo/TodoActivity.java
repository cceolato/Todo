package br.com.ceolato.todo;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import br.com.ceolato.todo.dao.TarefaDAO;
import br.com.ceolato.todo.entity.Tarefa;
import br.com.ceolato.todo.fragment.DatePickerFragment;

public class TodoActivity extends AppCompatActivity {

    private EditText editTextTitle;
    private EditText editTextDescription;
    private EditText editTextDate;
    private EditText editTextTime;

    private Button buttonDate;
    private Button buttonTime;
    private Button buttonSave;

    private int year, month, day, hour, minute;
    private Long id;
    private Calendar cal;

    private static final int DATE_DIALOG_ID = 0;
    private static final int TIME_DIALOG_ID = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo);
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
        buttonDate = (Button) findViewById(R.id.buttonDate);
        buttonTime = (Button) findViewById(R.id.buttonTime);
        buttonSave = (Button) findViewById(R.id.buttonSave);
    }

    private void carregaTarefa(){
        long id = this.getIntent().getLongExtra("tarefa", -1);
        TarefaDAO dao = new TarefaDAO(this);
        if (id > -1) {
            Tarefa tarefa = dao.consultar(id);
            editTextTitle.setText(tarefa.getTitle());
            editTextDescription.setText(tarefa.getDescription());
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
                saveToDo(v);
                finish();
            }
        });
    }

    public void saveToDo(View view){
        if (validaCampos()) {
            cal.set(year, month, day, hour, minute);
            final Tarefa tarefa = new Tarefa();
            tarefa.setTitle(editTextTitle.getText().toString());
            tarefa.setDescription(editTextDescription.getText().toString());
            tarefa.setData(cal.getTime());

            new Thread(new Runnable() {
                @Override
                public void run() {
                    TarefaDAO dao = new TarefaDAO(TodoActivity.this);
                    tarefa.setId(dao.inserir(tarefa));
                    dao.close();
                    Snackbar.make(findViewById(R.id.snackbarPosition), getResources().getString(R.string.savedTodo), Snackbar.LENGTH_LONG);
                }
            }).start();
        }
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DATE_DIALOG_ID:
                return new DatePickerDialog(this, mDateSetListener, year, month, day);
            case TIME_DIALOG_ID:
                return new TimePickerDialog(this, mTimeSetListener, hour, minute, false);
        }

        return null;
    }

    private DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int yearSelected, int monthOfYear, int dayOfMonth) {
            year = yearSelected;
            month = monthOfYear;
            day = dayOfMonth;
            cal.set(year, month, minute);
            editTextDate.setText(new SimpleDateFormat("dd/MM/yyyy").format(cal.getTime()));
        }
    };

    private TimePickerDialog.OnTimeSetListener mTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
        public void onTimeSet(TimePicker view, int hourOfDay, int min) {
            hour = hourOfDay;
            minute = min;
            editTextTime.setText(new SimpleDateFormat("HH:mm").format(cal.getTime()));
        }
    };

    private boolean validaCampos(){
        if (editTextTitle.getText().toString().equals("")){
            Snackbar.make(findViewById(R.id.snackbarPosition), this.getResources().getText(R.string.errorTitle), Snackbar.LENGTH_LONG);
            return false;
        } else if (editTextDescription.getText().toString().equals("")){
            Snackbar.make(findViewById(R.id.snackbarPosition), this.getResources().getText(R.string.errorDescription), Snackbar.LENGTH_LONG);
            return false;
        } else if (editTextDate.getText().toString().equals("")) {
            Snackbar.make(findViewById(R.id.snackbarPosition), this.getResources().getText(R.string.errorDate), Snackbar.LENGTH_LONG);
            return false;
        } else if (editTextTime.getText().toString().equals("")) {
            Snackbar.make(findViewById(R.id.snackbarPosition), this.getResources().getText(R.string.errorTime), Snackbar.LENGTH_LONG);
            return false;
        }else {
            return true;
        }
    }
}
