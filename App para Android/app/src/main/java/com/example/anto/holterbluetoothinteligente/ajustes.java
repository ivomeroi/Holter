package com.example.anto.holterbluetoothinteligente;

import android.os.Bundle;

import androidx.preference.EditTextPreference;
import androidx.preference.PreferenceFragmentCompat;

public class ajustes extends PreferenceFragmentCompat {
    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
        addPreferencesFromResource(R.xml.ajustes);
    }
}