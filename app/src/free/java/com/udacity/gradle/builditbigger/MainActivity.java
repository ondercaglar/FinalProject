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

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
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
    private InterstitialAd mInterstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        spinner = findViewById(R.id.progressBar1);
        spinner.setVisibility(View.GONE);


        AdView mAdView = (AdView) findViewById(R.id.adView);
        // Create an ad request. Check logcat output for the hashed device ID to
        // get test ads on a physical device. e.g.
        // "Use AdRequest.Builder.addTestDevice("ABCDEF012345") to get test ads on this device."
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build();
        mAdView.loadAd(adRequest);


        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(getString(R.string.interstitial_ad_unit_id));
        mInterstitialAd.loadAd(new AdRequest.Builder().build());
        mInterstitialAd.setAdListener(new AdListener()
        {
            // Display the ad after the user hits the button, but before the joke is shown.
            @Override
            public void onAdClosed()
            {
                spinner.setVisibility(View.VISIBLE);
                new EndpointsAsyncTaskNew().execute(MainActivity.this);

                // Load the next interstitial.
                mInterstitialAd.loadAd(new AdRequest.Builder().build());
            }

        });

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
        // Display the ad after the user hits the button, but before the joke is shown.
        if (mInterstitialAd.isLoaded())
        {
            mInterstitialAd.show();
        }
        else
        {
            spinner.setVisibility(View.VISIBLE);
            new EndpointsAsyncTaskNew().execute(MainActivity.this);
        }
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


    class EndpointsAsyncTaskNew extends AsyncTask<Context, Void, String>
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
            //Toast.makeText(context, result, Toast.LENGTH_LONG).show();

            spinner.setVisibility(View.GONE);

            Intent intent = new Intent(context, JokeDisplayActivity.class);
            intent.putExtra(JokeDisplayActivity.JOKE_KEY, result);
            startActivity(intent);
        }
    }



}
