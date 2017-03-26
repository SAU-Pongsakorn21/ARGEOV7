package com.raw.arview;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Location;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.raw.utils.Camera;
import com.raw.utils.LocationPlace;
import com.raw.utils.PaintUtils;
import com.raw.utils.RadarLine;

import java.util.ArrayList;

import sau.comsci.com.argeov7.R;

import static com.raw.arview.RadarView.RADIUS;

/**
 * Created by KorPai on 10/2/2560.
 */
@SuppressWarnings("deprecation")
public class DataView extends AppCompatActivity implements View.OnClickListener {
    RelativeLayout[] locationMarkerView;
    ImageView[] subjectImageView;
    RelativeLayout.LayoutParams[] layoutParamses;
    RelativeLayout.LayoutParams[] subjectImageViewParams;
    RelativeLayout.LayoutParams[] subjectTextViewParams;
    TextView[] locationTextView;

    ARView arView = new ARView();
    int[] nextXofText;

    ArrayList<Integer> nextYofText = new ArrayList<Integer>();

    public double[] bearings;
    float angleToShitf;
    float yPosition;
    boolean isClick = true;
    Location currentLocation = new Location("provider");


    //String[] places = new String[]{"ณัฐกานตฺ์", "บริษัท พรีเมียร์ เพลทติ้ง แอนด์ คอม", "มอเอเชีย", "วัดท่าไม้", "ธนาคารธนชาติ", "western Union", "ณัฐกานต์2", "my place"};

    boolean isInit = false;
    boolean isDrawing = true;

    double mark_position = 0;

    Context _context;
    LocationPlace lp = new LocationPlace();
    int width, height;
    android.hardware.Camera camera;

    float yawPrevious;
    float yaw = 0;
    float pitch = 0;
    float roll = 0;

    DisplayMetrics displayMetrics;
    RadarView radarPoints;

    RadarLine lrl = new RadarLine();
    RadarLine rrl = new RadarLine();
    float rx = 10, ry = 20;
    public float addX = 0, addY = 0;
    public float degreetopixelWidth;
    public float degreetopixelHeight;
    public float pixelstodp;
    public float bearing;
    public int keepView = 0;

    public double lat, lon;
    public int[][] coordinateArray;
    public String[] namePlace;
    public int c_count;
    double [] distance;
    public DataView(Context ctx) {
        this._context = ctx;
    }

    public boolean isInited() {

        return isInit;
    }

    public void init(int widthInit, int heightInit, android.hardware.Camera camera, DisplayMetrics displayMetrics, final RelativeLayout rel,SharedPreferences sharedPreferences ) {


        c_count = Integer.parseInt(sharedPreferences.getString("result",""));
        String[] idPlace = subStringName(sharedPreferences.getString("id_place",""));
        namePlace = subStringName(sharedPreferences.getString("name_place",""));
        locationMarkerView = new RelativeLayout[c_count];
        layoutParamses = new RelativeLayout.LayoutParams[c_count];
        subjectImageViewParams = new RelativeLayout.LayoutParams[c_count];
        subjectTextViewParams = new RelativeLayout.LayoutParams[c_count];
        subjectImageView = new ImageView[c_count];
        locationTextView = new TextView[c_count];
        nextXofText = new int[c_count];


        bearings = subString(sharedPreferences.getString("bearing",""));

        for (int i = 0; i < c_count; i++) {

            subjectImageView[i] = new ImageView(_context);
            locationMarkerView[i] = new RelativeLayout(_context);
            locationTextView[i] = new TextView(_context);

            layoutParamses[i] = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,RelativeLayout.LayoutParams.MATCH_PARENT);
            subjectTextViewParams[i] = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            subjectImageViewParams[i] = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);

            locationTextView[i].setText(checkTextToDisplay(namePlace[i]));
            locationTextView[i].setTextColor(Color.WHITE);
            locationTextView[i].setSingleLine();
            subjectImageView[i].setBackgroundResource(R.drawable.icon);

            subjectImageView[i].setId(R.id.ImageViewID);
            locationTextView[i].setId(R.id.TextViewID);

