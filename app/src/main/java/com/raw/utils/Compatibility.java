package com.raw.utils;


import android.content.Context;
import android.hardware.Camera;
import android.support.v7.app.AppCompatActivity;
import android.view.Display;
import android.view.WindowManager;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

@SuppressWarnings("deprecation")
public class Compatibility
{
    private static Method mParameters_getSupportedPreviewSizes;
    private static Method mDefaultDisplay_getRotation;

    static
    {
        initCompatibility();
    };

    private static void initCompatibility()
    {
        try
        {
            mParameters_getSupportedPreviewSizes = android.hardware.Camera.Parameters.class.getMethod(
                    "getSupportedPreviewSizes",new Class[] {});
            mDefaultDisplay_getRotation = Display.class.getMethod("getRotation", new Class[] {});
        }
        catch(NoSuchMethodException nsme)
        {

        }
    }

    public static List<Camera.Size> getSupportedPreviewSizes(Camera.Parameters params)
    {
        List<Camera.Size> retList = null;
        try
        {
            Object retObj = mParameters_getSupportedPreviewSizes.invoke(params);
            if(retObj != null)
            {
                retList = (List<Camera.Size>) retObj;
            }
        }
        catch(InvocationTargetException ite)
        {
            Throwable cause = ite.getCause();
            if(cause instanceof RuntimeException)
            {
                throw(RuntimeException) cause;
            }
            else if(cause instanceof  Error)
            {
                throw(Error) cause;
            }
            else
            {
                throw new RuntimeException(ite);
            }
        }
        catch(IllegalAccessException ie)
        {

        }
        return retList;
    }

    static public int getRotation(final AppCompatActivity activity)
    {
        int result = 1;
        try
        {
            Display display = ((WindowManager) activity.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
            Object retObj = mDefaultDisplay_getRotation.invoke(display);
            if(retObj != null)
            {
                result = (Integer) retObj;
            }
        }
        catch(Exception ex)
        {

        }
        return result;
    }
}
