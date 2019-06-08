package com.blakezheng.widget.cordiformview;

import android.content.Context;
import android.os.Build;

import androidx.annotation.ColorRes;
import androidx.annotation.NonNull;

/**
 * Created by BlackZheng on 2019/6/2.
 */
public class ColorUtils {
    public static int getColor(@NonNull Context context, @ColorRes int id) {
        return Build.VERSION.SDK_INT >= 23 ? context.getColor(id) : context.getResources().getColor(id);
    }
}
