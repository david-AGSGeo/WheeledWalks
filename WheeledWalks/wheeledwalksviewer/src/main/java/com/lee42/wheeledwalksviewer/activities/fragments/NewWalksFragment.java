package com.lee42.wheeledwalksviewer.activities.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lee42.wheeledwalksviewer.R;
import com.lee42.wheeledwalksviewer.controllers.NewWalkCardViewAdapter;
import com.lee42.wheeledwalksviewer.controllers.NewWalksDatabaseHelper;

/**
 * Created by David on 18/05/2016.
 */
public class NewWalksFragment extends Fragment {

    private View mView;
    private RecyclerView mNewWalksRecyclerView;
    private NewWalksDatabaseHelper mNewWalksDatabaseHelper;

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

        mView = inflater.inflate(R.layout.fragment_new_walks, container, false);

        mNewWalksDatabaseHelper = new NewWalksDatabaseHelper(mView.getContext());
        mNewWalksRecyclerView = (RecyclerView) mView.findViewById(R.id.new_walk_card_list);
        mNewWalksRecyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mView.getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mNewWalksRecyclerView.setLayoutManager(linearLayoutManager);

        NewWalkCardViewAdapter newWalkCardViewAdapter = new NewWalkCardViewAdapter
                (mView.getContext(), mNewWalksDatabaseHelper.getAllWalks());
        mNewWalksRecyclerView.setAdapter(newWalkCardViewAdapter);
        return mView;
    }
}
