package davidlee_11055579.wheeledwalks.services;

import android.app.Service;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothProfile;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.SystemClock;
import android.util.Log;

import java.util.ArrayList;

import java.util.logging.LogRecord;

import davidlee_11055579.wheeledwalks.models.Constants;
import davidlee_11055579.wheeledwalks.models.SensorTagConstants;

/**
 * Created by David on 27/04/2016.
 */
public class BtSensorManager extends Service{


    ArrayList<BluetoothDevice> mDeviceList;
    private BluetoothGatt mConnectedGatt1;


    /**
     * Runs when the service is started, and sets up the sensor listeners
     * @param intent - the starting intent
     * @param flags - ???
     * @param startId - ???
     * @return - a flag with the type of service
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.v(Constants.DEBUG_TAG, "ALM: onStartCommand BtSensorManager Service");

        if (mDeviceList == null){
            Log.v(Constants.BT_TAG, "Bluetooth Sensor Manager started");
            mDeviceList = new ArrayList<>();
        }

        if (intent.hasExtra(Constants.DEVICE_TAG)) {
            BluetoothDevice newDevice = intent.getParcelableExtra(Constants.DEVICE_TAG);
            Boolean isNew = true;
            for (BluetoothDevice device : mDeviceList){
                if (newDevice.getAddress().equals(device.getAddress())){
                    isNew = false;
                    Log.d(Constants.BT_TAG, "device already connected");
                }
            }
            if(isNew) {
                mDeviceList.add(newDevice);
                Log.d(Constants.BT_TAG, "Added device parcel: " +
                        newDevice.getAddress());
                //TODO: try different context?
                mConnectedGatt1 = newDevice.connectGatt(this,false,mGattCallback1);
                Log.d(Constants.BT_TAG, "trying to connect... ");
                mConnectedGatt1.connect();
                Log.d(Constants.BT_TAG, "...connected");
            }
        }
        Log.d(Constants.BT_TAG, "Connected Devices: ");
        for (BluetoothDevice device : mDeviceList){
            Log.d(Constants.BT_TAG, device.getAddress());
        }



        return START_STICKY; //restart the service if the phone terminates it due to lack of memory
    }

    /*
     * In this callback, we've created a bit of a state machine to enforce that only
     * one characteristic be read or written at a time until all of our sensors
     * are enabled and we are registered to get notifications.
     */
    private final BluetoothGattCallback mGattCallback1 = new BluetoothGattCallback() {

        /* State Machine Tracking */
        private int mState = 0;

        private void reset() { mState = 0; }

        private void advance() { mState++; }

        /*
         * Send an enable command to each sensor by writing a configuration
         * characteristic.  This is specific to the SensorTag to keep power
         * low by disabling sensors you aren't using.
         */
        private void enableNextSensor(BluetoothGatt gatt) {
            BluetoothGattCharacteristic characteristic;
            switch (mState) {
                case 0:
                    Log.d(Constants.BT_TAG, "Enabling Accelerometer");
                    characteristic = gatt.getService(SensorTagConstants.ACCELEROMETER_SERVICE)
                            .getCharacteristic(SensorTagConstants.ACCELEROMETER_CONFIG_CHAR);
                    characteristic.setValue(new byte[] {0x01});
                    break;
                case 1:
                    Log.d(Constants.BT_TAG, "Enabling Gyroscope");
                    characteristic = gatt.getService(SensorTagConstants.GYROSCOPE_SERVICE)
                            .getCharacteristic(SensorTagConstants.GYROSCOPE_CONFIG_CHAR);
                    characteristic.setValue(new byte[] {0x01});
                    break;
                case 2:
                    Log.d(Constants.BT_TAG, "Enabling humidity");
                    characteristic = gatt.getService(SensorTagConstants.HUMIDITY_SERVICE)
                            .getCharacteristic(SensorTagConstants.HUMIDITY_CONFIG_CHAR);
                    characteristic.setValue(new byte[] {0x01});
                    break;
                default:
                    //finished enabling sensors
                    Log.i(Constants.BT_TAG, "All Sensors Enabled");
                    return;
            }

            gatt.writeCharacteristic(characteristic);
        }

        /*
         * Read the data characteristic's value for each sensor explicitly
         */
        private void readNextSensor(BluetoothGatt gatt) {
            BluetoothGattCharacteristic characteristic;
            switch (mState) {
                case 0:
                    Log.d(Constants.BT_TAG, "Reading Accelerometer ");
                    characteristic = gatt.getService(SensorTagConstants.ACCELEROMETER_SERVICE)
                            .getCharacteristic(SensorTagConstants.ACCELEROMETER_DATA_CHAR);
                    break;
                case 1:
                    Log.d(Constants.BT_TAG, "Reading Gyroscope");
                    characteristic = gatt.getService(SensorTagConstants.GYROSCOPE_SERVICE)
                            .getCharacteristic(SensorTagConstants.GYROSCOPE_DATA_CHAR);
                    break;
                case 2:
                    Log.d(Constants.BT_TAG, "Reading humidity");
                    characteristic = gatt.getService(SensorTagConstants.HUMIDITY_SERVICE)
                            .getCharacteristic(SensorTagConstants.HUMIDITY_DATA_CHAR);
                    break;
                default:

                    Log.i(Constants.BT_TAG, "All Sensors Enabled");
                    return;
            }

            gatt.readCharacteristic(characteristic);
        }

        /*
         * Enable notification of changes on the data characteristic for each sensor
         * by writing the ENABLE_NOTIFICATION_VALUE flag to that characteristic's
         * configuration descriptor.
         */
        private void setNotifyNextSensor(BluetoothGatt gatt) {
            BluetoothGattCharacteristic characteristic;
            switch (mState) {
                case 0:
                    Log.d(Constants.BT_TAG, "Set notify Accelerometer");
                    characteristic = gatt.getService(SensorTagConstants.ACCELEROMETER_SERVICE)
                            .getCharacteristic(SensorTagConstants.ACCELEROMETER_DATA_CHAR);
                    break;
                case 1:
                    Log.d(Constants.BT_TAG, "Set notify Gyroscope");
                    characteristic = gatt.getService(SensorTagConstants.GYROSCOPE_SERVICE)
                            .getCharacteristic(SensorTagConstants.GYROSCOPE_DATA_CHAR);
                    break;
                case 2:
                    Log.d(Constants.BT_TAG, "Set notify humidity");
                    characteristic = gatt.getService(SensorTagConstants.HUMIDITY_SERVICE)
                            .getCharacteristic(SensorTagConstants.HUMIDITY_DATA_CHAR);
                    break;
                default:

                    Log.i(Constants.BT_TAG, "All Sensors Enabled");
                    return;
            }

            //Enable local notifications
            gatt.setCharacteristicNotification(characteristic, true);
            //Enabled remote notifications
            BluetoothGattDescriptor desc = characteristic.getDescriptor(SensorTagConstants.CONFIG_DESCRIPTOR);
            desc.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
            gatt.writeDescriptor(desc);
        }

        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            Log.d(Constants.BT_TAG, "Connection State Change: "+status+" -> "+connectionState(newState));
            if (status == BluetoothGatt.GATT_SUCCESS && newState == BluetoothProfile.STATE_CONNECTED) {
                /*
                 * Once successfully connected, we must next discover all the services on the
                 * device before we can read and write their characteristics.
                 */
                Log.d(Constants.BT_TAG, "Discovering Services...");
                gatt.discoverServices();
                SystemClock.sleep(3000);

               // mHandler.sendMessage(Message.obtain(null, MSG_PROGRESS, "Discovering Services..."));
            } else if (status == BluetoothGatt.GATT_SUCCESS && newState == BluetoothProfile.STATE_DISCONNECTED) {
                Log.w(Constants.BT_TAG, "Device Disconnected");
            } else if (status != BluetoothGatt.GATT_SUCCESS) {
                Log.w(Constants.BT_TAG, "Communication Failure!");
                gatt.disconnect();
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            Log.d(Constants.BT_TAG, "Services Discovered: "+ status);
            Log.d(Constants.BT_TAG, "Enabling Sensors...");
            if (status != 0) {
                reset();
                enableNextSensor(gatt);
            }else {
                Log.w(Constants.BT_TAG, "No Services Discovered");
                gatt.disconnect();
            }

        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            //For each read, pass the data up to the UI thread to update the display
            if ((SensorTagConstants.ACCELEROMETER_DATA_CHAR).equals(characteristic.getUuid())) {
                //mHandler.sendMessage(Message.obtain(null, MSG_HUMIDITY, characteristic));
                Log.v(Constants.BT_TAG, "Accelerometer Raw: " + characteristic.getStringValue(0));
            }
            if ((SensorTagConstants.GYROSCOPE_DATA_CHAR).equals(characteristic.getUuid())) {
                //mHandler.sendMessage(Message.obtain(null, MSG_PRESSURE, characteristic));
                Log.v(Constants.BT_TAG, "Gyroscope Raw: " + characteristic.getStringValue(0));
            }
            if ((SensorTagConstants.HUMIDITY_DATA_CHAR).equals(characteristic.getUuid())) {
                //mHandler.sendMessage(Message.obtain(null, MSG_PRESSURE_CAL, characteristic));
                Log.v(Constants.BT_TAG, "Humidity Raw: " + characteristic.getStringValue(0));
            }

            //After reading the initial value, next we enable notifications
            setNotifyNextSensor(gatt);
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            //After writing the enable flag, next we read the initial value
            readNextSensor(gatt);
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            /*
             * After notifications are enabled, all updates from the device on characteristic
             * value changes will be posted here.  Similar to read, we hand these up to the
             * UI thread to update the display.
             */
            if (SensorTagConstants.ACCELEROMETER_DATA_CHAR.equals(characteristic.getUuid())) {
                //mHandler.sendMessage(Message.obtain(null, MSG_HUMIDITY, characteristic));
                Log.v(Constants.BT_TAG, "Accelerometer Raw: " + characteristic.getStringValue(0));
            }
            if (SensorTagConstants.GYROSCOPE_DATA_CHAR.equals(characteristic.getUuid())) {
                //mHandler.sendMessage(Message.obtain(null, MSG_PRESSURE, characteristic));
                Log.v(Constants.BT_TAG, "Gyroscope Raw: " + characteristic.getStringValue(0));
            }
            if (SensorTagConstants.HUMIDITY_DATA_CHAR.equals(characteristic.getUuid())) {
                //mHandler.sendMessage(Message.obtain(null, MSG_PRESSURE_CAL, characteristic));
                Log.v(Constants.BT_TAG, "Humidity Raw: " + characteristic.getStringValue(0));
            }
        }

        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            //Once notifications are enabled, we move to the next sensor and start over with enable
            advance();
            enableNextSensor(gatt);
        }

        @Override
        public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
            Log.d(Constants.BT_TAG, "Remote RSSI: "+rssi);
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


    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public void onDestroy() {
        Log.v(Constants.DEBUG_TAG, "ALM: onDestroy BtSensorManager Service");
        if (mConnectedGatt1 != null) {
            mConnectedGatt1.disconnect();
            mConnectedGatt1 = null;
        }
        super.onDestroy();
    }


}
