package lmcloud.popularmovies;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

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
import java.util.Arrays;
import java.util.prefs.PreferenceChangeListener;

/**
 * A placeholder fragment created by Limeng on 2/2/2016.
 */
public class GridFragment extends Fragment {

    static int displayWidth;
    static ArrayList<String> posters;
    static boolean sortByPopularity = true;
    static String API_KEY = "3b22241ec67d02221f26fe0f0906794f";

    static PreferenceChangeListener PClistener;
    static SharedPreferences prefs;
    static boolean sortByFavorites;
    static ArrayList<String> postersF = new ArrayList<String>();

    ImageGridAdapter mAdapter;
    String mSortKey;

    public GridFragment(){
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main,container, false);
        // get the width of the device
        WindowManager windowManager = (WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        // phone only
        displayWidth = size.x/3;
        if(savedInstanceState==null) // Create the ImageGridAdapter
        {
            mAdapter = new ImageGridAdapter(getActivity(),new ArrayList<mParcel>());
            updateMovieGrid();
        } else {

        }
        //listen for presses on movieGrid items
        movieGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                System.out.println(position);
            }
        });


        return rootView;
    }
    private class PreferenceChangeListener implements SharedPreferences.OnSharedPreferenceChangeListener{


        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            movieGrid.setAdapter(null);
            onStart();
        }
    }
    @Override
    public void onStart()
    {
        super.onStart();
        prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        PClistener = new PreferenceChangeListener();
        prefs.registerOnSharedPreferenceChangeListener(PClistener);

        if(prefs.getString("sortby","popularity").equals("popularity"))
        {
            getActivity().setTitle("Most Popular Movies");
            sortByPopularity = true;
            sortByFavorites=false;
        }
        else if(prefs.getString("sortby","popularity").equals("rating"))
        {
            getActivity().setTitle("Highest Rated Movies");
            sortByPopularity = false;
            sortByFavorites=false;
        }
        else if(prefs.getString("sortby","popularity").equals("favorites"))
        {
            getActivity().setTitle("Favorited Movies");
            sortByPopularity = false;
            sortByFavorites=true;
        }
        TextView textView = new TextView(getActivity());
        LinearLayout layout = (LinearLayout)getActivity().findViewById(R.id.linearlayout);
        if(sortByFavorites)
        {
            if(postersF.size()==0)
            {
                textView.setText("You have no favorites movies.");
                if(layout.getChildCount()==1)
                    layout.addView(textView);
                movieGrid.setVisibility(GridView.GONE);
            }
            else{
                movieGrid.setVisibility(GridView.VISIBLE);
                layout.removeView(textView);
            }
            if(postersF!=null&&getActivity()!=null)
            {
                ImageAdapter adapter = new ImageAdapter(getActivity(),postersF,displayWidth);
                movieGrid.setAdapter(adapter);
            }
        }
        else{
            movieGrid.setVisibility(GridView.VISIBLE);
            layout.removeView(textView);
        }

        if(isNetworkAvailable())
        {

            new ImageLoadTask().execute();
        }
        else{
            TextView textview1 = new TextView(getActivity());
            LinearLayout layout1 = (LinearLayout)getActivity().findViewById(R.id.linearlayout);
            textview1.setText("You are not connected to the Internet");
            if(layout1.getChildCount()==1)
            {
                layout1.addView(textview1);
            }
            movieGrid.setVisibility(GridView.GONE);
        }
    }

    public boolean isNetworkAvailable()
    {
        ConnectivityManager connectivityManager = (ConnectivityManager)getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo !=null &&activeNetworkInfo.isConnected();
    }
    public class ImageLoadTask extends AsyncTask<Void, Void, ArrayList<String>> {

        @Override
        protected ArrayList<String> doInBackground(Void... params) {
            while(true){
                try{
                    posters = new ArrayList(Arrays.asList(getPathsFromAPI(sortByPopularity)));
                    return posters;
                }
                catch(Exception e)
                {
                    continue;
                }
            }

        }
        @Override
        protected void onPostExecute(ArrayList<String>result)
        {
            if(result!=null && getActivity()!=null)
            {
                ImageAdapter adapter = new ImageAdapter(getActivity(),result, displayWidth);
                movieGrid.setAdapter(adapter);

            }
        }
        public String[] getPathsFromAPI(boolean sortbypop)
        {
            while(true)
            {
                HttpURLConnection urlConnection = null;
                BufferedReader reader = null;
                String JSONResult;

                try {
                    String urlString = null;
                    if (sortbypop) {
                        urlString = "http://api.themoviedb.org/3/discover/movie?sort_by=popularity.desc&api_key=" + API_KEY;
                    } else {
                        urlString = "http://api.themoviedb.org/3/discover/movie?sort_by=vote_average.desc&vote_count.gte=500&api_key=" + API_KEY;
                    }
                    URL url = new URL(urlString);
                    urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("GET");
                    urlConnection.connect();

                    //Read the input stream into a String
                    InputStream inputStream = urlConnection.getInputStream();
                    StringBuffer buffer = new StringBuffer();
                    if (inputStream == null) {
                        return null;
                    }
                    reader = new BufferedReader(new InputStreamReader(inputStream));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        buffer.append(line + "\n");
                    }
                    if (buffer.length() == 0) {
                        return null;
                    }
                    JSONResult = buffer.toString();

                    try {
                        return getPathsFromJSON(JSONResult);
                    } catch (JSONException e) {
                        return null;
                    }
                }catch(Exception e)
                {
                    continue;
                }finally {
                    if(urlConnection!=null)
                    {
                        urlConnection.disconnect();
                    }
                    if(reader!=null)
                    {
                        try{
                            reader.close();
                        }catch(final IOException e)
                        {
                        }
                    }
                }


            }
        }
        public String[] getPathsFromJSON(String JSONStringParam) throws JSONException {

            JSONObject JSONString = new JSONObject(JSONStringParam);

            JSONArray moviesArray = JSONString.getJSONArray("results");
            String[] result = new String[moviesArray.length()];

            for(int i = 0; i<moviesArray.length();i++)
            {
                JSONObject movie = moviesArray.getJSONObject(i);
                String moviePath = movie.getString("poster_path");
                result[i] = moviePath;
            }
            return result;
        }
    }



}