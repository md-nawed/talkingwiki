package com.example.android.talkingwiki;

import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.net.URL;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private final static String LOG_TAG = MainActivity.class.getSimpleName();

    private EditText mSearchBoxET;
    private TextView mDisplayTV;
    private Button mPlay;
    private Button mPause;
    private TextToSpeech mTTS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSearchBoxET = (EditText) findViewById(R.id.et_search_box);
        mDisplayTV = (TextView) findViewById(R.id.display);

        mPlay = (Button) findViewById(R.id.button_play);
        mPause = (Button) findViewById(R.id.button_pause);

        mTTS = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    mTTS.setLanguage(Locale.US);
                }
            }
        });

        mPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String toSpeak = mDisplayTV.getText().toString();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    mTTS.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null, null);
                } else {
                    mTTS.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null);
                }
            }
        });

        mPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTTS.stop();
            }
        });

    }

    @Override
    protected void onPause() {
        if (mTTS != null) {
            mTTS.stop();
            mTTS.shutdown();
        }
        super.onPause();
    }

    private void makeSearchQuery() {
        String wikiQuery = mSearchBoxET.getText().toString();
        URL wikiSearchUrl = NetworkUtils.buildUrl(wikiQuery);

        new WikiQueryTask().execute(wikiSearchUrl);
    }

    public class WikiQueryTask extends AsyncTask<URL, Void, String> {

        @Override
        protected String doInBackground(URL... params) {
            URL searchUrl = params[0];
            String wikiSearchResults = null;
            try {
                wikiSearchResults = NetworkUtils.getResponseFromHttpUrl(searchUrl);


            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                return getDataFromJson(wikiSearchResults);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        public String getDataFromJson(String jsonStr) throws JSONException {
            JSONArray array = new JSONArray(jsonStr);
            String result = array.getString(2); //To get the nested jsonarray

            return result;
        }

        @Override
        protected void onPostExecute(String wikiSearchResults) {
            if (wikiSearchResults != null && !wikiSearchResults.equals("")) {
                mDisplayTV.setText(wikiSearchResults);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemClickedId = item.getItemId();
        if (itemClickedId == R.id.action_search) {
            makeSearchQuery();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}



