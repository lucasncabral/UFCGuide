package com.example.lucas.esapp.Fragment;

import android.app.DialogFragment;
import android.app.Fragment;
import android.os.Bundle;
import android.preference.PreferenceFragment;

import com.example.lucas.esapp.R;

/**
 * Created by Lucas on 05/08/2016.
 */
public class SettingsFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.activity_settings);
    }


}
