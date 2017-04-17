package io.zeetee.githubsocial.utils;

import android.support.annotation.NonNull;

import io.zeetee.githubsocial.models.GithubRepo;
import io.zeetee.githubsocial.models.GithubUser;

/**
 * By GT.
 */
public interface IActions {
    void onUserClicked(String userName);
    void showUserList(int userListType, String userName);
    void showUserList(int userListType, String repoName, String ownerUserName);
    void showUserRepos(String userName);
    void showStarredRepos();
    void showRepoDetails(String repoName, String repoOwner);
    void showLoginScreen();
    void showLoginPrompt(String s);
    void followUnFollowClicked(@NonNull GithubUser githubUser);
    void starUnstarClicked(GithubRepo repo);
}
