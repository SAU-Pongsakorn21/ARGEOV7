package com.raw.utils;

public class LocationPlace
{
    //"วัดไผ่เลี้ยง","ณัฐกานตฺ์","บริษัท พรีเมียร์ เพลทติ้ง แอนด์ คอม","ประส","วัดท่าไม้"
    public double[] latitudes = new double[] {13.7162761,13.7157897,13.706378,13.6594573,13.7064231
            ,13.7064775,13.7162761};
    public double[] longitudes = new double[] {100.339622,100.3390378,100.3559572,100.2426938,100.3560555
            ,100.3562382,100.339622};
    public double[] position = new double[] {-480,1500,555,3000,2500,300,950};
    public float[][] coordinateArray = new float[latitudes.length][2];
    public double[] distance = new double[latitudes.length];
    public int[] idMarker = new int[] {0,1,2,3,4,5,6};

    public double[] bearings = new double[latitudes.length];

}
