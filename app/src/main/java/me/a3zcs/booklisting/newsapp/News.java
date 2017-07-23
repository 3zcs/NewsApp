package me.a3zcs.booklisting.newsapp;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by root on 23/07/17.
 */

public class News implements Parcelable{
    private String sectionName;
    private String title;
    private String date;
    private String body;

    public News() {
    }

    public News(String sectionName, String title, String date, String body) {
        this.sectionName = sectionName;
        this.title = title;
        this.date = date.substring(0,10);
        this.body = body;
    }

    protected News(Parcel in) {
        sectionName = in.readString();
        title = in.readString();
        date = in.readString();
        body = in.readString();
    }

    public static final Creator<News> CREATOR = new Creator<News>() {
        @Override
        public News createFromParcel(Parcel in) {
            return new News(in);
        }

        @Override
        public News[] newArray(int size) {
            return new News[size];
        }
    };

    public String getSectionName() {
        return sectionName;
    }

    public String getTitle() {
        return title;
    }

    public String getDate() {
        return date;
    }

    public String getBody() {
        return body;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(sectionName);
        parcel.writeString(title);
        parcel.writeString(date);
        parcel.writeString(body);
    }
}
