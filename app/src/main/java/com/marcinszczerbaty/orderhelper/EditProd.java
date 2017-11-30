package com.marcinszczerbaty.orderhelper;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

/**
 * Created by Marcin on 05.11.2017.
 */

public class EditProd extends AppCompatActivity{
    DBHelper db;
    Button addProd;
    Button addImage;
    Button delProd;
    EditText nam;
    EditText desc;
    EditText quant;
    ImageView prodImage;
    ImageView qrimage;
    public static final int REQUEST_CAMERA = 1;
    private int CAMERA_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_prod);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        db = new DBHelper(this);
        addProd = (Button) findViewById(R.id.addProd);
        addImage = (Button) findViewById(R.id.makePhoto);
        delProd = (Button) findViewById(R.id.delProd);
        nam = (EditText) findViewById(R.id.prodName);
        desc = (EditText) findViewById(R.id.prodDesc);
        quant = (EditText) findViewById(R.id.prodQuant);
        prodImage = (ImageView) findViewById(R.id.prodImage);
        qrimage = (ImageView) findViewById(R.id.qrcode);

        Bundle extras = getIntent().getExtras();
        String prod = extras.getString("prod");
        Cursor cursor = db.getProductID(prod);
        cursor.moveToFirst();

        String name = cursor.getString(cursor.getColumnIndex(DBHelper.NAME));
        String descr = cursor.getString(cursor.getColumnIndex(DBHelper.DESCRIPTION));
        String quanti = cursor.getString(cursor.getColumnIndex(DBHelper.QUANTITY));
        byte[] image = cursor.getBlob(cursor.getColumnIndex(DBHelper.KEY_IMAGE));

        if(!cursor.isClosed()){
            cursor.close();
        }


        nam.setText(name);
        desc.setText(descr);
        quant.setText(quanti);
        prodImage.setImageBitmap(AddElement.DbBitmapUtility.getImage(image));

        addImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(EditProd.this, Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(EditProd.this,
                            new String[]{Manifest.permission.CAMERA},REQUEST_CAMERA);
                } else {
                    Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent, CAMERA_REQUEST);
                }
            }
        });

        addProd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle extras = getIntent().getExtras();
                int itemID = extras.getInt("itemID");
                Bitmap bitmap = ((BitmapDrawable)prodImage.getDrawable()).getBitmap();
                String newname = nam.getText().toString();
                String newdesc = desc.getText().toString();
                String newquant = quant.getText().toString();
                byte[] newimage = AddElement.DbBitmapUtility.getBytes(bitmap);
                db.updateProduct(db.getProduct(itemID), newname, newdesc, newquant, newimage);
                Intent intent = new Intent(getApplicationContext(), EditMag.class);
                startActivity(intent);
            }
        });

        delProd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle extras = getIntent().getExtras();
                int itemID = extras.getInt("itemID");
                db.deleteProduct(db.getProduct(itemID));
                Intent intent = new Intent(getApplicationContext(), EditMag.class);
                startActivity(intent);
            }
        });


    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CAMERA: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    Toast.makeText(getApplicationContext(), "Permission granted", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Permission denied", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            prodImage.setImageBitmap(photo);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:

                Intent intent = new Intent(EditProd.this, EditMag.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
