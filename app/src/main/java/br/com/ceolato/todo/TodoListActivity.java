package br.com.ceolato.todo;

import android.app.ActionBar;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.util.List;

import br.com.ceolato.todo.adapter.TodoRecyclerViewAdapter;
import br.com.ceolato.todo.adapter.TodoSpinnerAdapter;
import br.com.ceolato.todo.alarm.AlarmUtil;
import br.com.ceolato.todo.broadcast.TodoReceiver;
import br.com.ceolato.todo.dao.TarefaDAO;
import br.com.ceolato.todo.db.SQLiteHelper;
import br.com.ceolato.todo.entity.Tarefa;
import br.com.ceolato.todo.listeners.RecyclerViewOnClickListener;

public class TodoListActivity extends AppCompatActivity implements RecyclerViewOnClickListener {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private List<Tarefa> listaTarefas;
    private TarefaDAO dao;
    private Spinner spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setIcon(R.mipmap.ic_launcher);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerViewToDo);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.addOnItemTouchListener(new RecyclerViewOnTouchListener(this, mRecyclerView, this));

        spinner = new Spinner(getSupportActionBar().getThemedContext());
        TodoSpinnerAdapter adapter = new TodoSpinnerAdapter(this);
        ActionBar.LayoutParams lp = new ActionBar.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mostrarTarefas();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        toolbar.addView(spinner, 1);

        final ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                final TarefaDAO dao = new TarefaDAO(TodoListActivity.this);
                final Tarefa tarefa = listaTarefas.get(viewHolder.getAdapterPosition());
                String mensagem = "";
                if (swipeDir == ItemTouchHelper.RIGHT){
                    if (tarefa.isDone() && tarefa.isArchived()){
                        mensagem = "Nenhuma ação tomada!";
                    }else if(tarefa.isDone()){
                        tarefa.setArchived(true);
                        dao.alterar(tarefa);
                        mensagem = getResources().getString(R.string.tarefa_archived);
                    }else {
                        tarefa.setDone(true);
                        dao.alterar(tarefa);
                        mensagem = getResources().getString(R.string.tarefa_done);
                    }
                    Snackbar.make(mRecyclerView, mensagem, Snackbar.LENGTH_LONG).show();
                    TodoListActivity.this.sendBroadcast(new Intent("UPDATE_LIST"));
                }else{
                    dao.excluir(tarefa);
                    AlarmUtil.cancel(TodoListActivity.this, new Intent(TodoListActivity.this, TodoReceiver.class), (int) tarefa.getId());
                    Snackbar.make(mRecyclerView, getResources().getString(R.string.deletedTodo), Snackbar.LENGTH_LONG)
                            .setAction(getResources().getString(R.string.undo), new View.OnClickListener(){
                                @Override
                                public void onClick(View v){
                                    dao.inserir(tarefa);
                                    mostrarTarefas();
                                }
                            }).setActionTextColor(Color.YELLOW).show();
                    TodoListActivity.this.sendBroadcast(new Intent("UPDATE_LIST"));
                }
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(mRecyclerView);

        dao = new TarefaDAO(this);

        registerReceiver(updateListBroadcastReceiver, new IntentFilter("UPDATE_LIST"));

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), TodoActivity.class);
                startActivity(i);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mostrarTarefas();
        registerReceiver(updateListBroadcastReceiver, new IntentFilter("UPDATE_LIST"));
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(updateListBroadcastReceiver);
    }

    private void mostrarTarefas() {
        final int ALL = 0;
        final int ARCHIVED = 1;
        final int DONE = 2;
        final int IMOPORTANT = 3;
        final int UNDONE = 4;
        int position = spinner.getSelectedItemPosition();

        String where = null;
        String[] whereArgs = null;

        switch (position) {
            case ALL:
                Log.v(SQLiteHelper.TAG, "ALL: " + position);
                where = null;
                whereArgs = null;
                break;
            case UNDONE:
                Log.v(SQLiteHelper.TAG, "UNDONE : " + position);
                where = SQLiteHelper.TAREFA_DONE + " = ?";
                whereArgs = new String[]{String.valueOf(SQLiteHelper.FALSE)};
                break;
            case DONE:
                Log.v(SQLiteHelper.TAG, "DONE: " + position);
                where = SQLiteHelper.TAREFA_DONE + " = ?";
                whereArgs = new String[]{String.valueOf(SQLiteHelper.TRUE)};
                break;
            case ARCHIVED:
                Log.v(SQLiteHelper.TAG, "ARCHIVED: " + position);
                where = SQLiteHelper.TAREFA_ARCHIVED + " = ?";
                whereArgs = new String[]{String.valueOf(SQLiteHelper.TRUE)};
                break;
            case IMOPORTANT:
                Log.v(SQLiteHelper.TAG, "IMPORTANT: " + position);
                where = SQLiteHelper.TAREFA_IMPORTANT + " = ?";
                whereArgs = new String[]{String.valueOf(SQLiteHelper.TRUE)};
            default:
                break;
        }

        String orderBy = SQLiteHelper.TAREFA_DATE + " ASC";
        listaTarefas = dao.consultar(where, whereArgs, orderBy);
        TodoRecyclerViewAdapter todoRecyclerViewAdapter = new TodoRecyclerViewAdapter(this, listaTarefas);
        todoRecyclerViewAdapter.setRecyclerViewOnClickListener(this);
        mRecyclerView.setAdapter(todoRecyclerViewAdapter);
    }

    @Override
    public void onClickListener(View view, int position) {
        Intent i = new Intent(getApplicationContext(), TodoActivity.class);
        i.putExtra("tarefa", listaTarefas.get(position).getId());
        startActivity(i);
    }

    @Override
    public void onLongPressClickListener(View view, int position) {
        final Tarefa tarefa = listaTarefas.get(position);
        dao.excluir(tarefa);
        AlarmUtil.cancel(this, new Intent(this, TodoReceiver.class), (int) tarefa.getId());
        Snackbar.make(view, view.getResources().getString(R.string.deletedTodo), Snackbar.LENGTH_LONG)
                .setAction(view.getResources().getString(R.string.undo), new View.OnClickListener(){
                    @Override
                    public void onClick(View v){
                        dao.inserir(tarefa);
                        mostrarTarefas();
                    }
                }).setActionTextColor(Color.YELLOW).show();
        mostrarTarefas();
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_todo_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private BroadcastReceiver updateListBroadcastReceiver = new BroadcastReceiver()  {
        @Override
        public void onReceive(Context context, Intent intent) {
            mostrarTarefas();
        }
    };

    private static class RecyclerViewOnTouchListener implements RecyclerView.OnItemTouchListener{
        private Context context;
        private RecyclerViewOnClickListener rc;
        private GestureDetector gestureDetector;

        public RecyclerViewOnTouchListener(Context c, final RecyclerView rv, RecyclerViewOnClickListener rco){
            context = c;
            rc = rco;
            gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener(){
                @Override
                public void onLongPress(MotionEvent e){
                    super.onLongPress(e);
                    View cv = rv.findChildViewUnder(e.getX(), e.getY());
                    if (cv != null && rc != null){
                        rc.onLongPressClickListener(cv, rv.getChildAdapterPosition(cv));
                    }
                }

                @Override
                public boolean onSingleTapUp(MotionEvent e){
                    return false;
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
            gestureDetector.onTouchEvent(e);
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {
        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
        }

    }

}
