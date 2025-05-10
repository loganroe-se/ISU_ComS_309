package com.example.cystaff_frontend.app;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.LruCache;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

/**
 * Singleton Public Class for the AppController that stores code related to handling server
 * requests and its associated requestQueue.
 */
public class AppController {

    private static AppController instance;
    private RequestQueue requestQueue;
    private ImageLoader imageLoader;
    private static Context ctx;

    private AppController(Context context) {
        ctx = context;
        requestQueue = getRequestQueue();

        imageLoader = new ImageLoader(requestQueue,
                new ImageLoader.ImageCache() {
                    private final LruCache<String, Bitmap>
                            cache = new LruCache<String, Bitmap>(20);

                    @Override
                    public Bitmap getBitmap(String url) {
                        return cache.get(url);
                    }

                    @Override
                    public void putBitmap(String url, Bitmap bitmap) {
                        cache.put(url, bitmap);
                    }
                });
    }

    /**
     * Getter for the AppController instance. If no instance exists, the Method calls the private
     * constructor Method.
     *
     * @param context - the current application context
     * @return the instance of the AppController Class
     */
    public static synchronized AppController getInstance(Context context) {
        if (instance == null) {
            instance = new AppController(context);
        }
        return instance;
    }

    /**
     * Getter for the requestQueue private field.
     * Returns if the requestQueue is instantiated, otherwise, a new instance is created.
     *
     * @return the application's requestQueue instance
     */
    public RequestQueue getRequestQueue() {
        if (requestQueue == null) {
            // getApplicationContext() is key, it keeps you from leaking the
            // Activity or BroadcastReceiver if someone passes one in.
            requestQueue = Volley.newRequestQueue(ctx.getApplicationContext());
        }
        return requestQueue;
    }

    /**
     * A Method to add a request to the requestQueue
     *
     * @param req - The request Object to add to the requestQueue
     * @param <T> - The type of Object to add to the requestQueue
     */
    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }

    /**
     * Getter for the imageLoader private field
     *
     * @return imageLoader - the AppController's imageLoader
     */
    public ImageLoader getImageLoader() {
        return imageLoader;
    }
}
