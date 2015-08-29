package com.treehouse.ribbit;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseInstallation;
import com.parse.ParseUser;
import com.treehouse.ribbit.utils.ParseConstants;

/**
 * Created by lbalmaceda on 8/22/15.
 */
public class RibbitApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Parse.enableLocalDatastore(this);
        //FIXME: Change AppId and ClientKey to your Parse App.
        Parse.initialize(this, "y9nehBsAoXjElOkJNnPVljHCxr6cTk7s07sV2uTG", "ppxxZ8U80EpG9YWnl4s7qHBm6KKVk3VLxJDfkTUE");
        ParseInstallation.getCurrentInstallation().saveInBackground();
    }


    public static void updateParseInstallation(ParseUser user) {
        ParseInstallation installation = ParseInstallation.getCurrentInstallation();
        installation.put(ParseConstants.KEY_USER_ID, user.getObjectId());
        installation.saveInBackground();
    }
}
