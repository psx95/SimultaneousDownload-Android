package com.psx.mysolution;

import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by Pranav on 07-05-2017.
 */

/* This class is No longer used by the Application, The class was created to handle the RequestQueue in Volley
 * But The Application no longer uses Volley for Network calls 
 */
public class ApplicationController extends com.activeandroid.app.Application {

    // Contains Variables accessible throught the application
    // this class will handle all the requests

    private RequestQueue requestQueue;
    public static final String TAG = ApplicationController.class.getSimpleName();
    private static ApplicationController applicationControllerInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        applicationControllerInstance = this;
    }

    public static synchronized ApplicationController getApplicationControllerInstance(){
        return applicationControllerInstance;
    }

    public RequestQueue getRequestQueue (){
        if (requestQueue == null) {
            // craete a new requestQueue
            requestQueue = Volley.newRequestQueue(getApplicationContext());
        }
        return this.requestQueue;
    }

    public <T> void addTorequestQueue (Request<T> request){
        request.setTag(TAG);
        getRequestQueue().add(request);
    }

    public void cancelPendingrequests (Object tag){
        // cancel all requests with the tag
        if (requestQueue != null){
            requestQueue.cancelAll(tag);
        }
    }
}
