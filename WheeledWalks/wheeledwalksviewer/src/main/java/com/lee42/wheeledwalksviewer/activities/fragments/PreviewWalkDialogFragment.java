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
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.lee42.wheeledwalksviewer.R;
import com.lee42.wheeledwalksviewer.models.Constants;
import com.lee42.wheeledwalksviewer.models.TrackSegment;
import com.lee42.wheeledwalksviewer.models.Walk;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by David on 12/10/2015.
 * Dialog fragment to display and allow editing of a recorded walk
 */
public class PreviewWalkDialogFragment extends DialogFragment
        implements View.OnClickListener, CompoundButton.OnCheckedChangeListener{

    private static String sWalkName;
    private Walk mWalk;
    private CustomMapFragment mCustomMapFragment;
    private List<TrackSegment> mTrackSegments = new ArrayList<>();
    private int mGradientSource;

    public PreviewWalkDialogFragment() {
        // Empty constructor required for DialogFragment
    }

    public static PreviewWalkDialogFragment newInstance(String title, String walkName) {
        PreviewWalkDialogFragment frag = new PreviewWalkDialogFragment();

        sWalkName = walkName;
        Bundle args = new Bundle();
        args.putString("title", title);
        frag.setArguments(args);

        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.dialo_fragment_preview_walk, container, false);
        getDialog().setTitle(sWalkName);
        mCustomMapFragment = new CustomMapFragment();
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.add(R.id.preview_map_fragment_container, mCustomMapFragment).commit();

        ToggleButton stepToggleButton =
                (ToggleButton) rootView.findViewById(R.id.map_step_toggle_button);
        stepToggleButton.setOnCheckedChangeListener(this);
        ToggleButton obstacleToggleButton =
                (ToggleButton) rootView.findViewById(R.id.map_obstacle_toggle_button);
        obstacleToggleButton.setOnCheckedChangeListener(this);
        ToggleButton pathToggleButton =
                (ToggleButton) rootView.findViewById(R.id.map_path_toggle_button);
        pathToggleButton.setOnCheckedChangeListener(this);

        Spinner gradientSpinner = (Spinner) rootView.findViewById(R.id.preview_gradient_select_spinner);
        gradientSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                mGradientSource = position;
                if (mTrackSegments.size() != 0) {
                    mCustomMapFragment.plotRoute(mTrackSegments, mGradientSource);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });


        Firebase walkTrackRef = new Firebase("https://wheeledwalks.firebaseio.com/Processed/Walks/"
                + sWalkName + "/Track");
        walkTrackRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                for (DataSnapshot postSnapshot: snapshot.getChildren()) {
                    TrackSegment segment = postSnapshot.getValue(TrackSegment.class);
                    mTrackSegments.add(segment);
                }
                if (mTrackSegments.size() != 0) {
                    mCustomMapFragment.plotRoute(mTrackSegments, mGradientSource);
                }
            }
            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Log.d(Constants.DEBUG_TAG,"The track read failed: " + firebaseError.getMessage());
            }
        });

        return rootView;
    }

    /**
     * Handles the toggle buttons for toggling markers on and off
     *
     * @param buttonView = the toggled button
     * @param isChecked  = on or off
     */
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case (R.id.map_step_toggle_button): //add step tag
                mCustomMapFragment.toggleStepMarkers(isChecked);
                break;
            case (R.id.map_obstacle_toggle_button): //add obstacle tag
                mCustomMapFragment.toggleObstacleMarkers(isChecked);
                break;
            case (R.id.map_path_toggle_button): //add obstacle tag
                mCustomMapFragment.togglePath(isChecked);
                break;

            default:
                Log.e(Constants.DEBUG_TAG, "Button not handled correctly");
                break;
        }
    }


    /**
     * Handles the buttons in a switch statement
     */
    @Override
    public void onClick(View v) {
//        switch (v.getId()) {
//            case (R.id.edit_activity_walk_image):
//                changeThumbnailImage();
//                break;
//
//            case (R.id.edit_activity_Delete_walk_btn): //delete the walk if preferences set
//                SharedPreferences sharedPreferences = PreferenceManager.
//                        getDefaultSharedPreferences(getActivity().getBaseContext());
//
//                if (sharedPreferences.getBoolean(getString(R.string.preference_allow_walk_delete), false)) {
//                    if (sharedPreferences.getBoolean(getString(R.string.preference_confirm_deletions), false)) {
//                        //display a deletion confirmation dialog
//                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
//                        builder.setMessage(getString(R.string.confirm_delete_message))
//                                .setCancelable(false)
//                                .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
//                                    public void onClick(DialogInterface dialog, int id) {
//
//                                        deleteThisWalk();
//                                        Intent deleteWalkIntent = new Intent(getContext(), StartScreenActivity.class);
//                                        deleteWalkIntent.putExtra(Constants.WALK_DELETED, true);
//                                        startActivity(deleteWalkIntent);
//                                        getDialog().dismiss();
//                                    }
//                                })
//                                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//                                    public void onClick(DialogInterface dialog, int id) {
//                                        dialog.cancel();
//                                    }
//                                });
//                        AlertDialog alert = builder.create();
//                        alert.show();
//                    } else {
//                        deleteThisWalk();
//                        Intent deleteWalkIntent = new Intent(this.getContext(), StartScreenActivity.class);
//                        deleteWalkIntent.putExtra(Constants.WALK_DELETED, true);
//                        startActivity(deleteWalkIntent);
//                        this.dismiss();
//                    }
//
//                } else {
//                    Utils.showToast(getContext(), "Unable to delete: check your settings!");
//                }
//
//                break;
//            case (R.id.edit_activity_view_log_btn): //show the log view dialog
//                //start asyncTask to read logfile
//                new LogFileViewerDialog(this.getContext(), mWalk.getLogFilePath());
//                break;
//            case (R.id.edit_activity_save_changes_btn): //save the walk changes if they are valid
//
//
//                if (getWalkChanges()) {
//                    mWalksDatabaseHelper.deleteWalk(mOldWalkName);
//                    mWalksDatabaseHelper.addWalk(mWalk);
//                    Intent saveChangesIntent = new Intent(this.getContext(), StartScreenActivity.class);
//                    saveChangesIntent.putExtra(Constants.WALK_CHANGED, true);
//                    startActivity(saveChangesIntent);
//                    this.dismiss();
//                }
//
//                break;
//            case (R.id.edit_activity_discard_changes_btn): //return to the start screen
//
//                this.dismiss();
//                break;
//
//            default:
//                Log.e(Constants.DEBUG_TAG, "Button not handled correctly");
//                break;
//        }
    }
}
