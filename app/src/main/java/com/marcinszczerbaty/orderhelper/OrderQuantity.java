package com.marcinszczerbaty.orderhelper;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Marcin on 11.11.2017.
 */

public class OrderQuantity extends AppCompatActivity{

    TextView quant;
    TextView produ;
    EditText quant2;
    DBHelper db;
    Button add;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.order_quantity);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        quant = (TextView) findViewById(R.id.quantity);
        produ = (TextView) findViewById(R.id.name);
        quant2 = (EditText) findViewById(R.id.quantity2);
        add = (Button) findViewById(R.id.addQuant) ;
        db = new DBHelper(this);

        Bundle extras = getIntent().getExtras();
        String produc = extras.getString("product name");
        Cursor cursor = db.getProductID(produc);
        if(cursor!=null && cursor.getCount()>0){
            cursor.moveToFirst();
        }else{
            Toast.makeText(getApplicationContext(), "PUSTY CURSOR", Toast.LENGTH_LONG).show();
        }
        final int available = Integer.parseInt(cursor.getString(cursor.getColumnIndex(DBHelper.QUANTITY)));
        final String name = cursor.getString(cursor.getColumnIndex(DBHelper.NAME));
        String quanti = cursor.getString(cursor.getColumnIndex(DBHelper.QUANTITY));

        if(!cursor.isClosed()){
            cursor.close();
        }

        produ.setText(name);
        quant.setText(quanti);

        final int ordID = extras.getInt("id order");
        final int prodID = extras.getInt("id product");

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(quant2.getText().toString().isEmpty()){
                    Toast.makeText(getApplicationContext(), "Nalezy wpisac ilosc", Toast.LENGTH_LONG).show();
                } else {
                    if (Integer.parseInt(quant2.getText().toString()) > available) {
                        Toast.makeText(getApplicationContext(), "Brak dostępnej ilości produktu: " + name + " w magazynie", Toast.LENGTH_LONG).show();
                    } else if (Integer.parseInt(quant2.getText().toString()) < 1) {
                        Toast.makeText(getApplicationContext(), "Należy wybrać przynajmniej 1 produkt", Toast.LENGTH_LONG).show();
                    } else {
                        db.addQuantity(ordID, prodID, Integer.parseInt(quant2.getText().toString()));
                        Bundle dataBundle4 = new Bundle();
                        dataBundle4.putInt("orderID", ordID);
                        Intent intent = new Intent(getApplicationContext(), MakeOrder.class);
                        intent.putExtras(dataBundle4);
                        startActivity(intent);
                    }
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Bundle extras = getIntent().getExtras();
                int ordID = extras.getInt("id order");
                Bundle dataBundle = new Bundle();
                dataBundle.putInt("orderID", ordID);
                Intent intent = new Intent(OrderQuantity.this, MakeOrder.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtras(dataBundle);
                startActivity(intent);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
