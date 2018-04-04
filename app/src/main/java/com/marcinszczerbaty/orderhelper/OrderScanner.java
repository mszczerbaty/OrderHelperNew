package com.marcinszczerbaty.orderhelper;

import android.*;
import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.SparseArray;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Marcin on 15.11.2017.
 */

public class OrderScanner extends AppCompatActivity {
    DBHelper db;
    SurfaceView prev;
    TextView name;
    TextView desc;
    TextView quant;
    ImageView picture;
    Button confirm;
    ImageView detected;
    BarcodeDetector barcodeDetector;
    CameraSource cameraSource;
    final int RequestCameraPermissionID = 1001;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case RequestCameraPermissionID: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    try{
                        cameraSource.start(prev.getHolder());
                    } catch (IOException e){
                        e.printStackTrace();
                    }

                }
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ord_scan);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        db = new DBHelper(this);
        prev = (SurfaceView) findViewById(R.id.surfaceView);
        name = (TextView) findViewById(R.id.name2);
        desc = (TextView) findViewById(R.id.desc2);
        quant = (TextView) findViewById(R.id.quant1);
        picture = (ImageView) findViewById(R.id.prodImage);
        detected = (ImageView) findViewById(R.id.detected);
        confirm = (Button) findViewById(R.id.confirm);


        barcodeDetector = new BarcodeDetector.Builder(this)
                .setBarcodeFormats(Barcode.QR_CODE)
                .build();
        cameraSource = new CameraSource
                .Builder(this, barcodeDetector)
                .setRequestedPreviewSize(640, 480)
                .build();

        Bundle extras = getIntent().getExtras();
        final int orderID = extras.getInt("ordID");

        Cursor productsID = db.getQuantities(orderID);
        final ArrayList<String> prodIDs = new ArrayList<>();
        if(productsID.getCount() == 0){
            Toast.makeText(this, "Baza danych jest pusta", Toast.LENGTH_SHORT).show();
        } else {
            while(productsID.moveToNext()){
                prodIDs.add(productsID.getString(2));
            }
        }

        prev.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                if (ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(OrderScanner.this, new String[]{Manifest.permission.CAMERA}, RequestCameraPermissionID);
                    return;
                }
                try{
                    cameraSource.start(prev.getHolder());
                } catch (IOException e){
                    e.printStackTrace();
                }
            }
            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {}
            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                cameraSource.stop();
            }
        });

        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {
            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> qrcode = detections.getDetectedItems();
                final String coded = qrcode.valueAt(0).displayValue;
                if(qrcode.size() != 0){
                    if(prodIDs.contains(coded)){
                        final String code = coded;
                        name.post(new Runnable() {
                            @Override
                            public void run() {
                                Vibrator vibrator = (Vibrator)getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
                                vibrator.vibrate(1000);
                                int green = Color.parseColor("#00ff00");
                                detected.setColorFilter(green);
                                Cursor cursor = db.getProductName(Integer.parseInt(code));
                                cursor.moveToFirst();
                                Cursor cursor2 = db.getQuantity(orderID, Integer.parseInt(code));
                                cursor2.moveToFirst();
                                name.setText(cursor.getString(1));
                                desc.setText(cursor.getString(cursor.getColumnIndex(DBHelper.DESCRIPTION)));
                                quant.setText(cursor2.getString(cursor2.getColumnIndex(DBHelper.ORD_QUANTITY)));
                                picture.setImageBitmap(AddElement.DbBitmapUtility.getImage(cursor.
                                        getBlob(cursor.getColumnIndex(DBHelper.KEY_IMAGE))));
                            }
                        });
                        confirm.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if(prodIDs.contains(code)) {
                                    prodIDs.remove(code);
                                    int red = Color.parseColor("#ff0000");
                                    detected.setColorFilter(red);
                                    Toast.makeText(OrderScanner.this, "Pobrano produkt", Toast.LENGTH_LONG).show();
                                    if (prodIDs.isEmpty()) {
                                        String state = "Zrealizowane";
                                        if (db.setRealised(orderID, state)) {
                                            Toast.makeText(OrderScanner.this, "Pomyślnie zaktualizowano status zlecenia", Toast.LENGTH_LONG).show();
                                        }
                                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                        Toast.makeText(OrderScanner.this, "Zakończono realizację zlecenia", Toast.LENGTH_LONG).show();
                                        startActivity(intent);
                                    }
                                } else {
                                    Toast.makeText(OrderScanner.this, "Znajdz produkt", Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                    } else {
                        name.post(new Runnable() {
                            @Override
                            public void run() {
                                int red = Color.parseColor("#ff0000");
                                detected.setColorFilter(red);
                                name.setText("");
                                desc.setText("");
                                quant.setText("");
                                picture.setImageDrawable(null);
                            }
                        });
                    }
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(OrderScanner.this, SelectOrder.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
