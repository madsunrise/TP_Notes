package com.rv150.notes;

/**
 * Created by Rudnev on 24.11.2016.
 */

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class ThemeChanger
{
    public final static int THEME_LIGHT = 0;
    public final static int THEME_DARK = 1;
    private static int sTheme = -1;

    // Сменить сразу тему активити
    public static void changeToTheme(Activity activity, int theme)
    {
        sTheme = theme;
        activity.finish();
        activity.startActivity(new Intent(activity, activity.getClass()));
    }

    // Выставить активити тему при его создании
    public static void onActivityCreateSetTheme(Activity activity)
    {
        if (sTheme == -1) { // Загрузка текущей темы из настроек при запуске
            SharedPreferences sPrefs = PreferenceManager.getDefaultSharedPreferences(activity);
            boolean isDark = sPrefs.getBoolean("dark_theme", false);
            sTheme = isDark? THEME_DARK : THEME_LIGHT;
        }
        switch (sTheme)
        {
            case THEME_LIGHT:
                activity.setTheme(R.style.AppTheme_NoActionBarLight);
                break;
            case THEME_DARK:
                activity.setTheme(R.style.AppTheme_NoActionBarDark);
                break;
            default:
                break;
        }
    }

    public static int getTheme() {
        return sTheme;
    }
}
