package graziano.g.accessmonitoring.service;

import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.IBinder;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class ChildService extends Service {


    private long UPDATE_INTERVAL = 15 * 1000;  /* 10 secs */
    private long FASTEST_INTERVAL = 5000; /* 2 sec */
    private static FusedLocationProviderClient locationClient;
    public static Location lastLocation;
    public static String lastLocationAddressString;
    private Geocoder geocoder;

    @Override
    public void onCreate() {
        geocoder = new Geocoder(this, Locale.getDefault());
        this.locationClient = LocationServices.getFusedLocationProviderClient(this);
        startLocationUpdates();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopLocationUpdates();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void restartLocationUpdates(){
        stopLocationUpdates();
        startLocationUpdates();
    }

    public void startLocationUpdates() {

        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(UPDATE_INTERVAL);
        locationRequest.setFastestInterval(FASTEST_INTERVAL);

        LocationCallback locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                Location location = locationResult.getLastLocation();
                if(location != null) {
                    lastLocation = location;
                    new GeocodeAsyncTask().execute(location);
                }
            }

        };

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
        }

    }

    private void stopLocationUpdates() {
        locationClient.removeLocationUpdates(new LocationCallback());
    }

    private class GeocodeAsyncTask extends AsyncTask<Location, Void, Address> {

        @Override
        protected Address doInBackground(Location... locations) {
            List<Address> addr = null;
            Location location = locations[0];
            try {
                addr = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                return (addr == null || addr.isEmpty()) ? null : addr.get(0);
            } catch (IOException ignored) {

            }
            return null;
        }

        @Override
        protected void onPostExecute(Address address) {
            StringBuilder builder = new StringBuilder();
            if(address != null && address.getMaxAddressLineIndex() >= 0) {
                builder.append(address.getAddressLine(0));
            }
            lastLocationAddressString = builder.toString();

        }
    }
}


