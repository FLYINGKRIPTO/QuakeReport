package com.example.android.quakereport;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.util.Log;

import java.util.List;

public class EarthquakeLoader extends AsyncTaskLoader<List<Earthquake>> {
    private static final String TAG = EarthquakeLoader.class.getName();
    private String mUrl;

    public EarthquakeLoader(Context context,String url){
        super(context);
        mUrl = url ;
    }
    @Override
    protected void onStartLoading() {
        forceLoad();
        Log.d(TAG, "onStartLoading: method " );
    }

    @Override
    public List<Earthquake> loadInBackground() {
        if(mUrl==null){
            Log.d(TAG, "loadInBackground: " );
        return null;}
        List<Earthquake> earthquakes = QueryUtils.fetchEarthquakeData(mUrl);
        return earthquakes;

     }
}
