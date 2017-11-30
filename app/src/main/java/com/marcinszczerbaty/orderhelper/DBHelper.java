package com.marcinszczerbaty.orderhelper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Marcin on 16.09.2017.
 */

public class DBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "MAGAZYN";
    public static final String TABLE_PROD = "PRODUKTY";
    public static final String ID = "_id";
    public static final String NAME = "name";
    public static final String DESCRIPTION = "description";
    public static final String QUANTITY = "quantity";
    public static final String KEY_IMAGE = "image";
    public static int DATABASE_VERSION = 15;

    public static final String TABLE_ORD = "ZLECENIA";
    public static final String ID2 = "_id";
    public static final String ORD_NAME = "order";
    public static final String ORD_STATUS = "status";
    //public static final String PROD_NAME = "products";
    //public static final String COMP = "company";

    public static final String TABLE_PROD_ORD = "PRODZLEC";
    public static final String ID3 = "_id";
    public static final String ID_ORD = "idorder";
    public static final String ID_PROD = "idproduct";
    public static final String ORD_QUANTITY = "quantity";


    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_PROD + "( " +
                ID + " INTEGER PRIMARY KEY, " +
                NAME + " TEXT, " +
                DESCRIPTION + " TEXT, " +
                QUANTITY + " TEXT, " +
                KEY_IMAGE + " BLOB)");
        db.execSQL("CREATE TABLE " + TABLE_ORD + " ( " +
                ID2 + " INTEGER PRIMARY KEY, " +
                ORD_NAME + " TEXT, " +
                ORD_STATUS + " TEXT)");
        db.execSQL("CREATE TABLE " +
                TABLE_PROD_ORD + " ( " +
                ID3 + " INTEGER PRIMARY KEY, " +
                ID_ORD + " INTEGER, " +
                ID_PROD + " INTEGER, " +
                ORD_QUANTITY + " TEXT, " +
                "FOREIGN KEY("+ID_ORD+") REFERENCES " + TABLE_ORD+"("+ID2+"), " +
                "FOREIGN KEY("+ID_PROD+") REFERENCES " + TABLE_PROD+"("+ID+"))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PROD);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ORD);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PROD_ORD);
        onCreate(db);
    }

    //******************** tabela produktow ************************//

    public boolean addProduct(Product product) throws SQLiteException{
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(NAME,product.get_name());
        cv.put(DESCRIPTION,product.get_description());
        cv.put(QUANTITY,product.get_quantity());
        cv.put(KEY_IMAGE,product.get_image());
        db.insert(TABLE_PROD, null, cv);
        db.close();
        return true;
    }

    public Product getProduct(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_PROD, new String[] { ID,
                        NAME, DESCRIPTION, QUANTITY , KEY_IMAGE}, ID + "=?",
                new String[] { String.valueOf(id) }, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        Product product = new Product(Integer.parseInt(cursor.getString(0)),
                cursor.getString(1), cursor.getString(2), Integer.parseInt(cursor.getString(3)), cursor.getBlob(4));
        return product;
    }

    public Cursor getAllProducts() {
        String selectQuery = "SELECT * FROM " + TABLE_PROD;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        return cursor;
    }

    public int getProductsCount() {
        String countQuery = "SELECT  * FROM " + TABLE_PROD;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        //String count = Integer.toString(cursor.getCount());
        int count = cursor.getCount();
        cursor.close();
        return count;
    }

    public boolean updateProduct(Product product, String name, String desc, String quant, byte[] image) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(NAME,name);
        cv.put(DESCRIPTION,desc);
        cv.put(QUANTITY,quant);
        cv.put(KEY_IMAGE,image);
        String[] whereArgs = { String.valueOf(product.get_id()) };
        boolean updated = db.update(TABLE_PROD, cv, ID + " = ?", whereArgs) > 0;
        db.close();
        return updated;
    }

    public void deleteProduct(Product product) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_PROD, ID + " = ?",
                new String[] { String.valueOf(product.get_id()) });
        db.close();
    }

    public Cursor getProductID(String name){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_PROD + " WHERE " + NAME + " ='" + name + "'", null);
        return cursor;
    }

    public Cursor getProductName(int id){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_PROD + " WHERE " + ID + " ='" + id + "'", null);
        return cursor;
    }

    public boolean updateQuantity(int id, String previous, String todelete){
        SQLiteDatabase db = this.getWritableDatabase();
        int newquant = Integer.parseInt(previous) - Integer.parseInt(todelete);
        ContentValues cv = new ContentValues();
        cv.put(QUANTITY,Integer.toString(newquant));
        boolean updated = db.update(TABLE_PROD, cv, ID + " = " + id, null) > 0;
        db.close();
        return updated;
    }

    //******************** tabela zlecen ************************//

    public boolean addOrder(String name, String state){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(ORD_NAME, name);
        cv.put(ORD_STATUS, state);
        db.insert(TABLE_ORD, null, cv);
        db.close();
        return true;
    }

    public Cursor getOrderID(String name){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_ORD + " WHERE " + ORD_NAME + " ='" + name + "'", null);
        return cursor;
    }

    public Cursor getOrders(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_ORD, null);
        return cursor;
    }

    public Cursor getOrderName(int id){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_ORD + " WHERE " + ID2 + " ='" + id + "'",null);
        return cursor;
    }

    public Cursor getOrdersToRealise(){
        SQLiteDatabase db = this.getWritableDatabase();
        String torealise = "TO REALISE";
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_ORD + " WHERE " + ORD_STATUS + " ='" + torealise + "'", null);
        return cursor;
    }

    public boolean setRealised(int id, String state){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(ORD_STATUS, state);
        boolean updated = db.update(TABLE_ORD, cv, ID2 + " = " + id, null) > 0;
        db.close();
        return updated;
    }

    //******************** tabela produktow ************************//

    public boolean addQuantity(int ordId, int prodId, int quantity){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(ID_ORD, ordId);
        cv.put(ID_PROD, prodId);
        cv.put(ORD_QUANTITY, quantity);
        db.insert(TABLE_PROD_ORD, null, cv);
        db.close();
        return true;
    }

    public Cursor getQuantities(int id){
        SQLiteDatabase db = this.getWritableDatabase();
        String selectQuery = "SELECT * FROM " + TABLE_PROD_ORD + " WHERE " + ID_ORD + " ='" + id + "'";
        Cursor cursor = db.rawQuery(selectQuery, null);
        return cursor;
    }

    public Cursor getQuantity(int ordID, int prodID){
        SQLiteDatabase db = this.getWritableDatabase();
        String selectQuery = "SELECT * FROM " + TABLE_PROD_ORD + " WHERE (" + ID_ORD + " ='"+ordID+"' AND " +
                ID_PROD + " ='"+prodID+"')";
        Cursor cursor = db.rawQuery(selectQuery, null);
        return cursor;
    }

    public boolean updateQuantity(int mainID, String ordQuant){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(ORD_QUANTITY, ordQuant);
        boolean updated = db.update(TABLE_PROD_ORD, cv, ID3 + " = " + mainID, null) > 0;
        db.close();
        return updated;
    }

    public void deleteQuantity(int mainID){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_PROD_ORD, ID3 + " = " + mainID, null);
        db.close();
    }

    public Cursor getAllQuant(){
        SQLiteDatabase db = this.getWritableDatabase();
        String selectQuery = "SELECT * FROM " + TABLE_PROD_ORD;
        Cursor cursor = db.rawQuery(selectQuery, null);
        return cursor;
    }
}
