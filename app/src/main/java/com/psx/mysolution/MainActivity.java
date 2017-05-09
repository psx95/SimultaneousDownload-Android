package com.psx.mysolution;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.okhttp.OkHttpClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.android.view.OnClickEvent;
import rx.android.view.ViewObservable;
import rx.functions.Action1;
import rx.functions.Func2;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private com.squareup.okhttp.Response response;
    private Context context;
    public TextView startTimeForUrl_comments, startTimeForUrl_photos, startTimeForUrl_todos, startTimeForUrl_posts;
    public TextView endTimeForUrl_comments, endTimeForUrl_photos,endTimeForUrl_todos, endTimeForUrl_posts;
    public TextView startSaveForUrl_comments, startSaveForUrl_photos, startSaveForUrl_todos, startSaveForUrl_posts;
    public TextView endSaveForUrl_comments, endSaveForUrl_photos, endSaveForUrl_todos, endSaveForUrl_posts;
    public String urls [] = {"https://jsonplaceholder.typicode.com/comments","https://jsonplaceholder.typicode.com/photos","https://jsonplaceholder.typicode.com/todos","https://jsonplaceholder.typicode.com/posts"};
    public JSONArray jsonArrayComments, jsonArrayPhotos, jsonArrayTodos, jsonArrayPosts;
    public Button button_url_comments, button_url_photos, button_url_todos, button_url_posts, button_currentTimestamp;
    public Observable<JSONArray> fetchComments, fetchPhotos, fetchTodos, fetchPosts;
    public Observable<OnClickEvent> observableClick;
    public Observable<JSONObject> zipped;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // finding all the TextViews in the layouts
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
        // finding all the buttons in the layout
        button_url_comments = (Button) findViewById(R.id.button_url_comments);
        button_url_photos = (Button) findViewById(R.id.button_url_photos);
        button_url_todos = (Button) findViewById(R.id.button_url_todos);
        button_url_posts = (Button) findViewById(R.id.button_url_posts);
        button_currentTimestamp = (Button) findViewById(R.id.button_current_timestamp);
        // set Onclick listeners to each button
        button_currentTimestamp.setOnClickListener(this);
        button_url_comments.setOnClickListener(this);
        button_url_photos.setOnClickListener(this);
        button_url_todos.setOnClickListener(this);
        button_url_posts.setOnClickListener(this);
        //observableClick = ViewObservable.clicks(button_url_comments);

        // Observable for comments
        fetchComments = Observable.create(new Observable.OnSubscribe<JSONArray>() {
            @Override
            public void call(Subscriber<? super JSONArray> subscriber) {
                try {
                  //  startTimeForUrl_comments.append(getTime());
                    JSONArray data = fetcchDataOkHttp(urls[0]);
                    subscriber.onNext(data);
                   // endTimeForUrl_comments.append(getTime());
                    subscriber.onCompleted();
                }
                catch (Exception e){
                    subscriber.onError(e);
                }
            }
        });
        /*observableClick.observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<OnClickEvent>() {
            @Override
            public void call(OnClickEvent onClickEvent) {
                // this will start the data download from the url- comments
                fetchComments.subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Action1<JSONArray>() {
                            @Override
                            public void call(JSONArray jsonArray) {
                                if (jsonArray!=null) {
                                    startSaveForUrl_comments.append(getTime());
                                    Log.d("CHECKIG", jsonArray.length() + " COMMENTS");
                                    endSaveForUrl_comments.append(getTime());
                                }
                                else{
                                    Log.d("CHECKING","null");
                                }
                            }
                        });
            }
        });*/

        // Observable for fetching the photos
        fetchPhotos = Observable.create(new Observable.OnSubscribe<JSONArray>() {
            @Override
            public void call(Subscriber<? super JSONArray> subscriber) {
                try {
                    JSONArray data = fetcchDataOkHttp(urls[1]);
                    subscriber.onNext(data);
                    subscriber.onCompleted();
                }
                catch (Exception e){
                    subscriber.onError(e);
                }
            }
        });

        // observable  for todos
        fetchTodos = Observable.create(new Observable.OnSubscribe<JSONArray>() {
            @Override
            public void call(Subscriber<? super JSONArray> subscriber) {
                try{
                    JSONArray jsonArray = fetcchDataOkHttp(urls[2]);
                    subscriber.onNext(jsonArray);
                    subscriber.onCompleted();
                }
                catch (Exception e){
                    subscriber.onError(e);
                }
            }
        });

        // Observabe for fetching the posts
        fetchPosts = Observable.create(new Observable.OnSubscribe<JSONArray>() {
            @Override
            public void call(Subscriber<? super JSONArray> subscriber) {
                try{
                    JSONArray data = fetcchDataOkHttp(urls[3]);
                    subscriber.onNext(data);
                    subscriber.onCompleted();
                }
                catch (Exception e){
                    subscriber.onError(e);
                }
            }
        });

        // creating observable that fetches from both urls simultaneosly
        // zip operator
         /*zipped  = Observable.zip(fetchComments, fetchPosts, new Func2<JSONArray, JSONArray, JSONObject>() {
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
        });*/



    }

    @Override
    protected void onResume() {
        super.onResume();
        // wait for 5 seconds
        /*
        * Code to make Android wait for 5 seconds
        * */
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //ping all the 4 urls directly
                Log.d("CHECK","here");
               /* zipped.subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(getObserver());*/
               startTimeForUrl_comments.append(getTime());
               fetchPhotos.subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(getObserverForPhotos());
                startTimeForUrl_photos.append(getTime());
               fetchComments.subscribeOn(Schedulers.newThread())
                       .observeOn(AndroidSchedulers.mainThread())
                       .subscribe(getObserverForComments());
                startTimeForUrl_todos.append(getTime());
                fetchTodos.subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(getObserverForTodos());
                startTimeForUrl_posts.append(getTime());
                fetchPosts.subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(getObserverForPosts());
            }
        },5000);

    }

    // make separate Observers for the observables
    private Observer<JSONArray> getObserverForComments (){
        return new Observer<JSONArray>() {
            @Override
            public void onCompleted() {
                Log.d("OBSERVER COMMENTS","COMPLETED");
                endTimeForUrl_comments.append(getTime());
                Toast.makeText(context,"COMMENTS DOWNLOADED",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(Throwable e) {
                Log.e("ERROR",e.getMessage());
            }

            @Override
            public void onNext(JSONArray jsonArray) {
                if (jsonArray != null){
                    Log.d("OBSERVER",jsonArray.length()+" COMMENTS");
                }
            }
        };
    }

    private Observer<JSONArray> getObserverForPhotos (){
        return new Observer<JSONArray>() {
            @Override
            public void onCompleted() {
                Log.d("OBSERVER PHOTOS","COMPLETED");
                endTimeForUrl_photos.append(getTime());
                Toast.makeText(context,"PHOTOS DOWNLOADED",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(Throwable e) {
                Log.e("ERROR",e.getMessage());
            }

            @Override
            public void onNext(JSONArray jsonArray) {
                if (jsonArray != null){
                    Log.d("OBSERVER",jsonArray.length()+" PHOTOS");
                }
            }
        };
    }

    private Observer<JSONArray> getObserverForTodos (){
        return new Observer<JSONArray>() {
            @Override
            public void onCompleted() {
                Log.d("OBSERVER TODOS","COMPLETED");
                endTimeForUrl_todos.append(getTime());
                Toast.makeText(context,"TODOS DOWNLOADED",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(Throwable e) {
                Log.e("ERROR",e.getMessage());
            }

            @Override
            public void onNext(JSONArray jsonArray) {
                if (jsonArray != null){
                    Log.d("OBSERVER",jsonArray.length()+" TODOS");
                }
            }
        };
    }

    private Observer<JSONArray> getObserverForPosts  (){
        return new Observer<JSONArray>() {
            @Override
            public void onCompleted() {
                Log.d("OBSERVER POSTS","COMPLETED");
                endTimeForUrl_posts.append(getTime());
                Toast.makeText(context,"POSTS DOWNLOADED",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(Throwable e) {
                Log.e("ERROR",e.getMessage());
            }

            @Override
            public void onNext(JSONArray jsonArray) {
                if (jsonArray != null){
                    Log.d("OBSERVER",jsonArray.length()+" TODOS");
                    startSaveForUrl_todos.append(getTime());
                    Toast.makeText(context,"json not null",Toast.LENGTH_SHORT).show();
                }
            }
        };
    }

    /*private Observer<JSONObject> getObserver (){
        return new Observer<JSONObject>() {
            @Override
            public void onCompleted() {
                // observer(zipped) has finished emitting data
                //endSaveForUrl_comments.append(getTime());
                //endSaveForUrl_posts.append(getTime());
                Toast.makeText(context,"CHECKED",Toast.LENGTH_SHORT).show();
                Log.d("OBSERVER"," "+"completed");
            }

            @Override
            public void onError(Throwable e) {
                Log.d("OBSERVER",e.getMessage()+"");
                //Toast.makeText(context,e.getMessage(),Toast.LENGTH_LONG).show();
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
    }*/



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

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id){
            case R.id.button_url_comments:

                // this will start the data download from the url- comments
                fetchComments.subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Action1<JSONArray>() {
                            @Override
                            public void call(JSONArray jsonArray) {
                                if (jsonArray!=null) {
                                  //  startSaveForUrl_comments.append(getTime());
                                    Log.d("CHECKING RX", jsonArray.length() + " COMMENTS");
                                   // endSaveForUrl_comments.append(getTime());
                                }
                                else{
                                    Log.d("CHECKING","null");
                                }
                            }
                        });
                 break;
            case R.id.button_url_photos:

                // subscribe it - this will begin downloading the data
                fetchPhotos.subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Action1<JSONArray>() {
                            @Override
                            public void call(JSONArray jsonArray) {
                                // this function will be called whenever the observable emits any data
                                if (jsonArray != null){
                                    Log.d("CHECKING RX",jsonArray.length()+" PHOTOS");
                                }
                                else {
                                    Log.d("CHECKING RX", "PHOTOS were empty");
                                }
                            }
                        });
                break;
            case R.id.button_url_todos:

                // subscribe the above observable
                fetchTodos.subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Action1<JSONArray>() {
                            @Override
                            public void call(JSONArray jsonArray) {
                                // this action will be performed whever the observer emits the data
                                if (jsonArray != null) {
                                    Log.d("CHECKING RX", jsonArray.length() + " TODOS");
                                }
                                else {
                                    Log.d("CHECKING RX","json array for todos was null");
                                }
                            }
                        });
                break;
            case R.id.button_url_posts:

                // attach a observer and subscribe it -  this will begin downloading the data
                fetchPosts.subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Action1<JSONArray>() {
                            @Override
                            public void call(JSONArray jsonArray) {
                                // action performed whenever the observer emits the data
                                if (jsonArray !=  null) {
                                    Log.d("CHECKING RX", jsonArray.length() + " POSTS");
                                }
                                else {
                                    Log.d("CHECKING RX", "json array was null");
                                }
                            }
                        });
                break;
            case R.id.button_current_timestamp:
                Toast.makeText(context,"Current Timestamp is "+getTime(),Toast.LENGTH_LONG).show();
                break;
        }
    }
}
