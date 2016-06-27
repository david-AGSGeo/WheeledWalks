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


import android.Manifest;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.firebase.client.Firebase;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import davidlee_11055579.wheeledwalks.R;
import davidlee_11055579.wheeledwalks.activities.fragments.SearchWalksDialogFragment;
import davidlee_11055579.wheeledwalks.controllers.CloudStorage;
import davidlee_11055579.wheeledwalks.controllers.DevicesDatabaseHelper;
import davidlee_11055579.wheeledwalks.controllers.NavDrawerAdapter;
import davidlee_11055579.wheeledwalks.controllers.WalkCardViewAdapter;
import davidlee_11055579.wheeledwalks.controllers.WalksDatabaseHelper;
import davidlee_11055579.wheeledwalks.models.BtDeviceDbConstants;
import davidlee_11055579.wheeledwalks.models.Constants;
import davidlee_11055579.wheeledwalks.models.Walk;
import davidlee_11055579.wheeledwalks.models.WalkDbConstants;
import davidlee_11055579.wheeledwalks.utilities.Utils;

/**
 * The starting screen of the app. Sets up the Navigation drawer and the walk card
 * recycler view, and handle database deletions and additions
 */

public class StartScreenActivity extends AppCompatActivity {

    private RecyclerView mCardRecyclerView; //the walk card list
    private DrawerLayout mNavDrawerLayout;
    private WalksDatabaseHelper mWalksDatabaseHelper;
    private DevicesDatabaseHelper mDevicesDatabaseHelper;
    private Button mClearSearchButton;
    private SharedPreferences mSharedPreferences;

    // Storage Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.v(Constants.DEBUG_TAG, "ALM: onCreate StartScreenActivity");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_screen);

        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        Toolbar mToolbar = (Toolbar) findViewById(R.id.tool_bar);   //add the action bar at the top
        setSupportActionBar(mToolbar);

