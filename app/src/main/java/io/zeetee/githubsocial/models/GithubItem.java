package io.zeetee.githubsocial.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * By GT.
 */

public class GithubItem implements Parcelable {

    public long id;

    protected GithubItem(Parcel in) {
        id = in.readLong();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<GithubItem> CREATOR = new Creator<GithubItem>() {
        @Override
        public GithubItem createFromParcel(Parcel in) {
            return new GithubItem(in);
        }

        @Override
        public GithubItem[] newArray(int size) {
            return new GithubItem[size];
        }
    };

    @Override
    public String toString() {
        return "GithubItem{" +
                "id=" + id +
                '}';
    }
}
