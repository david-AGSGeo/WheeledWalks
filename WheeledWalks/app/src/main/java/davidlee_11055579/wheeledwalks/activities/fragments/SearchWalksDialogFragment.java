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

package davidlee_11055579.wheeledwalks.activities.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import davidlee_11055579.wheeledwalks.R;
import davidlee_11055579.wheeledwalks.activities.StartScreenActivity;
import davidlee_11055579.wheeledwalks.models.Constants;

/**
 * Created by David Lee on 30/10/2015.
 * Shows a dialog allowing the user to enter a search string which is then used to search the database
 */
public class SearchWalksDialogFragment extends DialogFragment implements View.OnClickListener {

    private EditText mSearchStringEt;


    public static SearchWalksDialogFragment newInstance(String title) {
        SearchWalksDialogFragment frag = new SearchWalksDialogFragment();

        Bundle args = new Bundle();
        args.putString("title", title);
        frag.setArguments(args);

        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.dialog_fragment_search_walks, container, false);
        getDialog().setTitle("Search Walks");

        mSearchStringEt = (EditText) rootView.findViewById(R.id.search_dialog_search_et);

        //listens for the enter key on the keyboard, which now shows a search icon
        mSearchStringEt.setOnKeyListener(new View.OnKeyListener() {

            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_UP) {
                    returnToStartScreen();
                }
                return false;
            }
        });

        ImageButton searchBtn = (ImageButton) rootView.findViewById(R.id.search_dialog_search_button);
        searchBtn.setOnClickListener(this);
        return rootView;
    }

    /**
     * Returns the search string to the start screen via intent
     */
    private void returnToStartScreen() {
        Intent returnToStartScreenIntent = new Intent(getContext(),
                StartScreenActivity.class);
        returnToStartScreenIntent.putExtra(Constants.SEARCH_STRING, mSearchStringEt
                .getText().toString());
        startActivity(returnToStartScreenIntent);
        this.dismiss();
    }


    /**
     * Handles the buttons in a switch statement
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case (R.id.search_dialog_search_button):
                returnToStartScreen();
                break;
            default:
                Log.e(Constants.DEBUG_TAG, "Button not handled correctly");
                break;
        }
    }
}
