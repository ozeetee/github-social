package io.zeetee.githubsocial.utils;

import android.graphics.Typeface;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;

/**
 * By GT.
 */

public class LinkSpan extends ClickableSpan {

    @Override
    public void onClick(View widget) {

    }

    @Override
    public void updateDrawState(TextPaint ds) {
        super.updateDrawState(ds);
        ds.setUnderlineText(false);
        ds.setTypeface(Typeface.create(ds.getTypeface(), Typeface.BOLD));
        ds.setColor(0xff336699);
    }
}
