package com.example.seifmostafa.movies;

import android.app.ActionBar;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.seifmostafa.movies.Data.Movie;
import com.example.seifmostafa.movies.Data.MovieContract;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutionException;


public class detailsFragment extends Fragment {

    TextView overview;
    TextView releasedate;
    TextView tittle;
    TextView vote_average;
    ImageView poster;
    ImageButton trailer;
    ImageButton favorite;
    ImageButton share;
    TextView reviews;

    String ID;
    String TITTLE;
    double VOTE_AVERAGE;
    String POSTER;
    String RELEASED_DATE;
    String OVERVIEW;
    String TRAILER;
    String REVIEWS;
    Movie movie=null;


    private OnFragmentInteractionListener mListener;

    public detailsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Toast.makeText(getActivity(),"Preparing The Movie",Toast.LENGTH_SHORT).show();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_details, container, false);
        setHasOptionsMenu(true);
        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        getActivity().getMenuInflater().inflate(R.menu.menu_details, menu);
    }





    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        final MenuItem ITEM = item;

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            ITEM.setVisible(true);
            startActivity(new Intent(getActivity(), SettingsActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onViewCreated(View rootView, Bundle savedInstanceState) {
        super.onViewCreated(rootView, savedInstanceState);

        // Inflate the layout for this fragment
        Bundle bundle = getArguments();
        movie = bundle.getParcelable("DETAIL_MOVIE");
        Log.i("MOVIE", movie.toString());

        if (movie != null) {
            rootView.setVisibility(View.VISIBLE);
            ID = String.valueOf(movie.getId());
            TITTLE = movie.getTitle();
            OVERVIEW = movie.getPlot_synopsis();
            VOTE_AVERAGE = movie.getVote_average();
            POSTER = movie.getImageView();
            RELEASED_DATE = movie.getRelease_date();

            String TRAILERuri = "http://api.themoviedb.org/3/movie/" + ID + "/videos?api_key=" + BuildConfig.MoviesDB;
            String REVIEWuri = "http://api.themoviedb.org/3/movie/" + ID + "/reviews?api_key=" + BuildConfig.MoviesDB;

            try {
                TRAILER = (new fetchtrailer().execute(TRAILERuri)).get();
                REVIEWS = (new fetchreviews().execute(REVIEWuri)).get();

            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }


            poster = (ImageView) rootView.findViewById(R.id.imageView_detailposter);
            releasedate = (TextView) rootView.findViewById(R.id.textView_releasedate);
            tittle = (TextView) rootView.findViewById(R.id.textView_title);
            vote_average = (TextView) rootView.findViewById(R.id.textView_voteaverage);
            overview = (TextView) rootView.findViewById(R.id.textView_overview);
            overview.setMovementMethod(new ScrollingMovementMethod());
            trailer = (ImageButton) rootView.findViewById(R.id.imageButton_trailer);
            reviews = (TextView) rootView.findViewById(R.id.textView_reviews);
            favorite = (ImageButton) rootView.findViewById(R.id.action_favorite);
            share = (ImageButton) rootView.findViewById(R.id.action_share);

            new AsyncTask<Void, Void, Integer>() {
                @Override
                protected Integer doInBackground(Void... params) {
                    Movie movie = getArguments().getParcelable("DETAIL_MOVIE");
                    return Utility.isFavorited(getActivity(), movie.getId());
                }

                @Override
                protected void onPostExecute(Integer isFavorited) {
                    if (isFavorited == 1) {
                        favorite.setImageResource(R.drawable.btn_star_big_on);
                    } else {
                        favorite.setImageResource(R.drawable.staroff);
                    }
                }
            }.execute();
        }
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Picasso.with(getActivity()).load(POSTER).into(poster);
        releasedate.setText(RELEASED_DATE);
        tittle.setText(TITTLE);
        vote_average.setText(String.valueOf(VOTE_AVERAGE));
        overview.setText(OVERVIEW);
        reviews.setText(REVIEWS);
        trailer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(TRAILER));
                startActivity(intent);
            }
        });

        if(TRAILER==null)
        {
            trailer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getActivity(),"No Trailers found",Toast.LENGTH_SHORT).show();
                }
            });
        }
        if(REVIEWS==null)
        {
            reviews.setText("No Reviews found");
        }
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // we will use the tittle instead of overview
                // if overview > 140 , so user will be confused.
                // in the next version , we will try to summarize
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_TEXT,"Watch " + TITTLE+ " trailer .. " + " " + TRAILER);
                startActivity(shareIntent);
            }
        });
        favorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (movie != null) {
                    // check if movie is in favorites or not
                    new AsyncTask<Void, Void, Integer>() {
                        @Override
                        protected Integer doInBackground(Void... params) {
                            return Utility.isFavorited(getActivity(), movie.getId());
                        }
                        @Override
                        protected void onPostExecute(Integer isFavorited) {
                            // if it is in favorites
                            if (isFavorited == 1) {
                                // delete from favorites
                                new AsyncTask<Void, Void, Integer>() {
                                    @Override
                                    protected Integer doInBackground(Void... params) {
                                        return getActivity().getContentResolver().delete(
                                                MovieContract.MovieEntry.CONTENT_URI,
                                                MovieContract.MovieEntry.COLUMN_MOVIE_ID + " = ?",
                                                new String[]{Integer.toString(movie.getId())}
                                        );
                                    }

                                    @Override
                                    protected void onPostExecute(Integer rowsDeleted) {
                                        favorite.setImageResource(R.drawable.staroff);
                                        Toast.makeText(getActivity(), "Removed from Favorites", Toast.LENGTH_SHORT).show();

                                    }
                                }.execute();
                            }
                            // if it is not in favorites
                            else {
                                // add to favorites
                                new AsyncTask<Void, Void, Uri>() {
                                    @Override
                                    protected Uri doInBackground(Void... params) {
                                        ContentValues values = new ContentValues();

                                        values.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID, movie.getId());
                                        values.put(MovieContract.MovieEntry.COLUMN_TITLE, movie.getTitle());
                                        values.put(MovieContract.MovieEntry.COLUMN_IMAGE, movie.getImageView());
                                        values.put(MovieContract.MovieEntry.COLUMN_OVERVIEW, movie.getPlot_synopsis());
                                        values.put(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE, movie.getVote_average());
                                        values.put(MovieContract.MovieEntry.COLUMN_RELEASED_DATE, movie.getRelease_date());

                                        return getActivity().getContentResolver().insert(MovieContract.MovieEntry.CONTENT_URI,
                                                values);
                                    }

                                    @Override
                                    protected void onPostExecute(Uri returnUri) {
                                        favorite.setImageResource(R.drawable.btn_star_big_on);
                                        Toast.makeText(getActivity(), "Added to favorites", Toast.LENGTH_SHORT).show();

                                    }
                                }.execute();
                            }
                        }
                    }.execute();
                }
            }
        });
    }











    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }


    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }










    class fetchtrailer extends AsyncTask<String,Void,String>
    {
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
            String link = null;

            try {
                JSONObject whole = new JSONObject(fetch(params[0]));
                JSONArray results = whole.getJSONArray("results");
                if(results.length()>0)
                {
                    JSONObject trail = (JSONObject) results.get(0);
                    if(trail.get("site").equals("YouTube"))
                    {
                        link= "http://www.youtube.com/watch?v="+trail.getString("key");
                    }
                }
            } catch (Exception e) {
                Log.e("TRAILER", e.toString());
            }
            return link;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            TRAILER=s;
        }
    }


    class fetchreviews extends AsyncTask<String,Void,String>
    {
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
            String result ="";
            try {
                JSONObject whole = null;
                whole = new JSONObject(fetch(params[0]));
                JSONArray reviews = whole.getJSONArray("results");
                if(reviews.length()>0)
                {

                    for(int i=0;i<reviews.length();i++)
                    {
                        JSONObject review = (JSONObject) reviews.get(i);
                        result +=review.getString("author") +": "+ review.getString("content")+"\n";
                    }
                    return result;
                }
            } catch (JSONException e1) {
                e1.printStackTrace();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            return result;
        }



        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            REVIEWS=s;
        }
    }
}
