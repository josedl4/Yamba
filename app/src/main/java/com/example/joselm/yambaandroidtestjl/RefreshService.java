// Autores:
// Martin Martin, Jose Luis
// Martinez Arias, Miguel
package com.example.joselm.yambaandroidtestjl;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.util.Log;

import java.util.List;

import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

/**
 * Servicio que actualiza el timeline.
 */
public class RefreshService extends IntentService {

    static final String TAG = RefreshService.class.getSimpleName();

    static final int DELAY = 30000;
    private boolean runFlag = false;

    public RefreshService() {
        super(TAG);
    }

    DbHelper dbHelper;
    SQLiteDatabase db;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreated");

        dbHelper = new DbHelper(this);
    }

    @Override
    public void onHandleIntent(Intent intent) {
        Log.d(TAG, "onStarted");

        this.runFlag = true;

        // Recogemos las preferencias compartidas para la autenticacion
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String accesstoken = prefs.getString("accesstoken", "");
        String accesstokensecret = prefs.getString("accesstokensecret", "");

        // Repetimos la actualizacion hasta que no se pare el servicio
        List<Status> timeline;
        Uri uri;
        while (runFlag) {
            Log.d(TAG, "Updater running");

            try {
                // Autenticacion
                ConfigurationBuilder builder = new ConfigurationBuilder();
                builder.setOAuthConsumerKey(getString(R.string.costumer_key))
                        .setOAuthConsumerSecret(getString(R.string.costumer_secret))
                        .setOAuthAccessToken(accesstoken)
                        .setOAuthAccessTokenSecret(accesstokensecret);
                TwitterFactory factory = new TwitterFactory(builder.build());
                Twitter twitter = factory.getInstance();

                try {
                    timeline = twitter.getHomeTimeline();

                    // Se recorre los status del timeline
                    ContentValues values = new ContentValues();
                    for (Status status : timeline) {
                        // Imprimimos las actualizaciones en el log
                        Log.d(TAG, String.format("%s: %s", status.getUser().getName(),
                                status.getText()));

                        // Insertar en la base de datos
                        values.clear();
                        values.put(StatusContract.Column.ID, status.getId());
                        values.put(StatusContract.Column.USER, status.getUser().getName());
                        values.put(StatusContract.Column.MESSAGE, status.getText());
                        values.put(StatusContract.Column.CREATED_AT, status.getCreatedAt().getTime());

                        uri = getContentResolver().insert(StatusContract.CONTENT_URI, values);
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

        Log.d(TAG, "onDestroyed");
    }
}
