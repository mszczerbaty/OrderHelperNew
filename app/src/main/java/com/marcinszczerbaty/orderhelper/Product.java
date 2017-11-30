package com.marcinszczerbaty.orderhelper;

import android.support.v7.app.AppCompatActivity;

/**
 * Created by Marcin on 16.09.2017.
 */

public class Product extends AppCompatActivity {

    public int _id;
    public String _name;
    public String _description;
    public int _quantity;
    public byte[] _image;

    public Product(){}

    public Product(int id, String name, String description, int quantity, byte[] image) {
        this._id = id;
        this._name = name;
        this._description = description;
        this._quantity = quantity;
        this._image = image;
    }

    public Product(String name, String description, int quantity, byte[] image){
        this._name = name;
        this._description = description;
        this._quantity = quantity;
        this._image = image;
    }

    //setters
    public void set_id(int _id) {
        this._id = _id;
    }

    public void set_name(String _name) {
        this._name = _name;
    }

    public void set_description(String _description) {
        this._description = _description;
    }

    public void set_quantity(int _quantity) {
        this._quantity = _quantity;
    }

    public void set_image(byte[] _image){ this._image= _image; }

    //getters
    public int get_id() {
        return _id;
    }

    public String get_name() {
        return _name;
    }

    public String get_description() {
        return _description;
    }

    public int get_quantity() {
        return _quantity;
    }

    public byte[] get_image() { return _image; }
}
