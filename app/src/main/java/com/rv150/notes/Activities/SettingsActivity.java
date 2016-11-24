package com.rv150.notes.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;

import com.rv150.notes.Constants;
import com.rv150.notes.R;
import com.rv150.notes.Utils;

/**
 * Created by Rudnev on 23.11.2016.
 */

public class SettingsActivity extends PreferenceActivity {

    enum Colors { BLUE, GREEN, RED }

   static boolean lightTheme = true;
   static boolean wasThemeChanged = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.onActivityCreateSetTheme(this);
//        getFragmentManager().beginTransaction()
//                .replace(android.R.id.content, new PrefsFragment()).commit();
        addPreferencesFromResource(R.xml.preferences);
        Preference checkbox = findPreference("checkbox");
        checkbox.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                changeTheme();
                wasThemeChanged = true;
                return true;
            }
        });
    }

    public static class PrefsFragment extends PreferenceFragment {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            // Load the preferences from an XML resource
            addPreferencesFromResource(R.xml.preferences);
        }
    }

    private void changeTheme() {
        if (!lightTheme) {
            Utils.changeToTheme(this, Utils.THEME_DEFAULT);
        }
        else {
            Utils.changeToTheme(this, Utils.THEME_DARK);
        }
        lightTheme = !lightTheme;
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra(Constants.WAS_THEME_CHANGED, wasThemeChanged);
        setResult(RESULT_OK, intent);
        finish();
        wasThemeChanged = false; // для будущих запусков
    }












//    private void setColors(Colors color) {
//        int toolbarColor = 0;
//        int statusbarColor = 0;
//        switch (color) {
//            case BLUE:
//                setTheme(R.style.AppTheme_NoActionBar_Green);
//                toolbarColor = R.color.green;
//                statusbarColor = R.color.greenDark;
//                break;
//            case GREEN:
//                setTheme(R.style.AppTheme_NoActionBar_Red);
//                toolbarColor = R.color.red;
//                statusbarColor = R.color.redDark;
//                break;
//            case RED:
//                setTheme(R.style.AppTheme_NoActionBar_Blue);
//                toolbarColor = R.color.blue;
//                statusbarColor = R.color.blueDark;
//                break;
//        }
//        mToolbar.setBackgroundColor(ContextCompat.getColor(MainActivity.this, toolbarColor));
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            getWindow().setStatusBarColor(ContextCompat.getColor(MainActivity.this, statusbarColor));
//        }
//    }
}
