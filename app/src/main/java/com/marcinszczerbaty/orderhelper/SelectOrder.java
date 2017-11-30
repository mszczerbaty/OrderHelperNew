package com.marcinszczerbaty.orderhelper;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by Marcin on 14.11.2017.
 */

public class SelectOrder extends AppCompatActivity{
    DBHelper db;
    ListView ordList;
    ListAdapter listAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sel_ord);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        ordList = (ListView) findViewById(R.id.orderList);
        db = new DBHelper(this);

        ArrayList<String> orders = new ArrayList<>();
        Cursor cursor = db.getOrdersToRealise();
        if(cursor.getCount() == 0){
            Toast.makeText(this, "Baza danych jest pusta", Toast.LENGTH_SHORT).show();
        } else {
            while(cursor.moveToNext()){
                orders.add(cursor.getString(1));
            }
        }
        listAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, orders);
        ordList.setAdapter(listAdapter);

        ordList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //przekazanie id zlecenia* : string name get item at position to string, get id by name
                String name = parent.getItemAtPosition(position).toString();
                Toast.makeText(getApplicationContext(), "Item clicked: " + position + " \n" + "Name: " + name, Toast.LENGTH_LONG).show();
                Cursor ordid = db.getOrderID(name);
                int orderID = -1;
                while(ordid.moveToNext()){
                    orderID = ordid.getInt(0);
                }
                if(orderID > -1) {
                    Toast.makeText(getApplicationContext(), "Powiazane ID to: " + orderID, Toast.LENGTH_LONG).show();
                }
                Bundle dataBundle = new Bundle();
                dataBundle.putInt("ordID", orderID);
                Intent intent = new Intent(getApplicationContext(), OrderScanner.class);
                intent.putExtras(dataBundle);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(SelectOrder.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
