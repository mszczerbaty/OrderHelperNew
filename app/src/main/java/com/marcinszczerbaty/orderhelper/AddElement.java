package com.marcinszczerbaty.orderhelper;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaScannerConnection;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.EditText;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Marcin on 14.09.2017.
 */

public class AddElement extends AppCompatActivity {

    DBHelper db;
    Button addProd;
    Button addImage;
    EditText nam;
    EditText desc;
    EditText quant;
    ImageView prodImage;
    public static final int REQUEST_CAMERA = 1;
    private int CAMERA_REQUEST = 1;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_element);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        db = new DBHelper(this);
        addProd = (Button) findViewById(R.id.addProd);
        addImage = (Button) findViewById(R.id.makePhoto);
        nam = (EditText) findViewById(R.id.prodName);
        desc = (EditText) findViewById(R.id.prodDesc);
        quant = (EditText) findViewById(R.id.prodQuant);
        prodImage = (ImageView) findViewById(R.id.prodImage);

        addImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(AddElement.this, Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(AddElement.this,
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
                if(nam.getText().toString().matches("") || desc.getText().toString().matches("") ||
                        quant.getText().toString().matches("")){
                    Toast.makeText(AddElement.this, "Musisz wypełnić wszystkie informacje", Toast.LENGTH_LONG).show();
                } else {
                    Bitmap bitmap = ((BitmapDrawable) prodImage.getDrawable()).getBitmap();
                    boolean added;
                    Product product1 = new Product(nam.getText().toString(),
                            desc.getText().toString(), Integer.parseInt(quant.getText().toString()),
                            DbBitmapUtility.getBytes(bitmap));
                    added = db.addProduct(product1);
                    if (added) {
                        Toast.makeText(AddElement.this, "Dodano produkt", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(AddElement.this, "Nie powiodlo sie", Toast.LENGTH_LONG).show();
                    }
                    Cursor cursor = db.getProductID(nam.getText().toString());
                    String textQr = "";
                    while (cursor.moveToNext()) {
                        textQr = cursor.getString(0);
                    }
                    MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
                    try {
                        BitMatrix bitMatrix = multiFormatWriter.encode(textQr, BarcodeFormat.QR_CODE, 200, 200);
                        BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
                        Bitmap bitmap2 = barcodeEncoder.createBitmap(bitMatrix);
                        String qrtitle = Integer.toString(db.getProductsCount());
                        saveToInternalStorage(bitmap2, qrtitle);
                    } catch (WriterException e) {
                        e.printStackTrace();
                    }
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                }
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

                Intent intent = new Intent(AddElement.this, EditMag.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public static class DbBitmapUtility {
        // convert from bitmap to byte array
        public static byte[] getBytes(Bitmap bitmap) {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 0, stream);
            return stream.toByteArray();
        }
        // convert from byte array to bitmap
        public static Bitmap getImage(byte[] image) {
            return BitmapFactory.decodeByteArray(image, 0, image.length);
        }
    }


    @NonNull
    private String saveToInternalStorage(Bitmap bitmapImage, String qrtitle){
        File mypath= getOutputMediaFile(qrtitle);
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
                MediaScannerConnection.scanFile(this, new String[] {mypath.toString()}, null, null);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return mypath.getAbsolutePath();
    }

    private  File getOutputMediaFile(String qrtitle) {
        File mediaStorageDir1 = new File(Environment.getExternalStorageDirectory().toString(),"/QRcodes");
        Toast.makeText(getApplicationContext(),mediaStorageDir1.toString(),Toast.LENGTH_LONG).show();
        if (!mediaStorageDir1.exists()) {
            if (!mediaStorageDir1.mkdirs()) {
                return null;
            }
        }
        File mediaFile;
        mediaFile = new File(mediaStorageDir1.getPath() + File.separator + qrtitle + ".jpg");
        return mediaFile;
    }
}