package com.redbassett.popwatch;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.redbassett.popwatch.sync.PopwatchSyncAdapter;


public class MainActivity extends AppCompatActivity {
    /**
     * LOG_TAG provides a constant to pass to Log methods indicating the class that the log
     * message was generated in.
     */
    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        PopwatchSyncAdapter.initializeSyncAdapter(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            /** In preparation for a release, this menu should not be shown to the user.
                This code is not removed as it will be used in later development (and this app
                hasn't implemented feature flags yet).
            case R.id.action_refresh:
                updateMovieFeed();
                return true; **/
            /** Settings removed until other options are added
            case R.id.action_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                return true; **/
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStart() {
        super.onStart();
    }
}
