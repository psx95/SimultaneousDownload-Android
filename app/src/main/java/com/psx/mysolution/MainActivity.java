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

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
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

        // wait for 5 seconds
        /*
        * Code to make Android wait for 5 seconds
        * */

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //ping all the 4 urls directly
                Log.d("CHECK","here");
               // jsonArrayComments = fetchData(urls[0]);
               // Log.d("CHECK",jsonArrayComments.toString());
                //jsonArrayComments = fetchData(urls[0],jsonArrayComments);
                new DownloadTask().execute(urls[0]);
                /*JSONArray test = new JSONArray();
                test = fetcchDataOkHttp(urls[0]);
                if (test!=null){
                    Log.d("TEST",test.length()+" was not null");
                }
                else {
                    Log.d("TEST"," was null");
                }*/
            }
        },5000);

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

    public JSONArray fetchData (String url){
        RequestFuture<JSONArray> future = RequestFuture.newFuture();
        // a New Constructor added in the volley library
        // the null parameter is actually a json array that indicates the json arrya that can be posted with the request made
        // the last 2 parameters are the listener and the error listener
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET,url,null,future,future){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return super.getParams();
            }

        };
        request.setRetryPolicy(new DefaultRetryPolicy(300000,0,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        ApplicationController.getApplicationControllerInstance().addTorequestQueue(request);
        try {
            return future.get(2, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
            Log.d("CHECK",e.getMessage());
        } catch (TimeoutException e) {
//            Log.d("ERROR",e.getMessage());
            e.printStackTrace();
        }
        return null;
    }



    class DownloadTask extends AsyncTask <String, Void, JSONArray> {

        JSONArray res;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            startTimeForUrl_comments.append(getTime());
        }

        @Override
        protected JSONArray doInBackground(String... strings) {
            String url = strings[0];
            JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray response) {
                    //endTimeForUrl_comments.append(getTime());
                    res = response;
                    Log.d("CHECK REQUEST",""+res.length());
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d("VOLLEYERROR",error.getMessage());
                    Toast.makeText(context,error.getMessage(),Toast.LENGTH_SHORT).show();
                }
            });
            ApplicationController.getApplicationControllerInstance().addTorequestQueue(jsonArrayRequest);
            return res;
        }

        @Override
        protected void onPostExecute(JSONArray jsonArray) {
            super.onPostExecute(jsonArray);
            endSaveForUrl_comments.append(getTime());
        }
    }

    /*class DownloadTask extends AsyncTask <String, Void, JSONArray> {

        JSONArray jsonArray;
        @Override
        protected void onPostExecute(JSONArray aVoid) {
            super.onPostExecute(aVoid);
           *//* *//**//**//**//*List<Comments> commentsList = new Select().from(Comments.class).execute();
            for (int i = 0; i < commentsList.size();i++){
                Log.d("TABLE",commentsList.get(i).name +" "+i);
            }*//*

        }

        @Override
        protected JSONArray doInBackground(final String... strings) {
            String url = urls[0];
            JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray response) {
               /*//*Log.d("CHECK", response.length()+" length of response");
                    // Parsing the JSON ARRAY
                    String jsonResponse = "";
                    ActiveAndroid.beginTransaction();
                    try {
                    for (int i = 0; i < response.length(); i++){
                            if (strings[0].equals(urls[0])){
                                JSONObject comment = (JSONObject) response.get(i);
                                int postId = comment.getInt("postId");
                                int id = comment.getInt("id");
                                String name = comment.getString("name");
                                String email = comment.getString("email");
                                String body = comment.getString("body");
                                Comments comments = new Comments(postId,name,email,body);
                                comments.save();
                                jsonResponse += " "+body+" "+email+" "+name+" "+postId;
                            }
                            else if (strings[0].equals(urls[1])){
                                JSONObject photo = (JSONObject) response.get(i);
                                int albumnId = photo.getInt("albumnId");
                                int id = photo.getInt("id");
                                String title = photo.getString("title");
                                String url = photo.getString("url");
                                String thumbnailUrl = photo.getString("thumbnailUrl");
                                Photos photos = new Photos(albumnId,title,url,thumbnailUrl);
                                photos.save();
                            }
                            else if (strings[0].equals(urls[2])){
                                JSONObject todo = (JSONObject) response.get(i);
                                int userId = todo.getInt("userId");
                                int id  = todo.getInt("id");
                                String title = todo.getString("title");
                                boolean completed = todo.getString("completed").equals("true")? true:false;
                                jsonResponse += " "+completed+" "+title+" "+userId;
                                Todos todos = new Todos(userId,title,completed);
                                todos.save();
                            }
                            else if (strings[0].equals(urls[3])){
                                JSONObject post = (JSONObject) response.get(i);
                                Log.d("CHECKOBJECT",post.toString());
                                int userId;
                                if (post.has("userId")){
                                    userId = post.getInt("userId");
                                    Log.d("POSTS",userId+" not null");
                                }
                                else{
                                    userId = 0;
                                    Log.d("POSTS",userId+"is null");
                                }
                                int id = post.getInt("id");
                                String title = post.getString("title");
                                String body = post.getString("body");
                                jsonResponse += " "+body+" "+title+" "+userId;
                                Posts posts = new Posts(userId,title,body);
                                posts.save();
                            }
                        }
                        ActiveAndroid.setTransactionSuccessful();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    finally{
                        ActiveAndroid.endTransaction();
                    }/*//*
                    jsonArray = response;
                    Log.d("CHECK",jsonArray.toString());
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("CHECK",error.getMessage());
                    Toast.makeText(context,error.getMessage(),Toast.LENGTH_SHORT).show();
                }
            });
            ApplicationController.getApplicationControllerInstance().addTorequestQueue(jsonArrayRequest);
            return jsonArray;
        }
    }*/

}
