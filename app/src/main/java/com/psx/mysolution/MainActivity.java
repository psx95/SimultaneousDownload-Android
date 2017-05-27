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
import com.psx.mysolution.helper.ObserverFactory;
import com.psx.mysolution.helper.ObserverableFactory;
import com.squareup.okhttp.OkHttpClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import rx.Observable;
import rx.Observer;
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
    public ObserverFactory<JSONArray> observerFactory;

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

        observerFactory = new ObserverFactory<>(JSONArray.class);
        ObserverableFactory<JSONArray> observervableFactory = new ObserverableFactory<>(JSONArray.class);
        fetchComments = observervableFactory.getObservervable(JSONArray.class,0);
        fetchPhotos = observervableFactory.getObservervable(JSONArray.class, 1);
        fetchTodos = observervableFactory.getObservervable(JSONArray.class, 2);
        fetchPosts = observervableFactory.getObservervable(JSONArray.class, 3);
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
                .subscribe(observerFactory.getObserver(endTimeForUrl_comments,1,startSaveForUrl_comments,endSaveForUrl_comments));
    }

    public void subscribToPhotos (){
        startTimeForUrl_photos.setText("Start: "+getTime());
        fetchPhotos.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observerFactory.getObserver(endTimeForUrl_photos,2,startSaveForUrl_photos,endSaveForUrl_photos));
    }

    public void subscribeToTodos (){
        startTimeForUrl_todos.setText("Start: "+getTime());
        fetchTodos.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observerFactory.getObserver(endTimeForUrl_todos,3,startSaveForUrl_todos,endSaveForUrl_todos));
    }

    public void subscribeToPosts (){
        startTimeForUrl_posts.setText("Start: "+getTime());
        fetchPosts.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observerFactory.getObserver(endTimeForUrl_posts,4,startSaveForUrl_posts,endSaveForUrl_posts));
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
                subscribeToPosts();
                break;
            case R.id.button_current_timestamp:
                Toast.makeText(context,"Current Timestamp is "+getTime(),Toast.LENGTH_LONG).show();
                break;
        }
    }

    public PerformDBOperations getDBOperations(){
        return new PerformDBOperations();
    }

    public class PerformDBOperations extends AsyncTask<Object, Void, Object[]>{

        JSONArray jsonArray;
        String startTime;
        int pos;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            startTime = getTime();
        }

        @Override
        protected void onPostExecute(Object[] objects) {
            super.onPostExecute(objects);
            ((TextView)(objects[1])).setText("End Save: "+getTime());
            ((TextView)(objects[0])).setText("Start Save: "+startTime);
        }

        @Override
        protected Object[] doInBackground(Object... objects) {
            jsonArray = (JSONArray) objects[0];
            pos = (Integer) objects[1];
            switch (pos){
                case 1:
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
                    break;
                case 2:
                    Log.d("SAVING PHOTOS","PHOTOS WERE NOT NULL");
                    ActiveAndroid.beginTransaction();
                    try{
                        //  startSaveForUrl_photos.setText("Start Save: "+getTime());
                        JSONObject photo;
                        for (int i = 0; i< jsonArray.length();i++){
                            photo = (JSONObject) jsonArray.get(i);
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
                    break;
                case 3:
                    Log.d("SAVING TODOS","TODOS WERE NOT NULL");
                    try{
                        ActiveAndroid.beginTransaction();
                        JSONObject todo;
                        for (int i = 0;i <jsonArray.length();i++){
                            todo = (JSONObject) jsonArray.get(i);
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
                    break;
                case 4:
                    Log.d("SAVING POSTS","POSTS WERE NOT NULL");
                    try{
                        ActiveAndroid.beginTransaction();
                        JSONObject post;
                        for (int i = 0 ;i< jsonArray.length();i++){
                            post = (JSONObject) jsonArray.get(i);
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
                    break;
                default:
                    Log.d("SAVE_DB","Invalid input");
            }
            return new Object[]{objects[2],objects[3]};
        }
    }
}
