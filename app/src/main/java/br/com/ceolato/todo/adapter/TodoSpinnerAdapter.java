package br.com.ceolato.todo.adapter;

import android.content.Context;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.zip.Inflater;

import br.com.ceolato.todo.R;
import br.com.ceolato.todo.dao.TarefaDAO;
import br.com.ceolato.todo.entity.Tarefa;

/**
 * Created by 1541714 on 22/10/2015.
 */
public class TodoSpinnerAdapter extends BaseAdapter {
    private String[] mItems;
    private String[][] spinnerItens;
    Context contecxt;
    LayoutInflater inflater;

    public TodoSpinnerAdapter(Context context){
        this.contecxt = context;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mItems = context.getResources().getStringArray(R.array.filtro_tarefas);
    }

    @Override
    public int getCount() {
        return mItems.length;
    }

    @Override
    public String getItem(int position) {
        return mItems[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getDropDownView(int position, View view, ViewGroup parent) {
        if (view == null || !view.getTag().toString().equals("DROPDOWN")) {
            view = inflater.inflate(R.layout.toolbar_spinner_item_dropdown, parent, false);
            view.setTag("DROPDOWN");
        }
        contaQuantidades();
        TextView textView = (TextView) view.findViewById(R.id.textSpinner);
        textView.setText(spinnerItens[0][position] + " (" + spinnerItens[1][position] + ")");

        return view;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        if (view == null || !view.getTag().toString().equals("NON_DROPDOWN")) {
            view = inflater.inflate(R.layout.toolbar_spinner_item_actionbar, parent, false);
            view.setTag("NON_DROPDOWN");
        }
        TextView textView = (TextView) view.findViewById(R.id.textSpinner);
        textView.setText(getItem(position));
        return view;
    }

    private void contaQuantidades(){
        TarefaDAO dao = new TarefaDAO(contecxt);
        List<Tarefa> lista = dao.consultar();
        int done = 0, undone = 0, important = 0, archived = 0;
        int all = lista.size();
        for(Tarefa tarefa : lista){
            if(tarefa.isDone()){
                done++;
            }
            if(tarefa.isImportant()){
                important++;
            }
            if(tarefa.isArchived()){
                archived++;
            }
        }
        undone = all - done;
        spinnerItens = new String[][]{mItems, {String.valueOf(all), String.valueOf(archived), String.valueOf(done), String.valueOf(important), String.valueOf(undone)}};
    }
}
