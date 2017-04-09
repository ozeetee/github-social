package io.zeetee.githubsocial.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Date;

/**
 * By GT.
 */

public class GsonHelper {

    private static volatile Gson gson;

    public static Gson getGson(){
        if(gson == null){
            synchronized (GsonHelper.class){
                if(gson == null){
                    gson = new GsonBuilder().registerTypeAdapter(Date.class, new GsonUTCDateAdapter()).create();
                }
            }
        }
        return gson;
    }

}
