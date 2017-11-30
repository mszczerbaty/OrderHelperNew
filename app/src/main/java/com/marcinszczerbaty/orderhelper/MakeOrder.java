package com.marcinszczerbaty.orderhelper;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView;

import java.util.ArrayList;

/**
 * Created by Marcin on 14.09.2017.
 */

public class MakeOrder extends AppCompatActivity implements SearchView.OnQueryTextListener {
    DBHelper db;
    SearchView inputSearch;
    ListView magEl;
    Button endOrder;
    ListAdapter listAdapter;
    ListView orderQuantity;
    CustomListView2 customadapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.make_order);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        db = new DBHelper(this);
        magEl = (ListView) findViewById(R.id.magEl);
        endOrder = (Button) findViewById(R.id.endOrder);
        inputSearch = (SearchView) findViewById(R.id.inputSearch);
        orderQuantity = (ListView) findViewById(R.id.selectedProd);

        ArrayList<String> list = new ArrayList<>();
        Cursor products = db.getAllProducts();
        if(products.getCount() == 0){
            Toast.makeText(this, "Baza danych jest pusta", Toast.LENGTH_SHORT).show();
        } else {
            while(products.moveToNext()){
                list.add(products.getString(1));
            }
            listAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, list);
            magEl.setAdapter(listAdapter);
        }

        magEl.setTextFilterEnabled(true);
        setupSearchView();

        Bundle extras = getIntent().getExtras();
        final int orderId = extras.getInt("orderID");
        int nameid;

        final ArrayList<String> quants = new ArrayList<>();
        final ArrayList<String> names = new ArrayList<>();
        Cursor quantities = db.getQuantities(orderId);
        Cursor cursor2;
        if(quantities.getCount() == 0){
            Toast.makeText(this, "Baza danych jest pusta", Toast.LENGTH_SHORT).show();
        } else {
            while(quantities.moveToNext()){
                quants.add(quantities.getString(3));
                nameid = Integer.parseInt(quantities.getString(2));
                cursor2 = db.getProductName(nameid);
                cursor2.moveToFirst();
                names.add(cursor2.getString(1));
            }
        }

        customadapter = new CustomListView2(MakeOrder.this, names, quants);
        orderQuantity.setAdapter(customadapter);

        //wybor produktu i dodanie ilosci do zlecenia
        magEl.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //if(){//jezeli w liscie 2 znajduje sie produkt z listy1
                //    Toast.makeText(getApplicationContext(), "Produkt został już dodany do zlecenia", Toast.LENGTH_LONG).show();
                //} else {
                    String name = parent.getItemAtPosition(position).toString();
                    Toast.makeText(getApplicationContext(), "Item clicked: " + position + " \n" + "Name: " + name, Toast.LENGTH_LONG).show();
                    Bundle dataBundle2 = new Bundle();
                    dataBundle2.putString("product name", name);
                    Cursor data = db.getProductID(name);
                    int itemID = -1;
                    while (data.moveToNext()) {
                        itemID = data.getInt(0);
                    }
                    if (itemID > -1) {
                        Toast.makeText(getApplicationContext(), "Powiazane ID to: " + itemID, Toast.LENGTH_LONG).show();
                    }
                    dataBundle2.putInt("id product", itemID);
                    dataBundle2.putInt("id order", orderId);
                    Intent intent = new Intent(getApplicationContext(), OrderQuantity.class);
                    intent.putExtras(dataBundle2);
                    startActivity(intent);
                //}
            }
        });

        //edycja ilości wybranego produktu
        orderQuantity.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String name = parent.getItemAtPosition(position).toString();
                Toast.makeText(getApplicationContext(), "Item clicked: " + position + " \n" + "Name: " + name, Toast.LENGTH_LONG).show();
                //wyswietlenie id i nazwy produktu
                Bundle dataBundle = new Bundle();
                Cursor data = db.getProductID(name);
                int prodID = -1;
                while(data.moveToNext()){
                    prodID = data.getInt(0);
                }
                if(prodID > -1) {
                    Toast.makeText(getApplicationContext(), "Powiazane ID to: " + prodID, Toast.LENGTH_LONG).show();
                }
                dataBundle.putInt("prodID", prodID);
                dataBundle.putInt("orderID", orderId);
                Intent intent = new Intent(getApplicationContext(), EditOrder.class);
                intent.putExtras(dataBundle);
                startActivity(intent);
            }
        });

        //zakonczenie zlecenia i powrot do menu
        endOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String quanttodelete;
                String productname;
                String productid;
                String actualquant;
                for(int i = 0; i<quants.size(); i++){
                    //iteruje liste
                    quanttodelete = quants.get(i);
                    //pobieram ilosc
                    productname = names.get(i);
                    //pobieram nazwe produktu
                    Cursor cursor = db.getProductID(productname);
                    cursor.moveToFirst();
                    productid = cursor.getString(0);
                    //pobieram id produktu
                    actualquant = cursor.getString(cursor.getColumnIndex(DBHelper.QUANTITY));
                    //pobieram aktualna ilosc tego produktu
                    db.updateQuantity(Integer.parseInt(productid), actualquant, quanttodelete);
                    //zmniejszam w tabeli magazynu ilosc produktow o id
                }
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                Toast.makeText(getApplicationContext(), "Dodano zlecenie do bazy danych", Toast.LENGTH_LONG).show();
                startActivity(intent);
            }
        });
    }

    //klasy do searchview
    private void setupSearchView() {
        inputSearch.setIconifiedByDefault(false);
        inputSearch.setOnQueryTextListener(this);
        inputSearch.setSubmitButtonEnabled(true);
        inputSearch.setQueryHint("Search Here");
    }
    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }
    @Override
    public boolean onQueryTextChange(String newText) {
        if (TextUtils.isEmpty(newText)) {
            magEl.clearTextFilter();
        } else {
            magEl.setFilterText(newText.toString());
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(MakeOrder.this, OrderName.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
