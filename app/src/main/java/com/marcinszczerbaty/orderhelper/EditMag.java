package com.marcinszczerbaty.orderhelper;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by Marcin on 14.09.2017.
 */

public class EditMag extends AppCompatActivity {
    DBHelper db;
    Button addEl;
    ListView elList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_mag);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        db = new DBHelper(this);
        addEl = (Button) findViewById(R.id.addEl);
        elList = (ListView) findViewById(R.id.elemList);

        addEl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), AddElement.class);
                startActivity(intent);
            }
        });

        ArrayList<String> prodNameList = new ArrayList<>();
        Cursor products = db.getAllProducts();
        if(products.getCount() == 0){
            Toast.makeText(this, "Baza danych jest pusta", Toast.LENGTH_SHORT).show();
        } else {
            while(products.moveToNext()){
                prodNameList.add(products.getString(1));
            }
        }

        ArrayList<byte[]> imageList = new ArrayList<>();
        Cursor products2 = db.getAllProducts();
        if(products2.getCount() == 0){
            Toast.makeText(this, "Baza danych obrazów produktów jest pusta", Toast.LENGTH_SHORT).show();
        } else {
            while(products2.moveToNext()){
                imageList.add(products2.getBlob(4));
            }
        }

        ArrayList<String> quantList = new ArrayList<>();
        Cursor quantities = db.getAllProducts();
        if(quantities.getCount() == 0){
            Toast.makeText(this, "Baza danych jest pusta", Toast.LENGTH_SHORT).show();
        } else {
            while(quantities.moveToNext()){
                quantList.add(quantities.getString(3));
            }
        }

        CustomListView adapter = new CustomListView(EditMag.this, prodNameList, imageList, quantList);
        elList.setAdapter(adapter);

        elList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String name = parent.getItemAtPosition(position).toString();
                Toast.makeText(getApplicationContext(), "Item clicked: " + position + " \n" + "Name: " + name, Toast.LENGTH_LONG).show();
                //wyswietlenie id i nazwy produktu
                Bundle dataBundle = new Bundle();
                dataBundle.putString("prod",name);
                //przekazanie nazwy produktu
                Cursor data = db.getProductID(name);
                int itemID = -1;
                while(data.moveToNext()){
                    itemID = data.getInt(0);
                }
                if(itemID > -1) {
                    Toast.makeText(getApplicationContext(), "Powiazane ID to: " + itemID, Toast.LENGTH_LONG).show();
                }
                dataBundle.putInt("itemID", itemID);
                Intent intent = new Intent(getApplicationContext(), EditProd.class);
                intent.putExtras(dataBundle);
                startActivity(intent);
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:

                Intent intent = new Intent(EditMag.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
