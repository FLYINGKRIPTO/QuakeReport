package com.example.android.quakereport;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

public class SettingsActivity extends AppCompatActivity {
    private static final String TAG = SettingsActivity.class.getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Log.e(TAG, "onCreate: in SettingsActivity OnCreate" );

    }
    public static class EarthquakePreferenceFragment extends PreferenceFragment implements Preference.OnPreferenceChangeListener{
        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.settings_main);

            Preference minMagnitude = findPreference(getString(R.string.settings_min_magnitude_key));
            bindPreferenceSummaryToValue(minMagnitude);
            Preference orderBy = findPreference(getString(R.string.settings_order_by_key));
            bindPreferenceSummaryToValue(orderBy);
        }

        private void bindPreferenceSummaryToValue(Preference preference) {
            preference.setOnPreferenceChangeListener(this);
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(preference.getContext());
            String preferenceString = preferences.getString(preference.getKey(), "");
            onPreferenceChange(preference, preferenceString);
        }

        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            // The code in this method takes care of updating the displayed preference summary after it has been changed
            String stringValue = newValue.toString();
            if(preference instanceof ListPreference){
               ListPreference listPreference  = (ListPreference) preference;
               int prefIndex = listPreference.findIndexOfValue(stringValue);
               if(prefIndex>=0){
                   CharSequence[] label = listPreference.getEntries();preference.setSummary(label[prefIndex]);

               }

               }
            else {
                preference.setSummary(stringValue);

            }
            return true;
        }
    }
}
