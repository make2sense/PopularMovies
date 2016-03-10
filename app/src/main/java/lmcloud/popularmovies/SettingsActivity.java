package lmcloud.popularmovies;

import android.app.Activity;
import android.os.Bundle;

/**
 * Reference: http://developer.android.com/guide/topics/ui/settings.html
 */
public class SettingsActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Display the fragment as the main content.
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();
    }
}