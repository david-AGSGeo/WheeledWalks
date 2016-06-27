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
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.File;

import davidlee_11055579.wheeledwalks.R;
import davidlee_11055579.wheeledwalks.activities.StartScreenActivity;
import davidlee_11055579.wheeledwalks.controllers.WalksDatabaseHelper;
import davidlee_11055579.wheeledwalks.models.Constants;
import davidlee_11055579.wheeledwalks.models.Walk;
import davidlee_11055579.wheeledwalks.utilities.Utils;

/**
 * Created by David on 12/10/2015.
 * Dialog fragment to display and allow editing of a recorded walk
 */
public class EditWalkDialogFragment extends DialogFragment implements View.OnClickListener {

    private static String sWalkName;
    private Walk mWalk;
    private WalksDatabaseHelper mWalksDatabaseHelper;
    private String mOldWalkName;

    private EditText mWalkNameEt;
    private RatingBar mWalkRatingRb;
    private TextView mWalkGradeText;
    private SeekBar mGradeSeekBar;
    private EditText mWalkDescriptionEt;
    private EditText mWalkLengthEt;
    private EditText mWalkLocalityEt;
    private ImageButton mwalkThumbnailIb;

    public EditWalkDialogFragment() {
        // Empty constructor required for DialogFragment
    }

