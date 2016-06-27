package com.lee42.wheeledwalksviewer.activities.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lee42.wheeledwalksviewer.R;

/**
 * Created by David on 18/05/2016.
 */
public class SavedWalksFragment extends Fragment {
    private View mView;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onResume() {
        super.onResume();


    }

    @Override
    public void onPause() {
        super.onPause();
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fregment_saved_walks, container, false);
//        mLattitudeTv = (TextView) mView.findViewById(R.id.raw_gps_lattitude_tv);
//        mLongitudeTv = (TextView) mView.findViewById(R.id.raw_gps_longitude_tv);
//        mAccelXTv = (TextView) mView.findViewById(R.id.raw_accel_x_tv);
//        mAccelYTv = (TextView) mView.findViewById(R.id.raw_accel_y_tv);
//        mAccelZTv = (TextView) mView.findViewById(R.id.raw_accel_z_tv);
//        mMedianAccelXTv = (TextView) mView.findViewById(R.id.median_accel_x_tv);
//        mMedianAccelYTv = (TextView) mView.findViewById(R.id.median_accel_y_tv);
//        mMedianAccelZTv = (TextView) mView.findViewById(R.id.median_accel_z_tv);

        return mView;
    }
}
