package com.example.seifmostafa.movies;


import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.example.seifmostafa.movies.Data.ArrayAdapter;
import com.example.seifmostafa.movies.Data.Movie;
import com.example.seifmostafa.movies.Data.MovieContract;
import com.example.seifmostafa.movies.Data.MoviesProvider;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity  {
    //  MovieContract.MovieEntry.ID,   just for counting .. actually it will be random but I mean that we will not use it.
    public static final int COL_MOVIE_ID = 1;
    public static final int COL_TITLE = 2;
    public static final int COL_IMAGE = 3;
    public static final int COL_OVERVIEW = 4;
    public static final int COL_RATING = 5;
    public static final int COL_DATE = 6;
    public static ArrayList<Movie> Movies = null;
    public static boolean tablet;
    GridView gridView;
    public static ArrayAdapter arrayAdapter;
    public static String SortingMethod = null;
    public static final String[] MOVIE_COLUMNS = {
            MovieContract.MovieEntry._ID,
            MovieContract.MovieEntry.COLUMN_MOVIE_ID,
            MovieContract.MovieEntry.COLUMN_TITLE,
            MovieContract.MovieEntry.COLUMN_IMAGE,
            MovieContract.MovieEntry.COLUMN_OVERVIEW,
            MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE,
            MovieContract.MovieEntry.COLUMN_RELEASED_DATE
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(findViewById(R.id.activity_details_container)!=null)
        {
            tablet = true;
            if(savedInstanceState == null)
            {
                getSupportFragmentManager().beginTransaction().replace(R.id.activity_details_container,new detailsFragment(),"DETAILFRAGMET").commit();
            }else
            {
                tablet =false;
            }
        }

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
        SortingMethod =  preferences.getString(getString(R.string.sortingway),getString(R.string.defaultsort));
        MoviesProvider.builduriforSortingmethods();
        try {
            Movies = MoviesProvider.getmovies(((new MoviesProvider.Downloader().execute(MoviesProvider.uri)).get()));
        } catch (Exception e) {
            e.printStackTrace();
        }

        gridView = (GridView)findViewById(R.id.gridview_movies);
        arrayAdapter  = new ArrayAdapter(Movies,MainActivity.this);
        gridView.setAdapter(arrayAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(tablet)
                {
                    Bundle bundle = new Bundle();

                    bundle.putParcelable("DETAIL_MOVIE",(Movie) arrayAdapter.getItem(position));
                    detailsFragment detailsFragment = new detailsFragment();
                    detailsFragment.setArguments(bundle);

                    getSupportFragmentManager().beginTransaction().replace(R.id.activity_details_container,detailsFragment,"FRAGMENT").commit();

                }else
                {
                    Movie movie = (Movie) arrayAdapter.getItem(position);
                    Intent intent = new Intent(MainActivity.this, DetailsActivity.class)
                            .putExtra("DETAIL_MOVIE", movie);
                    startActivity(intent);
                }

            }
        });
    }
    @Override
    public void onSaveInstanceState(Bundle outState) {
        try {
            updateMovies();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        super.onSaveInstanceState(outState);
    }


    private  void updateMovies() throws ExecutionException, InterruptedException, JSONException {

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
        SortingMethod =  preferences.getString(getString(R.string.sortingway),getString(R.string.defaultsort));
        if(SortingMethod.equals("Favorites"))
        {
            new MoviesProvider.FetchFavorites().execute(MainActivity.this);
        }
        else
        {
            MoviesProvider.builduriforSortingmethods();
            Movies = MoviesProvider.getmovies(((new MoviesProvider.Downloader().execute(MoviesProvider.uri)).get()));
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        Toast.makeText(MainActivity.this,"Preparing Movies",Toast.LENGTH_LONG).show();

    }

    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MainActivity.this.getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(MainActivity.this,SettingsActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
