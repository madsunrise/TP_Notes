package com.rv150.notes.Activities;

import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.v7.app.AppCompatActivity;

import com.rv150.notes.R;
import com.rv150.notes.ThemeChanger;

/**
 * Created by Rudnev on 29.11.2016.
 */

public class SettingsActivity extends AppCompatActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ThemeChanger.onActivityCreateSetTheme(this);
        setContentView(R.layout.pref_with_toolbar);
        android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.pref_toolbar);
        setSupportActionBar(toolbar);

        getFragmentManager().beginTransaction().replace(R.id.content_frame, new MyPreferenceFragment()).commit();
    }


    public static class MyPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(final Bundle savedInstanceState){
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);
            final CheckBoxPreference darkTheme = (CheckBoxPreference) findPreference("dark_theme");
            darkTheme.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    if (darkTheme.isChecked()) {    // Выбрали темную тему
                        ThemeChanger.changeToTheme(getActivity(), ThemeChanger.THEME_DARK);
                    }
                    else {
                        ThemeChanger.changeToTheme(getActivity(), ThemeChanger.THEME_LIGHT);
                    }
                    return true;
                }
            });
        }
    }
}