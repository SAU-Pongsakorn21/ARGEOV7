package com.raw.utils;

import android.support.v7.app.AppCompatActivity;

public class LocationPlace extends AppCompatActivity {
    public double[] latitudes = new double[] {13.7162761,13.7157897,13.706378,13.6594573,13.7064231
            ,13.7064775,13.7162761,13.8212467};
    public double[] longitudes = new double[] {100.339622,100.3390378,100.3559572,100.2426938,100.3560555
            ,100.3562382,100.339622,100.6974059};
    //public double[] position = new double[] {-480,1500,555,3000,2500,300,950};
    public double[] position = new double[latitudes.length];
    public float[][] coordinateArray = new float[latitudes.length][2];
    public double[] distance = new double[latitudes.length];
    public int[] idMarker = new int[] {0,1,2,3,4,5,6,7};

    public double[] bearings = new double[latitudes.length];

    public String nameuser = "admin";
}
