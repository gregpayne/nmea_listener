package com.labvolution.nmealistener;

import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import net.sf.marineapi.nmea.parser.SentenceFactory;
import net.sf.marineapi.nmea.sentence.GGASentence;
import net.sf.marineapi.nmea.sentence.GSASentence;
import net.sf.marineapi.nmea.sentence.RMCSentence;
import net.sf.marineapi.nmea.sentence.Sentence;
import net.sf.marineapi.nmea.util.GpsFixQuality;
import net.sf.marineapi.nmea.util.GpsFixStatus;

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
    private int satelliteCount;
    private GpsFixQuality gpsFixQuality;
    private double hdop;
    private double pdop;
    private double vdop;
    private GpsFixStatus gpsFixStatus;
    private String gpsTime;
    private String gpsDate;

    private boolean validString;

    public GlobalPositioningSystem(LocationManager locationManager) {
        Log.d(TAG, "GlobalPositioningSystem() Constructor");
        this.locationManager = locationManager;
    }

    public void registerGpsListeners() {
        Log.d(TAG, "registerGpsListeners()");
        try {
            locationListener = new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    Log.d(TAG, "onLocationChanged()");
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

            locationManager.addNmeaListener(new GpsStatus.NmeaListener() {
                @Override
                public void onNmeaReceived(long timestamp, String nmea) {
                    validString = parseNmeaString(nmea);
                    Log.d("RAW", "onNmeaReceived() " + nmea + " parsed " + validString);
                }
            });

            this.locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

        } catch (SecurityException ex) {
            Log.e(TAG, "NMEA Listener error: " + ex.getStackTrace());
        }
    }

    private boolean parseNmeaString(String nmea) {
        try {
            Sentence sentence = SentenceFactory.getInstance().createParser(nmea.trim());
            switch (sentence.getSentenceId()) {
                case "GGA":
                    latitude = ((GGASentence)sentence).getPosition().getLatitude();
                    longitude = ((GGASentence)sentence).getPosition().getLongitude();
                    altitude = ((GGASentence)sentence).getPosition().getAltitude();
                    satelliteCount = ((GGASentence)sentence).getSatelliteCount();
                    gpsFixQuality = ((GGASentence)sentence).getFixQuality();

                    Log.d(TAG, "GPS fix quality: " + getGpsFixQuality().toString());
                    Log.d(TAG, "Satellite count: " + Integer.toString(getSatelliteCount()));
                    Log.d(TAG, "Altitude: " + Double.toString(getAltitude()));
                    Log.d(TAG, "Latitude: " + (Double.toString(getLatitude())));
                    Log.d(TAG, "Longitude: " + (Double.toString(getLongitude())));
                    validString = true;
                    break;
                case "GSA":
                    hdop = ((GSASentence) sentence).getHorizontalDOP();
                    vdop = ((GSASentence) sentence).getVerticalDOP();
                    pdop = ((GSASentence) sentence).getPositionDOP();
                    gpsFixStatus = ((GSASentence) sentence).getFixStatus();

                    Log.d(TAG, "PDOP: " + Double.toString(getPdop()));
                    Log.d(TAG, "VDOP: " + Double.toString(getVdop()));
                    Log.d(TAG, "HDOP: " + Double.toString(getHdop()));
                    Log.d(TAG, "Fix type: " + getGpsFixStatus().toString());
                    validString = true;
                    break;
                case "GSV":
                    break;
                case "RMC":
                    gpsTime = ((RMCSentence)sentence).getTime().toISO8601();
                    gpsDate = ((RMCSentence)sentence).getDate().toISO8601();

                    Log.d(TAG, "RMC Time: " + getGpsTime());
                    Log.d(TAG, "RMC Date: " + getGpsDate());
                    validString = true;
                    break;
                case "VTG":
                    validString = true;
                    break;
                default:
                    break;
            }
        } catch (IllegalArgumentException ex) {
            validString = false;
            Log.e(TAG, "Illegal Argument: " + ex.getStackTrace()[2].toString());
        } catch (Exception ex) {
            validString = false;
            Log.e(TAG, "Exception: " + ex.getStackTrace()[2].toString());
        }
        return validString;
    }

    public String getGpsDate() {
        return gpsDate;
    }

    public String getGpsTime() {
        return gpsTime;
    }

    public GpsFixStatus getGpsFixStatus() {
        return gpsFixStatus;
    }

    public double getHdop() {
        return hdop;
    }

    public double getPdop() {
        return pdop;
    }

    public double getVdop() {
        return vdop;
    }

    public GpsFixQuality getGpsFixQuality() {
        return gpsFixQuality;
    }

    public int getSatelliteCount() {
        return satelliteCount;
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

    public LocationListener getLocationListener() {
        return locationListener;
    }
}
