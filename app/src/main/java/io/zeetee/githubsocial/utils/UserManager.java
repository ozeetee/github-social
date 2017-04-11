package io.zeetee.githubsocial.utils;

import android.content.SharedPreferences;
import android.text.TextUtils;

import io.reactivex.Observable;
import io.reactivex.functions.Consumer;
import io.zeetee.githubsocial.GSApp;
import io.zeetee.githubsocial.bus.RxEventBus;
import io.zeetee.githubsocial.bus.RxEvents;
import io.zeetee.githubsocial.models.AppUser;
import io.zeetee.githubsocial.network.RestApi;

/**
 * By GT.
 *
 */
public class UserManager {

    private static final String TAG = UserManager.class.getSimpleName();
    private static final String PREFS_NAME = "io.zeetee.githubsocial.sharedpreferences";

    private static volatile UserManager sharedInstance;
    private volatile AppUser appUser;

    // Private constructor
    private UserManager(){
        this.appUser = getAppUserFromSharedPreference();
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

    public boolean isLoggedIn(){
        return appUser != null && !TextUtils.isEmpty(appUser.userName) && !TextUtils.isEmpty(appUser.password);
    }

    private AppUser getAppUserFromSharedPreference(){
        SharedPreferences sp = GSApp.getCurrentInstance().getSharedPreferences(PREFS_NAME,0);
        AppUser appUser = new AppUser();
        appUser.userName = sp.getString(GSConstants.USER_NAME, null);
        appUser.password = sp.getString(GSConstants.PASSWORD, null);
        appUser.token = sp.getString(GSConstants.TOKEN, null);
        return appUser;
    }

    public synchronized void saveUser(AppUser u){
        this.appUser = u;
        if(u == null || TextUtils.isEmpty(u.userName)){
            removeUserFromPref();
            return;
        }
        SharedPreferences sp = GSApp.getCurrentInstance().getSharedPreferences(PREFS_NAME,0);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(GSConstants.USER_NAME, u.userName);
        editor.putString(GSConstants.PASSWORD, u.password);
        editor.putString(GSConstants.TOKEN, u.token);
        editor.apply();
    }

    private void removeUserFromPref(){
        SharedPreferences sp = GSApp.getCurrentInstance().getSharedPreferences(PREFS_NAME,0);
        SharedPreferences.Editor editor = sp.edit();
        editor.remove(GSConstants.USER_NAME);
        editor.remove(GSConstants.PASSWORD);
        editor.remove(GSConstants.TOKEN);
        editor.apply();
    }

    public void removeUserToken(){
        if(this.appUser != null) this.appUser.token = null;
        SharedPreferences sp = GSApp.getCurrentInstance().getSharedPreferences(PREFS_NAME,0);
        SharedPreferences.Editor editor = sp.edit();
        editor.remove(GSConstants.TOKEN);
        editor.apply();
    }


    public void logOfUser(){
        saveUser(null);
        UserProfileManager.getSharedInstance().logOffUser();
    }


    private void saveUserInfo(String userName, String password, String token) {
        if(this.appUser == null) appUser = new AppUser();
        appUser.userName = userName;
        appUser.password = password;
        appUser.token = token;
        saveUser(appUser);
    }

    public synchronized void userLoggedIn(String userName, String password, String token) {
        saveUserInfo(userName,password,token);
        RxEventBus.getInstance().post(new RxEvents.UserLoggedInEvent(userName,password,token));
    }

    public Observable<String> getTokenForRestCall(){

        //We have a token return it for rest call ... Sweet
        if(appUser != null && !TextUtils.isEmpty(appUser.token)){
            return Observable.just(appUser.token);
        }

        if(appUser == null || TextUtils.isEmpty(appUser.userName) || TextUtils.isEmpty(appUser.password)){
            return Observable.just(""); //Can't pass null
        }

        return RestApi.auth(appUser.userName, appUser.password).doOnError(on401Error);
    }

    private Consumer<Throwable> on401Error = new Consumer<Throwable>() {
        @Override
        public void accept(Throwable throwable) throws Exception {
            //Special handling when auth call failed with username and password
            int code = Utils.getHttpStatusCode(throwable);
            if(code == 401){
                logOfUser();
            }
        }
    };


    public String getUserName(){
        return (appUser == null || appUser.userName == null) ? null : appUser.userName;
    }

    public boolean isMe(String userName){
        return appUser != null && appUser.userName != null && appUser.userName.equalsIgnoreCase(userName);
    }

}
