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

package davidlee_11055579.wheeledwalks.activities.fragments;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import davidlee_11055579.wheeledwalks.R;
import davidlee_11055579.wheeledwalks.models.Constants;

/**
 * Created by David on 5/10/2015.
 * Shows statistics regarding the recorded walk data
 * contains the finalise button to stop recording and
 * start the finalise walk activity
 */
public class ControlFragment extends Fragment {

    private static final int RECORDING_STOPPED = 0;
    private static final int RECORDING_PAUSED = 1;
    private static final int RECORDING_STARTED = 2;

    private View mView;

    private TextView mGpsStatusTv;
    private TextView mBluetoothStatusTv;
    private ProgressDialog mBtConnectProgressDialog;
    private int mRecordingStatus = RECORDING_STOPPED;
    private int mNumDevices = 0;



    /**
     * Receives and displays the stats data from the services
     */
    private BroadcastReceiver mGpsStatusReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.hasExtra(Constants.GPS_LATTITUDE)) {
                mGpsStatusTv.setText
                        ("Connected");
            }

            mView.invalidate();
        }
    };

    private BroadcastReceiver mBluetoothConnectedReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, final Intent intent) {
            if (intent.hasExtra(Constants.BT_SENSOR_ADDRESS)) {
                String address = intent.getStringExtra(Constants.BT_SENSOR_ADDRESS);
                if (intent.getBooleanExtra(Constants.BT_SENSOR_CONNECTED, false)){
                    mNumDevices++;
                    mBtConnectProgressDialog.setMessage("Connected to device: " + address);
                    mBluetoothStatusTv.setText("Devices connected: " + mNumDevices);
                } else {
                    mBtConnectProgressDialog.setMessage("Connection to device: " + address + " failed!");
                }
            }
            if (intent.hasExtra(Constants.BT_CONNECTIONS_COMPLETE)) {
                mBtConnectProgressDialog.dismiss();
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mView = inflater.inflate(R.layout.fragment_ctrl, container, false);

        mGpsStatusTv = (TextView) mView.findViewById(R.id.ctrl_fragment_gps_status);
        mBluetoothStatusTv = (TextView) mView.findViewById(R.id.ctrl_fragment_bluetooth_status);
        mBluetoothStatusTv.setText("Devices connected: " + mNumDevices);

        //finish recording and save all data
        Button finaliseWalkBtn = (Button) mView.findViewById(R.id.stats_fragment_finalise_button);
        finaliseWalkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final View parentView = v;
                //Alert the user that this will stop recording
                AlertDialog.Builder builder = new AlertDialog.Builder(parentView.getContext());
                builder.setMessage(getString(R.string.finalise_walk_dialog_warning))
                        .setCancelable(false)
                        .setPositiveButton("Stop Recording", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //exit services and save data
                                Intent finaliseIntent = new Intent(Constants.INTERFACE_INTENT);
                                finaliseIntent.putExtra(Constants.FINALISE_WALK, true);
                                getContext().sendBroadcast(finaliseIntent);

                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });

        //Finish recording and delete all data
        Button discardWalkBtn = (Button) mView.findViewById(R.id.stats_fragment_discard_button);
        discardWalkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final View parentView = v;
                //Alert the user that this will stop recording
                AlertDialog.Builder builder = new AlertDialog.Builder(parentView.getContext());
                builder.setMessage(getString(R.string.discard_recording_alert_dialog_text))
                        .setCancelable(false)
                        .setPositiveButton("Discard Recording", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) { //let the services
                                // know to exit and delete all data
                                Intent discardIntent = new Intent(Constants.INTERFACE_INTENT);
                                discardIntent.putExtra(Constants.DISCARD_RECORDING, true);
                                getContext().sendBroadcast(discardIntent);

                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });

        final Button recordingControlBtn = (Button) mView.findViewById(R.id.ctrl_fragment_control_button);
        recordingControlBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final View parentView = v;
                switch (mRecordingStatus){
                    case RECORDING_STOPPED: //recording has not yet begun
                        recordingControlBtn.setText("Pause Recording");
                        recordingControlBtn.setBackgroundColor(getResources().getColor(R.color.pause_recording));
                        mRecordingStatus = RECORDING_STARTED;
                        Log.d(Constants.DEBUG_TAG, "Recording Started");
                        Intent startRecordingIntent = new Intent(Constants.CONTROL_DATA_REPLY);
                        startRecordingIntent.putExtra(Constants.START_RECORDING, true);
                        getContext().sendBroadcast(startRecordingIntent);
                        break;
                    case RECORDING_PAUSED: //recording has been started and then paused
                        recordingControlBtn.setText("Pause Recording");
                        recordingControlBtn.setBackgroundColor(getResources().getColor(R.color.pause_recording));
                        mRecordingStatus = RECORDING_STARTED;
                        Log.d(Constants.DEBUG_TAG, "Recording Resumed");
                        Intent resumeRecordingIntent = new Intent(Constants.CONTROL_DATA_REPLY);
                        resumeRecordingIntent.putExtra(Constants.START_RECORDING, true);
                        getContext().sendBroadcast(resumeRecordingIntent);
                        break;
                    case RECORDING_STARTED: //recording is currently running
                        recordingControlBtn.setText("Resume Recording");
                        recordingControlBtn.setBackgroundColor(getResources().getColor(R.color.start_recording));
                        mRecordingStatus = RECORDING_PAUSED;
                        Log.d(Constants.DEBUG_TAG, "Recording Paused");
                        Intent pauseRecordingIntent = new Intent(Constants.CONTROL_DATA_REPLY);
                        pauseRecordingIntent.putExtra(Constants.PAUSE_RECORDING, true);
                        getContext().sendBroadcast(pauseRecordingIntent);
                        break;
                }

            }
        });

        //set recording button colour
        switch (mRecordingStatus){
            case RECORDING_STOPPED: //recording has not yet begun
                recordingControlBtn.setText("START RECORDING");
                recordingControlBtn.setBackgroundColor(getResources().getColor(R.color.start_recording));

                break;
            case RECORDING_PAUSED: //recording has been started and then paused
                recordingControlBtn.setText("Resume Recording");
                recordingControlBtn.setBackgroundColor(getResources().getColor(R.color.start_recording));

                break;
            case RECORDING_STARTED: //recording is currently running
                recordingControlBtn.setText("Pause Recording");
                recordingControlBtn.setBackgroundColor(getResources().getColor(R.color.pause_recording));
                break;
        }

        final Button connectBluetoothBtn = (Button) mView.findViewById(R.id.ctrl_fragment_bluetooth_connect_button);
        connectBluetoothBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(Constants.DEBUG_TAG, "Configure Bluetooth Dialog started");
                FragmentManager fragmentManager = ((AppCompatActivity) getContext()).getSupportFragmentManager();
                BluetoothSetupDialog bluetoothSetupDialog = BluetoothSetupDialog
                        .newInstance(Constants.BLUETOOTH_SETUP_DIALOG_TITLE);
                bluetoothSetupDialog.show(fragmentManager, Constants.EDIT_DIALOG_FRAGMENT_TAG);

                mBtConnectProgressDialog = new ProgressDialog(getContext());
                mBtConnectProgressDialog.setTitle("Connecting to Bluetooth Devices");
                mBtConnectProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                mBtConnectProgressDialog.show();
            }
        });

        return mView;
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);

        // Check whether we're recreating a previously destroyed instance
        if (savedInstanceState != null) {
            // Restore value of members from saved state
            Log.d(Constants.DEBUG_TAG, "View Restored");

        } else {

        }

    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().registerReceiver(mGpsStatusReceiver,
                new IntentFilter(Constants.RAW_DATA_REPLY));
        getActivity().registerReceiver(mBluetoothConnectedReceiver, new IntentFilter(Constants.BT_SENSOR_CONNECTED_INTENT));

    }

    @Override
    public void onPause() {
        super.onStop();
        getActivity().unregisterReceiver(mGpsStatusReceiver);
        getActivity().unregisterReceiver(mBluetoothConnectedReceiver);
    }
}
