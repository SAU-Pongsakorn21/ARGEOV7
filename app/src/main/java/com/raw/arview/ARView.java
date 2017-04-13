package com.raw.arview;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.gson.Gson;
import com.raw.utils.Compatibility;
import com.raw.utils.MyCurrentLocation;
import com.raw.utils.OnLocationChangedListener;
import com.raw.utils.PaintUtils;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import sau.comsci.com.argeov7.AddLocationActivity;
import sau.comsci.com.argeov7.MainActivity;
import sau.comsci.com.argeov7.R;
import sau.comsci.com.argeov7.Register_Activity;

@SuppressWarnings("deprecation")
public class ARView extends AppCompatActivity implements OnLocationChangedListener, SensorEventListener, View.OnClickListener{

    public double mMyLatitude = 0;
    public double mMyLongitude = 0;
    private MyCurrentLocation myCurrentLocation;
    private static Context _context;
    PowerManager.WakeLock mWakeLock;
    CameraView cameraView;
    RadarMarkerView radarMarkerView;
    public RelativeLayout upperLayerLayout;
    public static PaintUtils paintScreen;
    public static DataView dataView;
    boolean isInited = false;
    public static float azimuth;
    public static float pitch;
    public static float roll;

    DisplayMetrics displayMetrics;
    Camera camera;
    public int screenWidth;
    public int screenHeight;

    private float RTmp[] = new float[9];
    private float Rot[] = new float[9];
    private float I[] = new float[9];
    private float grav[] = new float[3];
    private float mag[] = new float[3];
    private float results[] = new float[3];
    private SensorManager sensorMgr;
    private List<Sensor> sensors;
    private Sensor sensorGrav, sensorMag;

    static final float ALPHA = 0.25f;
    protected float[] gravSensorVals;
    protected float[] magSensorVals;
    Button btnAdd;



    String A_Lat, A_Long,A_placename,A_placeDetail,A_id_place;
    public int count = 0;
    public SharedPreferences sharedPreferences;
    public Gson gson = new Gson();


