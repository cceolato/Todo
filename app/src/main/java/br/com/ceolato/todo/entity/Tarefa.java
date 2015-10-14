package br.com.ceolato.todo.entity;

import java.util.Date;

/**
 * Created by 1541714 on 13/10/2015.
 */
public class Tarefa {

    protected long id;
    protected String title;
    protected String description;
    protected Date data;

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
}
