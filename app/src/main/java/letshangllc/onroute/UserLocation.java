package letshangllc.onroute;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.places.Place;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Created by Carl on 6/16/2015.
 */
public class UserLocation extends Service implements LocationListener {
    private final Context mContext;

    // flag for GPS Status
    boolean isGPSEnabled = false;

    // flag for network status
    boolean isNetworkEnabled = false;

    boolean canGetLocation = false;

    android.location.Location location;
    double latitude;
    double longitude;

    /* Reference the map created in the activity */
    private GoogleMap mMap;

    // The minimum distance to change updates in meters
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 5;

    // The minimum time beetwen updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 1000;

    // Declaring a Location Manager
    protected LocationManager locationManager;

    public UserLocation(Context context, GoogleMap map) {
        this.mMap = map;
        this.mContext = context;
        getLocation();
    }

    public UserLocation(Context context){
        this.mContext = context;
        getLocation();
    }

    public android.location.Location getLocation() {
        try {
            locationManager = (LocationManager) mContext
                    .getSystemService(LOCATION_SERVICE);

            // getting GPS status
            isGPSEnabled = locationManager
                    .isProviderEnabled(LocationManager.GPS_PROVIDER);

            // getting network status
            isNetworkEnabled = locationManager
                    .isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!isGPSEnabled && !isNetworkEnabled) {
                // location service disabled
                Log.i("GPS: ", "Disabled");
            } else {
                this.canGetLocation = true;

                // if GPS Enabled get lat/long using GPS Services

                if (isGPSEnabled) {
                    try {
                        locationManager.requestLocationUpdates(
                                LocationManager.GPS_PROVIDER, MIN_TIME_BW_UPDATES,
                                MIN_DISTANCE_CHANGE_FOR_UPDATES, this);

                        if (locationManager != null) {
                            location = locationManager
                                    .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            updateGPSCoordinates(location);
                        }
                    }catch (SecurityException e){
                        e.printStackTrace();
                    }
                    Log.d("GPS Enabled", "GPS Enabled");

                }

                // First get location from Network Provider
                if (isNetworkEnabled) {
                    if (location == null) {
                        try {
                            locationManager.requestLocationUpdates(
                                    LocationManager.NETWORK_PROVIDER,
                                    MIN_TIME_BW_UPDATES,
                                    MIN_DISTANCE_CHANGE_FOR_UPDATES, this);

                            Log.d("Network", "Network");

                            if (locationManager != null) {
                                location = locationManager
                                        .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                                updateGPSCoordinates(location);
                            }
                        }catch (SecurityException e){
                            e.printStackTrace();
                        }
                    }

                }
            }
        } catch (Exception e) {
            // e.printStackTrace();
            Log.e("Error : Location",
                    "Impossible to connect to LocationManager", e);
        }

        return location;
    }

    public void updateGPSCoordinates(Location location) {
        if (location != null) {
            latitude = location.getLatitude();
            longitude = location.getLongitude();
        }
    }

    public boolean canGetLocation() {
        return this.canGetLocation;
    }

    public void onLocationChanged(Location location) {
        //Toast.makeText(getApplicationContext(), : " + location.toString(), Toast.LENGTH_LONG).show();
        updateGPSCoordinates(location);
        CameraUpdate center = CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(), location.getLongitude()));
        if(mMap != null){
            mMap.moveCamera(center);
        }


    }

    public Address getUserPlace() throws IOException {
        Geocoder geocoder = new Geocoder(mContext, Locale.getDefault());
        List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
        return addresses.get(0);

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    public void onProviderDisabled(String provider) {
    }

    public void onProviderEnabled(String provider) {
    }

    public IBinder onBind(Intent intent) {
        return null;
    }
}