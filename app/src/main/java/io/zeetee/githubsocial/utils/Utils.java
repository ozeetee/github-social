package io.zeetee.githubsocial.utils;

import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import io.zeetee.githubsocial.GSApp;
import io.zeetee.githubsocial.R;
import io.zeetee.githubsocial.models.ErrorResponse;
import io.zeetee.githubsocial.models.GithubItem;
import io.zeetee.githubsocial.models.GithubRepo;
import io.zeetee.githubsocial.models.GithubSearchResult;
import io.zeetee.githubsocial.models.GithubUser;
import io.zeetee.githubsocial.models.GithubUserDetails;

/**
 * By GT.
 */
public class Utils {

    private static final String TAG = Utils.class.getSimpleName();

    public static List<GithubItem> constructHomePage(GithubSearchResult topAndroidRepo, GithubSearchResult mostFollowedAndroidDev, GithubUserDetails userDetails){
        final int MAX = 5;
        List<GithubItem> retList = new ArrayList<>();

        //Combine top 5 Android Repo with top most followed Android dev
        if(topAndroidRepo != null && topAndroidRepo.items != null){
            int upTo = topAndroidRepo.items.size() < MAX ? topAndroidRepo.items.size() : MAX;
            for (int i = 0 ; i < upTo ; i++){
                retList.add(topAndroidRepo.items.get(i));
            }
        }
        if(mostFollowedAndroidDev != null && mostFollowedAndroidDev.items != null){
            int upTo = mostFollowedAndroidDev.items.size() < MAX ? mostFollowedAndroidDev.items.size() : MAX;
            for (int i = 0 ; i < upTo ; i++){
                retList.add(mostFollowedAndroidDev.items.get(i));
            }
        }

        if(userDetails != null){
            retList.add(userDetails);
        }

        return retList;
    }

    public static String getBasicAuthHeader(String userName, String password){
        if(TextUtils.isEmpty(userName) || TextUtils.isEmpty(password)) return null;
        String cred = String.format("%s:%s",userName,password);
        String base64 = Utils.encodeBase64(cred);
        if(TextUtils.isEmpty(base64)) return null;
        return "Basic" + " "  + base64;
    }


    @Nullable
    public static String encodeBase64(String str){
        if(TextUtils.isEmpty(str)) return null;
        try {
            byte[] data = str.getBytes("UTF-8");
            return encodeBase64(data);
        } catch (UnsupportedEncodingException e) {
            Log.e(TAG,"Not able to encode String to base 64",e);
        }
        return null;
    }

    @Nullable
    public static String encodeBase64(byte[] data){
        try {
            return Base64.encodeToString(data, Base64.NO_WRAP);
        } catch (Exception e) {
            Log.e(TAG,"Not able to encode String to base 64",e);
        }
        return null;
    }


    public static int getHttpStatusCode(Throwable throwable){
        int code = 0 ;
        if(throwable instanceof retrofit2.adapter.rxjava2.HttpException){
            retrofit2.adapter.rxjava2.HttpException httpException = (retrofit2.adapter.rxjava2.HttpException) throwable;
            code = httpException.code();
        }

        if(throwable instanceof retrofit2.HttpException){
            retrofit2.HttpException httpException = (retrofit2.HttpException) throwable;
            code = httpException.code();
        }
        return code;
    }


    public static String getErrorTitle(Throwable throwable){

        if(throwable instanceof UnknownHostException){
            //Usually due to not able to connect to internet.
            return GSApp.getCurrentInstance().getString(R.string.unable_to_connect);
        }

        int code = Utils.getHttpStatusCode(throwable);
        return "Error" + ": " + String.valueOf(code);
    }

    public static String getErrorMessage(Throwable throwable) {
        if(throwable instanceof UnknownHostException){
            //Usually due to not able to connect to internet.
            return GSApp.getCurrentInstance().getString(R.string.check_internet_connection);
        }

        String message = throwable == null ? null : throwable.getMessage();

        if(throwable instanceof retrofit2.adapter.rxjava2.HttpException){
            retrofit2.adapter.rxjava2.HttpException httpException = (retrofit2.adapter.rxjava2.HttpException) throwable;
            message = httpException.message();

            try {
                String sJson = httpException.response().errorBody().string();
                ErrorResponse er = GsonHelper.getAppGson().fromJson(sJson, ErrorResponse.class);
                message = er.message;
            }catch (Exception e){
                //Do Nothing
            }

        }
        return message;
    }

    public static boolean isUser(GithubUser githubUser) {
        return githubUser != null && GSConstants.UserType.USER.equalsIgnoreCase(githubUser.type);
    }

    public static void fillInStarState(List<? extends GithubItem> githubItems) {
        if(githubItems == null) return;
        for (GithubItem item : githubItems){
            if(item == null) continue;
            item.starState = new StarState();
            if(item instanceof GithubUser){
                GithubUser githubUser = (GithubUser) item;
                if(UserProfileManager.getSharedInstance().isFollowing(githubUser.login)){
                    githubUser.starState.currentState = true;
                    githubUser.starState.originalState = true;
                }
            }

            if(item instanceof GithubRepo){
                GithubRepo githubRepo = (GithubRepo) item;
                if(UserProfileManager.getSharedInstance().isStarred(githubRepo.full_name)){
                    githubRepo.starState.currentState = true;
                    githubRepo.starState.originalState = true;
                }

            }

        }
    }
}
