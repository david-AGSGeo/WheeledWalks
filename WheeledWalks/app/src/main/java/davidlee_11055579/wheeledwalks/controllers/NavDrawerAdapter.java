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

package davidlee_11055579.wheeledwalks.controllers;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import davidlee_11055579.wheeledwalks.R;
import davidlee_11055579.wheeledwalks.activities.NewWalkActivity;
import davidlee_11055579.wheeledwalks.activities.SettingsActivity;
import davidlee_11055579.wheeledwalks.activities.StartScreenActivity;
import davidlee_11055579.wheeledwalks.activities.fragments.BluetoothSetupDialog;
import davidlee_11055579.wheeledwalks.activities.fragments.EditWalkDialogFragment;
import davidlee_11055579.wheeledwalks.models.Constants;
import davidlee_11055579.wheeledwalks.utilities.Utils;

/**
 * Created by David on 5/10/2015.
 * Adapter and ViewHolder for the navigation drawer. handles nav drawer clicks
 */
public class NavDrawerAdapter extends RecyclerView.Adapter<NavDrawerAdapter.NavDrawerViewHolder> {

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;

    private String mNavDrawerTitles[];
    private int mNavDrawerIcons[];
    private String mTitle;
    private String mSubtext;


    // Creating a NavDrawerViewHolder which extends the RecyclerView View Holder
    // NavDrawerViewHolder are used to to store the inflated views in order to recycle them

    public NavDrawerAdapter(String Titles[], int Icons[], String title, String subtext) { //  Constructor with titles and icons parameter

        mNavDrawerTitles = Titles;
        mNavDrawerIcons = Icons;
        mTitle = title;
        mSubtext = subtext;

    }

