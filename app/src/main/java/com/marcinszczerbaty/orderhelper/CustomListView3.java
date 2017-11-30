package com.marcinszczerbaty.orderhelper;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Marcin on 29.11.2017.
 */

public class CustomListView3 extends ArrayAdapter<String>{
    TextView orderName;
    TextView orderStatus;
    private final Activity context;
    private ArrayList<String> orders;
    private ArrayList<String> status;

    public CustomListView3(Activity context, ArrayList<String> orders, ArrayList<String> status){
        super(context, R.layout.list_row3, orders);
        this.context = context;
        this.orders = orders;
        this.status = status;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater layoutInflater = context.getLayoutInflater();
        View rowView = layoutInflater.inflate(R.layout.list_row3, null, true);

        orderName = (TextView) rowView.findViewById(R.id.orderName);
        orderStatus = (TextView) rowView.findViewById(R.id.orderStatus);

        orderName.setText(orders.get(position));
        orderStatus.setText(status.get(position));

        return rowView;
    }
}
