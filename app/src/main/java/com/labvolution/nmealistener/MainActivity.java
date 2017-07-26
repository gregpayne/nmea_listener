package com.labvolution.nmealistener;

import android.content.Context;
import android.content.Intent;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private final static String TAG = "NMEA";

    TextView nmeaTextView;
    Button gpsOnButton, gpsOffButton;

    LocationManager locationManager;
    GlobalPositioningSystem gps;

    Handler handler;

    private final Runnable getLocationUpdate = this::getLocationUpdate;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        nmeaTextView = (TextView)findViewById(R.id.nmeaTextView);
        gpsOnButton = (Button)findViewById(R.id.gpsOnButton);
        gpsOffButton = (Button)findViewById(R.id.gpsOffButton);
        nmeaTextView.setText("");
        locationManager = (LocationManager) MainActivity.this.getSystemService(Context.LOCATION_SERVICE);

        startGpsMonitor();
        createButtonListeners();

//
//
//        locationListener = new LocationListener() {
//            @Override
//            public void onLocationChanged(Location location) {
//                Log.e(TAG, "onLocationChanged()");
//            }
//
//            @Override
//            public void onStatusChanged(String provider, int status, Bundle extras) {
//                Log.e(TAG, "onStatusChanged()");
//            }
//
//            @Override
//            public void onProviderEnabled(String provider) {
//                Log.e(TAG, "onProviderEnabled()");
//            }
//
//            @Override
//            public void onProviderDisabled(String provider) {
//                Log.e(TAG, "onProviderDisabled()");
//            }
//        };
//
//        Log.d(TAG, "LocationListener created");
//
//        try {
//            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
//            Log.d(TAG, "LocationListener started");
//
//            locationManager.addNmeaListener(new GpsStatus.NmeaListener() {
//                @Override
//                public void onNmeaReceived(long timestamp, String nmea) {
//                    Log.e(TAG, "onNmeaReceived()");
//                    nmeaTextView.setText(nmea);
//                }
//            });
//        } catch (SecurityException ex) {
//            Log.e(TAG, ex.getStackTrace().toString());
//        }

    }

    private void startGpsMonitor() {
        Log.d(TAG, "startGpsMonitor() called");
        new Thread(new Runnable() {
            @Override
            public void run() {
                gps = new GlobalPositioningSystem(locationManager);
                gpsOn();
            }
        }).run();
    }

    private void createButtonListeners() {
        gpsOnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gpsOn();
            }
        });

        gpsOffButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gpsOff();
            }
        });
    }

    private void gpsOn(){
        Log.d(TAG, "gpsOn()");
        try {
            getHandler().post(getLocationUpdate);

//            Intent intent = new Intent("android.location.GPS_ENABLED_CHANGE");
//            intent.putExtra("enabled", true);
//            sendBroadcast(intent);
        } catch (Exception ex) {
            Log.e(TAG, "Error in gpsOn(): " + ex.getStackTrace());
        }
    }

    private void gpsOff() {
        Log.d(TAG, "gpsOff()");
        try {
            getHandler().removeCallbacks(getLocationUpdate);
            nmeaTextView.setText("GPS Off");
            // http://www.instructables.com/id/Turn-on-GPS-Programmatically-in-Android-44-or-High/
//            Intent intent = new Intent("android.location.GPS_ENABLED_CHANGE");
//            intent.putExtra("enabled", false);
//            sendBroadcast(intent);
        } catch (Exception ex) {
            Log.e(TAG, "Error in gpsOff(): " + ex.getStackTrace());
        }
    }

    private void getLocationUpdate() {
        nmeaTextView.setText("");
        nmeaTextView.append("onLocationChanged()");
        nmeaTextView.append("\nLatitude: " + gps.getLatitude());
        nmeaTextView.append("\nLongitude: " + gps.getLongitude());
        nmeaTextView.append("\nAccuracy: " + gps.getAccuracy());
        nmeaTextView.append("\nAltitude: " + gps.getAltitude());
        getHandler().postDelayed(getLocationUpdate, 2000);
    }

    private Handler getHandler() {
        if (this.handler == null) {
            this.handler = new Handler();
        }
        return this.handler;
    }
}
