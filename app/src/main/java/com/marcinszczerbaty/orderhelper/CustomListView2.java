package com.marcinszczerbaty.orderhelper;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Marcin on 13.11.2017.
 */

public class CustomListView2 extends ArrayAdapter<String> {
    TextView prodName;
    TextView prodQuant;
    private final Activity context;
    private ArrayList<String> names;
    private ArrayList<String> quants;
    public CustomListView2(Activity context,
                          ArrayList<String> names, ArrayList<String> quants) {
        super(context, R.layout.list_row2, names);
        this.context = context;
        this.names = names;
        this.quants = quants;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView= inflater.inflate(R.layout.list_row2, null, true);

        prodName = (TextView) rowView.findViewById(R.id.prname);
        prodQuant = (TextView) rowView.findViewById(R.id.quant1);

        prodName.setText(names.get(position));
        prodQuant.setText(quants.get(position));

        return rowView;
    }
}
