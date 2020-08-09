package com.emad.hanna.easytrack;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;

import java.util.Date;
import java.util.List;

public class MyLocationProvider {
    Location location;
    LocationManager locationManager;
    Context context;
    boolean canGetLocation;
    public static final long MIN_TIME_BETWEEN_UPDATES=3*1000;
    public static final float MIN_DISTANCE_BETWEEN_UPDATES=3f;


    public MyLocationProvider(Context context){
        this.context=context;
        locationManager=(LocationManager)
                context.getSystemService(Context.LOCATION_SERVICE);
        location=null;
        canGetLocation=false;
        getCurrentLocation();
    }

    @SuppressLint("MissingPermission")
    private void getCurrentLocation(){
        boolean GPSEnabled=
                locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean isNetWorkEnabled=
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        if(!GPSEnabled&&!isNetWorkEnabled){
            canGetLocation=false;
            location=null;
            return;
        }
        if(GPSEnabled)
            location=locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        else
            location=locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

        if(location==null)
            getBestLastKnownLocation();
    }

    @SuppressLint("MissingPermission")
    private void getBestLastKnownLocation() {
        List<String> providers=
                locationManager.getProviders(false);
        for(int i=0;i<providers.size();i++){
            String provider=providers.get(i);
            Location temp=
                    locationManager.getLastKnownLocation(provider);

            if(temp==null)continue;

            if(location==null){
                location=temp;
                continue;
            }
            if(temp.getAccuracy()>location.getAccuracy()){
                location=temp;
            }

            long tempTime=temp.getTime();
            Date tempDate=new Date(tempTime);
            Date locationDate=new Date(location.getTime());
            if(tempDate.compareTo(locationDate)>0)
                location=temp;
        }
    }
    public Location getLocation() {
        getCurrentLocation();
        return location;
    }
    @SuppressLint("MissingPermission")
    public void trackDeviceLocation(LocationListener locationListener){
        String provider=null;
        if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
            provider=LocationManager.GPS_PROVIDER;
        else provider=LocationManager.NETWORK_PROVIDER;

        locationManager.requestLocationUpdates(provider,MIN_TIME_BETWEEN_UPDATES,
                MIN_DISTANCE_BETWEEN_UPDATES,locationListener);
    }

}
