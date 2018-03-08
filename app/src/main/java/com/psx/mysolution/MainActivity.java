package com.psx.mysolution;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
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
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    String TAG = "MainActivity";

    private com.squareup.okhttp.Response response;
    private Context context;
    public TextView startTimeForUrl_comments, startTimeForUrl_photos, startTimeForUrl_todos, startTimeForUrl_posts;
    public TextView endTimeForUrl_comments, endTimeForUrl_photos,endTimeForUrl_todos, endTimeForUrl_posts;
    public TextView startSaveForUrl_comments, startSaveForUrl_photos, startSaveForUrl_todos, startSaveForUrl_posts;
    public TextView endSaveForUrl_comments, endSaveForUrl_photos, endSaveForUrl_todos, endSaveForUrl_posts;
    public TextView currentTimestamp;
    public String urls [] = {"https://jsonplaceholder.typicode.com/comments","https://jsonplaceholder.typicode.com/photos","https://jsonplaceholder.typicode.com/todos","https://jsonplaceholder.typicode.com/posts"};
    public JSONArray jsonArrayComments, jsonArrayPhotos, jsonArrayTodos, jsonArrayPosts;
    public Button button_url_comments, button_url_photos, button_url_todos, button_url_posts, button_currentTimestamp;
    public Observable<JSONArray> fetchComments, fetchPhotos, fetchTodos, fetchPosts;
    public HashMap<String, List<TextView>> observableHashMap = new HashMap<>();
    ObserverFactory<JSONArray> observerFactory;
    ObserverableFactory<JSONArray> observervableFactory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Log.e(TAG, "onCreate: ");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        observerFactory = new ObserverFactory<>(JSONArray.class);
        observervableFactory= new ObserverableFactory<>(JSONArray.class);
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

        currentTimestamp = (TextView) findViewById(R.id.tv_current_timestamp);
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

        observableHashMap.put(urls[0], Arrays.asList(startTimeForUrl_comments, endTimeForUrl_comments, startSaveForUrl_comments, endSaveForUrl_comments));
        observableHashMap.put(urls[1], Arrays.asList(startTimeForUrl_photos, endTimeForUrl_photos, startSaveForUrl_photos, endSaveForUrl_photos));
        observableHashMap.put(urls[2], Arrays.asList(startTimeForUrl_todos, endTimeForUrl_todos, startSaveForUrl_todos, endSaveForUrl_todos));
        observableHashMap.put(urls[3], Arrays.asList(startTimeForUrl_posts, endTimeForUrl_posts, startSaveForUrl_posts, endSaveForUrl_posts));

    }

    public Observable fetchData (String url){
        OkHttpClient client = new OkHttpClient();
        com.squareup.okhttp.Request request = new com.squareup.okhttp.Request.Builder().url(url).build();
        Observable<Object> observable = Observable
                .create((subscriber) -> {
                    try{
                        Response response = client.newCall(request).execute();
                        try{
                            JSONArray tmp_array = new JSONArray(response.body().string());
                            Log.e(TAG, "fetchData: URL=>" + url + " data: " + tmp_array.toString().length());
                            subscriber.onNext(tmp_array);
                            subscriber.onCompleted();
                        } catch (JSONException e) {
                            subscriber.onError(e);
                            e.printStackTrace();}
                    }catch (IOException e) {
                        subscriber.onError(e);
                        e.printStackTrace();
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
        return observable;
    }

    @Override
    protected void onResume() {
        super.onResume();

        Observable
                .defer(() -> Observable.just(null).delay(3, TimeUnit.SECONDS))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(ignore ->
                        Observable
                                .from(urls)
                                .flatMap((url) -> subscribeTo(url, observableHashMap.get(url)))
                                .subscribe()
                )
                .subscribe();
    }

    public Observable saveData(Object ...objects){
        Observable<Object> observable = Observable
                .create((subscriber) -> {
                    try{
                        int pos = (Integer) objects[1];
                        String obj = saveToDB(objects);
                        Log.e(TAG, "saveData: " + pos + " " + obj );
                        subscriber.onNext(obj);
                        subscriber.onCompleted();
                    }catch (Exception e){
                        subscriber.onError(e);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
        return observable;
    }

    public Observable<JSONArray> subscribeTo (String url, List<TextView> textViews){
        int indexOfUrl = Arrays.asList(urls).indexOf(url);
        Log.e("Inside Subscribe", "for url: " + url);


        Observable observable = Observable.just(url)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext((s) -> textViews.get(0).setText("Start: "+getTime()))
                .flatMap(s -> fetchData(url))
                .doOnNext((s) -> textViews.get(1).setText("End: "+ getTime()))
                .doOnNext((s) -> textViews.get(2).setText("Start Save: "+ getTime()))
                .flatMap(data -> saveData(data, indexOfUrl))
                .doOnNext((s) -> textViews.get(3).setText("End Save: "+ s))
                .doOnNext((s) -> currentTimestamp.setText(getTime()));

        return observable;
    }

    // function to return the current timestamp in String format
    public String getTime(){
        Long currentStartTimestamp = System.currentTimeMillis();
        String ts = currentStartTimestamp.toString();
        return ts;
    }

    // handle the clicks on individual buttons
    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id){
            case R.id.button_url_comments:
                Observable.just(null)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .flatMap((s) -> subscribeTo(urls[0], observableHashMap.get(urls[0])))
                        .subscribe();
                break;
            case R.id.button_url_photos:
                Observable.just(null)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .flatMap((s) -> subscribeTo(urls[1], observableHashMap.get(urls[1])))
                        .subscribe();
                break;
            case R.id.button_url_todos:
                Observable.just(null)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .flatMap((s) -> subscribeTo(urls[2], observableHashMap.get(urls[2])))
                        .subscribe();
                break;
            case R.id.button_url_posts:
                Observable.just(null)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .flatMap((s) -> subscribeTo(urls[3], observableHashMap.get(urls[3])))
                        .subscribe();
                break;
            case R.id.button_current_timestamp:
                currentTimestamp.setText(getTime());
                break;
        }
    }

    protected String saveToDB(Object... objects) {
        JSONArray jsonArray = (JSONArray) objects[0];
        int pos = (Integer) objects[1];
        String endSaveTime = "";
        Log.e(TAG, "saveData: jsonArray " + jsonArray );
        Log.e(TAG, "saveData: pos " + pos );

        switch (pos){
            case 0:
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
                    endSaveTime = getTime();
                    // Test cases
                    //  endSaveForUrl_comments.setText("End Save: "+getTime());
                    Comments todos = new Select().from(Comments.class).where("postId = ?",10).executeSingle();
                    Log.d("Database Check",todos.body+" "+todos.name+" "+todos.email+" "+todos.postId);
                }
                break;
            case 1:
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
                    endSaveTime = getTime();
                    //endSaveForUrl_photos.setText("End Save: "+getTime());
                }
                break;
            case 2:
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
                    endSaveTime = getTime();

                    Todos todos = new Select().from(Todos.class).where("userId = ?",10).executeSingle();
                    Log.d("Database Check",todos.toString());
                }
                break;
            case 3:
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
                    endSaveTime = getTime();
                }
                break;
            default:
                Log.d("SAVE_DB","Invalid input");
        }
        Log.e(TAG, "saveToDB: saved" + pos + " " + endSaveTime );
        return endSaveTime;
    }
}
