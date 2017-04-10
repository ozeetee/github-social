package io.zeetee.githubsocial.utils;

import java.util.ArrayList;
import java.util.List;

import io.zeetee.githubsocial.models.GithubItem;
import io.zeetee.githubsocial.models.GithubSearchResult;

/**
 * By GT.
 */
public class Utils {

    public static List<GithubItem> constructHomePage(GithubSearchResult topAndroidRepo, GithubSearchResult mostFollowedAndroidDev){
        final int MAX = 5;
        List<GithubItem> retList = new ArrayList<>();

        //Combine top 5 Android Repo with top most followed Android dev
        if(topAndroidRepo != null && topAndroidRepo.items != null){
            int upTo = topAndroidRepo.items.size() < MAX ? topAndroidRepo.items.size() : MAX;
            for (int i = 0 ; i < MAX ; i++){
                retList.add(topAndroidRepo.items.get(i));
            }
        }
        if(mostFollowedAndroidDev != null && mostFollowedAndroidDev.items != null){
            int upTo = mostFollowedAndroidDev.items.size() < MAX ? mostFollowedAndroidDev.items.size() : MAX;
            for (int i = 0 ; i < MAX ; i++){
                retList.add(mostFollowedAndroidDev.items.get(i));
            }
        }
        return retList;
    }

}
