package io.zeetee.githubsocial.utils;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * By GT.
 */

public class StarState implements Parcelable{

    public boolean originalState;
    public boolean currentState;

    public StarState(){

    }

    protected StarState(Parcel in) {
        originalState = in.readByte() != 0;
        currentState = in.readByte() != 0;
    }

    public static final Creator<StarState> CREATOR = new Creator<StarState>() {
        @Override
        public StarState createFromParcel(Parcel in) {
            return new StarState(in);
        }

        @Override
        public StarState[] newArray(int size) {
            return new StarState[size];
        }
    };

    @Override
    public String toString() {
        return "StarState{" +
                "originalState=" + originalState +
                ", currentState=" + currentState +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte((byte) (originalState ? 1 : 0));
        dest.writeByte((byte) (currentState ? 1 : 0));
    }
}