        //Get permission to access internal storage
        int permission = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    this,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }


        //set up Nav Drawer
        RecyclerView mNavDrawerRecyclerView = (RecyclerView) findViewById
                (R.id.nav_drawer_recycler_view);
        mNavDrawerRecyclerView.setHasFixedSize(true);
        RecyclerView.Adapter mNavDrawerRecViewAdapter = new NavDrawerAdapter
                (Constants.NAV_DRAWER_TITLES, Constants.NAV_DRAWER_ICONS,
                        Constants.NAV_DRAWER_HEADER_TITLE, Constants.NAV_DRAWER_HEADER_SUBTEXT);
        mNavDrawerRecyclerView.setAdapter(mNavDrawerRecViewAdapter);
        RecyclerView.LayoutManager mNavDrawerRecViewLayoutManager = new LinearLayoutManager(this);
        mNavDrawerRecyclerView.setLayoutManager(mNavDrawerRecViewLayoutManager);
        mNavDrawerLayout = (DrawerLayout) findViewById(R.id.nav_drawer_layout);
        ActionBarDrawerToggle mNavDrawerToggle = new ActionBarDrawerToggle
                (this, mNavDrawerLayout, mToolbar, R.string.open_nav_drawer,
                        R.string.close_nav_drawer) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                // code here will execute once the drawer is opened
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                // Code here will execute once drawer is closed
            }


        };
        mNavDrawerLayout.setDrawerListener(mNavDrawerToggle);
        mNavDrawerToggle.syncState();



        //initialise Database Helpers
        mWalksDatabaseHelper = new WalksDatabaseHelper(this);
        mDevicesDatabaseHelper = new DevicesDatabaseHelper(this);
        if (!mDevicesDatabaseHelper.getAllDevices().moveToFirst()) { //check if cursor has any data
            Log.v(Constants.DEBUG_TAG, "Bluetooth Device Database Empty");
        }


        //set up Cloud access
        Firebase.setAndroidContext(this);
        //TODO: clear firebase database when necessary
        //Firebase deleteAllRef = new Firebase("https://wheeledwalks.firebaseio.com");
        //deleteAllRef.removeValue();
        //TODO: Delete Bluetooth device Db if necessary
        //getApplication().deleteDatabase(BtDeviceDbConstants.DATABASE_NAME);
        //TODO: Delete Walks Database if neccessary
        //getApplication().deleteDatabase(WalkDbConstants.DATABASE_NAME);



        if (mSharedPreferences.getBoolean(getString(R.string.preference_repopulate_database), false)) {
            populateWalkList(); //pre-populates database if empty and preference is set
        }

        //set up card list and adapter
        mCardRecyclerView = (RecyclerView) findViewById(R.id.walk_card_list);
        mCardRecyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mCardRecyclerView.setLayoutManager(linearLayoutManager);

        WalkCardViewAdapter walkCardViewAdapter = new WalkCardViewAdapter
                (this, mWalksDatabaseHelper.getAllWalks());
        mCardRecyclerView.setAdapter(walkCardViewAdapter);


        mClearSearchButton = (Button) findViewById(R.id.clear_search_button);
        mClearSearchButton.setVisibility(View.INVISIBLE);
        mClearSearchButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mClearSearchButton.setVisibility(View.INVISIBLE);
                WalkCardViewAdapter walkCardViewAdapter = new WalkCardViewAdapter
                        (v.getContext(), mWalksDatabaseHelper.getAllWalks());
                mCardRecyclerView.setAdapter(walkCardViewAdapter);
            }
        });


    }



    /**
     * Updates the walk card recyclerView when a new intent is received and/or
     * the database has changed
     *
     * @param intent the new intent
     */
    @Override
    protected void onNewIntent(Intent intent) {
        Log.v(Constants.DEBUG_TAG, "ALM: onNewIntent StartScreenActivity");
        mNavDrawerLayout.closeDrawers();
        if (intent.hasExtra(Constants.WALK_TAG)) { //a new walk has been received, add it to the database
            Utils.showToast(this, "New Walk Added!");
            Walk walk = intent.getParcelableExtra(Constants.WALK_TAG);
            mWalksDatabaseHelper.addWalk(walk);
            WalkCardViewAdapter walkCardViewAdapter = new WalkCardViewAdapter
                    (this, mWalksDatabaseHelper.getAllWalks());
            mCardRecyclerView.setAdapter(walkCardViewAdapter);
        }
        if (intent.hasExtra(Constants.WALK_DELETED)) { //a walk has been removed. refresh the card list
            Utils.showToast(this, "Walk Deleted!");
            WalkCardViewAdapter walkCardViewAdapter = new WalkCardViewAdapter
                    (this, mWalksDatabaseHelper.getAllWalks());
            mCardRecyclerView.setAdapter(walkCardViewAdapter);
        }

        if (intent.hasExtra(Constants.DELETE_ALL)) { //delete the database and all files
            if (mSharedPreferences.getBoolean(getString(R.string.preference_allow_delete_all), false)) {
                if (mSharedPreferences.getBoolean(getString(R.string.preference_confirm_deletions), false)) {
                    //display a delete all confirmation dialog
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setMessage(getString(R.string.confirm_delete_message))
                            .setCancelable(false)
                            .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    mWalksDatabaseHelper.close();
                                    getApplication().deleteDatabase(WalkDbConstants.DATABASE_NAME);
                                    File rootDir = new File(Environment
                                            .getExternalStorageDirectory(), Constants.ROOT_DIRECTORY);
                                    Utils.deleteRecursive(rootDir);
                                    Utils.showToast(getBaseContext(), "All walks Deleted!");
                                    mWalksDatabaseHelper = new WalksDatabaseHelper(getBaseContext());
                                    WalkCardViewAdapter walkCardViewAdapter = new WalkCardViewAdapter
                                            (getBaseContext(), mWalksDatabaseHelper.getAllWalks());
                                    mCardRecyclerView.setAdapter(walkCardViewAdapter);
                                    dialog.dismiss();
                                }
                            })
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });
                    AlertDialog alert = builder.create();
                    alert.show();
                } else {
                    mWalksDatabaseHelper.close();
                    getApplication().deleteDatabase(WalkDbConstants.DATABASE_NAME);
                    File rootDir = new File(Environment.getExternalStorageDirectory(), Constants.ROOT_DIRECTORY);
                    Utils.deleteRecursive(rootDir);
                    Utils.showToast(this, "All walks Deleted!");
                    mWalksDatabaseHelper = new WalksDatabaseHelper(this);
                    WalkCardViewAdapter walkCardViewAdapter = new WalkCardViewAdapter
                            (this, mWalksDatabaseHelper.getAllWalks());
                    mCardRecyclerView.setAdapter(walkCardViewAdapter);
                }
            } else {
                Utils.showToast(this, "This option is disabled: check your settings!");
            }
        }
        if (intent.hasExtra(Constants.WALK_CHANGED)) {
            Utils.showToast(this, "Walk Changes Saved!");
            WalkCardViewAdapter walkCardViewAdapter = new WalkCardViewAdapter
                    (this, mWalksDatabaseHelper.getAllWalks());
            mCardRecyclerView.setAdapter(walkCardViewAdapter);
        }
        if (intent.hasExtra(Constants.DISCARD_RECORDING)) {
            Utils.showToast(this, "Walk Recording Discarded!");

        }
        if (intent.hasExtra(Constants.EXPORT_WALK)) {

            String walkName = intent.getStringExtra(Constants.EXPORT_WALK);
            if (walkName == null) {
                Utils.showToast(this, "Walk card not found");
            } else {
                Walk exportedWalk = mWalksDatabaseHelper.getWalkObject(walkName);
                //Utils.showToast(this, "Exporting Walk named " + exportedWalk.getName() );
                exportWalk(exportedWalk);
            }

        }
        if (intent.hasExtra(Constants.SEARCH_STRING)) {
            String searchString = intent.getStringExtra(Constants.SEARCH_STRING);
            Cursor searchResult = mWalksDatabaseHelper.searchByNameOrLocation(searchString);
            if (searchResult.moveToFirst()) {
                mClearSearchButton.setVisibility(View.VISIBLE);
            } else { //no results found
                searchResult = mWalksDatabaseHelper.getAllWalks();
                Utils.showToast(this, "No matches found");
            }

            WalkCardViewAdapter walkCardViewAdapter = new WalkCardViewAdapter(this, searchResult);
            mCardRecyclerView.setAdapter(walkCardViewAdapter);
        }
    }

    private void exportWalk(Walk exportedWalk) {
        Firebase cloudReference =
                new Firebase("https://wheeledwalks.firebaseio.com/Raw_Walks");
        Firebase walkReference = cloudReference.child("Walks")
                .child(Utils.getFileNameString(
                exportedWalk.getName(),
                exportedWalk.getLocality(),
                ""));   //set subfolder for walk in firebase
                walkReference.setValue(exportedWalk);
        //upload walk object to firebase - isn't it easy?!
        File logFile = new File(exportedWalk.getLogFilePath());

        if (logFile.exists()) { //log file found: upload to Firebase asyncronously
                    new exportLogAsyncTask(logFile, walkReference.child("LogFile"))
                            .execute();
        } else {
            //no log file found! exit asyncTask without showing the dialog
            Utils.showToast(this, "No log file found");
            return;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_start_screen, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent i = new Intent(StartScreenActivity.this, SettingsActivity.class);
            startActivity(i);
            return true;
        }
        if (id == R.id.action_add_walk) {   //display a warning dialog letting the user know
            //that settings cannot be changed once a new walk
            // is started
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(getString(R.string.new_walk_warning_message))
                    .setCancelable(false)
                    .setPositiveButton("Continue", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Intent newWalkIntent = new Intent(StartScreenActivity.this, NewWalkActivity.class);
                            startActivity(newWalkIntent);

                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();

            return true;
        }
        if (id == R.id.action_search) { //show the search dialog
            FragmentManager fragmentManager = getSupportFragmentManager();
            SearchWalksDialogFragment searchWalksDialogFragment = SearchWalksDialogFragment
                    .newInstance(Constants.SEARCH_WALKS_DIALOG_TITLE);
            searchWalksDialogFragment.show(fragmentManager, Constants.SEARCH_DIALOG_FRAGMENT_TAG);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * adds a set of dummy walks to the database if it is empty.
     */
    public void populateWalkList() {

        if (!mWalksDatabaseHelper.getAllWalks().moveToFirst()) { //check if cursor has any data
            Walk walk1 = new Walk("Ettrema Gorge", 1293810, 12345.6f, 4.5f, 0, "Really cool walk with lots of swimming opportunities", "Morton NP", Constants.DEFAULT_IMAGE_PATH, "log path");
            mWalksDatabaseHelper.addWalk(walk1);
            walk1 = new Walk("Kanangra Main", 1293810, 5919.5f, 3.5f, 1, "Another Really cool walk, that is totally easy and you dont even need to carry 120m of ropes. All you need is a parachute!", "Kanangra Boyd NP", Constants.DEFAULT_IMAGE_PATH, "log path");
            mWalksDatabaseHelper.addWalk(walk1);
            walk1 = new Walk("Blue Rocks", 1293810, 20123.1f, 5.0f, 2, "Another Really cool walk, that is totally easy and you dont even need to carry 120m of ropes. All you need is a parachute!", "Gardens Of Stone NP", Constants.DEFAULT_IMAGE_PATH, "log path");
            mWalksDatabaseHelper.addWalk(walk1);
            walk1 = new Walk("Gooch's Crater", 1293810, 7453.8f, 3.0f, 3, "Another Really cool walk, that is totally easy and you dont even need to carry 120m of ropes. All you need is a parachute!", "Wollomi NP", Constants.DEFAULT_IMAGE_PATH, "log path");
            mWalksDatabaseHelper.addWalk(walk1);
            walk1 = new Walk("Mt Solitary", 1293810, 13332.5f, 2.5f, 4, "Another Really cool walk, that is totally easy and you dont even need to carry 120m of ropes. All you need is a parachute!", "Blue Mountains NP", Constants.DEFAULT_IMAGE_PATH, "log path");
            mWalksDatabaseHelper.addWalk(walk1);
            walk1 = new Walk("Three Peaks", 1293810, 90123.2f, 3.0f, 0, "Another Really cool walk, that is totally easy and you dont even need to carry 120m of ropes. All you need is a parachute!", "Blue Mountains NP", Constants.DEFAULT_IMAGE_PATH, "log path");
            mWalksDatabaseHelper.addWalk(walk1);
        }
    }


    //Lifecycle logging
    @Override
    protected void onStop() {
        Log.v(Constants.DEBUG_TAG, "ALM: onStop StartScreenActivity");
        super.onStop();
    }

    @Override
    protected void onPause() {
        Log.v(Constants.DEBUG_TAG, "ALM: onPause StartScreenActivity");
        super.onPause();
    }

    @Override
    protected void onResume() {
        Log.v(Constants.DEBUG_TAG, "ALM: onResume StartScreenActivity");
        super.onResume();
    }

    @Override
    protected void onStart() {
        Log.v(Constants.DEBUG_TAG, "ALM: onStart StartScreenActivity");
        super.onStart();
    }

    /**
     * Asyncronous log file exporter
     * Loads the log file one line at a time and publishes each line to the mLogTextView
     * each line is filtered and colour coded based on its tag
     */
    private class exportLogAsyncTask extends AsyncTask<Void, Void, Void> {

        private File mLogFile;
        private Firebase mWalkReference;
        private ProgressDialog mExportProgressDialog;

        public exportLogAsyncTask(File logFile, Firebase walkReference) {
            mLogFile = logFile;
            mWalkReference = walkReference;
        }

        /**
         * set up a progress Dialog
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mExportProgressDialog = new ProgressDialog(StartScreenActivity.this);
            mExportProgressDialog.setTitle("Exporting log file to Cloud");
            mExportProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mExportProgressDialog.show();
        }


        /**
         * loads the file into a buffer, and reads one line at a time until
         * the end of file or a filter changes
         *
         * @param params - void
         * @return publishes each line of the file with a colour attached
         */
        @Override
        protected Void doInBackground(Void... params) {

            int linesRead = 0;

            try {
                BufferedReader logBufferedReader =
                        new BufferedReader(new FileReader(mLogFile));
                String line;


                while ((line = logBufferedReader.readLine()) != null) {

                    if (isCancelled()) {
                        //mTaskFinished = true;
                        break;
                    }

                    if (!line.isEmpty()) {
                        String dataTag = ((line.split(","))[0]).substring(1);
                        //separate out the
                        // data tag by splitting the line on comma,
                        // and dropping the first "#" char
                        switch (dataTag) { //export raw data line by line
                            case Constants.LOG_ACCEL_TAG:
                            case Constants.LOG_GPS_TAG:
                            case Constants.LOG_HEARTRATE_TAG:
                            case Constants.LOG_SENSORTAG_TAG:
                            case Constants.LOG_DEVICE_TAG:
                                linesRead++;
                                mWalkReference.child(String.valueOf(linesRead))
                                        .setValue(line);

                                break;
                            case Constants.LOG_MARKER_TAG:
                                //TODO: set up marker exporting

                                break;
                            default: //header and footer - do not export

                                break;
                        }
                    }


                }
                logBufferedReader.close(); //close the buffer
                //TODO: get image upload working!
//                Log.d(Constants.DEBUG_TAG, "Uploading Images...");
//                CloudStorage cloudStorage = new CloudStorage();
//                try{
//                for(String bucket : cloudStorage.listBuckets()){
//                    Log.d(Constants.DEBUG_TAG, "bucket name: " + bucket);
//                }
//
//                }catch (Exception e){
//                    e.printStackTrace();
//                }

            } catch (IOException e) {
                //error reading file
                e.printStackTrace();
            }

            return null;
        }


        @Override
        protected void onPostExecute(Void aVoid) {
            //mTaskFinished = true;
            mExportProgressDialog.dismiss();
            super.onPostExecute(aVoid);

        }
    }

}
