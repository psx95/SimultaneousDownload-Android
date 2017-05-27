package com.psx.mysolution.helper;

import android.util.Log;
import android.widget.TextView;

import com.psx.mysolution.MainActivity;

import org.json.JSONArray;

import rx.Observer;

/**
 * Created by Pranav on 28-05-2017.
 */

public class ObserverFactory<T> {

    final Class<T> typeParameterClass;
    MainActivity mainActivity;
    JSONArray array = null;

    public ObserverFactory(Class<T> typeParameterClass){
        this.mainActivity = new MainActivity();
        this.typeParameterClass = typeParameterClass;
    }

    public Observer<T> getObserver(final TextView textView_for_end_time_display, final JSONArray jsonArray){
        Observer<T> observer = new Observer<T>() {
            @Override
            public void onCompleted() {
                Log.d("OBSERVER_FACTORY","onCompleted");
                textView_for_end_time_display.setText("End: "+mainActivity.getTime());
                if (array!=null){
                    Log.d("OBSERVER_FACTORY",array.toString());
                    mainActivity.getDBOperations().execute(array);
                }
            }

            @Override
            public void onError(Throwable e) {
                Log.e("OBSERVERFACTORY","Some Error Occoured "+e.getMessage());
            }

            @Override
            public void onNext(T t) {
                Log.d("OBSERVER_FACTORY","onNext");
                if (t.getClass().equals(JSONArray.class)){
                    array = (JSONArray) t;
                }
            }
        };
        return observer;
    }

}
