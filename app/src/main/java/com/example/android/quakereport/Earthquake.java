package com.example.android.quakereport;

public class Earthquake {
    private Double mMagnitude;
    private String mLocation;
    private long  mTimeInMiliSeconds;
    private String mURL;
    public Earthquake(Double magnitude, String location, long timeInMiliSeconds,String url)
    {
        mMagnitude= magnitude;
        mLocation = location;
        mTimeInMiliSeconds = timeInMiliSeconds;
        mURL = url;
    }
    public Double getMagnitude()
    {
        return mMagnitude;
    }
    public String getLocation()
    {
        return mLocation;

    }
    public long getTimeInMiliSeconds()
    {
        return mTimeInMiliSeconds;
    }
   public String getURL(){
        return mURL;
   }
}
