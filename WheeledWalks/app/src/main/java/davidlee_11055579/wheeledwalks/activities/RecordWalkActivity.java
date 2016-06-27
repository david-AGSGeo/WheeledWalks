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

package davidlee_11055579.wheeledwalks.activities;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Message;
import android.os.PowerManager;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.WindowManager;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import davidlee_11055579.wheeledwalks.R;
import davidlee_11055579.wheeledwalks.activities.fragments.RawDataFragment;
import davidlee_11055579.wheeledwalks.activities.fragments.ControlFragment;
import davidlee_11055579.wheeledwalks.activities.fragments.WalkMapFragment;
import davidlee_11055579.wheeledwalks.models.Constants;
import davidlee_11055579.wheeledwalks.models.PolarH7Constants;
import davidlee_11055579.wheeledwalks.models.SensorTagConstants;
import davidlee_11055579.wheeledwalks.models.Walk;
import davidlee_11055579.wheeledwalks.services.LogWriter;
import davidlee_11055579.wheeledwalks.utilities.PolarH7Utils;
import davidlee_11055579.wheeledwalks.utilities.SensorTagUtils;

/**
 * Created by David on 5/10/2015.
 * Holds the fragmentmanager that handles the tabbed fragments.
 * starts the logWriter
 */
public class RecordWalkActivity extends AppCompatActivity {

    public static final int SENSORTAG = 1;
    public static final int HEARTRATE = 2;
    public static FragmentManager mFragmentManager;
    private Walk mWalk;

    private List<BluetoothGatt> mConnectedGatt;
    private boolean mBtConnectionComplete;

    //needed for keeping recording going while screen is locked
    private PowerManager.WakeLock mWakeLock;

    private android.os.Handler mHandler = new android.os.Handler() {
        @Override
        public void handleMessage(Message msg) {

        }
    };

