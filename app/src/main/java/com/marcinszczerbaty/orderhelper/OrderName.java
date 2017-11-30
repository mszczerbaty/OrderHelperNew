package com.marcinszczerbaty.orderhelper;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

/**
 * Created by Marcin on 12.11.2017.
 */

public class OrderName extends AppCompatActivity{
    DBHelper db;
    Button addName;
    EditText ordName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.order_name);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        db = new DBHelper(this);
        addName = (Button) findViewById(R.id.addOrderName);
        ordName = (EditText) findViewById(R.id.orderName);

        addName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String status = "Niezrealizowane";
                db.addOrder(ordName.getText().toString(), status);
                Cursor data = db.getOrderID(ordName.getText().toString());
                int orderID = -1;
                while(data.moveToNext()){
                    orderID = data.getInt(0);
                }
                if(orderID > -1) {
                    Toast.makeText(getApplicationContext(), "Powiazane ID to: " + orderID, Toast.LENGTH_LONG).show();
                }
                Bundle dataBundle3 = new Bundle();
                dataBundle3.putInt("orderID", orderID);
                Intent intent = new Intent(getApplicationContext(), MakeOrder.class);
                intent.putExtras(dataBundle3);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(OrderName.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
