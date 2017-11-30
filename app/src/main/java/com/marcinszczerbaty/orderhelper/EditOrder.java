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
 * Created by Marcin on 18.11.2017.
 */

public class EditOrder extends AppCompatActivity {
    DBHelper db;
    TextView quant;
    TextView produ;
    EditText editquant;
    Button change;
    Button del;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_order);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        db = new DBHelper(this);
        quant = (TextView) findViewById(R.id.quantity);
        produ = (TextView) findViewById(R.id.name);
        editquant = (EditText) findViewById(R.id.quantity2);
        change = (Button) findViewById(R.id.changeQuant);
        del = (Button) findViewById(R.id.delete);

        Bundle extras = getIntent().getExtras();
        final int ordID = extras.getInt("orderID");
        int prodID = extras.getInt("prodID");

        Cursor cursor = db.getProductName(prodID);
        if(cursor!=null && cursor.getCount()>0){
            cursor.moveToFirst();
        }else{
            Toast.makeText(getApplicationContext(), "PUSTY CURSOR", Toast.LENGTH_LONG).show();
        }
        //powinno byc w if:
        final int available = Integer.parseInt(cursor.getString(cursor.getColumnIndex(DBHelper.QUANTITY)));
        final String name = cursor.getString(cursor.getColumnIndex(DBHelper.NAME));
        String quanti = cursor.getString(cursor.getColumnIndex(DBHelper.QUANTITY));
        produ.setText(name);
        quant.setText(quanti);
        //cursor.close();

        final Cursor cursor2 = db.getQuantity(ordID, prodID);
        if(cursor2!=null && cursor2.getCount()>0){
            cursor2.moveToFirst();
        }else{
            Toast.makeText(getApplicationContext(), "PUSTY CURSOR", Toast.LENGTH_LONG).show();
        }
        //powinno byc w if:
        String quantOrd = cursor2.getString(cursor2.getColumnIndex(DBHelper.ORD_QUANTITY));
        editquant.setText(quantOrd);
        //cursor2.close();

        change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Integer.parseInt(editquant.getText().toString())>available){
                    Toast.makeText(getApplicationContext(), "Brak dostępnej ilości produktu: " + name + " w magazynie", Toast.LENGTH_LONG).show();
                } else if(Integer.parseInt(editquant.getText().toString())<1) {
                    Toast.makeText(getApplicationContext(), "Należy wybrać przynajmniej 1 produkt", Toast.LENGTH_LONG).show();
                } else {
                    int mainID = Integer.parseInt(cursor2.getString(cursor2.getColumnIndex(DBHelper.ID3)));
                    db.updateQuantity(mainID, editquant.getText().toString());
                    Bundle dataBundle = new Bundle();
                    dataBundle.putInt("orderID", ordID);
                    Intent intent = new Intent(getApplicationContext(), MakeOrder.class);
                    intent.putExtras(dataBundle);
                    startActivity(intent);
                }
            }
        });

        del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int mainID = Integer.parseInt(cursor2.getString(cursor2.getColumnIndex(DBHelper.ID3)));
                db.deleteQuantity(mainID);
                Bundle dataBundle = new Bundle();
                dataBundle.putInt("orderID", ordID);
                Intent intent = new Intent(getApplicationContext(), MakeOrder.class);
                intent.putExtras(dataBundle);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Bundle extras = getIntent().getExtras();
                int ordID = extras.getInt("orderID");
                Bundle dataBundle = new Bundle();
                dataBundle.putInt("orderID", ordID);
                Intent intent = new Intent(EditOrder.this, MakeOrder.class);
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
