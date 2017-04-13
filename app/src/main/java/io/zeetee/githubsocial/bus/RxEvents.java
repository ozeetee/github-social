package io.zeetee.githubsocial.bus;

import io.zeetee.githubsocial.models.GithubRepo;
import io.zeetee.githubsocial.models.GithubUser;
import io.zeetee.githubsocial.models.GithubUserDetails;

/**
 * By GT.
 */

public interface RxEvents {

    class UserLoggedInEvent{
        public final String userName;
        public final String token;
        public final String password;

        public UserLoggedInEvent(String userName, String token, String password) {
            this.userName = userName;
            this.token = token;
            this.password = password;
        }
    }

    class UserInfoLoadedEvent{
        public final GithubUserDetails userDetails;

        public UserInfoLoadedEvent(GithubUserDetails userDetails) {
            this.userDetails = userDetails;
        }
    }

    class UserFollowingListLoadedEvent{

    }

    class StarredRepoLoadedEvent {
    }

    class UserFollowedEvent {
        public final GithubUser githubUser;

        public UserFollowedEvent(GithubUser githubUser) {
            this.githubUser = githubUser;
        }
    }

    class UserUnFollowedEvent {
        public final GithubUser githubUser;
        public UserUnFollowedEvent(GithubUser githubUser) {
            this.githubUser = githubUser;
        }
    }


    class RepoStarredEvent {
        public final GithubRepo githubRepo;

        public RepoStarredEvent(GithubRepo githubRepo) {
            this.githubRepo = githubRepo;
        }
    }

    class RepoUnStarredEvent {
        public final GithubRepo githubRepo;
        public RepoUnStarredEvent(GithubRepo githubRepo) {
            this.githubRepo = githubRepo;
        }
    }


}
