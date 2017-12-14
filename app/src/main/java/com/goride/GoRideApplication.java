package com.goride;

import android.app.Application;

import com.google.firebase.FirebaseApp;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by root on 11/14/17.
 */

public class GoRideApplication extends Application {

    private ExecutorService executorService = Executors.newCachedThreadPool();
    private static GoRideApplication application;

    @Override
    public void onCreate() {
        super.onCreate();

        FirebaseApp.initializeApp(this);
        application = this;
    }

    public ExecutorService getExecutorService() {
        return executorService;
    }

    public static GoRideApplication getApplication() {
        return application;
    }
}
