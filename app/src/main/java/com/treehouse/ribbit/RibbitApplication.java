package com.treehouse.ribbit;

import android.app.Application;

import com.parse.Parse;

/**
 * Created by lbalmaceda on 8/22/15.
 */
public class RibbitApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Parse.enableLocalDatastore(this);
        Parse.initialize(this, "yqnehBsAoXjElOkJNnPVljHCxr6cTk7s07sV2uTG", "pppxZ8U80EpG9YWnl4s7qHBm6KKVk3VLxJDfkTUE");
    }
}
