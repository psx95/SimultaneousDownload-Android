package com.psx.mysolution;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.query.Select;
import com.psx.mysolution.Tables.Comments;
import com.psx.mysolution.Tables.Photos;
import com.psx.mysolution.Tables.Posts;
import com.psx.mysolution.Tables.Todos;
import com.squareup.okhttp.OkHttpClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
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

    }

    @Override
    protected void onResume() {
        super.onResume();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //ping all the 4 urls directly
                Log.d("CHECK","here");
               // pinging all the urls at the same time to begin the download in seperate threads
               subscribeTocomments();
               subscribToPhotos();
               subscribeToTodos();
               subscribeToPosts();
            }
        },5000);

    }

    public void subscribeTocomments (){
        //startTimeForUrl_comments.append(getTime());
        startTimeForUrl_comments.setText("Start: "+getTime());
        fetchComments.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(getObserverForComments());
    }

    public void subscribToPhotos (){
        fetchPhotos.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(getObserverForPhotos());
        startTimeForUrl_photos.setText("Start: "+getTime());
    }

    public void subscribeToTodos (){
        startTimeForUrl_todos.setText("Start: "+getTime());
        fetchTodos.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(getObserverForTodos());
    }

    public void subscribeToPosts (){
        startTimeForUrl_posts.setText("Start: "+getTime());
        fetchPosts.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(getObserverForPosts());
    }

    // make separate Observers for the observables
    private Observer<JSONArray> getObserverForComments (){
        return new Observer<JSONArray>() {
            @Override
            public void onCompleted() {
                Log.d("OBSERVER COMMENTS","COMPLETED");
                endTimeForUrl_comments.setText("End: "+getTime());
                //Toast.makeText(context,"COMMENTS DOWNLOADED",Toast.LENGTH_SHORT).show();
                if (jsonArrayComments != null){
                    // data base operation are making the frames skip. --> need to do database operations in a seperate thread
                    new PerformDBOperations().execute(jsonArrayComments);
                }
                else {
                    Log.d("SAVING COMMENTS","COMMENTS WERE NULL");
                }
            }

            @Override
            public void onError(Throwable e) {
                Log.e("ERROR",e.getMessage());
            }

            @Override
            public void onNext(JSONArray jsonArray) {
                if (jsonArray != null){
                    jsonArrayComments = jsonArray;
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
                endTimeForUrl_photos.setText("End: "+getTime());
                if (jsonArrayPhotos != null){
                   new PerformDBOperations().execute(jsonArrayPhotos);
                }
                else {
                    Log.d("SAVING PHOTOS","PHOTOS WERE NULL");
                }
                //Toast.makeText(context,"PHOTOS DOWNLOADED",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(Throwable e) {
                Log.e("ERROR",e.getMessage());
            }

            @Override
            public void onNext(JSONArray jsonArray) {
                if (jsonArray != null){
                    jsonArrayPhotos = jsonArray;
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
                endTimeForUrl_todos.setText("End: "+getTime());
                if (jsonArrayTodos != null){
                    // start saving in the database
                    new PerformDBOperations().execute(jsonArrayTodos);
                }
                else {
                    Log.d("SAVING TODOS","TODOS WERE NULL");
                }
               // Toast.makeText(context,"TODOS DOWNLOADED",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(Throwable e) {
                Log.e("ERROR",e.getMessage());
            }

            @Override
            public void onNext(JSONArray jsonArray) {
                if (jsonArray != null){
                    jsonArrayTodos = jsonArray;
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
                endTimeForUrl_posts.setText("End: "+getTime());
                if (jsonArrayPosts != null){
                    new PerformDBOperations().execute(jsonArrayPosts);
                }
                else {
                    Log.d("SAVING POSTS","POSTS WERE NULL");
                }
               // Toast.makeText(context,"POSTS DOWNLOADED",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(Throwable e) {
                Log.e("ERROR",e.getMessage());
            }

            @Override
            public void onNext(JSONArray jsonArray) {
                if (jsonArray != null){
                    Log.d("OBSERVER",jsonArray.length()+" TODOS");
                    jsonArrayPosts = jsonArray;
                   // Toast.makeText(context,"json not null",Toast.LENGTH_SHORT).show();
                }
            }
        };
    }


    // function to return the current timestamp in String format
    public String getTime(){
        Long currentStartTimestamp = System.currentTimeMillis()/1000;
        String ts = currentStartTimestamp.toString();
        return ts;
    }


    // A blocking function that will fetch data from the given URL using OkHTTP, (Synchronous call)
    // Volley is more suited for Asynchronous call
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


    // handle the clicks on individual buttons
    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id){
            case R.id.button_url_comments:
                subscribeTocomments();
                 break;
            case R.id.button_url_photos:
                subscribToPhotos();
                break;
            case R.id.button_url_todos:
                subscribeToTodos();
                break;
            case R.id.button_url_posts:

                // attach a observer and subscribe it -  this will begin downloading the data
                /*fetchPosts.subscribeOn(Schedulers.newThread())
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
                        });*/
                subscribeToPosts();
                break;
            case R.id.button_current_timestamp:
                Toast.makeText(context,"Current Timestamp is "+getTime(),Toast.LENGTH_LONG).show();
                break;
        }
    }

    class PerformDBOperations extends AsyncTask<JSONArray, Void, Void>{

        JSONArray jsonArray;
        String startTime;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            startTime = getTime();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (jsonArray.equals(jsonArrayComments)){
                endSaveForUrl_comments.setText("End Save: "+getTime());
                startSaveForUrl_comments.setText("Start Save: "+startTime);
            }
            else if (jsonArray.equals(jsonArrayPhotos)){
                endSaveForUrl_photos.setText("End Save: "+getTime());
                startSaveForUrl_photos.setText("Start Save: "+startTime);
            }
            else if (jsonArray.equals(jsonArrayTodos)){
                endSaveForUrl_todos.setText("End Save: "+getTime());
                startSaveForUrl_todos.setText("Start Save: "+startTime);
            }
            else if (jsonArray.equals(jsonArrayPosts)){
                endSaveForUrl_posts.setText("End Save: "+getTime());
                startSaveForUrl_posts.setText("Start Save: "+startTime);
            }

        }

        @Override
        protected Void doInBackground(JSONArray... jsonArrays) {
            jsonArray = jsonArrays[0];
            if (jsonArray.equals(jsonArrayComments)) {
                Log.d("SAVING COMMENTS", "COMMENTS WERE NOT NULL");
                ActiveAndroid.beginTransaction();
                //startSaveForUrl_comments.setText("Start Save: "+getTime());
                JSONObject comment;
                try {
                    for (int i = 0; i < jsonArray.length(); i++) {
                        comment = (JSONObject) jsonArray.get(i);
                        Comments comments = new Comments(comment.getInt("postId"), comment.getString("name"), comment.getString("email"), comment.getString("body"));
                        comments.save();
                    }
                    ActiveAndroid.setTransactionSuccessful();
                } catch (JSONException e) {
                    e.printStackTrace();
                } finally {
                    ActiveAndroid.endTransaction();
                    //  endSaveForUrl_comments.setText("End Save: "+getTime());
                    Comments todos = new Select().from(Comments.class).where("postId = ?",10).executeSingle();
                    Log.d("Database Check",todos.body+" "+todos.name+" "+todos.email+" "+todos.postId);
                }
            }
            else if (jsonArray.equals(jsonArrayPhotos)){
                    Log.d("SAVING PHOTOS","PHOTOS WERE NOT NULL");
                    ActiveAndroid.beginTransaction();
                    try{
                      //  startSaveForUrl_photos.setText("Start Save: "+getTime());
                        JSONObject photo;
                        for (int i = 0; i< jsonArrayPhotos.length();i++){
                            photo = (JSONObject) jsonArrayPhotos.get(i);
                            Photos photos = new Photos(photo.getInt("albumId"),photo.getString("title"),photo.getString("url"),photo.getString("thumbnailUrl"));
                            photos.save();
                        }
                        ActiveAndroid.setTransactionSuccessful();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    finally {
                        ActiveAndroid.endTransaction();
                        //endSaveForUrl_photos.setText("End Save: "+getTime());
                    }
            }
            else if (jsonArray.equals(jsonArrayTodos)){
                   Log.d("SAVING TODOS","TODOS WERE NOT NULL");
                    try{
                        ActiveAndroid.beginTransaction();
                        JSONObject todo;
                        for (int i = 0;i <jsonArrayTodos.length();i++){
                            todo = (JSONObject) jsonArrayTodos.get(i);
                            Todos todos = new Todos(todo.getInt("userId"),todo.getString("title"),todo.getString("completed").equals("true")?true:false);
                            todos.save();
                        }
                        ActiveAndroid.setTransactionSuccessful();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    finally {
                        ActiveAndroid.endTransaction();
                        Todos todos = new Select().from(Todos.class).where("userId = ?",10).executeSingle();
                        Log.d("Database Check",todos.toString());
                    }
            }
            else if (jsonArray.equals(jsonArrayPosts)){
                   Log.d("SAVING POSTS","POSTS WERE NOT NULL");
                    try{
                        ActiveAndroid.beginTransaction();
                        JSONObject post;
                        for (int i = 0 ;i< jsonArrayPosts.length();i++){
                            post = (JSONObject) jsonArrayPosts.get(i);
                            Posts posts = new Posts(post.getInt("userId"),post.getString("title"),post.getString("body"));
                            posts.save();
                        }
                        ActiveAndroid.setTransactionSuccessful();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    finally {
                        ActiveAndroid.endTransaction();
                    }
            }
            return null;
        }
    }
}
