package com.example.regener.texttranslationassistant;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Regener on 24.03.2017.
 */

public class ItemObject implements Parcelable
{
    private String mTitle;
    private String mText;
    private String mPath;

    public ItemObject(String title, String text, String path)
    {
        this.mTitle = title;
        this.mText = text;
        this.mPath = path;
    }

    public ItemObject(Parcel in){
        this.mTitle = in.readString();
        this.mText = in.readString();
        this.mPath = in.readString();
    }

    public String getPath() {
        return mPath;
    }

    public void setPath(String path) {
        mPath = path;
    }

    public String getText() {
        return mText;
    }

    public void setText(String text) {
        mText = text;
    }

    public String getTitle()
    {
        return mTitle;
    }

    public void setTitle(String title)
    {
        this.mTitle = title;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(mTitle);
        parcel.writeString(mText);
        parcel.writeString(mPath);
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {

        public ItemObject createFromParcel(Parcel in) {
            return new ItemObject(in);
        }

        public ItemObject[] newArray(int size) {
            return new ItemObject[size];
        }
    };
}