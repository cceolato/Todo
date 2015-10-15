package br.com.ceolato.todo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import br.com.ceolato.todo.R;
import br.com.ceolato.todo.dao.TarefaDAO;
import br.com.ceolato.todo.entity.Tarefa;

/**
 * Created by 1541714 on 13/10/2015.
 */
public class TodoAdapter extends BaseAdapter {

    private LayoutInflater inflater;
    private SimpleDateFormat dateFormat;
    private SimpleDateFormat timeFormat;
    private List<Tarefa> listaTarefas;
    
    public TodoAdapter (Context context) throws SQLException {
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.dateFormat = new SimpleDateFormat("dd/MM");
        this.timeFormat = new SimpleDateFormat("HH:mm");
        TarefaDAO dao = new TarefaDAO(context);
        this.listaTarefas = dao.consultar();
    }

    @Override
    public int getCount() {
        return this.listaTarefas.size();
    }

    @Override
    public Object getItem(int position) {
        return this.listaTarefas.get(position);
    }

    @Override
    public long getItemId(int position) {
        Tarefa tarefa = this.listaTarefas.get(position);
        return tarefa.getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        if (convertView == null){
            view = this.inflater.inflate(R.layout.listview_todo, null);
        } else{
            view = convertView;
        }
        Tarefa tarefa = listaTarefas.get(position);
        TextView textViewTitle = (TextView) view.findViewById(R.id.textViewTitle);
        TextView textViewDescription = (TextView) view.findViewById(R.id.textViewDescription);
        TextView textViewDate = (TextView) view.findViewById(R.id.textViewDate);
        TextView textViewTime = (TextView) view.findViewById(R.id.textViewTime);

        textViewTitle.setText(tarefa.getTitle());
        textViewDescription.setText(tarefa.getDescription());
        textViewDate.setText(dateFormat.format(tarefa.getData()));
        textViewTime.setText(timeFormat.format(tarefa.getData()));

        pintaFundo(tarefa, view);

        return view;
    }

    private void pintaFundo(Tarefa tarefa, View view){
        if(tarefa.getDone()) {
            view.setBackgroundColor(view.getResources().getColor(R.color.green));
        }else if (tarefa.getData().before(Calendar.getInstance().getTime())){
            view.setBackgroundColor(view.getResources().getColor(R.color.red));
        }else{
            view.setBackgroundColor(view.getResources().getColor(R.color.white));
        }
    }
}
