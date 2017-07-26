package com.labvolution.nmealistener;

import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

/**
 * Created by gregory.payne on 26/07/2017.
 */

public class GlobalPositioningSystem {
    private static final String TAG = "NMEA";

    private LocationManager locationManager;
    private LocationListener locationListener;

    private double latitude;
    private double longitude;
    private double accuracy;
    private double altitude;

    public GlobalPositioningSystem(LocationManager locationManager) {
        Log.d(TAG, "GlobalPositioningSystem() Constructor");
        this.locationManager = locationManager;

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Log.d(TAG, "onLocationChanged()");
                Log.d(TAG, "Latitude: " + location.getLatitude());
                Log.d(TAG, "Longitude: " + location.getLongitude());
                Log.d(TAG, "Accuracy: " + location.getAccuracy());
                Log.d(TAG, "Altitude: " + location.getAltitude());

                latitude = location.getLatitude();
                longitude = location.getLongitude();
                accuracy = location.getAccuracy();
                altitude = location.getAltitude();
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
                Log.d(TAG, "onStatusChanged()");
            }

            @Override
            public void onProviderEnabled(String provider) {
                Log.d(TAG, "onProviderEnabled()");
            }

            @Override
            public void onProviderDisabled(String provider) {
                Log.d(TAG, "onProviderDisabled()");
            }
        };

        try {
            locationManager.addNmeaListener(new GpsStatus.NmeaListener() {
                @Override
                public void onNmeaReceived(long timestamp, String nmea) {
                    Log.d(TAG, "onNmeaReceived() " + nmea);

                }
            });

            this.locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

        } catch (SecurityException ex) {
            Log.e(TAG, "NMEA Listener error: " + ex.getStackTrace());
        }
    }

    public double getAccuracy() {
        return accuracy;
    }

    public double getAltitude() {
        return altitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }
}
