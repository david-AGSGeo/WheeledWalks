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

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import davidlee_11055579.wheeledwalks.R;
import davidlee_11055579.wheeledwalks.controllers.DevicesDatabaseHelper;
import davidlee_11055579.wheeledwalks.models.Constants;

/**
 * Created by David on 5/10/2015.
 * Displays raw data received from the sensors via intent
 */
public class RawDataFragment extends Fragment {


    private TextView mLattitudeTv;
    private TextView mLongitudeTv;
    private TableLayout mDataTable;

    private View mView;

    private boolean mAccelEnabled = true;
    private List<String> btAddresses = new ArrayList<>();

    /**
     * Receives and displays the raw data
     */
    private BroadcastReceiver mRawDataReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getBooleanExtra(Constants.SERVICE_IS_DESTROYED, false)) {
                Log.w(Constants.DEBUG_TAG, "LogWriter service has ended!");
            } else {
                if (intent.hasExtra(Constants.GPS_LATTITUDE)) {
                    mLattitudeTv.setText
                            (Double.toString(intent.getDoubleExtra(Constants.GPS_LATTITUDE, 0.0)));
                }
                if (intent.hasExtra(Constants.GPS_LONGITUDE)) {
                    mLongitudeTv.setText
                            (Double.toString(intent.getDoubleExtra(Constants.GPS_LONGITUDE, 0.0)));
                }

                if (intent.hasExtra(Constants.ACCEL_X)) {

                    TableRow tRow = (TableRow) mDataTable.findViewWithTag("Accelerometer");
                    if (tRow != null) {
                        ((TextView) tRow.findViewWithTag("accelX")).setText(
                                String.format(Constants.ACCEL_DISPLAY_FORMAT,
                                        intent.getFloatExtra(Constants.ACCEL_X, 0)));
                        ((TextView) tRow.findViewWithTag("accelY")).setText(
                                String.format(Constants.ACCEL_DISPLAY_FORMAT,
                                        intent.getFloatExtra(Constants.ACCEL_Y, 0)));
                        ((TextView) tRow.findViewWithTag("accelZ")).setText(
                                String.format(Constants.ACCEL_DISPLAY_FORMAT,
                                        intent.getFloatExtra(Constants.ACCEL_Z, 0)));
                    }

                }

            }
        }
    };

    private BroadcastReceiver mBluetoothConnectedReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, final Intent intent) {
            if (intent.hasExtra(Constants.BT_SENSOR_ADDRESS)) {
                String address = intent.getStringExtra(Constants.BT_SENSOR_ADDRESS);
                if (intent.getBooleanExtra(Constants.BT_SENSOR_CONNECTED, false)){
                    btAddresses.add(address);
                    mAccelEnabled = false;
                } else {
                    //sensor did not connect!
                }
            }

        }
    };

    /**
     * receives the intents broadcast by the SensorTags, and forwards data to the other fragments
     */
    private BroadcastReceiver mBtSensorDataReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {


            if (intent.hasExtra(Constants.BT_SENSORTAG_DATA)) {
                String data =intent.getStringExtra(Constants.BT_SENSORTAG_DATA);
                String address =  intent.getStringExtra(Constants.BT_SENSOR_ADDRESS);

                TableRow tRow = (TableRow) mDataTable.findViewWithTag(address);
                if (tRow != null) {
                    ((TextView) tRow.findViewWithTag("accelX")).setText(
                            (data.split(","))[3]);
                    ((TextView) tRow.findViewWithTag("accelY")).setText(
                            (data.split(","))[4]);
                    ((TextView) tRow.findViewWithTag("accelZ")).setText(
                            (data.split(","))[5]);
                }

            } else if (intent.hasExtra(Constants.BT_HEARTRATE_DATA)) {

                int data =intent.getIntExtra(Constants.BT_HEARTRATE_DATA, 0);
                //Log.v(Constants.DEBUG_TAG, "received Heartrate Data:" +  data);
                String address =  intent.getStringExtra(Constants.BT_SENSOR_ADDRESS);

                TableRow tRow = (TableRow) mDataTable.findViewWithTag(address);
                if (tRow != null) {
                    ((TextView) tRow.findViewWithTag("accelX")).setText("" +
                            data);

                }

            }

        }

    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().registerReceiver(mRawDataReceiver,
                new IntentFilter(Constants.RAW_DATA_REPLY));
        getActivity().registerReceiver(mBtSensorDataReceiver,
                new IntentFilter(Constants.BT_SENSOR_DATA_INTENT));




    }

    @Override
    public void onPause() {
        super.onStop();
        getActivity().unregisterReceiver(mRawDataReceiver);
        getActivity().unregisterReceiver(mBtSensorDataReceiver);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(mBluetoothConnectedReceiver);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_raw_data, container, false);
        mLattitudeTv = (TextView) mView.findViewById(R.id.raw_gps_lattitude_tv);
        mLongitudeTv = (TextView) mView.findViewById(R.id.raw_gps_longitude_tv);
        mDataTable = (TableLayout) mView.findViewById(R.id.raw_data_table_layout);
        getActivity().registerReceiver(mBluetoothConnectedReceiver, new IntentFilter(Constants.BT_SENSOR_CONNECTED_INTENT));
        getActivity().registerReceiver(mBtSensorDataReceiver, new IntentFilter(Constants.BT_SENSOR_DATA_INTENT));

        Log.d(Constants.DEBUG_TAG, "Creating Table Row");

        if (mAccelEnabled) {
            mDataTable.addView(createDataRow(mView.getContext(),
                    "Accelerometer",
                   "Accelerometer"));
        } else {

            DevicesDatabaseHelper devicesDatabaseHelper = new DevicesDatabaseHelper(mView.getContext());
            for (String address : btAddresses) {
                mDataTable.addView(createDataRow(mView.getContext(),
                        devicesDatabaseHelper.getAlias(address),
                        address
                        ));
            }


        }

        return mView;
    }

    private TableRow createDataRow(Context context, String name, String tag){

        TableRow newRow = new TableRow(context);
        newRow.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT));
        newRow.setTag(tag);
        TextView nameTextView = new TextView(context);
        nameTextView.setTextSize(18.0f);
        nameTextView.setText(name);
        nameTextView.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 2f));
        nameTextView.setBackgroundColor(0xdcdcdc);
        newRow.addView(nameTextView);
        TextView accelXTextView = new TextView(context);
        accelXTextView.setTag("accelX");
        accelXTextView.setTextSize(18.0f);
        accelXTextView.setText("NC");
        accelXTextView.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f));
        newRow.addView(accelXTextView);
        TextView accelYTextView = new TextView(context);
        accelYTextView.setTag("accelY");
        accelYTextView.setTextSize(18.0f);
        accelYTextView.setText("NC");
        accelYTextView.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f));
        newRow.addView(accelYTextView);
        TextView accelZTextView = new TextView(context);
        accelZTextView.setTag("accelZ");
        accelZTextView.setTextSize(18.0f);
        accelZTextView.setText("NC");
        accelZTextView.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f));
        newRow.addView(accelZTextView);
        return newRow;
    }
}
