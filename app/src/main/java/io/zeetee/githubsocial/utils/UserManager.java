package io.zeetee.githubsocial.utils;

import io.zeetee.githubsocial.models.User;

/**
 * By GT.
 */

public class UserManager {

    private static final String TAG = UserManager.class.getSimpleName();
    private static final String PREFS_NAME = "io.zeetee.githubsocial.sharedpreferences";

    private static volatile UserManager sharedInstance;
    private volatile User user;

    // Private constructor
    private UserManager(){
//        this.user = getUserFromSharedPreference();
    }

    public static UserManager getSharedInstance(){
        if(sharedInstance == null){
            synchronized (UserManager.class){
                if(sharedInstance == null){
                    sharedInstance = new UserManager();
                }
            }
        }
        return sharedInstance;
    }

    public void initialize() {

    }
//
//    public synchronized User getUserFromSharedPreference(){
//        SharedPreferences sp = GSApp.getCurrentInstance().getSharedPreferences(PREFS_NAME,0);
//        String userId = sp.getString(USERID, null);
//        if(StringUtils.isBlank(userId)) return null;
//        user = new User();
//        user.UserID = userId;
//        return user;
//    }
//
//
//    public synchronized void saveUser(User u){
//        if(u == null || StringUtils.isBlank(u.UserID)) return;
//        this.user = u;
//        SharedPreferences sp = WarehouseApplication.getCurrentInstance().getSharedPreferences(PREFS_NAME,0);
//        SharedPreferences.Editor editor = sp.edit();
//        editor.putString(USERID, u.UserID);
//        editor.apply();
//    }

}
