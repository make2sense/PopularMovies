package lmcloud.popularmovies;

import android.os.Bundle;
import android.preference.PreferenceFragment;

/**
 * Reference: http://developer.android.com/reference/android/preference/PreferenceFragment.html
 */
public class PrefsFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);
    }
}