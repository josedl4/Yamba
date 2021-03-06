// Autores:
// Martin Martin, Jose Luis
// Martinez Arias, Miguel
package com.example.joselm.yambaandroidtestjl;

import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

import static twitter4j.HttpResponseCode.UNAUTHORIZED;

/**
 * Fragment para la actualizacion de estado.
 */
public class StatusFragment extends Fragment implements View.OnClickListener, TextWatcher {

    private static final String TAG = StatusFragment.class.getSimpleName();
    private EditText editStatus;
    private Button buttonTweet;
    private Twitter twitter;
    private TextView textCount;
    private ProgressBar progressBar;
    private SharedPreferences prefs;

    public View onCreateView(LayoutInflater inflater, ViewGroup
            container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_status, container, false);

        prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        // Enlazar views
        editStatus = view.findViewById(R.id.editStatus);
        buttonTweet = view.findViewById(R.id.buttonTweet);
        buttonTweet.setOnClickListener(this);
        progressBar = view.findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.GONE);

        textCount = view.findViewById(R.id.textCount);
        textCount.setText(Integer.toString(140));
        textCount.setTextColor(Color.GREEN);
        editStatus.addTextChangedListener(this);

        return view;
    }

    @Override
    public void onClick(View view) {
        String status = editStatus.getText().toString();
        Log.d(TAG, "onClicked");
        if (textCount.getText().charAt(0) != '-') {
            new PostTask().execute(status);
        }
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void afterTextChanged(Editable statusText) {
        int count = 140 - statusText.length();
        textCount.setText(Integer.toString(count));
        textCount.setTextColor(Color.GREEN);
        if (count < 10)
            textCount.setTextColor(Color.YELLOW);
        if (count < 0)
            textCount.setTextColor(Color.RED);
    }


    // Publicar en Twitter de manera asíncrona
    private final class PostTask extends AsyncTask<String, Void, OperationStatus> {
        // Llamada al empezar
        @Override
        protected OperationStatus doInBackground(String... params) {

            String accesstoken = prefs.getString("accesstoken", "");
            String accesstokensecret = prefs.getString("accesstokensecret", "");
            // Comprobar si el nombre de usuario o el password están vacíos.
            // Si lo están, indicarlo mediante un Toast y redirigir al usuario a Settings
            if (TextUtils.isEmpty(accesstoken) || TextUtils.isEmpty(accesstokensecret)) {
                getActivity().startActivity(new Intent(getActivity(), SettingsActivity.class));
                return OperationStatus.TOKEN_FAIL;
            }
            ConfigurationBuilder builder = new ConfigurationBuilder();
            builder.setOAuthConsumerKey(getString(R.string.costumer_key))
                    .setOAuthConsumerSecret(getString(R.string.costumer_secret))
                    .setOAuthAccessToken(accesstoken)
                    .setOAuthAccessTokenSecret(accesstokensecret);
            TwitterFactory factory = new TwitterFactory(builder.build());
            twitter = factory.getInstance();

            try {
                twitter.updateStatus(params[0]);
                return OperationStatus.SUCCESS;
            } catch (TwitterException e) {
                Log.e(TAG, "Error in the process");
                e.printStackTrace();
                if (e.isCausedByNetworkIssue())
                    return OperationStatus.NETWORK_FAIL;
                else if (e.getStatusCode() == UNAUTHORIZED)
                    return OperationStatus.TOKEN_FAIL;
                else return OperationStatus.UNKNOWN;
            }
        }

        // Llamada cuando la actividad en background ha terminado
        @Override
        protected void onPostExecute(OperationStatus result) {
            super.onPostExecute(result);

            progressBar.setVisibility(View.GONE);

            if (result == OperationStatus.SUCCESS) editStatus.setText("");
            Snackbar.make(StatusFragment.this.getView(), result.getTexto(), Snackbar.LENGTH_LONG).show();
        }


        protected void onProgressUpdate(Integer... progress) {
            progressBar.setProgress(progress[0]);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
        }
    }
}

