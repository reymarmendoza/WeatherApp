package com.example.android.sunshine.app;

import android.os.Bundle;
import android.preference.PreferenceFragment;

/**
 *
 */

public class SettingFragment extends PreferenceFragment {

    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.fragment_setting);
    }

}
