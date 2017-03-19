package com.raw.arview;


import android.graphics.Color;
import android.location.Location;

import com.raw.utils.LocationPlace;
import com.raw.utils.PaintUtils;

public class RadarView {

    public DataView view; //หน้าจอ
    public float range; //ระยะของเรด้า
    public static float RADIUS = 40; //ขนาดของเรด้า
    public static float origenX = 0, origentY = 0; //ตำแหน่งเรด้าบนจอโทรศัพท์
    public static int radarColor = Color.argb(100, 149, 237, 0); //สีเรด้า
    public float des;

    Location currentLocation = new Location("provider"); //ตำแหน่งต้นทาง
    Location destinedLocation = new Location("provider"); //ตำแหน่งปลายทาง

    public LocationPlace lp = new LocationPlace(); //class เก็บตัวแปร latitude longitude

    public float angleToShift;
    public float degreetopixcel;
    public float bearing;
    public float circleOriginX;
    public float circleOriginY;
    private float mscale;

    public float x = 0;
    public float y = 0;
    public float z = 0;

    float yaw = 0;
    double[] bearings;


    ARView arView = new ARView();

    public RadarView(DataView dataView, double[] bearings) {
        this.bearings = bearings;
        calculateMetrics();
    }

    public void calculateMetrics() {
        circleOriginX = origenX + RADIUS;
        circleOriginY = origentY + RADIUS;

        range = (float) arView.converToPix(10) * 100;
        mscale = range / arView.converToPix((int) RADIUS);
    }

    public void paint(PaintUtils dw, float yaw) {
        this.yaw = yaw;

        dw.setFill(true);
        dw.setColor(radarColor);
        dw.paintCircle(origenX + RADIUS, origentY + RADIUS, RADIUS);

        currentLocation.setLatitude(view.getLat());
        currentLocation.setLongitude(view.getLon());
        for (int i = 0; i < lp.latitudes.length; i++) {
            destinedLocation.setLatitude(lp.latitudes[i]);
            destinedLocation.setLongitude(lp.longitudes[i]);
            convLocToVec(currentLocation, destinedLocation);
            float x = this.x / mscale;
            float y = this.z / mscale;

            if (x * x + y * y < RADIUS * RADIUS) {
                dw.setFill(true);
                dw.setColor(Color.rgb(247, 156, 9));
                dw.paintRect(x + RADIUS, y + RADIUS, 2, 2);
            }
        }
    }

    public float getWidth() // ความกว้างบนหน้าจอโทรศัพท์
    {
        return RADIUS * 2;
    }

    public float getHeight() // ความสูงหน้าจอโทรศัพท์
    {
        return RADIUS * 2;
    }

    public void set(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public void convLocToVec(Location source, Location destinedLocation) {
        float[] z = new float[1];
        z[0] = 0;

        Location.distanceBetween(source.getLatitude(), source.getLongitude(), destinedLocation.getLatitude(), destinedLocation.getLongitude(), z);

        float[] x = new float[1];

        Location.distanceBetween(source.getLatitude(), source.getLongitude(), destinedLocation.getLatitude(), destinedLocation.getLongitude(), x);


        if (source.getLatitude() < destinedLocation.getLatitude()) {
            z[0] *= -1;
        }

        if (source.getLongitude() > destinedLocation.getLongitude()) {
            x[0] *= -1;
        }
        set(x[0], (float) 0, z[0]);
    }

}
