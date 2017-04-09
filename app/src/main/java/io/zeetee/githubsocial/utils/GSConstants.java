package io.zeetee.githubsocial.utils;

/**
 * By GT.
 */

public interface GSConstants {

    long CONNECTION_TIMEOUT = 10L;
    long READ_TIMEOUT = 10L;
    String USER_DETAILS = "USER_DETAILS";
    String USER_NAME = "USER_NAME";
    String URL = "URL";
    String REPO_NAME = "REPO_NAME";


    interface Github{

        String API_BASE_URL = "https://api.github.com";
        String note = "Github Social Android App";
        String client_id = "6b83219eff7bf1df43d6";
        String client_secret = "4f891294e09e86001edd418f05452ccb86944055";

    }


    interface ListType {
        String LIST_TYPE = "LIST_TYPE";
        int FOLLOWERS = 0;
        int FOLLOWING = 1;
        int STARGAZER = 2;
        int WATCHERS = 3;
    }

}