    public double check_location = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getData();
        setUpListeners();
        final PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        this.mWakeLock = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK, " ");
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        screenHeight = displayMetrics.heightPixels;
        screenWidth = displayMetrics.widthPixels;

        upperLayerLayout = new RelativeLayout(this);
        RelativeLayout.LayoutParams upperLayerLayoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        upperLayerLayout.setLayoutParams(upperLayerLayoutParams);
        upperLayerLayout.setBackgroundColor(Color.TRANSPARENT);

        btnAdd = new Button(this);
        btnAdd.setBackgroundResource(R.drawable.reorder);
        btnAdd.setWidth(RelativeLayout.LayoutParams.WRAP_CONTENT);
        btnAdd.setHeight(RelativeLayout.LayoutParams.WRAP_CONTENT);
        _context = this;
        cameraView = new CameraView(this);
        radarMarkerView = new RadarMarkerView(this, displayMetrics, upperLayerLayout,sharedPreferences);


        displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(cameraView);
        addContentView(radarMarkerView, new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT));
        addContentView(btnAdd, new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, 60, Gravity.RIGHT));
        addContentView(upperLayerLayout, upperLayerLayoutParams);
        if (!isInited) {
            dataView = new DataView(ARView.this);
            paintScreen = new PaintUtils();
            isInited = true;
        }
        btnAdd.setOnClickListener(this);


        checkGPS();
    }

    public void checkGPS()
    {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            Toast.makeText(this, "GPS is Enabled in your devide", Toast.LENGTH_SHORT).show();
        }else{
            showGPSDisabledAlertToUser();
        }
    }

    private void showGPSDisabledAlertToUser(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("GPS is disabled in your device. Would you like to enable it?")
                .setCancelable(false)
                .setPositiveButton("Goto Settings Page To Enable GPS",
                        new DialogInterface.OnClickListener(){
                            public void onClick(DialogInterface dialog, int id){
                                Intent callGPSSettingIntent = new Intent(
                                        android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                startActivity(callGPSSettingIntent);
                            }
                        });
        alertDialogBuilder.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int id){
                        dialog.cancel();
                    }
                });
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }

    public void setCheck_location(Double check)
    {
        this.check_location = check;
    }

    public double getCheck_location()
    {
        return check_location;
    }

    public void getData()
    {
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if(bundle.get("result").equals(""))
        {
            count = 0;
            A_Lat = "0";
            A_Long = "0";
            A_placename = "No name";
            A_id_place = "0";


            sharedPreferences = getPreferences(MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt("count",count);
            editor.putString("A_Lat",A_Lat);
            editor.putString("A_Long",A_Long);
            editor.putString("A_placename",A_placename);
            editor.putString("A_id_place",A_id_place);
            editor.commit();
        }
        else
        {
            count = Integer.parseInt(String.valueOf(bundle.get("result")));
            A_Lat = String.valueOf(bundle.get("myLat"));
            A_Long = String.valueOf(bundle.get("myLong"));
            A_placename = String.valueOf(bundle.get("name_place"));
            A_id_place = String.valueOf(bundle.get("id_place"));


            sharedPreferences = getPreferences(MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt("count",count);
            editor.putString("A_Lat",A_Lat);
            editor.putString("A_Long",A_Long);
            editor.putString("A_placename",A_placename);
            editor.putString("A_id_place",A_id_place);
            editor.commit();


        }

    }
    public static Context getContext() {
        return _context;
    }

    public int converToPix(int val) {
        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, val, _context.getResources().getDisplayMetrics());
        return (int) px;
    }

    @Override
    public void onLocationChanged(Location location) {
        mMyLatitude = location.getLatitude();
        mMyLongitude = location.getLongitude();

        setCheck_location(mMyLatitude);
        dataView.setLat(mMyLatitude);
        dataView.setLon(mMyLongitude);
    }

    @Override
    public void onResume() {
        super.onResume();
        myCurrentLocation.start();
        this.mWakeLock.acquire();
        sensorMgr = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensors = sensorMgr.getSensorList(Sensor.TYPE_ACCELEROMETER);
        if (sensors.size() > 0) {
            sensorGrav = sensors.get(0);
        }

        sensors = sensorMgr.getSensorList(Sensor.TYPE_MAGNETIC_FIELD);
        if (sensors.size() > 0) {
            sensorMag = sensors.get(0);
        }

        sensorMgr.registerListener(this, sensorGrav, SensorManager.SENSOR_DELAY_NORMAL);
        sensorMgr.registerListener(this, sensorMag, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        this.mWakeLock.release();

        sensorMgr.unregisterListener(this, sensorGrav);
        sensorMgr.unregisterListener(this, sensorMag);
        sensorMgr = null;
    }

    @Override
    public void onStop() {
        myCurrentLocation.stop();
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.sharedPreferences.edit().clear().apply();
        radarMarkerView.R_sharedPreferences.edit().clear().apply();

    }

    @Override
    public void onSensorChanged(SensorEvent evt) {
        if (evt.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            gravSensorVals = lowPass(evt.values.clone(), gravSensorVals);
            grav[0] = evt.values[0];
            grav[1] = evt.values[1];
            grav[2] = evt.values[2];
        } else if (evt.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            magSensorVals = lowPass(evt.values.clone(), magSensorVals);
            mag[0] = evt.values[0];
            mag[1] = evt.values[1];
            mag[2] = evt.values[2];
        }

        if (gravSensorVals != null && magSensorVals != null) {
            SensorManager.getRotationMatrix(RTmp, I, gravSensorVals, magSensorVals);

            int rotation = Compatibility.getRotation(this);

            if (rotation == 1) {
                SensorManager.remapCoordinateSystem(RTmp, SensorManager.AXIS_X, SensorManager.AXIS_MINUS_Z, Rot);
            } else {
                SensorManager.remapCoordinateSystem(RTmp, SensorManager.AXIS_Y, SensorManager.AXIS_MINUS_Z, Rot);
            }

            SensorManager.getOrientation(Rot, results);

            ARView.azimuth = (float) (((results[0] * 180) / Math.PI) + 180);
            ARView.pitch = (float) (((results[1] * 180) / Math.PI) + 90);
            ARView.roll = (float) (((results[2] * 180) / Math.PI));

            radarMarkerView.postInvalidate();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor arg0, int arg1) {

    }

    public void setUpListeners() {

        myCurrentLocation = new MyCurrentLocation(this);
        myCurrentLocation.buildGoogleApiClient(this);
        myCurrentLocation.start();
    }


    protected float[] lowPass(float[] input, float[] output) {
        if (output == null)
            return input;
        for (int i = 0; i < input.length; i++) {
            output[i] = output[i] + ALPHA * (input[i] - output[i]);
        }
        return output;
    }


    @Override
    public void onClick(View view) {
        PopupMenu popupMenu = new PopupMenu(this, view);
        popupMenu.inflate(R.menu.popup_menu);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.menu_main) {
                    Intent intent = new Intent(ARView.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } else if (item.getItemId() == R.id.menu_add) {
                    Intent intent = new Intent(ARView.this, AddLocationActivity.class);
                    intent.putExtra("Latitude", mMyLatitude);
                    intent.putExtra("Longitude", mMyLongitude);
                    intent.putExtra("place_id",A_id_place);
                    startActivity(intent);
                } else if (item.getItemId() == R.id.menu_register) {
                    Intent intent = new Intent(ARView.this, Register_Activity.class);
                    startActivity(intent);
                }
                return true;
            }
        });
        popupMenu.show();
    }
}

