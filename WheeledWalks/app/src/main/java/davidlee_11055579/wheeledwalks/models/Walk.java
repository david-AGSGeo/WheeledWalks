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

package davidlee_11055579.wheeledwalks.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by David on 21/09/2015.
 * Class for storing data on a single walk.
 * implements parcelable so that it can be passed in an intent as a parcelableExtra
 */
public class Walk implements Parcelable {

    /**
     * The CREATOR that parcels the walk. called automatically when a walk is
     * added to an intentExtra
     */
    public static final Parcelable.Creator<Walk> CREATOR = new Parcelable.Creator<Walk>() {

        @Override
        public Walk createFromParcel(Parcel source) {

            return new Walk(source);  //using parcelable constructor
        }

        @Override
        public Walk[] newArray(int size) {

            return new Walk[size];
        }
    };
    private String mName;   //the name of the walk
    private Long mDateSurveyed; //the date the recording was started
    private Float mLength; //the total length of the walk as recorded by the gps
    private Float mRating;  //the user's rating of the walk (0-5)
    private int mGrade; //the objective difficulty of the walk (1-5) determined from the data
    private String mDescription; // a brief description of the walk
    private String mLocality; //the suburb or national park in which the walk is located
    private String mImageFilePath; //the absolute filepath of the thumnail image
    private String mLogFilePath; //the absolute filepath of the log file


    public Walk() {  //empty constructor: all values initialised as 0 or empty strings
        this.mName = "";
        this.mDateSurveyed = 0l;
        this.mLength = 0.0f;
        this.mRating = 0.0f;
        this.mGrade = 0;
        this.mDescription = "";
        this.mLocality = "";
        this.mImageFilePath = "";
        this.mLogFilePath = "";
    }

    //parcelable methods

    public Walk(String Name,    //populate all fields
                long DateSurveyed,
                Float Length,
                float Rating,
                int Grade,
                String Description,
                String Locality,
                String imageFilePath,
                String logFilePath) {
        this.mName = Name;
        this.mDateSurveyed = DateSurveyed;
        this.mLength = Length;
        this.mRating = Rating;
        this.mGrade = Grade;
        this.mDescription = Description;
        this.mLocality = Locality;
        this.mImageFilePath = imageFilePath;
        this.mLogFilePath = logFilePath;
    }

    /**
     * Takes a data parcel from an intent and stores it as a walk
     *
     * @param in = the parcelableExtra
     */
    public Walk(Parcel in) {
        String[] data = new String[9];

        in.readStringArray(data);

        this.mName = data[0];
        this.mDateSurveyed = Long.parseLong(data[1]);
        this.mLength = Float.parseFloat(data[2]);
        this.mRating = Float.parseFloat(data[3]);
        this.mGrade = Integer.parseInt(data[4]);
        this.mDescription = data[5];
        this.mLocality = data[6];
        this.mImageFilePath = data[7];
        this.mLogFilePath = data[8];

    }

    @Override
    public int describeContents() {

        return 0;
    }

    /**
     * saves a walk as a data parcel to be added to an intentExtra
     *
     * @param dest  = the parcelableExtra
     * @param flags = not used
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(new String[]{
                this.mName,
                String.valueOf(this.mDateSurveyed),
                String.valueOf(this.mLength),
                String.valueOf(this.mRating),
                String.valueOf(this.mGrade),
                this.mDescription,
                this.mLocality,
                this.mImageFilePath,
                this.mLogFilePath
        });
    }

    //Getters and setters

    public String getName() {
        return mName;
    }

    public void setName(String Name) {
        this.mName = Name;
    }

    public long getDateSurveyed() {
        return mDateSurveyed;
    }

    public void setDateSurveyed(Long DateSurveyed) {
        this.mDateSurveyed = DateSurveyed;
    }

    public Float getLength() {
        return mLength;
    }

    public void setLength(Float Length) {
        this.mLength = Length;
    }


    public float getRating() {
        return mRating;
    }

    public void setRating(float Rating) {
        this.mRating = Rating;
    }

    public int getGrade() {
        return mGrade;
    }

    public void setGrade(int Grade) {
        this.mGrade = Grade;
    }

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String Description) {
        this.mDescription = Description;
    }

    public String getLocality() {
        return mLocality;
    }

    public void setLocality(String Locality) {
        this.mLocality = Locality;
    }

    public String getImageFilePath() {
        return mImageFilePath;
    }

    public void setImageFilePath(String imageFilePath) {
        this.mImageFilePath = imageFilePath;
    }

    public String getLogFilePath() {
        return mLogFilePath;
    }

    public void setLogFilePath(String logFilePath) {
        this.mLogFilePath = logFilePath;
    }
}