package io.zeetee.githubsocial.models;

/**
 * By GT.
 */

public class AuthResponse {

    public long id;
    public String url;
    public App app;
    public String token;

    public String hashed_token;
    public String token_last_eight;
    public String note;
    public String note_url;
    public String created_at;
    public String updated_at;
    public String[] scopes;
    public String fingerprint;

    public static class App{
        public String name;
        public String url;
        public String client_id;
    }
}
