package sau.comsci.com.argeov7;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.raw.arview.ARView;
import com.raw.utils.MyCurrentLocation;
import com.raw.utils.VolleyCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import sau.comsci.com.argeov7.adapter.EndangeredItem;
import sau.comsci.com.argeov7.adapter.GridAdapter;
import sau.comsci.com.argeov7.utils.Constants;
import sau.comsci.com.argeov7.utils.RequestHandler;

public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private GridAdapter adapter;
    private List<EndangeredItem> itemList;
    String place_id,place_name,place_detail,user_username;
    private MyCurrentLocation myCurrentLocation;
    public List<String> namePlace,id_place;
    public List<Double> myLat,myLong,L_bearing;
    public SharedPreferences sharedPreferences;
    public double place_latitude,place_longitude;
    public Gson gson = new Gson();
    public int count = 0;

    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = getPreferences(MODE_PRIVATE);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        initCollapsingToolbar();
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view_main);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Dowloading data from server...");
        progressDialog.show();
        itemList = new ArrayList<>();
        adapter = new GridAdapter(this,itemList);

        RecyclerView.LayoutManager mlayoutManager = new GridLayoutManager(this,2);
        recyclerView.setLayoutManager(mlayoutManager);
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(2,dpToPx(10),true));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);

        prepareList();
        try
        {
            Glide.with(this).load(R.drawable.cover).into((ImageView) findViewById(R.id.backdrop));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }



    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        sharedPreferences.edit().clear();
    }

    @Override
    protected void onResume()
    {
        progressDialog.dismiss();
        getString(new VolleyCallback() {
            @Override
            public void onSuccessResponse(String result ,List<Double> Lat, List<Double> Long,List<String> namePlace,List<String> id_place) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("myLat",gson.toJson(Lat).toString());
                editor.putString("myLong",gson.toJson(Long).toString());
                editor.putString("id_place",gson.toJson(id_place).toString());
                editor.putString("name_place",gson.toJson(namePlace).toString());
                editor.putString("result",result);
                editor.commit();
            }
        });
        super.onResume();
        ((GridAdapter)adapter).setOnItemClickListener(new GridAdapter.MyClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                Intent intent = new Intent();
                if(position==0)
                {
                    intent = new Intent(MainActivity.this, ARView.class);
                    intent.putExtra("result",sharedPreferences.getString("result",""));
                    intent.putExtra("myLat",sharedPreferences.getString("myLat",""));
                    intent.putExtra("myLong",sharedPreferences.getString("myLong",""));
                    intent.putExtra("id_place",sharedPreferences.getString("id_place",""));
                    intent.putExtra("name_place",sharedPreferences.getString("name_place",""));
                }
                else if(position==1)
                {

                }
                else if(position ==2)
                {
                    intent = new Intent(MainActivity.this, Register_Activity.class);
                }

                startActivity(intent);
            }
        });
    }
    private void initCollapsingToolbar()
    {
        final CollapsingToolbarLayout collapsingToolbarLayout =
                (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);

        collapsingToolbarLayout.setTitle("");
        AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.appbar);
        appBarLayout.setExpanded(true);

        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener()
        {
            boolean isShow = false;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout mAppBarLayout, int verticalOffset)
            {
                if(scrollRange == -1)
                {
                    scrollRange = mAppBarLayout.getTotalScrollRange();
                }
                if(scrollRange + verticalOffset == 0)
                {
                    collapsingToolbarLayout.setTitle(getString(R.string.app_name));
                    isShow = true;
                }
                else if(isShow)
                {
                    collapsingToolbarLayout.setTitle("");
                    isShow = false;
                }
            }
        });
    }

    private void prepareList()
    {
        int[] covers = new int[] {
                R.drawable.ic_local_see_black_24dp,
                R.drawable.maptopview,
                R.drawable.register,
                R.drawable.home72
        };

        EndangeredItem a = new EndangeredItem("Camera View",covers[0]);
        itemList.add(a);

        a = new EndangeredItem("Map view",covers[1]);
        itemList.add(a);

        a = new EndangeredItem("Register",covers[2]);
        itemList.add(a);

        a = new EndangeredItem("Home",covers[3]);
        itemList.add(a);

        adapter.notifyDataSetChanged();

    }
    private class GridSpacingItemDecoration extends RecyclerView.ItemDecoration
    {
        private int spanCount;
        private int spacing;
        private boolean includeEdge;

        public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge)
        {
            this.spacing = spacing;
            this.spanCount = spanCount;
            this.includeEdge = includeEdge;
        }

        @Override

        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state)
        {
            int position = parent.getChildAdapterPosition(view);
            int column = position % spanCount;

            if(includeEdge)
            {
                outRect.left = spacing - column * spacing / spanCount;
                outRect.right = (column+1) * spacing/ spanCount;

                if(position < spanCount)
                {
                    outRect.top = spacing;
                }
                outRect.bottom = spacing;

            }else
            {
                outRect.left = column * spacing / spanCount;
                outRect.right = spacing - (column+1) * spacing/spanCount;
                if(position >= spanCount)
                {
                    outRect.top = spacing;
                }
            }
        }
    }

    private int dpToPx(int dp)
    {
        Resources r =getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,dp,r.getDisplayMetrics()));
    }


    public void getString(final VolleyCallback callback)
    {
        myLat = new ArrayList<Double>();
        myLong = new ArrayList<Double>();
        namePlace = new ArrayList<String>();
        id_place = new ArrayList<String>();
        JsonArrayRequest jsRequest = new JsonArrayRequest(Constants.URL_LOAD_LOCATION,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        JSONObject jsObj;
                        try {
                            for (int i = 0; i < response.length(); i++) {
                                jsObj = response.getJSONObject(i);
                                place_id = jsObj.getString("place_id");
                                place_name = jsObj.getString("place_name");
                                place_detail = jsObj.getString("place_detail");
                                place_latitude = jsObj.getDouble("place_latitude");
                                place_longitude = jsObj.getDouble("place_longitude");
                                user_username = jsObj.getString("user_username");
                                myLat.add(place_latitude);
                                myLong.add(place_longitude);
                                namePlace.add(place_name);
                                id_place.add(place_id);
                            }
                            count = myLat.size();
                            callback.onSuccessResponse(String.valueOf(count),myLat,myLong,namePlace,id_place);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        finally {

                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),"คุณไม่ได้เชื่อมต่อกับ Server กรุณาตรวจสอบการเชื่อมต่ออินเทอร์เน็ต",Toast.LENGTH_LONG).show();

            }
        });
        RequestHandler.getInstance(getApplicationContext()).addToRequestQueue(jsRequest);
    }

}
