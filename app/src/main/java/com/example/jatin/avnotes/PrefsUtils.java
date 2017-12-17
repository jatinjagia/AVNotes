package com.example.jatin.avnotes;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by jatin on 17/12/17.
 */

public class PrefsUtils {

    private static PrefsUtils mPrefUtils;
    public static final String MyPREFERENCES = "MyPrefs";
    private SharedPreferences mSharedPreferences;
    protected Context mContext;
    private SharedPreferences.Editor mSharedPreferencesEditor;

    private PrefsUtils(Context context) {
        mContext = context;
        mSharedPreferences = context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        mSharedPreferencesEditor = mSharedPreferences.edit();
    }

    public static synchronized PrefsUtils getInstance(Context context) {

        if (mPrefUtils == null) {
            mPrefUtils = new PrefsUtils(context.getApplicationContext());
        }
        return mPrefUtils;
    }

    public void setValue(String key, Boolean value) {
        mSharedPreferencesEditor.putBoolean(key, value);
        mSharedPreferencesEditor.commit();
    }

    public boolean getValue(String key){
        return mSharedPreferences.getBoolean(key,false);
    }

    public boolean hasValue(String key){
        return mSharedPreferences.contains(key);
    }
}
