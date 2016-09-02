package kr.edcan.acar;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.util.Pair;
import android.util.Log;

import kr.edcan.acar.models.User;

/**
 * Created by KOHA on 7/9/16.
 */

public class DataManager {
    /* Data Keys */
    public static final String USER_PROFILE_URL = "user_profile_url";
    public static final String IS_SILHOUETTE = "is_silhouette";
    public static final String HAS_ACTIVE_USER = "has_active_user";
    public static final String USER_TOKEN = "user_token";
    public static final String USER_TOKEN_SECRET = "user_token_secret";
    public static final String USER_NAME = "user_name";
    public static final String USER_ID = "user_id";
    public static final String LOGIN_TYPE = "login_type";
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private Context context;
    public DataManager instance;

    public DataManager() {
    }

    public void initializeManager(Context c) {
        this.context = c;
        preferences = context.getSharedPreferences("ACAR", Context.MODE_PRIVATE);
        editor = preferences.edit();
    }

    public void save(String key, String data) {
        editor.putString(key, data);
        editor.apply();
    }

    public void saveUserCredential(String facebookToken) {
        editor.putString(USER_TOKEN, facebookToken);
        editor.apply();
    }

    public void saveUserCredential(String[] twitterToken) {
        editor.putString(USER_TOKEN, twitterToken[0]);
        editor.putString(USER_TOKEN_SECRET, twitterToken[1]);
        editor.apply();
    }

    public void saveFacebookUserInfo(FacebookUser user) {
        editor.putInt(LOGIN_TYPE, 0);
        editor.putBoolean(HAS_ACTIVE_USER, true);
        editor.putString(USER_ID, user.content.id);
        editor.putString(USER_NAME, user.content.name);
        editor.apply();
    }
    public Pair<Boolean, User> getActiveUser() {
        if (preferences.getBoolean(HAS_ACTIVE_USER, false)) {
            String id = preferences.getString(USER_ID, "");
            String name = preferences.getString(USER_NAME, "");
            User user = new User(id, name);
            return Pair.create(true, user);
        } else return Pair.create(false, null);
    }

    public String getFacebookUserCredential() {
        if (preferences.getBoolean(HAS_ACTIVE_USER, false) && preferences.getInt(LOGIN_TYPE, -1) == 0) {
            return preferences.getString(USER_TOKEN, "");
        } else return "";
    }

    public String[] getTwitterUserCredentials() {
        if (preferences.getBoolean(HAS_ACTIVE_USER, false) && preferences.getInt(LOGIN_TYPE, -1) == 1)
            return new String[]{preferences.getString(USER_TOKEN, ""),
                    preferences.getString(USER_TOKEN_SECRET, ""),
                    preferences.getString(USER_ID, "")
            };
        else return new String[]{""};
    }

    public void removeAllData() {
        editor.clear();
        editor.apply();
    }

    public String getString(String key) {
        return preferences.getString(key, "");
    }

    public int getInt(String key) {
        return preferences.getInt(key, 0);
    }

    public boolean getBoolean(String key) {
        return preferences.getBoolean(key, false);
    }

    public boolean isFirst() {
        return preferences.getBoolean("IS_FIRST", true);
    }

    public void notFirst() {
        editor.putBoolean("IS_FIRST", false);
        editor.apply();
    }

    public long getLong(String key) {
        return preferences.getLong(key, 0);
    }

}