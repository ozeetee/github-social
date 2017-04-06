package io.zeetee.githubsocial.network;

import java.util.List;

import io.reactivex.Observable;
import io.zeetee.githubsocial.dtos.AuthDto;
import io.zeetee.githubsocial.models.AuthResponse;
import io.zeetee.githubsocial.models.GithubUser;
import io.zeetee.githubsocial.models.GithubUserDetails;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * By GT.
 */

public interface GithubApi {

    @POST("authorizations")
    Observable<AuthResponse> authorizations(@Body AuthDto authDto);


    @GET("/users/following")
    Observable<List<GithubUser>> meFollowing(@Header("Authorization") String token);


    @GET("/users/{username}/following")
    Observable<List<GithubUser>> following(@Path("username") String username);


    @GET("/users/{username}")
    Observable<GithubUserDetails> user(@Path("username") String username);
}