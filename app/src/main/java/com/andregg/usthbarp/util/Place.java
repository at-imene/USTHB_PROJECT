package com.andregg.usthbarp.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.StrictMode;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

public class Place {
    String name, modelUrl;
    double latitude, longitude;
    int image, bgColor;
    Bitmap img;

    String MODEL_URL= "https://firebasestorage.googleapis.com/v0/b/rock-sublime-371122.appspot.com/o/";



    public Place(String name, String location, int image){
        this.name = name;
        this.latitude = new Double(location.split(",")[0]);
        this.longitude = new Double(location.split(",")[1]);
        this.image = image;
    }

    public Place(String name, String modelUrl, String location, int image, int bgColor) {
        this.name = name;
        this.modelUrl = MODEL_URL+modelUrl;

        this.latitude = new Double(location.split(",")[0]);
        this.longitude = new Double(location.split(",")[1]);
        this.image = image;
        this.bgColor = bgColor;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getModelUrl() {
        return modelUrl;
    }

    public void setModelUrl(String modelUrl) {
        this.modelUrl = modelUrl;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public int getBgColor() {
        return bgColor;
    }

    public void setBgColor(int bgColor) {
        this.bgColor = bgColor;
    }
}
