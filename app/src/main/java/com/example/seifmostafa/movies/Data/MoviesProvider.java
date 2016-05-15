package com.example.seifmostafa.movies.Data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.example.seifmostafa.movies.BuildConfig;
import com.example.seifmostafa.movies.MainActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by seifmostafa on 30/01/16.
 */
public class MoviesProvider  extends ContentProvider {

    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private MovieDb MOVIES_Db;
    public static String uri=null;
    static final int MOVIE = 100;



    static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = MovieContract.CONTENT_AUTHORITY;

        // For each type of URI you want to add, create a corresponding code.
        matcher.addURI(authority, MovieContract.PATH_MOVIE, MOVIE);
        return matcher;
    }












    @Override
    public boolean onCreate() {
        MOVIES_Db = new MovieDb(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor retCursor;
        switch (sUriMatcher.match(uri)) {
            case MOVIE: {
                // recursive ..
                retCursor = MOVIES_Db.getReadableDatabase().query(
                        MovieContract.MovieEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);

        switch (match) {
            case MOVIE:
                return MovieContract.MovieEntry.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }
    @Override
    public Uri insert(Uri uri, ContentValues values) {

        final SQLiteDatabase db = MOVIES_Db.getWritableDatabase();
        Uri returnUri;

        switch (sUriMatcher.match(uri)) {
            case MOVIE: {
                long _id = db.insert(MovieContract.MovieEntry.TABLE_NAME, null, values);
                if (_id > 0) {
                    returnUri = MovieContract.MovieEntry.buildMovieUri(_id);
                }
                else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {

        final SQLiteDatabase db = MOVIES_Db.getWritableDatabase();
        int rowsDeleted;
        // this makes delete all rows return the number of rows deleted
        if (null == selection) selection = "1";
        switch (sUriMatcher.match(uri)) {
            case MOVIE:
                rowsDeleted = db.delete(
                        MovieContract.MovieEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        // Because a null deletes all rows
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = MOVIES_Db.getWritableDatabase();
        int rowsUpdated;

        switch (sUriMatcher.match(uri)) {
            case MOVIE:
                rowsUpdated = db.update(MovieContract.MovieEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsUpdated;
    }



















    public static class Downloader extends AsyncTask<String,Void,String> {
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String forecastJsonStr = null;

        public String fetch(String uri_TO) throws IOException {
            URL url = new URL(uri_TO);

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            InputStream inputStream = (InputStream) urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                // Nothing to do.
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line);
            }
            if (buffer.length() == 0) {
                return null;
            }

            forecastJsonStr = buffer.toString();
            urlConnection.disconnect();
            reader.close();
            return forecastJsonStr;

        }


        @Override
        protected String doInBackground(String... params) {

            try {
                return fetch(params[0]);
            } catch (IOException e) {
                Log.e("ERRORDOWNLOAD", e.toString());
                Log.i("LINK",params[0]);
            }
            return null;
        }
    }

    public static void builduriforSortingmethods() {
        if(MainActivity.SortingMethod.equals("Most popular"))
        {
           uri = "http://api.themoviedb.org/3/discover/movie?sort_by=popularity.desc&api_key=" + BuildConfig.MoviesDB;
        }
        else
        {
            uri = "http://api.themoviedb.org/3/discover/movie?certification_country=US&certification=R&sort_by=vote_average.desc&api_key=" + BuildConfig.MoviesDB;
        }
    }

    public static ArrayList<Movie> getmovies(String data) throws JSONException {
          final String movieslist = "results";
          final String movieid = "id";
          final String movieposter = "poster_path";
          final String movietittle = "title";
          final String movievote_average = "vote_average";
          final String movieplot_synopsis = "overview";
          final String movierelease_date = "release_date";
        JSONObject whole = new JSONObject(data);
        JSONArray list = whole.getJSONArray(movieslist);
        ArrayList<Movie> movies = new ArrayList<Movie>();
        for(int i=0;i<list.length();i++)
        {
            JSONObject movie = list.getJSONObject(i);
            movies.add( new Movie(movie.getString(movieposter),movie.getString(movietittle),movie.getString(movierelease_date),movie.getString(movieplot_synopsis),movie.getDouble(movievote_average),Integer.valueOf(movie.getString(movieid))));
        }
        return movies;
    }

    public static class FetchFavorites extends AsyncTask<Context, Void, Void> {
        @Override
        protected Void doInBackground(Context... params) {
            Cursor cursor = params[0].getContentResolver().query(
                    MovieContract.MovieEntry.CONTENT_URI,
                    MainActivity.MOVIE_COLUMNS,
                    null,
                    null,
                    null
            );
            ArrayList<Movie> results = new ArrayList<>();
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    Movie movie = new Movie(cursor);
                    results.add(movie);
                } while (cursor.moveToNext());
                cursor.close();
            }
            MainActivity.Movies = results;
            return null;
        }
        @Override
        protected void onPreExecute() {
            MainActivity.arrayAdapter.setData(MainActivity.Movies);
        }
    }















}
