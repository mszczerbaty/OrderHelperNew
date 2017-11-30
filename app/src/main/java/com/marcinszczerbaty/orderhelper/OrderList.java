package com.marcinszczerbaty.orderhelper;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 * Created by Marcin on 29.11.2017.
 */

public class OrderList extends AppCompatActivity {
    ListView orders;
    CustomListView3 adapter;
    DBHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.order_list);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        db = new DBHelper(this);
        orders = (ListView) findViewById(R.id.orderList);

        ArrayList<String> orderList = new ArrayList<>();
        ArrayList<String> status = new ArrayList<>();

        Cursor cursor = db.getOrders();
        while(cursor.moveToNext()){
            orderList.add(cursor.getString(1));
            status.add(cursor.getString(2));
        }

        adapter = new CustomListView3(OrderList.this, orderList, status);
        orders.setAdapter(adapter);

        orders.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //get order id i databundle
                String name = parent.getItemAtPosition(position).toString();
                Cursor cursor = db.getOrderID(name);
                int orderID = -1;
                while(cursor.moveToNext()){
                    orderID = cursor.getInt(0);
                }
                Bundle dataBundle = new Bundle();
                dataBundle.putInt("orderID", orderID);
                Intent intent = new Intent(getApplicationContext(), OrderInfo.class);
                intent.putExtras(dataBundle);
                startActivity(intent);
                //otworzenie informacji o danym zleceniu
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(OrderList.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
