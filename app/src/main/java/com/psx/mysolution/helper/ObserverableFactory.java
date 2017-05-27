package com.psx.mysolution.helper;

import com.psx.mysolution.MainActivity;
import com.squareup.okhttp.OkHttpClient;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.lang.reflect.Type;

import rx.Observable;
import rx.Subscriber;

/**
 * Created by Pranav on 27-05-2017.
 */

public class ObserverableFactory<T> {

    private com.squareup.okhttp.Response response;
    final Class<T> typeParameterClass;
    MainActivity mainActivity;

    public ObserverableFactory(Class<T> typeParameterClass){
        this.mainActivity = new MainActivity();
        this.typeParameterClass = typeParameterClass;
    }

    public Observable<T> getObservervable (Type t, final int url_index){
        Observable<T> observervable = Observable.create(new Observable.OnSubscribe<T>() {
            @Override
            public void call(Subscriber<? super T> subscriber) {
                try {
                    T data = fetcchDataOkHttp(mainActivity.urls[url_index], typeParameterClass);
                    subscriber.onNext(data);
                    subscriber.onCompleted();
                }
                catch (Exception e){
                    subscriber.onError(e);
                }
            }
        });
        return observervable;
    }


    public  <T> T fetcchDataOkHttp (String url, Class<T> cls){
        try{
            OkHttpClient client = new OkHttpClient();
            com.squareup.okhttp.Request request = new com.squareup.okhttp.Request.Builder().url(url).build();
            response = client.newCall(request).execute();
            JSONArray tmp_array = new JSONArray(response.body().string());
            Object tmp = (Object) tmp_array;
            return cls.cast(tmp);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
