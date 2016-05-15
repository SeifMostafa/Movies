package com.example.seifmostafa.movies;

import android.content.Context;
import android.database.Cursor;

import com.example.seifmostafa.movies.Data.MovieContract;

/**
 * Created by seifmostafa on 01/02/16.
 */
public class Utility {

    public static int isFavorited(Context context, int id) {
        Cursor cursor = context.getContentResolver().query(
                MovieContract.MovieEntry.CONTENT_URI,
                null,   // projection
                MovieContract.MovieEntry.COLUMN_MOVIE_ID + " = ?", // selection
                new String[] { Integer.toString(id) },   // selectionArgs
                null    // sort order
        );
        int numRows = cursor.getCount();
        cursor.close();
        return numRows;
    }


}