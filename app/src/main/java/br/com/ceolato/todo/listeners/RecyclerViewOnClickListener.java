package br.com.ceolato.todo.listeners;

import android.view.View;

/**
 * Created by CarlosAlberto on 20/10/2015.
 */
public interface RecyclerViewOnClickListener {
    public void onClickListener(View view, int position);
    public void onLongPressClickListener(View view, int position);
}
