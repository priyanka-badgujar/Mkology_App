package com.mkology.thelearningapp.saveUserData;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.mkology.thelearningapp.apiCalls.UserProfile;

public class SaveSharedPreference {
    static SharedPreferences getPreferences(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static void setLoggedIn(Context context, boolean loggedIn) {
        SharedPreferences.Editor editor = getPreferences(context).edit();
        editor.putBoolean(PreferencesUtility.LOGGED_IN_PREF, loggedIn);
        editor.apply();
    }

    public static boolean getLoggedStatus(Context context) {
        return getPreferences(context).getBoolean(PreferencesUtility.LOGGED_IN_PREF, false);
    }

    public static void setUserData(Context context, UserProfile userData) {
        SharedPreferences.Editor editor = getPreferences(context).edit();
        editor.putString(PreferencesUtility.EMAIL, userData.getEmail());
        editor.putString(PreferencesUtility.NAME, userData.getName());
        editor.putString(PreferencesUtility.MOBILE, userData.getMobile());
        editor.putString(PreferencesUtility.PLACE, userData.getPlace());
        editor.apply();
    }

    public static void setCartData(Context context, String cartData) {
        SharedPreferences.Editor editor = getPreferences(context).edit();
        editor.putString(PreferencesUtility.CART, cartData);
        editor.apply();
    }

    public static String getCartData(Context context) {
        return getPreferences(context).getString(PreferencesUtility.CART, "");
    }

    public static void setVideosData(Context context, String videosData) {
        SharedPreferences.Editor editor = getPreferences(context).edit();
        editor.putString(PreferencesUtility.VIDEOS, videosData);
        editor.apply();
    }

    public static String getVideosData(Context context) {
        return getPreferences(context).getString(PreferencesUtility.VIDEOS, "");
    }

    public static void setEmail(Context context, String email) {
        SharedPreferences.Editor editor = getPreferences(context).edit();
        editor.putString(PreferencesUtility.EMAIL, email);
        editor.apply();
    }

    public static String getEmail(Context context) {
        return getPreferences(context).getString(PreferencesUtility.EMAIL, "");
    }

    public static void setName(Context context, String fname) {
        SharedPreferences.Editor editor = getPreferences(context).edit();
        editor.putString(PreferencesUtility.NAME, fname);
        editor.apply();
    }

    public static String getName(Context context) {
        return getPreferences(context).getString(PreferencesUtility.NAME, "");
    }

    public static void setMobile(Context context, String mobile) {
        SharedPreferences.Editor editor = getPreferences(context).edit();
        editor.putString(PreferencesUtility.MOBILE, mobile);
        editor.apply();
    }

    public static String getMobile(Context context) {
        return getPreferences(context).getString(PreferencesUtility.MOBILE, "");
    }

    public static void setPlace(Context context, String place) {
        SharedPreferences.Editor editor = getPreferences(context).edit();
        editor.putString(PreferencesUtility.PLACE, place);
        editor.apply();
    }

    public static String getPlace(Context context) {
        return getPreferences(context).getString(PreferencesUtility.PLACE, "");
    }

}
