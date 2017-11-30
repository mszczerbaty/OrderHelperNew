package com.marcinszczerbaty.orderhelper;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Marcin on 29.11.2017.
 */

public class OrderInfo extends AppCompatActivity{
    TextView orderName;
    TextView status;
    CustomListView3 adapter;
    ListView list;
    DBHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.order_info);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        db = new DBHelper(this);
        orderName = (TextView) findViewById(R.id.orderName);
        list = (ListView) findViewById(R.id.productList);
        status = (TextView) findViewById(R.id.status);

        Bundle extras = getIntent().getExtras();
        int orderID = extras.getInt("orderID");

        ArrayList<String> products = new ArrayList<>();
        ArrayList<String> quantity = new ArrayList<>();

        Cursor cursor = db.getQuantities(orderID);
        while(cursor.moveToNext()){
            Cursor cursor2 = db.getProductName(Integer.parseInt(cursor.getString(2)));
            String name = "";
            while (cursor2.moveToNext()){
                name = cursor2.getString(1);
            }
            products.add(name);
            quantity.add(cursor.getString(3));
        }

        Cursor cursor3 = db.getOrderName(orderID);
        String nameOrder = "";
        String statusOrder = "";
        while(cursor3.moveToNext()){
            nameOrder = cursor3.getString(1);
            statusOrder = cursor3.getString(2);
        }
        orderName.setText(nameOrder);
        status.setText(statusOrder);

        adapter = new CustomListView3(OrderInfo.this, products, quantity);
        list.setAdapter(adapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(OrderInfo.this, OrderList.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
