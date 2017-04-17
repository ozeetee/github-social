package io.zeetee.githubsocial.network;

import java.util.List;

import io.reactivex.Observable;
import io.zeetee.githubsocial.dtos.AuthDto;
import io.zeetee.githubsocial.models.AuthResponse;
import io.zeetee.githubsocial.models.GithubRepo;
import io.zeetee.githubsocial.models.GithubRepoDetails;
import io.zeetee.githubsocial.models.GithubRepoReadme;
import io.zeetee.githubsocial.models.GithubSearchResult;
import io.zeetee.githubsocial.models.GithubUser;
import io.zeetee.githubsocial.models.GithubUserDetails;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Url;

/**
 * By GT.
 */

public interface GithubApi {

    @POST("/authorizations")
    Observable<AuthResponse> authorizations(@Header("Authorization") String basicAuth, @Body AuthDto authDto);

    //For Home Screen
    @GET("/search/repositories?order=desc&q=Android+language:Java&sort=stars")
    Observable<GithubSearchResult> fetchTopAndroidRepo(@Header("Authorization") String token);

    @GET("/search/users?order=desc&q=Android+language:Java&sort=followers")
    Observable<GithubSearchResult> fetchMostFollowedAndroidDevs(@Header("Authorization") String token);

    @GET("/users/ozeetee")
    Observable<GithubUserDetails> fetchHomeProfile(@Header("Authorization") String token);

    @GET("/user")
    Observable<GithubUserDetails> meProflie(@Header("Authorization") String token);

    @GET("/user/starred")
    Observable<List<GithubRepo>> meStarredRepos(@Header("Authorization") String token, @Query("page") int page, @Query("per_page") int perPage);

    @GET("/user/repos")
    Observable<List<GithubRepo>> meRepos(@Header("Authorization") String token, @Query("page") int page, @Query("per_page") int perPage);

    @GET("/user/following")
    Observable<List<GithubUser>> meFollowing(@Header("Authorization") String token, @Query("page") int page, @Query("per_page") int perPage);

    @GET("/user/followers")
    Observable<List<GithubUser>> meFollowers(@Header("Authorization") String token, @Query("page") int page, @Query("per_page") int perPage);


    @GET("/users/{username}/following")
    Observable<List<GithubUser>> following(@Header("Authorization") String token, @Path("username") String username, @Query("page") int page, @Query("per_page") int perPage);

    @GET("/users/{username}/followers")
    Observable<List<GithubUser>> followers(@Header("Authorization") String token, @Path("username") String username, @Query("page") int page, @Query("per_page") int perPage);

    @GET("/users/{username}/repos")
    Observable<List<GithubRepo>> repos(@Header("Authorization") String token, @Path("username") String username, @Query("page") int page, @Query("per_page") int perPage);

    @GET("/users/{username}")
    Observable<GithubUserDetails> user(@Header("Authorization") String token, @Path("username") String username);

    @GET("/repos/{owner}/{repo}")
    Observable<GithubRepoDetails> repoDetails(@Header("Authorization") String token, @Path("owner") String owner, @Path("repo") String repo);

    @GET("/repos/{owner}/{repo}/readme")
    Observable<GithubRepoReadme> repoReadme(@Header("Authorization") String token, @Path("owner") String owner, @Path("repo") String repo);

    @GET("/repos/{owner}/{repo}/watchers")
    Observable<List<GithubUser>> repoWatchers(@Header("Authorization") String token, @Path("repo") String repo, @Path("owner") String owner, @Query("page") int page, @Query("per_page") int perPage);

    @GET("/repos/{owner}/{repo}/stargazers")
    Observable<List<GithubUser>> repoStarGazer(@Header("Authorization") String token, @Path("repo") String repo, @Path("owner") String owner, @Query("page") int page, @Query("per_page") int perPage);

    @GET("/orgs/{org}/members")
    Observable<List<GithubUser>> orgMembers(@Header("Authorization") String token, @Path("org") String org, @Query("page") int page, @Query("per_page") int perPage);


    //Actions
    @PUT("/user/following/{username}")
    Observable<Response<Void>> followUser(@Header("Authorization") String token, @Path("username") String userName);

    @DELETE("/user/following/{username}")
    Observable<Response<Void>> unFollowUser(@Header("Authorization") String token, @Path("username") String userName);

    @PUT("/user/starred/{owner}/{repo}")
    Observable<Response<Void>> starRepo(@Header("Authorization") String token, @Path("owner") String owner, @Path("repo") String repo);

    @DELETE("/user/starred/{owner}/{repo}")
    Observable<Response<Void>> unStarRepo(@Header("Authorization") String token,@Path("owner") String owner, @Path("repo") String repo);


    // Following generic method won't work
    //https://github.com/square/retrofit/issues/2012
//    @GET
//    <T> Observable<T> executeUrl(@Url String url);

}