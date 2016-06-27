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

package com.lee42.wheeledwalksviewer.activities.fragments;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.lee42.wheeledwalksviewer.R;
import com.lee42.wheeledwalksviewer.models.Constants;
import com.lee42.wheeledwalksviewer.models.TrackSegment;

import java.util.ArrayList;
import java.util.List;


import static com.google.android.gms.maps.GoogleMap.MAP_TYPE_SATELLITE;
import static com.google.android.gms.maps.GoogleMap.MAP_TYPE_TERRAIN;

/**
 * Created by David on 15/10/2015.
 * Custom google map fragment that is added at runtime to the mapfragment
 */
public class CustomMapFragment extends com.google.android.gms.maps.SupportMapFragment {

    private static final LatLng SYDNEY = new LatLng(-33.882992, 151.19893133);
    private static float sZoomLevel = 18f;
    private ArrayList<Marker> mStepMarkerList;
    private ArrayList<Marker> mObstacleMarkerList;
    private ArrayList<Polyline> mWalkPathList;
    private LatLng mStartLocation = null;
    private LatLng mMyLocation;
    private LatLng mLastLocation = null;
    private GoogleMap mMap;
    private int mObstacleCount;

    /**
     * Receive gps location data from GpsTracker service
     */
//    private BroadcastReceiver mGPSDataReceiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            Location location = intent.getParcelableExtra(Constants.GPS_LOCATION);
//
//
//            if (location != null) {
//                mMyLocation = new LatLng(location.getLatitude(),
//                        location.getLongitude());
//
//                if (mStartLocation == null) { //if this is the first gps location received
//                    mStartLocation = mMyLocation;
//                    mMap.addMarker(new MarkerOptions().position(mStartLocation).title("Walk Start"));
//                    mLastLocation = mStartLocation;
//                    centerMapOnLocation();
//                } else {
//                    centerMapOnLocation();
//                    plotRoute();
//                }
//            }
//        }
//    };


//    /**
//     * Starts the AddStepDialogFragment to collect data from the user, if there is GPS reception
//     */
//    public void addStep() {
//
//        if (mMyLocation != null) {
//            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
//            AddStepDialogFragment addStepFragmentDialog = AddStepDialogFragment
//                    .newInstance("Add_Step");
//            addStepFragmentDialog.setTargetFragment(this, Constants.STEP_MARKER_REQUEST_CODE);
//            addStepFragmentDialog.show(fragmentManager, "add_step_dialog_tag");
//        } else {
//            Utils.showToast(this.getContext(), "No GPS Signal");
//
//        }
//
//    }

//    /**
//     * Starts the AddObstacleDialogFragment to collect data from the user, if there is GPS reception
//     */
//    public void addObstacle() {
//        if (mMyLocation != null) {
//            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
//            AddObstacleDialogFragment addObstacleFragmentDialog = AddObstacleDialogFragment
//                    .newInstance("Add Obstacle");
//            addObstacleFragmentDialog.setTargetFragment(this, Constants.OBSTACLE_MARKER_REQUEST_CODE);
//            addObstacleFragmentDialog.show(fragmentManager, "add_obstacle_tag");
//        } else {
//            Utils.showToast(this.getContext(), "No GPS Signal");
//        }
//    }

