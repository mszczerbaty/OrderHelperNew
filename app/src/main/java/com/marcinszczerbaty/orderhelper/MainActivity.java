package com.marcinszczerbaty.orderhelper;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button makeOrder = (Button) findViewById(R.id.makeOrder);
        Button realOrder = (Button) findViewById(R.id.realOrder);
        Button editMag = (Button) findViewById(R.id.editMag);
        Button orderHist = (Button) findViewById(R.id.orderHist);

        makeOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), OrderName.class);
                startActivity(intent);
            }
        });

        realOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SelectOrder.class);
                startActivity(intent);
            }
        });

        editMag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), EditMag.class);
                startActivity(intent);
            }
        });

        orderHist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), OrderList.class);
                startActivity(intent);
            }
        });




    }
}
