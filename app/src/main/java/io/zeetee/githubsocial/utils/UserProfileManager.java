package io.zeetee.githubsocial.utils;

import android.util.Log;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import io.zeetee.githubsocial.bus.RxEventBus;
import io.zeetee.githubsocial.bus.RxEvents;
import io.zeetee.githubsocial.models.GithubRepo;
import io.zeetee.githubsocial.models.GithubUser;
import io.zeetee.githubsocial.models.GithubUserDetails;
import io.zeetee.githubsocial.network.RestApi;

/**
 * By GT.
 */

public class UserProfileManager {

    private static volatile UserProfileManager sharedInstance;
    private GithubUserDetails userDetails;
    private List<GithubUser> following;
    private List<GithubRepo> starredRepo;

    public static UserProfileManager getSharedInstance() {
        if(sharedInstance == null){
            synchronized (UserManager.class){
                if(sharedInstance == null){
                    sharedInstance = new UserProfileManager();
                }
            }
        }
        return sharedInstance;
    }

    public void fetchUserInformationSilently(){
        if(!UserManager.getSharedInstance().isLoggedIn()) return;
        //Fetch Profile
        fetchProfileSilently();

        //Fetch Following
        fetchFollowingSilently();

        //Fetch Starred Repo
        fetchStarredRepoSilently();
    }

    private void fetchProfileSilently(){
        RestApi
                .meProfile()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<GithubUserDetails>() {
                    @Override
                    public void accept(GithubUserDetails userDetails) throws Exception {
                        setUserDetails(userDetails);
                    }
                },throwableConsumer);

    }

    private void fetchFollowingSilently(){
        RestApi
                .meFollowing()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<GithubUser>>() {
                    @Override
                    public void accept(List<GithubUser> githubUsers) throws Exception {
                        setFollowing(githubUsers);
                    }
                },throwableConsumer);
    }


    private void fetchStarredRepoSilently(){
        RestApi
                .meStarred()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<GithubRepo>>() {
                    @Override
                    public void accept(List<GithubRepo> githubRepos) throws Exception {
                        setStarredRepo(githubRepos);
                    }
                },throwableConsumer);
    }


    private Consumer<Throwable> throwableConsumer = new Consumer<Throwable>() {
        @Override
        public void accept(Throwable throwable) throws Exception {
            Log.e("GTGT","Error occured", throwable);
        }
    };


    public synchronized void setUserDetails(GithubUserDetails userDetails) {
        this.userDetails = userDetails;
        RxEventBus.getInstance().post(new RxEvents.UserInfoLoadedEvent(userDetails));
    }

    public synchronized void setFollowing(List<GithubUser> following) {
        this.following = following;
        RxEventBus.getInstance().post(new RxEvents.UserFollowingListLoadedEvent());
    }

    public synchronized void setStarredRepo(List<GithubRepo> starredRepo) {
        this.starredRepo = starredRepo;
        RxEventBus.getInstance().post(new RxEvents.StarredRepoLoadedEvent());
    }

    public String getAavatarUrl() {
        return userDetails == null ? null : userDetails.avatar_url;
    }

    public void logOffUser() {
        setUserDetails(null);
        setFollowing(null);
        setStarredRepo(null);
    }
}
