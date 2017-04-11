package io.zeetee.githubsocial.bus;

import io.zeetee.githubsocial.models.GithubUser;
import io.zeetee.githubsocial.models.GithubUserDetails;

/**
 * By GT.
 */

public interface RxEvents {

    class UserLoggedInEvent{
        public String userName;
        public String token;
        public String password;

        public UserLoggedInEvent(String userName, String token, String password) {
            this.userName = userName;
            this.token = token;
            this.password = password;
        }
    }

    class UserInfoLoadedEvent{
        public GithubUserDetails userDetails;

        public UserInfoLoadedEvent(GithubUserDetails userDetails) {
            this.userDetails = userDetails;
        }
    }

    class UserFollowingListLoadedEvent{

    }

    class StarredRepoLoadedEvent {
    }
}
