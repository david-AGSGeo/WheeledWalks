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

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.text.Spannable;
import android.text.method.ScrollingMovementMethod;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import davidlee_11055579.wheeledwalks.R;
import davidlee_11055579.wheeledwalks.models.Constants;
import davidlee_11055579.wheeledwalks.utilities.Utils;

/**
 * Created by David Lee on 23/10/2015.
 * Opens a dialog window when given a valid context and log file path.
 * Populates the dialog from the log file line by line asyncronously,
 * colour coding the line based on its tag.
 * Allows for filtering of the data based on tags
 */
public class LogFileViewerDialog implements CompoundButton.OnCheckedChangeListener {

    private static showLogAsyncTask sShowLogAsyncTask;
    //filter variables linked to checkboxes
    private boolean mShowAccel = true;
    private boolean mShowGps = true;
    private boolean mShowMarkers = true;
    private boolean mShowHeartRate = true;
    //flag for checking if the asyncTask is finished
    private boolean mTaskFinished = false;
    private File mLogFile;
    private TextView mLogTextTv;


    public LogFileViewerDialog(Context context, String logFilePath) {

        mLogFile = new File(logFilePath);

        //only show dialog if the log file exists
        if (mLogFile.exists()) {
            Dialog logDialog = showLogFileDialog(context);
            mLogTextTv = (TextView) logDialog.findViewById(R.id.show_log_dialog_tv);
            mLogTextTv.setLayerType(View.LAYER_TYPE_SOFTWARE, null); //set to software
            // rendering for better performance. This is due to a known bug with
            // scrolling large TextViews
            logDialog.show();
        } else {
            //no log file found! exit asyncTask without showing the dialog
            Utils.showToast(context, "No log file found");
            return;
        }
        sShowLogAsyncTask = new showLogAsyncTask();
        sShowLogAsyncTask.execute();
    }

    /**
     * Appends a text string to a textview with a given colour
     *
     * @param tv     = the TextView
     * @param text   = The String
     * @param colour = The colour
     */
    public static void appendColoredText(TextView tv, String text, int colour) {

        //get the start and end of the added string
        int start = tv.getText().length();
        tv.append(text);
        int end = tv.getText().length();

        //set the added string as a new spannableText to a different colour
        Spannable spannableText = (Spannable) tv.getText();
        spannableText.setSpan(new ForegroundColorSpan(colour), start, end, 0);
    }

