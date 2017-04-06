package sau.comsci.com.argeov7;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

public class Register_Activity extends AppCompatActivity implements View.OnClickListener {
    private EditText edt_username, edt_password, edt_email;
    private Button btn_register;
    private ProgressDialog progressDialog;
    private TextView textLogin;
    Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_);

        edt_username = (EditText) findViewById(R.id.rgt_edt_username);
        edt_password = (EditText) findViewById(R.id.rgt_edt_password);
        edt_email = (EditText) findViewById(R.id.rgt_edt_email);
        textLogin = (TextView) findViewById(R.id.rgt_txt_login);
        btn_register = (Button) findViewById(R.id.rgt_btn_register);

        progressDialog = new ProgressDialog(this);

        btn_register.setOnClickListener(this);
        textLogin.setOnClickListener(this);


        toolbar = (Toolbar) findViewById(R.id.register_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    private void registerUser() {
        final String email = edt_email.getText().toString().trim();
        final String username = edt_username.getText().toString().trim();
        final String password = edt_password.getText().toString().trim();
        progressDialog.setMessage("Registering user...");
        progressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                Constants.URL_REGISTER, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressDialog.dismiss();
                try {
                    JSONObject jsonObject = new JSONObject(response);

                    Toast.makeText(getApplicationContext(), jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.hide();
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("username", username);
                params.put("password", password);
                params.put("email", email);
                return params;
            }
        };

        RequestHandler.getInstance(this).addToRequestQueue(stringRequest);
    }

    @Override
    public void onClick(View view) {
        if (view == btn_register) {
            registerUser();
        }
        else if(view == textLogin)
        {
            startActivity(new Intent(this,LoginActivity.class));
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
}
