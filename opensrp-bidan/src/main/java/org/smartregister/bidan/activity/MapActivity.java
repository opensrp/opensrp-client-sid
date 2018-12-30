package org.smartregister.bidan.activity;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.events.MapEventsReceiver;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.ItemizedOverlayWithFocus;
import org.osmdroid.views.overlay.MapEventsOverlay;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.infowindow.InfoWindow;
import org.smartregister.bidan.R;
import org.smartregister.bidan.utils.MyInfoWindow;
import org.smartregister.commonregistry.CommonPersonObject;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.commonregistry.CommonRepository;
import org.smartregister.view.activity.SecuredActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;

public class MapActivity extends SecuredActivity implements MapEventsReceiver {
    private static final String TAG = MapActivity.class.getName();
    MapView map = null;
    LocationManager locationManager;
    String provider;
    Timer timer1;
    Location savedLocation = null;
    private SharedPreferences preferences;
    LocationResult locationResult = new LocationResult(){
        @Override
        public void gotLocation(Location location){
            if (location == null) return;
            saveLocation(location);
            //Got the location!
            Double lat = location.getLatitude();
            Double lng = location.getLongitude();
            GeoPoint startPoint = new GeoPoint(lat, lng);
            IMapController mapController = map.getController();
            mapController.animateTo(startPoint);
            mapController.setZoom(16);
        }
    };
    boolean gps_enabled=false;
    boolean network_enabled=false;
    CommonRepository commonRepository;

