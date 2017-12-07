package com.example.joselm.yambaandroidtestjl;

import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

import java.util.List;

import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

public class RefreshService extends IntentService {
    static final String TAG = "RefreshService";

    static final int DELAY = 30000;
    private boolean runFlag = false;

    public RefreshService() {
        super(TAG);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreated");
    }

    @Override
    public void onHandleIntent(Intent intent) {
        Log.d(TAG, "onStarted");

        this.runFlag = true;

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String accesstoken = prefs.getString("accesstoken", "");
        String accesstokensecret = prefs.getString("accesstokensecret", "");

        while(runFlag) {
            Log.d(TAG, "Updater running");

            try {
                ConfigurationBuilder builder = new ConfigurationBuilder();
                builder.setOAuthConsumerKey("")
                        .setOAuthConsumerSecret("")
                        .setOAuthAccessToken(accesstoken)
                        .setOAuthAccessTokenSecret(accesstokensecret);
                TwitterFactory factory = new TwitterFactory(builder.build());
                Twitter twitter = factory.getInstance();

                try {
                    List<Status> timeline = twitter.getHomeTimeline();

                    for(Status status : timeline) {
                        Log.d(TAG, String.format("%s: %s", status.getUser().getName(),
                                status.getText()));
                    }
                } catch (TwitterException te) {
                    Log.e(TAG, "Failed to fetch the timeline", te);
                }

                Log.d(TAG, "Updater ran");
                Thread.sleep(DELAY);
            } catch (InterruptedException e) {
                runFlag = false;
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        this.runFlag = false;

        Log.d(TAG,"onDestroyed");
    }
}
