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
import android.content.ContextWrapper;
import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import davidlee_11055579.wheeledwalks.R;
import davidlee_11055579.wheeledwalks.activities.StartScreenActivity;
import davidlee_11055579.wheeledwalks.activities.fragments.EditWalkDialogFragment;
import davidlee_11055579.wheeledwalks.models.Constants;
import davidlee_11055579.wheeledwalks.utilities.Utils;

/**
 * Created by David on 21/09/2015.
 * The viewholder for the card list on the start screen.
 * Also handle the buttons on the cards, starting the edit and export fragments
 */
public class WalkCardViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    protected TextView mName;
    protected RatingBar mRating;
    protected TextView mGrade;
    protected TextView mDescription;
    protected TextView mLocality;
    protected TextView mDistance;
    protected ImageView mImage;
    protected Button mEditBtn;
    protected Button mExportBtn;

    private Context mContext;

    public WalkCardViewHolder(View itemView, Context context) {
        super(itemView);
        mContext = context;

        mName = (TextView) itemView.findViewById(R.id.walk_name);
        mRating = (RatingBar) itemView.findViewById(R.id.walk_rating);
        mGrade = (TextView) itemView.findViewById(R.id.walk_grade);
        mDescription = (TextView) itemView.findViewById(R.id.walk_description);
        mLocality = (TextView) itemView.findViewById(R.id.walk_locality);
        mDistance = (TextView) itemView.findViewById(R.id.walk_distance);
        mImage = (ImageView) itemView.findViewById(R.id.walk_image);
        mEditBtn = (Button) itemView.findViewById(R.id.card_edit_walk_button);
        mExportBtn = (Button) itemView.findViewById(R.id.card_export_walk_button);
        mEditBtn.setOnClickListener(this);
        mExportBtn.setOnClickListener(this);


    }

    public void bindItemView(View itemView) {

    }

    /**
     * Handle the edit and export buttons on each card
     * @param v - the button that was clicked
     */
    @Override
    public void onClick(View v) {



        switch (v.getId()) {
            case (R.id.card_edit_walk_button):

                FragmentManager fragmentManager = ((AppCompatActivity)mContext).getSupportFragmentManager();
                EditWalkDialogFragment editWalkDialogFragment = EditWalkDialogFragment
                        .newInstance(Constants.EDIT_WALK_DIALOG_TITLE, mName.getText().toString());
                editWalkDialogFragment.show(fragmentManager, Constants.EDIT_DIALOG_FRAGMENT_TAG);
                break;
            case (R.id.card_export_walk_button):
                //TODO: do as above!
                //Utils.showToast(context, "Feature will be added in V1.2");
                Intent exportWalkIntent = new Intent(mContext, StartScreenActivity.class);
                exportWalkIntent.putExtra(Constants.EXPORT_WALK,  mName.getText().toString());
               mContext.startActivity(exportWalkIntent);
                break;
        }

    }

}
