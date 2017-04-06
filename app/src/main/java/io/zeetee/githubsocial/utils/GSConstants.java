package io.zeetee.githubsocial.utils;

/**
 * By GT.
 */

public class GSConstants {

    public static final long CONNECTION_TIMEOUT = 10L; //10 seconds connection timeout
    public static final long READ_TIMEOUT = 10L;
    public static final String USER_DETAILS = "USER_DETAILS";
    public static final String USER_NAME = "USER_NAME";

    public interface Github{

        String API_BASE_URL = "https://api.github.com";
        String note = "Github Social Android App";
        String client_id = "6b83219eff7bf1df43d6";
        String client_secret = "4f891294e09e86001edd418f05452ccb86944055";

    }

}
