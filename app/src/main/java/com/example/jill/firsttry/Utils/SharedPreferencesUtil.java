package com.example.jill.firsttry.Utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by smile on 02/03/2018.
 */

public class SharedPreferencesUtil {
    public static final String TAG = "SharedPreferencesUtil";
    private static final String USER_TOKEN = "USER_TOKEN";

    private static SharedPreferences mPreferences;
    private static SharedPreferences.Editor mEditor;
    private static SharedPreferencesUtil mSharedPreferencesUtil;
    private final Context context;

    public SharedPreferencesUtil(Context context) {

        this.context = context.getApplicationContext();
        mPreferences =   this.context.getSharedPreferences(TAG, Context.MODE_PRIVATE);
        mEditor = mPreferences.edit();
    }

    public static SharedPreferencesUtil getInstance(Context context) {
        if (mSharedPreferencesUtil ==null){
            mSharedPreferencesUtil =new SharedPreferencesUtil(context);
        }
        return  mSharedPreferencesUtil;
    }

    public void put(String key, String value) {
        mEditor.putString(key,value);
        mEditor.commit();
    }

    public String get(String key) {
        return mPreferences.getString(key,"");
    }

    public static SharedPreferencesUtil getCurrentInstance() {
        return  mSharedPreferencesUtil;
    }

    public  void setToken(String token) {
        put(USER_TOKEN,token);
    }

    public String getToken() {
        return get(USER_TOKEN);
    }

}