    public static EditWalkDialogFragment newInstance(String title, String walkName) {
        EditWalkDialogFragment frag = new EditWalkDialogFragment();

        sWalkName = walkName;
        Bundle args = new Bundle();
        args.putString("title", title);
        frag.setArguments(args);

        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.dialog_fragment_edit_walk, container, false);
        getDialog().setTitle("Edit Walk");
        int width = getResources().getDimensionPixelSize(R.dimen.edit_dialog_width);
        int height = getResources().getDimensionPixelSize(R.dimen.edit_dialog_height);
        getDialog().getWindow().setLayout(width, height);
        mWalksDatabaseHelper = new WalksDatabaseHelper(this.getContext());
        mWalk = mWalksDatabaseHelper.getWalkObject(sWalkName);
        if (mWalk != null) {
            mOldWalkName = mWalk.getName();
            (mWalkNameEt = (EditText) rootView.findViewById(R.id.edit_activity_walk_name))
                    .setText(mWalk.getName());
            (mWalkLocalityEt = (EditText) rootView.findViewById(R.id.edit_activity_walk_locality))
                    .setText(mWalk.getLocality());
            (mWalkLengthEt = (EditText) rootView.findViewById(R.id.edit_activity_walk_distance))
                    .setText(mWalk.getLength().toString());

            (mWalkDescriptionEt = (EditText) rootView.findViewById
                    (R.id.edit_activity_walk_description)).setText(mWalk.getDescription());
            (mWalkRatingRb = (RatingBar) rootView.findViewById(R.id.edit_activity_walk_rating))
                    .setRating(mWalk.getRating());
            mWalkGradeText = (TextView) rootView.findViewById(R.id.edit_dialog_fragment_grade_text);
            mWalkGradeText.setText(Utils.getGradeString(mWalk.getGrade()));
            mWalkGradeText.setBackgroundResource(Constants.GRADE_COLOURS[mWalk.getGrade()]);

            //set up the seekbar to update the grade text and colour when it is moved
            mGradeSeekBar = (SeekBar) rootView.findViewById(R.id.edit_dialog_fragment_grade_seekbar);
            mGradeSeekBar.setProgress(mWalk.getGrade() - 1);
            mGradeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

                @Override
                public void onProgressChanged(SeekBar seekBar, int progressValue, boolean fromUser) {
                    mWalkGradeText.setText(Utils.getGradeString(progressValue)); //progress is from 0-4
                    mWalkGradeText.setBackgroundResource(Constants.GRADE_COLOURS[progressValue]);
                }


                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }


                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });


            String filePath = mWalk.getImageFilePath();
            (mwalkThumbnailIb = (ImageButton) rootView.findViewById
                    (R.id.edit_activity_walk_image)).setImageBitmap
                    (Utils.getBitmap(getContext(), filePath));

            Button deleteBtn = (Button) rootView.findViewById
                    (R.id.edit_activity_Delete_walk_btn);
            deleteBtn.setOnClickListener(this);
            Button viewLogBtn = (Button) rootView.findViewById
                    (R.id.edit_activity_view_log_btn);
            viewLogBtn.setOnClickListener(this);
            Button saveChangesBtn = (Button) rootView.findViewById
                    (R.id.edit_activity_save_changes_btn);
            saveChangesBtn.setOnClickListener(this);
            Button discardChangesBtn = (Button) rootView.findViewById
                    (R.id.edit_activity_discard_changes_btn);
            discardChangesBtn.setOnClickListener(this);
            ImageButton changeImageBtn = (ImageButton) rootView.findViewById
                    (R.id.edit_activity_walk_image);
            changeImageBtn.setOnClickListener(this);


        } else {
            //no walk found in database!
            Utils.showToast(this.getContext(), "Walk not found in database!");
            this.dismiss();
        }
        return rootView;
    }


    /**
     * Handles the buttons in a switch statement
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case (R.id.edit_activity_walk_image):
                changeThumbnailImage();
                break;

            case (R.id.edit_activity_Delete_walk_btn): //delete the walk if preferences set
                SharedPreferences sharedPreferences = PreferenceManager.
                        getDefaultSharedPreferences(getActivity().getBaseContext());

                if (sharedPreferences.getBoolean(getString(R.string.preference_allow_walk_delete), false)) {
                    if (sharedPreferences.getBoolean(getString(R.string.preference_confirm_deletions), false)) {
                        //display a deletion confirmation dialog
                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        builder.setMessage(getString(R.string.confirm_delete_message))
                                .setCancelable(false)
                                .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {

                                        deleteThisWalk();
                                        Intent deleteWalkIntent = new Intent(getContext(), StartScreenActivity.class);
                                        deleteWalkIntent.putExtra(Constants.WALK_DELETED, true);
                                        startActivity(deleteWalkIntent);
                                        getDialog().dismiss();
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
                        deleteThisWalk();
                        Intent deleteWalkIntent = new Intent(this.getContext(), StartScreenActivity.class);
                        deleteWalkIntent.putExtra(Constants.WALK_DELETED, true);
                        startActivity(deleteWalkIntent);
                        this.dismiss();
                    }

                } else {
                    Utils.showToast(getContext(), "Unable to delete: check your settings!");
                }

                break;
            case (R.id.edit_activity_view_log_btn): //show the log view dialog
                //start asyncTask to read logfile
                new LogFileViewerDialog(this.getContext(), mWalk.getLogFilePath());
                break;
            case (R.id.edit_activity_save_changes_btn): //save the walk changes if they are valid


                if (getWalkChanges()) {
                    mWalksDatabaseHelper.deleteWalk(mOldWalkName);
                    mWalksDatabaseHelper.addWalk(mWalk);
                    Intent saveChangesIntent = new Intent(this.getContext(), StartScreenActivity.class);
                    saveChangesIntent.putExtra(Constants.WALK_CHANGED, true);
                    startActivity(saveChangesIntent);
                    this.dismiss();
                }

                break;
            case (R.id.edit_activity_discard_changes_btn): //return to the start screen

                this.dismiss();
                break;

            default:
                Log.e(Constants.DEBUG_TAG, "Button not handled correctly");
                break;
        }
    }

    /**
     * Deletes the walk that this edit dialog is showing
     */
    private void deleteThisWalk() {
        mWalksDatabaseHelper.deleteWalk(mWalk.getName());
        File file = new File(mWalk.getLogFilePath());
        boolean logDeleted = file.delete();
        file = new File(mWalk.getImageFilePath());
        boolean imageDeleted = file.delete();
        if (logDeleted && imageDeleted) {
            Log.d(Constants.DEBUG_TAG, "Log and Image deleted");
        } else {
            Log.e(Constants.DEBUG_TAG, "File delete failed!");
        }

    }

    /**
     * gets the user input and save it to the Walk object
     * checks input for invalid values first, and returns true if successful
     */
    private boolean getWalkChanges() {
        if ((mWalkNameEt.getText().toString().trim()).equals(Constants.EMPTY_STRING)) {
            Utils.showToast(getContext(), "please enter a name");
            return false;
        }
        mWalk.setName(mWalkNameEt.getText().toString());
        if ((mWalkDescriptionEt.getText().toString().trim()).equals(Constants.EMPTY_STRING)) {
            Utils.showToast(getContext(), "Please enter a description");
            return false;
        }
        mWalk.setDescription(mWalkDescriptionEt.getText().toString());
        if ((mWalkLocalityEt.getText().toString().trim()).equals(Constants.EMPTY_STRING)) {
            Utils.showToast(getContext(), "Please enter a locality");
            return false;
        }
        mWalk.setLocality(mWalkLocalityEt.getText().toString());
        mWalk.setRating(mWalkRatingRb.getRating());
        mWalk.setGrade(mGradeSeekBar.getProgress());

        try {
            mWalk.setLength(Float.parseFloat(mWalkLengthEt.getText().toString()));
        } catch (NumberFormatException e) {
            Utils.showToast(getContext(), "invalid value for length");
            return false;
        }

        renameLogFile();
        renameThumbnail();
        return true;
    }

    /**
     * updates the name of the log file to match the new walk name and locality
     * saves the new path to the Walk object
     */
    private void renameLogFile() {
        File root = new File(Environment.getExternalStorageDirectory(), Constants.LOG_FILE_DIRECTORY);
        File from = new File(mWalk.getLogFilePath());
        File to = new File(root, mWalk.getName() + "_"
                + mWalk.getLocality() + Constants.LOG_FILE_EXTENSION);
        from.renameTo(to);

        mWalk.setLogFilePath(to.getAbsolutePath());
        Log.d(Constants.DEBUG_TAG, "Log File renamed to: " + mWalk.getLogFilePath());
    }

    /**
     * updates the name of the thumbnail image to match the new walk name and locality
     * saves the new path to the Walk object
     */
    private void renameThumbnail() {
        File root = new File(Environment.getExternalStorageDirectory(),
                Constants.IMAGE_FILE_DIRECTORY);
        File from = new File(mWalk.getImageFilePath());
        File to = new File(root, Utils.getFileNameString(
                mWalk.getName(),
                mWalk.getLocality(),
                Constants.IMAGE_FILE_EXTENSION));
        from.renameTo(to);

        mWalk.setImageFilePath(to.getAbsolutePath());
        Log.d(Constants.DEBUG_TAG, "Image File renamed to: " + mWalk.getImageFilePath());
    }


    /**
     * Opens the gallery to chose a new thumbnail
     */
    private void changeThumbnailImage() {
        Log.d(Constants.DEBUG_TAG, "Starting image gallery intent");
        Intent imageIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(imageIntent, Constants.LOAD_IMAGE_RESULTS);

    }


    /**
     * replaces thumbnail with image returned from the gallery
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        switch (requestCode) {

            case Constants.LOAD_IMAGE_RESULTS: {
                if (resultCode == Activity.RESULT_OK && null != data) {
                    // Let's read picked image data - its URI
                    Uri pickedImage = data.getData();
                    // Let's read picked image path using content resolver
                    String[] filePath = {MediaStore.Images.Media.DATA};
                    Cursor cursor = getActivity().getContentResolver().query(pickedImage,
                            filePath,
                            null,
                            null,
                            null);
                    cursor.moveToFirst();
                    String imagePath = cursor.getString(cursor.getColumnIndex(filePath[0]));


                    Bitmap resizedImage = Utils.getResizedBitmap(imagePath);

                    mWalk.setImageFilePath(Utils.saveToInternalStorage(resizedImage,
                            Utils.getFileNameString(
                                    mWalk.getName(),
                                    mWalk.getLocality(),
                                    Constants.IMAGE_FILE_EXTENSION)));
                    mwalkThumbnailIb.setImageBitmap(resizedImage);


                    cursor.close();
                }
                break;
            }
        }

    }


}
