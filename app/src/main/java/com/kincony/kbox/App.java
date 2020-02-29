package com.kincony.kbox;

import android.app.Application;

public class App extends Application {
    public static Application application;

    @Override
    public void onCreate() {
        super.onCreate();
        this.application = this;
    }
}
