package br.com.ceolato.todo;

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
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import java.util.List;

import br.com.ceolato.todo.adapter.TodoRecyclerViewAdapter;
import br.com.ceolato.todo.alarm.AlarmUtil;
import br.com.ceolato.todo.broadcast.TodoReceiver;
import br.com.ceolato.todo.dao.TarefaDAO;
import br.com.ceolato.todo.entity.Tarefa;
import br.com.ceolato.todo.listeners.RecyclerViewOnClickListener;

public class TodoListActivity extends AppCompatActivity implements RecyclerViewOnClickListener {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private List<Tarefa> listaTarefas;
    private TarefaDAO dao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerViewToDo);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.addOnItemTouchListener(new RecyclerViewOnTouchListener(this, mRecyclerView, this));

        final ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                final TarefaDAO dao = new TarefaDAO(TodoListActivity.this);
                final Tarefa tarefa = listaTarefas.get(viewHolder.getAdapterPosition());
                //Remove swiped item from list and notify the RecyclerView
                if (swipeDir == ItemTouchHelper.RIGHT){
                    tarefa.setDone(true);
                    dao.alterar(tarefa);
                    TodoListActivity.this.sendBroadcast(new Intent("UPDATE_LIST"));
                }else{
                    dao.excluir(tarefa);
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

        mostrarTarefas();

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
        listaTarefas = dao.consultar();
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
        getMenuInflater().inflate(R.menu.menu_todo, menu);
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