@SuppressWarnings("deprecation")
class CameraView extends SurfaceView implements SurfaceHolder.Callback {
    ARView arView;
    SurfaceHolder holder;
    android.hardware.Camera camera;

    public CameraView(Context context) {
        super(context);
        arView = (ARView) context;

        holder = getHolder();
        holder.addCallback(this);
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        try {
            if (camera != null) {
                try {
                    camera.stopPreview();
                } catch (Exception ignore) {

                }
                try {
                    camera.release();
                } catch (Exception ignore) {

                }
                camera = null;
            }

            camera = Camera.open();
            Camera.Parameters parameters = camera.getParameters();
            List<String> focusModes = parameters.getSupportedFocusModes();
            if(focusModes.contains(Camera.Parameters.FOCUS_MODE_AUTO))
            {
                parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
            }
            camera.setParameters(parameters);
            arView.camera = camera;
            camera.setPreviewDisplay(holder);
        } catch (Exception ex) {
            try {
                if (camera != null) {
                    if (camera != null) {
                        try {
                            camera.stopPreview();
                        } catch (Exception ignore) {

                        }
                        try {
                            camera.release();
                        } catch (Exception ignore) {

                        }
                        camera = null;
                    }
                }
            } catch (Exception ignore) {

            }
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        try {
            android.hardware.Camera.Parameters parameters = camera.getParameters();
            try {
                List<Camera.Size> supportedSizes = null;
                supportedSizes = com.raw.utils.Compatibility.getSupportedPreviewSizes(parameters);

                Iterator<Camera.Size> itr = supportedSizes.iterator();
                while (itr.hasNext()) {
                    Camera.Size element = itr.next();
                    element.width -= width;
                    element.height -= height;
                }
                Collections.sort(supportedSizes, new ResolutionsOrder());
                parameters.setPreviewSize(width + supportedSizes.get(supportedSizes.size() - 1).width, height + supportedSizes.get(supportedSizes.size() - 1).height);
            } catch (Exception ex) {
                parameters.setPreviewSize(arView.screenWidth, arView.screenHeight);
            }
            camera.setParameters(parameters);
            camera.startPreview();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        try {
            if (camera != null) {
                try {
                    camera.stopPreview();
                } catch (Exception ignore) {

                }
                try {
                    camera.release();
                } catch (Exception ignore) {

                }
                camera = null;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}

class RadarMarkerView extends View {
    ARView arView;
    DisplayMetrics displayMetrics;
    RelativeLayout upperLayoutView = null;
    int count = 0;
    SharedPreferences R_sharedPreferences;

    public RadarMarkerView(final Context context, DisplayMetrics displayMetrics, RelativeLayout rel,SharedPreferences sharedPreferences) {
        super(context);
        arView = (ARView) context;
        this.displayMetrics = displayMetrics;
        upperLayoutView = rel;
        count = sharedPreferences.getInt("count",0);
        R_sharedPreferences = sharedPreferences;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        ARView.paintScreen.setWidth(canvas.getWidth());
        ARView.paintScreen.setHeight(canvas.getHeight());
        ARView.paintScreen.setCanvase(canvas);
        if (!ARView.dataView.isInited() && arView.getCheck_location() != 0) {
            ARView.dataView.init(ARView.paintScreen.getWidth(), ARView.paintScreen.getHeight(), arView.camera, displayMetrics, upperLayoutView,R_sharedPreferences);
        }

        if(arView.getCheck_location() != 0)
        {
            ARView.dataView.draw(ARView.paintScreen, ARView.azimuth, ARView.pitch, ARView.roll,count,R_sharedPreferences);
        }
    }

}

@SuppressWarnings("deprecation")
class ResolutionsOrder implements java.util.Comparator<Camera.Size> {
    public int compare(Camera.Size left, Camera.Size right) {
        return Float.compare(left.width + left.height, right.width + right.height);
    }
}