package com.marcinszczerbaty.orderhelper;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by Marcin on 18.11.2017.
 */

public class DBTest extends AppCompatActivity{
    DBHelper db;
    ListView ordid;
    ListView ordname;
    ListView ordstate;
    ListView ordpordid;
    ListView idord;
    ListView idprod;
    ListView quant;
    ListAdapter listAdapter;
    ListAdapter listAdapter2;
    ListAdapter listAdapter3;
    ListAdapter listAdapter4;
    ListAdapter listAdapter5;
    ListAdapter listAdapter6;
    ListAdapter listAdapter7;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.db_test);

        db = new DBHelper(this);
        ordid = (ListView) findViewById(R.id.lv1);
        ordname = (ListView) findViewById(R.id.lv2);
        ordstate = (ListView) findViewById(R.id.lv3);
        ordpordid = (ListView) findViewById(R.id.lv4);
        idord = (ListView) findViewById(R.id.lv5);
        idprod = (ListView) findViewById(R.id.lv6);
        quant = (ListView) findViewById(R.id.lv7);

        ArrayList<String> orderid = new ArrayList<>();
        ArrayList<String> ordername = new ArrayList<>();
        ArrayList<String> state = new ArrayList<>();
        Cursor cursor1 = db.getOrders();
        if(cursor1.getCount() == 0){
            Toast.makeText(this, "Baza danych jest pusta", Toast.LENGTH_SHORT).show();
        } else {
            while(cursor1.moveToNext()){
                orderid.add(cursor1.getString(0));
                ordername.add(cursor1.getString(1));
                state.add(cursor1.getString(2));
            }
        }
        listAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, orderid);
        ordid.setAdapter(listAdapter);
        listAdapter2 = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, ordername);
        ordname.setAdapter(listAdapter2);
        listAdapter3 = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, state);
        ordstate.setAdapter(listAdapter3);

        ArrayList<String> orderproductid = new ArrayList<>();
        ArrayList<String> idorder = new ArrayList<>();
        ArrayList<String> idproduct = new ArrayList<>();
        ArrayList<String> quantity = new ArrayList<>();
        Cursor cursor2 = db.getAllQuant();
        while(cursor2.moveToNext()){
            orderproductid.add(cursor2.getString(0));
            idorder.add(cursor2.getString(1));
            idproduct.add(cursor2.getString(2));
            quantity.add(cursor2.getString(3));
        }

        listAdapter4 = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, orderproductid);
        ordpordid.setAdapter(listAdapter4);
        listAdapter5 = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, idorder);
        idord.setAdapter(listAdapter5);
        listAdapter6 = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, idproduct);
        idprod.setAdapter(listAdapter6);
        listAdapter7 = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, quantity);
        quant.setAdapter(listAdapter7);
    }
}
