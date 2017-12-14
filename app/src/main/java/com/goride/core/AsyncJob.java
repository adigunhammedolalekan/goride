package com.goride.core;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;

/**
 * Created by root on 11/14/17.
 */

/*
* Just like AsyncTask
* */

public abstract class AsyncJob<T> implements Runnable {


    public interface IAsyncJobCallback<T> {

        void onFinish(T data);
        void error(Throwable throwable);

    }

    private IAsyncJobCallback callback;
    private Handler handler = new Handler(Looper.getMainLooper());


    public AsyncJob(IAsyncJobCallback callback) {
        this.callback = callback;
    }

    @Override
    public void run() {
        try {
            final T result = doIt();

            handler.post(new Runnable() {
                @Override
                public void run() {
                    if(callback != null)
                        callback.onFinish(result);
                }
            });
        }catch (final Exception e) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    if(callback != null)
                        callback.error(e);
                }
            });
        }
    }
    abstract T doIt();
}
