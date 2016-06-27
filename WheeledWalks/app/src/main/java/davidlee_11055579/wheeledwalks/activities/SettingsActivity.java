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

package davidlee_11055579.wheeledwalks.activities;

import android.os.Bundle;
import android.preference.PreferenceActivity;

import davidlee_11055579.wheeledwalks.R;

/**
 * Created by David Lee on 1/09/2015.
 * handles preferences set from the startscreen. not accessible while recording
 */
public class SettingsActivity extends PreferenceActivity

{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preference); //use deprecated method, as I do not
        //currently need custom headers or preferenceFragments


    }
}