    /**
     * Receive the finalise walk broadcast from the stats tab and pass the walk on to the
     * FinaliseWalk Activity
     */
    private BroadcastReceiver mFinaliseReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.hasExtra(Constants.WALK_TAG)) {
                mWalk = intent.getParcelableExtra(Constants.WALK_TAG);

                Intent finaliseIntent = new Intent(getApplicationContext(),
                        FinaliseWalkActivity.class);
                finaliseIntent.putExtra(Constants.WALK_TAG, mWalk);
                startActivity(finaliseIntent);
            } else if (intent.hasExtra(Constants.DISCARD_RECORDING)) {

                Intent startScreenIntent = new Intent(getApplicationContext(),
                        StartScreenActivity.class);
                startScreenIntent.putExtra(Constants.DISCARD_RECORDING, true);
                startActivity(startScreenIntent);
            }
            //release the wakelock so that the screen can be locked again
            mWakeLock.release();
        }
    };

    private BroadcastReceiver mConnectBluetoothReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, final Intent intent) {


            Log.d(Constants.BT_TAG, "Connecting Gatt Callbacks...");
            //check if any devices exist, and if not, signal close dialog and exit
            if (!intent.hasExtra(Constants.BT_DEVICE_TAG + 0)) {
                Intent connectedIntent = new Intent(Constants.BT_SENSOR_CONNECTED_INTENT);
                connectedIntent.putExtra(Constants.BT_CONNECTIONS_COMPLETE, true);
                sendBroadcast(connectedIntent);
                return;
            }


            for (int i = 0; i < Constants.MAX_BT_DEVICES; i++) { //loop through intent to connect devices

                if (intent.hasExtra(Constants.BT_DEVICE_TAG + i)) {
                    //check if this is the last device
                    final boolean isLastDevice = !(intent.hasExtra(Constants.BT_DEVICE_TAG + (i+1)));
                    final BluetoothDevice device = intent.getParcelableExtra("BtDevice" + i);
                    Log.d(Constants.BT_TAG, "got device with address: " + device.getAddress());
                    mHandler.postDelayed(new Runnable() {
                        public void run() {
                            connectDevice(device, isLastDevice);
                        }
                    }, 2000 * i);

                }

            }
            //finished all connections


        }

    };




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.v(Constants.DEBUG_TAG, "ALM: onCreate RecordWalkActivity");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_walk);

        //display this activity before the lockscreen so as not to interrupt recording
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        //settings to make the app record even when locked
        try {
            PowerManager powerManager = (PowerManager) getSystemService(StartScreenActivity.POWER_SERVICE);
            mWakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                    getString(R.string.wake_lock_label));
            mWakeLock.acquire();
        } catch (SecurityException e) {
            e.printStackTrace();
        }

        Log.d(Constants.DEBUG_TAG, "RecordWalkActivity OnCreate started");
        if (getIntent().hasExtra(Constants.WALK_TAG)) {
            mWalk = getIntent().getParcelableExtra(Constants.WALK_TAG);
            Log.d(Constants.DEBUG_TAG, "received walk parcel: " + mWalk.getName());

            Intent logWriterIntent = new Intent(this, LogWriter.class);
            logWriterIntent.putExtra(Constants.WALK_TAG, mWalk);
            startService(logWriterIntent);
        } else {
            Log.e(Constants.DEBUG_TAG, "no walk parcel sent to RecordWalkActivity" + mWalk.getName());
        }

        ((TextView) findViewById(R.id.record_walk_activity_title)).setText(mWalk.getName());
        ViewPager mViewPager = (ViewPager) findViewById(R.id.record_viewpager);
        setupViewPager(mViewPager);

        TabLayout mTabLayout = (TabLayout) findViewById(R.id.record_tabs);
        mTabLayout.setupWithViewPager(mViewPager);

        mConnectedGatt = new ArrayList<>();
    }

    /**
     * Adds fragments to the tabbed view pager
     *
     * @param viewPager - the pager to add the fragments to
     */
    private void setupViewPager(ViewPager viewPager) {
        mFragmentManager = getSupportFragmentManager();
        ViewPagerAdapter adapter = new ViewPagerAdapter(mFragmentManager);
        adapter.addFragment(new RawDataFragment(), Constants.TAB_HEADER_RAW_DATA);
        adapter.addFragment(new WalkMapFragment(), Constants.TAB_HEADER_MAP);
        adapter.addFragment(new ControlFragment(), Constants.TAB_HEADER_CONTROL);
        viewPager.setAdapter(adapter);
    }


    /**
     * Override tha back button so that it does not interrupt recording
     */
    @Override
    public void onBackPressed() {
        //moveTaskToBack(true); //do not go back!
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Walk is recording. Save or Discard walk to exit to main menu")
                .setCancelable(true)

                .setNegativeButton("Back", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    protected void onStart() {
        Log.v(Constants.DEBUG_TAG, "ALM: onStart RecordWalkActivity");
        //register receiver here to avoid it being unregistered and not reconnected

        registerReceiver(mFinaliseReceiver, new IntentFilter(Constants.FINALISE_WALK_REPLY));
        registerReceiver(mConnectBluetoothReceiver, new IntentFilter(Constants.SENSOR_CONNECT_INTENT));
        super.onStart();
    }
    @Override
    protected void onStop() {
        Log.v(Constants.DEBUG_TAG, "ALM: onStop RecordWalkActivity");
        unregisterReceiver(mFinaliseReceiver);
        unregisterReceiver(mConnectBluetoothReceiver);
        for (BluetoothGatt gatt : mConnectedGatt){
            gatt.disconnect();
            gatt.close();
        }
        super.onStop();
    }

    @Override
    protected void onPause() {
        Log.v(Constants.DEBUG_TAG, "ALM: onPause RecordWalkActivity");
        super.onPause();
    }

    @Override
    protected void onResume() {
        Log.v(Constants.DEBUG_TAG, "ALM: onResume RecordWalkActivity");
        super.onResume();

        try {
            mWakeLock.acquire();
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }



    /**
     * This is the Tab layout view pager that handles and populates the tabbed fragments
     */
    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {

            super(manager);
        }

        @Override
        public Fragment getItem(int position) {

            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {

            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {

            return mFragmentTitleList.get(position);
        }
    }


    private void connectDevice(BluetoothDevice device, final boolean isLastDevice){

        BluetoothGattCallback callback = new BluetoothGattCallback() {

            private boolean sensorEnabled = false;
            private boolean sensorPeriodSet = false;

            private BluetoothDevice connectedDevice;
            private String deviceAddress;
            private int deviceType;



            @Override
            public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
                Log.d(Constants.BT_TAG, "Connection State Change: "+status+" -> "+connectionState(newState));
                if (status == BluetoothGatt.GATT_SUCCESS && newState == BluetoothProfile.STATE_CONNECTED) {
                /*
                 * Once successfully connected, we must next discover all the services on the
                 * device before we can read and write their characteristics.
                 */

                    connectedDevice = gatt.getDevice();
                    switch (connectedDevice.getName()){
                        case "CC2650 SensorTag":
                            deviceType = SENSORTAG;
                            Log.d(Constants.DEBUG_TAG, "Connecting Sensortag device");
                            break;
                        case "Polar H7 3958881C":
                            deviceType = HEARTRATE;
                            Log.d(Constants.DEBUG_TAG, "Connecting Heartrate device");
                            break;
                        default:
                            deviceType = 0;
                            Log.d(Constants.DEBUG_TAG, "Unknown device");
                            break;
                    }
                    deviceAddress = connectedDevice.getAddress();
                    Log.d(Constants.BT_TAG, "Discovering Services on device " + deviceAddress + " :...");
                    gatt.discoverServices();
                } else if (status == BluetoothGatt.GATT_SUCCESS && newState == BluetoothProfile.STATE_DISCONNECTED) {
                    Log.w(Constants.BT_TAG, "Device Disconnected");
                    Intent connectedIntent = new Intent(Constants.BT_SENSOR_CONNECTED_INTENT);
                    connectedIntent.putExtra(Constants.BT_SENSOR_ADDRESS, deviceAddress);
                    connectedIntent.putExtra(Constants.BT_SENSOR_CONNECTED, false);
                    sendBroadcast(connectedIntent);
                } else if (status != BluetoothGatt.GATT_SUCCESS) {
                    Log.w(Constants.BT_TAG, "Communication Failure!");
                    Intent connectedIntent = new Intent(Constants.BT_SENSOR_CONNECTED_INTENT);
                    connectedIntent.putExtra(Constants.BT_SENSOR_ADDRESS, deviceAddress);
                    connectedIntent.putExtra(Constants.BT_SENSOR_CONNECTED, false);
                    sendBroadcast(connectedIntent);
                    gatt.disconnect();
                }
            }

            @Override
            public void onServicesDiscovered(BluetoothGatt gatt, int status) {
                Log.d(Constants.BT_TAG, "Services Discovered: Status = "+ status);

                if (status == BluetoothGatt.GATT_SUCCESS) {

                    List<BluetoothGattService> services = gatt.getServices();

                    //print services to log
                    for (BluetoothGattService service : services){
                        Log.d(Constants.BT_TAG, "found service UUID:" + service.getUuid());
                    }

                    enableSensor(gatt);

                }else {
                    Log.w(Constants.BT_TAG, "No Services Discovered: status = " + status);
                    Intent connectedIntent = new Intent(Constants.BT_SENSOR_CONNECTED_INTENT);
                    connectedIntent.putExtra(Constants.BT_SENSOR_ADDRESS, deviceAddress);
                    connectedIntent.putExtra(Constants.BT_SENSOR_CONNECTED, false);
                    sendBroadcast(connectedIntent);
                    gatt.disconnect();
                }

            }

            /*
             * Send an enable command to the movement sensor by writing a configuration
             * characteristic.  This is specific to the SensorTag to keep power
             * low by disabling sensors you aren't using.
             */
            private void enableSensor(BluetoothGatt gatt) {

                if(deviceType == SENSORTAG) {
                    if (!sensorEnabled) {
                        Log.d(Constants.BT_TAG, "Enabling SensorTag...");
                        BluetoothGattCharacteristic characteristic;
                        characteristic = gatt.getService(SensorTagConstants.MOVEMENT_SERVICE)
                                .getCharacteristic(SensorTagConstants.MOVEMENT_CONFIG_CHAR);
                        characteristic.setValue(new byte[]{0b01111111, 0b00000010}); //enable all sensors and disable wake on shake, set range to 8G
                        gatt.writeCharacteristic(characteristic);
                    } else {
                        sensorPeriodSet = true;
                        Log.d(Constants.BT_TAG, "setting Sensor Period");
                        BluetoothGattCharacteristic characteristic;

                        characteristic = gatt.getService(SensorTagConstants.MOVEMENT_SERVICE)
                                .getCharacteristic(SensorTagConstants.MOVEMENT_PERIOD_CHAR);
                        characteristic.setValue(new byte[]{0x0A}); //set period to 100ms
                        gatt.writeCharacteristic(characteristic);
                    }
                } else if(deviceType == HEARTRATE){
                    Log.d(Constants.BT_TAG, "Enabling HeartRate Sensor...");
                    //Heart rate sensor cannot be read implicitly, so we just turn on notifications
                    setNotifySensor(gatt);

                }
            }

            /*
                     * once the config characteristic is written, read the sensor
                     */
            @Override
            public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
                //After writing the enable flag, next we read the initial value
                if(deviceType == SENSORTAG) {
                    if (!sensorPeriodSet) {
                        sensorEnabled = true;
                        enableSensor(gatt);
                    } else {
                        readSensor(gatt);
                    }
                } else if(deviceType == HEARTRATE){
                    //do nothing here - should never be called

                }
            }

            /*
             * Attempt to read the data characteristic's value for the movement sensor explicitly
             */
            private void readSensor(BluetoothGatt gatt) {
                BluetoothGattCharacteristic characteristic;
                if (deviceType == SENSORTAG) {
                    Log.v(Constants.BT_TAG, "attempting to read Movement Data");
                    characteristic = gatt.getService(SensorTagConstants.MOVEMENT_SERVICE)
                            .getCharacteristic(SensorTagConstants.MOVEMENT_DATA_CHAR);
                    gatt.readCharacteristic(characteristic);
                }else if(deviceType == HEARTRATE){
                    //heart rate is never read implicitly
                }

            }

            @Override
            public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
                //For each read, pass the data up to the UI thread to update the display
                if (SensorTagConstants.MOVEMENT_DATA_CHAR.equals(characteristic.getUuid())) {

                    String formattedData = SensorTagUtils.ConvertMovementDataToString(characteristic.getValue());
                    Log.v(Constants.BT_TAG, "Read Data: " + formattedData + deviceAddress);

                    Intent connectedIntent = new Intent(Constants.BT_SENSOR_CONNECTED_INTENT);
                    connectedIntent.putExtra(Constants.BT_SENSOR_ADDRESS, deviceAddress);
                    connectedIntent.putExtra(Constants.BT_SENSOR_CONNECTED, true);
                    sendBroadcast(connectedIntent);
                } else if (PolarH7Constants.HEART_RATE_MEASUREMENT.equals(characteristic.getUuid())){
                    //heart rate is never read explicitly - should not get here :)
                } else {
                    Log.w(Constants.BT_TAG, "unknown UUID: " + characteristic.getUuid());
                }


                //After reading the initial value, next we enable notifications
                setNotifySensor(gatt);
            }

            /*
             * Enable notification of changes on the data characteristic for each sensor
             * by writing the ENABLE_NOTIFICATION_VALUE flag to that characteristic's
             * configuration descriptor.
             */
            private void setNotifySensor(BluetoothGatt gatt) {
                BluetoothGattCharacteristic characteristic;
                if (deviceType == SENSORTAG){
                    characteristic = gatt.getService(SensorTagConstants.MOVEMENT_SERVICE)
                            .getCharacteristic(SensorTagConstants.MOVEMENT_DATA_CHAR);
                    //Enable local notifications
                    gatt.setCharacteristicNotification(characteristic, true);
                    //Enabled remote notifications
                    BluetoothGattDescriptor desc = characteristic.getDescriptor(SensorTagConstants.CONFIG_DESCRIPTOR);
                    desc.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                    gatt.writeDescriptor(desc);
                } else if (deviceType == HEARTRATE){
                    Intent connectedIntent = new Intent(Constants.BT_SENSOR_CONNECTED_INTENT);
                    connectedIntent.putExtra(Constants.BT_SENSOR_ADDRESS, deviceAddress);
                    connectedIntent.putExtra(Constants.BT_SENSOR_CONNECTED, true);
                    sendBroadcast(connectedIntent);

                    characteristic = gatt.getService(PolarH7Constants.HEART_RATE_SERVICE)
                            .getCharacteristic(PolarH7Constants.HEART_RATE_MEASUREMENT);
                    gatt.setCharacteristicNotification(characteristic, true);

                    BluetoothGattDescriptor desc = characteristic.getDescriptor(PolarH7Constants.HEART_RATE_CONFIG);
                    desc.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                    gatt.writeDescriptor(desc);
                }

            }

            @Override
            public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
                if (isLastDevice){
                    Intent connectedIntent = new Intent(Constants.BT_SENSOR_CONNECTED_INTENT);
                    connectedIntent.putExtra(Constants.BT_CONNECTIONS_COMPLETE, true);
                    sendBroadcast(connectedIntent);
                }
            }

            @Override
            public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            /*
             * After notifications are enabled, all updates from the device on characteristic
             * value changes will be posted here.  Similar to read, we hand these up to the
             * UI thread to update the display.
             */

                if (SensorTagConstants.MOVEMENT_DATA_CHAR.equals(characteristic.getUuid())) {
                    //mHandler.sendMessage(Message.obtain(null, MSG_HUMIDITY, characteristic));
                    String formattedData = SensorTagUtils.ConvertMovementDataToString(characteristic.getValue());
                    //Log.v(Constants.BT_TAG, "Changed Movement Data: " + formattedData + deviceAddress);
                    Intent dataIntent = new Intent(Constants.BT_SENSOR_DATA_INTENT);
                    dataIntent.putExtra(Constants.BT_SENSOR_ADDRESS, deviceAddress);
                    dataIntent.putExtra(Constants.BT_SENSORTAG_DATA, formattedData);
                    dataIntent.putExtra(Constants.BT_TIMESTAMP, System.currentTimeMillis());
                    sendBroadcast(dataIntent);
                } else if (PolarH7Constants.HEART_RATE_MEASUREMENT.equals(characteristic.getUuid())){
                    int heartRate = PolarH7Utils.getHeartRate(characteristic);
                    //Log.v(Constants.DEBUG_TAG, String.format("Received heart rate in Changed: %d", heartRate));
                    Intent dataIntent = new Intent(Constants.BT_SENSOR_DATA_INTENT);
                    dataIntent.putExtra(Constants.BT_SENSOR_ADDRESS, deviceAddress);
                    dataIntent.putExtra(Constants.BT_HEARTRATE_DATA, heartRate);
                    dataIntent.putExtra(Constants.BT_TIMESTAMP, System.currentTimeMillis());
                    sendBroadcast(dataIntent);
                    //
                }

            }



            @Override
            public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
                Log.d(Constants.BT_TAG, "Remote RSSI: " + rssi);
            }



            private String connectionState(int status) {
                switch (status) {
                    case BluetoothProfile.STATE_CONNECTED:
                        return "Connected";
                    case BluetoothProfile.STATE_DISCONNECTED:
                        return "Disconnected";
                    case BluetoothProfile.STATE_CONNECTING:
                        return "Connecting";
                    case BluetoothProfile.STATE_DISCONNECTING:
                        return "Disconnecting";
                    default:
                        return String.valueOf(status);
                }
            }
        };

        BluetoothGatt connectedGatt = device.connectGatt(getBaseContext(), false, callback);
        connectedGatt.connect();
        mConnectedGatt.add(connectedGatt);

    }


}

