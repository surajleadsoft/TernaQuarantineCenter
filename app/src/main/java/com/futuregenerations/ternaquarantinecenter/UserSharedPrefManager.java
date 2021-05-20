package com.futuregenerations.ternaquarantinecenter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

public class UserSharedPrefManager {

    public static final String USER_SHARED_PREF_USER_NAME = "usersharedprefusername";
    public static final String USER_SHARED_PREF_NAME = "usersharedprefmanager";
    public static final String USER_SHARED_PREF_USER_EMAIL = "usersharedprefuseremail";
    public static final String USER_SHARED_PREF_USER_CENTER_TYPE = "usersharedprefusercentertype";
    public static final String USER_SHARED_PREF_USER_CENTER = "usersharedprefusercenter";
    public static final String USER_SHARED_PREF_USER_TYPE = "usersharedprefusertype";

    @SuppressLint("StaticFieldLeak")
    private static UserSharedPrefManager mInstance;

    @SuppressLint("StaticFieldLeak")
    private static Context mContext;

    public UserSharedPrefManager(Context context) {
        mContext = context;
    }

    public static synchronized UserSharedPrefManager getInstance(Context context) {
        if (mInstance == null) {
            mInstance =new UserSharedPrefManager(context);
        }
        return mInstance;
    }

    public void userData(UserSharedPrefData data) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(USER_SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(USER_SHARED_PREF_USER_NAME,data.getName());
        editor.putString(USER_SHARED_PREF_USER_EMAIL,data.getEmail());
        editor.putString(USER_SHARED_PREF_USER_CENTER_TYPE,data.getCenterType());
        editor.putString(USER_SHARED_PREF_USER_CENTER,data.getCenter());
        editor.putString(USER_SHARED_PREF_USER_TYPE,data.getUserType());
        editor.apply();
    }

    public UserSharedPrefData getData() {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(USER_SHARED_PREF_NAME, Context.MODE_PRIVATE);

        return new UserSharedPrefData(
                sharedPreferences.getString(USER_SHARED_PREF_USER_NAME,null),
                sharedPreferences.getString(USER_SHARED_PREF_USER_EMAIL,null),
                sharedPreferences.getString(USER_SHARED_PREF_USER_CENTER_TYPE,null),
                sharedPreferences.getString(USER_SHARED_PREF_USER_CENTER,null),
                sharedPreferences.getString(USER_SHARED_PREF_USER_TYPE,null)
        );
    }

    public void clearData() {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(USER_SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }

    public boolean isLoggedIn() {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(USER_SHARED_PREF_NAME,Context.MODE_PRIVATE);
        return sharedPreferences.getString(USER_SHARED_PREF_USER_EMAIL,null)!=null;
    }

    public boolean getUserType() {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(USER_SHARED_PREF_NAME,Context.MODE_PRIVATE);
        String type = sharedPreferences.getString(USER_SHARED_PREF_USER_TYPE,null);
        return mContext.getString(R.string.officer).equals(type);
    }
}
