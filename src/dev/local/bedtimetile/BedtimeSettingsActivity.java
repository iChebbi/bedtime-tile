package dev.local.bedtimetile;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;

/** Long-pressing the tile lands here; it forwards to Digital Wellbeing's Bedtime mode screen and closes. */
public class BedtimeSettingsActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            startActivity(new Intent("com.google.android.apps.wellbeing.action.WIND_DOWN")
                    .setPackage("com.google.android.apps.wellbeing"));
        } catch (ActivityNotFoundException ignored) {
            // Digital Wellbeing is missing or changed its screen: nothing to open.
        }
        finish();
    }
}
