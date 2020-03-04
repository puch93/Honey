package com.match.marryme.activity;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import com.match.marryme.sharedPref.UserPref;

public class BaseActivity extends Activity{

    LocationManager locationmanager;
    protected double latitude,longitude;

    public static final String TAG = "TEST_HOME";

    protected final LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            latitude = location.getLatitude();
            longitude = location.getLongitude();

            System.out.println("gps data : " + latitude + " , " + longitude);

            UserPref.setLat(getApplicationContext(),String.valueOf(latitude));
            UserPref.setLon(getApplicationContext(),String.valueOf(longitude));

            stopLocationService();
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };

    protected void startLocationService(){
        locationmanager = (LocationManager) getApplication().getSystemService(Context.LOCATION_SERVICE);
        long minTime = 0;
        float minDistance = 0;

        locationmanager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,minTime,minDistance,locationListener);
        locationmanager.requestLocationUpdates(LocationManager.GPS_PROVIDER,minTime,minDistance,locationListener);
    }

    protected void stopLocationService(){
        locationmanager = (LocationManager) getApplication().getSystemService(Context.LOCATION_SERVICE);
        long minTime = 0;
        float minDistance = 0;

        locationmanager.removeUpdates(locationListener);
    }
}
