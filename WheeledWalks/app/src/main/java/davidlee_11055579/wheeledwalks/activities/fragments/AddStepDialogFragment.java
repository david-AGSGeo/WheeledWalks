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
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import davidlee_11055579.wheeledwalks.R;
import davidlee_11055579.wheeledwalks.models.Constants;

/**
 * Created by David on 31/10/2015.
 * This Dialog Fragment shows options for adding a step marker to the map.
 * It allows the user to select the height of the step
 */
public class AddStepDialogFragment extends DialogFragment implements View.OnClickListener {

    private SeekBar mHeightSeekBar;

    /**
     * Sets up a new instance of the dialog, giving it the title passed to it, and returns the instance of the dialog
     *
     * @param title - the dialog title
     * @return the new dialog instance
     */
    public static AddStepDialogFragment newInstance(String title) {
        AddStepDialogFragment frag = new AddStepDialogFragment();

        Bundle args = new Bundle();
        args.putString("title", title);
        frag.setArguments(args);

        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.dialog_fragment_add_step, container, false);
        getDialog().setTitle("Add Step");
        Button addMarkerBtn = (Button) rootView.findViewById(R.id.add_step_dialog_add_marker_btn);
        addMarkerBtn.setOnClickListener(this);
        Button cancelBtn = (Button) rootView.findViewById(R.id.add_step_dialog_cancel_btn);
        cancelBtn.setOnClickListener(this);

        final TextView heightTv = (TextView) rootView.findViewById(R.id.add_step_dialog_height_label);

        mHeightSeekBar = (SeekBar) rootView.findViewById(R.id.add_step_dialog_height_seekbar);

        //set the textview to update whenever the SeekBar changes
        mHeightSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progressValue, boolean fromUser) {
                heightTv.setText(String.format("Height: %d cm", progressValue));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        return rootView;
    }

    /**
     * Handles the buttons in a switch statement
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case (R.id.add_step_dialog_add_marker_btn): //return the marker information to the map via intent
                Intent stepMarkerIntent = new Intent();
                stepMarkerIntent.putExtra(Constants.STEP_HEIGHT, mHeightSeekBar.getProgress());
                getTargetFragment().onActivityResult(getTargetRequestCode(),
                        Activity.RESULT_OK,
                        stepMarkerIntent);
                this.dismiss();
                break;
            case (R.id.add_step_dialog_cancel_btn): //exit
                this.dismiss();
                break;
            default:
                Log.e(Constants.DEBUG_TAG, "Button not handled correctly");
                break;
        }
    }
}
