package com.gdglima.multimedia.appmultimedia.utils;

import android.util.Log;

import com.gdglima.multimedia.appmultimedia.BuildConfig;


/**
 * Created by emedinaa on 10/13/14.
 */
public class LogUtils {

    private static final String LOG_PREFIX = "LOG_OSP";
    private static final int LOG_PREFIX_LENGTH = LOG_PREFIX.length();
    private static final int MAX_LOG_TAG_LENGTH = 23;

    public static String makeLogTag(String str) {
        if (str.length() > MAX_LOG_TAG_LENGTH - LOG_PREFIX_LENGTH) {
            return LOG_PREFIX + str.substring(0, MAX_LOG_TAG_LENGTH - LOG_PREFIX_LENGTH - 1);
        }

        return LOG_PREFIX + str;
    }

    public static String makeLogTag(Class cls) {
        return makeLogTag(cls.getSimpleName());
    }

    public static void LOGI(final String tag, String message) {
        if (BuildConfig.DEBUG || false || Log.isLoggable(tag, Log.DEBUG)) {
            Log.i(tag, message);
        }
    }

    public static void LOGI(final String tag, String message, Throwable cause) {
        if (BuildConfig.DEBUG || false || Log.isLoggable(tag, Log.DEBUG)) {
            Log.i(tag, message, cause);
        }
    }
}
