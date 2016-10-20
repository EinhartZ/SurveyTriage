package com.qaelum.einhart.qaelumsurvey;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;

/**
 * Created by Einhart on 10/17/2016.
 */

public class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preference);

        EditTextPreference questionTxt = (EditTextPreference)findPreference("pref_questionTxt");
        questionTxt.setSummary(questionTxt.getText());

        EditTextPreference submitURL = (EditTextPreference)findPreference("pref_submitURL");
        submitURL.setSummary(submitURL.getText());

        EditTextPreference location = (EditTextPreference)findPreference("pref_location");
        location.setSummary(location.getText());

        EditTextPreference questionID = (EditTextPreference) findPreference("pref_questionID");
        questionID.setSummary(questionID.getText());

        EditTextPreference questionnaireID = (EditTextPreference) findPreference("pref_questionnaireID");
        questionnaireID.setSummary(questionnaireID.getText());

        EditTextPreference password = (EditTextPreference) findPreference("pref_password");
        password.setSummary(password.getText());

        Preference button = findPreference("pref_ok");
        button.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent intent = new Intent(getActivity(), MainActivity.class);
                getActivity().startActivity(intent);
                return true;
            }
        });
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Preference pref = findPreference(key);
        if(pref instanceof EditTextPreference) {
            EditTextPreference editTextPreference = (EditTextPreference) pref;
            pref.setSummary(editTextPreference.getText());
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }
}
