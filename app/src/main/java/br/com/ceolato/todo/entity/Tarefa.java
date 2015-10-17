package br.com.ceolato.todo.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by 1541714 on 13/10/2015.
 */
public class Tarefa implements Serializable {

    protected long id;
    protected String title;
    protected String description;
    protected Date data;
    protected boolean done;

    public Tarefa(){}

    public Tarefa (String title, String description, Date data){
        this.title = title;
        this.description = description;
        this.data = data;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDone(boolean done){
        this.done = done;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data;
    }

    public boolean getDone(){
        return done;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Tarefa tarefa = (Tarefa) o;

        return id == tarefa.id;

    }

    @Override
    public int hashCode() {
        return (int) (id ^ (id >>> 32));
    }
}
