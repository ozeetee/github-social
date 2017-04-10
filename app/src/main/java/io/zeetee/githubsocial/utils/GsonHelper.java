package io.zeetee.githubsocial.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Date;

import io.zeetee.githubsocial.models.GithubItem;

/**
 * By GT.
 */

public class GsonHelper {

    private static volatile Gson appGson;

    public static Gson getAppGson(){
        if(appGson == null){
            synchronized (GsonHelper.class){
                if(appGson == null){
                    appGson = new GsonBuilder()
                            .registerTypeAdapter(Date.class, new GsonUTCDateAdapter())
                            .registerTypeAdapter(GithubItem.class, new GithubItemAdapter())
                            .create();
                }
            }
        }
        return appGson;
    }

}
