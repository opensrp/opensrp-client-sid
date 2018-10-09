package org.smartregister.bidan.utils;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import org.osmdroid.config.Configuration;
import org.smartregister.commonregistry.CommonRepository;

import java.util.Timer;
import java.util.TimerTask;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;

public class LocationHelper {
    private static final String TAG = LocationHelper.class.getName();
    LocationManager locationManager;
    String provider;
    Timer timer1;
    private SharedPreferences preferences;
    Context context;
    GetLastLocationTask getLastLocationTask;

    public LocationResult locationResult = new LocationResult(){
        @Override
        public void gotLocation(Location location){
            saveLocation(location);
        }
    };

    boolean gps_enabled=false;
    boolean network_enabled=false;
    CommonRepository commonRepository;

    public LocationHelper(Context context){
        this.context = context;
        init();
    }

    public void init(){
        Configuration.getInstance().load(context, PreferenceManager.getDefaultSharedPreferences(context));
        preferences = getDefaultSharedPreferences(context);

        locationManager = (LocationManager) context.getSystemService(android.content.Context.LOCATION_SERVICE);

        provider = locationManager.getBestProvider(new Criteria(), false);
        if ( provider == null ) {
            provider = LocationManager.GPS_PROVIDER;
        }
        getLastLocationTask = new GetLastLocationTask();
        getLocation(context,locationResult);
    }

    public static String implode(String separator, String... data) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < data.length - 1; i++) {
            //data.length - 1 => to not add separator at the end
            if (!data[i].matches(" *")) {//empty string are ""; " "; "  "; and so on
                sb.append(data[i]);
                sb.append(separator);
            }
        }
        sb.append(data[data.length - 1].trim());
        return sb.toString();
    }

    public Location getSavedLocation(){
        String gps = preferences.getString("gpsCoordinates", "").trim();
        if("".equals(gps)){
            return null;
        }
        String [] latlon = gps.split(" ");
        Location location = new Location("");
        location.setLatitude(Double.valueOf(latlon[0]));
        location.setLongitude(Double.valueOf(latlon[1]));
        return location;
    }

    public void saveLocation(Location location){
        String gps = String.valueOf(location.getLatitude())+" "+String.valueOf(location.getLongitude());
        preferences.edit().putString("gpsCoordinates", gps).apply();
        Log.d(TAG, "saveLocation: location saved : "+gps);
    }

    public boolean getLocation(Context context, LocationResult result)
    {
        //I use LocationResult callback class to pass location value from MyLocation to user code.
        locationResult=result;
        if(locationManager==null)
            locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        //exceptions will be thrown if provider is not permitted.
        try{gps_enabled=locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);}catch(Exception ex){}
        try{network_enabled=locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);}catch(Exception ex){}

        //don't start listeners if no provider is enabled
        if(!gps_enabled && !network_enabled)
            return false;

        if(gps_enabled){
            if (ContextCompat.checkSelfPermission(context,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListenerGps);
            }
        }

        if(network_enabled){
            if (ContextCompat.checkSelfPermission(context,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListenerNetwork);
            }
        }

        timer1=new Timer();
        timer1.schedule(new LocationHelper.GetLastLocation(), 20000);
        return true;
    }

    class GetLastLocation extends TimerTask {
        @Override
        public void run() {
            if(getLastLocationTask.getStatus() != AsyncTask.Status.RUNNING){
                getLastLocationTask.execute();
            }

        }
    }

    LocationListener locationListenerGps = new LocationListener() {
        public void onLocationChanged(Location location) {
            timer1.cancel();
            locationResult.gotLocation(location);
            locationManager.removeUpdates(this);
            locationManager.removeUpdates(locationListenerNetwork);
        }
        public void onProviderDisabled(String provider) {}
        public void onProviderEnabled(String provider) {}
        public void onStatusChanged(String provider, int status, Bundle extras) {}
    };

    LocationListener locationListenerNetwork = new LocationListener() {
        public void onLocationChanged(Location location) {
            timer1.cancel();
            locationResult.gotLocation(location);
            locationManager.removeUpdates(this);
            locationManager.removeUpdates(locationListenerGps);
        }
        public void onProviderDisabled(String provider) {}
        public void onProviderEnabled(String provider) {}
        public void onStatusChanged(String provider, int status, Bundle extras) {}
    };

    public static abstract class LocationResult{
        public abstract void gotLocation(Location location);
    }

    class GetLastLocationTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            Log.d(TAG, "run: Timer triggered");
            // update ui here
            locationManager.removeUpdates(locationListenerGps);
            locationManager.removeUpdates(locationListenerNetwork);

            Location net_loc=null, gps_loc=null;
            if(gps_enabled){
                if (ContextCompat.checkSelfPermission(context,
                        Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "onCreate: location permission granted, getting location");
                    gps_loc=locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                }
            }

            if(network_enabled){
                if (ContextCompat.checkSelfPermission(context,
                        Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "onCreate: location permission granted, getting location");
                    net_loc=locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                }
            }


            //if there are both values use the latest one
            if(gps_loc!=null && net_loc!=null){
                if(gps_loc.getTime()>net_loc.getTime())
                    locationResult.gotLocation(gps_loc);
                else
                    locationResult.gotLocation(net_loc);
                return null;
            }

            if(gps_loc!=null){
                locationResult.gotLocation(gps_loc);
                return null;
            }
            if(net_loc!=null){
                locationResult.gotLocation(net_loc);
                return null;
            }
            locationResult.gotLocation(null);
            return null;
        }
    }
}
