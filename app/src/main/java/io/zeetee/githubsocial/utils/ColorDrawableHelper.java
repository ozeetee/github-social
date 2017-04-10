package io.zeetee.githubsocial.utils;

import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;

import io.zeetee.githubsocial.GSApp;
import io.zeetee.githubsocial.R;

/**
 * By GT.
 */

public class ColorDrawableHelper {
    private static ColorDrawableHelper instance;
    private int[] colorsArr;
    private int drawableSize;
    private Drawable[] colorDrawables;

    private ColorDrawableHelper(){
        colorsArr = GSApp.getCurrentInstance().getResources().getIntArray(R.array.placeholder_colors);
        drawableSize = GSApp.getCurrentInstance().getResources().getDimensionPixelOffset(R.dimen.drawable_size);
        colorDrawables = new Drawable[colorsArr.length];
    }

    public static ColorDrawableHelper getInstance() {
        if(instance == null) instance = new ColorDrawableHelper();
        return instance;
    }

    public Drawable getColorDrawableForString(String string){
        int hashCode = 0;
        if(string != null) hashCode = string.hashCode();
        int offset = Math.abs(hashCode % colorsArr.length);

        if(colorDrawables[offset] == null){
//            Drawable drawable = GSApp.getCurrentInstance().getResources().getDrawable(R.drawable.circle_drawable);
//            drawable.setColorFilter(colorsArr[offset], PorterDuff.Mode.SRC_ATOP);
            GradientDrawable drawable = new GradientDrawable();
            drawable.setColor(colorsArr[offset]);
            drawable.setBounds(0,0,drawableSize,drawableSize);
            colorDrawables[offset] = drawable;
        }
        return colorDrawables[offset];
//
//        GradientDrawable bgColorDrawable = new GradientDrawable();
//        bgColorDrawable.setColor(colorsArr[offset]);
//        return bgColorDrawable;

    }

}
