package io.zeetee.githubsocial.utils;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

import io.zeetee.githubsocial.models.GithubItem;
import io.zeetee.githubsocial.models.GithubRepo;
import io.zeetee.githubsocial.models.GithubUser;

/**
 * By GT.
 */

public class GithubItemAdapter implements JsonSerializer<GithubItem>,JsonDeserializer<GithubItem> {

    @Override
    public GithubItem deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        try {
            JsonObject jsonObj = json.getAsJsonObject();
            if(jsonObj.has("owner")){
                // This is repo
                return context.deserialize(json, GithubRepo.class);
            }else if(jsonObj.has("login")){
                // This is user Or Organization.
                return context.deserialize(json, GithubUser.class);
            }
        }catch (Exception e){
            //Do nothing.
        }
        return context.deserialize(json,typeOfT);
    }

    @Override
    public JsonElement serialize(GithubItem src, Type typeOfSrc, JsonSerializationContext context) {
        return context.serialize(src,typeOfSrc);
    }
}
