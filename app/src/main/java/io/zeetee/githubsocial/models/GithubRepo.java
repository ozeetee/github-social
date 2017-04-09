package io.zeetee.githubsocial.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

/**
 * By GT.
 */

public class GithubRepo implements Parcelable{

    public long id;
    public String name;
    public String full_name;
    public String description;
    public String language;
    public long watchers_count;
    public long stargazers_count;
    public GithubUser owner;
    public Date updated_at;
    public Date created_at;
    public long forks;

    protected GithubRepo(Parcel in) {
        id = in.readLong();
        name = in.readString();
        full_name = in.readString();
        description = in.readString();
        language = in.readString();
        watchers_count = in.readLong();
        stargazers_count = in.readLong();
        owner = in.readParcelable(GithubUser.class.getClassLoader());
        updated_at = (Date) in.readSerializable();
        created_at = (Date) in.readSerializable();
        forks = in.readLong();
    }

    public static final Creator<GithubRepo> CREATOR = new Creator<GithubRepo>() {
        @Override
        public GithubRepo createFromParcel(Parcel in) {
            return new GithubRepo(in);
        }

        @Override
        public GithubRepo[] newArray(int size) {
            return new GithubRepo[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(name);
        dest.writeString(full_name);
        dest.writeString(description);
        dest.writeString(language);
        dest.writeLong(watchers_count);
        dest.writeLong(stargazers_count);
        dest.writeParcelable(owner, flags);
        dest.writeSerializable(updated_at);
        dest.writeSerializable(created_at);
        dest.writeLong(forks);
    }
}
