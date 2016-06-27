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

import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.speech.RecognizerIntent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import davidlee_11055579.wheeledwalks.R;
import davidlee_11055579.wheeledwalks.models.Constants;
import davidlee_11055579.wheeledwalks.models.Walk;
import davidlee_11055579.wheeledwalks.utilities.Utils;

/**
 * Created by David on 6/10/2015.
 * Accepts user input to finalise the recorded walk, and passes this back
 * to the start screen activity
 */
public class FinaliseWalkActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageButton mChoseImageButton;
    private EditText mWalkDescriptionEt;
    private SeekBar mGradeSeekBar;
    private Walk mWalk;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.v(Constants.DEBUG_TAG, "ALM: onCreate FinaliseWalkActivity");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finalise_walk);

        mWalk = getIntent().getParcelableExtra(Constants.WALK_TAG); //receive walk object from recording

        Button finaliseButton = (Button) findViewById(R.id.finalise_walk_btn);
        finaliseButton.setOnClickListener(this);
        mChoseImageButton = (ImageButton) findViewById(R.id.finalise_walk_chose_image_btn);
        mChoseImageButton.setOnClickListener(this);
        FloatingActionButton microphoneButton =
                (FloatingActionButton) findViewById(R.id.finalise_walk_description_microphone_button);
        microphoneButton.setOnClickListener(this);

        ((TextView) findViewById(R.id.finalise_walk_name_tv)).setText(mWalk.getName());
        ((TextView) findViewById(R.id.finalise_walk_location_tv)).setText(mWalk.getLocality());
        String dateString =
                new SimpleDateFormat(getString(R.string.default_date_format), Locale.ENGLISH)
                        .format(new Date(mWalk.getDateSurveyed()));
        ((TextView) findViewById(R.id.finalise_walk_date_tv)).setText(dateString);
        ((TextView) findViewById(R.id.finalise_walk_Distance_tv)).
                setText(Utils.getFormattedDistanceString(mWalk.getLength()));

        mWalkDescriptionEt = (EditText) findViewById(R.id.finalise_walk_description_et);

        // set up SeekBar to change the grade text and colour
        mGradeSeekBar = (SeekBar) findViewById(R.id.finalise_walk_grade_seekbar);
        mGradeSeekBar.setProgress(0); //start at very easy
        final TextView gradeText = (TextView) findViewById(R.id.finalise_walk_grade_text);
        gradeText.setText(Utils.getGradeString(mGradeSeekBar.getProgress()));
        gradeText.setBackgroundResource(Constants.GRADE_COLOURS[mGradeSeekBar.getProgress()]);
        mGradeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progressValue, boolean fromUser) {
                gradeText.setText(Utils.getGradeString(progressValue)); //progress is from 0-4
                gradeText.setBackgroundResource(Constants.GRADE_COLOURS[progressValue]);
            }


            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }


            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

    }

    /**
     * Checks the user input is correct, and then saves it to the walk object and passes it via
     * intent back to the start screen
     */
    private void finaliseWalk() {

        //Autopopulate if the preference is set
        if (PreferenceManager.getDefaultSharedPreferences(this)
                .getBoolean(getString(R.string.preference_populate_new_walk), false)) {
            mWalk.setRating(3);
            mWalk.setDescription("default description");
            mWalk.setGrade(3);
            mWalk.setImageFilePath(Constants.DEFAULT_IMAGE_PATH);
        } else {

            mWalk.setRating(((RatingBar) findViewById(R.id.finalise_walk_rating_bar)).getRating());
            mWalk.setDescription(mWalkDescriptionEt.getText().toString());
            mWalk.setGrade(mGradeSeekBar.getProgress());
            if (mWalk.getRating() == 0.0f) {
                Utils.showToast(getApplicationContext(), "Please Rate the walk!");

                return;
            } else if (mWalk.getImageFilePath().equals(Constants.EMPTY_STRING)) {
                Utils.showToast(getApplicationContext(), "Please choose a thumbnail image!");

                return;
            } else if (mWalk.getDescription().equals(Constants.EMPTY_STRING)) {
                Utils.showToast(getApplicationContext(), "Please enter a walk description!");

                return;
            }
        }
        //all data entered correctly
        finaliseLogFile();
        //pass walk to StartScreenActivity
        Intent returnToStartScreenIntent = new Intent(FinaliseWalkActivity.this,
                StartScreenActivity.class);
        returnToStartScreenIntent.putExtra(Constants.WALK_TAG, mWalk);
        startActivity(returnToStartScreenIntent);
    }

    /**
     * Adds the entered data to the end of the log file
     */
    private void finaliseLogFile() {
        File logFile = new File(mWalk.getLogFilePath());

        if (logFile.exists()) {
            try {
                FileWriter logFileWriter = new FileWriter(logFile, true);
                String logFooter = String.format(Constants.LOG_FILE_FOOTER_FORMAT,
                        mWalk.getRating(),
                        mWalk.getGrade(),
                        mWalk.getLength(),
                        mWalk.getDescription(),
                        mWalk.getImageFilePath());
                logFileWriter.append(logFooter);
                logFileWriter.flush();
                logFileWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Log.e(Constants.DEBUG_TAG, "Unable to finalise Log File!");
        }
    }

    /**
     * Handles the buttons in a switch statement
     */
    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case (R.id.finalise_walk_btn):

                finaliseWalk();


                break;
            case (R.id.finalise_walk_chose_image_btn): //start chose image activity
                Log.d(Constants.DEBUG_TAG, "Starting image gallery intent");
                Intent imageIntent = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(imageIntent, Constants.LOAD_IMAGE_RESULTS);

                break;
            case (R.id.finalise_walk_description_microphone_button): //start chose image activity
                Log.d(Constants.DEBUG_TAG, "Starting voice to text");
                Intent voiceIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                voiceIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                        RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                voiceIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
                voiceIntent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                        "Please speak your walk description");
                try {
                    startActivityForResult(voiceIntent, 2);
                } catch (ActivityNotFoundException a) {
                    a.printStackTrace();
                    Log.w(Constants.DEBUG_TAG, "Speech not supported!");
                    Toast.makeText(getApplicationContext(), "Speech not supported",
                            Toast.LENGTH_SHORT).show();
                }
                break;

            default:
                Log.e(Constants.DEBUG_TAG, "Button not handled correctly!");
                break;
        }
    }


    /**
     * handles the return intents from the image and speech requests
     *
     * @param requestCode = which request is this?
     * @param data        = the intent containing the returned data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case Constants.REQUEST_SPEECH_RESULTS: {
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    mWalkDescriptionEt.setText(result.get(0));  //this is the returned string
                    //from voice to text
                }
            }
            break;
            case Constants.LOAD_IMAGE_RESULTS: {
                if (resultCode == RESULT_OK && null != data) {
                    // Let's read picked image data - its URI
                    Uri pickedImage = data.getData();
                    // Let's read picked image path using content resolver
                    String[] filePath = {MediaStore.Images.Media.DATA};
                    Cursor cursor = getContentResolver().query(pickedImage,
                            filePath, null, null, null);
                    cursor.moveToFirst();
                    String imagePath = cursor.getString(cursor.getColumnIndex(filePath[0]));

                    Bitmap resizedImage = Utils.getResizedBitmap(imagePath);
                    mWalk.setImageFilePath(Utils.saveToInternalStorage(resizedImage, Utils.getFileNameString(mWalk.getName(), mWalk.getLocality(), Constants.IMAGE_FILE_EXTENSION)));

                    mChoseImageButton.setImageBitmap(resizedImage);

                    // At the end remember to close the cursor or you will end with the RuntimeException!
                    cursor.close();
                }
                break;
            }
        }

    }

    /**
     * Override tha back button so that it does not interrupt recording
     */
    @Override
    public void onBackPressed() {
        //moveTaskToBack(true); //do not go back!
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("cannot resume recording. please finalise the walk")
                .setCancelable(true)

                .setNegativeButton("Back", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    protected void onStop() {
        Log.v(Constants.DEBUG_TAG, "ALM: onStop FinaliseWalkActivity");
        super.onStop();
    }

    @Override
    protected void onPause() {
        Log.v(Constants.DEBUG_TAG, "ALM: onPause FinaliseWalkActivity");
        super.onPause();
    }

    @Override
    protected void onResume() {
        Log.v(Constants.DEBUG_TAG, "ALM: onResume FinaliseWalkActivity");
        super.onResume();
    }

    @Override
    protected void onStart() {
        Log.v(Constants.DEBUG_TAG, "ALM: onStart FinaliseWalkActivity");
        super.onStart();
    }
}
