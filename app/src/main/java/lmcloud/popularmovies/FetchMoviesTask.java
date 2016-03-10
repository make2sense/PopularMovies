package lmcloud.popularmovies;

import android.os.AsyncTask;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

public class FetchMoviesTask extends AsyncTask<Void, Void, ArrayList<String>> {

    @Override
    protected ArrayList<String> doInBackground(Void... params) {
        while(true){
            try{
                posters = new ArrayList(Arrays.asList(getPathsFromAPI(sortByPop)));
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
            ImageAdapter adapter = new ImageAdapter(getActivity(),result, width);
            gridview.setAdapter(adapter);

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
                    overviews = new ArrayList<String>(Arrays.asList(getStringsFromJSON(JSONResult,"overview")));

                    titles = new ArrayList<String>(Arrays.asList(getStringsFromJSON(JSONResult,"original_title")));
                    ratings = new ArrayList<String>(Arrays.asList(getStringsFromJSON(JSONResult,"vote_average")));
                    dates = new ArrayList<String>(Arrays.asList(getStringsFromJSON(JSONResult,"release_date")));
                    ids = new ArrayList<String>(Arrays.asList(getStringsFromJSON(JSONResult,"id")));
                    while(true)
                    {
                        youtubes = new ArrayList<String>(Arrays.asList(getYoutubesFromIds(ids,0)));
                        youtubes2 = new ArrayList<String>(Arrays.asList(getYoutubesFromIds(ids,1)));
                        int nullCount = 0;
                        for(int i = 0; i<youtubes.size();i++)
                        {
                            if(youtubes.get(i)==null)
                            {
                                nullCount++;
                                youtubes.set(i,"no video found");
                            }
                        }
                        for(int i = 0; i<youtubes2.size();i++)
                        {
                            if(youtubes2.get(i)==null)
                            {
                                nullCount++;
                                youtubes2.set(i,"no video found");
                            }
                        }
                        if(nullCount>2)continue;
                        break;
                    }
                    comments = getReviewsFromIds(ids);
                    return getPathsFromJSON(JSONResult);

                } catch (JSONException e) {
                    continue;
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
