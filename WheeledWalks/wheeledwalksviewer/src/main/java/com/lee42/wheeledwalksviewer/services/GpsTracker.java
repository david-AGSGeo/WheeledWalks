/*
 * Copyright (C) 2015 David Lee WheeledWalks Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.lee42.wheeledwalksviewer.services;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.lee42.wheeledwalksviewer.models.Constants;


/**
 * Created by David Lee on 6/10/2015.
 * Service to track the GPS data and forward it to the logWriter for logging
 */
public class GpsTracker extends Service implements LocationListener {


    // The minimum distance to change Updates in meters
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 1; // 1 meter
    // The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 1000; // 1 second
    // flag for GPS status
    public boolean isGPSEnabled = false;
    // Declaring a Location Manager
    protected LocationManager mLocationManager;
    private Location mLocation; // Current gps location


//    private BroadcastReceiver mInterfaceDataReceiver = new BroadcastReceiver() {
//
//        @Override
//        public void onReceive(Context context, Intent intent) {
//
//            if (intent.hasExtra(Constants.FINALISE_WALK) ||
//                    intent.hasExtra(Constants.DISCARD_RECORDING)) {
//
//                stopSelf(); //terminate the GpsTracker
//            }
//        }
//    };

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.v(Constants.DEBUG_TAG, "ALS: onStartCommand GpsTracker Service");
        //registerReceiver(mInterfaceDataReceiver, new IntentFilter(Constants.INTERFACE_INTENT));
        getLocation();

        return START_STICKY; //restart the service if the phone terminates it due to lack of memory
    }

    /**
     * set op the location listener and return the current location
     * @return - current GPS location
     */
    public Location getLocation() {
        try {
            mLocationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);

            // getting GPS status
            isGPSEnabled = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

            if (!isGPSEnabled) {

                this.stopSelf();
            } else {  // if GPS Enabled get lat/long using GPS Services

                if (mLocation == null) {
                    mLocationManager.requestLocationUpdates( //request periodic updates
                            LocationManager.GPS_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                    Log.d(Constants.DEBUG_TAG, "GPS Enabled");
                    if (mLocationManager != null) {
                        mLocation = mLocationManager
                                .getLastKnownLocation(LocationManager.GPS_PROVIDER);

                    }
                }
            }

        } catch (SecurityException e) {
            e.printStackTrace();
        }

        return mLocation;
    }

    /**
     * Listens for changes to the location, and sends the data to the LogWriter
     * @param location - the new location
     */
    @Override
    public void onLocationChanged(Location location) {

        mLocation = location;
//        Intent replyIntent = new Intent(Constants.GPS_DATA_INTENT);
//        replyIntent.putExtra(Constants.GPS_LOCATION, mLocation);
//        sendBroadcast(replyIntent);
    }

    @Override
    public void onProviderDisabled(String provider) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public void onDestroy() {
        Log.v(Constants.DEBUG_TAG, "ALM: onDestroy GpsTracker Service");
        //unregisterReceiver(mInterfaceDataReceiver);
        super.onDestroy();
    }
}
