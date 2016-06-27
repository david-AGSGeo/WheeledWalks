package com.lee42.wheeledwalksviewer.controllers;

import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.lee42.wheeledwalksviewer.R;
import com.lee42.wheeledwalksviewer.activities.fragments.PreviewWalkDialogFragment;
import com.lee42.wheeledwalksviewer.models.Constants;

/**
 * Created by David on 18/05/2016.
 */
public class NewWalkCardViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    protected TextView mName;
    protected RatingBar mRating;
    protected TextView mGrade;
    protected TextView mDescription;
    protected TextView mLocality;
    protected TextView mDistance;
    protected ImageView mImage;
    protected Button mPreviewBtn;
    protected Button mDownloadBtn;

    private Context mContext;

    public NewWalkCardViewHolder(View itemView, Context context) {
        super(itemView);
        mContext = context;

        mName = (TextView) itemView.findViewById(R.id.walk_name);
        mRating = (RatingBar) itemView.findViewById(R.id.walk_rating);
        mGrade = (TextView) itemView.findViewById(R.id.walk_grade);
        mDescription = (TextView) itemView.findViewById(R.id.walk_description);
        mLocality = (TextView) itemView.findViewById(R.id.walk_locality);
        mDistance = (TextView) itemView.findViewById(R.id.walk_distance);
        mImage = (ImageView) itemView.findViewById(R.id.walk_image);
        mPreviewBtn = (Button) itemView.findViewById(R.id.card_preview_walk_button);
        mDownloadBtn = (Button) itemView.findViewById(R.id.card_download_walk_button);
        mPreviewBtn.setOnClickListener(this);
        mDownloadBtn.setOnClickListener(this);


    }

    public void bindItemView(View itemView) {

    }

    /**
     * Handle the edit and export buttons on each card
     *
     * @param v - the button that was clicked
     */
    @Override
    public void onClick(View v) {


            switch (v.getId()) {
                case (R.id.card_preview_walk_button):

                    FragmentManager fragmentManager = ((AppCompatActivity)mContext).getSupportFragmentManager();
                    PreviewWalkDialogFragment previewWalkDialogFragment = PreviewWalkDialogFragment
                            .newInstance("Preview", mName.getText().toString());
                    previewWalkDialogFragment.show(fragmentManager, Constants.PREVIEW_DIALOG_FRAGMENT_TAG);
                    break;
                case (R.id.card_download_walk_button):
                    //TODO: set up walk download!

                    break;
            }

        }

    }