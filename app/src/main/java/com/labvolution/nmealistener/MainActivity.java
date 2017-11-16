package com.labvolution.nmealistener;

import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Handler;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private final static String TAG = "NMEA";

    TextView nmeaTextView;
    Button gpsOnButton, gpsOffButton;

    LocationManager locationManager;
    GlobalPositioningSystem gps;

    Handler handler;

    private final Runnable getLocationUpdate = this::getLocationUpdate;

    private ArrayList<ImageView> gpsIcons = new ArrayList<>();

    int previousSatelliteCount = -1;
    int currentSatelliteCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        nmeaTextView = (TextView)findViewById(R.id.nmeaTextView);
        gpsOnButton = (Button)findViewById(R.id.gpsOnButton);
        gpsOffButton = (Button)findViewById(R.id.gpsOffButton);
        nmeaTextView.setText("");

        gpsIcons.add((ImageView) findViewById(R.id.gps0));
        gpsIcons.add((ImageView) findViewById(R.id.gps1));
        gpsIcons.add((ImageView) findViewById(R.id.gps2));
        gpsIcons.add((ImageView) findViewById(R.id.gps3));
        gpsIcons.add((ImageView) findViewById(R.id.gps4));
        gpsIcons.add((ImageView) findViewById(R.id.gps5));
        gpsIcons.add((ImageView) findViewById(R.id.gps6));
        gpsIcons.add((ImageView) findViewById(R.id.gps7));
        gpsIcons.add((ImageView) findViewById(R.id.gps8));
        gpsIcons.add((ImageView) findViewById(R.id.gps9));
        gpsIcons.add((ImageView) findViewById(R.id.gps10));
        gpsIcons.add((ImageView) findViewById(R.id.gps10More));

        locationManager = (LocationManager) MainActivity.this.getSystemService(Context.LOCATION_SERVICE);

        for(String provider : locationManager.getAllProviders()) {
            Log.d(TAG, "LocationManager providers: " + provider.toString());
        }

        startGpsMonitor();
        createButtonListeners();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    private void startGpsMonitor() {
        gps = new GlobalPositioningSystem(locationManager);
        gps.registerGpsListeners();
        if (gpsSettingsState()) {
            getHandler().post(getLocationUpdate);
            previousSatelliteCount = -1;
        } else {
            gpsOn();
        }
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
            if (gpsSettingsState()) { return; }

            // XXX: https://stackoverflow.com/a/29232367
            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            getHandler().post(getLocationUpdate);
            previousSatelliteCount = -1;
        } catch (Exception ex) {
            Log.e(TAG, "Error in gpsOn(): " + ex.getStackTrace());
        }
    }

    private void gpsOff() {
        Log.d(TAG, "gpsOff()");
        try {
            if (!gpsSettingsState()) { return; }

            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            getHandler().removeCallbacks(getLocationUpdate);
            nmeaTextView.setText("GPS Off");
            for (ImageView i : gpsIcons) { i.setVisibility(View.GONE); }
        } catch (Exception ex) {
            Log.e(TAG, "Error in gpsOff(): " + ex.getStackTrace());
        }
    }

    private boolean gpsSettingsState() {
        Log.d(TAG, "isProviderEnabled(): " + Boolean.toString(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)));
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    private void getLocationUpdate() {
        currentSatelliteCount = gps.getSatelliteCount();

        nmeaTextView.setText("");
        nmeaTextView.append("onLocationChanged()");
        nmeaTextView.append(String.format(Locale.UK, "\nLatitude: %.3f", gps.getLatitude()));
        nmeaTextView.append(String.format(Locale.UK, "\nLongitude: %.3f", gps.getLongitude()));
        nmeaTextView.append(String.format(Locale.UK, "\nAccuracy: %s", gps.getAccuracy()));
        nmeaTextView.append(String.format(Locale.UK, "\nCalculated Accuracy: %s", gps.getCalculatedAccuracy()));
        nmeaTextView.append(String.format(Locale.UK, "\nAltitude: %.0f", gps.getAltitude()));
        nmeaTextView.append(String.format(Locale.UK, "\nSatellite count: %d", currentSatelliteCount));
        nmeaTextView.append(String.format(Locale.UK, "\nFix quality: %s", gps.getGpsFixQuality()));
        nmeaTextView.append(String.format(Locale.UK, "\nFix status: %s", gps.getGpsFixStatus()));
        nmeaTextView.append(String.format(Locale.UK, "\nHDOP: %.1f", gps.getHdop()));
        nmeaTextView.append(String.format(Locale.UK, "\nPDOP: %.1f", gps.getPdop()));
        nmeaTextView.append(String.format(Locale.UK, "\nVDOP: %.1f", gps.getVdop()));
        nmeaTextView.append(String.format(Locale.UK, "\nGPS time: %s", gps.getGpsTime()));
        nmeaTextView.append(String.format(Locale.UK, "\nGPS date: %s", gps.getGpsDate()));

        if (previousSatelliteCount != currentSatelliteCount) {
            previousSatelliteCount = currentSatelliteCount;
            // Hide all GPS icons
            for (ImageView i : gpsIcons) { i.setVisibility(View.GONE); }
            switch (currentSatelliteCount) {
                case 0: case 1: case 2: case 3: case 4: case 5: case 6: case 7: case 8: case 9: case 10:
                    gpsIcons.get(currentSatelliteCount).setVisibility(View.VISIBLE);
                    break;
                default:
                    gpsIcons.get(11).setVisibility(View.VISIBLE); // > 10 satellites
            }
        }
        getHandler().postDelayed(getLocationUpdate, 2000);
    }

    private Handler getHandler() {
        if (this.handler == null) {
            this.handler = new Handler();
        }
        return this.handler;
    }
}
