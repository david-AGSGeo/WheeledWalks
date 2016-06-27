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

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;

import davidlee_11055579.wheeledwalks.R;
import davidlee_11055579.wheeledwalks.models.Constants;

/**
 * Created by David on 31/10/2015.
 * This Dialog Fragment shows options for adding an obstacle marker to the map.
 * It allows the user to select an obstacle type, and optionally take a photo to store
 * with the tag for visual reference
 */
public class AddObstacleDialogFragment extends DialogFragment implements View.OnClickListener {

    private View mRootView;
    private Bitmap mObstacleImage;
    private Spinner mTypeSpinner;

    /**
     * Sets up a new instance of the dialog, giving it the title passed to it, and returns the instance of the dialog
     *
     * @param title - the dialog title
     * @return the new dialog instance
     */
    public static AddObstacleDialogFragment newInstance(String title) {
        AddObstacleDialogFragment frag = new AddObstacleDialogFragment();

        Bundle args = new Bundle();
        args.putString("title", title);
        frag.setArguments(args);

        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mRootView = inflater.inflate(R.layout.dialog_fragment_add_obstacle, container, false);
        getDialog().setTitle("Add Obstacle");


        Button addMarkerBtn = (Button) mRootView.findViewById(R.id.add_obstacle_dialog_add_marker_btn);
        addMarkerBtn.setOnClickListener(this);
        Button cancelBtn = (Button) mRootView.findViewById(R.id.add_obstacle_dialog_cancel_btn);
        cancelBtn.setOnClickListener(this);
        FloatingActionButton cameraButton = (FloatingActionButton) mRootView.findViewById
                (R.id.add_obstacle_dialog_camera_button);
        cameraButton.setOnClickListener(this);
        mTypeSpinner = (Spinner) mRootView.findViewById(R.id.add_obstacle_dialog_type_spinner);

        return mRootView;
    }


    /**
     * Handles the buttons in a switch statement
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case (R.id.add_obstacle_dialog_add_marker_btn): //Return the marker to the Map via intent
                Intent obstacleMarkerIntent = new Intent();
                obstacleMarkerIntent.putExtra(Constants.OBSTACLE_TYPE,
                        mTypeSpinner.getSelectedItem().toString());
                if (mObstacleImage != null) {
                    obstacleMarkerIntent.putExtra(Constants.OBSTACLE_IMAGE,
                            mObstacleImage);
                }
                getTargetFragment().onActivityResult(getTargetRequestCode(),
                        Activity.RESULT_OK,
                        obstacleMarkerIntent);
                this.dismiss();
                break;
            case (R.id.add_obstacle_dialog_camera_button): //take a photo
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, Constants.CAMERA_REQUEST_CODE);
                break;
            case (R.id.add_obstacle_dialog_cancel_btn): //return to the map
                this.dismiss();
                break;
            default:
                Log.e(Constants.DEBUG_TAG, "Button not handled correctly");
                break;
        }
    }

    /**
     * recieves the result of the camera activity (a bitmap) and shows it in the view
     *
     * @param requestCode - what was requested?
     * @param resultCode  - was it successful?
     * @param data        - the intent containing the photo
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.CAMERA_REQUEST_CODE) {
            mObstacleImage = (Bitmap) data.getExtras().get("data");
            ImageView image = (ImageView) mRootView.findViewById(R.id.add_obstacle_dialog_image);
            image.setImageBitmap(mObstacleImage);
        } else {
            //no picture taken, do nothing
        }

    }
}
