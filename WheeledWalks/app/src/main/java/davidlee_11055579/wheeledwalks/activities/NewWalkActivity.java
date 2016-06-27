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

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.Calendar;

import davidlee_11055579.wheeledwalks.R;
import davidlee_11055579.wheeledwalks.models.Constants;
import davidlee_11055579.wheeledwalks.models.Walk;
import davidlee_11055579.wheeledwalks.utilities.Utils;

/**
 * Created by David on 5/10/2015.
 * Takes in user input for setting up and recording a new walk,
 * and passes a walk object on to the recording activity.
 * checks for missing or duplicate information
 */
public class NewWalkActivity extends AppCompatActivity implements View.OnClickListener, View.OnFocusChangeListener {

    private Walk mWalk;

    private EditText mNameEt;
    private EditText mLocationEt;
    private TextView mLogFileName;

    private SharedPreferences mSharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.v(Constants.DEBUG_TAG, "ALM: onCreate NewWalkActivity");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_walk);
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mWalk = new Walk();

        mNameEt = (EditText) findViewById(R.id.new_walk_name_et);
        mLocationEt = (EditText) findViewById(R.id.new_walk_location_et);
        mLogFileName = (TextView) findViewById(R.id.new_walk_log_filename_tv);

        mNameEt.setOnFocusChangeListener(this);
        mLocationEt.setOnFocusChangeListener(this);


        Button cancelButton = (Button) findViewById(R.id.new_walk_cancel_btn);
        cancelButton.setOnClickListener(this);
        Button saveWalkButton = (Button) findViewById(R.id.new_walk_save_btn);
        saveWalkButton.setOnClickListener(this);
    }

    /**
     * Handles the buttons in a switch statement
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case (R.id.new_walk_cancel_btn):
                this.finish();  //exit to main menu

                break;
            case (R.id.new_walk_save_btn): //check inputs are correct and save to a new walk object

                if (mSharedPreferences.getBoolean(getString(R.string.preference_populate_new_walk), false)) {
                    testPopulate();
                }


                if (mNameEt.getText().toString().equals(Constants.EMPTY_STRING)) {
                    Toast.makeText(getApplicationContext(), "Please enter a walk name!", Toast.LENGTH_SHORT).show();
                    break;
                }
                if (mLocationEt.getText().toString().equals(Constants.EMPTY_STRING)) {
                    Toast.makeText(getApplicationContext(), "Please enter a walk location!", Toast.LENGTH_SHORT).show();
                    break;
                }
                mWalk.setName(mNameEt.getText().toString());
                mWalk.setLocality(mLocationEt.getText().toString());

                //test if logfile already exists
                File logPath = new File(Environment.getExternalStorageDirectory(),
                        Constants.LOG_FILE_DIRECTORY);
                File logFile = new File(logPath, mWalk.getName() +
                        "_" + mWalk.getLocality() + Constants.LOG_FILE_EXTENSION);
                if (logFile.exists()) {
                    Toast.makeText(getApplicationContext(), "A walk with this name and location already exists!", Toast.LENGTH_SHORT).show();
                    break;
                }
                //mWalk.setLogFilePath(mLogFileName.getText().toString());
                mWalk.setDateSurveyed(Calendar.getInstance().getTimeInMillis());

                //send walk object on to RecordWalkActivity
                Intent recordWalkIntent = new Intent(NewWalkActivity.this,
                        RecordWalkActivity.class);
                recordWalkIntent.putExtra(Constants.WALK_TAG, mWalk);
                startActivity(recordWalkIntent);
                break;

            default:
                Log.e(Constants.DEBUG_TAG, "Button not handled correctly");
                break;
        }
    }

    /**
     * updates the filename whenever the walk name or locality changes
     *
     * @param v
     * @param hasFocus
     */
    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (mNameEt.getText().length() > 0 && mLocationEt.getText().length() > 0) {
            //if both the name and location have been entered
            mLogFileName.setText(Utils.getFileNameString(
                    mNameEt.getText().toString(),
                    mLocationEt.getText().toString(),
                    Constants.LOG_FILE_EXTENSION));
        } else {
            mLogFileName.setText(getResources().getString(R.string.new_walk_log_file_hint));
        }
    }

    private void testPopulate() {

        boolean isOriginalWalk = false;
        String name = "";
        String locality = "";
        int iter = 1;
        File root = new File(Environment.getExternalStorageDirectory(),
                Constants.LOG_FILE_DIRECTORY);
        while (!isOriginalWalk) {
            //test if logfile already exists
            name = "Test " + iter;
            locality = "Walk " + iter;

            File logFile = new File
                    (root, Utils.getFileNameString(name, locality, Constants.LOG_FILE_EXTENSION));
            if (logFile.exists()) {
                iter++;
            } else {
                isOriginalWalk = true;
            }
        }
        mNameEt.setText(name);
        mLocationEt.setText(locality);
    }

    @Override
    protected void onStop() {
        Log.v(Constants.DEBUG_TAG, "ALM: onStop NewWalkActivity");
        super.onStop();
    }

    @Override
    protected void onPause() {
        Log.v(Constants.DEBUG_TAG, "ALM: onPause NewWalkActivity");
        super.onPause();
    }

    @Override
    protected void onResume() {
        Log.v(Constants.DEBUG_TAG, "ALM: onResume NewWalkActivity");
        super.onResume();
    }

    @Override
    protected void onStart() {
        Log.v(Constants.DEBUG_TAG, "ALM: onStart NewWalkActivity");
        super.onStart();
    }
}
