package com.marcinszczerbaty.orderhelper;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Marcin on 30.10.2017.
 */

public class CustomListView extends ArrayAdapter<String> {

    ImageView imageView;
    TextView txtTitle;
    TextView quantity;
    private final Activity context;
    private ArrayList<String> names;
    private final ArrayList<byte[]> images;
    private ArrayList<String> quants;
    public CustomListView(Activity context,
                          ArrayList<String> names, ArrayList<byte[]> images, ArrayList<String> quants) {
        super(context, R.layout.list_row, names);
        this.context = context;
        this.names = names;
        this.images = images;
        this.quants = quants;

    }
    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView= inflater.inflate(R.layout.list_row, null, true);

        txtTitle = (TextView) rowView.findViewById(R.id.prname);
        imageView = (ImageView) rowView.findViewById(R.id.list_image);
        quantity = (TextView) rowView.findViewById(R.id.quant1) ;

        Bitmap bitmap = AddElement.DbBitmapUtility.getImage(images.get(position));
        txtTitle.setText(names.get(position));
        quantity.setText(quants.get(position));
        //zmniejszenie obrazu
        getResizedBitmap(bitmap, 20, 20);

        imageView.setImageBitmap(bitmap);
        return rowView;
    }

    public Bitmap getResizedBitmap(Bitmap bm, int newHeight, int newWidth) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
// CREATE A MATRIX FOR THE MANIPULATION
        Matrix matrix = new Matrix();
// RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight);
// RECREATE THE NEW BITMAP
        Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, false);
        return resizedBitmap;
    }
}