            locationMarkerView[i] = new RelativeLayout(_context);
            locationMarkerView[i].setBackgroundResource(R.drawable.shape_marker);

            layoutParamses[i].setMargins(displayMetrics.widthPixels / 2, displayMetrics.heightPixels / 2, 0, 0);

            subjectImageViewParams[i].addRule(RelativeLayout.ALIGN_PARENT_LEFT,RelativeLayout.TRUE);
            subjectImageViewParams[i].addRule(RelativeLayout.ALIGN_PARENT_TOP,RelativeLayout.TRUE);
            subjectTextViewParams[i].addRule(RelativeLayout.RIGHT_OF,subjectImageView[i].getId());
            subjectTextViewParams[i].addRule(RelativeLayout.ALIGN_BASELINE,subjectImageView[i].getId());

            locationMarkerView[i].setLayoutParams(layoutParamses[i]);
            locationMarkerView[i].addView(subjectImageView[i],subjectImageViewParams[i]);
            locationMarkerView[i].addView(locationTextView[i],subjectTextViewParams[i]);

            rel.addView(locationMarkerView[i]);
            locationMarkerView[i].setId(Integer.parseInt(idPlace[i])-1);
            locationMarkerView[i].setOnClickListener(this);
        }

        this.displayMetrics = displayMetrics;
        this.degreetopixelWidth = this.displayMetrics.widthPixels / camera.getParameters().getHorizontalViewAngle();
        this.degreetopixelHeight = this.displayMetrics.heightPixels / camera.getParameters().getVerticalViewAngle();
        System.out.println("camera.getParameters().getHorizontalViewAngle() == " + camera.getParameters().getHorizontalViewAngle());

        radarPoints = new RadarView(this, lp.bearings);
        this.camera = camera;
        width = widthInit;
        height = heightInit;

        lrl.set(0, -RADIUS);
        lrl.rotate(Camera.DEFAULT_VIEW_ANGLE / 2);
        lrl.add(rx + RADIUS, ry + RADIUS);
        rrl.set(0, -RADIUS);
        rrl.rotate(-Camera.DEFAULT_VIEW_ANGLE / 2);
        rrl.add(rx + RADIUS, ry + RADIUS);

        isInit = true;
        isClick = true;
    }

    public void draw(PaintUtils dw, float yaw, float pitch, float roll,int count,SharedPreferences sharedPreferences) {
        this.yaw = yaw;
        this.pitch = pitch;
        this.roll = roll;
        String dirText = "";

        int bearing = (int) this.yaw;
        int range = (int) (this.yaw / (360f / 16f));
        if (range == 15 || range == 0) dirText = "N";
        else if (range == 1 || range == 2) dirText = "NE";
        else if (range == 3 || range == 4) dirText = "E";
        else if (range == 5 || range == 6) dirText = "SE";
        else if (range == 7 || range == 8) dirText = "S";
        else if (range == 9 || range == 10) dirText = "SW";
        else if (range == 11 || range == 12) dirText = "W";
        else if (range == 13 || range == 14) dirText = "NW";

        radarPoints.view = this;
        dw.paintObj(radarPoints, rx + PaintUtils.XPADDING, ry + PaintUtils.YPADDING, -this.yaw, 1, this.yaw,sharedPreferences);
        dw.setFill(false);
        dw.setColor(Color.argb(100, 220, 0, 0));
        dw.paintLine(lrl.x, lrl.y, rx + RADIUS, ry + RADIUS);
        dw.paintLine(rrl.x, rrl.y, rx + RADIUS, ry + RADIUS);
        dw.setColor(Color.rgb(255, 255, 255));
        dw.setFontSize(12);

        radarText(dw, " " + bearing + ((char) 176) + " " + dirText, rx + RADIUS, ry - 5, true, false, -1,count,sharedPreferences);
        drawTextBlock(dw,count,sharedPreferences);
    }

    void radarText(PaintUtils dw, String txt, float x, float y, boolean bg, boolean isLocationBlock, int count,int count_marker,SharedPreferences sharedPreferences) {
        float padw = 4, padh = 2;
        float width = dw.getTextWidth(txt) + padw * 2;
        float height;
        double [] latitude = subString(sharedPreferences.getString("myLat",""));
        double [] longitude = subString(sharedPreferences.getString("myLong",""));
        distance = new double[count_marker];

        addX = 300;
        currentLocation.setLatitude(getLat());
        currentLocation.setLongitude(getLon());
        for (int i = 0; i < count_marker; i++) {
            distance[i] = calDistance(currentLocation.getLatitude(), currentLocation.getLongitude(), latitude[i], longitude[i])*1000;
        }
        if (isLocationBlock) {
            height = dw.getTextAsc() + dw.getTextDesc() + padh * 2 + 10;
        } else {
            height = dw.getTextAsc() + dw.getTextDesc() + padh * 2;
        }
        if (bg) {
            if (isLocationBlock) {
                if (distance[count] < 500) {
                    if (mark_position == lp.position[count]) {
                        addY = 200;
                    } else {
                        addY = 0;
                        if (distance[count] < 500) {
                            mark_position = lp.position[count];
                        }
                    }
                    //layoutParamses[count].setMargins((int) (x - width / 2 + lp.position[count]), (int) (y - height / 2 - 10 - addY), 0, 0);
                    layoutParamses[count].setMargins((int) (this.yaw + x), (int) (y - height / 2 - 10 - addY), 0, 0);
                    layoutParamses[count].height = RelativeLayout.LayoutParams.WRAP_CONTENT;
                    layoutParamses[count].width = 300;
                    subjectTextViewParams[count].addRule(RelativeLayout.CENTER_IN_PARENT);
                    locationMarkerView[count].setLayoutParams(layoutParamses[count]);
                    locationMarkerView[count].setVisibility(View.VISIBLE);
                    addY = 0;
                } else {
                    locationMarkerView[count].setVisibility(View.GONE);
                }
            } else {
                dw.setColor(Color.rgb(0, 0, 0));
                dw.setFill(true);
                dw.paintRect((x - width / 2) + PaintUtils.XPADDING, (y - height / 2) + PaintUtils.YPADDING, width, height);
                pixelstodp = (padw + x - width / 2) / ((displayMetrics.density) / 160);
                dw.setColor(Color.rgb(255, 255, 255));
                dw.setFill(false);
                dw.paintText((padw + x - width / 2) + PaintUtils.XPADDING, ((padh + dw.getTextAsc() + y - height / 2)) + PaintUtils.YPADDING, txt);
            }
        }
    }

    String checkTextToDisplay(String str) {
        if (str.length() > 15) {
            str = str.substring(0, 15) + "...";
        }
        return str;
    }


    void drawTextBlock(PaintUtils dw,int count,SharedPreferences sharedPreferences) {
        String[] namePlace = subStringName(sharedPreferences.getString("name_place",""));
        coordinateArray = new int[count][2];
        for (int i = 0; i < count; i++) {
            if (bearings[i] == 0) {
                if (this.pitch != 90) {
                    yPosition = (this.pitch - 90) * this.degreetopixelHeight + 200;
                } else {
                    yPosition = (float) this.height / 2;
                }

                bearings[i] = 360 - bearings[i];
                angleToShitf = (float) bearings[i] - this.yaw;
                nextXofText[i] = (int) (angleToShitf * this.degreetopixelWidth);
                yawPrevious = this.yaw;
                isDrawing = true;
                radarText(dw,namePlace[i], nextXofText[i], yPosition, true, true, i,count,sharedPreferences);
                coordinateArray[i][0] = nextXofText[i];
                coordinateArray[i][1] = (int) yPosition;
            } else {
                angleToShitf = (float) bearings[i] - this.yaw;
                if (this.pitch != 90) {
                    yPosition = (this.pitch - 90) * this.degreetopixelHeight + 200;
                } else {
                    yPosition = (float) this.height / 2;
                }

                nextXofText[i] = (int) ((displayMetrics.widthPixels / 2) + (angleToShitf * degreetopixelWidth));
                if (Math.abs(coordinateArray[i][0] - nextXofText[i]) > 50) {
                    radarText(dw, namePlace[i], (nextXofText[i]), yPosition, true, true, i,count,sharedPreferences);
                    coordinateArray[i][0] = (int) ((displayMetrics.widthPixels / 2) + (angleToShitf * degreetopixelWidth));
                    coordinateArray[i][1] = (int) yPosition;
                    //lp.position[i] = this.yaw + coordinateArray[i][0];
                    isDrawing = true;
                } else {
                    radarText(dw, namePlace[i], coordinateArray[i][0], yPosition, true, true, i,count,sharedPreferences);
                    isDrawing = false;
                }
            }
        }
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public double getLat() {
        return lat;
    }

    public double getLon() {
        return lon;
    }

    public double calDistance(Double lat1, Double lon1, Double lat2, Double lon2) {
        double d2r = Math.PI / 180;
        double dlon = (lon1 - lon2) * d2r;
        double dlat = (lat1 - lat2) * d2r;

        double a = Math.pow(Math.sin(dlat / 2.0), 2) + Math.cos(lat2 * d2r)
                * Math.cos(lat1 * d2r) * Math.pow(Math.sin(dlon / 2.0), 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double d = 6367 * c;

        return d;
    }

    @Override
    public void onClick(View v) {
        isClick = !isClick;

        locationMarkerView[v.getId()].setBackgroundResource(isClick ? R.drawable.shape_marker : R.drawable.shape_marker_click);

        //Toast.makeText(_context,"สถานที่ : "+places[v.getId()]+"\n ระยะทาง : "+lp.distance[v.getId()]+" เมตร",Toast.LENGTH_SHORT).show();
        final Dialog dialog = new Dialog(_context);
        Window window = dialog.getWindow();
        window.setGravity(Gravity.BOTTOM);

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        dialog.setContentView(R.layout.detail_location);
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        TextView txt_title = (TextView) dialog.findViewById(R.id.dtl_txt_title);
        txt_title.setText(namePlace[v.getId()]);
        TextView txt_detail = (TextView) dialog.findViewById(R.id.dtl_txt_show);
        txt_detail.setText("ระยะทาง : " + distance[v.getId()]);
        Button btnClose = (Button) dialog.findViewById(R.id.dtl_btn_close);
        dialog.setCancelable(false);
        keepView = v.getId();
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                locationMarkerView[keepView].setBackgroundResource(R.drawable.shape_marker);
                isClick = true;
            }
        });
        dialog.show();
    }

    public double[] calBearings(Location curent, double mMyLatitude, double mMyLongitude,SharedPreferences sharedPreferences) {
        int count = Integer.parseInt(sharedPreferences.getString("result",""));
        curent.setLatitude(mMyLatitude);
        curent.setLongitude(mMyLongitude);
        Location distance = new Location("distance");
        bearings = new double[count];
        double [] latitude = subString(sharedPreferences.getString("myLat",""));
        double [] longitude = subString(sharedPreferences.getString("myLong",""));
        if (bearing < 0) {
            bearing = 360 + bearing;
        }
        for (int i = 0; i < bearings.length; i++) {
            distance.setLatitude(latitude[i]);
            distance.setLongitude(longitude[i]);
            bearing = curent.bearingTo(distance);
            if (bearing < 0) {
                bearing = 360 + bearing;
            }
            bearings[i] = bearing;
        }
        return bearings;
    }

    public double[] subString(String value)
    {
        String [] text = value.split(",");
        double [] result = new double[text.length];
        for(int x=0;x<text.length;x++)
        {
            if(text[x].contains("["))
            {
                text[x] = text[x].replace("[","");
            }
            else if(text[x].contains("]"))
            {
                text[x] = text[x].replace("]","");
            }

            if(text[x].contains("\""))
            {
                text[x] = text[x].replace("\"","");
            }
            result[x] = Double.parseDouble(text[x]);
        }
        return result;
    }

    public String[] subStringName(String value)
    {
        String[] text = value.split(",");

        for(int x=0;x<text.length;x++)
        {
            if(text[x].contains("[\""))
            {
                text[x] = text[x].replace("[\"","");
            }
            else if(text[x].contains("\"]"))
            {
                text[x] = text[x].replace("\"]","");
            }

            if(text[x].contains("\""))
            {
                text[x] = text[x].replace("\"","");
            }
        }
        return text;
    }
}
