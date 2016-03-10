package lmcloud.popularmovies;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Modified from AndroidFlavor.java from https://github.com/udacity/android-custom-arrayadapter/blob/parcelable/app/src/main/java/demo/example/com/customarrayadapter/AndroidFlavor.java
 */
public class mParcel implements Parcelable {
    String mID;
    String mTitle;
    String mSynopsis;
    String mRating;
    String mPosterPath;
    int mThumbnail; // drawable reference id

    public mParcel(Parcel in) {
        mID = in.readString();
        mTitle = in.readString();
        mSynopsis = in.readString();
        mRating = in.readString();
        mPosterPath = in.readString();
        mThumbnail = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(mID);
        parcel.writeString(mTitle);
        parcel.writeString(mSynopsis);
        parcel.writeString(mRating);
        parcel.writeString(mPosterPath);
        parcel.writeInt(mThumbnail);
    }

    public final Parcelable.Creator<mParcel> CREATOR
            = new Parcelable.Creator<mParcel>() {
        @Override
        public mParcel createFromParcel(Parcel parcel) {
            return new mParcel(parcel);
        }

        @Override
        public mParcel[] newArray(int i) {
            return new mParcel[i];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }
}
