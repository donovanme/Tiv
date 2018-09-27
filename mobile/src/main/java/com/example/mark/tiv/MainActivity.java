package com.example.mark.tiv;

import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import android.widget.TextView;
import android.widget.Toast;
import android.os.Handler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class MainActivity extends AppCompatActivity {


    TextView timerTextView;
    long startTime = 0;


      //runs without a timer by reposting this handler at the end of the runnable
    Handler timerHandler = new Handler();
    Runnable timerRunnable = new Runnable() {

        @Override
        public void run() {


            new GetQuestion().execute();

            timerHandler.postDelayed(this, 15000); // How long in between questions. (15 seconds)
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Load a Question
        timerHandler.postDelayed(timerRunnable, 0);


    }

     private String TAG = MainActivity.class.getSimpleName();

/*
    public class updateWiki extends AsyncTask<String, Void, Void> {


        public void updateWiki (String searchTerm) {
            HttpHandler sh = new HttpHandler();
            // Making a request to url and getting response
            String url = "https://en.wikipedia.org/w/api.php?action=opensearch&limit=1&format=json&search=" + searchTerm;
            String jsonStr = sh.makeServiceCall(url);

            //String a = sUrl[0]
            Log.e(TAG, "Response from wiki: " + jsonStr);
            Log.e(TAG, "URL:" + url);

            if (jsonStr != null) {
                try {

                    JSONArray jsonObj = new JSONArray(jsonStr);

                    JSONArray c = jsonObj.getJSONArray(2);

                    final String wikiText = c.getString(0);

                    // runOnUiThread(new Runnable() {
                    //    @Override
                    //    public void run() {
                    final TextView QuestionTextView = (TextView) findViewById(com.example.mark.tiv.R.id.wikiText);
                    QuestionTextView.setText(searchTerm);

                    //   }
                    // });
                } catch (final JSONException e) {
                    Log.e(TAG, "Error from Wiki parsing JSON");
                }

            } else {
                Log.e(TAG, "Error from Wiki");
            }

        }

        @Override
        protected Void doInBackground(String ... arg0) {

           updateWiki ("abc");

            return null;
        }

    }
    */


    private class GetQuestion extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Toast.makeText(MainActivity.this, "Downloading next question...", Toast.LENGTH_LONG).show();
        }


        protected void displayDownloaderror() {
            Log.e(TAG, "Couldn't get json from server.");
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(),
                            "Couldn't get json from server. Check LogCat for possible errors!",
                            Toast.LENGTH_LONG).show();
                }
            });

        }

        protected void displayJSONParseError (final JSONException e) {
            Log.e(TAG, "Json parsing error: " + e.getMessage());
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(),
                            "Json parsing error: " + e.getMessage(),
                            Toast.LENGTH_LONG).show();
                }
            });

        }

        public void displayNewQuestion() {
            HttpHandler sh = new HttpHandler();
            // Making a request to url and getting response
            String url = "http://jservice.io/api/random";
            String jsonStr = sh.makeServiceCall(url);

            Log.e(TAG, "Response from url: " + jsonStr);

            if (jsonStr != null) {

                displayQuestion(jsonStr);

            } else {
                displayDownloaderror();
            }
        }




        protected void displayQuestion ( final String jsonStr ) {


            try {

                JSONArray jsonObj = new JSONArray(jsonStr);

                JSONObject c = jsonObj.getJSONObject(0);

                final String question = c.getString("question");
                final String answer = c.getString("answer");
                final JSONObject categoryJSON = c.getJSONObject("category");
                final String categoryTitle = categoryJSON.getString("title");


                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        final TextView QuestionTextView = (TextView) findViewById(com.example.mark.tiv.R.id.questionText);
                        QuestionTextView.setText(question.toString());

                        final TextView AnswerTextView = (TextView) findViewById(com.example.mark.tiv.R.id.answerText);


                        new CountDownTimer(7000, 1000) {

                            public void onTick(long millisUntilFinished) {
                                AnswerTextView.setText("Seconds remaining: " + millisUntilFinished / 1000);
                                /*Toast.makeText(getApplicationContext(),
                                        "Category " + categoryTitle,
                                        Toast.LENGTH_LONG).show(); */
                                //  int mytime = (int) millisUntilFinished / 100;
  //                              timerBar.setProgress(mytime);
                            }

                            public void onFinish() {
                                AnswerTextView.setText(answer);
                                //new updateWiki().execute(answer);
                            }

                        }.start();


                    }
                });
            } catch (final JSONException e) {
                displayJSONParseError(e);
            }

        }

        @Override
        protected Void doInBackground(Void... arg0) {


            displayNewQuestion();



           return null;
        }




        }


}
