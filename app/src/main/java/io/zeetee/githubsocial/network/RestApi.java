package io.zeetee.githubsocial.network;


import java.util.List;

import io.reactivex.Observable;
import io.zeetee.githubsocial.GSApp;
import io.zeetee.githubsocial.models.GithubRepo;
import io.zeetee.githubsocial.models.GithubRepoDetails;
import io.zeetee.githubsocial.models.GithubUser;
import io.zeetee.githubsocial.models.GithubUserDetails;
import io.zeetee.githubsocial.utils.GSConstants;
import io.zeetee.githubsocial.utils.GsonHelper;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Url;

/**
 * By GT.
 */

public class RestApi {

    private static GithubApi client;

    private static GithubApi getClient(){
        if(client == null){
            client = createClient();
        }
        return client;
    }

    private static GithubApi createClient(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(GSConstants.Github.API_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(GsonHelper.getGson()))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(GSApp.getCurrentInstance().getCustomOKClient())
                .build();
        return retrofit.create(GithubApi.class);
    }


    public static Observable<List<GithubUser>> fetchHome(){
        return getClient().following("ozeetee");
    }

    public static Observable<GithubUserDetails> user(String userName){
        return getClient().user(userName);
    }


    public static Observable<List<GithubUser>> following(String userName){
        return getClient().following(userName);
    }

    public static Observable<List<GithubUser>> followers(String userName){
        return getClient().followers(userName);
    }

    public static Observable<List<GithubRepo>> repos(String userName){
        return getClient().repos(userName);
    }

    public static Observable<GithubRepoDetails> getRepoDetails(String owner, String repo){
        return getClient().getRepoDetails(owner, repo);
    }

    public static Observable<List<GithubUser>> stargazer(String repoName, String userName) {
        return getClient().repoStarGazer(repoName,userName);
    }

    public static Observable<List<GithubUser>> watchers(String repoName, String userName) {
        return getClient().repoWatchers(repoName,userName);
    }
}
