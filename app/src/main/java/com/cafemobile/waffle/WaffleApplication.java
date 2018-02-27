package com.cafemobile.waffle;

import android.app.Application;
import android.content.res.Configuration;
import android.view.Display;
import android.view.WindowManager;

/**
 * Created by SungHyun on 2018-02-21.
 */

public class WaffleApplication extends Application{
    public static final String SERVER_BASE_PATH = "http://13.124.188.3/waffle_test/";    //http://13.124.188.3/waffle/      http://13.124.188.3/waffle_test/api/v1/
    public static final String SERVER_API_PATH = "http://13.124.188.3/waffle_test/api/v1/";
    public static int DISPLAY_WIDTH;
    public static int DISPLAY_HEIGHT;

    @Override
    public void onCreate() {
        super.onCreate();
        Display display;
        display = ((WindowManager)getApplicationContext().getSystemService(getApplicationContext().WINDOW_SERVICE)).getDefaultDisplay();
        DISPLAY_HEIGHT = display.getHeight();
        DISPLAY_WIDTH = display.getWidth();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }

    public int getDISPLAY_WIDTH() {
        return DISPLAY_WIDTH;
    }

    public int getDISPLAY_HEIGHT() {
        return DISPLAY_HEIGHT;
    }
}
