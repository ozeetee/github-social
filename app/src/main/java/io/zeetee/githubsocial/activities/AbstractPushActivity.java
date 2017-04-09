package io.zeetee.githubsocial.activities;

import android.view.MenuItem;

/**
 * By GT.
 */
public abstract class AbstractPushActivity extends AbstractBaseActivity{

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