    /**
     * shows a dialog allowing the user to scroll through the Log File
     * changes are not allowed at this point
     */
    private Dialog showLogFileDialog(Context context) {
        final Dialog dialog = new Dialog(context);


        dialog.getWindow().setBackgroundDrawableResource(R.color.colorPrimary);
        dialog.setContentView(R.layout.dialog_show_log_file);
        dialog.setTitle(R.string.log_file_viewer_dialog_title);

        TextView logTextView = (TextView) dialog.findViewById(R.id.show_log_dialog_tv);
        logTextView.setMovementMethod(new ScrollingMovementMethod());
        Button exitLog = (Button) dialog.findViewById(R.id.show_log_dialog_ok_btn);
        exitLog.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                sShowLogAsyncTask.cancel(true);
                dialog.dismiss();
            }
        });


        ((CheckBox) dialog.findViewById(R.id.show_log_dialog_accel_cb))
                .setOnCheckedChangeListener(this);
        ((CheckBox) dialog.findViewById(R.id.show_log_dialog_gps_cb))
                .setOnCheckedChangeListener(this);
        ((CheckBox) dialog.findViewById(R.id.show_log_dialog_markers_cb))
                .setOnCheckedChangeListener(this);
        ((CheckBox) dialog.findViewById(R.id.show_log_dialog_heartrate_cb))
                .setOnCheckedChangeListener(this);

        return dialog;

    }

    /**
     * Sets up the filter flags based on changes to the checkboxes.
     * Restarts the asyncTask when a filter is chaned
     *
     * @param buttonView the checkbox that has been changed
     * @param isChecked  is the filter on or off
     */
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

        if (!mTaskFinished) {
            sShowLogAsyncTask.cancel(true); //cancel and allow to interrupt operation
        }
        switch (buttonView.getId()) {
            case (R.id.show_log_dialog_accel_cb):
                mShowAccel = isChecked;
                break;
            case (R.id.show_log_dialog_gps_cb):
                mShowGps = isChecked;
                break;
            case (R.id.show_log_dialog_markers_cb):
                mShowMarkers = isChecked;
                break;
            case (R.id.show_log_dialog_heartrate_cb):
                mShowHeartRate = isChecked;
                break;
            default:
                break;
        }
        while (!mTaskFinished) {
            //waiting for task to finish
            //this prevents progress updates from being passed on to the new asyncTask
        }
        sShowLogAsyncTask = new showLogAsyncTask(); //restart the viewer with the new filter
        sShowLogAsyncTask.execute();
    }

    /**
     * Loads the log file one line at a time and publishes each line to the mLogTextView
     * each line is filtered and colour coded based on its tag
     */
    private class showLogAsyncTask extends AsyncTask<Void, colouredLogString, Void> {


        public showLogAsyncTask() {

        }

        /**
         * clear the TextView and scroll to the very top
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mLogTextTv.setText(Constants.EMPTY_STRING); //clear the log dialog textview
            mLogTextTv.scrollTo(0, 0); //reset text view scroll position to start
            mTaskFinished = false;
        }

        /**
         * append the coloured string to the TextView
         *
         * @param values the string with a colour value attached
         */
        @Override
        protected void onProgressUpdate(colouredLogString... values) {

            appendColoredText(mLogTextTv, (values[0]).mMessage, (values[0]).mColour);
            super.onProgressUpdate(values);
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


            try {
                BufferedReader logBufferedReader = new BufferedReader(new FileReader(mLogFile));
                String line;

                while ((line = logBufferedReader.readLine()) != null) {
                    if (isCancelled()) {
                        mTaskFinished = true;
                        break;
                    }

                    String dataTag = ((line.split(","))[0]).substring(1); //separate out the
                    // data tag by splitting the line on comma, and dropping the first "#" char

                    switch (dataTag) { //display line in colour if dataType enabled
                        case Constants.LOG_ACCEL_TAG:
                            if (mShowAccel) {
                                publishProgress(new colouredLogString
                                        (line + "\n", Color.BLACK));

                                Utils.sleepThread(Constants.ASYNCTASK_LINE_SLEEP_DELAY); //sleep so that the UI thread doesnt get
                                // tied up with publishing. Improves performance
                            }
                            break;
                        case Constants.LOG_GPS_TAG:
                            if (mShowGps) {
                                publishProgress(new colouredLogString
                                        (line + "\n", Color.BLUE));
                                Utils.sleepThread(Constants.ASYNCTASK_LINE_SLEEP_DELAY);
                            }
                        case Constants.LOG_SENSORTAG_TAG:
                            if (mShowAccel) {
                                publishProgress(new colouredLogString
                                        (line + "\n", Color.DKGRAY));
                                Utils.sleepThread(Constants.ASYNCTASK_LINE_SLEEP_DELAY);
                            }
                            break;
                        case Constants.LOG_MARKER_TAG:
                            if (mShowMarkers) {
                                publishProgress(new colouredLogString
                                        (line + "\n", Color.MAGENTA));
                                Utils.sleepThread(Constants.ASYNCTASK_LINE_SLEEP_DELAY);
                            }
                            break;
                        case Constants.LOG_HEARTRATE_TAG:
                            if (mShowHeartRate) {
                                publishProgress(new colouredLogString
                                        (line + "\n", Color.YELLOW));
                                Utils.sleepThread(Constants.ASYNCTASK_LINE_SLEEP_DELAY);
                            }
                            break;
                        default:
                            publishProgress(new colouredLogString
                                    (line + "\n", Color.RED));
                            Utils.sleepThread(Constants.ASYNCTASK_LINE_SLEEP_DELAY);
                            break;
                    }


                }
                logBufferedReader.close(); //close the buffer
            } catch (IOException e) {
                //error reading file
                e.printStackTrace();
            }

            return null;
        }


        @Override
        protected void onPostExecute(Void aVoid) {
            mTaskFinished = true;
            super.onPostExecute(aVoid);

        }
    }

    /**
     * container for log string with colour attached
     */
    public class colouredLogString {
        public final String mMessage;
        public final Integer mColour;

        public colouredLogString(String message, Integer colour) {
            mMessage = message;
            mColour = colour;
        }
    }
}
