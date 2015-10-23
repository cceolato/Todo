package br.com.ceolato.todo.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import br.com.ceolato.todo.R;
import br.com.ceolato.todo.dao.TarefaDAO;
import br.com.ceolato.todo.entity.Tarefa;
import br.com.ceolato.todo.listeners.RecyclerViewOnClickListener;

/**
 * Created by CarlosAlberto on 20/10/2015.
 */
public class TodoRecyclerViewAdapter extends RecyclerView.Adapter<TodoRecyclerViewAdapter.MyViewHolder> {

    private LayoutInflater inflater;
    private SimpleDateFormat dateFormat;
    private SimpleDateFormat timeFormat;
    private List<Tarefa> listaTarefas;
    private RecyclerViewOnClickListener recyclerViewOnClickListener;
    private Context context;

    public TodoRecyclerViewAdapter(Context context, List<Tarefa> listaTarefas) {
        this.context = context;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.dateFormat = new SimpleDateFormat("dd/MM");
        this.timeFormat = new SimpleDateFormat("HH:mm");
        this.listaTarefas = listaTarefas;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = inflater.inflate(R.layout.listview_todo, viewGroup, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        Tarefa tarefa = listaTarefas.get(position);
        holder.textViewTitle.setText(tarefa.getTitle());
        holder.textViewDescription.setText(tarefa.getDescription());
        holder.textViewDate.setText(dateFormat.format(tarefa.getData()));
        holder.textViewTime.setText(timeFormat.format(tarefa.getData()));
        holder.imageViewImportant.setTag(tarefa.getId());

        if (tarefa.isImportant()){
            holder.imageViewImportant.setBackground(holder.itemView.getResources().getDrawable(android.R.drawable.star_on));
        }else{
            holder.imageViewImportant.setBackground(holder.itemView.getResources().getDrawable(android.R.drawable.star_off));
        }

        holder.imageViewImportant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TarefaDAO dao = new TarefaDAO(v.getContext());
                long id = (long) v.getTag();
                Tarefa tarefa = dao.consultar(id);
                tarefa.setImportant(!tarefa.isImportant());
                if (tarefa.isImportant()) {
                    v.setBackground(v.getResources().getDrawable(android.R.drawable.star_on));
                } else {
                    v.setBackground(v.getResources().getDrawable(android.R.drawable.star_off));
                }
                dao.alterar(tarefa);
                v.getContext().sendBroadcast(new Intent("UPDATE_LIST"));
            }
        });

        pintaFundo(tarefa, holder.itemView);
    }

    @Override
    public int getItemCount() {
        return listaTarefas.size();
    }

    public void setRecyclerViewOnClickListener (RecyclerViewOnClickListener r){
        recyclerViewOnClickListener = r;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView textViewTitle;
        public TextView textViewDescription;
        public TextView textViewDate;
        public TextView textViewTime;
        public ImageView imageViewImportant;

        public MyViewHolder(View itemView){
            super(itemView);
            textViewTitle = (TextView) itemView.findViewById(R.id.textViewTitle);
            textViewDescription = (TextView) itemView.findViewById(R.id.textViewDescription);
            textViewDate = (TextView) itemView.findViewById(R.id.textViewDate);
            textViewTime = (TextView) itemView.findViewById(R.id.textViewTime);
            imageViewImportant = (ImageView) itemView.findViewById(R.id.imageViewImportant);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (recyclerViewOnClickListener != null){
                recyclerViewOnClickListener.onClickListener(v, getPosition());
            }
        }

    }

    private void pintaFundo(Tarefa tarefa, View view){
        if(tarefa.isDone()) {
//            view.setBackgroundColor(view.getResources().getColor(R.color.green));
            ((TextView) view.findViewById(R.id.textViewTitle)).setTextColor(view.getResources().getColor(R.color.green));
        }else if (tarefa.getData().before(Calendar.getInstance().getTime())){
//            view.setBackgroundColor(view.getResources().getColor(R.color.red));
            ((TextView) view.findViewById(R.id.textViewTitle)).setTextColor(view.getResources().getColor(R.color.red));
        }else{
//            view.setBackgroundColor(view.getResources().getColor(R.color.white));
            ((TextView) view.findViewById(R.id.textViewTitle)).setTextColor(view.getResources().getColor(R.color.black));
        }
    }
}
