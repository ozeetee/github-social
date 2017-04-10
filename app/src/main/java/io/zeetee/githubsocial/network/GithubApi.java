package io.zeetee.githubsocial.network;

import java.util.List;

import io.reactivex.Observable;
import io.zeetee.githubsocial.dtos.AuthDto;
import io.zeetee.githubsocial.models.AuthResponse;
import io.zeetee.githubsocial.models.GithubRepo;
import io.zeetee.githubsocial.models.GithubRepoDetails;
import io.zeetee.githubsocial.models.GithubRepoReadme;
import io.zeetee.githubsocial.models.GithubUser;
import io.zeetee.githubsocial.models.GithubUserDetails;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Url;

/**
 * By GT.
 */

public interface GithubApi {

    @POST("authorizations")
    Observable<AuthResponse> authorizations(@Body AuthDto authDto);

    @GET("/user/following")
    Observable<List<GithubUser>> meFollowing(@Header("Authorization") String token);

    @GET("/user/followers")
    Observable<List<GithubUser>> meFollowers(@Header("Authorization") String token);

    @GET("/users/{username}/following")
    Observable<List<GithubUser>> following(@Path("username") String username);

    @GET("/users/{username}/followers")
    Observable<List<GithubUser>> followers(@Path("username") String username);

    @GET("/users/{username}/repos")
    Observable<List<GithubRepo>> repos(@Path("username") String username);

    @GET("/users/{username}")
    Observable<GithubUserDetails> user(@Path("username") String username);

    @GET("/repos/{owner}/{repo}")
    Observable<GithubRepoDetails> repoDetails(@Path("owner") String owner, @Path("repo") String repo);

    @GET("/repos/{owner}/{repo}/readme")
    Observable<GithubRepoReadme> repoReadme(@Path("owner") String owner, @Path("repo") String repo);

    @GET("/repos/{owner}/{repo}/watchers")
    Observable<List<GithubUser>> repoWatchers(@Path("repo") String repo, @Path("owner") String owner);

    @GET("/repos/{owner}/{repo}/stargazers")
    Observable<List<GithubUser>> repoStarGazer(@Path("repo") String repo, @Path("owner") String owner);




    // Following generic method won't work
    //https://github.com/square/retrofit/issues/2012
//    @GET
//    <T> Observable<T> executeUrl(@Url String url);

}