package br.com.ceolato.todo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import java.sql.SQLException;

import br.com.ceolato.todo.adapter.TodoAdapter;
import br.com.ceolato.todo.db.SQLiteHelper;

public class TodoListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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
        try {
            ListView listaToDo = (ListView) findViewById(R.id.listViewToDo);
            listaToDo.setAdapter(new TodoAdapter(this));
            listaToDo.setOnItemClickListener(new ListaToDOItemClickListener());
        }catch (SQLException s){
            Log.v(SQLiteHelper.TAG, "Erro de SQL");
        }
    }

    private class ListaToDOItemClickListener implements OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Intent i = new Intent(getApplicationContext(), TodoActivity.class);
            i.putExtra("tarefa", id);
            startActivity(i);
        }
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
}