    /**
     * called when one of the add marker dialogs returns a value. Adds the marker to the map and records it to the log file
     *
     * @param requestCode -whick marker?
     * @param resultCode  - is it successful?
     * @param data        - the intent containing the marker info
     */
//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//
//        if (requestCode == Constants.STEP_MARKER_REQUEST_CODE) {
//
//            if (resultCode == Activity.RESULT_OK) {
//                Marker marker = mMap.addMarker(new MarkerOptions()
//                        .position(mMyLocation)
//                        .title("Step")
//                        .snippet(String.format("Height: %d cm",
//                                data.getIntExtra(Constants.STEP_HEIGHT, 0)))
//                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_step2)));
//
//                mStepMarkerList.add(marker);
//                String markerString = markerToString(marker);
//                Intent logMarkerIntent = new Intent(Constants.INTERFACE_INTENT);
//                logMarkerIntent.putExtra(Constants.LOG_MARKER, markerString);
//                getContext().sendBroadcast(logMarkerIntent);
//            }
//        } else if (requestCode == Constants.OBSTACLE_MARKER_REQUEST_CODE) {
//            if (resultCode == Activity.RESULT_OK) {
//                mObstacleCount++;
//                String markerImageFilePath = Constants.EMPTY_STRING;
//                if (data.hasExtra(Constants.OBSTACLE_IMAGE)) {
//                    markerImageFilePath = Utils.saveToInternalStorage(
//                            ((Bitmap) data.getParcelableExtra(Constants.OBSTACLE_IMAGE)),
//                            Utils.getFileNameString("Obstacle", "" + mObstacleCount,
//                                    Constants.IMAGE_FILE_EXTENSION));
//                }
//
//                Marker marker = mMap.addMarker(new MarkerOptions()
//                        .position(mMyLocation)
//                        .title("Obstacle " + mObstacleCount)
//                        .snippet("Type: " + data.getStringExtra(Constants.OBSTACLE_TYPE))
//                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_obstacle)));
//
//                mObstacleMarkerList.add(marker);
//                String markerString = markerToString(marker);
//                Intent logMarkerIntent = new Intent(Constants.INTERFACE_INTENT);
//                logMarkerIntent.putExtra(Constants.LOG_MARKER, markerString);
//                if (!markerImageFilePath.equals(Constants.EMPTY_STRING)) {
//                    logMarkerIntent.putExtra(Constants.LOG_MARKER_IMAGE_PATH, markerImageFilePath);
//                }
//
//                getContext().sendBroadcast(logMarkerIntent);
//            }
//        }
//    }


    /**
     * Converts a marker object into a string for logging purposes
     *
     * @param marker - the marker
     * @return - the loggable string
     */
    private String markerToString(Marker marker) {
        String markerString;
        if (marker.getTitle().equals("Step")) {
            markerString = String.format("%s,%s,LAT:%f,LONG:%f",
                    marker.getTitle(),
                    marker.getSnippet(),
                    marker.getPosition().latitude,
                    marker.getPosition().longitude);
        } else {
            markerString = String.format("%s,%s,LAT:%f,LONG:%f",
                    marker.getTitle(),
                    marker.getSnippet(),
                    marker.getPosition().latitude,
                    marker.getPosition().longitude);
        }
        return markerString;
    }

    /**
     * Toggles the step markers on and off,
     * triggered by the ToggleButton on the mapFragment
     *
     * @param markersOn = boolean markers on or off
     */
    public void toggleStepMarkers(boolean markersOn) {

        if (markersOn) {
            for (int i = 0; i < mStepMarkerList.size(); i++) {
                mStepMarkerList.get(i).setVisible(true);
            }

        } else {
            for (int i = 0; i < mStepMarkerList.size(); i++) {
                mStepMarkerList.get(i).setVisible(false);
            }
        }
    }

    /**
     * Toggles the obstacle markers on and off,
     * triggered by the ToggleButton on the mapFragment
     *
     * @param markersOn = boolean markers on or off
     */
    public void toggleObstacleMarkers(boolean markersOn) {

        if (markersOn) {
            Log.v(Constants.DEBUG_TAG, "obstacles on");
            for (int i = 0; i < mObstacleMarkerList.size(); i++) {
                mObstacleMarkerList.get(i).setVisible(true);
            }

        } else {
            Log.v(Constants.DEBUG_TAG, "obstacles off");
            for (int i = 0; i < mObstacleMarkerList.size(); i++) {
                mObstacleMarkerList.get(i).setVisible(false);
            }
        }
    }

    /**
     * Toggles the path on and off,
     * triggered by the ToggleButton on the mapFragment
     *
     * @param pathOn = path on or off
     */
    public void togglePath(boolean pathOn) {

        if (pathOn) {
            Log.v(Constants.DEBUG_TAG, "path on");
            for (int i = 0; i < mWalkPathList.size(); i++) {

                mWalkPathList.get(i).setVisible(true);
            }

        } else {
            Log.v(Constants.DEBUG_TAG, "path off");
            for (int i = 0; i < mWalkPathList.size(); i++) {

                mWalkPathList.get(i).setVisible(false);
            }
        }
    }

