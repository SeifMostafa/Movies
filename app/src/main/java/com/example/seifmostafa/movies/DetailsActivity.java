package com.example.seifmostafa.movies;

import android.app.FragmentTransaction;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.seifmostafa.movies.Data.Movie;

public class DetailsActivity extends AppCompatActivity  {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        Bundle arg = new Bundle();
        arg.putParcelable("DETAIL_MOVIE", getIntent().getParcelableExtra("DETAIL_MOVIE"));
        detailsFragment detailsFragment = new detailsFragment();
        detailsFragment.setArguments(arg);
        getSupportFragmentManager().beginTransaction().replace(R.id.activity_details_container, detailsFragment, "FRAGMENT").commit();
        getSupportFragmentManager().executePendingTransactions();
    }



}