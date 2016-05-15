package com.example.seifmostafa.movies.Data;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.example.seifmostafa.movies.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

/**
 * Created by seifmostafa on 25/12/15.
 */
public class ArrayAdapter extends BaseAdapter {
    Movie movie;
    ArrayList<Movie>movies;
    LayoutInflater inflater;
    Activity activity;

    public ArrayAdapter(ArrayList<Movie> m, Activity activity) {
        movies = m;
        this.activity = activity;
        inflater = (LayoutInflater) this.activity.getSystemService(LAYOUT_INFLATER_SERVICE);
    }

    public Context getContext() {
        return activity;
    }

    public void add(Movie object) {
        synchronized (movie) {
            movies.add(object);
        }
        notifyDataSetChanged();
    }

    public void clear() {
        synchronized (movie) {
            movies.clear();
        }
        notifyDataSetChanged();
    }

    public void setData(List<Movie> data) {
        clear();
        for (Movie movie : data) {
            add(movie);
        }
    }
    @Override
    public int getCount() {
        return movies.size();
    }

    @Override
    public Object getItem(int position) {
        return movies.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void updateReceiptsList(ArrayList<Movie>newlist) {
        movies = newlist;
        this.notifyDataSetChanged();
    }

    public static class ViewHolder
    {
        ImageView imageView;
        public void prepare(View view)
        {
            imageView = (ImageView) view.findViewById(R.id.movie_imageview);
        }
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

       View vi = convertView;
        ViewHolder holder;

        if (convertView == null) {

            /****** Inflate tabitem.xml file for each row ( Defined below ) *******/
            vi = inflater.inflate(R.layout.imageview,null);

            /****** View Holder Object to contain tabitem.xml file elements ******/

            holder = new ViewHolder();
            holder.prepare(vi);

            /************  Set holder with LayoutInflater ************/
            vi.setTag(holder);
        } else
            holder = (ViewHolder) vi.getTag();

        if (movies.size() <= 0) {
            holder.imageView=null;

        } else {
            /***** Get each Model object from Arraylist ********/
            this.movie = null;
            movie =  movies.get(position);

            /************  Set Model values in Holder elements ***********/

            Picasso.with(this.activity).load(movie.getImageView()).into(holder.imageView);
            //holder.imageView.setImageURI(Uri.parse(imageview_movie.getImageView()));
            /******** Set Item Click Listner for LayoutInflater for each row *******/
        }
        return vi;


    }

}
