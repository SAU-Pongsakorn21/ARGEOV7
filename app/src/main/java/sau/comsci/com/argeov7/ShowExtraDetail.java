package sau.comsci.com.argeov7;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

public class ShowExtraDetail extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_extra_detail);
        Bundle bundle = getIntent().getExtras();

        if(bundle != null)
        {
            String name = bundle.getString("name_place");

            double distance = bundle.getDouble("distance");

            Toast.makeText(this,"Place name : "+name+"\nDistance : "+distance+"เมตร",Toast.LENGTH_SHORT).show();
        }
    }
}
