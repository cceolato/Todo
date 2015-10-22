package br.com.ceolato.todo.activity;

import android.view.View;
import android.widget.AdapterView;

/**
 * Created by CarlosAlberto on 22/10/2015.
 */
public class SpinnerMenu implements AdapterView.OnItemSelectedListener {

    final private int ALL = 0;
    final private int UNDONE = 1;
    final private int DONE = 2;
    final private int ARCHIVED = 3;

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        switch (position) {
            case ALL:

            case UNDONE:

            case DONE:

            case ARCHIVED:

            default:

        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
