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
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import davidlee_11055579.wheeledwalks.controllers.DevicesDatabaseHelper;
import davidlee_11055579.wheeledwalks.models.AccelerometerData;
import davidlee_11055579.wheeledwalks.models.Constants;
import davidlee_11055579.wheeledwalks.models.Walk;
import davidlee_11055579.wheeledwalks.utilities.Utils;

/**
 * Created by David Lee on 9/10/2015.
 * Service responsible for creating the Log file and adding data to it.
 * Also passes data on as intents to the map fragment and the raw data fragment
 */
public class LogWriter extends Service {


    private Walk mWalk;
    private File mLogFile = null;
    private boolean mRecording = false;
    private DevicesDatabaseHelper mDevicesDatabaseHelper;

    //vars for calculating distance travelled
    private Location mPreviousLocation;
    private Float mTotalDistance;

    private FileWriter mLogFileWriter;


    /**
     * receives the intents broadcast by the GpsTracker, and forwards data to the other fragments
     */
    private BroadcastReceiver mGPSDataReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

                if (intent.getBooleanExtra(Constants.SERVICE_IS_DESTROYED, false)) {
                    Log.w(Constants.DEBUG_TAG, "GPS service has ended!");
                } else {
                    // mAccelMedian = getAccelerometerMedian();
                    Location location = intent.getParcelableExtra(Constants.GPS_LOCATION);
                    mTotalDistance += getDistanceFromLastFix(location);
                    Intent replyIntent = new Intent(Constants.RAW_DATA_REPLY);
                    replyIntent.putExtra(Constants.GPS_LATTITUDE, location.getLatitude());
                    replyIntent.putExtra(Constants.GPS_LONGITUDE, location.getLongitude());
                    replyIntent.putExtra(Constants.TOTAL_DISTANCE_TRAVELLED, mTotalDistance);
                    //replyIntent.putExtra("MedianAccelerometer", mAccelMedian);
                    if (mRecording) {
                        writeToLog(System.currentTimeMillis(), Constants.LOG_GPS_TAG, location.toString());
                    }
                    sendBroadcast(replyIntent);
                    //Log.v(Constants.DEBUG_TAG, "Sent");
                }
            }

    };


    /**
     * receives the intents broadcast by the SensorTags, and forwards data to the other fragments
     */
    private BroadcastReceiver mBtSensorConnectReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {


            Log.w(Constants.DEBUG_TAG, "adding devices to log " );
            for(int i = 0; i < Constants.MAX_BT_DEVICES; i++) {
                if (intent.hasExtra(Constants.BT_DEVICE_TAG + i)) {
                    BluetoothDevice device = intent.getParcelableExtra(Constants.BT_DEVICE_TAG + i);
                    String address = device.getAddress();
                    String alias = mDevicesDatabaseHelper.getAlias(address);

                    writeToLog(System.currentTimeMillis(), Constants.LOG_DEVICE_TAG,
                            device.getName() + "," +
                            address + "," +
                            alias);
                }
            }


        }

    };
    /**
     * receives the intents broadcast by the SensorTags, and forwards data to the other fragments
     */
    private BroadcastReceiver mBtSensorReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {


                //Log.w(Constants.DEBUG_TAG, "recieved Sensortag: " );
                if (mRecording) {
                    if (intent.hasExtra(Constants.BT_SENSORTAG_DATA)) {
                        writeToLog(intent.getLongExtra(Constants.BT_TIMESTAMP, 0), Constants.LOG_SENSORTAG_TAG,
                                intent.getStringExtra(Constants.BT_SENSORTAG_DATA)
                                        + intent.getStringExtra(Constants.BT_SENSOR_ADDRESS));
                    } else if (intent.hasExtra(Constants.BT_HEARTRATE_DATA)) {

                        writeToLog(intent.getLongExtra(Constants.BT_TIMESTAMP, 0), Constants.LOG_HEARTRATE_TAG,
                                "" + intent.getIntExtra(Constants.BT_HEARTRATE_DATA, 0) + ","
                                        + intent.getStringExtra(Constants.BT_SENSOR_ADDRESS));
                    }
                }

        }

    };

    /**
     * receives the intents broadcast by the GpsTracker, and forwards data to the other fragments
     */
    private BroadcastReceiver mControlDataReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getBooleanExtra(Constants.PAUSE_RECORDING, false)) {
                mRecording = false;
                Log.w(Constants.DEBUG_TAG, "Pause Recording intent received");
            }

            if (intent.getBooleanExtra(Constants.START_RECORDING, false)) {
                Log.w(Constants.DEBUG_TAG, "Start Recording intent received");
                mRecording = true;
            }
        }
    };

    /**
     * receives the intents broadcast by the AccelerometerManager, and forwards data to the other fragments
     */
    private BroadcastReceiver mAccelDataReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

                if (intent.getBooleanExtra(Constants.SERVICE_IS_DESTROYED, false)) {
                    Log.w(Constants.DEBUG_TAG, "Accelerometer service has ended!");
                } else {


                    Intent replyIntent = new Intent(Constants.RAW_DATA_REPLY);
                    AccelerometerData accelData = intent.getParcelableExtra(Constants.ACCEL_DATA);
                    if (mRecording) {
                        writeToLog(System.currentTimeMillis(), Constants.LOG_ACCEL_TAG, accelData.toString());
                    }
                    replyIntent.putExtra(Constants.ACCEL_X, accelData.getAccelX());
                    //mAccelXList.add(accelData.getAccelX());
                    replyIntent.putExtra(Constants.ACCEL_Y, accelData.getAccelY());
                    //mAccelYList.add(accelData.getAccelY());
                    replyIntent.putExtra(Constants.ACCEL_Z, accelData.getAccelZ());
                    //mAccelZList.add(accelData.getAccelZ());

                    sendBroadcast(replyIntent);
                }
            }

    };

    /**
     * receives the intents broadcast by the user interface, and acts on them
     */
    private BroadcastReceiver mInterfaceDataReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            if (mRecording) {
                if (intent.hasExtra(Constants.LOG_MARKER)) { //write the marker to the log
                    String markerString = intent.getStringExtra(Constants.LOG_MARKER);

                    if (intent.hasExtra(Constants.LOG_MARKER_IMAGE_PATH)) {
                        writeToLog(System.currentTimeMillis(),
                                Constants.LOG_MARKER_TAG,
                                markerString + "," +
                                        intent.getStringExtra(Constants.LOG_MARKER_IMAGE_PATH));
                    } else {
                        writeToLog(System.currentTimeMillis(),
                                Constants.LOG_MARKER_TAG,
                                markerString);
                    }
                }
            } else {
                Log.d(Constants.DEBUG_TAG, "Cannot add tag while not recording");
            }

            if (intent.hasExtra(Constants.FINALISE_WALK)) { //finish recording

                unregisterReceiver(mAccelDataReceiver);
                unregisterReceiver(mGPSDataReceiver);
                unregisterReceiver(mInterfaceDataReceiver);
                unregisterReceiver(mControlDataReceiver);
                unregisterReceiver(mBtSensorReceiver);
                unregisterReceiver(mBtSensorConnectReceiver);
                mWalk.setLength(mTotalDistance);
                Intent replyIntent = new Intent(Constants.FINALISE_WALK_REPLY);
                replyIntent.putExtra(Constants.WALK_TAG, mWalk);
                sendBroadcast(replyIntent);
                stopSelf(); //terminate the logWriter
            }

            if (intent.hasExtra(Constants.DISCARD_RECORDING)) { //discard recording - what a waste :(

                unregisterReceiver(mAccelDataReceiver);
                unregisterReceiver(mGPSDataReceiver);
                unregisterReceiver(mInterfaceDataReceiver);
                unregisterReceiver(mControlDataReceiver);
                unregisterReceiver(mBtSensorReceiver);
                unregisterReceiver(mBtSensorConnectReceiver);
                if (mLogFile.delete()) {
                    Log.d(Constants.DEBUG_TAG, "log file deleted");
                } else {
                    Log.e(Constants.DEBUG_TAG, "failed to delete log!");
                }
                Intent replyIntent = new Intent(Constants.FINALISE_WALK_REPLY);
                replyIntent.putExtra(Constants.DISCARD_RECORDING, true);
                sendBroadcast(replyIntent);

                stopSelf(); //terminate the logWriter
            }


        }
    };

    /**
     * Constructor initialises the location and accelerometer reading counter
     */
    public LogWriter() {
        mPreviousLocation = null;

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.v(Constants.DEBUG_TAG, "ALS: onStartCommand LogWriter Service");
        if (intent.hasExtra(Constants.WALK_TAG)) {
            mWalk = intent.getParcelableExtra(Constants.WALK_TAG);

            mTotalDistance = 0f; //set distance travelled to 0

            if (mWalk.getLogFilePath().equals(Constants.EMPTY_STRING)) {
                Log.d(Constants.DEBUG_TAG, "no log file found");
                mLogFile = initLogFile(mWalk);
                try {
                    mWalk.setLogFilePath(mLogFile.getAbsolutePath());
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
                Log.d(Constants.DEBUG_TAG, "Log file path: " + mWalk.getLogFilePath());
            }
        } else {
            Log.e(Constants.DEBUG_TAG, "no walk object sent to log writer!");
            stopSelf();
        }

        Log.d(Constants.DEBUG_TAG, "attempting to start gps service");

        Intent gpsServiceIntent = new Intent(this, GpsTracker.class);
        startService(gpsServiceIntent);
        Log.d(Constants.DEBUG_TAG, "GPS service started");

        //TODO: disable accelerometer when bt devices connected
        Log.d(Constants.DEBUG_TAG, "attempting to start Accelerometer service");
        Intent accelServiceIntent = new Intent(this, AccelerometerManager.class);
        startService(accelServiceIntent);


        mDevicesDatabaseHelper = new DevicesDatabaseHelper(getBaseContext());

        registerReceiver(mGPSDataReceiver, new IntentFilter(Constants.GPS_DATA_INTENT));
        registerReceiver(mAccelDataReceiver, new IntentFilter(Constants.ACCEL_DATA_INTENT));
        registerReceiver(mInterfaceDataReceiver, new IntentFilter(Constants.INTERFACE_INTENT));
        registerReceiver(mControlDataReceiver, new IntentFilter(Constants.CONTROL_DATA_REPLY));
        registerReceiver(mBtSensorReceiver, new IntentFilter(Constants.BT_SENSOR_DATA_INTENT));
        registerReceiver(mBtSensorConnectReceiver, new IntentFilter(Constants.SENSOR_CONNECT_INTENT));
        return START_STICKY;
    }


    @Override
    public void onDestroy() {
        Log.v(Constants.DEBUG_TAG, "ALM: onDestroy LogWriter Service");
        try {
            unregisterReceiver(mGPSDataReceiver);
            unregisterReceiver(mAccelDataReceiver);
            unregisterReceiver(mInterfaceDataReceiver);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }

        try {
            mLogFileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Intent replyIntent = new Intent(Constants.RAW_DATA_REPLY);
        replyIntent.putExtra(Constants.SERVICE_IS_DESTROYED, true);
        sendBroadcast(replyIntent);
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    /**
     * Writes a line to the log file
     * @param timestamp - current time in millis
     * @param dataTag - the type of data
     * @param data - the data
     */
    private void writeToLog(long timestamp, String dataTag, String data) {
        try {

            String logData = String.format(Constants.LOG_DATA_FORMAT, dataTag, timestamp, data);
            mLogFileWriter.append(logData);
            mLogFileWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Creates the log file and adds the header information
     * @param walk - the walk the log is for
     * @return - the log file object
     */
    private File initLogFile(Walk walk) {
        Log.w(Constants.DEBUG_TAG, "Initialising Log File: " + Utils.getFileNameString(walk.getName(), walk.getLocality(), Constants.LOG_FILE_EXTENSION));
        try {
            File root = new File(Environment.getExternalStorageDirectory(), Constants.LOG_FILE_DIRECTORY);
            if (!root.exists()) {
                if (root.mkdirs()) {
                    Log.d(Constants.DEBUG_TAG, "Log root did not exist. Created");
                } else {
                    Log.e(Constants.DEBUG_TAG, "Log root creation failed!");
                }
            }
            File logFile = new File(root, Utils.getFileNameString(walk.getName(), walk.getLocality(), Constants.LOG_FILE_EXTENSION));
            mLogFileWriter = new FileWriter(logFile);
            String logHeader = String.format(Constants.LOG_FILE_HEADER_FORMAT, walk.getName(), walk.getLocality(), walk.getDateSurveyed());
            mLogFileWriter.append(logHeader);
            mLogFileWriter.flush();

            return logFile;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * calculates the distance travelled since the last GPS fix, and returns it in meters
     * @param newLocation - the newest GPS fix
     * @return - the distance in meters
     */
    private Float getDistanceFromLastFix(Location newLocation) {
        Float returnValue;
        if (mPreviousLocation != null) {
            returnValue = mPreviousLocation.distanceTo(newLocation);
            mPreviousLocation = newLocation;
            return returnValue;
        } else {
            mPreviousLocation = newLocation;
            return 0f;
        }

    }

}


