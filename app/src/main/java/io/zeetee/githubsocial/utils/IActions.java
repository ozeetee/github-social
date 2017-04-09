package io.zeetee.githubsocial.utils;

/**
 * By GT.
 */
public interface IActions {
    void onUserClicked(String userName);
    void onRepoClicked(String repoName);
    void showUserList(int userListType, String userName);
    void showUserList(int userListType, String repoName, String ownerUserName);
    void showUserRepos(String userName);
    void showRepoDetails(String repoUrl);
}
