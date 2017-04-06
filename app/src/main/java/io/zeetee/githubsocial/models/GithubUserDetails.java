package io.zeetee.githubsocial.models;

import android.os.Parcel;

/**
 * By GT.
 */

public class GithubUserDetails extends GithubUser {

    public String name;
    public String company;
    public String blog;

    public String bio;
    public String location;

    public int public_repos;
    public int public_gists;
    public int followers;
    public int following;

    protected GithubUserDetails(Parcel in) {
        super(in);
        name = in.readString();
        company = in.readString();
        blog = in.readString();
        bio = in.readString();
        location = in.readString();
        public_repos = in.readInt();
        public_gists = in.readInt();
        followers = in.readInt();
        following = in.readInt();
    }

    public static final Creator<GithubUserDetails> CREATOR = new Creator<GithubUserDetails>() {
        @Override
        public GithubUserDetails createFromParcel(Parcel in) {
            return new GithubUserDetails(in);
        }

        @Override
        public GithubUserDetails[] newArray(int size) {
            return new GithubUserDetails[size];
        }
    };


    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(name);
        dest.writeString(company);
        dest.writeString(blog);
        dest.writeString(bio);
        dest.writeString(location);
        dest.writeInt(public_repos);
        dest.writeInt(public_gists);
        dest.writeInt(followers);
        dest.writeInt(following);
    }
}
