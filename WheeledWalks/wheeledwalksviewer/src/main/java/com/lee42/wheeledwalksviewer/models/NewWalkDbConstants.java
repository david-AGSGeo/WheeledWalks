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

package com.lee42.wheeledwalksviewer.models;

/**
 * Created by David on 14/10/2015.
 * Class for holding all the constants related to setting up the walks database
 */
public class NewWalkDbConstants {

    //Walks Database Name
    public static final String DATABASE_NAME = "wheeledwalksviewer_new.db";

    //Database Version
    public static final int DATABASE_VERSION = 1;

    //Walks Table Name
    public static final String TABLE_NEW_WALKS = "walks_table";

    //Walks Table Columns
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_DATE_SURVEYED = "date_surveyed";
    public static final String COLUMN_LENGTH = "length";
    public static final String COLUMN_RATING = "rating";
    public static final String COLUMN_GRADE = "grade";
    public static final String COLUMN_DESCRIPTION = "description";
    public static final String COLUMN_LOCALITY = "locality";
    public static final String COLUMN_IMAGE_FILE_PATH = "image_file";
    public static final String COLUMN_LOG_FILE_PATH = "log_file";

    //walks table array
    public static final String[] WALK_TABLE_COLUMNS = {
            COLUMN_ID,
            COLUMN_NAME,
            COLUMN_DATE_SURVEYED,
            COLUMN_LENGTH,
            COLUMN_RATING,
            COLUMN_GRADE,
            COLUMN_DESCRIPTION,
            COLUMN_LOCALITY,
            COLUMN_IMAGE_FILE_PATH,
            COLUMN_LOG_FILE_PATH
    };

    // Database creation sql statement
    public static final String DATABASE_CREATE = "create table "
            + TABLE_NEW_WALKS + "("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COLUMN_NAME + " TEXT not null, "
            + COLUMN_DATE_SURVEYED + " INTEGER not null, "
            + COLUMN_LENGTH + " REAL not null, "
            + COLUMN_RATING + " REAL not null, "
            + COLUMN_GRADE + " INTEGER not null, "
            + COLUMN_DESCRIPTION + " TEXT not null, "
            + COLUMN_LOCALITY + " TEXT not null, "
            + COLUMN_IMAGE_FILE_PATH + " TEXT not null, "
            + COLUMN_LOG_FILE_PATH + " TEXT not null"
            + ");";
}
