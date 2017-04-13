package io.zeetee.githubsocial.activities;

import android.os.Bundle;

import io.reactivex.functions.Consumer;
import io.zeetee.githubsocial.R;

/**
 * By GT.
 */

public class AboutActivity extends AbstractPushActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
    }


    @Override
    public void hideContent() {

    }

    @Override
    public void showContent() {

    }

    @Override
    public Consumer<Object> getRxBusConsumer() {
        return null;
    }
}
