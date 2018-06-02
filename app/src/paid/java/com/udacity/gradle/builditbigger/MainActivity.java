package com.udacity.gradle.builditbigger;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;
import com.udacity.gradle.builditbigger.backend.myApi.MyApi;

import java.io.IOException;

import gradle.udacity.com.ajokedisplay.JokeDisplayActivity;
import gradle.udacity.com.ajokesource.JokeSource;


public class MainActivity extends AppCompatActivity {

    private ProgressBar spinner;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        spinner = findViewById(R.id.progressBar1);
        spinner.setVisibility(View.GONE);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    // Make the button kick off a task to retrieve a joke,
    // then launch the activity from your Android Library to display it.
    public void tellJokeFromTask(View view)
    {
        spinner.setVisibility(View.VISIBLE);
        new EndpointsAsyncTask().execute(MainActivity.this);
    }


    public void tellJoke(View view)
    {
         JokeSource jokeSource = new JokeSource();
         String joke = jokeSource.getJoke();
         Toast.makeText(this, joke, Toast.LENGTH_SHORT).show();
    }


    public void launchJokeDisplayActivity(View view)
    {
        Intent intent = new Intent(this, JokeDisplayActivity.class);
        JokeSource jokeSource = new JokeSource();
        String joke = jokeSource.getJoke();
        intent.putExtra(JokeDisplayActivity.JOKE_KEY, joke);
        startActivity(intent);
    }


    class EndpointsAsyncTask extends AsyncTask<Context, Void, String>
    {
        private Context context;
        private MyApi myApiService = null;

        @Override
        protected String doInBackground(Context... params)
        {
            if(myApiService == null)
            {  // Only do this once
                MyApi.Builder builder = new MyApi.Builder(AndroidHttp.newCompatibleTransport(),
                        new AndroidJsonFactory(), null)
                        // options for running against local devappserver
                        // - 10.0.2.2 is localhost's IP address in Android emulator
                        // - turn off compression when running against local devappserver
                        .setRootUrl("http://10.0.2.2:8080/_ah/api/")
                        .setGoogleClientRequestInitializer(new GoogleClientRequestInitializer()
                        {
                            @Override
                            public void initialize(AbstractGoogleClientRequest<?> abstractGoogleClientRequest) throws IOException {
                                abstractGoogleClientRequest.setDisableGZipContent(true);
                            }
                        });
                // end options for devappserver

                myApiService = builder.build();
            }

            context = params[0];

            try
            {
                return myApiService.pulljokes().execute().getData();
            }
            catch (IOException e)
            {
                return e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(String result)
        {
            spinner.setVisibility(View.GONE);

            Intent intent = new Intent(context, JokeDisplayActivity.class);
            intent.putExtra(JokeDisplayActivity.JOKE_KEY, result);
            startActivity(intent);
        }
    }



}
