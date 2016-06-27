package com.lee42.wheeledwalksviewer.activities;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.lee42.wheeledwalksviewer.R;
import com.lee42.wheeledwalksviewer.activities.fragments.NewWalksFragment;
import com.lee42.wheeledwalksviewer.activities.fragments.SavedWalksFragment;
import com.lee42.wheeledwalksviewer.activities.fragments.UserInfoFragment;
import com.lee42.wheeledwalksviewer.controllers.NewWalksDatabaseHelper;
import com.lee42.wheeledwalksviewer.models.Constants;
import com.lee42.wheeledwalksviewer.models.NewWalkDbConstants;
import com.lee42.wheeledwalksviewer.models.Walk;

import java.util.ArrayList;
import java.util.List;

public class ViewerMainActivity extends AppCompatActivity {

    public static FragmentManager mFragmentManager;
    private NewWalksDatabaseHelper mNewWalksDatabaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewer_main);

        //initialise Database Helpers
        mNewWalksDatabaseHelper = new NewWalksDatabaseHelper(this);

        //TODO: Delete Bluetooth device Db if necessary
        //getApplication().deleteDatabase(NewWalkDbConstants.DATABASE_NAME);

        //set the context for accessing firebase
        Firebase.setAndroidContext(this);
        Firebase walkInfoRef = new Firebase("https://wheeledwalks.firebaseio.com/Walk_Info/Walks");

        walkInfoRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                Log.d(Constants.DEBUG_TAG,"There are " + snapshot.getChildrenCount() + " walks in the online database");
                getApplication().deleteDatabase(NewWalkDbConstants.DATABASE_NAME);
                for (DataSnapshot postSnapshot: snapshot.getChildren()) {
                    Walk walk = postSnapshot.getValue(Walk.class);
                    if (mNewWalksDatabaseHelper.getWalkObject(walk.getName()) == null){
                        Log.d(Constants.DEBUG_TAG,"Adding walk " + walk.getName() + " to database");
                        mNewWalksDatabaseHelper.addWalk(walk);
                    } else {
                        Log.d(Constants.DEBUG_TAG,"Walk " + walk.getName() + " already exists in database");
                    }

                }
            }
            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Log.d(Constants.DEBUG_TAG,"The read failed: " + firebaseError.getMessage());
            }
        });


        ViewPager mViewPager = (ViewPager) findViewById(R.id.viewer_viewpager);
        setupViewPager(mViewPager);

        TabLayout mTabLayout = (TabLayout) findViewById(R.id.viewer_tabs);
        mTabLayout.setupWithViewPager(mViewPager);
    }


    /**
     * Adds fragments to the tabbed view pager
     *
     * @param viewPager - the pager to add the fragments to
     */
    private void setupViewPager(ViewPager viewPager) {
        mFragmentManager = getSupportFragmentManager();
        ViewPagerAdapter adapter = new ViewPagerAdapter(mFragmentManager);
        adapter.addFragment(new NewWalksFragment(), Constants.TAB_HEADER_NEW_WALKS);
        adapter.addFragment(new SavedWalksFragment(), Constants.TAB_HEADER_SAVED_WALKS);
        adapter.addFragment(new UserInfoFragment(), Constants.TAB_HEADER_INFO);
        viewPager.setAdapter(adapter);
    }


    /**
     * This is the Tab layout view pager that handles and populates the tabbed fragments
     */
    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {

            super(manager);
        }

        @Override
        public Fragment getItem(int position) {

            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {

            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {

            return mFragmentTitleList.get(position);
        }


    }




}