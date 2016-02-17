package org.catrobat.catroid.utils;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Pair;
import android.view.ViewConfiguration;
import android.view.WindowManager;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.common.ScreenValues;

/**
 * Created by nxp67657 on 05.02.2016.
 */

public class ScreenSizeManager {

    private static ScreenSizeManager instance;
    Context mContext;

    public void setContext(Context context) {
        this.mContext = context;
    }

    public static ScreenSizeManager getInstance() {
        if (instance == null)
            instance = new ScreenSizeManager();

        return instance;
    }

    public Pair<Integer, Integer> getRealScreenSize() {
        DisplayMetrics metrics = new DisplayMetrics();
        WindowManager windowManager = (WindowManager)
                mContext.getSystemService(Context.WINDOW_SERVICE);

        boolean hardwareButtons = ViewConfiguration.get(mContext).hasPermanentMenuKey();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1 && hardwareButtons) {
            windowManager.getDefaultDisplay().getRealMetrics(metrics);
            return new Pair<Integer, Integer>(metrics.widthPixels, metrics.heightPixels);
        }
        windowManager.getDefaultDisplay().getMetrics(metrics);
        return new Pair<Integer, Integer>(metrics.widthPixels, metrics.heightPixels);
    }

    public Pair<Integer, Integer> getScreenMiddlePoint() {
        Pair<Integer, Integer> middlePoint = getRealScreenSize();
        return new Pair<Integer, Integer>(middlePoint.first / 2, middlePoint.second / 2);
    }

    public void updateScreenSize() {
        Pair<Integer, Integer> update = getRealScreenSize();
        ScreenValues.SCREEN_HEIGHT = update.second + 1;
        ScreenValues.SCREEN_WIDTH = update.first + 1;
    }

    public boolean isLandscape() {
        if(mContext.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
            return true;
        }
        return false;
    }

}