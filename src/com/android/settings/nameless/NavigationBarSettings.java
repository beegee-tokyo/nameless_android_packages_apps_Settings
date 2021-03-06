/*
 * Copyright (C) 2013-2014 The NamelessROM Project
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses
 */

package com.android.settings.nameless;

import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;
import android.provider.Settings;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.Utils;

// **** BEEGEE_PATCH_START ****
import android.preference.CheckBoxPreference;
import android.app.Activity;
import android.view.View;
import android.util.Slog;
// **** BEEGEE_PATCH_END ****

public class NavigationBarSettings extends SettingsPreferenceFragment implements
        Preference.OnPreferenceChangeListener {
    private static final String CATEGORY_NAVBAR = "navigation_bar";

    private static final String KEY_NAVIGATION_BAR_HEIGHT = "navigation_bar_height";
    private static final String KEY_NAVIGATION_BAR_WIDTH  = "navigation_bar_width";

    private ListPreference mNavigationBarHeight;
    private ListPreference mNavigationBarWidth;

    // **** BEEGEE_PATCH_START ****
    private static final String KEY_NAV_BAR_POS = "nav_position";
    private static final String KEY_NAV_BAR_LEFT = "navigation_bar_left";
    private ListPreference mNavPos;
    // **** BEEGEE_PATCH_END ****

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.navigation_bar_settings);

        PreferenceScreen prefScreen = getPreferenceScreen();
        PreferenceCategory category;

        final boolean hasRealNavigationBar = getResources()
                .getBoolean(com.android.internal.R.bool.config_showNavigationBar);
        if (hasRealNavigationBar) { // only disable on devices with REAL navigation bars
            category = (PreferenceCategory) findPreference("navigation_bar_force");
            if (category != null) {
                prefScreen.removePreference(category);
            }
        }

        category = (PreferenceCategory) findPreference(CATEGORY_NAVBAR);
        mNavigationBarHeight = (ListPreference) findPreference(KEY_NAVIGATION_BAR_HEIGHT);
        mNavigationBarHeight.setOnPreferenceChangeListener(this);

        mNavigationBarWidth = (ListPreference) findPreference(KEY_NAVIGATION_BAR_WIDTH);
        if (!Utils.isPhone(getActivity())) {
            category.removePreference(mNavigationBarWidth);
            mNavigationBarWidth = null;
        } else {
            mNavigationBarWidth.setOnPreferenceChangeListener(this);
        }

        /**** BEEGEE_PATCH_START ****/
        mNavPos = (ListPreference) prefScreen.findPreference(KEY_NAV_BAR_POS);
        CheckBoxPreference mNavbarleft = (CheckBoxPreference) prefScreen.findPreference(KEY_NAV_BAR_LEFT);
        PreferenceCategory notificationsCategory = (PreferenceCategory) findPreference("navigation_bar");
//Slog.d("NavBarPos","device_type = "+device_type);
        if (Utils.isTablet(getActivity())) {
            notificationsCategory.removePreference(mNavbarleft);
            Settings.System.putInt(getContentResolver(), Settings.System.NAV_BAR_POS, 4);
            mNavPos.setOnPreferenceChangeListener(this);
        } else {
            notificationsCategory.removePreference(mNavPos);
        }
        /**** BEEGEE_PATCH_END ****/

        updateDimensionValues();
    }

    public boolean onPreferenceChange(final Preference preference, final Object objValue) {
        if (preference == mNavigationBarWidth) {
            Settings.System.putInt(getContentResolver(),
                    Settings.System.NAVIGATION_BAR_WIDTH,
                    Integer.parseInt((String) objValue));
            return true;
        } else if (preference == mNavigationBarHeight) {
            Settings.System.putInt(getContentResolver(),
                    Settings.System.NAVIGATION_BAR_HEIGHT,
                    Integer.parseInt((String) objValue));
            return true;
        }
        // **** BEEGEE_PATCH_START ****
        if (preference == mNavPos) {
            int mNavPosSel = 2;
            if (objValue.toString().equals("left")) {
					mNavPosSel = 0;
            } else if (objValue.toString().equals("right")) {
					mNavPosSel = 1;
            }
            Settings.System.putInt(getContentResolver(), Settings.System.NAV_BAR_POS, mNavPosSel);
//Slog.d("NavBarPos","Selected Position = "+mNavPosSel);
//Slog.d("NavBarPos","Selected Position String = "+objValue.toString());
           return true;
        }
        // **** BEEGEE_PATCH_END ****
        return false;
    }

    private void updateDimensionValues() {
        int navigationBarHeight = Settings.System.getInt(getContentResolver(),
                Settings.System.NAVIGATION_BAR_HEIGHT, -2);
        if (navigationBarHeight == -2) {
            navigationBarHeight = (int) (getResources().getDimension(
                    com.android.internal.R.dimen.navigation_bar_height)
                    / getResources().getDisplayMetrics().density);
        }
        mNavigationBarHeight.setValue(String.valueOf(navigationBarHeight));

        if (mNavigationBarWidth == null) {
            return;
        }
        int navigationBarWidth = Settings.System.getInt(getContentResolver(),
                Settings.System.NAVIGATION_BAR_WIDTH, -2);
        if (navigationBarWidth == -2) {
            navigationBarWidth = (int) (getResources().getDimension(
                    com.android.internal.R.dimen.navigation_bar_width)
                    / getResources().getDisplayMetrics().density);
        }
        mNavigationBarWidth.setValue(String.valueOf(navigationBarWidth));
    }

}
