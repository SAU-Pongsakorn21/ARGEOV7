package sau.comsci.com.argeov7;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import sau.comsci.com.argeov7.utils.Constants;
import sau.comsci.com.argeov7.utils.RequestHandler;

public class AddLocationActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText edt_place_name, edt_place_detail;
    private TextView txt_latitude, txt_longitude, txt_username;
    private Button btn_add_location, btn_cancel;
    private ProgressDialog progressDialog;

    boolean checkimage = true;
    public double place_latitude;
    public double place_longitude;
    ImageView imgAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_location);
        Bundle bundle = getIntent().getExtras();
        place_latitude= bundle.getDouble("Latitude");
        place_longitude = bundle.getDouble("Longitude");
        Log.d("lat",String.valueOf(place_latitude));
        edt_place_detail = (EditText) findViewById(R.id.add_edt_place_detail);
        edt_place_name = (EditText) findViewById(R.id.add_edt_place_name);

        txt_latitude = (TextView) findViewById(R.id.add_txt_latitude);
        txt_longitude = (TextView) findViewById(R.id.add_txt_longitude);
        txt_username = (TextView) findViewById(R.id.add_txt_username);

        //btn_add_location = (Button) findViewById(R.id.add_btn_add);
        imgAdd = (ImageView) findViewById(R.id.add_btn_add);
        btn_cancel = (Button) findViewById(R.id.add_btn_cancel);
        txt_username.setText("admin");
        txt_latitude.setText(String.valueOf(place_latitude));
        txt_longitude.setText(String.valueOf(place_longitude));
        progressDialog = new ProgressDialog(this);

        //btn_add_location.setOnClickListener(this);
        btn_cancel.setOnClickListener(this);
        imgAdd.setOnClickListener(this);

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
                params.put("user_username","admin");
                return params;
            }
        };

        RequestHandler.getInstance(this).addToRequestQueue(stringRequest);
        //imgAdd.setBackgroundResource(R.drawable.add);
        checkimage = true;
    }

    @Override
    public void onClick(View view)
    {
        if(view == imgAdd)
        {
            checkimage = false;
            imgAdd.setBackgroundResource(R.drawable.add_click);

            addLocation();
        }
    }
}
