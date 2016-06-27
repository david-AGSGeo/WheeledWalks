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

package davidlee_11055579.wheeledwalks.services;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.util.Log;

import davidlee_11055579.wheeledwalks.models.AccelerometerData;
import davidlee_11055579.wheeledwalks.models.Constants;

/**
 * Created by David Lee on 13/10/2015.
 * Service to monitor the Phones accelerometer.
 * sets up the listener and broadcasts the data to the LogWriter
 */
public class AccelerometerManager extends Service implements SensorEventListener {


    private AccelerometerData mAccelData;
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;

    private BroadcastReceiver mInterfaceDataReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.hasExtra(Constants.FINALISE_WALK) ||
                    intent.hasExtra(Constants.DISCARD_RECORDING)) {

                stopSelf(); //terminate the AccelerometerManager
            }
        }
    };

    private BroadcastReceiver mBluetoothConnectedReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.hasExtra(Constants.BT_CONNECTIONS_COMPLETE)) {
                Log.d(Constants.DEBUG_TAG, "disabling Internal Accelerometer - BT device Connected");
                stopSelf(); //terminate the AccelerometerManager
            }
        }
    };

    /**
     * Runs when the service is started, and sets up the sensor listeners
     * @param intent - the starting intent
     * @param flags - ???
     * @param startId - ???
     * @return - a flag with the type of service
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.v(Constants.DEBUG_TAG, "ALS: onStartCommand AccelerometerManager Service");
        mAccelData = new AccelerometerData();
        //set up accelerometer


        mSensorManager = (SensorManager) getApplicationContext()
                .getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        registerReceiver(mInterfaceDataReceiver, new IntentFilter(Constants.INTERFACE_INTENT));
        registerReceiver(mBluetoothConnectedReceiver, new IntentFilter(Constants.BT_SENSOR_CONNECTED_INTENT));


        return START_STICKY; //restart the service if the phone terminates it due to lack of memory
    }

    /**
     * Listens to changes in the sensors and sends the raw accelerometer data to the logWriter
     * @param event - a sensor event
     */
    @Override
    public void onSensorChanged(SensorEvent event) {
        //Log.v(Constants.DEBUG_TAG, "sensor changed!");
        Sensor mySensor = event.sensor;
        if (mySensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            mAccelData.setTimestamp(event.timestamp);
            mAccelData.setAccelX(event.values[0]);
            mAccelData.setAccelY(event.values[1]);
            mAccelData.setAccelZ(event.values[2]);
            Intent replyIntent = new Intent(Constants.ACCEL_DATA_INTENT);
            replyIntent.putExtra(Constants.ACCEL_DATA, mAccelData);
            sendBroadcast(replyIntent);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public void onDestroy() {
        Log.v(Constants.DEBUG_TAG, "ALM: onDestroy AccelerometerManager Service");
        unregisterReceiver(mInterfaceDataReceiver);
        unregisterReceiver(mBluetoothConnectedReceiver);
        mSensorManager.unregisterListener(this);

        super.onDestroy();
    }
}
