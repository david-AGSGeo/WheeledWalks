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

package com.lee42.wheeledwalksviewer.controllers;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.lee42.wheeledwalksviewer.R;
import com.lee42.wheeledwalksviewer.models.Constants;
import com.lee42.wheeledwalksviewer.models.NewWalkDbConstants;
import com.lee42.wheeledwalksviewer.utilities.Utils;


/**
 * Created by David on 21/09/2015.
 * Adapter for the card recycleview on the start page
 */
public class NewWalkCardViewAdapter extends RecyclerView.Adapter<NewWalkCardViewHolder> {

    private Context mContext;
    private CursorAdapter mCursorAdapter;
    private NewWalkCardViewHolder mWalkCardViewHolder;

    public NewWalkCardViewAdapter(Context context, Cursor cursor) {
        this.mContext = context;
        this.mCursorAdapter = new CursorAdapter(mContext, cursor, 0) {

            @Override
            public View newView(Context context, Cursor cursor, ViewGroup parent) {
                return LayoutInflater.from(context).
                        inflate(R.layout.new_walk_card_view, parent, false);
            }

            @Override
            public void bindView(View view, Context context, Cursor cursor) {
                mWalkCardViewHolder.mName.setText(cursor.getString
                        (cursor.getColumnIndexOrThrow(NewWalkDbConstants.COLUMN_NAME)));
                mWalkCardViewHolder.mRating.setRating(cursor.getFloat
                        (cursor.getColumnIndexOrThrow(NewWalkDbConstants.COLUMN_RATING)));
                mWalkCardViewHolder.mGrade.setText(Utils.getGradeString(cursor.getInt
                        (cursor.getColumnIndexOrThrow(NewWalkDbConstants.COLUMN_GRADE))));
                mWalkCardViewHolder.mGrade.setBackgroundResource(Constants.GRADE_COLOURS[cursor.getInt
                        (cursor.getColumnIndexOrThrow(NewWalkDbConstants.COLUMN_GRADE))]);
                mWalkCardViewHolder.mDescription.setText(cursor.getString
                        (cursor.getColumnIndexOrThrow(NewWalkDbConstants.COLUMN_DESCRIPTION)));
                mWalkCardViewHolder.mLocality.setText(cursor.getString
                        (cursor.getColumnIndexOrThrow(NewWalkDbConstants.COLUMN_LOCALITY)));
                mWalkCardViewHolder.mDistance.setText(Utils.getFormattedDistanceString
                        (cursor.getFloat(
                                cursor.getColumnIndexOrThrow(NewWalkDbConstants.COLUMN_LENGTH))));
                String imageFilePath = cursor.getString(cursor.getColumnIndexOrThrow(NewWalkDbConstants.COLUMN_IMAGE_FILE_PATH));

                //mWalkCardViewHolder.mImage.setImageBitmap(Utils.getBitmap(context, imageFilePath));
                mWalkCardViewHolder.mImage.setImageDrawable(mContext.getResources().getDrawable(R.drawable.defaultcardimage));
            }
        };
    }


    @Override
    public int getItemCount() {
        return mCursorAdapter.getCount();
    }

    @Override
    public void onBindViewHolder(NewWalkCardViewHolder walkCardViewHolder, int position) {
        mCursorAdapter.getCursor().moveToPosition(position);
        mWalkCardViewHolder = walkCardViewHolder;
        mCursorAdapter.bindView(walkCardViewHolder.itemView, mContext, mCursorAdapter.getCursor());
    }

    @Override
    public NewWalkCardViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View itemView = mCursorAdapter.newView(mContext, mCursorAdapter.getCursor(), viewGroup);
        return new NewWalkCardViewHolder(itemView, mContext);
    }



}
