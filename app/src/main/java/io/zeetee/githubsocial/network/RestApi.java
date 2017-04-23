package io.zeetee.githubsocial.network;


import android.text.TextUtils;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.zeetee.githubsocial.GSApp;
import io.zeetee.githubsocial.dtos.AuthDto;
import io.zeetee.githubsocial.models.AuthResponse;
import io.zeetee.githubsocial.models.GithubRepo;
import io.zeetee.githubsocial.models.GithubRepoDetails;
import io.zeetee.githubsocial.models.GithubRepoReadme;
import io.zeetee.githubsocial.models.GithubSearchResult;
import io.zeetee.githubsocial.models.GithubUser;
import io.zeetee.githubsocial.models.GithubUserDetails;
import io.zeetee.githubsocial.utils.GSConstants;
import io.zeetee.githubsocial.utils.GsonHelper;
import io.zeetee.githubsocial.utils.UserManager;
import io.zeetee.githubsocial.utils.UserProfileManager;
import io.zeetee.githubsocial.utils.Utils;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * By GT.
 */
public class RestApi {

    private static final String TAG = RestApi.class.getSimpleName();
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
                .addConverterFactory(GsonConverterFactory.create(GsonHelper.getAppGson()))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(GSApp.getCurrentInstance().getCustomOKClient())
                .build();
        return retrofit.create(GithubApi.class);
    }


    //Api

    public static Observable<String> auth(final String userName, final String password){

        final String authHeader = Utils.getBasicAuthHeader(userName, password);

        return getClient()
                .authorizations(authHeader, new AuthDto())
                .map(extractToken);
    }

    private static Function<AuthResponse, String> extractToken = new Function<AuthResponse, String>() {
        @Override
        public String apply(AuthResponse authResponse) throws Exception {
            return authResponse == null ? null : authResponse.token;
        }
    };

    //Logged in User
    public static Observable<GithubUserDetails> meProfile(){
        return UserManager
                .getSharedInstance()
                .getTokenForRestCall()
                .flatMap(new Function<String, ObservableSource<GithubUserDetails>>() {
                            @Override
                            public ObservableSource<GithubUserDetails> apply(String token) throws Exception {
                                token = normalizeToken(token);
                                return getClient().meProflie(token);
                            }
                }).doOnError(on401Error);
    }

    public static Observable<List<GithubUser>> meFollowing(final int page, final int perPage){
        return UserManager
                .getSharedInstance()
                .getTokenForRestCall()
                .flatMap(new Function<String, ObservableSource<List<GithubUser>>>() {
                    @Override
                    public ObservableSource<List<GithubUser>> apply(String token) throws Exception {
                        token = normalizeToken(token);
                        return getClient().meFollowing(token,page, perPage);
                    }
                }).doOnError(on401Error);
    }

    public static Observable<List<GithubUser>> meFollowers(final int page, final int perPage){
        return UserManager
                .getSharedInstance()
                .getTokenForRestCall()
                .flatMap(new Function<String, ObservableSource<List<GithubUser>>>() {
                    @Override
                    public ObservableSource<List<GithubUser>> apply(String token) throws Exception {
                        token = normalizeToken(token);
                        return getClient().meFollowers(token,page,perPage);
                    }
                }).doOnError(on401Error);
    }

    public static Observable<List<GithubRepo>> meStarred(final int page, final int perPage){
        return UserManager
                .getSharedInstance()
                .getTokenForRestCall()
                .flatMap(new Function<String, ObservableSource<List<GithubRepo>>>() {
                    @Override
                    public ObservableSource<List<GithubRepo>> apply(String token) throws Exception {
                        token = normalizeToken(token);
                        return getClient().meStarredRepos(token,page,perPage);
                    }
                }).doOnError(on401Error);
    }

    public static Observable<List<GithubRepo>> meRepos(final int page, final int perPage) {
        return UserManager
                .getSharedInstance()
                .getTokenForRestCall()
                .flatMap(new Function<String, ObservableSource<List<GithubRepo>>>() {
                    @Override
                    public ObservableSource<List<GithubRepo>> apply(String token) throws Exception {
                        token = normalizeToken(token);
                        return getClient().meRepos(token,page,perPage);
                    }
                }).doOnError(on401Error);
    }

    public static Observable<Response<Void>> followUser(final GithubUser githubUser){
        UserProfileManager.getSharedInstance().userFollowed(githubUser);
        final String userName = githubUser.login;
        return UserManager
                .getSharedInstance()
                .getTokenForRestCall()
                .flatMap(new Function<String, ObservableSource<Response<Void>>>() {
                    @Override
                    public ObservableSource<Response<Void>> apply(String token) throws Exception {
                        token = normalizeToken(token);
                        return getClient().followUser(token,userName);
                    }
                }).doOnError(on401Error).doOnError(new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        UserProfileManager.getSharedInstance().userUnFollowed(githubUser);
                    }
                });
    }

    public static Observable<Response<Void>> unFollowUser(final GithubUser githubUser){
        UserProfileManager.getSharedInstance().userUnFollowed(githubUser);
        final String userName = githubUser.login;
        return UserManager
                .getSharedInstance()
                .getTokenForRestCall()
                .flatMap(new Function<String, ObservableSource<Response<Void>>>() {
                    @Override
                    public ObservableSource<Response<Void>> apply(String token) throws Exception {
                        token = normalizeToken(token);
                        return getClient().unFollowUser(token,userName);
                    }
                }).doOnError(on401Error).doOnError(new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        UserProfileManager.getSharedInstance().userFollowed(githubUser);
                    }
                });
    }


    public static Observable<Response<Void>> starRepo(final GithubRepo githubRepo){
        UserProfileManager.getSharedInstance().repoStarred(githubRepo);
        final String owner = githubRepo.owner.login;
        final String repo = githubRepo.name;
        return UserManager
                .getSharedInstance()
                .getTokenForRestCall()
                .flatMap(new Function<String, ObservableSource<Response<Void>>>() {
                    @Override
                    public ObservableSource<Response<Void>> apply(String token) throws Exception {
                        token = normalizeToken(token);
                        return getClient().starRepo(token,owner,repo);
                    }
                })
                .doOnError(on401Error)
                .doOnError(new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        UserProfileManager.getSharedInstance().repoUnStarred(githubRepo);
                    }
                });
    }

    public static Observable<Response<Void>> unStarRepo(GithubRepo githubRepo){
        UserProfileManager.getSharedInstance().repoUnStarred(githubRepo);
        final String owner = githubRepo.owner.login;
        final String repo = githubRepo.name;
        return UserManager
                .getSharedInstance()
                .getTokenForRestCall()
                .flatMap(new Function<String, ObservableSource<Response<Void>>>() {
                    @Override
                    public ObservableSource<Response<Void>> apply(String token) throws Exception {
                        token = normalizeToken(token);
                        return getClient().unStarRepo(token,owner,repo);
                    }
                })
                .doOnError(on401Error)
                .doOnError(new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                    }
                });
    }

    private static Consumer<Throwable> on401Error = new Consumer<Throwable>() {
        @Override
        public void accept(Throwable throwable) throws Exception {
            int code = Utils.getHttpStatusCode(throwable);
            if(code == 401){
                UserManager.getSharedInstance().removeUserToken();
            }
        }
    };


    public static Observable<GithubSearchResult> searchRepos(final String query, final int page, final int perPage){
        return UserManager
                .getSharedInstance()
                .getTokenForRestCall()
                .flatMap(new Function<String, ObservableSource<GithubSearchResult>>() {
                    @Override
                    public ObservableSource<GithubSearchResult> apply(String token) throws Exception {
                        token = normalizeToken(token);
                        return getClient().searchRepos(token, query, page,perPage);
                    }
                });
    }

    public static Observable<GithubSearchResult> fetchTopAndroidRepo(){
        return UserManager
                .getSharedInstance()
                .getTokenForRestCall()
                .flatMap(new Function<String, ObservableSource<GithubSearchResult>>() {
                    @Override
                    public ObservableSource<GithubSearchResult> apply(String token) throws Exception {
                        token = normalizeToken(token);
                        return getClient().fetchTopAndroidRepo(token);
                    }
                });
    }

    public static Observable<GithubSearchResult> fetchMostFollowedAndroidDevs(){
        return UserManager
                .getSharedInstance()
                .getTokenForRestCall()
                .flatMap(new Function<String, ObservableSource<GithubSearchResult>>() {
                    @Override
                    public ObservableSource<GithubSearchResult> apply(String token) throws Exception {
                        token = normalizeToken(token);
                        return getClient().fetchMostFollowedAndroidDevs(token);
                    }
                });
    }

    public static Observable<GithubUserDetails> fetchHomeProfile(){
        return UserManager
                .getSharedInstance()
                .getTokenForRestCall()
                .flatMap(new Function<String, ObservableSource<GithubUserDetails>>() {
                    @Override
                    public ObservableSource<GithubUserDetails> apply(String token) throws Exception {
                        token = normalizeToken(token);
                        return getClient().fetchHomeProfile(token);
                    }
                });
    }


    public static Observable<GithubUserDetails> user(final String userName){
        return UserManager
                .getSharedInstance()
                .getTokenForRestCall()
                .flatMap(new Function<String, ObservableSource<GithubUserDetails>>() {
                    @Override
                    public ObservableSource<GithubUserDetails> apply(String token) throws Exception {
                        token = normalizeToken(token);
                        return getClient().user(token, userName);
                    }
                });
    }

    public static Observable<List<GithubUser>> following(final String userName, final int page, final int perPage){
        return UserManager
                .getSharedInstance()
                .getTokenForRestCall()
                .flatMap(new Function<String, ObservableSource<List<GithubUser>>>() {
                    @Override
                    public ObservableSource<List<GithubUser>> apply(String token) throws Exception {
                        token = normalizeToken(token);
                        return getClient().following(token,userName,page,perPage);
                    }
                });
    }

    public static Observable<List<GithubUser>> followers(final String userName, final int page, final int perPage){
        return UserManager
                .getSharedInstance()
                .getTokenForRestCall()
                .flatMap(new Function<String, ObservableSource<List<GithubUser>>>() {
                    @Override
                    public ObservableSource<List<GithubUser>> apply(String token) throws Exception {
                        token = normalizeToken(token);
                        return getClient().followers(token,userName,page,perPage);
                    }
                });
    }



    public static Observable<List<GithubRepo>> repos(final String userName, final int page, final int perPage){
        return UserManager
                .getSharedInstance()
                .getTokenForRestCall()
                .flatMap(new Function<String, ObservableSource<List<GithubRepo>>>() {
                    @Override
                    public ObservableSource<List<GithubRepo>> apply(String token) throws Exception {
                        token = normalizeToken(token);
                        return getClient().repos(token, userName,page,perPage);
                    }
                });
    }

    public static Observable<GithubRepoDetails> repoDetails(final String owner, final String repo){
        return UserManager
                .getSharedInstance()
                .getTokenForRestCall()
                .flatMap(new Function<String, ObservableSource<GithubRepoDetails>>() {
                    @Override
                    public ObservableSource<GithubRepoDetails> apply(String token) throws Exception {
                        token = normalizeToken(token);
                        return getClient().repoDetails(token, owner, repo);
                    }
                });
    }

    public static Observable<GithubRepoReadme> repoReadme(final String owner, final String repo){
        return UserManager
                .getSharedInstance()
                .getTokenForRestCall()
                .flatMap(new Function<String, ObservableSource<GithubRepoReadme>>() {
                    @Override
                    public ObservableSource<GithubRepoReadme> apply(String token) throws Exception {
                        token = normalizeToken(token);
                        return getClient().repoReadme(token, owner, repo);
                    }
                });

    }

    public static Observable<List<GithubUser>> stargazer(final String repoName, final String userName, final int page, final int perPage) {
        return UserManager
                .getSharedInstance()
                .getTokenForRestCall()
                .flatMap(new Function<String, ObservableSource<List<GithubUser>>>() {
                    @Override
                    public ObservableSource<List<GithubUser>> apply(String token) throws Exception {
                        token = normalizeToken(token);
                        return getClient().repoStarGazer(token, repoName,userName,page,perPage);
                    }
                });
    }

    public static Observable<List<GithubUser>> watchers(final String repoName, final String userName, final int page, final int perPage) {
        return UserManager
                .getSharedInstance()
                .getTokenForRestCall()
                .flatMap(new Function<String, ObservableSource<List<GithubUser>>>() {
                    @Override
                    public ObservableSource<List<GithubUser>> apply(String token) throws Exception {
                        token = normalizeToken(token);
                        return getClient().repoWatchers(token, repoName,userName,page,perPage);
                    }
                });
    }

    public static Observable<List<GithubUser>> orgMembers(final String org, final int page, final int perPage){
        return UserManager
                .getSharedInstance()
                .getTokenForRestCall()
                .flatMap(new Function<String, ObservableSource<List<GithubUser>>>() {
                    @Override
                    public ObservableSource<List<GithubUser>> apply(String token) throws Exception {
                        token = normalizeToken(token);
                        return getClient().orgMembers(token,org, page, perPage);
                    }
                });
    }

    private static String normalizeToken(String token){
        return TextUtils.isEmpty(token) ? null : "token " + token;
    }


}
