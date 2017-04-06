package io.zeetee.githubsocial.network;


import java.util.List;

import io.reactivex.Observable;
import io.zeetee.githubsocial.GSApp;
import io.zeetee.githubsocial.models.GithubUser;
import io.zeetee.githubsocial.models.GithubUserDetails;
import io.zeetee.githubsocial.utils.GSConstants;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

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
                .addConverterFactory(GsonConverterFactory.create())
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


}
