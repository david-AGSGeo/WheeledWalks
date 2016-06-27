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
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;

import davidlee_11055579.wheeledwalks.R;
import davidlee_11055579.wheeledwalks.models.Constants;
import davidlee_11055579.wheeledwalks.models.WalkDbConstants;
import davidlee_11055579.wheeledwalks.utilities.Utils;

/**
 * Created by David on 21/09/2015.
 * Adapter for the card recycleview on the start page
 */
public class WalkCardViewAdapter extends RecyclerView.Adapter<WalkCardViewHolder> {

    private Context mContext;
    private CursorAdapter mCursorAdapter;
    private WalkCardViewHolder mWalkCardViewHolder;

    public WalkCardViewAdapter(Context context, Cursor cursor) {
        this.mContext = context;
        this.mCursorAdapter = new CursorAdapter(mContext, cursor, 0) {

            @Override
            public View newView(Context context, Cursor cursor, ViewGroup parent) {
                return LayoutInflater.from(context).
                        inflate(R.layout.walk_card_view, parent, false);
            }

            @Override
            public void bindView(View view, Context context, Cursor cursor) {
                mWalkCardViewHolder.mName.setText(cursor.getString
                        (cursor.getColumnIndexOrThrow(WalkDbConstants.COLUMN_NAME)));
                mWalkCardViewHolder.mRating.setRating(cursor.getFloat
                        (cursor.getColumnIndexOrThrow(WalkDbConstants.COLUMN_RATING)));
                mWalkCardViewHolder.mGrade.setText(Utils.getGradeString(cursor.getInt
                        (cursor.getColumnIndexOrThrow(WalkDbConstants.COLUMN_GRADE))));
                mWalkCardViewHolder.mGrade.setBackgroundResource(Constants.GRADE_COLOURS[cursor.getInt
                        (cursor.getColumnIndexOrThrow(WalkDbConstants.COLUMN_GRADE))]);
                mWalkCardViewHolder.mDescription.setText(cursor.getString
                        (cursor.getColumnIndexOrThrow(WalkDbConstants.COLUMN_DESCRIPTION)));
                mWalkCardViewHolder.mLocality.setText(cursor.getString
                        (cursor.getColumnIndexOrThrow(WalkDbConstants.COLUMN_LOCALITY)));
                mWalkCardViewHolder.mDistance.setText(Utils.getFormattedDistanceString
                        (cursor.getFloat(
                                cursor.getColumnIndexOrThrow(WalkDbConstants.COLUMN_LENGTH))));
                String imageFilePath = cursor.getString(cursor.getColumnIndexOrThrow(WalkDbConstants.COLUMN_IMAGE_FILE_PATH));

                mWalkCardViewHolder.mImage.setImageBitmap(Utils.getBitmap(context, imageFilePath));

            }
        };
    }


    @Override
    public int getItemCount() {
        return mCursorAdapter.getCount();
    }

    @Override
    public void onBindViewHolder(WalkCardViewHolder walkCardViewHolder, int position) {
        mCursorAdapter.getCursor().moveToPosition(position);
        mWalkCardViewHolder = walkCardViewHolder;
        mCursorAdapter.bindView(walkCardViewHolder.itemView, mContext, mCursorAdapter.getCursor());
    }

    @Override
    public WalkCardViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View itemView = mCursorAdapter.newView(mContext, mCursorAdapter.getCursor(), viewGroup);
        return new WalkCardViewHolder(itemView, mContext);
    }

}
