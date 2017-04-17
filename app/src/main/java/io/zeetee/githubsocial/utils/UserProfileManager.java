package io.zeetee.githubsocial.utils;

import android.text.TextUtils;
import android.util.Log;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
    private Map<String,GithubUser> followingMap;
    private Map<String,GithubRepo> starredRepoMap;


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


    public void checkFetchUserInfo(){
        if(!UserManager.getSharedInstance().isLoggedIn()) return;
        if(userDetails == null) fetchProfileSilently();
        if(followingMap == null) fetchFollowingSilently();
        if(starredRepoMap == null) fetchStarredRepoSilently();
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
                .meFollowing(0, GSConstants.PER_PAGE)
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
                .meStarred(0, GSConstants.PER_PAGE)
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
        followingMap = new LinkedHashMap<>();
        if(following != null){
            for (GithubUser user : following) {
                if(user == null || TextUtils.isEmpty(user.login)) continue;
                followingMap.put(user.login, user);
            }
        }
        RxEventBus.getInstance().post(new RxEvents.UserFollowingListLoadedEvent());
    }

    public synchronized void setStarredRepo(List<GithubRepo> starredRepo) {
        starredRepoMap = new LinkedHashMap<>();
        if(starredRepo != null){
            for (GithubRepo repo : starredRepo) {
                if(repo == null || TextUtils.isEmpty(repo.full_name)) continue;
                starredRepoMap.put(repo.full_name, repo);
            }
        }
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

    public boolean isFollowing(GithubUser user){
        return user !=null && isFollowing(user.login);
    }


    public boolean isFollowing(String userName){
       return userName != null && followingMap != null && followingMap.get(userName) != null;
    }

    public boolean isStarred(GithubRepo repo){
        return repo != null && isStarred(repo.full_name);
    }

    public boolean isStarred(String repoFullName){
        return repoFullName != null && starredRepoMap != null && starredRepoMap.get(repoFullName) != null;
    }

    public void userFollowed(GithubUser githubUser) {
        if(githubUser == null || TextUtils.isEmpty(githubUser.login)) return;
        followingMap.put(githubUser.login, githubUser);
        RxEventBus.getInstance().post(new RxEvents.UserFollowedEvent(githubUser));
    }

    public void userUnFollowed(GithubUser githubUser) {
        if(githubUser == null || TextUtils.isEmpty(githubUser.login)) return;
        followingMap.remove(githubUser.login);
        RxEventBus.getInstance().post(new RxEvents.UserUnFollowedEvent(githubUser));
    }


    public void repoStarred(GithubRepo githubRepo) {
        final String owner = githubRepo.owner.login;
        final String repo = githubRepo.name;
        final String key = owner + "/" + repo;
        starredRepoMap.put(key, githubRepo);
    }

    public void repoUnStarred(GithubRepo githubRepo) {
        final String owner = githubRepo.owner.login;
        final String repo = githubRepo.name;
        final String key = owner + "/" + repo;
        starredRepoMap.remove(key);
    }
}
