package com.psx.mysolution;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.RequestFuture;
import com.squareup.okhttp.OkHttpClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func2;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    Observable<JSONArray> zipped;
    private com.squareup.okhttp.Response response;
    private Context context;
    public TextView startTimeForUrl_comments, startTimeForUrl_photos, startTimeForUrl_todos, startTimeForUrl_posts;
    public TextView endTimeForUrl_comments, endTimeForUrl_photos,endTimeForUrl_todos, endTimeForUrl_posts;
    public TextView startSaveForUrl_comments, startSaveForUrl_photos, startSaveForUrl_todos, startSaveForUrl_posts;
    public TextView endSaveForUrl_comments, endSaveForUrl_photos, endSaveForUrl_todos, endSaveForUrl_posts;
    public String urls [] = {"https://jsonplaceholder.typicode.com/comments","https://jsonplaceholder.typicode.com/photos","https://jsonplaceholder.typicode.com/todos","https://jsonplaceholder.typicode.com/posts"};
    public JSONArray jsonArrayComments, jsonArrayPhotos, jsonArrayTodos, jsonArrayPosts;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startTimeForUrl_comments = (TextView) findViewById(R.id.textView14);
        startTimeForUrl_photos = (TextView) findViewById(R.id.textView17);
        startTimeForUrl_todos = (TextView) findViewById(R.id.textView6);
        startTimeForUrl_posts = (TextView) findViewById(R.id.textView11);
        endTimeForUrl_comments = (TextView) findViewById(R.id.textView15);
        endTimeForUrl_photos = (TextView) findViewById(R.id.textView18);
        endTimeForUrl_todos = (TextView) findViewById(R.id.textView9);
        endTimeForUrl_posts = (TextView) findViewById(R.id.textView12);
        startSaveForUrl_comments = (TextView) findViewById(R.id.textView16);
        startSaveForUrl_photos = (TextView) findViewById(R.id.textView19);
        startSaveForUrl_todos = (TextView) findViewById(R.id.textView10);
        startSaveForUrl_posts = (TextView) findViewById(R.id.textView13);
        endSaveForUrl_comments = (TextView) findViewById(R.id.endSaveComments);
        endSaveForUrl_photos = (TextView) findViewById(R.id.endSavePhotos);
        endSaveForUrl_todos = (TextView) findViewById(R.id.endSaveTodos);
        endSaveForUrl_posts = (TextView) findViewById(R.id.endSavePosts);
        context = this;
        // craete Observables for the 4 urls
        Observable<JSONArray> fetchComments = Observable.create(new Observable.OnSubscribe<JSONArray>() {
            @Override
            public void call(Subscriber<? super JSONArray> subscriber) {
                try {
                    JSONArray data = fetcchDataOkHttp(urls[0]);
                    subscriber.onNext(data);
                    subscriber.onCompleted();
                }
                catch (Exception e){
                    subscriber.onError(e);
                }
            }
        });
        fetchComments.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<JSONArray>() {
                    @Override
                    public void call(JSONArray jsonArray) {
                        if (jsonArray!=null) {
                            Log.d("CHECKIG", jsonArray.length() + " ");
                        }
                        else{
                            Log.d("CHECKING","null");
                        }
                    }
                });
        // Observabe for fetching the posts
        Observable<JSONArray> fetchPosts = Observable.create(new Observable.OnSubscribe<JSONArray>() {
            @Override
            public void call(Subscriber<? super JSONArray> subscriber) {
                try{
                    JSONArray data = fetcchDataOkHttp(urls[2]);
                    subscriber.onNext(data);
                    subscriber.onCompleted();
                }
                catch (Exception e){
                    subscriber.onError(e);
                }
            }
        });
        // attach a observer and subscribe it
        fetchPosts.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<JSONArray>() {
                    @Override
                    public void call(JSONArray jsonArray) {
                        // action performed whenever the observer emits the data
                        if (jsonArray !=  null) {
                            Log.d("CHECKING RX", jsonArray.length() + " not null");
                        }
                        else {
                            Log.d("CHECKING RX", "json array was null");
                        }
                    }
                });
        // creating observable that fetches from both urls simultaneosly
        // zip operator
        Observable<JSONObject> zipped  = Observable.zip(fetchComments, fetchPosts, new Func2<JSONArray, JSONArray, JSONObject>() {
            JSONObject jsonObject;
            @Override
            public JSONObject call(JSONArray jsonArray, JSONArray jsonArray2) {
                Log.d("ZIPPED","length of comments "+jsonArray.length()+" "+getTime()+" length of posts "+jsonArray2.length()+" "+getTime());
                try {
                    jsonObject = new JSONObject();
                    jsonObject.put("comments",jsonArray);
                    jsonObject.put("posts",jsonArray2);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return jsonObject;
            }
        });
        zipped.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(getObserver());
        // wait for 5 seconds
        /*
        * Code to make Android wait for 5 seconds
        * */

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //ping all the 4 urls directly
                Log.d("CHECK","here");
            }
        },5000);

    }

    private Observer<JSONObject> getObserver (){
        return new Observer<JSONObject>() {
            @Override
            public void onCompleted() {
                // observer(zipped) has finished emmitting data
                endSaveForUrl_comments.append(getTime());
                endSaveForUrl_posts.append(getTime());
                Log.d("OBSERVER"," "+"completed");
            }

            @Override
            public void onError(Throwable e) {
                Log.d("OBSERVER",e.getMessage()+"");
                Toast.makeText(context,e.getMessage(),Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNext(JSONObject jsonObject) {
                //called each time the observer emits the data
                try {
                    Log.d("OBSERVER",jsonObject.getJSONArray("posts").length()+" "+jsonObject.getJSONArray("comments").length());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        };
    }



    public String getTime(){
        Long currentStartTimestamp = System.currentTimeMillis()/1000;
        String ts = currentStartTimestamp.toString();
        return ts;
    }

    public JSONArray fetcchDataOkHttp (String url){
        try{
            OkHttpClient client = new OkHttpClient();
            com.squareup.okhttp.Request request = new com.squareup.okhttp.Request.Builder().url(url).build();
            response = client.newCall(request).execute();
            return new JSONArray(response.body().string());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

}
