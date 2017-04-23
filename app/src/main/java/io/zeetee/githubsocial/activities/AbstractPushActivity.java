package io.zeetee.githubsocial.activities;

import android.content.Intent;
import android.view.MenuItem;

import io.zeetee.githubsocial.R;

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
            case R.id.action_search:
                Intent intent = new Intent(this,SearchActivity.class);
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