    @Override
    public NavDrawerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (viewType == TYPE_ITEM) { //check whether to inflate a header or item
            View v = LayoutInflater.from(parent.getContext()).inflate
                    (R.layout.nav_drawer_item, parent, false); //Inflating the layout

            return new NavDrawerViewHolder(v, viewType); // Returning the created object


        } else if (viewType == TYPE_HEADER) {//check whether to inflate a header or item

            View v = LayoutInflater.from(parent.getContext()).inflate
                    (R.layout.nav_drawer_header, parent, false); //Inflating the layout

            return new NavDrawerViewHolder(v, viewType); //returning the object created

        }
        return null;

    }


    /**
     * Binds the ViewHolder to the view.
     * if the holderId is an item, get the item name and icon from the constant array
     * else if it is a header set the header views
     *
     * @param holder   = the viewholder for the navdrawer item in the recyclerview
     * @param position = the position index in the recyclerview
     */
    @Override
    public void onBindViewHolder(NavDrawerViewHolder holder, int position) {
        if (holder.holderId == TYPE_ITEM) {
            holder.itemTextView.setText(mNavDrawerTitles[position - 1]); // Setting the Text with the array of our Titles
            holder.itemIconImageView.setImageResource(mNavDrawerIcons[position - 1]);// Setting the image with array of our icons
        } else {
            holder.headerTitle.setText(mTitle);
            holder.headerSubtext.setText(mSubtext);
        }
    }

    /**
     * Returns the number of items present in the list
     */
    @Override
    public int getItemCount() {
        return mNavDrawerTitles.length + 1; // the number of items in the list will be +1 the titles including the header view.
    }

    /**
     * Checks what type of view is being passed
     */

    @Override
    public int getItemViewType(int position) {
        if (isPositionHeader(position)) {
            return TYPE_HEADER;
        }

        return TYPE_ITEM;
    }

    private boolean isPositionHeader(int position) {
        return position == 0;
    }


    /**
     * NavDrawerViewHolder for the Navigation Drawer RecyclerView
     */
    public static class NavDrawerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        int holderId;

        TextView itemTextView;
        ImageView itemIconImageView;

        TextView headerTitle;
        TextView headerSubtext;


        public NavDrawerViewHolder(View itemView, int ViewType) {
            super(itemView);

            itemView.setClickable(true);
            itemView.setOnClickListener(this);

            // Set the appropriate view with the the view type

            if (ViewType == TYPE_ITEM) {
                itemTextView = (TextView) itemView.findViewById(R.id.nav_bar_rowText);
                itemIconImageView = (ImageView) itemView.findViewById(R.id.nav_bar_rowIcon);
                holderId = TYPE_ITEM;
            } else {


                headerTitle = (TextView) itemView.findViewById(R.id.nav_bar_title);
                headerSubtext = (TextView) itemView.findViewById(R.id.nav_bar_subtext);
                holderId = TYPE_HEADER;
            }
        }

        /**
         * clicks on each item in the drawer
         * @param v - the item that was clicked
         */
        @Override
        public void onClick(View v) {
            final Context context = v.getContext();

            switch (getAdapterPosition()) {
                case 0: //header selected
                    Log.d(Constants.DEBUG_TAG, "selected Header");
                    //do nothing :)
                    break;
                case 1: //Add walk Selected
                    Log.d(Constants.DEBUG_TAG, "selected First item: Add walk");
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setMessage("Make sure all your settings are correct and sensors are calibrated before starting a new walk!")
                            .setCancelable(false)
                            .setPositiveButton("Continue", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    Intent newWalkIntent = new Intent(context, NewWalkActivity.class);
                                    context.startActivity(newWalkIntent);

                                }
                            })
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });
                    AlertDialog alert = builder.create();
                    alert.show();


                    break;
                case 2: //Setting selected
                    Log.d(Constants.DEBUG_TAG, "selected Second item: Settings");
                    Intent settingsIntent = new Intent(context, SettingsActivity.class);
                    context.startActivity(settingsIntent);
                    break;
                case 3: //Connect Sensor Tags selected
                    //TODO: set up sensor tags  for v1.2
                    Log.d(Constants.DEBUG_TAG, "selected Third item: Connect sensor tags");

                    FragmentManager fragmentManager = ((AppCompatActivity) context).getSupportFragmentManager();
                    BluetoothSetupDialog bluetoothSetupDialog = BluetoothSetupDialog
                            .newInstance(Constants.BLUETOOTH_SETUP_DIALOG_TITLE);
                    bluetoothSetupDialog.show(fragmentManager, Constants.EDIT_DIALOG_FRAGMENT_TAG);
                    //Utils.showToast(context, "Feature will be added in V1.2");
                    break;
                case 4: //Connect Heart Rate monitor selected
                    //TODO: set up heart rate monitor  for v1.2
                    Log.d(Constants.DEBUG_TAG, "selected Fourth item: Connect Heart rate monitor");
                    Utils.showToast(context, "Feature will be added in V1.2");
                    break;
                case 5: //Calibrate Sensor selected
                    Log.d(Constants.DEBUG_TAG, "selected Fifth item: Calibrate Sensors");
                    View popupView = LayoutInflater.from(context).inflate
                            (R.layout.popup_calibrate_sensors, null);

                    //Show a popup window prompt
                    final PopupWindow popupWindow = new PopupWindow(popupView,
                            RelativeLayout.LayoutParams.MATCH_PARENT,
                            RelativeLayout.LayoutParams.MATCH_PARENT,
                            true);
                    //set semi-transparent background
                    (popupView.findViewById(R.id.calibrate_popup_background))
                            .getBackground().setAlpha(220);
                    popupWindow.setOutsideTouchable(false);


                    Button calibrateBtn = (Button) popupView.findViewById
                            (R.id.calibrate_sensor_popup_btn);
                    calibrateBtn.setOnClickListener(new View.OnClickListener() {

                        public void onClick(View popupView) {
                            popupWindow.dismiss();
                        }
                    });

                    popupWindow.showAtLocation(v, Gravity.CENTER, 0, 0);

                    break;
                case 6: //delete all the things!
                    Log.d(Constants.DEBUG_TAG, "selected Sixth item: delete all the things");

                    Intent returnToStartScreenIntent = new Intent(context,
                            StartScreenActivity.class);
                    returnToStartScreenIntent.putExtra(Constants.DELETE_ALL, true);
                    context.startActivity(returnToStartScreenIntent);
                    break;
            }

        }

    }

}
