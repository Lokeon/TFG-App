package es.tfg.listener;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class SavedSharedPreferences {

    public static final String LOGGED_IN_PREF = "logged_in_status";
    public static String TOKEN = "user_token";
    public static String ID = "user_id";


    static SharedPreferences getPreferences(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static void setLoggedIn(Context context, boolean loggedIn) {
        SharedPreferences.Editor editor = getPreferences(context).edit();
        editor.putBoolean(LOGGED_IN_PREF, loggedIn);
        editor.apply();
    }

    public static void setTokenId(Context context, String token, String id) {
        SharedPreferences.Editor editor = getPreferences(context).edit();
        editor.putString(TOKEN, token);
        editor.putString(ID, id);
        editor.apply();
    }

    public static boolean getLoggedStatus(Context context) {
        return getPreferences(context).getBoolean(LOGGED_IN_PREF, false);
    }

    public static String getToken(Context context) {
        return getPreferences(context).getString(TOKEN, "error");
    }

    public static String getID(Context context) {
        return getPreferences(context).getString(ID, "error");
    }
}