package sau.comsci.com.argeov7.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.LruCache;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

public class RequestHandler
{
    private static RequestHandler mInstance;
    private RequestQueue mrequestQueue;
    private ImageLoader mImageLoader;
    private static Context mCtx;

    private RequestHandler(Context context)
    {
        mCtx = context;
        mrequestQueue = getRequestQueue();

        mImageLoader = new ImageLoader(mrequestQueue, new ImageLoader.ImageCache(){
            private final LruCache<String,Bitmap> cache = new LruCache<String,Bitmap>(20);

            @Override
            public Bitmap getBitmap(String url)
            {
                return cache.get(url);
            }

            @Override
            public void putBitmap(String url, Bitmap bitmap)
            {
                cache.put(url,bitmap);
            }
        });
    }

    public static synchronized RequestHandler getInstance(Context context)
    {
        if(mInstance == null)
        {
            mInstance = new RequestHandler(context);
        }
        return mInstance;
    }

    public RequestQueue getRequestQueue()
    {
        if(mrequestQueue == null)
        {
            mrequestQueue = Volley.newRequestQueue(mCtx.getApplicationContext());
        }
        return mrequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req)
    {
        getRequestQueue().add(req);
    }

    public ImageLoader getmImageLoader()
    {
        return mImageLoader;
    }
}
