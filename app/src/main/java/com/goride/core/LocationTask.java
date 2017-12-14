package com.goride.core;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Handler;
import android.os.Looper;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;
import java.util.Locale;

/**
 * Created by root on 9/24/17.
 */

public class LocationTask implements Runnable {

    private Location mLocation;
    private IOnLocationReady locationReady;
    private Context mContext;
    private Handler handler = new Handler(Looper.getMainLooper());
    private LatLng latLng;

    public LocationTask(Context context, IOnLocationReady iOnLocationReady, Location location) {
        locationReady = iOnLocationReady;
        mLocation = location;
        mContext = context;
    }
    public LocationTask(Context context, IOnLocationReady iOnLocationReady, LatLng latLng) {
        mContext = context;
        locationReady = iOnLocationReady;
        this.latLng = latLng;
    }

    @Override
    public void run() {
        Geocoder geocoder = new Geocoder(mContext, Locale.getDefault());
        try {

            List<Address> addresses = null;
            if(mLocation == null) {
                addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
            }else {
                addresses = geocoder.getFromLocation(mLocation.getLatitude(), mLocation.getLongitude(), 1);
            }
            final Address address = addresses.get(0);

            if(locationReady != null) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        locationReady.onReady(address);
                    }
                });
            }
        }catch (final Exception e) {
            if(locationReady != null) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        locationReady.onError(e);
                    }
                });
            }
        }
    }
    public interface IOnLocationReady {

        void onReady(Address address);
        void onError(Throwable throwable);
    }
}