    /**
     * Maintains the zoom level when a user changes it
     *
     * @return - the CameraChangeListener
     */
    public GoogleMap.OnCameraChangeListener getCameraChangeListener() {
        return new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition position) {
                sZoomLevel = position.zoom;
            }
        };
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.v(Constants.DEBUG_TAG, "ALM: onActivityCreated CustomMapFragment");
        mObstacleCount = 0;
        mMap = getMap();
        mMap.setMapType(MAP_TYPE_SATELLITE);  //Sattelite map type
        if (mMyLocation == null) { //if there is no current location stored
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(SYDNEY,    //set the start cursor to UTS :)
                    sZoomLevel));
            //initialise the marker and path arrays
            mStepMarkerList = new ArrayList<>();
            mObstacleMarkerList = new ArrayList<>();
            mWalkPathList = new ArrayList<>();
        } else {
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mMyLocation, sZoomLevel));
        }
        mMap.setOnCameraChangeListener(getCameraChangeListener());
        try {
            mMap.setMyLocationEnabled(true);
        }catch (SecurityException e){
            e.printStackTrace();
        }


        //getActivity().registerReceiver(mGPSDataReceiver, new IntentFilter(Constants.GPS_DATA_INTENT));
    }

    /**
     * Centers the map on the users location
     */
    private void centerMapOnLocation() {
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mMyLocation,
                sZoomLevel));
    }

    /**
     * plot a track of the walk on the map
     * track is made up of a number of seperate polylines for each small segment
     * this is easier to draw in realtime than a single larger polyline, and allows
     * for different colours for each polyline segment
     */
    public void plotRoute(List<TrackSegment> trackSegments, int gradientSource) {
        for (Polyline segment: mWalkPathList){
            segment.remove();
        }
        mWalkPathList.clear();

        int[] gradient = getResources().getIntArray(R.array.TrackGradient);
        for (TrackSegment segment: trackSegments){

            PolylineOptions track = new PolylineOptions();
            LatLng start = new LatLng(segment.getStartLattitude(),segment.getStartLongitude());
            LatLng end = new LatLng(segment.getEndLattitude(),segment.getEndLongitude());

            track.add(start);
            track.add(end);
            int segmentColour;
            switch (gradientSource){
                case 0:
                    segmentColour = gradient[segment.getDifficulty()];
                    break;
                case 1:
                    segmentColour = gradient[segment.getSteepness()];
                    break;
                case 2:
                    segmentColour = gradient[segment.getRoughness()];
                    break;
                case 3:
                    segmentColour = gradient[segment.getRoll()];
                    break;
                default:
                    segmentColour =  0xffffff;
                    break;
            }
            //TODO: get color based on difficulty
            //draw the line and add it to the path list
            mWalkPathList.add(mMap.addPolyline(track.color(segmentColour)
                    .width(5)));
        }
        //move map to start of track
        LatLng walkStart = new LatLng(trackSegments.get(0).getStartLattitude(),
                trackSegments.get(0).getStartLongitude());
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                walkStart,
                sZoomLevel));
    }

    //Lifecycle logging
    @Override
    public void onStop() {
        Log.v(Constants.DEBUG_TAG, "ALM: onStop CustomMapFragment");
        super.onStop();
    }

    @Override
    public void onPause() {
        Log.v(Constants.DEBUG_TAG, "ALM: onPause CustomMapFragment");
        super.onPause();
    }

    @Override
    public void onResume() {
        Log.v(Constants.DEBUG_TAG, "ALM: onResume CustomMapFragment");
        super.onResume();
    }

    @Override
    public void onStart() {
        Log.v(Constants.DEBUG_TAG, "ALM: onStart CustomMapFragment");
        super.onStart();
    }

    @Override
    public void onDestroy() {
        Log.v(Constants.DEBUG_TAG, "ALM: onDestroy CustomMapFragment");
        //getActivity().unregisterReceiver(mGPSDataReceiver);
        super.onDestroy();
    }
}

