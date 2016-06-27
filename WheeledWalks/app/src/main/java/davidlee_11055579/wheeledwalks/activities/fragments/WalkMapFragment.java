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

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ToggleButton;

import davidlee_11055579.wheeledwalks.R;
import davidlee_11055579.wheeledwalks.models.Constants;


/**
 * Created by David on 5/10/2015.
 * Tab fragment containing the map and buttons to interact with it
 */
public class WalkMapFragment extends android.support.v4.app.Fragment
        implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {


    private CustomMapFragment mCustomMapFragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (container == null) {
            return null;
        }
        View tabView = inflater.inflate(R.layout.fragment_walk_map, container, false);
        mCustomMapFragment = new CustomMapFragment();
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.add(R.id.walk_map_fragment_container, mCustomMapFragment).commit();

        Button addStepBtn = (Button) tabView.findViewById(R.id.map_add_step_btn);
        addStepBtn.setOnClickListener(this);
        Button addObstacleBtn = (Button) tabView.findViewById(R.id.map_add_obstacle_btn);
        addObstacleBtn.setOnClickListener(this);

        ToggleButton stepToggleButton =
                (ToggleButton) tabView.findViewById(R.id.map_step_toggle_button);
        stepToggleButton.setOnCheckedChangeListener(this);
        ToggleButton obstacleToggleButton =
                (ToggleButton) tabView.findViewById(R.id.map_obstacle_toggle_button);
        obstacleToggleButton.setOnCheckedChangeListener(this);
        ToggleButton pathToggleButton =
                (ToggleButton) tabView.findViewById(R.id.map_path_toggle_button);
        pathToggleButton.setOnCheckedChangeListener(this);

        return tabView;
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
        switch (v.getId()) {
            case (R.id.map_add_step_btn): //add step tag
                mCustomMapFragment.addStep();
                break;
            case (R.id.map_add_obstacle_btn): //add obstacle tag
                mCustomMapFragment.addObstacle();
                break;

            default:
                Log.e(Constants.DEBUG_TAG, "Button not handled correctly");
                break;
        }
    }

    //Lifecycle logging
    @Override
    public void onStop() {
        Log.v(Constants.DEBUG_TAG, "ALM: onStop WalkMapFragment");
        super.onStop();
    }

    @Override
    public void onPause() {
        Log.v(Constants.DEBUG_TAG, "ALM: onPause WalkMapFragment");
        super.onPause();
    }

    @Override
    public void onResume() {
        Log.v(Constants.DEBUG_TAG, "ALM: onResume WalkMapFragment");
        super.onResume();
    }

    @Override
    public void onStart() {
        Log.v(Constants.DEBUG_TAG, "ALM: onStart WalkMapFragment");
        super.onStart();
    }

}

