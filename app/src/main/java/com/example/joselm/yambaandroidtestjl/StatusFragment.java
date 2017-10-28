package com.example.joselm.yambaandroidtestjl;

import android.app.Fragment;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Button;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

import static twitter4j.HttpResponseCode.UNAUTHORIZED;


public class StatusFragment extends Fragment implements View.OnClickListener, TextWatcher{

    private static final String TAG = "StatusFragment";
    EditText editStatus;
    Button buttonTweet;
    Twitter twitter;
    TextView textCount;
    ProgressBar progressBar;

    public View onCreateView(LayoutInflater inflater, ViewGroup
            container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_status, container, false);

        // Enlazar views
        editStatus = view.findViewById(R.id.editStatus);
        buttonTweet = view.findViewById(R.id.buttonTweet);
        buttonTweet.setOnClickListener(this);
        progressBar = view.findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.GONE);

        ConfigurationBuilder builder = new ConfigurationBuilder();
        builder.setOAuthConsumerKey("4v0x4Fqtkw5IPUlZdzcOTFhUN")
                .setOAuthConsumerSecret("q82M0t96q5aQF9teaBHm7cnsTOzOFDQwIHgoRlqKK55hHdqkTu")
                .setOAuthAccessToken("918582870217838592-Bhf1Nk7Hpo9lV6qWPWYoh1smjhUfYRs")
                .setOAuthAccessTokenSecret("BCDIEbgTpYNgtHzyWJrU7UY7AcP4iJrpm8Qt8z1dL3TsM");
        TwitterFactory factory = new TwitterFactory(builder.build());
        twitter = factory.getInstance();

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

        new PostTask().execute(status);
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
            try {
                twitter.updateStatus(params[0]);
                return OperationStatus.SUCCESS;
            } catch (TwitterException e) {
                Log.e(TAG, "Error in the process");
                e.printStackTrace();

                if(e.isCausedByNetworkIssue())
                    return OperationStatus.NETWORK_FAIL;
                else if(e.getStatusCode() == UNAUTHORIZED)
                    return OperationStatus.TOKEN_FAIL;
                else return OperationStatus.UNKNOWN;
            }
        }

        // Llamada cuando la actividad en background ha terminado
        @Override
        protected void onPostExecute(OperationStatus result) {
            super.onPostExecute(result);

            progressBar.setVisibility(View.GONE);

            switch (result){
                case SUCCESS:
                    Snackbar.make(StatusFragment.this.getView(),
                            R.string.operation_success,
                            Snackbar.LENGTH_LONG)
                            .show();
                    break;
                case NETWORK_FAIL:
                    Snackbar.make(StatusFragment.this.getView(),
                            R.string.operation_network_fail,
                            Snackbar.LENGTH_LONG)
                            .show();
                    break;
                case TOKEN_FAIL:
                    Snackbar.make(StatusFragment.this.getView(),
                            R.string.operation_token_fail,
                            Snackbar.LENGTH_LONG)
                            .show();
                    break;
                default:
                    Snackbar.make(StatusFragment.this.getView(),
                            R.string.operation_unknown,
                            Snackbar.LENGTH_LONG)
                            .show();
            }


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

