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

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import davidlee_11055579.wheeledwalks.models.Constants;
import davidlee_11055579.wheeledwalks.models.Walk;
import davidlee_11055579.wheeledwalks.models.WalkDbConstants;

/**
 * Created by David on 14/10/2015.
 * Set up and administer the walks database. This is an
 * internal SQLite database that holds all the walks recorded
 * by this phone.
 */
public class WalksDatabaseHelper extends SQLiteOpenHelper {

    public WalksDatabaseHelper(Context context) {
        super(context, WalkDbConstants.DATABASE_NAME, null, WalkDbConstants.DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(WalkDbConstants.DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(WalksDatabaseHelper.class.getName(), "Upgrading Database from version " +
                oldVersion + " to " + newVersion);
        db.execSQL("DROP TABLE IF EXISTS " + WalkDbConstants.TABLE_WALKS);
        onCreate(db);
    }

    /**
     * adds a walk to the Walks Table
     * @param newWalk - the walk to add
     */
    public void addWalk(Walk newWalk) {
        ContentValues values = new ContentValues();
        values.put(WalkDbConstants.COLUMN_NAME, newWalk.getName());
        values.put(WalkDbConstants.COLUMN_DATE_SURVEYED, newWalk.getDateSurveyed());
        values.put(WalkDbConstants.COLUMN_LENGTH, newWalk.getLength());
        values.put(WalkDbConstants.COLUMN_RATING, newWalk.getRating());
        values.put(WalkDbConstants.COLUMN_GRADE, newWalk.getGrade());
        values.put(WalkDbConstants.COLUMN_DESCRIPTION, newWalk.getDescription());
        values.put(WalkDbConstants.COLUMN_LOCALITY, newWalk.getLocality());
        values.put(WalkDbConstants.COLUMN_IMAGE_FILE_PATH, newWalk.getImageFilePath());
        values.put(WalkDbConstants.COLUMN_LOG_FILE_PATH, newWalk.getLogFilePath());


        SQLiteDatabase db = this.getWritableDatabase();

        db.insert(WalkDbConstants.TABLE_WALKS, null, values);

        db.close();
        Log.w(Constants.DEBUG_TAG, "Walk added to database: " + newWalk.getName());
    }

    /**
     * searches for a walk by name,
     *
     * @param name = name to search for
     * @return a cursor containing all found results
     */

    public Cursor getWalkByName(String name) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(WalkDbConstants.TABLE_WALKS,
                WalkDbConstants.WALK_TABLE_COLUMNS,
                "name = ?",
                new String[]{String.valueOf(name)},
                null,
                null,
                null,
                null);

        if (cursor != null) {
            cursor.moveToFirst();
        } else {
            return null;
        }
        return cursor;
    }

    /**
     * Searches the database for partial matches to the search string in the
     * name and location column, and returns all results
     * @param searchString - the search string
     * @return - a cursor containing the results
     */
    public Cursor searchByNameOrLocation(String searchString) {
        SQLiteDatabase db = this.getReadableDatabase();


        searchString = searchString.trim(); //remove spaces from front and end
        Cursor cursor = db.query(true,
                WalkDbConstants.TABLE_WALKS,
                WalkDbConstants.WALK_TABLE_COLUMNS,
                WalkDbConstants.COLUMN_NAME + " LIKE ?" + " OR "
                        + WalkDbConstants.COLUMN_LOCALITY + " LIKE ?",
                new String[]{"%" + searchString + "%", "%" + searchString + "%"},
                null,
                null,
                null,
                null);


        if (cursor.moveToFirst()) {
            Log.d(Constants.DEBUG_TAG, "search found results for: " + searchString);

        }
        return cursor;
    }

    /**
     * Searches for walk by name, and returns a walk object of the first result
     *
     * @param name = name to search for
     * @return the first result as a Walk object
     */
    public Walk getWalkObject(String name) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(WalkDbConstants.TABLE_WALKS,
                WalkDbConstants.WALK_TABLE_COLUMNS,
                "name = ?",
                new String[]{String.valueOf(name)},
                null,
                null,
                null,
                null);

        if (cursor != null) {
            cursor.moveToFirst();
            Walk walk = new Walk(
                    cursor.getString(cursor.getColumnIndex(WalkDbConstants.COLUMN_NAME)),
                    cursor.getLong(cursor.getColumnIndex(WalkDbConstants.COLUMN_DATE_SURVEYED)),
                    cursor.getFloat(cursor.getColumnIndex(WalkDbConstants.COLUMN_LENGTH)),
                    cursor.getFloat(cursor.getColumnIndex(WalkDbConstants.COLUMN_RATING)),
                    cursor.getInt(cursor.getColumnIndex(WalkDbConstants.COLUMN_GRADE)),
                    cursor.getString(cursor.getColumnIndex(WalkDbConstants.COLUMN_DESCRIPTION)),
                    cursor.getString(cursor.getColumnIndex(WalkDbConstants.COLUMN_LOCALITY)),
                    cursor.getString(cursor.getColumnIndex(WalkDbConstants.COLUMN_IMAGE_FILE_PATH)),
                    cursor.getString(cursor.getColumnIndex(WalkDbConstants.COLUMN_LOG_FILE_PATH)));
            return walk;
        } else {
            return null;
        }

    }



    /**
     * Returns a cursor with all walks in the walks table
     * @return - the cursor
     */
    public Cursor getAllWalks() {

        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery("SELECT * FROM " + WalkDbConstants.TABLE_WALKS, null);
    }

    /**
     * Deletes a walk from the database by name
     * @param name - the name of the walk to delete
     */
    public void deleteWalk(String name) {
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(WalkDbConstants.TABLE_WALKS,
                WalkDbConstants.COLUMN_NAME + " = ?",
                new String[]{String.valueOf((name))});
        db.close();
    }
}