    @Override
    public void onCreation() {

        //handle permissions first, before map is created. not depicted here

        //load/initialize the osmdroid configuration, this can be done
        Context ctx = getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));
        //setting this before the layout is inflated is a good idea
        //it 'should' ensure that the map has a writable location for the map cache, even without permissions
        //if no tiles are displayed, you can try overriding the cache path using Configuration.getInstance().setCachePath
        //see also StorageUtils
        //note, the load method also sets the HTTP User Agent to your application's package name, abusing osm's tile servers will get you banned based on this string

        //inflate and create the map
        preferences = getDefaultSharedPreferences(this);
        commonRepository = new CommonRepository("ec_kartu_ibu", new String[]{"ec_kartu_ibu.is_closed", "ec_kartu_ibu.namalengkap", "ec_kartu_ibu.umur", "ec_kartu_ibu.namaSuami", "noIbu"});
        setContentView(R.layout.map_layout);

        map = (MapView) findViewById(R.id.map);
        map.setTileSource(TileSourceFactory.MAPNIK);
        map.setBuiltInZoomControls(true);
        map.setMultiTouchControls(true);
        savedLocation = getSavedLocation();
        if(savedLocation!=null){
            showUserLocation(savedLocation);
        }else{
            IMapController mapController = map.getController();
            mapController.setZoom(11);
            GeoPoint startPoint = new GeoPoint(-8.5973, 116.3878);
            mapController.setCenter(startPoint);
        }

        locationManager = (LocationManager) getSystemService(android.content.Context.LOCATION_SERVICE);

        provider = locationManager.getBestProvider(new Criteria(), false);
        if ( provider == null ) {
            provider = LocationManager.GPS_PROVIDER;
        }

        getLocation(this,locationResult);

        Cursor kiCursor = context().commonrepository("ec_kartu_ibu").rawCustomQueryForAdapter("SELECT object_id FROM ec_kartu_ibu_search WHERE ec_kartu_ibu_search.is_closed=0 AND namalengkap != '' AND namalengkap IS NOT NULL");

        ArrayList<String> iDs = new ArrayList<String>();
        if (kiCursor.moveToFirst()) {
            do {
                String value = kiCursor.getString(kiCursor.getColumnIndex("object_id"));
                iDs.add(value);
            }while (kiCursor.moveToNext());
        }
        String ids = "("+implode(",",iDs.toArray(new String[0]))+")";
        kiCursor.close();

        kiCursor = context().initRepository().getWritableDatabase().rawQuery("SELECT * FROM ec_details WHERE key='gps' AND value!='' AND base_entity_id IN ('"+implode("','",iDs.toArray(new String[0]))+"')", null);
        HashMap<String, String> gpses = new HashMap<>();
        if (kiCursor.moveToFirst()) {
            do {
                String bei = kiCursor.getString(kiCursor.getColumnIndex("base_entity_id"));
                String value = kiCursor.getString(kiCursor.getColumnIndex("value"));
                gpses.put(bei,value);
            }while (kiCursor.moveToNext());
        }
        kiCursor.close();
        showClientLocations(gpses);
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

    public void showUserLocation(Location location){
        Double lat = location.getLatitude();
        Double lng = location.getLongitude();
        GeoPoint startPoint = new GeoPoint(lat, lng);
        IMapController mapController = map.getController();
        mapController.animateTo(startPoint);
        mapController.setZoom(16);
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
    }

    public void showClientLocations(HashMap<String, String> gpses){
        ArrayList<Marker> items = new ArrayList<Marker>();
        for(Map.Entry<String, String> entry : gpses.entrySet()){
            Marker marker = new Marker(map);
            String bei = entry.getKey();

            HashMap<String, String> client = new HashMap<>();
            Cursor kiCursor = context().initRepository().getWritableDatabase().rawQuery("select ec_kartu_ibu.id as _id , ec_kartu_ibu.relationalid , ec_kartu_ibu.is_closed , ec_kartu_ibu.details , ec_kartu_ibu.isOutOfArea , ec_kartu_ibu.namalengkap , ec_kartu_ibu.umur , ec_kartu_ibu.namaSuami , noIbu FROM ec_kartu_ibu  WHERE id='"+bei+"'", null);
            kiCursor.moveToFirst();
            CommonPersonObject personinlist = commonRepository.readAllcommonforCursorAdapter(kiCursor);
            CommonPersonObjectClient pClient = new CommonPersonObjectClient(personinlist.getCaseId(),
                    personinlist.getDetails(), personinlist.getDetails().get("FWHOHFNAME"));
            pClient.setColumnmaps(personinlist.getColumnmaps());
            kiCursor.close();

            Log.d(TAG, "showClientLocations: client="+client);
            String gps = entry.getValue();
            String [] latlon = gps.split(" ");
            Double lat = Double.valueOf(latlon[0]);
            Double lng = Double.valueOf(latlon[1]);

            marker.setPosition(new GeoPoint(lat,lng));
            marker.setAnchor(Marker.ANCHOR_CENTER, 1.0f);
            marker.setOnMarkerClickListener(new Marker.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker, MapView mapView) {
                    InfoWindow.closeAllInfoWindowsOn(mapView);
                    mapView.getController().animateTo(marker.getPosition());
                    marker.showInfoWindow();
                    return true;
                }
            });
            MyInfoWindow infoWindow = new MyInfoWindow(R.layout.bonuspack_bubble, map, pClient,this);
            marker.setInfoWindow(infoWindow);
            map.getOverlays().add(marker);
        }

        MapEventsOverlay mapEventsOverlay = new MapEventsOverlay( this);
        map.getOverlays().add(0, mapEventsOverlay);
    }

    public void showRandomPoint(Location location){
        Double lat = location.getLatitude();
        Double lng = location.getLongitude();
        double multiplier = 0.01;

        ArrayList<OverlayItem> items = new ArrayList<OverlayItem>();
        for (int i=0; i < 50; i++){
            int sign1 = 1;
            int sign2 = 1;
            if(Math.random()>0.5){
                sign1 = 1;
            }else{
                sign1 = -1;
            }
            if(Math.random()>0.5){
                sign2 = 1;
            }else{
                sign2 = -1;
            }
            double random_lon = lat + Math.random()*multiplier*sign1;
            double random_lat = lng + Math.random()*multiplier*sign2;
            items.add(new OverlayItem("Client #"+i, "Description", new GeoPoint(random_lon,random_lat))); // Lat/Lon decimal degrees
        }


        //the overlay
        ItemizedOverlayWithFocus<OverlayItem> mOverlay = new ItemizedOverlayWithFocus<OverlayItem>(items,
                new ItemizedIconOverlay.OnItemGestureListener<OverlayItem>() {
                    @Override
                    public boolean onItemSingleTapUp(final int index, final OverlayItem item) {
                        //do something
                        return true;
                    }
                    @Override
                    public boolean onItemLongPress(final int index, final OverlayItem item) {
                        return false;
                    }
                },this);
        mOverlay.setFocusItemsOnTap(true);
        // add overlay
        map.getOverlays().add(mOverlay);
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
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListenerGps);
            }
        }

        if(network_enabled){
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListenerNetwork);
            }
        }

        timer1=new Timer();
        timer1.schedule(new GetLastLocation(), 20000);
        return true;
    }

    @Override
    public boolean singleTapConfirmedHelper(GeoPoint p) {
        InfoWindow.closeAllInfoWindowsOn(map);
        return false;
    }

    @Override
    public boolean longPressHelper(GeoPoint p) {
        Toast.makeText(this, "Tap on ("+p.getLatitude()+","+p.getLongitude()+")", Toast.LENGTH_SHORT).show();
        return false;
    }

    class GetLastLocation extends TimerTask {
        @Override
        public void run() {
            runOnUiThread(new Runnable(){

                @Override
                public void run(){
                    Log.d(TAG, "run: Timer triggered");
                    // update ui here
                    locationManager.removeUpdates(locationListenerGps);
                    locationManager.removeUpdates(locationListenerNetwork);

                    Location net_loc=null, gps_loc=null;
                    if(gps_enabled){
                        if (ContextCompat.checkSelfPermission(MapActivity.this,
                                Manifest.permission.ACCESS_FINE_LOCATION)
                                == PackageManager.PERMISSION_GRANTED) {
                            Log.d(TAG, "onCreate: location permission granted, getting location");
                            gps_loc=locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        }
                    }

                    if(network_enabled){
                        if (ContextCompat.checkSelfPermission(MapActivity.this,
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
                        return;
                    }

                    if(gps_loc!=null){
                        locationResult.gotLocation(gps_loc);
                        return;
                    }
                    if(net_loc!=null){
                        locationResult.gotLocation(net_loc);
                        return;
                    }
                    locationResult.gotLocation(null);
                }
            });

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

    @Override
    protected void onResumption() {
        map.onResume(); //needed for compass, my location overlays, v6.0.0 and up
//        if (ContextCompat.checkSelfPermission(this,
//                Manifest.permission.ACCESS_FINE_LOCATION)
//                == PackageManager.PERMISSION_GRANTED) {
//
//            getLocation(this,locationResult);
//        }
    }

    public void onPause(){
        super.onPause();
        //this will refresh the osmdroid configuration on resuming.
        //if you make changes to the configuration, use
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //Configuration.getInstance().save(this, prefs);
        map.onPause();  //needed for compass, my location overlays, v6.0.0 and up
    }
}