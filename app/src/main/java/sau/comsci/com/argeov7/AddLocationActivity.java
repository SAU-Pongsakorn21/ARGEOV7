package sau.comsci.com.argeov7;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.kosalgeek.android.photoutil.GalleryPhoto;
import com.kosalgeek.android.photoutil.ImageBase64;
import com.kosalgeek.android.photoutil.PhotoLoader;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import sau.comsci.com.argeov7.utils.Constants;
import sau.comsci.com.argeov7.utils.MyCommand;
import sau.comsci.com.argeov7.utils.RequestHandler;

import static sau.comsci.com.argeov7.utils.Constants.URL_UPLOAD_IMAGE;

public class AddLocationActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText edt_place_name, edt_place_detail;
    private TextView txt_latitude, txt_longitude, txt_username;
    private ProgressDialog progressDialog;

    public double place_latitude;
    public double place_longitude;
    ImageView imgAdd,iv_gallery,iv_uploade;
    GalleryPhoto galleryPhoto;
    LinearLayout linerMain;
    int place_id;
    String s_place_id[];

    final int GALLERY_REQUEST = 1200;

    ArrayList<String> imageList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_location);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_addLocation);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        galleryPhoto = new GalleryPhoto(getApplicationContext());
        linerMain = (LinearLayout) findViewById(R.id.add_lnl_showImage);


        Bundle bundle = getIntent().getExtras();
        place_latitude= bundle.getDouble("Latitude");
        place_longitude = bundle.getDouble("Longitude");
        s_place_id = bundle.getString("place_id").split(",");
        place_id = s_place_id.length;
        Log.d("lat",String.valueOf(place_latitude));

        init();

        imgAdd.setOnClickListener(this);
        iv_gallery.setOnClickListener(this);
        iv_uploade.setOnClickListener(this);

    }

    public void init()
    {
        edt_place_detail = (EditText) findViewById(R.id.add_edt_place_detail);
        edt_place_name = (EditText) findViewById(R.id.add_edt_place_name);

        txt_latitude = (TextView) findViewById(R.id.add_txt_latitude);
        txt_longitude = (TextView) findViewById(R.id.add_txt_longitude);
        txt_username = (TextView) findViewById(R.id.add_txt_username);

        imgAdd = (ImageView) findViewById(R.id.add_btn_add);
        iv_gallery = (ImageView) findViewById(R.id.img_gallery);
        iv_uploade = (ImageView) findViewById(R.id.img_upload);


        txt_username.setText(SharedPrefManager.getInstance(this).getUsername());
        txt_latitude.setText(String.valueOf(place_latitude));
        txt_longitude.setText(String.valueOf(place_longitude));
        progressDialog = new ProgressDialog(this);
    }

    private void addLocation() {


        final String place_name = edt_place_name.getText().toString().trim();
        final String place_detail = edt_place_detail.getText().toString().trim();

        progressDialog.setMessage("Adding Location...");
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.URL_ADD_LOCATION, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressDialog.dismiss();
                try
                {
                    JSONObject jsonObject = new JSONObject(response);
                    Toast.makeText(getApplicationContext(),jsonObject.getString("message"),Toast.LENGTH_SHORT).show();
                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.hide();
                Toast.makeText(getApplication(),error.getMessage(),Toast.LENGTH_SHORT).show();
            }
        })
        {
            @Override
            protected Map<String,String> getParams() throws AuthFailureError
            {
                Map<String,String> params = new HashMap<>();
                params.put("place_name",place_name);
                params.put("place_detail",place_detail);
                params.put("place_latitude",String.valueOf(place_latitude));
                params.put("place_longitude",String.valueOf(place_longitude));
                params.put("user_username",SharedPrefManager.getInstance(getApplicationContext()).getUsername());
                return params;
            }
        };

        RequestHandler.getInstance(this).addToRequestQueue(stringRequest);
    }

    @Override
    public void onClick(View view)
    {
        if(view == imgAdd)
        {
            addLocation();
            uploadImage();
        }
        else if(view == iv_gallery)
        {
            Intent intent = galleryPhoto.openGalleryIntent();
            startActivityForResult(intent,GALLERY_REQUEST);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK)
        {
            if(requestCode == GALLERY_REQUEST)
            {
                galleryPhoto.setPhotoUri(data.getData());
                final String photoPath =galleryPhoto.getPath();
                imageList.add(photoPath);

                try {
                    final Bitmap bitmap = PhotoLoader.init().from(photoPath).requestSize(512,512).getBitmap();

                    final ImageView imageView = new ImageView(getApplicationContext());
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT);
                    imageView.setLayoutParams(layoutParams);

                    imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                    imageView.setPadding(0,0,0,10);
                    imageView.setAdjustViewBounds(true);
                    imageView.setImageBitmap(bitmap);

                    linerMain.addView(imageView);

                    imageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            imageList.remove(photoPath);
                            linerMain.removeView(imageView);
                        }
                    });
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

            }
        }
    }

    public void uploadImage()
    {
        final MyCommand myCommand = new MyCommand(getApplicationContext());
        Log.d("imagePath",""+imageList.size());
        for(String imagePath : imageList)
        {
            try {
                Bitmap bitmap = PhotoLoader.init().from(imagePath).requestSize(512,512).getBitmap();
                final String encodedString = ImageBase64.encode(bitmap);
                StringRequest stringRequest = new StringRequest(Request.Method.POST,URL_UPLOAD_IMAGE, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(getApplicationContext(),response,Toast.LENGTH_SHORT).show();
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(),"Error while upload image",Toast.LENGTH_SHORT).show();
                    }
                })
                {
                    @Override
                    protected Map<String,String> getParams() throws AuthFailureError{
                        Map<String,String> params= new HashMap<>();
                        params.put("image",encodedString);
                        params.put("place_id",String.valueOf(place_id));
                        Log.d("params",""+params);

                        return params;
                    }
                };

                myCommand.add(stringRequest);

            } catch (FileNotFoundException e) {
                Toast.makeText(getApplicationContext(),"Error while loading image",Toast.LENGTH_SHORT).show();
            }
        }
        linerMain.removeAllViews();
        imageList.clear();
        myCommand.execute();
    }
}
