package davidlee_11055579.wheeledwalks.activities.fragments;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;

import davidlee_11055579.wheeledwalks.R;
import davidlee_11055579.wheeledwalks.controllers.BtDeviceCardAdapter;
import davidlee_11055579.wheeledwalks.controllers.DevicesDatabaseHelper;
import davidlee_11055579.wheeledwalks.models.Constants;
import davidlee_11055579.wheeledwalks.utilities.Utils;

/**
 * Created by David on 19/04/2016.
 */
public class BluetoothSetupDialog extends DialogFragment implements View.OnClickListener,
        BluetoothAdapter.LeScanCallback {

    private Context mContext;
    private BluetoothAdapter mBluetoothAdapter;
    private SparseArray<BluetoothDevice> mDevices;

    private RecyclerView mDevicesRecyclerView;
    private BtDeviceCardAdapter mBtDevicesCardAdapter;
    private DevicesDatabaseHelper mDevicesDatabaseHelper;

    private ProgressBar mProgress;
    private Button mScanButton;
    private Button mExitButton;

    /**
     * receives the intents broadcast by the GpsTracker, and forwards data to the other fragments
     */
    private BroadcastReceiver mSensorConfigReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(Constants.DEBUG_TAG, "received reply from dialog");
            mBtDevicesCardAdapter.notifyDataSetChanged();
        }
    };



    public BluetoothSetupDialog() {
        // Empty constructor required for DialogFragment
    }


    public static BluetoothSetupDialog newInstance(String title) {
        BluetoothSetupDialog fragment = new BluetoothSetupDialog();


        Bundle args = new Bundle();
        args.putString("title", title);
        fragment.setArguments(args);

        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.dialog_fragment_bluetooth_setup, container, false);
        getDialog().setTitle("BlueTooth Setup");
        int width = getResources().getDimensionPixelSize(R.dimen.bluetooth_setup_dialog_width);
        int height = getResources().getDimensionPixelSize(R.dimen.bluetooth_setup_dialog_height);
        getDialog().getWindow().setLayout(width, height);
        mContext = getContext();

        /*
         * Bluetooth in Android 4.3 is accessed via the BluetoothManager, rather than
         * the old static BluetoothAdapter.getInstance()
         */
        BluetoothManager manager = (BluetoothManager) mContext.getSystemService(mContext.BLUETOOTH_SERVICE);
        mBluetoothAdapter = manager.getAdapter();

        mDevices = new SparseArray<BluetoothDevice>();

        /*
         * A progress dialog will be needed while the connection process is
         * taking place
         */
        mProgress = (ProgressBar) rootView.findViewById(R.id.bt_scan_progress);


        mDevicesRecyclerView = (RecyclerView) rootView.findViewById(R.id.bt_devices_recyclerview);
        mBtDevicesCardAdapter = new BtDeviceCardAdapter(mContext, mDevices);
        mDevicesRecyclerView.setAdapter(mBtDevicesCardAdapter);
        mDevicesRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mDevicesDatabaseHelper = new DevicesDatabaseHelper(getContext());


        mScanButton = (Button) rootView.findViewById
                (R.id.bt_dialog_scan_button);
        mScanButton.setOnClickListener(this);
        mExitButton = (Button) rootView.findViewById
                (R.id.bt_dialog_exit_btn);
        mExitButton.setOnClickListener(this);

        // Use this check to determine whether BLE is supported on the device. Then
        // you can selectively disable BLE-related features.
        SetupBluetooth();
        mContext.registerReceiver(mSensorConfigReceiver, new IntentFilter(Constants.SENSOR_CONFIG_INTENT));

        return rootView;
    }





    private void SetupBluetooth() {
        if (!getContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Utils.showToast(mContext, "Bluetooth LE not supported!");
        } else {
            Utils.showToast(mContext, "Bluetooth LE is supported!");
            final BluetoothManager bluetoothManager =
                    (BluetoothManager) mContext.getSystemService(mContext.BLUETOOTH_SERVICE);
            mBluetoothAdapter = bluetoothManager.getAdapter();

            // Ensures Bluetooth is available on the device and it is enabled. If not,
            // displays a dialog requesting user permission to enable Bluetooth.
            if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, Constants.BT_ENABLE_REQUEST_CODE);
            }
        }

    }

    /**
     * Handles the buttons in a switch statement
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case (R.id.bt_dialog_scan_button):
                mDevices.clear();
                startScan();
                break;

            case (R.id.bt_dialog_exit_btn): //exit the dialog
                //signal to connect bluetooth if anyone is listening (gets ignored if recordWalkActivity is not running)
                Intent replyIntent = new Intent(Constants.SENSOR_CONNECT_INTENT);
                for(int i = 0; i < mDevices.size(); i++) {
                    int key = mDevices.keyAt(i);
                    // get the object by the key.
                    BluetoothDevice device = mDevices.get(key);
                    mDevicesDatabaseHelper = new DevicesDatabaseHelper(this.getContext());
                    boolean isDeviceEnabled = false;
                    if (device != null && device.getName() != null) {
                        isDeviceEnabled = mDevicesDatabaseHelper.getSensorEnabled(device.getAddress());
                    }

                    if(isDeviceEnabled){
                        replyIntent.putExtra(Constants.BT_DEVICE_TAG + i, device);
                    }
                }
                v.getContext().sendBroadcast(replyIntent);
                this.dismiss();
                break;


            default:
                Log.e(Constants.DEBUG_TAG, "Button not handled correctly");
                break;
        }
    }


    private Runnable mStartRunnable = new Runnable() {
        @Override
        public void run() {
            startScan();
        }
    };
    private Runnable mStopRunnable = new Runnable() {
        @Override
        public void run() {
            stopScan();
        }
    };

    private void startScan() {
        mBluetoothAdapter.startLeScan(this);
        mScanButton.setEnabled(false);
        mExitButton.setEnabled(false);
        mProgress.setVisibility(View.VISIBLE);
        //mBtDevicesCardAdapter = new BtDeviceCardAdapter(mContext, mDevices);
        //mDevicesRecyclerView.setAdapter(mBtDevicesCardAdapter);
        mHandler.postDelayed(mStopRunnable, Constants.BLUETOOTH_SCAN_TIMEOUT_MILLIS);
    }

    private void stopScan() {
        mBluetoothAdapter.stopLeScan(this);
        mProgress.setVisibility(View.INVISIBLE);
        mScanButton.setEnabled(true);
        mExitButton.setEnabled(true);
    }



    /* BluetoothAdapter.LeScanCallback */

    @Override
    public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
        Log.i(Constants.BT_TAG, "New LE Device: " +
                device.getName() + " @ " +
                rssi + " Address: " +
                device.getAddress());

        //add all devices to the spinner regardless of type
        mDevices.put(device.hashCode(), device);
        //Update the devices RecyclerView
        mBtDevicesCardAdapter.notifyDataSetChanged();

    }

    @Override
    public void onStop() {
        mContext.unregisterReceiver(mSensorConfigReceiver);
        super.onStop();
    }



    /*
         * We have a Handler to process event results on the main thread
         */




    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

        }
    };
}