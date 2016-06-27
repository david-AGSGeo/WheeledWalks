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
 * Created by David Lee on 13/10/2015.
 * Parcelable class for storing XYZ accelerometer data
 */
public class AccelerometerData implements Parcelable {

    public static final Parcelable.Creator<AccelerometerData> CREATOR =
            new Parcelable.Creator<AccelerometerData>() {

                @Override
                public AccelerometerData createFromParcel(Parcel source) {

                    return new AccelerometerData(source);  //using parcelable constructor
                }

                @Override
                public AccelerometerData[] newArray(int size) {

                    return new AccelerometerData[size];
                }
            };
    private Long mTimestamp;
    private float mAccelX;
    private float mAccelY;
    private float mAccelZ;
    //TODO: the following will be used with the sensortag data in v1.2
    private float mGyroX;
    private float mGyroY;
    private float mGyroZ;
    private float mCompassX;
    private float mCompassY;
    private float mCompassZ;


    public AccelerometerData() {
        mAccelX = 1;
        mAccelY = 2;
        mAccelZ = 3;
    }

    //parcelable methods
    public AccelerometerData(Parcel in) {
        String[] data = new String[4];

        in.readStringArray(data);

        this.mTimestamp = Long.parseLong(data[0]);
        this.mAccelX = Float.parseFloat(data[1]);
        this.mAccelY = Float.parseFloat(data[2]);
        this.mAccelZ = Float.parseFloat(data[3]);


    }

    @Override
    public int describeContents() {

        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(new String[]{

                String.valueOf(this.mTimestamp),
                String.valueOf(this.mAccelX),
                String.valueOf(this.mAccelY),
                String.valueOf(this.mAccelZ),

        });
    }

    @Override
    public String toString() {
        return String.format("X:%f,Y:%f,Z:%f", mAccelX, mAccelY, mAccelZ);
    }

    //Getters and Setters


    public Long getTimestamp() {
        return mTimestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.mTimestamp = timestamp;
    }

    public float getAccelX() {
        return mAccelX;
    }

    public void setAccelX(float accelX) {
        this.mAccelX = accelX;
    }

    public float getAccelY() {
        return mAccelY;
    }

    public void setAccelY(float accelY) {
        this.mAccelY = accelY;
    }

    public float getAccelZ() {
        return mAccelZ;
    }

    public void setAccelZ(float accelZ) {
        this.mAccelZ = accelZ;
    }

    public float getGyroX() {
        return mGyroX;
    }

    public void setGyroX(float gyroX) {
        this.mGyroX = gyroX;
    }

    public float getGyroY() {
        return mGyroY;
    }

    public void setGyroY(float gyroY) {
        this.mGyroY = gyroY;
    }

    public float getGyroZ() {
        return mGyroZ;
    }

    public void setGyroZ(float gyroZ) {
        this.mGyroZ = gyroZ;
    }

    public float getCompassX() {
        return mCompassX;
    }

    public void setCompassX(float compassX) {
        this.mCompassX = compassX;
    }

    public float getCompassY() {
        return mCompassY;
    }

    public void setCompassY(float compassY) {
        this.mCompassY = compassY;
    }

    public float getCompassZ() {
        return mCompassZ;
    }

    public void setCompassZ(float compassZ) {
        this.mCompassZ = compassZ;
    }
}
