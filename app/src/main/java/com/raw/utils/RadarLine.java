package com.raw.utils;

public class RadarLine
{
    public float x, y;

    public RadarLine()
    {
        set(0,0);
    }

    public RadarLine(float x, float y)
    {
        set(x,y);
    }

    public void set(float x, float y)
    {
        this.x = x;
        this.y = y;
    }

    public void rotate(double t)
    {
        float xp = (float) Math.cos(t) * x - (float) Math.sin(t) * y;
        float yp = (float) Math.sin(t) * x + (float) Math.cos(t) * y;

        x = xp;
        y = yp;
    }

    public void add(float x, float y)
    {
        this.x += x;
        this.y += y;
    }
}
