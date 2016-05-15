package com.example.seifmostafa.movies.Data;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Bundle;
import android.os.Parcelable;

import com.example.seifmostafa.movies.MainActivity;

/**
 * Created by seifmostafa on 30/01/16.
 */
public class Movie implements Parcelable {
    String posterlink, title, release_date, plot_synopsis;
            int id;
    double vote_average;


    public Movie(String poster_link, String title, String release_date, String plot_synopsis, double vote_average,int id) {
        this.posterlink = poster_link;
        this.title = title;
        this.release_date = release_date;
        this.plot_synopsis = plot_synopsis;
        this.vote_average = vote_average;
        this.id=id;
    }


    protected Movie(Parcel in) {
        posterlink = in.readString();
        title = in.readString();
        release_date = in.readString();
        plot_synopsis = in.readString();
        id = in.readInt();
        vote_average = in.readDouble();
    }
    public Movie(Cursor cursor) {
        this.id = cursor.getInt(MainActivity.COL_MOVIE_ID);
        this.title = cursor.getString(MainActivity.COL_TITLE);
        this.posterlink = cursor.getString(MainActivity.COL_IMAGE);
        this.plot_synopsis = cursor.getString(MainActivity.COL_OVERVIEW);
        this.vote_average = cursor.getInt(MainActivity.COL_RATING);
        this.release_date = cursor.getString(MainActivity.COL_DATE);
    }

    public static final Creator<Movie> CREATOR = new Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    public String getImageView() {
        return "http://image.tmdb.org/t/p/w185/"+posterlink;
    }

    @Override
    public String toString() {
        return this.id
                +"##"+this.getImageView()
                +"##"+this.release_date+"@@"+this.title+"@@"+this.vote_average
                +"##"+this.plot_synopsis;

    }
    public String getTitle() {
        return title;
    }

    public String getRelease_date() {
        return release_date;
    }

    public String getPlot_synopsis() {
        return plot_synopsis;
    }

    public int getId() {
        return id;
    }

    public double getVote_average() {
        return vote_average;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(posterlink);
        dest.writeString(title);
        dest.writeString(release_date);
        dest.writeString(plot_synopsis);
        dest.writeInt(id);
        dest.writeDouble(vote_average);
    }
}
