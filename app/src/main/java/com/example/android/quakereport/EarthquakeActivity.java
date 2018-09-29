/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.quakereport;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class EarthquakeActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<Earthquake>>{
    private static final String LOG_TAG = EarthquakeActivity.class.getName();
    private static final int EARTHQUAKE_LOADER_ID = 1;
    private TextView mEmptyStateTextView;
    private static final String USGS_REQUEST_URL ="http://earthquake.usgs.gov/fdsnws/event/1/query";
    private EarthquakeAdapter mAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.earthquake_activity);
        ListView earthquakeListView = (ListView) findViewById(R.id.list);
        mEmptyStateTextView = (TextView) findViewById(R.id.empty_view);


        ConnectivityManager connMgr = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if(networkInfo!=null && networkInfo.isConnected()) {
            LoaderManager loaderManager = getLoaderManager();
            loaderManager.initLoader(EARTHQUAKE_LOADER_ID,null,this);
        }
else {
            ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);
            progressBar.setVisibility(View.INVISIBLE);
            mEmptyStateTextView.setText(R.string.no_internet_connection);
        }
        // Find a reference to the {@link ListView} in the layout


       mAdapter = new EarthquakeAdapter(this, new ArrayList<Earthquake>());
        // Set the adapter on the {@link ListView}
        // so the list can be populated in the user interface
        earthquakeListView.setAdapter(mAdapter);


        earthquakeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                // Find the current earthquake that was clicked on
                Earthquake currentEarthquake = mAdapter.getItem(position);

                // Convert the String URL into a URI object (to pass into the Intent constructor)
                Uri earthquakeUri = Uri.parse(currentEarthquake.getURL());

                // Create a new intent to view the earthquake URI
                Intent websiteIntent = new Intent(Intent.ACTION_VIEW, earthquakeUri);

                // Send the intent to launch a new activity
                startActivity(websiteIntent);
            }
        });
       // EarthquakeAsyncTask task = new EarthquakeAsyncTask();
        //task.execute(USGS_REQUEST_URL);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id =item.getItemId();
        if(id==R.id.action_settings){
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<List<Earthquake>> onCreateLoader(int id, Bundle args) {
        {
            Log.e(LOG_TAG, "onCreateLoader: in OnCreateLoader " );
            SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);

            // getString retrieves a String value from the preferences. The second parameter is the default value for this preference.
            String minMagnitude = sharedPrefs.getString(
                    getString(R.string.settings_min_magnitude_key),
                    getString(R.string.settings_min_magnitude_default));
            Log.e(LOG_TAG, "onCreateLoader: getStringMethod done");
            String orderBy  = sharedPrefs.getString(
                    getString(R.string.settings_order_by_key),
                    getString(R.string.settings_order_by_default)
            );

            // parse breaks apart the URI string that's passed into its parameter
            Uri baseUri = Uri.parse(USGS_REQUEST_URL);
            Log.e(LOG_TAG, "onCreateLoader: parse method done " );

            // buildUpon prepares the baseUri that we just parsed so we can add query parameters to it
            Uri.Builder uriBuilder = baseUri.buildUpon();
            Log.e(LOG_TAG, "onCreateLoader: uriBuilder method started" );
            // Append query parameter and its value. For example, the `format=geojson`
            uriBuilder.appendQueryParameter("format", "geojson");
            uriBuilder.appendQueryParameter("orderby", "orderBy");
            uriBuilder.appendQueryParameter("minmag", minMagnitude);
            uriBuilder.appendQueryParameter("limit", "10");


            Log.e(LOG_TAG, "onCreateLoader: uriBuilder Method done" );
            // Return the completed uri `http://earthquake.usgs.gov/fdsnws/event/1/query?format=geojson&limit=10&minmag=minMagnitude&orderby=time
            return new EarthquakeLoader(this, uriBuilder.toString());

        }

    }

    @Override
    public void onLoadFinished(Loader<List<Earthquake>> loader, List<Earthquake> earthquakes) {
        mAdapter.clear();

        // If there is a valid list of {@link Earthquake}s, then add them to the adapter's
        // data set. This will trigger the ListView to update.
        if (earthquakes != null && !earthquakes.isEmpty()) {
            mAdapter.addAll(earthquakes);
        }


        else{
            mEmptyStateTextView.setText(R.string.no_earthquakes);
        }
        ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);

    }

    @Override
    public void onLoaderReset(Loader<List<Earthquake>> loader) {
          mAdapter.clear();
    }

   /* private class EarthquakeAsyncTask extends AsyncTask<String,Void,List<Earthquake>> {

        @Override
        protected List<Earthquake> doInBackground(String... strings) {

            if (strings.length < 1 || strings[0] == null) {
                return null;
            }
            List<Earthquake> result = QueryUtils.fetchEarthquakeData(strings[0]);
            return result;
        }

        protected void onPostExecute(List<Earthquake> data) {
            mAdapter.clear();
            if (data != null || data.isEmpty()) {
                mAdapter.addAll(data);
            }
        }
    }*/
}
